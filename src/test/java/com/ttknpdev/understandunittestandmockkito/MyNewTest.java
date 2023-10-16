package com.ttknpdev.understandunittestandmockkito;

import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.repository.UserRepository;
import com.ttknpdev.understandunittestandmockkito.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MyNewTest {
    /*
    The @Mock annotation creates a mock implementation for the class it is annotated with.
    The @InjectMocks also creates the mock implementation of annotated type and injects the dependent mocks into it.
    */
    // mock (v. จำลอง ล้อเลียน)
    @Mock
    private UserRepository userRepository;
    // @InjectMocks
    private UserService userService;

    @BeforeEach
    public void initUseCase() {
        userService = new UserService(userRepository);
    }

    @Test
    public void create() {

        /*This will make userRepository.save(any(User.class)) return the same user object that is passed into the method.*/
        when( userRepository.save (any(User.class)) ).then( returnsFirstArg() );

        User savedUser = userService.create(getUser());
        assertThat(savedUser.getUsername()).isEqualTo("adam");
        verify(userRepository,times(1)).save(getUser());
        /*Now we will create a test class which will mock the MyDao class. In the first test
         we will verify that when we call the method of the service class (which in turn calls the DAO) the mock object has been called.
         We will do this by making use of the verify() method of the Mockito class.*/
        // when isn't equal
        /*
        org.opentest4j.AssertionFailedError:
        expected: "zaphods"
        but was: "zaphod"
        Expected :"zaphods"
        Actual   :"zaphod"*/
    }
    @Test
    public void reads() {
        // keep concept mocking
        /*
        So, we have to tell Mockito to return something when userRepository.findAll() is called.
        We do this with the static when method.
        */

        when( userRepository.findAll() ).thenReturn( getUsers() );

        /*
        Now users object called userRepository.findAll() passed userService.reads()
        So it should store list of getUsers() method
        */
        List<User> users = userService.reads();


        assertEquals("kavin", users.get(1).getUsername());
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
        verify(userRepository,times(1)).findById(username);

    }

    @Test
    public void update() {
        String username = "adam";
        User oldUser = new User("adam","adam@abc.abc");
        User newUser = new User("alex","alex@abc.abc");

        when(userRepository.findById(username)).thenReturn(Optional.of(oldUser));
        User oldUser2 = userService.readBeforeUpdateForMocking(username); // old data
        verify(userRepository,times(1)).findById(username);

        when( userRepository.save (any(User.class)) ).then( returnsFirstArg() );
        User newUser2 = userService.updateAfterReadMocking(newUser); // new data
        verify(userRepository,times(1)).save(newUser);

        assertThat("alex").isEqualTo(newUser2.getUsername());

    }

    @Test
    public void delete() {
        String username = "adam";
        User foundUser = new User("adam","adam@abc.abc");
        when(userRepository.findById(username)).thenReturn(Optional.of(foundUser)).thenReturn(null);
        Boolean check = userService.delete(username);
        verify(userRepository,times(1)).deleteById(username);
        assertEquals(true,check);
    }

    // these are block for mocking value
    private User getUser() {
        return new User("adam","adam@abc.abc");
    }

    private List<User> getUsers() {
        /*
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
