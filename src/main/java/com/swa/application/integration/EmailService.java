package com.swa.application.integration;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void send(String msg) {
        System.out.println(msg);
    }
}
