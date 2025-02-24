package com.backend.golvia.app.configs;

import com.backend.golvia.app.services.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppStartupRunner implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        adminService.createAdminIfNotExist();
    }
}
