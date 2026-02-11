package com.checkout.payment.gateway.infrastructure.client.bank;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.common.exception.BankTimeoutException;
import com.checkout.payment.gateway.common.exception.BankUnavailableException;
import com.checkout.payment.gateway.common.exception.BankUpstreamErrorException;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.core.service.AcquiringBankClient;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentRequest;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class BankSimulatorClient implements AcquiringBankClient {

  private static final Logger LOG = LoggerFactory.getLogger(BankSimulatorClient.class);

  private final RestTemplate restTemplate;
  private final String bankUrl;

  public BankSimulatorClient(RestTemplate restTemplate,
      @Value("${bank.simulator.url}") String bankUrl) {
    this.restTemplate = restTemplate;
    this.bankUrl = bankUrl;
  }

  @Override
  @Retry(name = "bankRetry")
  public PaymentStatus process(Payment payment, String cardNumber, String cvv) {
    BankPaymentRequest bankRequest = BankRequestMapper.mapToBankRequest(payment, cardNumber, cvv);

    try {
      BankPaymentResponse bankResponse = restTemplate.postForObject(bankUrl, bankRequest,
          BankPaymentResponse.class);

      if (bankResponse != null && bankResponse.authorized()) {
        return PaymentStatus.AUTHORIZED;
      } else {
        return PaymentStatus.DECLINED;
      }

    } catch (HttpServerErrorException e) {
      LOG.error("Bank server error (5xx) for payment {}: {}", payment.getId(), e.getStatusCode());
      if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
        throw new BankUnavailableException("Acquiring bank is temporarily unavailable", e);
      }
      throw new BankUpstreamErrorException("Acquiring bank returned an error", e);
    } catch (ResourceAccessException e) {
      LOG.error("Network timeout or connection error for payment {}", payment.getId(), e);
      throw new BankTimeoutException("Communication timeout with the acquiring bank", e);
    } catch (HttpClientErrorException e) {
      LOG.error("Bank rejected request (4xx) for payment {}: {}", payment.getId(),
          e.getStatusCode());
      return PaymentStatus.REJECTED;
    }
  }
}
