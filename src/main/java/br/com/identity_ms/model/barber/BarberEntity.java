package br.com.identity_ms.model.barber;

import br.com.identity_ms.model.user.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "barbers")
public class BarberEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String specialty;
    private BigDecimal rating;
    private String avatar;


    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public UserEntity getUser() { return user; }

    public void setUser(UserEntity user) { this.user = user; }

    public String getSpecialty() { return specialty; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public java.math.BigDecimal getRating() { return rating; }

    public void setRating(java.math.BigDecimal rating) { this.rating = rating; }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }
}