package dev.example.test7.service.by_entities;

import dev.example.test7.entity.Role;
import dev.example.test7.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements BaseCrudService<Role> {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByTitle(String title){
        return roleRepository.getRoleByTitle(title).orElse(null);
    }

    @Override
    public Role save(Role obj) {
       return roleRepository.save(obj);
    }

    @Override
    public Role findById(Long id) {
        return null;
    }

    @Override
    public List<Role> findAll() {
        return null;
    }

    @Override
    public Role update(Role obj) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
