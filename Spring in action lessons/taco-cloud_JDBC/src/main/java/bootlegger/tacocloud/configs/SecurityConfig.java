package bootlegger.tacocloud.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure (AuthenticationManagerBuilder managerBuilder) throws Exception {
        managerBuilder.ldapAuthentication()
                      .userSearchBase("ou=people")
                      .userSearchFilter("(uid={0})")
                      .groupSearchBase("ou=groups")
                      .groupSearchFilter("member={0}")
                      .passwordCompare()
                      .passwordEncoder(new BCryptPasswordEncoder())
                      .passwordAttribute("passcode")
                      .and()
                      .contextSource()
                      .root("dc=tacocloud,dc=com")
                      .ldif("classpath:users.ldif");


    }
}
