package bank.app.demo.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import bank.app.demo.model.dto.SensitiveDataDto;
import bank.app.demo.model.dto.TransactionDto;
import bank.app.demo.model.dto.TransactionHistory;
import bank.app.demo.model.dto.UserDto;
import bank.app.demo.model.entities.Transaction;
import bank.app.demo.model.entities.UserSensitiveData;
import bank.app.demo.repository.UserAccountRepository;
import bank.app.demo.repository.UserSensitiveDataRepository;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserSensitiveDataRepository userSensitiveDataRepository;
    private final UserAccountRepository userAccountRepository;

    public UserService(
            UserSensitiveDataRepository userSensitiveDataRepository,
            UserAccountRepository userAccountRepository) {

        this.userSensitiveDataRepository = userSensitiveDataRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public SensitiveDataDto sensitiveData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String username = userDetail.getUsername();

        Optional<UserSensitiveData> data = userSensitiveDataRepository.findByUsername(username);
        
        if (data.isPresent()) {
            UserDto userDto = new UserDto();
            userDto.setFirstname(data.get().getUser().getFirstname());
            userDto.setLastname(data.get().getUser().getLastname());
            userDto.setAccountNumber(data.get().getUser().getUserAccount().getAccountNumber());
            userDto.setAccountBalance(data.get().getUser().getUserAccount().getAccountBalance());
            userDto.setAddress(data.get().getUser().getAddress());
            
            SensitiveDataDto dataDto = new SensitiveDataDto();
            dataDto.setCreditCardNumber(AesService.decrypt(data.get().getCreditCardNumber()));
            dataDto.setIdCardNumber(AesService.decrypt(data.get().getIdCardNumber()));
            dataDto.setUserDto(userDto);
            return dataDto;
        }
        return null;
    }

    public TransactionHistory transactionHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String username = userDetail.getUsername();

        List<Transaction> received = userAccountRepository.findReceivedTransactionsByUsername(username);
        List<Transaction> sent = userAccountRepository.findSentTransactionsByUsername(username);

        List<TransactionDto> receivedDtos = (received != null && !received.isEmpty()) ? received.stream().map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setAmount(transaction.getAmount());
            dto.setDate(transaction.getDate());
            dto.setSenderAccount(transaction.getSenderAccount().getUser().getFirstname()
                    + transaction.getSenderAccount().getUser().getLastname());
            dto.setTitle(transaction.getTitle());
            return dto;
        }).collect(Collectors.toList()) : Collections.emptyList();

        List<TransactionDto> sentDtos =  (sent != null && !sent.isEmpty()) ? sent.stream().map(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setAmount(transaction.getAmount());
            dto.setDate(transaction.getDate());
            dto.setReceiverAccount(maskAccountNumber(transaction.getReceiverAccount().getAccountNumber()));
            dto.setTitle(transaction.getTitle());
            dto.setReceiverName(transaction.getReceiverName());
            dto.setReceiverAddress(transaction.getReceiverAddress());
            return dto;
        }).collect(Collectors.toList()) : Collections.emptyList();

        TransactionHistory his = new TransactionHistory();
        his.setReceivedTransactions(receivedDtos);
        his.setSentTransactions(sentDtos);

        return his;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber != null && accountNumber.length() > 3) {
            String visiblePart = accountNumber.substring(0, 3);
            String maskedPart = accountNumber.substring(3).replaceAll(".", "*");
            return visiblePart + maskedPart;
        }
        return accountNumber;
    }
}