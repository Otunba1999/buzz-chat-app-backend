package com.otunba.services;

import com.otunba.aws.Bucket;
import com.otunba.aws.FileStore;
import com.otunba.dtos.AppUserDto;
import com.otunba.dtos.AuthRequest;
import com.otunba.dtos.AuthResponse;
import com.otunba.dtos.ChatDto;
import com.otunba.enums.Status;
import com.otunba.exceptions.ApiException;
import com.otunba.models.AppUser;
import com.otunba.repository.IAppUserRepository;
import com.otunba.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.*;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {


    private static final Logger log = LoggerFactory.getLogger(AppUserService.class);
    private final IAppUserRepository repository;
    private final ChatService chatService;
    private final FileStore fileStore;
    private final ModelMapper mapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AppUserDto login(AuthRequest user) {
        var savedUser = repository.findByUsername(user.getUsername()).orElseThrow();
        savedUser.setStatus(Status.ONLINE);
        return mapper.mapToUserDto(repository.save(savedUser));
    }

    public AppUserDto disconnect(AuthRequest request) {
        var storedUser = repository.findByUsername(request.getUsername()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            return mapper.mapToUserDto(repository.save(storedUser));
        }
        return null;
    }

    public AuthResponse updateUsername(AuthRequest request){
        AppUser appUser =repository.findByUsername(getAuthenticatedUsername()).orElse(null);
        if(appUser == null) throw new ApiException("user not found");
        appUser.setUsername(request.getUsername());
        var updatedUser = mapper.mapToUserDto(repository.save(appUser));
        return AuthResponse.builder()
                .user(updatedUser)
                .token(null)
                .build();
    }

    public String setProfilePic(MultipartFile file){
        if(file.isEmpty())
            throw new ApiException("Cannot upload empty file");
        AppUser appUser = repository.findByUsername(getAuthenticatedUsername()).orElse(null);
        if(appUser == null) throw new ApiException("User does not exist");
        if(!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType()))
            throw new ApiException("Image must be of type PNG, JPEG, JPG");
        Map<String, String> metadata = Map.of(
                "Content-type", Objects.requireNonNull(file.getContentType()),
                "Content-Length", String.valueOf(file.getSize()));
        String path = String.format("%s/%s", Bucket.BUZZ_IMAGE.getBucketName(), appUser.getId());
        String filename  = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            appUser.setImageUrl(filename);
            repository.save(appUser);
            return filename;
        }catch (IOException e){
            throw new ApiException("Unable to uplaod profile pic");
        }
    }
    public byte[] downloadImage(String filename, String userId){
        String path = String.format("%s/%s", Bucket.BUZZ_IMAGE.getBucketName(), userId);
        return fileStore.download(path, filename);

    }

    public List<AppUserDto> findConnectedUsers() {
        List<AppUser> allByStatus = repository.findAllByStatus(Status.ONLINE);
        return allByStatus.stream().map(mapper::mapToUserDto)
                .filter(user -> !user.getUsername().equals(getAuthenticatedUsername()))
                .toList();
    }

    public List<ChatDto> findByUsername(String username) {
        var authenticatedUser = repository.findByUsername(getAuthenticatedUsername());
        log.info(getAuthenticatedUsername());
        Set<ChatDto> userChats = chatService.getUsersChat().stream()
                .filter(chat -> chat.getUser().getUsername().contains(username))
                .collect(Collectors.toSet());
        if(authenticatedUser.isEmpty()) throw new ApiException("Invalid user");
        List<AppUser> users = repository.findAll().stream()
                .filter(user -> user.getUsername().contains(username) &&
                        !user.getUsername().equals(authenticatedUser.get().getUsername()))
                .toList();

        Set<ChatDto> chatDtos = new HashSet<>();
        for (var user : users) {
            if(isChatExist(userChats, mapper.mapToUserDto(user))) continue;
            ChatDto chatDto = ChatDto.builder()
                    .lastMessage(null)
                    .user(mapper.mapToUserDto(user))
                    .chatType(null)
                    .build();

            chatDtos.add(chatDto);
        }
        userChats.addAll(chatDtos);
        return userChats.stream().toList();
    }

    private boolean isChatExist(Set<ChatDto> chats, AppUserDto user){
        for(var chat : chats){
            if(chat.getUser().equals(user))
                return true;

        }
        return false;
    }

    public static String getAuthenticatedUsername(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();

    }


}
