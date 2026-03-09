package com.iglesia.service;

import com.iglesia.*;
import com.iglesia.dto.EnrollmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchService churchService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             PersonRepository personRepository,
                             CourseRepository courseRepository,
                             PaymentRepository paymentRepository,
                             ChurchService churchService) {
        this.enrollmentRepository = enrollmentRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.paymentRepository = paymentRepository;
        this.churchService = churchService;
    }

    @Transactional
    public EnrollmentResponse create(Long personId, Long courseId) {
        Church church = churchService.requireChurch();

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        if (!person.getChurch().getId().equals(church.getId())
                || !course.getChurch().getId().equals(church.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos no pertenecen a la iglesia");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setPerson(person);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDIENTE);
        enrollmentRepository.save(enrollment);

        Payment payment = new Payment();
        payment.setType(PaymentType.INSCRIPCION_CURSO);
        payment.setAmount(course.getPrice());
        payment.setReferenceId(enrollment.getId());
        paymentRepository.save(payment);

        enrollment.setPaymentId(payment.getId());
        enrollmentRepository.save(enrollment);

        return EnrollmentResponse.from(enrollment, payment);
    }

    public List<EnrollmentResponse> list() {
        Church church = churchService.requireChurch();
        return enrollmentRepository.findAllByPersonChurchId(church.getId())
                .stream()
                .map(enrollment -> {
                    Payment payment = null;
                    if (enrollment.getPaymentId() != null) {
                        payment = paymentRepository.findById(enrollment.getPaymentId()).orElse(null);
                    }
                    return EnrollmentResponse.from(enrollment, payment);
                })
                .toList();
    }
}