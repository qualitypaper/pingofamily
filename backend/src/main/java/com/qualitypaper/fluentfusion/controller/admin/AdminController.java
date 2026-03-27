package com.qualitypaper.fluentfusion.controller.admin;

import com.qualitypaper.fluentfusion.service.admin.AdminService;
import com.qualitypaper.fluentfusion.service.email.NotificationService;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;
  private final NotificationService notificationService;

  @PostMapping("/query")
  public List<Map<String, Object>> query(@RequestBody QueryRequest query) throws HttpMediaTypeNotAcceptableException {
    return adminService.query(query.query());
  }

  @GetMapping("/getWord/{wordId}")
  public List<Map<String, Object>> getWord(@PathVariable Long wordId) throws HttpMediaTypeNotAcceptableException {
    return adminService.getWord(wordId);
  }

  @PostMapping("/sendEmail")
  public MapSB sendEmail(@RequestBody SendEmailRequest request) {
    notificationService.sendNotification(request.to(), request.subject(), request.body());

    return HttpUtils.successResponse();
  }


  public record SendEmailRequest(String[] to, String subject, @Nullable String body) {
  }

  public record QueryRequest(String query) {
  }

}
