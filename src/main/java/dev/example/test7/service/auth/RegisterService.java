package dev.example.test7.service.auth;

import dev.example.config.security.enums.PermissionEnum;
import dev.example.config.security.enums.RoleEnum;
import dev.example.test7.entity.Permission;
import dev.example.test7.entity.Role;
import dev.example.test7.entity.User;
import dev.example.test7.service.by_entities.PermissionService;
import dev.example.test7.service.by_entities.RoleService;
import dev.example.test7.service.by_entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class RegisterService {
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Autowired
    public RegisterService(
            UserService userService,
            RoleService roleService,
            PermissionService permissionService
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Transactional
    public void createUserWithRolePermission(User user) {
        Permission permission = permissionService.getPermissionByTitle(PermissionEnum.READ.name());
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        Role role = roleService.getRoleByTitle(RoleEnum.ROLE_ANONYMOUS.name());
        role.setPermissions(permissions);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);
        userService.save(user);
    }
}
