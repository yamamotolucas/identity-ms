package br.com.identity_ms.model.user;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false, nullable = false)
    private LocalDateTime creationDate;

    public UserEntity() {}

    public UserEntity(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public UserRole getRole() {return role;}

    public void setRole(UserRole role) {this.role = role;}

    public boolean isActive() {return isActive;}

    public void setActive(boolean active) {isActive = active;}

    public LocalDateTime getCreationDate() {return creationDate;}

    public void setCreationDate(LocalDateTime creationDate) {this.creationDate = creationDate;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));}

    @Override
    public boolean isEnabled() {return this.isActive;}

    @Override
    public String getUsername() {return this.email;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isAccountNonExpired() {return true;}

}