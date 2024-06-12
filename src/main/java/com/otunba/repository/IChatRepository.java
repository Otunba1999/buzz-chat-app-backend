package com.otunba.repository;

import com.otunba.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IChatRepository extends JpaRepository<Chat, Long> {

//    List<Chat> findByUsersContaining(AppUser user);

    Optional<Chat> findBySenderIdAndRecipientId(String senderId, String receiverId);

    List<Chat> findBySenderId(String id);
//    Chat findByUsersContaining(AppUser receiver, AppUser sender);
}
