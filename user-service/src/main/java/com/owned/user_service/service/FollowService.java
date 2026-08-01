package com.owned.user_service.service;

import com.owned.user_service.dto.UserProfileResponse;
import com.owned.user_service.entity.Follow;
import com.owned.user_service.entity.User;
import com.owned.user_service.repository.FollowRepository;
import com.owned.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Social graph operations — follow / unfollow / list followers.
 */
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
        }
        // Ensure target user exists
        userRepository.findById(followingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already following this user");
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow relationship not found");
        }
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getFollowers(Long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return followRepository.findAllByFollowingId(userId).stream()
                .map(f -> userRepository.findById(f.getFollowerId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> UserProfileResponse.from(opt.get()))
                .toList();
    }
}
