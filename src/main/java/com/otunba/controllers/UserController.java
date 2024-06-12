package com.otunba.controllers;

import com.otunba.dtos.*;
import com.otunba.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/api/buzz/auth")
@RequiredArgsConstructor
public class UserController {

    private final AppUserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<AppUserDto>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<List<ChatDto>> findUsersByNickname(@PathVariable("username") String username) {
//        log.info("username: {}", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }
    @PutMapping("/user/update")
    public ResponseEntity<AuthResponse> updateUsername(@RequestBody AuthRequest request){
        var user = userService.updateUsername(request);
        return  ResponseEntity.ok(user);
    }

    @PostMapping(path = "/user/upload-profile-pic", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageResponse> uploadProfilePic(@RequestParam("file") MultipartFile file) {
        var imageUrl = new ImageResponse(userService.setProfilePic(file));
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping(path = "/download/{userId}/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, IMAGE_GIF_VALUE})
    public byte[] download(@PathVariable("userId") String userId, @PathVariable("filename") String filename) {
        return userService.downloadImage(filename, userId);
    }
}
