package userservice.userservice.mapper;

import org.springframework.stereotype.Component;
import userservice.userservice.dto.UserDto;
import userservice.userservice.entity.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .dateCreation(user.getDateCreation())
                .build();
    }

    public User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .build();
    }
}