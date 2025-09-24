package com.blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.blog.dto.UserFollowRes;
import com.blog.dto.UsersRespons;
import com.blog.entity.*;
import com.blog.repository.FollowRepository;
import com.blog.repository.UserRepository;
import com.blog.exceptions.*;

@Service
public class UsersServices {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;

    public UsersServices(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    public List<UsersRespons> findAll() {
        List<User> users = userRepository.findAll();
        List<UsersRespons> userDTOs = new ArrayList<>();
        for (User user : users) {
            UsersRespons dto = convertToDto(user);
            userDTOs.add(dto);
        }
        return userDTOs;
    }

    private UsersRespons convertToDto(User user) {
        return new UsersRespons(user.getUuid(), user.getFirstName(), user.getLastName(), user.getUserName(),
                user.getEmail(), user.getRole());
    }

    public UsersRespons getCurrentUser() throws Exception {
        System.err.println("here");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication = " + authentication);
        System.out.println("Principal = " + authentication.getPrincipal());
        System.out.println("Authorities = " + authentication.getAuthorities());

        if (authentication != null && authentication.getPrincipal() instanceof String) {
            String username = (String) authentication.getPrincipal();
            // System.out.printf("username in getcrr: \n", username);
            User user = userRepository.findByUserName(username).orElseThrow();
            return new UsersRespons(user.getUuid(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getRole());
        }
        throw new Exception("User not authenticated");
    }

    public UserFollowRes follow(String uuid) {
        UsersRespons user;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
        User crrUser = userRepository.findByUserName(user.getUsername()).orElseThrow(() -> {
            throw new UserNotFoundException("need to login");
        });
        User otherUser = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("invalid user");
        });
        long follower = followRepository.countByFollowerId(crrUser.getId());
        long following = followRepository.countByFollowingId(crrUser.getId());

        boolean existe = followRepository.existsByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        if (existe) {
            return new UserFollowRes(follower, following, crrUser.getUuid(),
                    otherUser.getUuid(),
                    "already following");
        }
        Follow follow = new Follow();
        follow.setFollower(crrUser);
        follow.setFollowing(otherUser);
        followRepository.save(follow);
        follower = followRepository.countByFollowerId(crrUser.getId());
        following = followRepository.countByFollowingId(crrUser.getId());
        return new UserFollowRes(follower, following, crrUser.getUuid(),
                otherUser.getUuid(),
                "user has succseffully followed");
    }

    public UserFollowRes unfollow(String uuid) {
        UsersRespons user;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
        User crrUser = userRepository.findByUserName(user.getUsername()).orElseThrow(() -> {
            throw new UserNotFoundException("need to login");
        });
        User otherUser = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("invalid user");
        });
        boolean existe = followRepository.existsByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        long follower = followRepository.countByFollowerId(crrUser.getId());
        long following = followRepository.countByFollowingId(crrUser.getId());

        if (!existe) {
            return new UserFollowRes(follower, following, crrUser.getUuid(),
                    otherUser.getUuid(),
                    "already unfollowed");
        }
        System.out.println("hii follow");
        followRepository.deleteByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        follower = followRepository.countByFollowerId(crrUser.getId());
        following = followRepository.countByFollowingId(crrUser.getId());
        return new UserFollowRes(follower, following, crrUser.getUuid(),
                otherUser.getUuid(),
                "user has succseffully unfollowed");
    }

    public UserFollowRes isFollowing(String uuid) {
        UsersRespons user;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
        User crrUser = userRepository.findByUserName(user.getUsername()).orElseThrow(() -> {
            throw new UserNotFoundException("need to login");
        });
        User otherUser = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("invalid user");
        });
        long follower = followRepository.countByFollowerId(crrUser.getId());
        long following = followRepository.countByFollowingId(crrUser.getId());
        boolean existe = followRepository.existsByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        if (!existe) {
            return new UserFollowRes(follower, following, crrUser.getUuid(),
                    otherUser.getUuid(), "no follow");
        }
        return new UserFollowRes(follower, following, crrUser.getUuid(),
                otherUser.getUuid(), "follow");
    }
}
