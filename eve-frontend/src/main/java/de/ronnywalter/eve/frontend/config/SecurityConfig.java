package de.ronnywalter.eve.frontend.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurityConfigurerAdapter {

    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    public static final String LOGOUT_URL = "/";
    private static final String LOGOUT_SUCCESS_URL = "/";

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //super.configure(http);
        // Vaadin handles CSRF internally
       http.csrf().disable()
                 // Register our CustomRequestCache, which saves unauthorized access attempts, so the user is redirected after login.
                .requestCache().requestCache(new CustomRequestCache())

                .and().authorizeRequests()
               .antMatchers("/", "/eve-esi/code/eve-characters", "/eve-esi/authorization/eve-characters")
               .permitAll()

                // Allow all Vaadin internal requests.
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

                // Allow all requests by logged-in users.
                .anyRequest().authenticated()

                // Configure the login page.
                ;

    }

    /**
     * Allows access to static resources, bypassing Spring Security.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // Client-side JS
                "/VAADIN/**",

                // the standard favicon URI
                "/favicon.ico",

                // the robots exclusion standard
                "/robots.txt",

                // web application manifest
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",

                // icons and images
                "/icons/**",
                "/images/**",
                "/styles/**");
    }



}