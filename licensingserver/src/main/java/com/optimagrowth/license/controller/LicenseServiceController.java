package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value="v1/organization/{organizationId}/license")
@RequiredArgsConstructor
public class LicenseServiceController {

    private final LicenseService licenseService;
    @RequestMapping(value="/{licensId}/{clientType}", method=RequestMethod.GET)
    public License getLicenseWithClient(@PathVariable("organizationId") String organizationId,
                                        @PathVariable("licenseId") String licenseId,
                                        @PathVariable("clientType") String clientType){

        return licenseService.getLicense(licenseId,organizationId,clientType);
    }


//    @GetMapping(value="/{licenseId}")
//    public ResponseEntity<License> getLicense(@PathVariable("organizationId") String organizationId, @PathVariable("licenseId") String licenseId){
//
//        License license = licenseService.getLicense(licenseId, organizationId);
//        license.add(
//                linkTo(methodOn(LicenseServiceController.class).getLicense(license.getLicenseId(), organizationId)).withSelfRel(),
//                linkTo(methodOn(LicenseServiceController.class).createLicense(organizationId, license, null)).withRel("createLicense"),
//                linkTo(methodOn(LicenseServiceController.class).updateLicense(organizationId, license)).withRel("updateLicense"),
//                linkTo(methodOn(LicenseServiceController.class).deleteLicense(organizationId, license.getLicenseId())).withRel("deleteLicense"));
//
//        return ResponseEntity.ok(license);
//    }

    @PutMapping
    public ResponseEntity<String> updateLicense(@PathVariable("organizationId") String organizationId, @RequestBody License request){
        return ResponseEntity.ok(licenseService.updateLicesnse(request, organizationId));
    }

    @PostMapping
    public ResponseEntity<String> createLicense(@PathVariable("organizationId") String organizationId, @RequestBody License request, @RequestHeader(value="Accept-Language", required = false) Locale locale){
        return ResponseEntity.ok(licenseService.createLicense(request, organizationId, locale));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteLicense(@PathVariable("organizationId") String organizationId, @RequestBody String licenseId){
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId, organizationId));
    }

    @GetMapping("/")
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) throws TimeoutException {
//        logger.debug("LicenseServiceController Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
        return licenseService.getLicensesByOrganization(organizationId);
    }

}

