package com.ttknpdev.understandunittestandmockkito.service;

import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor // this annotation we can let the constructor be automatically generated ,** looking for final keyword
public class UserService {

    private final UserRepository userRepository;

    /**
    @Autowired -- I inject it inside the Controller Class
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    */
    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> reads() {
        return (List<User>) userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findById(username).orElseThrow(() -> new RuntimeException("Not found : " + username));
    }

    public boolean delete(String username) {
        log.info("Deleting with username : {}", username);
        // not working for testing
        /**
        Optional<User> user = userRepository.findById(username);
        if (user.get() != null) {
            userRepository.deleteById(username);
            // System.out.println("work");
            return true;
        } else {
            return false;
        }
        */
        return userRepository.findById(username).map(user1 -> {
            // log.info("Deleting with username : {}", user1);
            userRepository.deleteById(user1.getUsername());
            return true;
        }).orElseThrow(() -> new RuntimeException("Not found : "+username));
    }

    public User update(User user , String username) { // can't use for mocking
        log.info("Updating user : {} with username : {}",user, user.getUsername());
        return userRepository.findById(username).map(user1 -> {
            user1.setMail(user.getMail());
            user1.setUsername(user.getUsername());
            return userRepository.save(user1);
        }).orElseThrow(() -> new RuntimeException("Not found : "+username));
    }

    /**
    public User readBeforeUpdateForMocking(String username) {
        return userRepository.findById(username).orElseThrow(() -> new RuntimeException("Not found : " + username));
    }

    public User updateAfterReadMocking(User newUser) {
        return userRepository.save(newUser);
    }
    */
}
