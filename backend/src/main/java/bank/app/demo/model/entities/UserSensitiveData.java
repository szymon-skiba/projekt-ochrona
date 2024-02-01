package bank.app.demo.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_sensitive_data")
public class UserSensitiveData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String creditCardNumber;
    private String idCardNumber;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}