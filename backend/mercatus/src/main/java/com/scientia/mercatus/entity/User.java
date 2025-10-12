package com.scientia.mercatus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="users")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable=false)
    private Long userId;

    @Size(max=100)
    @NotNull
    @Column(name = "email", nullable=false, length = 100)
    private String email;

    @Size(max=500)
    @NotNull
    @Column(name = "password_hash", nullable=false, length = 500)
    private String passwordHash;

    @Size(max=100)
    @NotNull
    @Column(name = "name", nullable=false, length=100)
    private String name;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private Set<Role> roles = new LinkedHashSet<>();


}
