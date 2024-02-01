package bank.app.demo.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;
import java.security.Provider.Service;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bank.app.demo.model.dto.ControllerResponse;
import bank.app.demo.model.dto.PasswordResetResposne;
import bank.app.demo.model.dto.ServiceResponse;
import bank.app.demo.model.dto.auth.AuthenticationRequest;
import bank.app.demo.model.dto.auth.AuthenticationResponse;
import bank.app.demo.model.dto.auth.PasswordReset;
import bank.app.demo.model.dto.auth.PasswordResetRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Value("${jwt.cookieExpiry}")
    private int cookieExpiry;

    private final AuthenticationService service;

    @PostMapping("/passwordreset")
    public ResponseEntity<PasswordResetResposne> passwordreset_request(
            @RequestBody PasswordResetRequest request) {
        
        PasswordResetResposne response = new PasswordResetResposne(service.passwordreset_request(request));
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/passwordreset")
    public ResponseEntity passwordreset(
            @RequestBody PasswordReset request) {
        service.passwordreset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/maskedLogin/letters")
    public ResponseEntity<Integer> maskedLoginLetters(
        @RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok().body(service.maskedLoginLetters(request.getEmail()));
    }

    @PostMapping("/maskedLogin")
    public ResponseEntity<ControllerResponse<AuthenticationResponse>> maskedLoginAuthenticate(
            @RequestBody AuthenticationRequest request, HttpServletResponse response) {
                ServiceResponse<AuthenticationResponse> serviceResponse = service.maskedLoginAuthenticate(request);

            if(serviceResponse.getSuccess()){
                ResponseCookie cookie = ResponseCookie.from("token", serviceResponse.getCookieToken())
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(cookieExpiry)
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            }

            return ResponseEntity.ok().body(new ControllerResponse<AuthenticationResponse>(serviceResponse.getData(), serviceResponse.getSuccess(), serviceResponse.getMessage()));
    }

    @PostMapping("/authenticate")
    public void authenticate(
            @RequestBody AuthenticationRequest request, HttpServletResponse response) {

        String token = service.authenticate(request);
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(cookieExpiry)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }
}
