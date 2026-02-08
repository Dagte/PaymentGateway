package com.checkout.payment.gateway.infrastructure.persistence;

import com.checkout.payment.gateway.core.model.Payment;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final HashMap<UUID, Payment> payments = new HashMap<>();

  public void add(Payment payment) {
    payments.put(payment.getId(), payment);
  }

  public Optional<Payment> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}
