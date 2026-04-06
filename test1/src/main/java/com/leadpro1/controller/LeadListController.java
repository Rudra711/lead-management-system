package com.leadpro1.controller;

import com.leadpro1.dto.CreateLeadListRequest;
import com.leadpro1.entity.LeadList;
import com.leadpro1.entity.Role;
import com.leadpro1.service.LeadListService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/leadlists")
@RequiredArgsConstructor
public class LeadListController {

    private final LeadListService leadListService;

    @PostMapping
    public LeadList create(@RequestBody CreateLeadListRequest request,
                           HttpServletRequest httpRequest) {

        UUID userId = (UUID) httpRequest.getAttribute("userId");

        return leadListService.createLeadList(request.getName(), userId);
    }

    @GetMapping("/{id}")
    public LeadList get(@PathVariable UUID id,
                        HttpServletRequest request) {

        UUID userId = (UUID) request.getAttribute("userId");

        return leadListService.getLeadList(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id, HttpServletRequest request) {

        UUID userId = (UUID) request.getAttribute("userId");

        leadListService.deleteLeadList(id, userId);
    }

    @PutMapping("/{id}")
    public LeadList update(@PathVariable UUID id,
                           @RequestBody Map<String, String> body,
                           HttpServletRequest request) {

        UUID userId = (UUID) request.getAttribute("userId");

        return leadListService.updateLeadList(id, userId, body.get("name"));
    }
    @GetMapping
    public Map<String, List<LeadList>> getAll(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return leadListService.getAllLeadLists(userId);
    }
    @GetMapping("/{id}/role")
    public Map<String, String> getMyRole(@PathVariable UUID id, HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");

        System.out.println("USER ID FROM REQUEST: " + userId); // 👈 ADD THIS

        Role role = leadListService.getUserRole(userId, id);

        System.out.println("ROLE FOUND: " + role); // 👈 ADD THIS

        return Map.of("role", role.name());
    }
}