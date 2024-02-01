package bank.app.demo.config;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import bank.app.demo.model.entities.User;
import bank.app.demo.model.entities.UserAccount;
import bank.app.demo.model.entities.UserSensitiveData;
import bank.app.demo.repository.UserAccountRepository;
import bank.app.demo.repository.UserRepository;
import bank.app.demo.repository.UserSensitiveDataRepository;
import bank.app.demo.service.AesService;
import bank.app.demo.service.PasswordService;
import jakarta.transaction.Transactional;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserSensitiveDataRepository userSensitiveDataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordService passwordService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        Optional<User> isUser = userRepository.findByEmail("test@test.com");

        if (!isUser.isPresent()) {
            User user = new User();
            user.setFirstname("test");
            user.setLastname("test");
            user.setAddress("address");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");

            UserAccount account = new UserAccount();
            account.setAccountNumber("123456789");
            account.setAccountBalance(new BigDecimal(100));
            account.setUser(user);

            UserSensitiveData data = new UserSensitiveData();
            data.setCreditCardNumber(AesService.encrypt("123456789"));
            data.setIdCardNumber(AesService.encrypt("123456789"));
            data.setUser(user);

            userRepository.save(user);
            userAccountRepository.save(account);
            userSensitiveDataRepository.save(data);

            passwordService.prepareMaskedPasswords("test", 6, "test@test.com" );


            User user2 = new User();
            user2.setFirstname("test2");
            user2.setLastname("test2");
            user2.setAddress("address");
            user2.setPassword(passwordEncoder.encode("test2"));
            user2.setEmail("test2@test.com");

            UserAccount account2 = new UserAccount();
            account2.setAccountNumber("023456789");
            account2.setAccountBalance(new BigDecimal(100));
            account2.setUser(user2);

            UserSensitiveData data2 = new UserSensitiveData();
            data2.setCreditCardNumber(AesService.encrypt("023456789"));
            data2.setIdCardNumber(AesService.encrypt("023456789"));
            data2.setUser(user2);

            userRepository.save(user2);
            userAccountRepository.save(account2);
            userSensitiveDataRepository.save(data2);

            passwordService.prepareMaskedPasswords("test2", 6, "test2@test.com" );
        }

        alreadySetup = true;
    }
}