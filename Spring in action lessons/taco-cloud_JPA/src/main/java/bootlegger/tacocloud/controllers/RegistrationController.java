package bootlegger.tacocloud.controllers;

import bootlegger.tacocloud.model.RegistrationForm;
import bootlegger.tacocloud.model.User;
import bootlegger.tacocloud.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public RegistrationController (UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping
    public String registerForm(){
        return "registerForm";
    }

    @PostMapping
    public String processRegistrationForm(RegistrationForm form){
        User pass = userRepository.save(form.toUser(passwordEncoder));
        System.out.println(pass);
        return "redirect:/login";
    }
}
