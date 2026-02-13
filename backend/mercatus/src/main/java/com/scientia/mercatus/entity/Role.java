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
@Table(name="roles")
public class Role extends BaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50, unique = true)
    private RoleName name;



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Role)) return false;
        Role role = (Role) obj;
        return id != null && id.equals(role.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
