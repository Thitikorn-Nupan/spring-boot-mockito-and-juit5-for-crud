package com.ttknpdev.understandunittestandmockkito.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
@EnableWebSecurity

public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Way to define username / password in memory for authenticate
        User.UserBuilder userBuilder = User.builder(); // withDefaultPasswordEncoder() Deprecated. use instead builder() it's worked
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(userBuilder
                .username("user")
                .password(passwordEncoder().encode("12345"))
                .authorities("read")
                .build());
        manager.createUser(userBuilder
                .username("admin")
                .password(passwordEncoder().encode("12345"))
                .authorities("read", "write", "delete", "update")
                .build());
        manager.createUser(userBuilder
                .username("normal")
                .password(passwordEncoder().encode("12345"))
                .authorities("nothing")
                .build());
        return manager;
    }

    /*@Bean
    public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }*/

    // Going forward, the recommended way of doing this is registering a SecurityFilterChain bean
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity httpSecurity) throws Exception { //  , MvcRequestMatcher.Builder mvc

        /** Old way
         httpSecurity.authorizeRequests((requests) ->
         requests.anyRequest().authenticated()
         );
         */
/*        httpSecurity
                // .securityMatcher("/api/v2/**") // specify scope spring security
                .authorizeHttpRequests(( authorizationManagerRequestMatcherRegistry) ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(HttpMethod.GET, "/api/v2/reads").hasAuthority("read")
                                .requestMatchers(HttpMethod.POST, "/api/v2/create").hasAnyAuthority("write", "delete", "update")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());*/


        /*httpSecurity
                .securityMatcher("/api/v2/**")
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests.requestMatchers(HttpMethod.GET, "/api/v2/reads").hasAuthority("read").anyRequest().authenticated();
                    authorizeRequests.requestMatchers(HttpMethod.POST, "/api/v2/create").hasAnyAuthority("write", "delete", "update").anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults()) // use http basic //
                .csrf((csrf) -> { // enable csrf protection ** note it's working when you reaction on http bofy
                    csrf.ignoringRequestMatchers(
                            "/api/v2/create"
                    );
                })*/


        // spring security 6.1.4 have a difference config
        // see more ** https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html#match-by-mvc
        /*httpSecurity.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(mvc.pattern("/api/v2/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return httpSecurity.getOrBuild();*/


        // *** fix by second way ***
        // spring security 6.1.4 have a difference config
        // If you are having problem with the new requestMatchers methods,
        // you can always switch back to the RequestMatcher implementation that you were using.
        // For example, if you still want to use AntPathRequestMatcher and RegexRequestMatcher implementations,
        // you can use the requestMatchers method that accepts a RequestMatcher instance
        /**
        httpSecurity
                .authorizeHttpRequests((authz) -> authz
                        // ** Note that the above sample uses static factory methods from AntPathRequestMatcher and RegexRequestMatcher to improve readability.
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v2/reads")).hasAuthority("read")
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v2/create")).hasAnyAuthority("write", "delete", "update")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults()); // enable http basic *** dialog login
         */
         // Below is same
        httpSecurity
                .securityMatcher("/api/v2/**") // scope for authenticate/authorize spring security
                .authorizeHttpRequests((authz) -> {
                    authz.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/h2-ui")).permitAll();
                    authz.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/h2-ui/**")).permitAll();
                    authz.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v2/reads")).hasAuthority("read");
                    authz.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v2/read")).hasAuthority("read");
                    authz.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v2/create")).hasAuthority("write");
                })
                .httpBasic(withDefaults()) // enable http basic *** dialog login
                .headers().frameOptions().sameOrigin() // for enable loading h2 ui templates
        ;

        return httpSecurity.build();
    }

}
