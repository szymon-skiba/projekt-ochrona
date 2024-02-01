package bank.app.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bank.app.demo.model.entities.Transaction;
import bank.app.demo.model.entities.UserAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query("SELECT ua FROM UserAccount ua JOIN ua.user u WHERE u.email = :username")
    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByAccountNumber(String accountNumber);

    @Query("SELECT t FROM Transaction t JOIN t.receiverAccount ra JOIN ra.user u WHERE u.email = :username")
    List<Transaction> findReceivedTransactionsByUsername(String username);

    @Query("SELECT t FROM Transaction t JOIN t.senderAccount sa JOIN sa.user u WHERE u.email = :username")
    List<Transaction> findSentTransactionsByUsername(String username);
}