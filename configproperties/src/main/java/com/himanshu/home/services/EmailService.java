package com.himanshu.home.services;

import org.springframework.stereotype.Service;

import com.himanshu.home.records.MailProperties;

@Service
public class EmailService {

    private final MailProperties mailProperties;

    public EmailService(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    public void printConnectionDetails() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("Email Host: " + mailProperties.host());
        System.out.println("Email Port: " + mailProperties.port());
        System.out.println("----------------------------------------------------------------");
    }
}
