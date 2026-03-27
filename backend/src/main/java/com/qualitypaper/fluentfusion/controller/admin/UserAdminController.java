package com.qualitypaper.fluentfusion.controller.admin;

import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserAdminResponse;
import com.qualitypaper.fluentfusion.service.admin.AdminService;
import com.qualitypaper.fluentfusion.service.recall.RecallService;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class UserAdminController {

    private final AdminService adminService;
    private final RecallService recallCronService;

    public UserAdminController(AdminService adminService, RecallService recallCronService) {
        this.adminService = adminService;
        this.recallCronService = recallCronService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MapSB> deleteUser(@PathVariable Long id) {
        if (id == null) return ResponseEntity.badRequest().build();

        adminService.deleteUser(id);
        return ResponseEntity.ok(HttpUtils.successResponse());
    }

    @GetMapping("/all")
    public List<UserAdminResponse> allUsers() {
        return adminService.allUsers();
    }

    @PostMapping("/recall")
    public ResponseEntity<MapSB> recall() {
        recallCronService.recall();
        return ResponseEntity.ok(HttpUtils.successResponse());
    }
}
