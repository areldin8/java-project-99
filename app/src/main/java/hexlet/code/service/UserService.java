package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::map)
                .toList();
    }

    public UserDTO findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found"));
        return mapper.map(user);
    }

    public UserDTO create(UserCreateDTO userData) {
        User user = mapper.map(userData);
        user.setRole("USER");
        userRepository.save(user);
        return mapper.map(user);
    }

    public UserDTO update(long id, UserUpdateDTO userData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found"));
        mapper.update(userData, user);
        userRepository.save(user);
        return mapper.map(user);
    }

    public void delete(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found"));
        userRepository.delete(user);
    }
}
