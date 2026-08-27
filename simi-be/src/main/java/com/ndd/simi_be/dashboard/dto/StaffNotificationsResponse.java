package com.ndd.simi_be.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StaffNotificationsResponse {
    private long unreadCount;
    private List<StaffNotificationResponse> notifications;
}