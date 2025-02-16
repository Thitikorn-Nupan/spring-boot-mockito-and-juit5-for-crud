package com.ttknpdev.understandunittestandmockkito.api_security_logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttknpdev.understandunittestandmockkito.config.SecurityConfig;
import com.ttknpdev.understandunittestandmockkito.control.UserController;
import com.ttknpdev.understandunittestandmockkito.control.UserSecurityController;
import com.ttknpdev.understandunittestandmockkito.entity.User;
import com.ttknpdev.understandunittestandmockkito.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// *** JUnit5 test cases for CRUD REST APIs.  *** use the @WebMvcTest annotation to load only UserController class. (can multiple rest controller)
@WebMvcTest(controllers = {UserSecurityController.class, UserController.class})
// if api you have security configuration
// *** if you get 401 Unauthorized but you do the correct way
// you have to import it. you config like below,
@Import(SecurityConfig.class)
public class MyUserSecurityControllerTest {

    // *** using MockMvc class to make REST API calls.
    @Autowired
    private MockMvc mockMvc;

    // *** using @MockBean annotation to add mock objects to the Spring application context.
    @MockBean
    private UserService userService;

     @Test
    public void givenListOfUser_whenCallApiReadsAndAuthenticateOnHttpHeader_thenReturnListOfUserTestFirstSecurity() throws Exception {

        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.reads()).willReturn(getUsers());

        /// ** call the provider ** and set basic authenticate on http header
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/reads");

        // ** ResultActions class to handle the response of the REST API.
        ResultActions response = mockMvc.perform(request);

        // then - verify the output
        response.andExpect(
                        jsonPath("$.size()", is(2))
                )
                .andExpect(
                        // [{"username":"adam","mail":"adam@abc.abc"},{"username":"kavin","mail":"kavin@abc.abc"}]
                        jsonPath("$[0].username", is("adam"))
                )
                .andExpect(status().isAccepted());
    }


    @Test
    // @WithMockUser(username = "admin",password = "12345") // if you use @WithMockUser(...)  you don't have to use this @Import(...)
    public void givenListOfUser_whenCallApiReadsAndAuthenticateOnHttpHeader_thenReturnListOfUser() throws Exception {

        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.reads()).willReturn(getUsers());

        /// ** call the provider ** and set basic authenticate on http header
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v2/reads")
                .header("Authorization", "Basic YWRtaW46MTIzNDU=");

        // ** ResultActions class to handle the response of the REST API.
        ResultActions response = mockMvc.perform(request);

        // then - verify the output
        response.andExpect(
                        jsonPath("$.size()", is(2))
                )
                .andExpect(
                        jsonPath("$[0].username", is("adam"))
                )
                .andExpect(status().isAccepted());
    }

    @Test
    public void givenUser_whenCallApiReadAndPassPathParamAndAuthenticateOnHttpHeader_thenReturnUser() throws Exception {
        User user = getUser();
        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.findByUsername(user.getUsername())).willReturn(getUser());

        /// ** call the provider **
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v2/read/{username}", user.getUsername()) // we can pass dynamic path param as .get("/api/read/{key}", value)
                .with(httpBasic("admin", "12345"));

        ResultActions response = mockMvc.perform(request);

        // then - verify the output
        response.andExpect(
                        // *** "$.<json key>"
                        jsonPath("$.username", is(user.getUsername()))
                )
                .andExpect(
                        jsonPath("$.mail", is(user.getMail()))
                )
                .andExpect(status().isAccepted());
    }

    @Test
    public void givenUser_whenCallApiReadAndPassRequestParamAndAuthenticateOnHttpHeader_thenReturnUser() throws Exception {
        User user = getUser();
        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.findByUsername(user.getUsername())).willReturn(getUser());

        /// ** call the provider **
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v2/read")
                // we can pass dynamic request param as /read?<key>=<value>
                // result look like /read?username=adam
                .param("username",user.getUsername())
                .with(httpBasic("admin", "12345"));

        ResultActions response = mockMvc.perform(request);

        // then - verify the output
        response.andExpect(
                        jsonPath("$.username", is(user.getUsername()) )
                )
                .andExpect(
                        jsonPath("$.mail", is(user.getMail()) )
                )
                .andExpect(status().isAccepted());
    }


    @Test
    public void givenUser_whenCallApiCreateAndPassJsonDataAndAuthenticateOnHttpHeader_thenReturnUser() throws Exception {
        User user = getUser();
        /// ** provide response if service calls ** it's kinda same as when(...).return(...)
        given(userService.create(user)).willReturn(getUser());

        // convert java to json as string
        String requestBody = new ObjectMapper().writeValueAsString(user);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v2/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(httpBasic("admin", "12345"));

        /// ** call the provider **
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        // then - verify the output
        response.andExpect(
                        jsonPath("$.username", is(user.getUsername()) )
                )
                .andExpect(
                        jsonPath("$.mail", is(user.getMail()) )
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void givenUser_whenCallApiUpdateAndPassJsonDataAndRequestParamAndAuthenticateOnHttpHeader_thenReturnUser() throws Exception {
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
                .put("/api/v2/update")
                .param("username",usernameSearch)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(httpBasic("admin", "12345"));

        /// ** call the provider **
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        response.andExpect( status().isAccepted() );
        response.andExpect( jsonPath("$.username", is(user.getUsername()) ) )
                .andExpect( jsonPath("$.mail", is(user.getMail()) ) );
    }

    @Test
    public void delete() throws Exception {
        String usernameSearch = "adam";
        User user = getUser();

        // mocking delete your service!

        given(userService.findByUsername(usernameSearch)).willReturn(user); // optional

        // willDoNothing().given(userService).delete(usernameSearch); // if your delete(...) is void
        given(userService.delete(usernameSearch)).willReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/v2/delete")
                .param("username",usernameSearch)
                .with(httpBasic("admin", "12345"));

        /// ** call the provider **
        ResultActions response = mockMvc.perform(request);

        ///  ** result follow your api response
        response.andDo(print()); // print result on console
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
        return new User("adam", "adam@abc.abc");
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
