package dev.example.test7.service.by_entities;

import dev.example.test7.entity.Permission;
import dev.example.test7.repo.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission getPermissionByTitle(String title){
        return permissionRepository.getPermissionByTitle(title).orElse(null);
    }

    public void save(Permission permission) {
        permissionRepository.save(permission);
    }
}
