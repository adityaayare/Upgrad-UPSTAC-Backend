package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);
    @Autowired
    TestRequestFlowService testRequestFlowService;
    @Autowired
    private TestRequestUpdateService testRequestUpdateService;
    @Autowired
    private TestRequestQueryService testRequestQueryService;
    @Autowired
    private UserLoggedInService userLoggedInService;


    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations() {
        try {
            //Getting test requests for which status is LAB_TEST_COMPLETED
            return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor() {
        try {
            // Current logged in doctor
            User doctor = userLoggedInService.getLoggedInUser();
            //Getting test requests assigned to the doctor
            return testRequestQueryService.findByDoctor(doctor);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }


    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {
        try {
            // Current logged in doctor
            User doctor = userLoggedInService.getLoggedInUser();
            // Assigning for consultation
            return testRequestUpdateService.assignForConsultation(id, doctor);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id, @RequestBody CreateConsultationRequest testResult) {
        try {
            // Current logged in doctor
            User doctor = userLoggedInService.getLoggedInUser();
            // Updating the consultation
            return testRequestUpdateService.updateConsultation(id, testResult, doctor);
        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        } catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }


}
