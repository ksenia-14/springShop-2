package com.example.springshop2.config;

import com.example.springshop2.services.PersonDetailsService;
import com.example.springshop2.token.JWTAuthenticationFilter;
import com.example.springshop2.token.JWTTokenHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonDetailsService personDetailsService;
    private final JWTTokenHelper jWTTokenHelper;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(PersonDetailsService personDetailsService, JWTTokenHelper jWTTokenHelper, AuthenticationEntryPoint authenticationEntryPoint) {
        this.personDetailsService = personDetailsService;
        this.jWTTokenHelper = jWTTokenHelper;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    // конфигурация Spring Security
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .csrf().disable().cors().and().headers().frameOptions().disable()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint).and()
            .authorizeRequests((request) -> request
                    .antMatchers( "/api/authentication/login","/api/authentication/registration", "/api/product/**", "/api/file/**").permitAll()
                    .antMatchers("/api/admin/**").hasRole("ADMIN")
                    .antMatchers("/api/user/**").hasRole("USER")
                    .antMatchers("/api/seller/**").hasRole("SELLER")
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyRequest().authenticated())
            .addFilterBefore(new JWTAuthenticationFilter(personDetailsService, jWTTokenHelper),
                             UsernamePasswordAuthenticationFilter.class)
            .formLogin().loginPage("/api/authentication/login")
            .loginProcessingUrl("/process_login")
            .defaultSuccessUrl("/index", true)
            .failureUrl("/api/login")
            .and()
            .logout().logoutUrl("/api/authentication/logout").logoutSuccessUrl("/api/authentication/login")
            ;
            // РАЗРЕШАЕМ ВСЕЕЕЕ НИКАКОГО CORS
            http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());

    }

    // настройка аутентификации
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncoder()); // подключение шифрования пароля
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
