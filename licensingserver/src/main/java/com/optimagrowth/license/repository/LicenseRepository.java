package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.License;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LicenseRepository extends CrudRepository<License,String> {
    License findByOrganizationIdAndLicenseId(String organization, String licenseId);

    List<License> findByOrganizationId(String organizationId);
}
