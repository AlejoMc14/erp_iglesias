package com.iglesia;

import com.iglesia.dto.EnrollmentResponse;
import com.iglesia.service.EnrollmentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;  // ← solo el servicio

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public EnrollmentResponse create(@RequestBody EnrollmentRequest request) {
        return enrollmentService.create(request.personId(), request.courseId()); // ← delega
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<EnrollmentResponse> list() {
        return enrollmentService.list(); // ← delega
    }

    public record EnrollmentRequest(
            @NotNull Long personId,
            @NotNull Long courseId
    ) {}
}