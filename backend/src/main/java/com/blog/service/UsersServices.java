package com.blog.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import com.blog.dto.UserFollowRes;
import com.blog.dto.UsersRespons;
import com.blog.dto.FollowUserResponse;
import com.blog.entity.*;
import com.blog.repository.FollowRepository;
import com.blog.repository.NotifRepository;
import com.blog.repository.UserRepository;
import com.blog.exceptions.*;

@Service
public class UsersServices {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final NotifRepository notifRepository;

    UsersServices(
            UserRepository userRepository,
            FollowRepository followRepository,
            NotifRepository notifRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.notifRepository = notifRepository;
    }

    public List<UsersRespons> findAll() {
        List<User> users = userRepository.findAll();
        List<UsersRespons> userDTOs = new ArrayList<>();
        long follower = 0;
        long following = 0;
        for (User user : users) {
            follower = followRepository.countByFollowerId(user.getId());
            following = followRepository.countByFollowingId(user.getId());
            UsersRespons dto = convertToDto(user, following, follower);
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
        cnvDto.setProfileImagePath(user.getProfileImagePath());
        return cnvDto;
        // );
    }

    public UsersRespons getCurrentUser() throws Exception {
        System.err.println("here");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User user = userRepository.findByUserName(username).orElseThrow();
            UsersRespons crrUser = new UsersRespons();
            crrUser.setUuid(user.getUuid());
            crrUser.setFirstName(user.getFirstName());
            crrUser.setLastName(user.getLastName());
            crrUser.setUsername(user.getUserName());
            crrUser.setEmail(user.getEmail());
            crrUser.setRole(user.getRole());
            crrUser.setFollower(followRepository.countByFollowerId(user.getId()));
            crrUser.setFollowing(followRepository.countByFollowingId(user.getId()));
            crrUser.setProfileImagePath(user.getProfileImagePath());

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
            throw new UserNotFoundException("This user not available");
        });

        if (otherUser.getStatus() != null && otherUser.getStatus().equalsIgnoreCase("BAN")) {
            throw new FollowException("This user is banned and cannot be followed");
        }

        if (crrUser.getId() == otherUser.getId()) {
            throw new FollowException("you can't follow yourself");
        }
        long follower = followRepository.countByFollowerId(otherUser.getId());
        long following = followRepository.countByFollowingId(otherUser.getId());

        boolean existe = followRepository.existsByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        if (existe) {
            return new UserFollowRes(following, follower, crrUser.getUuid(),
                    otherUser.getUuid(),
                    "already following");
        }
        Follow follow = new Follow();
        follow.setFollower(crrUser);
        follow.setFollowing(otherUser);
        followRepository.save(follow);
        follower = followRepository.countByFollowerId(otherUser.getId());
        following = followRepository.countByFollowingId(otherUser.getId());
        return new UserFollowRes(following, follower, crrUser.getUuid(),
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

        // Check if user is banned or deleted
        if (otherUser.getStatus() != null && otherUser.getStatus().equalsIgnoreCase("BANNED")) {
            throw new FollowException("This user is banned");
        }
        if (otherUser.getStatus() != null && otherUser.getStatus().equalsIgnoreCase("DELETED")) {
            throw new FollowException("This user has been deleted");
        }

        if (crrUser.getId() == otherUser.getId()) {
            throw new FollowException("you can't follow yourself");
        }

        notifRepository.deleteByNotificatedUserAndNotificationOwner(crrUser.getUuid(), otherUser.getUuid());
        boolean existe = followRepository.existsByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        long follower = followRepository.countByFollowerId(otherUser.getId());
        long following = followRepository.countByFollowingId(otherUser.getId());

        if (!existe) {
            return new UserFollowRes(following, follower, crrUser.getUuid(),
                    otherUser.getUuid(),
                    "already unfollowed");
        }
        followRepository.deleteByFollowerIdAndFollowingId(crrUser.getId(), otherUser.getId());
        follower = followRepository.countByFollowerId(otherUser.getId());
        following = followRepository.countByFollowingId(otherUser.getId());
        return new UserFollowRes(following, follower, crrUser.getUuid(),
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
            return new UserFollowRes(following, follower, crrUser.getUuid(),
                    otherUser.getUuid(), "no follow");
        }
        return new UserFollowRes(following, follower, crrUser.getUuid(),
                otherUser.getUuid(), "follow");
    }

    public List<UsersRespons> getUser(String username) {
        if (username.isEmpty()) {
            return new ArrayList<UsersRespons>();
        }
        UsersRespons usersRespons;
        try {
            usersRespons = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("u are not login or registered to search");
        }
        List<User> users = userRepository.findByUuidNotAndUserNameStartingWith(usersRespons.getUuid(), username);
        List<UsersRespons> rs = convertToRes(users);
        return rs;
    }

    private List<UsersRespons> convertToRes(List<User> users) {
        List<UsersRespons> userList = new ArrayList<>();
        for (User user : users) {
            UsersRespons userRes = new UsersRespons();
            userRes.setUsername(user.getUserName());
            userRes.setUuid(user.getUuid());
            userRes.setFirstName(user.getFirstName());
            userRes.setLastName(user.getLastName());
            userRes.setProfileImagePath(user.getProfileImagePath());
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
        UsersRespons profile = new UsersRespons();
        profile.setEmail(user.getEmail());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setUsername(user.getUserName());
        profile.setUuid(user.getUuid());
        profile.setFollower(following);
        profile.setFollowing(follower);
        profile.setConnect(connecting);
        profile.setProfileImagePath(user.getProfileImagePath());
        profile.setStatus(user.getStatus());
        return profile;
    }

    public List<FollowUserResponse> getMyFollowers() {
        UsersRespons currentUser;
        try {
            currentUser = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("You are not logged in");
        }

        User user = userRepository.findByUuid(currentUser.getUuid()).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });
        List<User> followers = followRepository.findFollowersByUserId(user.getId());
        return convertToFollowUserDTOList(followers);
    }

    public List<FollowUserResponse> getMyFollowing() {
        UsersRespons currentUser;
        try {
            currentUser = getCurrentUser();
        } catch (Exception e) {
            throw new UserNotLoginException("You are not logged in");
        }

        User user = userRepository.findByUuid(currentUser.getUuid()).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });
        List<User> following = followRepository.findFollowingByUserId(user.getId());
        return convertToFollowUserDTOList(following);
    }

    private List<FollowUserResponse> convertToFollowUserDTOList(List<User> users) {
        List<FollowUserResponse> followUserDTOs = new ArrayList<>();
        for (User user : users) {
            FollowUserResponse dto = new FollowUserResponse();
            dto.setUuid(user.getUuid());
            dto.setUsername(user.getUserName());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setProfileImagePath(user.getProfileImagePath());
            followUserDTOs.add(dto);
        }
        return followUserDTOs;
    }
}
