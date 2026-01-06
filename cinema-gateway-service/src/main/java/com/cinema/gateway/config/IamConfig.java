package com.cinema.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "iam")
public class IamConfig {

    private boolean enabled;
    private ServiceAccounts serviceAccounts = new ServiceAccounts();
    private Keys keys = new Keys();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ServiceAccounts getServiceAccounts() {
        return serviceAccounts;
    }

    public void setServiceAccounts(ServiceAccounts serviceAccounts) {
        this.serviceAccounts = serviceAccounts;
    }

    public Keys getKeys() {
        return keys;
    }

    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    public static class ServiceAccounts {
        private String admin;
        private String user;

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public List<String> getAdminList() {
            return admin != null ? List.of(admin.split(",")) : new ArrayList<>();
        }

        public List<String> getUserList() {
            return user != null ? List.of(user.split(",")) : new ArrayList<>();
        }
    }

    public static class Keys {
        private String directory;

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }
}
