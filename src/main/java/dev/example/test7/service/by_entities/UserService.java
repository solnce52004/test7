package dev.example.test7.service.by_entities;

import dev.example.config.security.enums.PermissionEnum;
import dev.example.config.security.enums.RoleEnum;
import dev.example.test7.entity.Permission;
import dev.example.test7.entity.Role;
import dev.example.test7.entity.User;
import dev.example.test7.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService implements BaseCrudService<User> {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Autowired
    public UserService(
            UserRepository userRepository,
            RoleService roleService,
            PermissionService permissionService
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public void save(User obj) {
        userRepository.save(obj);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllByName(String username) {
        return userRepository.findAllByName(username);
    }

    //todo:!!!!
    public void patch(User obj) {
        userRepository.save(obj);
    }

    @Override
    public User update(User obj) {
        return userRepository.save(obj);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void saveList(List<User> users) {
        users.forEach(this::save);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<String> getAllEmails() {
        return userRepository.getAllEmails();
    }

    @Transactional
    public void createAnonymousRead(User user) {
        Permission permission = permissionService.getPermissionByTitle(PermissionEnum.READ.name());
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        Role role = roleService.getRoleByTitle(RoleEnum.ROLE_ANONYMOUS.name());
        role.setPermissions(permissions);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);
        save(user);
    }

    @Transactional
    public void createUserRead(User user) {
        Permission permission = permissionService.getPermissionByTitle(PermissionEnum.READ.name());
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        Role role = roleService.getRoleByTitle(RoleEnum.ROLE_USER.name());
        role.setPermissions(permissions);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);
        save(user);
    }
}
