package com.iglesia.dto;

import com.iglesia.Enrollment;
import com.iglesia.Payment;

public record EnrollmentResponse(
        Long id,
        Long personId,
        String personName,
        Long courseId,
        String courseName,
        String status,
        Long paymentId,
        String paymentStatus
) {
    public static EnrollmentResponse from(Enrollment enrollment, Payment payment) {
        String personName = enrollment.getPerson().getFirstName()
                + " " + enrollment.getPerson().getLastName();
        String paymentStatus = payment == null ? null : payment.getStatus().name();
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getPerson().getId(),
                personName,
                enrollment.getCourse().getId(),
                enrollment.getCourse().getName(),
                enrollment.getStatus().name(),
                enrollment.getPaymentId(),
                paymentStatus
        );
    }
}