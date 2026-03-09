package com.iglesia.dto;

import com.iglesia.Offering;
import com.iglesia.Payment;

public record OfferingResponse(
        Long id,
        Long personId,
        String personName,
        String concept,
        String amount,
        String status,
        Long paymentId,
        String paymentStatus
) {
    public static OfferingResponse from(Offering offering, Payment payment) {
        String personName = offering.getPerson().getFirstName()
                + " " + offering.getPerson().getLastName();
        String paymentStatus = payment == null ? null : payment.getStatus().name();
        return new OfferingResponse(
                offering.getId(),
                offering.getPerson().getId(),
                personName,
                offering.getConcept(),
                offering.getAmount().toPlainString(),
                offering.getStatus().name(),
                offering.getPaymentId(),
                paymentStatus
        );
    }
}