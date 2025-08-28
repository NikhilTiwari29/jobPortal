package com.jobPortal.utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Random;

public class Utils {

    public static String htmlContent = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f9f9f9;
      margin: 0;
      padding: 0;
    }
    .container {
      max-width: 500px;
      margin: 40px auto;
      padding: 20px;
      background: #ffffff;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    h2 {
      color: #333333;
      text-align: center;
    }
    .otp-box {
      margin: 20px auto;
      padding: 15px;
      background: #f3f7ff;
      border: 2px dashed #3b82f6;
      border-radius: 8px;
      text-align: center;
      font-size: 24px;
      font-weight: bold;
      color: #1e40af;
      letter-spacing: 4px;
    }
    p {
      color: #555555;
      font-size: 14px;
      line-height: 1.6;
      text-align: center;
    }
    .footer {
      margin-top: 20px;
      text-align: center;
      font-size: 12px;
      color: #888888;
    }
  </style>
</head>
<body>
  <div class="container">
    <h2>Your One-Time Password (OTP)</h2>
    <p>Please use the OTP below to complete your verification. This OTP is valid for <b>5 minutes</b>.</p>
    <div class="otp-box">%s</div>
    <p>If you did not request this, please ignore this email.</p>
    <div class="footer">
      &copy; 2025 Job Portal. All rights reserved.
    </div>
  </div>
</body>
</html>
""";


    public static String generateOTP(){
        return String.valueOf(new Random().nextInt(999999));
    }

    public static void sendOtpMail(String email, String otp, JavaMailSender mailSender) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setTo(email);
        message.setSubject("Your OTP Code");
        message.setContent(htmlContent.formatted(otp), "text/html; charset=utf-8");
        mailSender.send(message);
    }
}
