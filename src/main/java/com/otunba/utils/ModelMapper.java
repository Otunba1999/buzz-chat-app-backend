package com.otunba.utils;

import com.otunba.dtos.AppUserDto;
import com.otunba.models.AppUser;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public AppUserDto mapToUserDto(AppUser user){
        return AppUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .status(user.getStatus())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
