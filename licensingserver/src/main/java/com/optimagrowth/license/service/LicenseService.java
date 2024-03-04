package com.optimagrowth.license.service;

import com.optimagrowth.license.config.ServiceConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messages;

    private final LicenseRepository licenseRepository;

    private final ServiceConfig config;

    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    private final OrganizationFeignClient organizationFeignClient;

    private final OrganizationRestTemplateClient organizationRestClient;


    //회로차단기
    @CircuitBreaker(name="licenseService")
    public List<License> getLicensesByOrganization(String organizationId){
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private void randomlyRunLong() {
        Random random = new Random();
        int randomNum = random.nextInt(3) + 1;
        if(randomNum == 3) sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException e) {
            System.out.println(e);
        } catch (TimeoutException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

    }

//    public License getLicense(String licenseId, String organization){
//        License license = new License();
//        license.setLicenseId(licenseId);
//        license.
//
//        return License.builder()
//                .licenseId(licenseId)
//                .organizationId(organization)
//                .description("Software product")
//                .productName("Ostock")
//                .licenseType("full")
//                .build();
//    }

    public License getLicense(String licenseId, String organizationId, String clientType){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        if(null == license){
            throw new IllegalArgumentException(String.format(messages.getMessage("license.search.error.message", null,null), licenseId, organizationId));
        }

        Organization organization = retrieveOrganizationInfo(organizationId, clientType);

        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }

    public String createLicense(License license, String organizationId, Locale locale){
        String responseMessage = null;
        if (!StringUtils.isEmpty(license)){
            license.setOrganizationId(organizationId);
            responseMessage = String.format(messages.getMessage("license.create.message", null, locale), license.toString());
        }
        return responseMessage;
    }

    public String deleteLicense(String licenseId, String organizationId){
        return String.format("Deleting license with id %s for the organization %s", licenseId, organizationId);
    }

    public String updateLicesnse(License license, String organizationId) {
        String responseMessage = null;
        if(license != null){
            license.setOrganizationId(organizationId);
            responseMessage = String.format("This is the put and the object is: %s", license.toString());
        }
        return responseMessage;
    }
}
