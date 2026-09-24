package com.fink.fooddelivery.payment;

import com.fink.fooddelivery.shared.contract.ChargeRequest;
import com.fink.fooddelivery.shared.contract.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${app.payment.latency-min-ms:40}")
    private long latencyMinMs;

    @Value("${app.payment.latency-max-ms:160}")
    private long latencyMaxMs;

    @Value("${app.payment.failure-rate:0.03}")
    private double failureRate;

    @Transactional
    public PaymentResponse charge(ChargeRequest request) {
        simulateProcessing();
        boolean success = ThreadLocalRandom.current().nextDouble() >= failureRate;
        Payment payment = paymentRepository.save(new Payment(
                request.orderId(), request.amount(),
                success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED,
                request.method()
        ));
        return new PaymentResponse(payment.getId(), payment.getStatus().name());
    }

    private void simulateProcessing() {
        long min = Math.max(0, latencyMinMs);
        long max = Math.max(min, latencyMaxMs);
        long delay = min == max ? min : ThreadLocalRandom.current().nextLong(min, max + 1);
        if (delay <= 0) {
            return;
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
