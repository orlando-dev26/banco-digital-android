package com.corporation_dev.user_microservice.util;
import org.springframework.beans.BeanUtils;
import com.corporation_dev.user_microservice.dto.UserDto;
import com.corporation_dev.user_microservice.entity.User;
import java.time.LocalDateTime;

public class EntityDtoUtil {
    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        if (user != null) {
            BeanUtils.copyProperties(user, userDto);
        }
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        User user = new User();
        if (userDto != null) {
            BeanUtils.copyProperties(userDto, user);
        }

        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(LocalDateTime.now());
        }

        return user;
    }
}
