package com.otunba.dtos;

import com.otunba.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto {
    private String id;
    private String username;
    private String imageUrl;
    private Status status;
}
