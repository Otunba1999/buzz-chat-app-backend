package com.otunba.repository;

import com.otunba.models.AppUser;
import com.otunba.models.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepository  extends JpaRepository<GroupChat, Long> {
    List<GroupChat> findByMembersContaining(AppUser appUser);
}
