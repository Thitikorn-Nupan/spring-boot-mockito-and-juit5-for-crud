package com.ttknpdev.understandunittestandmockkito.service_logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttknpdev.understandunittestandmockkito.control.UserController;
import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.service.UserService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.mockito.BDDMockito.*;


// *** JUnit5 test cases for CRUD REST APIs.
// *** use the @WebMvcTest annotation to load only UserController class. (can multiple rest controller)
@WebMvcTest(controllers = UserController.class)
public class MyUserControllerTest {

    // *** using MockMvc class to make REST API calls.
    @Autowired
    private MockMvc mockMvc;

    /// @Autowired
    /// private ObjectMapper objectMapper;
    /// private UserRepository userRepository;
    // *** using @MockBean annotation to add mock objects to the Spring application context.
    // *** The mock will replace any existing bean of the same type in the application context.
    @MockBean
    private UserService userService;

    // JUnit test for reads
    @Test
    public void givenListOfUser_whenCallApiReads_thenReturnListOfUser() throws Exception {

        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        // given - precondition or setup
        given(userService.reads()).willReturn(getUsers());

        /// ** call the provider **
        // when -  action or the behaviour(n.พฤติกรรม) that we are going test
        RequestBuilder request = MockMvcRequestBuilders.get("/api/reads");
        // ** ResultActions class to handle the response of the REST API.
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        // then - verify the output
        response.andExpect(
                        jsonPath("$.size()", is(2))
                // seem <arrayListYouGot>().size() == is(<sizeYouProvide>)
                )
                .andExpect(status().isAccepted());
    }

    // JUnit test for read
    @Test
    public void givenUser_whenCallApiReadAndPassPathParam_thenReturnUser() throws Exception {
        User user = getUser();
        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.findByUsername(user.getUsername())).willReturn(getUser());

        /// ** call the provider **
        RequestBuilder request = MockMvcRequestBuilders.get("/api/read/{username}", user.getUsername()); // we can pass dynamic path param as .get("/api/read/{key}", key)

        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        // then - verify the output
        response.andExpect(
                        // *** "$.<json key>"
                        jsonPath("$.username", is(user.getUsername()) )
                )
                .andExpect(
                        jsonPath("$.mail", is(user.getMail()) )
                )
                .andExpect(status().isAccepted());
    }

    // JUnit test for read
    @Test
    public void givenUser_whenCallApiReadAndPassRequestParam_thenReturnUser() throws Exception {
        User user = getUser();
        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.findByUsername(user.getUsername())).willReturn(getUser());

        /// ** call the provider **
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/read")
                // we can pass dynamic request param as /read?<key>=<value>
                // result look like /read?username=adam
                .param("username",user.getUsername());

        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        // then - verify the output
        response.andExpect(
                        // *** "$.<json key>"
                        jsonPath("$.username", is(user.getUsername()) )
                )
                .andExpect(
                        jsonPath("$.mail", is(user.getMail()) )
                )
                .andExpect(status().isAccepted());
    }

    // JUnit test for create
    @Test
    public void givenUser_whenCallApiCreateAndPassJsonData_thenReturnUser() throws Exception {
        User user = getUser();
        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.create(user)).willReturn(getUser());

        // convert java to json as string
        String requestBody = new ObjectMapper().writeValueAsString(user);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        /// ** call the provider **
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        // then - verify the output
        response.andExpect(
                        // *** "$.<json key>"
                        jsonPath("$.username", is(user.getUsername()) )
                )
                .andExpect(
                        jsonPath("$.mail", is(user.getMail()) )
                )
                .andExpect(status().isCreated());
    }

    // JUnit test for update
    @Test
    public void givenUser_whenCallApiUpdateAndPassJsonDataAndRequestParam_thenReturnUser() throws Exception {
        String usernameSearch = "adam";
        User user = getUser();

        // mocking save your service!
        // first i findById(...)
        // if found i save(...)

        given(userService.findByUsername(usernameSearch)).willReturn(user);
        // update user
        user.setUsername("update");
        user.setMail("update@update.abc");
        given(userService.update(user,usernameSearch)).willReturn(user);


        // convert java to json as string
        String requestBody = new ObjectMapper().writeValueAsString(user);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/update")
                .param("username",usernameSearch)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        /// ** call the provider **
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        response.andExpect( status().isAccepted() );
        response.andExpect( jsonPath("$.username", is(user.getUsername()) ) )
                .andExpect( jsonPath("$.mail", is(user.getMail()) ) );
    }

    // JUnit test for delete
    @Test
    public void delete() throws Exception {
        String usernameSearch = "adam";
        User user = getUser();

        // optional ????
        // mocking delete your service!
        // first i findById(...)
        // if found i delete(...)

        given(userService.findByUsername(usernameSearch)).willReturn(user);

        // willDoNothing().given(userService).delete(usernameSearch); // if your delete(...) is void
        given(userService.delete(usernameSearch)).willReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/delete")
                .param("username",usernameSearch);

        /// ** call the provider **
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        //// response.andExpect( status().isAccepted() );
        response.andDo(print()); // print result
        // ** get response
        MvcResult mvcResult = response.andReturn();
        MockHttpServletResponse mockHttpServletResponse = mvcResult.getResponse();
        // ** and you can verify with any assert** as same test service logic
        assertThat(mockHttpServletResponse.getStatus()).isEqualTo(202);
        assertThat(mockHttpServletResponse.getErrorMessage()).isNull();
        assertThat(Boolean.parseBoolean(mockHttpServletResponse.getContentAsString())).isEqualTo(true);
        assertThat(mockHttpServletResponse.getHeader("Message")).isEqualTo("User deleted");
        assertThat(mockHttpServletResponse.getContentType()).isEqualTo("application/json");

    }

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
                new User("adam", "adam@abc.abc"),
                new User("kavin", "kavin@abc.abc")
        );
    }

}
