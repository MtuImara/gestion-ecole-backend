package com.gescom.ecole.entity.utilisateur;

import com.gescom.ecole.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",
    uniqueConstraints = @UniqueConstraint(columnNames = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<Utilisateur> utilisateurs = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public void addPermission(Permission permission) {
        permissions.add(permission);
        permission.getRoles().add(this);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
        permission.getRoles().remove(this);
    }
    
    // Builder manuel
    public static RoleBuilder builder() {
        return new RoleBuilder();
    }
    
    public static class RoleBuilder {
        private Role role = new Role();
        
        public RoleBuilder code(String code) {
            role.code = code;
            return this;
        }
        
        public RoleBuilder designation(String designation) {
            role.designation = designation;
            return this;
        }
        
        public RoleBuilder description(String description) {
            role.description = description;
            return this;
        }
        
        public RoleBuilder permissions(Set<Permission> permissions) {
            role.permissions = permissions;
            return this;
        }
        
        public Role build() {
            return role;
        }
    }
}
