package com.ttknpdev.understandunittestandmockkito.control;

import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class Controller {
    private UserService service;
    @Autowired
    public Controller(UserService service) {
        this.service = service;
    }
    @GetMapping("/reads")
    private ResponseEntity<List<User>> reads () {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.reads());
    }
    @GetMapping("/read/{username}")
    private ResponseEntity<User> read (@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.findByUsername(username));
    }
}
