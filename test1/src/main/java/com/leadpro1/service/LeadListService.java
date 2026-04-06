package com.leadpro1.service;

import com.leadpro1.entity.*;
import com.leadpro1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadListService {

    private final LeadListRepository leadListRepository;
    private final UserLeadListRepository userLeadListRepository;

    public LeadList createLeadList(String name, UUID userId) {

        LeadList list = LeadList.builder()
                .name(name)
                .createdBy(User.builder().id(userId).build())
                .build();

        leadListRepository.save(list);

        UserLeadList mapping = UserLeadList.builder()
                .userId(userId)
                .leadListId(list.getId())
                .role(Role.OWNER)
                .build();

        userLeadListRepository.save(mapping);

        return list;
    }

    public void validateAccess(UUID userId, UUID leadListId) {

        boolean hasAccess = userLeadListRepository
                .existsByUserIdAndLeadListId(userId, leadListId);

        if (!hasAccess) {
            throw new AccessDeniedException("Access Denied");
        }
    }

    public Role getUserRole(UUID userId, UUID leadListId) {

        UserLeadList mapping = userLeadListRepository
                .findByUserIdAndLeadListId(userId, leadListId)
                .orElseThrow(() -> new AccessDeniedException("Access Denied"));

        return mapping.getRole();
    }

    public LeadList getLeadList(UUID leadListId, UUID userId) {

        validateAccess(userId, leadListId);

        checkPermission(userId, leadListId,Permission.VIEW); // ✅ added

        return leadListRepository.findById(leadListId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "LeadList not found")
                );
    }
    public void deleteLeadList(UUID leadListId, UUID userId) {

        validateAccess(userId, leadListId);

        checkPermission(userId, leadListId, Permission.DELETE);

        leadListRepository.deleteById(leadListId);
    }
    public Map<String, List<LeadList>> getAllLeadLists(UUID userId) {
        List<UserLeadList> mappings = userLeadListRepository.findByUserId(userId);

        List<LeadList> owned = mappings.stream()
                .filter(m -> m.getRole() == Role.OWNER)
                .map(m -> leadListRepository.findById(m.getLeadListId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<LeadList> shared = mappings.stream()
                .filter(m -> m.getRole() != Role.OWNER)
                .map(m -> leadListRepository.findById(m.getLeadListId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Map.of("owned", owned, "shared", shared);
    }
    public LeadList updateLeadList(UUID leadListId, UUID userId, String newName) {

        validateAccess(userId, leadListId);

        checkPermission(userId, leadListId, Permission.EDIT);

        LeadList list = leadListRepository.findById(leadListId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        list.setName(newName);

        return leadListRepository.save(list);
    }
    // 🔥 CORRECT METHOD
    public void checkPermission(UUID userId, UUID leadListId,Permission permission) {

        Role userRole = getUserRole(userId, leadListId);

        switch (permission) {

            case VIEW:
                return;

            case CREATE:
            case EDIT:
                if (userRole == Role.VIEWER) {
                    throw new AccessDeniedException("Insufficient permissions");
                }
                return;

            case DELETE:
            case INVITE:
                if (userRole != Role.OWNER) {
                    throw new AccessDeniedException("Only owner allowed");
                }
                return;
        }
    }
}