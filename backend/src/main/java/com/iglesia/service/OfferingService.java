package com.iglesia.service;

import com.iglesia.*;
import com.iglesia.dto.OfferingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OfferingService {

    private final OfferingRepository offeringRepository;
    private final PersonRepository personRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchService churchService;

    public OfferingService(OfferingRepository offeringRepository,
                           PersonRepository personRepository,
                           PaymentRepository paymentRepository,
                           ChurchService churchService) {
        this.offeringRepository = offeringRepository;
        this.personRepository = personRepository;
        this.paymentRepository = paymentRepository;
        this.churchService = churchService;
    }

    @Transactional
    public OfferingResponse create(Long personId, BigDecimal amount, String concept) {
        Church church = churchService.requireChurch();

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        if (!person.getChurch().getId().equals(church.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona no pertenece a la iglesia");
        }

        Offering offering = new Offering();
        offering.setPerson(person);
        offering.setAmount(amount);
        offering.setConcept(concept);
        offering.setStatus(OfferingStatus.PENDIENTE);
        offeringRepository.save(offering);

        Payment payment = new Payment();
        payment.setType(PaymentType.OFRENDA);
        payment.setAmount(amount);
        payment.setReferenceId(offering.getId());
        paymentRepository.save(payment);

        offering.setPaymentId(payment.getId());
        offeringRepository.save(offering);

        return OfferingResponse.from(offering, payment);
    }

    public List<OfferingResponse> list() {
        Church church = churchService.requireChurch();
        return offeringRepository.findAllByPersonChurchId(church.getId())
                .stream()
                .map(offering -> {
                    Payment payment = null;
                    if (offering.getPaymentId() != null) {
                        payment = paymentRepository.findById(offering.getPaymentId()).orElse(null);
                    }
                    return OfferingResponse.from(offering, payment);
                })
                .toList();
    }
}