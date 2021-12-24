package bootlegger.tacocloud.configs;

import bootlegger.tacocloud.services.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserAuthService userAuthService;

    //Разрешение доступа к страницам
    @Override
    protected void configure (HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/design", "/orders").hasRole("USER")
            .antMatchers("/", "/**").access("permitAll")
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/design")
            .and()
            .logout().logoutSuccessUrl("/")
            .and()
            .csrf().ignoringAntMatchers("/h2-console/**")
            .and()
            .headers().frameOptions().sameOrigin()
        ;
    }

    @Bean
    public PasswordEncoder encoder () {
        return new BCryptPasswordEncoder();
    }

    //Аутентификация
    @Override
    protected void configure (AuthenticationManagerBuilder managerBuilder) throws Exception {
        managerBuilder.userDetailsService(userAuthService).passwordEncoder(encoder());
    }

}
