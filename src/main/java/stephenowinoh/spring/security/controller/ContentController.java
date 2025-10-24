package stephenowinoh.spring.security.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import stephenowinoh.spring.security.MyUserDetailsService;
import stephenowinoh.spring.security.login.LoginForm;
import stephenowinoh.spring.security.token.JwtService;

@RestController
public class ContentController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @GetMapping("/home")
    public String handleWelcome() {
        return "Home";
    }

    @GetMapping("/admin/home")
    public String handleAdminHome(){
        return "home_admin";
    }

    @GetMapping("/user/home")
    public String handleUserHome(){
        return "user_home";
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginForm.username(), loginForm.password()

        ));
        if (authentication.isAuthenticated()) {
           return jwtService.generateToken(myUserDetailsService.loadUserByUsername(loginForm.username()));

        }else {
            throw new UsernameNotFoundException("Invalid Credentials");
        }

    }

}
