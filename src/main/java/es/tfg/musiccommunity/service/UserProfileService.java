package es.tfg.musiccommunity.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.messages.request.LoginForm;
import es.tfg.musiccommunity.messages.request.SignUpForm;
import es.tfg.musiccommunity.messages.response.JwtResponse;
import es.tfg.musiccommunity.messages.response.ResponseMessage;
import es.tfg.musiccommunity.model.Role;
import es.tfg.musiccommunity.model.RoleName;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.RoleRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.FollowerDto;
import es.tfg.musiccommunity.service.dto.TagDto;
import es.tfg.musiccommunity.service.dto.UserProfileInfoDto;
import es.tfg.musiccommunity.service.security.JwtProvider;
import es.tfg.musiccommunity.service.security.UserAuth;

@Service
public class UserProfileService implements IUserProfileService, UserDetailsService {

	@Autowired
	UserProfileRepository userProfileRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
    JwtProvider jwtProvider;
    
    @Autowired
    private CommonService commonService;

    @Autowired
    private NotificationService notificationService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserProfile user = userProfileRepository.findByLogin(username).orElseThrow(
				() -> new UsernameNotFoundException("No se ha encontrado al usuario -> login : " + username));

		return UserAuth.build(user);
	}

	public ResponseEntity<JwtResponse> authenticateUser(LoginForm loginRequest){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtProvider.generateJwtToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
	}

	@Transactional
	public ResponseEntity<ResponseMessage> registerUser(SignUpForm signUpRequest){
		if (userProfileRepository.existsByLogin(signUpRequest.getLogin())) {
			return new ResponseEntity<>(new ResponseMessage("El login ya está en uso!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userProfileRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>(new ResponseMessage("El email ya está en uso!"),
					HttpStatus.BAD_REQUEST);
		}

		String pattern = "\\d{9}";
		if (!signUpRequest.getPhone().matches(pattern)) {
			return new ResponseEntity<>(new ResponseMessage("Número de teléfono inválido!"),
					HttpStatus.BAD_REQUEST);
		}

		UserProfile user = new UserProfile(signUpRequest.getLogin(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getPhone());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Causa: No se ha encontrado rol."));
			roles.add(userRole);
		});

		user.setRoles(roles);
		userProfileRepository.save(user);

		return new ResponseEntity<>(new ResponseMessage("Usuario registrado correctamente!"), HttpStatus.OK);
	}

    public ResponseEntity<List<UserProfileInfoDto>> getAllUsers(String userName) {
        List<UserProfile> users;
        if (userName.isEmpty()) {
            users = userProfileRepository.findByOrderByLogin();
        } else {
            users = userProfileRepository.findAllByLoginLike(userName);
        }
        List<UserProfileInfoDto> usersDto = new ArrayList<>(25);
        for (UserProfile user : users) {
            List<TagDto> interests = new ArrayList<>(5);
            List<Tag> userInterests = user.getInterests().stream().collect(Collectors.toList());
            userInterests.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : userInterests) {
                interests.add(new TagDto(tag.getTagName()));
            }
            usersDto.add(new UserProfileInfoDto(user.getId(), user.getLogin(), user.getEmail(), user.getPhone(), 
                user.getBio(), interests));
        }
        return new ResponseEntity<>((usersDto), HttpStatus.OK);
    }

    public ResponseEntity<UserProfileInfoDto> getUserInfo(String login) {
        List<TagDto> interests = new ArrayList<>(5);
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if (u.isPresent()) {
            UserProfile user = u.get();
            List<Tag> userInterests = user.getInterests().stream().collect(Collectors.toList()) ;
            userInterests.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : userInterests) {
                interests.add(new TagDto(tag.getTagName()));
            }
            UserProfileInfoDto userDto = new UserProfileInfoDto(user.getId(), user.getLogin(), 
                user.getEmail(), user.getPhone(), user.getBio(), interests);
            return new ResponseEntity<>((userDto), HttpStatus.OK);
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<UserProfileInfoDto> updateUserInfo(String login, UserProfileInfoDto updatedUser) {
        String pattern = "\\d{9}";
        Set<Tag> tags;
        List<TagDto> interestsDto = new ArrayList<>(5);
        if (!updatedUser.getPhone().matches(pattern)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        tags = commonService.handleTags(updatedUser.getInterests());
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if (u.isPresent()) {
            UserProfile user = u.get();
            user.setPhone(updatedUser.getPhone());
            user.setInterests(tags);
            user.setBio(updatedUser.getBio());
            user = userProfileRepository.save(user);
            List<Tag> userInterests = user.getInterests().stream().collect(Collectors.toList()) ;
            userInterests.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : userInterests) {
                interestsDto.add(new TagDto(tag.getTagName()));
            }
            UserProfileInfoDto userDto = new UserProfileInfoDto(user.getId(), user.getLogin(), user.getEmail(), 
                user.getPhone(), user.getBio(),interestsDto);
            return new ResponseEntity<>((userDto), HttpStatus.OK);
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<FollowerDto>> getUserFollowers(String login) {
        List<FollowerDto> followersDto = new ArrayList<>();
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if (u.isPresent()) {
            UserProfile user = u.get();
            List<UserProfile> users = user.getFollowers().stream().collect(Collectors.toList());
            users.sort((a,b) -> a.getLogin().toLowerCase().compareTo(b.getLogin().toLowerCase()));
            for (UserProfile aux : users) {
                List<TagDto> interests = new ArrayList<>();
                List<Tag> userInterests = aux.getInterests().stream().collect(Collectors.toList());
                userInterests.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
                for (Tag tag : userInterests) {
                    interests.add(new TagDto(tag.getTagName()));
                }
                followersDto.add(new FollowerDto(aux.getId(), aux.getLogin(), aux.getBio(), interests));
            }
            return new ResponseEntity<>((followersDto), HttpStatus.OK);
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<FollowerDto>> getUserFollows(String login) {
        List<FollowerDto> followsDto = new ArrayList<>(10);
        Optional<UserProfile> u = userProfileRepository.findByLogin(login);
        if (u.isPresent()) {
            UserProfile user = u.get();
            List<UserProfile> users = user.getFollowing().stream().collect(Collectors.toList());
            users.sort((a,b) -> a.getLogin().toLowerCase().compareTo(b.getLogin().toLowerCase()));
            for (UserProfile aux : users) {
                List<TagDto> interests = new ArrayList<>();
                List<Tag> userInterests = aux.getInterests().stream().collect(Collectors.toList());
                userInterests.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
                for (Tag tag : userInterests) {
                    interests.add(new TagDto(tag.getTagName()));
                }
                followsDto.add(new FollowerDto(aux.getId(), aux.getLogin(), aux.getBio(), interests));
            }
            return new ResponseEntity<>((followsDto), HttpStatus.OK);
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Boolean> amIFollower(String me, String user) {
        Optional<UserProfile> uTarget = userProfileRepository.findByLogin(user);
        Optional<UserProfile> uMe = userProfileRepository.findByLogin(me);
        if (uTarget.isPresent() && uMe.isPresent()) {
            if(!uTarget.get().getFollowers().contains(uMe.get())){
                return new ResponseEntity<>(false, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<Void> followUser(String login, String subscribeTo) {
        Optional<UserProfile> uToFollow = userProfileRepository.findByLogin(subscribeTo);
        Optional<UserProfile> uMe = userProfileRepository.findByLogin(login);
        if (uToFollow.isPresent() && uMe.isPresent()) {
            UserProfile followed = uToFollow.get();
            UserProfile follower = uMe.get();
            if(!followed.getFollowers().contains(follower)){
                followed.addFollower(follower);
                notificationService.notifyNewFollower(follower, followed);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<Void> unfollowUser(String login, String unsubscribeFrom) {
        Optional<UserProfile> uToUnfollow = userProfileRepository.findByLogin(unsubscribeFrom);
        Optional<UserProfile> uMe = userProfileRepository.findByLogin(login);
        if (uToUnfollow.isPresent() && uMe.isPresent()) {
            UserProfile unfollowed = uToUnfollow.get();
            UserProfile follower = uMe.get();
            if(unfollowed.getFollowers().contains(follower)){
                unfollowed.getFollowers().remove(follower);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

}
