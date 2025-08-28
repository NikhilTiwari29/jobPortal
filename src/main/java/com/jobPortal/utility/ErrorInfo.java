package com.jobPortal.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo {
    private List<String> errorMessages;
    private Integer errorCode;
    private LocalDateTime timeStamp;
}
