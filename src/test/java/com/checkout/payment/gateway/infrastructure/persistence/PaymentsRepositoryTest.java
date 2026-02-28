package com.checkout.payment.gateway.infrastructure.persistence;


import com.checkout.payment.gateway.core.model.Payment;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentsRepositoryTest {

  @Test
  void shouldHandleConcurrentWrites() throws InterruptedException {
    PaymentsRepository repository = new PaymentsRepository();

    int numberOfThreads = 100;
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(1);

    for (int i = 0; i < numberOfThreads; i++) {
      executor.submit(() -> {
        try {
          latch.await();
          Payment payment = new Payment();
          payment.setId(UUID.randomUUID());

          repository.add(payment);

        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }

    latch.countDown();

    executor.shutdown();
    boolean result = executor.awaitTermination(5, TimeUnit.SECONDS);

    assertTrue(result);
    assertEquals(numberOfThreads, repository.count());
  }
}