package com.gescom.ecole.entity.utilisateur;

import com.gescom.ecole.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions",
    uniqueConstraints = @UniqueConstraint(columnNames = "code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "designation", nullable = false, length = 150)
    private String designation;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "action", length = 50)
    private String action;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
    
    // Builder manuel
    public static PermissionBuilder builder() {
        return new PermissionBuilder();
    }
    
    public static class PermissionBuilder {
        private Permission permission = new Permission();
        
        public PermissionBuilder code(String code) {
            permission.code = code;
            return this;
        }
        
        public PermissionBuilder designation(String designation) {
            permission.designation = designation;
            return this;
        }
        
        public PermissionBuilder module(String module) {
            permission.module = module;
            return this;
        }
        
        public PermissionBuilder action(String action) {
            permission.action = action;
            return this;
        }
        
        public Permission build() {
            return permission;
        }
    }
}
