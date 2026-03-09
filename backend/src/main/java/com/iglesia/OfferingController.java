package com.iglesia;

import com.iglesia.dto.OfferingResponse;
import com.iglesia.service.OfferingService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {

    private final OfferingService offeringService;  // ← solo el servicio

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public OfferingResponse create(@RequestBody OfferingRequest request) {
        return offeringService.create(request.personId(), request.amount(), request.concept()); // ← delega
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<OfferingResponse> list() {
        return offeringService.list(); // ← delega
    }

    public record OfferingRequest(
            @NotNull Long personId,
            @NotNull BigDecimal amount,
            @NotBlank String concept
    ) {}
}