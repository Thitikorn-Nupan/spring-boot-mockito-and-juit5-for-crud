package com.ttknpdev.understandunittestandmockkito.service;

import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // this annotation we can let the constructor be automatically generated , So lines 16 - 19 wasn't nessessory
public class UserService {

    private final UserRepository userRepository;
    // @Autowired -- I inject it inside the Controller Class
    /*public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }*/
    public User create (User user) {
        return userRepository.save(user);
    }
    public List<User> reads() {
        return (List<User>) userRepository.findAll();
    }
    public User findByUsername (String username) {
        return userRepository.findById(username).orElseThrow(() -> new RuntimeException("Not found : "+username));
    }
    public boolean delete(String username) {
        Optional<User> user = userRepository.findById(username);
        if (user.isPresent()) {
            userRepository.deleteById(username);
            // System.out.println("work");
            return true;
        }
        else {
            return false;
        }
    }
    /*public User update(User user , String username) { // can't use for mocking
        return userRepository.findById(username).map(user1 -> {
            user1.setMail(user.getMail());
            user1.setUsername(user.getUsername());
            return userRepository.save(user1);
        }).orElseThrow(() -> new RuntimeException("Not found : "+username));
    }*/

    public User readBeforeUpdateForMocking (String username) {
        return userRepository.findById(username).orElseThrow(() -> new RuntimeException("Not found : "+username));
    }
    public User updateAfterReadMocking (User newUser) {
        return userRepository.save(newUser);
    }
}
