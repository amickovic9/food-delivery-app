package com.fink.fooddelivery.payment;

public record PaymentResult(PaymentStatus status, Long paymentId) {
    public boolean isSuccess() {
        return status == PaymentStatus.SUCCESS;
    }
}
