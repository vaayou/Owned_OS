package com.owned.user_service.service;

import com.owned.user_service.dto.UpdateProfileRequest;
import com.owned.user_service.dto.UserProfileResponse;
import com.owned.user_service.entity.User;
import com.owned.user_service.repository.FollowRepository;
import com.owned.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Public profile retrieval and profile mutation for the authenticated user.
 *
 * <p>The {@code is_public} gate is enforced in {@link #getPublicProfile}:
 * private profiles return 403 to anyone who is not a follower.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getPublicProfile(Long targetId, Long requesterId) {
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // Owner can always see their own profile
        if (!target.isPublic() && !targetId.equals(requesterId)) {
            boolean isFollower = followRepository
                    .existsByFollowerIdAndFollowingId(requesterId, targetId);
            if (!isFollower) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "This profile is private");
            }
        }

        return UserProfileResponse.from(target);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (req.getAvatarUrl() != null) {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getIsPublic() != null) {
            user.setPublic(req.getIsPublic());
        }

        return UserProfileResponse.from(userRepository.save(user));
    }
}
