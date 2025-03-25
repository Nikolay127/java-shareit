package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAll().stream()
                             .map(userDtoMapper::toUserDto)
                             .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.getById(id)
                                  .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return userDtoMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        throwIfExists(userDto.getEmail(), null);
        User user = userRepository.create(userDtoMapper.fromUserDto(userDto));
        return userDtoMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        UserDto userDtoExisted = getUserById(id);
        if (userDto.getEmail() != null && !userDto.getEmail().equals(userDtoExisted.getEmail())) {
            throwIfExists(userDto.getEmail(), id);
        }
        return userDtoMapper.toUserDto(userRepository.update(id, userDtoMapper.fromUserDto(userDto)));
    }

    @Override
    public UserDto deleteUser(Long id) {
        UserDto userDto = getUserById(id);
        userRepository.delete(id);
        return userDto;
    }

    private void throwIfExists(String email, Long excludedId) {
        if (userRepository.isExistedByEmail(email, excludedId)) {
            throw new ConflictException("User with email = " + email + " already exists");
        }
    }
}
