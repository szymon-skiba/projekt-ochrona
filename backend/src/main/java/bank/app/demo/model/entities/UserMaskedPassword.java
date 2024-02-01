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
public class UserMaskedPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String maskedPassword;
    private int letters;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id_masked")
    private User userm;

}
