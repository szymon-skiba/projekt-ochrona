package bank.app.demo.service;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bank.app.demo.model.entities.User;
import bank.app.demo.model.entities.UserMaskedPassword;
import bank.app.demo.repository.UserMaskedPasswordRepository;
import bank.app.demo.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class PasswordService {

    private final UserMaskedPasswordRepository userMaskedPasswordRepository;
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PasswordService(UserMaskedPasswordRepository userMaskedPasswordRepository, UserRepository userRepository) {
        this.userMaskedPasswordRepository = userMaskedPasswordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void prepareMaskedPasswords(String password, int number, String username) {
        Optional<User> user = userRepository.findByEmail(username);
        if (!user.isPresent()) {
            throw new RuntimeException("Unknown user");
        }
        userMaskedPasswordRepository.deleteByUserm_Email(username);
        SecureRandom secureRandom = new SecureRandom();
        int passwordLength = password.length();
        for (int i = 0; i < number; i++) {
            int letters = 0;
            int firstLetter = secureRandom.nextInt(passwordLength);
            letters += (firstLetter + 1) * 100;
            int secondLetter = secureRandom.nextInt(passwordLength);
            while(secondLetter == firstLetter){
                secondLetter = secureRandom.nextInt(passwordLength);
            }
            letters += (secondLetter + 1) * 10;
            int thirdLetter = secureRandom.nextInt(passwordLength);
            while(thirdLetter==firstLetter || thirdLetter==secondLetter){
                thirdLetter = secureRandom.nextInt(passwordLength);
            }
            letters += thirdLetter + 1;

            String masked = username + password.charAt(firstLetter) + password.charAt(secondLetter)
                    + password.charAt(thirdLetter);

            UserMaskedPassword userMaskedPassword = new UserMaskedPassword();
            userMaskedPassword.setLetters(letters);
            userMaskedPassword.setMaskedPassword(passwordEncoder.encode(masked));
            userMaskedPassword.setUserm(user.get());

            userMaskedPasswordRepository.save(userMaskedPassword);
        }

    }

}
