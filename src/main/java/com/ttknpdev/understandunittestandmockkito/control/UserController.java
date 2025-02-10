package com.ttknpdev.understandunittestandmockkito.control;

import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor // it'll look for all final keyword
public class UserController {

    private final UserService service;

    
    @GetMapping("/reads")
    private ResponseEntity<List<User>> reads () {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.reads());
    }
    @GetMapping("/read/{username}")
    private ResponseEntity<User> read (@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.findByUsername(username));
    }
    // create , update

    @PostMapping("/create")
    private ResponseEntity<User> create (@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(user));
    }

    @PutMapping("/update")
    private ResponseEntity<User> update (@RequestBody User user,@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.update(user,username));
    }

    @DeleteMapping("/delete")
    private ResponseEntity<Boolean> delete (@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.delete(username));
    }
}
