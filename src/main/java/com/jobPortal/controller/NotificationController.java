package com.jobPortal.controller;

import com.jobPortal.dto.NotificationDTO;
import com.jobPortal.dto.ResponseDTO;
import com.jobPortal.entity.Notification;
import com.jobPortal.exception.JobPortalException;
import com.jobPortal.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@Validated
public class NotificationController {
	@Autowired
	private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<ResponseDTO> sendNotification(@RequestBody @Valid NotificationDTO notificationDTO) throws JobPortalException {
        notificationService.sendNotification(notificationDTO);
        return new ResponseEntity<>(new ResponseDTO("Notification sent"), HttpStatus.CREATED);
    }
	
	@GetMapping("/{userId}")
	public ResponseEntity<List<Notification>>getNotifications(@PathVariable Long userId){
		return new ResponseEntity<>(notificationService.getUnreadNotifications(userId), HttpStatus.OK);
	}

	@PutMapping("/read/{id}")
	public ResponseEntity<ResponseDTO>readNotification(@PathVariable Long id) throws JobPortalException {
		notificationService.readNotification(id);
		return new ResponseEntity<>(new ResponseDTO("Success"), HttpStatus.OK);
	}
}
