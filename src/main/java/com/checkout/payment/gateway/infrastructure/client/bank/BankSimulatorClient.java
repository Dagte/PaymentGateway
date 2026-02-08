package com.checkout.payment.gateway.infrastructure.client.bank;

import com.checkout.payment.gateway.common.enums.PaymentStatus;
import com.checkout.payment.gateway.core.model.Payment;
import com.checkout.payment.gateway.core.service.AcquiringBankClient;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentRequest;
import com.checkout.payment.gateway.infrastructure.client.bank.model.BankPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class BankSimulatorClient implements AcquiringBankClient {

  private static final Logger LOG = LoggerFactory.getLogger(BankSimulatorClient.class);

  private final RestTemplate restTemplate;
  private final String bankUrl;

  public BankSimulatorClient(RestTemplate restTemplate, @Value("${bank.simulator.url}") String bankUrl) {
    this.restTemplate = restTemplate;
    this.bankUrl = bankUrl;
  }

  @Override
  public void process(Payment payment) {
    BankPaymentRequest bankRequest = BankRequestMapper.mapToBankRequest(payment);
    
    try {
      BankPaymentResponse bankResponse = restTemplate.postForObject(bankUrl, bankRequest, BankPaymentResponse.class);
      
      if (bankResponse != null && bankResponse.authorized()) {
        payment.setStatus(PaymentStatus.AUTHORIZED);
      } else {
        payment.setStatus(PaymentStatus.DECLINED);
      }
      
    } catch (HttpServerErrorException e) {
      LOG.error("Bank server error (5xx) for payment {}: {}", payment.getId(), e.getStatusCode());
      // TODO: Implement Resilience4j Retry mechanism here to handle transient failures before giving up
      throw new RuntimeException("Acquiring bank is currently experiencing issues", e);
    } catch (HttpClientErrorException e) {
      LOG.error("Bank rejected request (4xx) for payment {}: {}", payment.getId(), e.getStatusCode());
      payment.setStatus(PaymentStatus.REJECTED);
    } catch (Exception e) {
      LOG.error("Unexpected network error calling bank for payment {}", payment.getId(), e);
      throw new RuntimeException("Communication failure with the acquiring bank", e);
    }
  }
}