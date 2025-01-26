package com.medimeet.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MediMeetController {
    private static final Logger logger = LoggerFactory.getLogger(MediMeetController.class);

    @GetMapping("/health")
    public String healthCheck() {
        logger.info("Health check endpoint called");
        return "MediMeet service is running";
    }
}
