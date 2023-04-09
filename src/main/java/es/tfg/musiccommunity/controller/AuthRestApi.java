package es.tfg.musiccommunity.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.tfg.musiccommunity.messages.request.LoginForm;
import es.tfg.musiccommunity.messages.request.SignUpForm;
import es.tfg.musiccommunity.messages.response.JwtResponse;
import es.tfg.musiccommunity.messages.response.ResponseMessage;
import es.tfg.musiccommunity.service.UserProfileService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestApi {

    @Autowired
    private UserProfileService userProfileService;

	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
        return userProfileService.authenticateUser(loginRequest);
	}

	@PostMapping("/signup")
	public ResponseEntity<ResponseMessage> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
		return userProfileService.registerUser(signUpRequest);
	}
}
