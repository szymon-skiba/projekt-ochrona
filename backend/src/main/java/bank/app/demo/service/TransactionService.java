package bank.app.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import bank.app.demo.model.dto.TransactionRequest;
import bank.app.demo.model.entities.Transaction;
import bank.app.demo.model.entities.UserAccount;
import bank.app.demo.repository.TransactionRepository;
import bank.app.demo.repository.UserAccountRepository;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserAccountRepository userAccountRepository;
    private final BigDecimal MAX_TRANSFER;

    public TransactionService(TransactionRepository transactionRepository, UserAccountRepository userAccountRepository){
        this.userAccountRepository = userAccountRepository;
        this.transactionRepository = transactionRepository;
        MAX_TRANSFER = new BigDecimal(1000000000);
    }

    @Transactional
    public void make(TransactionRequest request) {
        if(request.getAmount().compareTo(new BigDecimal(0))==-1){
            throw new RuntimeException("Cannot be smaller than 0");
        }
    
        if(request.getAmount().compareTo(MAX_TRANSFER)==1){
            throw new RuntimeException("For bigger data transfers contact your local bank");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String username = userDetail.getUsername();

        if(username == null){
            throw new RuntimeException("Somthing went wrong");  
        }

        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(username);

        if(userAccount.isPresent()){
            if(userAccount.get().getAccountBalance().compareTo(request.getAmount())==-1){
                throw new RuntimeException("Not enough in balance");  
            }

            Optional<UserAccount> reciver = userAccountRepository.findByAccountNumber(request.getReceiverAccount());
            
            if(!reciver.isPresent()){
                throw new RuntimeException("Wrong reciver Account"); 
            }

            Transaction transaction = new Transaction();
            transaction.setDate(LocalDateTime.now());
            transaction.setReceiverAccount(reciver.get());
            transaction.setSenderAccount(userAccount.get());
            transaction.setAmount(request.getAmount());
            transaction.setTitle(request.getTitle());
            transaction.setReceiverName(request.getReceiverName());
            transaction.setReceiverAddress(request.getReceiverAddress());

            BigDecimal userBalance = userAccount.get().getAccountBalance();
            BigDecimal reciverBalance = reciver.get().getAccountBalance();

            userAccount.get().setAccountBalance(userBalance.subtract(request.getAmount()));
            reciver.get().setAccountBalance(reciverBalance.add(request.getAmount()));

            userAccountRepository.save(userAccount.get());
            userAccountRepository.save(reciver.get());
            transactionRepository.save(transaction);

        }else{
            throw new RuntimeException("User not found");  
        }


    }

}
