package com.iglesia;
import com.iglesia.service.ChurchService; 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final ChurchService churchService;      // ← 1. ChurchRepository → ChurchService

    public CourseController(CourseRepository courseRepository,
                            ChurchService churchService) {  // ← 2. constructor actualizado
        this.courseRepository = courseRepository;
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CourseResponse create(@RequestBody CourseRequest request) {
        Church church = churchService.requireChurch();  // ← 3. llamada actualizada
        Course course = new Course();
        course.setName(request.name());
        course.setDescription(request.description());
        course.setPrice(request.price());
        course.setChurch(church);
        courseRepository.save(course);
        return CourseResponse.from(course);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<CourseResponse> list() {
        Church church = churchService.requireChurch();  // ← 3. llamada actualizada
        return courseRepository.findAllByChurchId(church.getId())
                .stream()
                .map(CourseResponse::from)
                .toList();
    }

    // ← 4. requireChurch() eliminado

    public record CourseRequest(
            @NotBlank String name,
            String description,
            @NotNull BigDecimal price
    ) {}

    public record CourseResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            boolean active
    ) {
        public static CourseResponse from(Course course) {
            return new CourseResponse(
                    course.getId(),
                    course.getName(),
                    course.getDescription(),
                    course.getPrice(),
                    course.isActive()
            );
        }
    }
}