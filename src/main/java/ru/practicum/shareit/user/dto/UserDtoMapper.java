package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDto toUserDto(User user);

    User fromUserDto(UserDto userDto);
}
