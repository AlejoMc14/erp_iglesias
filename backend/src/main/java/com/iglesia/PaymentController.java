package com.iglesia;

import com.iglesia.dto.PaymentResponse;
import com.iglesia.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;  // ← solo el servicio

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<PaymentResponse> list(@RequestParam(name = "status", required = false) PaymentStatus status) {
        return paymentService.list(status);  // ← delega
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/confirm")
    public PaymentResponse confirm(@PathVariable Long id) {
        return paymentService.confirm(id);  // ← delega
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/fail")
    public PaymentResponse fail(@PathVariable Long id) {
        return paymentService.fail(id);  // ← delega
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/retry")
    public PaymentResponse retry(@PathVariable Long id) {
        return paymentService.retry(id);  // ← delega
    }
}