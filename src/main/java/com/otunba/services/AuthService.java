package com.otunba.services;

import com.otunba.dtos.AppUserDto;
import com.otunba.dtos.AuthRequest;
import com.otunba.dtos.AuthResponse;
import com.otunba.enums.Status;
import com.otunba.exceptions.ApiException;
import com.otunba.models.AppUser;
import com.otunba.repository.IAppUserRepository;
import com.otunba.utils.ModelMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.otunba.enums.Status.ONLINE;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final IAppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService tokenService;
    private final ModelMapper mapper;

    public AppUserDto registerUser(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new ApiException("user already exist with the username " + request.getUsername());
        AppUser appUser = AppUser.builder()
                .username((request.getUsername()))
                .password(passwordEncoder.encode(request.getPassword()))
                .status(Status.OFFLINE)
                .build();
        return mapper.mapToUserDto(userRepository.save(appUser));
    }

    public AuthResponse loginUser(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String token = tokenService.generateToken(authentication);
            var user1 = userRepository.findByUsername(request.getUsername());
            if(user1.isEmpty()) return  null;
            var loginUser = user1.get();
            loginUser.setStatus(ONLINE);
            var user = mapper.mapToUserDto(userRepository.save(loginUser));
            return AuthResponse.builder()
                    .user(user)
                    .token(token)
                    .build();
        } catch (UsernameNotFoundException exception) {
            return AuthResponse.builder().user(null).token("").build();
        }
    }
}
