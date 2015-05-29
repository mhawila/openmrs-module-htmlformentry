package org.openmrs.module.htmlformentry.web;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.util.StringUtils;

public class SimpleProvider {
    private Integer providerId;
    private String identifier;
    private String name;
    private String uuid;

    public SimpleProvider(Provider provider) {
        this.providerId = provider.getProviderId();
        this.identifier = provider.getIdentifier();
        if(StringUtils.hasLength(provider.getName())){
            this.name = provider.getName();
        } else {
            //Use person names if they exist.
            Person p = provider.getPerson();
            if(p!=null) {
                this.name = p.getGivenName() + " " + p.getFamilyName() + ", " + p.getMiddleName();
            }
        }
        this.uuid = provider.getUuid();
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
