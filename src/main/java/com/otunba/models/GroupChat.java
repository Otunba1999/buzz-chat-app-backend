package com.otunba.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@SuperBuilder
@Entity
@Data
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    private AppUser admin;
    @ManyToMany
    private Set<AppUser> members;
    @OneToMany
    private List<Message> messages;
    private String profilePic;
}
