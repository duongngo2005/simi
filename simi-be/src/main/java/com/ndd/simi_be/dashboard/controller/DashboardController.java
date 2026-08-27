package com.ndd.simi_be.dashboard.controller;

import com.ndd.simi_be.common.response.ApiResponse;
import com.ndd.simi_be.dashboard.dto.StaffDashboardResponse;
import com.ndd.simi_be.dashboard.dto.StaffNotificationsResponse;
import com.ndd.simi_be.dashboard.service.DashboardService;
import com.ndd.simi_be.dashboard.service.StaffNotificationService;
import com.ndd.simi_be.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class DashboardController {
    private final DashboardService dashboardService;
    private final StaffNotificationService staffNotificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<StaffDashboardResponse>> getDashboard(
            @RequestParam(defaultValue = "7d") String range
    ) {
        return ResponseEntity.ok(
                ApiResponse.<StaffDashboardResponse>builder()
                        .status(200)
                        .body(dashboardService.getDashboard(range))
                        .build()
        );
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<StaffNotificationsResponse>> getNotifications(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                ApiResponse.<StaffNotificationsResponse>builder()
                        .status(200)
                        .body(staffNotificationService.getNotifications(user))
                        .build()
        );
    }

    @PatchMapping("/notifications/read")
    public ResponseEntity<Void> markNotificationsAsRead(
            @AuthenticationPrincipal User user
    ) {
        staffNotificationService.markAllAsRead(user);
        return ResponseEntity.noContent().build();
    }
}