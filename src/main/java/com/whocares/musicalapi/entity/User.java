package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(unique = true, nullable = false, name = "username")
    private String username;
    
    @Column(nullable = false, name = "password")
    private String password;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "user_image")
    private String userImage;
}
