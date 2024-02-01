package bank.app.demo.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bank.app.demo.config.JwtService;
import bank.app.demo.model.dto.ServiceResponse;
import bank.app.demo.model.dto.auth.AuthenticationRequest;
import bank.app.demo.model.dto.auth.AuthenticationResponse;
import bank.app.demo.model.dto.auth.PasswordReset;
import bank.app.demo.model.dto.auth.PasswordResetRequest;
import bank.app.demo.model.dto.auth.RegisterRequest;
import bank.app.demo.model.entities.User;
import bank.app.demo.model.entities.UserMaskedPassword;
import bank.app.demo.repository.UserMaskedPasswordRepository;
import bank.app.demo.repository.UserRepository;
import bank.app.demo.service.PasswordService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordService passwordService;
    private final UserMaskedPasswordRepository userMaskedPasswordRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Optional<User> isUser = userRepository.findByEmail(request.getEmail());

        if (isUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        userRepository.save(user);

        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwt)
                .build();
    }

    public String authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return jwtService.generateToken(user);

    }

    public String passwordreset_request(PasswordResetRequest request) {

        Optional<User> isUser = userRepository.findByEmail(request.getEmail());

        if (isUser.isPresent()) {

            UUID uuid = UUID.randomUUID();
            String token = uuid.toString();

            isUser.get().setPasswordResetToken(token);
            isUser.get().setPasswordResetTokenExpiration(LocalDateTime.now().plusHours(1));

            userRepository.save(isUser.get());

            return "Unimplemented method 'passwordreset_request'\n"
                    + "Would send email containg link to resset password that is valid for 1 hour:"
                    + "https://localhost/resetPassword.html?token=" + token + "\n";
        }

        return "błąd";
    }

    @Transactional
    public void passwordreset(PasswordReset request) {

        Optional<List<User>> isUser = userRepository.findByPasswordResetToken(request.getToken());

        if (isUser.isPresent()) {
            List<User> validUsers = isUser.get().stream()
                    .filter(user -> user.getPasswordResetTokenExpiration() != null)
                    .filter(user -> user.getPasswordResetTokenExpiration()
                            .isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());

            if (!validUsers.isEmpty() && !(validUsers.size() > 1)) {
                validUsers.get(0).setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(validUsers.get(0));
                passwordService.prepareMaskedPasswords(request.getPassword(), 6, validUsers.get(0).getEmail());
            }
        }
    }

    @Transactional
    public Integer maskedLoginLetters(String username) {

        Optional<User> user = userRepository.findByEmail(username);
        if (user.get().getBanned()) {
            return Integer.valueOf(0);
        }

        List<UserMaskedPassword> maskedPasswords = userMaskedPasswordRepository.findByUserm_Email(username);

        SecureRandom secureRandom = new SecureRandom();
        int poz = secureRandom.nextInt(6);
        int requestedLetters = maskedPasswords.get(poz).getLetters();

        user.get().setRequestedLetters(requestedLetters);
        userRepository.save(user.get());
        return Integer.valueOf(requestedLetters);

    }

    public ServiceResponse<AuthenticationResponse> maskedLoginAuthenticate(AuthenticationRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        UserMaskedPassword userMaskedPassword = userMaskedPasswordRepository
                .findByUserm_EmailAndLetters(user.getEmail(), user.getRequestedLetters()).get(0);
        String masked = user.getEmail() + request.getPassword().charAt(0) + request.getPassword().charAt(1)
                + request.getPassword().charAt(2);

        int loginCount = user.getLoginCount();
        user.setLoginCount(loginCount + 1);
        loginCount++;
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        ServiceResponse<AuthenticationResponse> response = new ServiceResponse<AuthenticationResponse>();

        if (user.getBanned()) {
            response.setData(new AuthenticationResponse("token"));
            response.setSuccess(false);
            response.setMessage("Account is banned. Try reseting password or contacting bank.");
        } else {

            if (loginCount > 3 && user.getLastLogin().isAfter(oneHourAgo)) {
                user.setBanned(true);
                response.setData(new AuthenticationResponse("token"));
                response.setSuccess(false);
                response.setMessage("Exceded max amount of tries. Account is banned. Try reseting password or contacting bank.");

            } else if (loginCount > 3 && !user.getLastLogin().isAfter(oneHourAgo) && !user.getBanned()) {
                user.setLoginCount(1);
            }
            
            if (!(passwordEncoder.matches(masked, userMaskedPassword.getMaskedPassword()))) {
                if (loginCount == 3) {
                    user.setBanned(true);
                    response.setData(new AuthenticationResponse("token"));
                    response.setSuccess(false);
                    response.setMessage("Exceded max amount of tries. Account is banned. Try reseting password or contacting bank.");
                }else {
                    response.setData(new AuthenticationResponse("token"));
                    response.setSuccess(false);
                    response.setMessage("Failed. 2 tries left.");
                }
            } else {
                response.setData(new AuthenticationResponse("token"));
                response.setCookieToken(jwtService.generateToken(user));
                response.setSuccess(true);
                response.setMessage("Success");
            }
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return response;
    }
}