package es.tfg.musiccommunity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.tfg.musiccommunity.service.dto.FollowerDto;
import es.tfg.musiccommunity.service.dto.NotificationDto;
import es.tfg.musiccommunity.service.dto.UserProfileInfoDto;
import es.tfg.musiccommunity.service.NotificationService;
import es.tfg.musiccommunity.service.UserProfileService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<List<UserProfileInfoDto>> getAll(@RequestParam(value="userName", required=false) String userName) {
        String login = (userName == null || userName.isEmpty()) ? "" : userName;
        return userProfileService.getAllUsers(login);
    }

    @GetMapping("/user")
    public ResponseEntity<UserProfileInfoDto> getUserInfo(Authentication auth, @RequestParam(value="login", required=false) String login) {
        String ownLogin = auth.getName();
        if (login == null){
            return userProfileService.getUserInfo(ownLogin);
        } else {
            return userProfileService.getUserInfo(login);
        }
    }

    @PutMapping("/user")
    public ResponseEntity<UserProfileInfoDto> updateUserInfo(Authentication auth, @RequestBody UserProfileInfoDto userDto) {
        String login = auth.getName();
        return userProfileService.updateUserInfo(login, userDto);
    }

    @GetMapping("/user/followers")
    public ResponseEntity<List<FollowerDto>> getUserFollowers(Authentication auth, @RequestParam(value="login", required=false) String login) {
        String userName = (login == null || login.isEmpty()) ? auth.getName() : login;
        return userProfileService.getUserFollowers(userName);
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<FollowerDto>> getUserFollowed(Authentication auth) {
        String login = auth.getName();
        return userProfileService.getUserFollows(login);
    }

    @GetMapping("user/amIFollower")
    public ResponseEntity<Boolean> amIFollower(Authentication auth, @RequestParam(value="user", required=true) String user) {
        String me = auth.getName();
        return userProfileService.amIFollower(me, user);
    }

    @PostMapping("/user/followers")
    public ResponseEntity<Void> followUser(Authentication auth, @RequestParam(value="subscribeTo", required=true) String subscribeTo) {
        String login = auth.getName();
        return userProfileService.followUser(login, subscribeTo);
    }

    @PostMapping("/user/followers/unfollow")
    public ResponseEntity<Void> unfollowUser(Authentication auth, @RequestParam(value="unsubscribeFrom", required=true) String unsubscribeFrom) {
        String login = auth.getName();
        return userProfileService.unfollowUser(login, unsubscribeFrom);
    }

    @GetMapping("/user/notifications")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(Authentication auth, @RequestParam(value="unread", required=true) boolean unread) {
        String login = auth.getName();
        return notificationService.getNotifications(login, unread);
    }

    @PutMapping("/user/notifications/{id}")
    public ResponseEntity<Void> markNotificationAsread(Authentication auth, @PathVariable Long id) {
        String login = auth.getName();
        return notificationService.markNotificationAsRead(login, id);
    }
}
