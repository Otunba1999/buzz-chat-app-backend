package com.otunba.repository;

import com.otunba.enums.Status;
import com.otunba.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAllByStatus(Status status);
}
