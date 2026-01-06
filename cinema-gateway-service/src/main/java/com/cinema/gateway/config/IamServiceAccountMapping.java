package com.cinema.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IamServiceAccountMapping {

    @Autowired
    private IamConfig iamConfig;

    public String mapServiceAccountToRole(String serviceAccountEmail) {
        if (serviceAccountEmail == null) {
            return null;
        }

        String serviceAccountName = extractServiceAccountName(serviceAccountEmail);

        List<String> adminAccounts = iamConfig.getServiceAccounts().getAdminList();
        for (String adminAccount : adminAccounts) {
            if (serviceAccountName.contains(adminAccount.trim())) {
                return "ADMIN";
            }
        }

        List<String> userAccounts = iamConfig.getServiceAccounts().getUserList();
        for (String userAccount : userAccounts) {
            if (serviceAccountName.contains(userAccount.trim())) {
                return "USER";
            }
        }

        return "USER";
    }

    private String extractServiceAccountName(String serviceAccountEmail) {
        if (serviceAccountEmail.contains("@")) {
            return serviceAccountEmail.split("@")[0];
        }
        return serviceAccountEmail;
    }
}
