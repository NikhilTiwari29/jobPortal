package com.jobPortal.dto;

import com.jobPortal.enums.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;

    @NotNull(message = "{notification.userId.required}")
    private Long userId;

    @NotBlank(message = "{notification.message.required}")
    @Size(max = 500, message = "{notification.message.size}")
    private String message;

    @Size(max = 255, message = "{notification.action.size}")
    private String action;

    private NotificationStatus status;

    private LocalDateTime timestamp;
}
