package com.ttknpdev.understandunittestandmockkito.api_logic;

import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.repository.UserRepository;
import com.ttknpdev.understandunittestandmockkito.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// concept is mocking
// **
// ** JUnit 5 + Mockito ** For testing Service Logic Not API
// **
@SpringBootTest
// จากการที่ทดลองใช้ JUnit 5 กับ Mockito บน Spring Boot project นั้นส่วนใหญ่จะไม่แตกต่างจากการใช้งาน JUnit 4 กับ Mockito มากนัก
// ** อาจจะมีเปลี่ยน annotation จาก @RunWith เป็น @ExtendWith และ dependency ที่ต้องเพิ่มเองเท่านั้น
@ExtendWith(MockitoExtension.class)
@Slf4j
public class MyUserServiceLogicTest {
    /*
    The @Mock annotation creates a mock implementation for the class it is annotated with.
    The @InjectMocks also creates the mock implementation of annotated type and injects the dependent mocks into it.
    mock (v. จำลอง ล้อเลียน)
    */
    @Mock
    private UserRepository userRepository;
    // @Mock
    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void initUseCase() {
        userService = new UserService(userRepository);
        // MockitoAnnotations.initMocks(this);
    }

    /**
     * Mockito provides a method called Mockito.verify()
     * that can be used to verify that a mock object was called with specific parameters.
     * If mock objects are not verified, it can lead to unreliable tests and unexpected behavior.
     * Here’s an example of how to use the Mockito.verify() method to verify mock
    */

    @Test
    public void create() {

        // This will make userRepository.save(any(User.class)) return the same user object that is passed into the method.*/
        when( userRepository.save (any(User.class)) ).then( returnsFirstArg() );

        User savedUser = userService.create(getUser());

        verify(userRepository,times(1)).save(getUser());

        assertThat("adam").isEqualTo(savedUser.getUsername());

    }
    @Test
    public void reads() {
        // keep concept mocking
        /*
        So, we have to tell Mockito to return something
        when userRepository.findAll() is called.
        We do this with the static when method.
        */

        when( userRepository.findAll() ).thenReturn( getUsers() );

        /*
        Now users object called userRepository.findAll() passed userService.reads()
        So it should store list of getUsers() method
        */
        List<User> users = userService.reads();


        assertEquals("kavin", users.get(1).getUsername());
        assertThat(users).hasSize(2);

        verify(userRepository,times(1)).findAll(); // verify (v. ตรวจสอบ) , invocations (n. การร้องขอ)
    }

    @Test
    public void read () {
        String username = "adam";
        /*
        why we use when() method
        because it's always returned null
        */
        when(userRepository.findById(username)).thenReturn(Optional.of(getUser()));

        User user = userService.findByUsername(username);

        assertThat( username ).isEqualTo( user.getUsername() );
        assertThat( getUser() ).isEqualTo( user );

        verify(userRepository,times(1)).findById(username);

    }

    /**
    @Test
    public void updateForMethodMock() {

        String username = "adam";

        // User oldUser = getUser();
        User newUser = new User("alex","alex@abc.abc");

        when(userRepository.findById(username)).thenReturn(Optional.of(getUser()));

        User oldUser2 = userService.readBeforeUpdateForMocking(username); // old data

        verify(userRepository,times(1)).findById(username);

        when( userRepository.save (any(User.class)) ).then( returnsFirstArg() );

        User newUser2 = userService.updateAfterReadMocking(newUser); // new data

        verify(userRepository,times(1)).save(newUser);

        assertThat("alex").isEqualTo(newUser2.getUsername());

    }
    */

    // Perfect! I can test with real service ** userService.update(...) it
    @Test
    public void update() {

        String username = "adam";

        User newUser = new User("alex","alex@abc.abc");

        // You have to know what methods you'll do on your service logic
        // in this case i do findById(username) and save(user)
        // *** so now provide it

        // provide response
        when(userRepository.findById(username)).thenReturn(Optional.of(getUser()));
        // call method
        // User oldUserResponse = userService.findByUsername(username);
        // Or
        User oldUserResponse = userRepository.findById(username).get();
        // and verify
        verify(userRepository,times(1)).findById(username);
        // and log if failed
        assertThat( oldUserResponse ).isEqualTo( getUser() );

        // ** Note. actual database wasn't updated
        when( userRepository.save (any(User.class)) ).then( returnsFirstArg() );

        User newUserResponse = userService.update(newUser,username); // real service method it'll do .findById(...), .save(...)

        verify(userRepository,times(1)).save(newUser);
        assertThat(newUserResponse.getUsername()).isEqualTo("alex");

        // *** Why i do this way because i have to know user is existing on database

    }

    @Test
    public void delete() {
        String username = "adam";

        // You have to know what methods you'll do on your service logic
        // in this case i do findById(username) and deleteById(username)
        // *** so now provide it
        // provide response
        when(userRepository.findById(username)).thenReturn(Optional.of(getUser()));

        User oldUserResponse = userRepository.findById(username).get();
        // and verify
        verify(userRepository,times(1)).findById(username);
        // and log if failed
        assertThat( oldUserResponse ).isEqualTo( getUser() );

        // ** provide
        // ** Step mock void method
        // **
        // ** We mock the deleteById method to do nothing.
        Mockito.doNothing()
                .when(userRepository)
                .deleteById(username);
        // call
        Boolean check = userService.delete(username);
        // then verify that the deleteById method was called exactly once.
        verify(userRepository,times(1)).deleteById(username);

        assertThat(check).isTrue();
    }

    // these are block for mocking value
    private User getUser() {
        return new User("adam","adam@abc.abc");
    }

    private List<User> getUsers() {
        /**
            List<User> expected = new ArrayList<>();
            User a = new User("adam","adam@abc.abc");
            User b = new User("kavin","kavin@abc.abc");
            expected.add(a);
            expected.add(b);
        */
        return List.of(
                new User("adam","adam@abc.abc"),
                new User("kavin","kavin@abc.abc")
        );
    }

}
