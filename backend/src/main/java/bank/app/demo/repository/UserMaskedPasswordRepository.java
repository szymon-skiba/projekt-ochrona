package bank.app.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bank.app.demo.model.entities.UserMaskedPassword;
import jakarta.transaction.Transactional;

@Repository
public interface UserMaskedPasswordRepository extends JpaRepository<UserMaskedPassword, Long> {

    List<UserMaskedPassword> findByUserm_Email(String email);

    void deleteByUserm_Email(String email);

    List<UserMaskedPassword> findByUserm_EmailAndLetters(String email, int letters);
}