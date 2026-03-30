package com.scientia.mercatus.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
    @Column(name = "email", nullable=false, length = 100, unique = true)
    private String email;

    @Size(max=500)
    @NotNull
    @JsonIgnore
    @Column(name = "password_hash", nullable=false, length = 500)
    private String passwordHash;

    @Size(max=100)
    @NotNull
    @Column(name = "user_name", nullable=false, length=100)
    private String userName;

    @Size(max=36)
    @Column(name = "opaque_identifier", nullable=false, length=36, unique=true)
    private String opaqueIdentifier;

    @Column(name = "is_active", nullable=false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private Set<Role> roles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user",  fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}
