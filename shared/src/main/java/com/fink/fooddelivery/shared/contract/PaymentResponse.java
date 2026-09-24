package com.fink.fooddelivery.shared.contract;

public record PaymentResponse(Long paymentId, String status) {

    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }
}
