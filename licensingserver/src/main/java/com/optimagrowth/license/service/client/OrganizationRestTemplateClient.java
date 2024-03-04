package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    //로드 밸런서 지원 RestTemplate을 사용할 때 유레카 서비스 ID로 대상 URL을 생성한다.
    public Organization getOrganization(String organizationId){
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://organization-service/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
