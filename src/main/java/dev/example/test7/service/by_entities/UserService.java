package dev.example.test7.service.by_entities;

import dev.example.test7.entity.User;
import dev.example.test7.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements BaseCrudService<User> {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public List<User> findAllByName(String name) {
        return userRepository.findAllByName(name);
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

    public Optional<User> findByEmail(String email){
       return userRepository.findByEmail(email);
    }
}
