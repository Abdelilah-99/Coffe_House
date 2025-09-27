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
        long follower = 0;
        long following = 0;
        for (User user : users) {
            follower = followRepository.countByFollowerId(user.getId());
            following = followRepository.countByFollowingId(user.getId());
            UsersRespons dto = convertToDto(user, follower, following);
            userDTOs.add(dto);
        }
        return userDTOs;
    }

    private UsersRespons convertToDto(User user, long follower, long following) {
        UsersRespons cnvDto = new UsersRespons();
        cnvDto.setUuid(user.getUuid());
        cnvDto.setFirstName(user.getFirstName());
        cnvDto.setLastName(user.getLastName());
        cnvDto.setUsername(user.getUserName());
        cnvDto.setEmail(user.getEmail());
        cnvDto.setRole(user.getRole());
        cnvDto.setFollower(follower);
        cnvDto.setFollowing(following);
        return cnvDto;
        // );
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
            long follower = followRepository.countByFollowerId(user.getId());
            long following = followRepository.countByFollowingId(user.getId());
            UsersRespons crrUser = new UsersRespons();
            crrUser.setUuid(user.getUuid());
            crrUser.setFirstName(user.getFirstName());
            crrUser.setLastName(user.getLastName());
            crrUser.setUsername(user.getUserName());
            crrUser.setEmail(user.getEmail());
            crrUser.setRole(user.getRole());
            crrUser.setFollower(follower);
            crrUser.setFollowing(following);
            return crrUser;
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
        if (crrUser.getId() == otherUser.getId()) {
            throw new FollowException("you can't follow yourself");
        }
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
        if (crrUser.getId() == otherUser.getId()) {
            throw new FollowException("you can't follow yourself");
        }
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

    public List<UsersRespons> getUser(String username) {
        if (username.isEmpty()) {
            return new ArrayList<UsersRespons>();
        }
        List<User> users = userRepository.findByUserNameStartingWithIgnoreCase(username);
        List<UsersRespons> rs = convertToRes(users);
        return rs;
    }

    private List<UsersRespons> convertToRes(List<User> users) {
        List<UsersRespons> userList = new ArrayList<>();
        for (User user : users) {
            UsersRespons userRes = new UsersRespons();
            userRes.setUsername(user.getUserName());
            userRes.setUuid(user.getUuid());
            userList.add(userRes);
        }
        return userList;
    }

    public UsersRespons getProfile(String uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("user no exists");
        });
        UsersRespons crrUser;
        try {
            crrUser = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("u are not logged in");
        }
        User crrUserData = userRepository.findByUuid(crrUser.getUuid()).orElseThrow(() -> {
            throw new UserNotFoundException("user not found");
        });
        long follower = followRepository.countByFollowerId(user.getId());
        long following = followRepository.countByFollowingId(user.getId());
        boolean connecting = followRepository.existsByFollowerIdAndFollowingId(crrUserData.getId(), user.getId());
        System.out.println("user " + user.getId() + " crrUser " + crrUserData.getId());
        UsersRespons profile = new UsersRespons();
        profile.setEmail(user.getEmail());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setUsername(user.getUserName());
        profile.setUuid(user.getUuid());
        profile.setFollower(following);
        profile.setFollowing(follower);
        profile.setConnect(connecting);
        return profile;
    }
}
