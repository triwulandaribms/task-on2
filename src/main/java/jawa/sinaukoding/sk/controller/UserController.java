package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.RegisterBuyerReq;
import jawa.sinaukoding.sk.model.request.RegisterSellerReq;
import jawa.sinaukoding.sk.model.request.ResetPasswordReq;
import jawa.sinaukoding.sk.model.request.UpdateProfileReq;
import jawa.sinaukoding.sk.service.UserService;
import jawa.sinaukoding.sk.util.SecurityContextHolder;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secured/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public Response<Object> listUser(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.listUsers(authentication, page, size);

    }

    @PostMapping("/register-seller")
    public Response<Object> registerSeller(@RequestBody RegisterSellerReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.registerSeller(authentication, req);
    }

    @PostMapping("/register-buyer")
    public Response<Object> registerBuyer(@RequestBody RegisterBuyerReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.registerBuyer(authentication, req);
    }

    @PostMapping("/reset-password")
    public Response<Object> resetPassword(@RequestBody ResetPasswordReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.resetPassword(authentication, req);

    }

    @PostMapping("/update-profile")
    public Response<Object> updateProfile(@RequestBody UpdateProfileReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.updateProfile(authentication, req);
    }

    @DeleteMapping("/delete-user")
    public Response<Object> deleteUser(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("id");
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return userService.deletedUser(authentication, userId);
    }
}
