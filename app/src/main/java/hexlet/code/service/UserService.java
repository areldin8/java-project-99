package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserDTO create(UserCreateDTO userCreateDTO) {
        var user = userMapper.map(userCreateDTO);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public List<UserDTO> getAll() {
        var users = userRepository.findAll();

        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User With Id: " + id + " Not Found"));

        return userMapper.map(user);
    }

    public UserDTO update(UserUpdateDTO userUpdateDTO, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User With Id: " + id + " Not Found"));

        userMapper.update(userUpdateDTO, user);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public void delete(Long id) throws Exception {
        userRepository.deleteById(id);
    }
}
