package bank.app.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bank.app.demo.model.dto.SensitiveDataDto;
import bank.app.demo.model.dto.TransactionHistory;
import bank.app.demo.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/sensitiveData")
    public ResponseEntity<SensitiveDataDto> sensitiveData() {
        return ResponseEntity.ok().body(userService.sensitiveData());

    }

    @GetMapping("/ping")
    public void ping() {
    }

    @GetMapping("/transactionHistory")
    public ResponseEntity<TransactionHistory> transactionHistory() {
        return ResponseEntity.ok().body(userService.transactionHistory());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("token", "") 
                                     .path("/")
                                     .httpOnly(true)
                                     .maxAge(0)  
                                     .build();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookie.toString())
                             .body("Logged out successfully");
    }

}
