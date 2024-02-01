package bank.app.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import bank.app.demo.model.entities.UserSensitiveData;

@Repository
public interface UserSensitiveDataRepository extends JpaRepository<UserSensitiveData, Long> {
    @Query("SELECT usd FROM UserSensitiveData usd JOIN usd.user u WHERE u.email = :username")
    Optional<UserSensitiveData> findByUsername(String username);
}