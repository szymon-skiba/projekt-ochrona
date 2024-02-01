package bank.app.demo.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.micrometer.common.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String firstname;
    private String lastname;
    private String address; 
    private String email;
    private String password;
    @Nullable
    private LocalDateTime lastLogin;
    @Builder.Default
    private int failLoginCount = 0;
    @Builder.Default
    private int loginCount = 0;
    @Builder.Default
    private Boolean banned = false;
    @Nullable
    private int requestedLetters;
    @Nullable
    private String passwordResetToken;
    @Nullable
    private LocalDateTime passwordResetTokenExpiration;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "userm", fetch = FetchType.EAGER)
    private List<UserMaskedPassword> userMaskedPasswords;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
