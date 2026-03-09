package com.iglesia.dto;

import com.iglesia.Payment;

public record PaymentResponse(
        Long id,
        String type,
        String status,
        String amount,
        int attempts,
        Long referenceId
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getType().name(),
                payment.getStatus().name(),
                payment.getAmount().toPlainString(),
                payment.getAttempts(),
                payment.getReferenceId()
        );
    }
}