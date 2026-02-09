package com.checkout.payment.gateway.infrastructure.persistence;

import com.checkout.payment.gateway.core.model.Payment;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final ConcurrentHashMap<UUID, Payment> payments = new ConcurrentHashMap<>();

  public void add(Payment payment) {
    payments.put(payment.getId(), payment);
  }

  public Optional<Payment> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

  public int count() {
    return payments.size();
  }
}
