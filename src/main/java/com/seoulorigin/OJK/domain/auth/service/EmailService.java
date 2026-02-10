package com.seoulorigin.OJK.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// TODO: 송신 이메일 비공개 처리
// TODO: 이메일 내용 분리 -> Thymeleaf

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private static final String SENDER_EMAIL = "seoulorigin@gmail.com";

    public String createNumber() {
        return RandomStringUtils.randomNumeric(6);
    }

    public String sendMail(String emailAddress) {
        String number = createNumber();
        MimeMessage message = javaMailSender.createMimeMessage(); // TEXT + 사진/영상 + HTML 디자인 포함 가능

        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, emailAddress);
            message.setSubject("[오작교] 회원가입 이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }

        javaMailSender.send(message);
        return number;
    }
}
