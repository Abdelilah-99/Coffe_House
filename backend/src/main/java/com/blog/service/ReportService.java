package com.blog.service;

import com.blog.dto.RegisterResponse;
import com.blog.dto.UsersRespons;
import com.blog.entity.Report;
import com.blog.entity.User;
import com.blog.repository.ReportRepository;
import com.blog.repository.UserRepository;
import com.blog.service.UsersServices;
import org.springframework.stereotype.Service;
import com.blog.dto.ReportProfileResponse;
import com.blog.exceptions.UserNotFoundException;
import com.blog.exceptions.UserNotLoginException;
import com.blog.dto.ReportRequest;
import com.blog.exceptions.ReportException;

@Service
public class ReportService {
    UsersServices usersServices;
    UserRepository userRepository;
    ReportRepository reportRepository;

    public ReportService(
            UsersServices usersServices,
            UserRepository userRepository,
            ReportRepository reportRepository) {
        this.usersServices = usersServices;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    public ReportProfileResponse reportProfile(String uuid, ReportRequest reason) {
        UsersRespons usersRespons;
        try {
            usersRespons = usersServices.getCurrentUser();
        } catch (Exception e) {
            throw new UserNotFoundException("user not found for reporting");
        }
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> {
            throw new UserNotFoundException("user not found for reporting");
        });
        User crrUser = userRepository.findByUserName(usersRespons.getUsername()).orElseThrow(() -> {
            throw new UserNotLoginException("user not logged in or not rregistred");
        });
        if (reason.getReason() == null || reason.getReason().trim().isEmpty()) {
            throw new ReportException("reason must no be empty or null");
        }
        Report newReport = new Report();
        newReport.setReason(reason.getReason());
        newReport.setReportedUserId(user);
        newReport.setReporterId(crrUser);
        newReport.setTime(System.currentTimeMillis());
        newReport.setTypeReport("Profile");
        reportRepository.save(newReport);
        return new ReportProfileResponse("the report has created successfully");
    }
}
