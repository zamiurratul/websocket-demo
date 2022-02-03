package com.zamiurratul.websocket.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
@CrossOrigin(origins = "*")
public class CommonController {

    @GetMapping("/info")
    public String info() {
        return "This is the response from backend";
    }
}
