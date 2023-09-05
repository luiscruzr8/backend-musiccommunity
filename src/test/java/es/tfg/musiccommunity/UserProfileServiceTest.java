package es.tfg.musiccommunity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
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
import es.tfg.musiccommunity.repository.TagRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.UserProfileService;
import es.tfg.musiccommunity.service.dto.FollowerDto;
import es.tfg.musiccommunity.service.dto.TagDto;
import es.tfg.musiccommunity.service.dto.UserProfileInfoDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserProfileServiceTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserProfileService userProfileService;

    private static final String TEST_1 = "ZXYYYY";
    private static final String TEST_1_MAIL = "test1@test1";
    private static final String TEST_2 = "GHIIII";
    private static final String TEST_2_MAIL = "test2@test2";
    private static final String TEST_3 = "ABCCCC";
    private static final String TEST_3_MAIL = "test3@test3";
    private static final String PHONE_1 = "123456789";
    private static final String PHONE_2 = "987654321";
    private static final String PHONE_3 = "123459876";
    private static final String LUISIN = "luisin";
    private static final String LUISIN_MAIL = "luisin@mail.com";
    private static final String UNEXISTENT = "unexistent";
    private static final String INVALID_PHONE = "98765432l";
    private static final String ETIQUETA_1 = "etiqueta1";
    private static final String ETIQUETA_2 = "etiqueta2";

    @Test
    public void registerUserLoginOrEmailAlreadyUsedOrInvalidPhoneTest() {
        UserProfile luisin = new UserProfile(LUISIN, LUISIN_MAIL, LUISIN, PHONE_3, "");
        userProfileRepository.save(luisin);
        Set<String> roles = new HashSet<>();
        roles.add("user");

        SignUpForm newUserToRegister = new SignUpForm(LUISIN, LUISIN_MAIL, roles, LUISIN, PHONE_3, "");
        ResponseEntity<ResponseMessage> errorLogin = userProfileService.registerUser(newUserToRegister);
        Assert.assertEquals("El login ya está en uso!", errorLogin.getBody().getMessage());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorLogin.getStatusCode());

        SignUpForm newUserToRegister2 = new SignUpForm(TEST_1, LUISIN_MAIL, roles, LUISIN, PHONE_3, "");
        ResponseEntity<ResponseMessage> errorEmail = userProfileService.registerUser(newUserToRegister2);
        Assert.assertEquals("El email ya está en uso!", errorEmail.getBody().getMessage());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorEmail.getStatusCode());

        SignUpForm newUserToRegister3 = new SignUpForm(TEST_1, TEST_2_MAIL, roles, LUISIN, UNEXISTENT, "");
        ResponseEntity<ResponseMessage> errorPhone = userProfileService.registerUser(newUserToRegister3);
        Assert.assertEquals("Número de teléfono inválido!", errorPhone.getBody().getMessage());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorPhone.getStatusCode());
    }

    @Test(expected = RuntimeException.class)
    public void registerUserNoRoleTest() {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignUpForm newUserToRegister = new SignUpForm(LUISIN, LUISIN_MAIL, roles, LUISIN, PHONE_3,"");
        userProfileService.registerUser(newUserToRegister);
    }

    @Test
    public void registerUserTest() {
        Role r = new Role(RoleName.ROLE_USER);
        roleRepository.save(r);
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignUpForm newUserToRegister = new SignUpForm(LUISIN, LUISIN_MAIL, roles, LUISIN, PHONE_3,"");
        ResponseEntity<ResponseMessage> success = userProfileService.registerUser(newUserToRegister);
        Assert.assertEquals("Usuario registrado correctamente!", success.getBody().getMessage());
        Assert.assertEquals(HttpStatus.OK, success.getStatusCode());
    }   

    @Test
    public void authenticateUserTest() {
        Role r = new Role(RoleName.ROLE_USER);
        roleRepository.save(r);
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SignUpForm newUserToRegister = new SignUpForm(TEST_1, TEST_1_MAIL, roles, TEST_1, PHONE_1,"");
        userProfileService.registerUser(newUserToRegister);

        LoginForm authLogin = new LoginForm(TEST_1, TEST_1, "");
        ResponseEntity<JwtResponse> loginSuccess = userProfileService.authenticateUser(authLogin);
        Assert.assertEquals(1, loginSuccess.getBody().getAuthorities().size());
        Assert.assertEquals(TEST_1, loginSuccess.getBody().getUsername());
        Assert.assertEquals(HttpStatus.OK, loginSuccess.getStatusCode());

    }

    @Test
    public void createUserSuccessFullTest() {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");

        userProfileRepository.save(u1);

        Optional<UserProfile> result = userProfileRepository.findById(u1.getId());
        if (result.isPresent()) {
            UserProfile resultFound = result.get();
            Assert.assertEquals(resultFound.getId(), u1.getId());
            Assert.assertEquals(resultFound.getEmail(), u1.getEmail());
            Assert.assertEquals(resultFound.getLogin(), u1.getLogin());
            Assert.assertEquals(resultFound.getPassword(), u1.getPassword());
            Assert.assertEquals(resultFound.getPhone(), u1.getPhone());
        }

    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void createUserFailTest() {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");

        userProfileRepository.findById(u1.getId()).get();
    }

    @Test
    public void getZeroUsersTest() {
        ResponseEntity<List<UserProfileInfoDto>> users = userProfileService.getAllUsers("");
        Assert.assertEquals(0, users.getBody().size());
        Assert.assertEquals(HttpStatus.OK, users.getStatusCode());
    }

    @Test
    public void getAllUsersTest() {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u1);
        userProfileRepository.save(u2);
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t1);
        tagRepository.save(t2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);
        u3.setInterests(tags);
        userProfileRepository.save(u3);
        ResponseEntity<List<UserProfileInfoDto>> users = userProfileService.getAllUsers("");
        Assert.assertEquals(3, users.getBody().size());
        Assert.assertEquals(HttpStatus.OK, users.getStatusCode());
    }

    @Test
    public void getUnexistentUserInfoTest() {
        ResponseEntity<UserProfileInfoDto> user = userProfileService.getUserInfo(UNEXISTENT);
        Assert.assertEquals(null, user.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, user.getStatusCode());
    }

    @Test
    public void getUserInfoTest() {
        UserProfile luisin = new UserProfile(LUISIN, LUISIN_MAIL, LUISIN, PHONE_3, "");
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t1);
        tagRepository.save(t2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);
        luisin.setInterests(tags);
        userProfileRepository.save(luisin);

        ResponseEntity<UserProfileInfoDto> user = userProfileService.getUserInfo(LUISIN);
        Assert.assertEquals(HttpStatus.OK, user.getStatusCode());
        Assert.assertEquals(luisin.getId(), user.getBody().getId());
        Assert.assertEquals(luisin.getEmail(), user.getBody().getEmail());
        Assert.assertEquals(luisin.getLogin(), user.getBody().getLogin());
        Assert.assertEquals(luisin.getPhone(), user.getBody().getPhone());
    }

    @Test
    public void updateUserInfoInvalidPhoneTest() {
        UserProfileInfoDto updatedDto = new UserProfileInfoDto(null, null, null, INVALID_PHONE, null, null);
        ResponseEntity<UserProfileInfoDto> user = userProfileService.updateUserInfo(LUISIN, updatedDto);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, user.getStatusCode());
        Assert.assertEquals(null, user.getBody());
    }

    @Test
    public void updateUnexistentUserTest() {
        List<TagDto> tags = new ArrayList<>(5);
        UserProfileInfoDto updatedDto = new UserProfileInfoDto(null, null, null, PHONE_2, null, tags);
        ResponseEntity<UserProfileInfoDto> user = userProfileService.updateUserInfo(LUISIN, updatedDto);
        Assert.assertEquals(HttpStatus.NOT_FOUND, user.getStatusCode());
        Assert.assertEquals(null, user.getBody());
    }

    @Test
    public void updateUserInfoTest() {
        UserProfile luisin = new UserProfile(LUISIN, LUISIN_MAIL, LUISIN, PHONE_3, "");
        userProfileRepository.save(luisin);
        TagDto t1 = new TagDto(ETIQUETA_1);
        TagDto t2 = new TagDto(ETIQUETA_2);
        List<TagDto> tags = new ArrayList<>(5);
        tags.add(t1);
        tags.add(t2);
        UserProfileInfoDto updatedDto = new UserProfileInfoDto(null, null, null, PHONE_2, null, tags);
        ResponseEntity<UserProfileInfoDto> user = userProfileService.updateUserInfo(LUISIN, updatedDto);
        Assert.assertEquals(HttpStatus.OK, user.getStatusCode());
        Assert.assertEquals(luisin.getId(), user.getBody().getId());
        Assert.assertEquals(luisin.getLogin(), user.getBody().getLogin());
        Assert.assertEquals(luisin.getEmail(), user.getBody().getEmail());
        Assert.assertEquals(PHONE_2, user.getBody().getPhone());
        Assert.assertEquals(tags.get(0).getTagName(), user.getBody().getInterests().get(0).getTagName());
    }

    @Test
    public void getFollowersAndFollowingUnexistentUserTest(){
        ResponseEntity<List<FollowerDto>> followersUnexistentUser = userProfileService.getUserFollowers(UNEXISTENT);
        Assert.assertEquals(null, followersUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, followersUnexistentUser.getStatusCode());

        ResponseEntity<List<FollowerDto>> followingUnexistentUser = userProfileService.getUserFollows(UNEXISTENT);
        Assert.assertEquals(null, followingUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, followingUnexistentUser.getStatusCode());
    }

    @Test
    public void getFollowersAndFollowingTest() {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u1);
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t1);
        tagRepository.save(t2);
        Set<Tag> tags = new HashSet<>();
        tags.add(t1);
        tags.add(t2);
        Set<Tag> tags2 = new HashSet<>();
        tags2.add(t2);
        tags2.add(t1);
        u2.setInterests(tags2);
        userProfileRepository.save(u2);
        u3.setInterests(tags);
        userProfileRepository.save(u3);
        u2.addFollower(u1);
        u1.addFollower(u2);
        u1.addFollower(u3);
        u3.addFollower(u2);
        userProfileRepository.save(u1);
        userProfileRepository.save(u2);
        userProfileRepository.save(u3);
        ResponseEntity<List<FollowerDto>> followersU1 = userProfileService.getUserFollowers(u1.getLogin());
        Assert.assertEquals(2, followersU1.getBody().size());
        Assert.assertEquals(HttpStatus.OK, followersU1.getStatusCode());
        Assert.assertEquals(TEST_3, followersU1.getBody().get(0).getLogin());
        Assert.assertEquals(u3.getId(), followersU1.getBody().get(0).getId());
        Assert.assertEquals(ETIQUETA_1, followersU1.getBody().get(0).getInterests().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_2, followersU1.getBody().get(0).getInterests().get(1).getTagName());
        Assert.assertEquals(TEST_2, followersU1.getBody().get(1).getLogin());
        Assert.assertEquals(u2.getId(), followersU1.getBody().get(1).getId());
        Assert.assertEquals(ETIQUETA_1, followersU1.getBody().get(1).getInterests().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_2, followersU1.getBody().get(1).getInterests().get(1).getTagName());

        ResponseEntity<List<FollowerDto>> followingU2 = userProfileService.getUserFollows(u2.getLogin());
        Assert.assertEquals(2, followingU2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, followingU2.getStatusCode());
        Assert.assertEquals(TEST_3, followingU2.getBody().get(0).getLogin());
        Assert.assertEquals(u3.getId(), followingU2.getBody().get(0).getId());
        Assert.assertEquals(ETIQUETA_1, followingU2.getBody().get(0).getInterests().get(0).getTagName());
        Assert.assertEquals(ETIQUETA_2, followingU2.getBody().get(0).getInterests().get(1).getTagName());
        Assert.assertEquals(TEST_1, followingU2.getBody().get(1).getLogin());
        Assert.assertEquals(u1.getId(), followingU2.getBody().get(1).getId());
        Assert.assertEquals(0, followingU2.getBody().get(1).getInterests().size());
    }

    @Test
    public void amIFollowerBothUnexistentTest(){
        ResponseEntity<Boolean> unexistentUserAmIFollowerUnexistentUser = userProfileService.amIFollower(UNEXISTENT, UNEXISTENT);
        Assert.assertEquals(null, unexistentUserAmIFollowerUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserAmIFollowerUnexistentUser.getStatusCode());

        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);

        ResponseEntity<Boolean> userAmIFollowerUnexistentUser = userProfileService.amIFollower(u1.getLogin(), UNEXISTENT);
        Assert.assertEquals(null, userAmIFollowerUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, userAmIFollowerUnexistentUser.getStatusCode());

        ResponseEntity<Boolean> unexistentUserAmIFollowerUser = userProfileService.amIFollower(UNEXISTENT, u1.getLogin());
        Assert.assertEquals(null, unexistentUserAmIFollowerUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserAmIFollowerUser.getStatusCode());
    }

    @Test
    public void amIFollowerTest()  {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);

        ResponseEntity<Boolean> amIFollower = userProfileService.amIFollower(u1.getLogin(), u2.getLogin());
        Assert.assertEquals(false, amIFollower.getBody());
        Assert.assertEquals(HttpStatus.OK, amIFollower.getStatusCode());

        userProfileService.followUser(u1.getLogin(), u2.getLogin());

        ResponseEntity<Boolean> amIFollowerAgain = userProfileService.amIFollower(u1.getLogin(), u2.getLogin());
        Assert.assertEquals(true, amIFollowerAgain.getBody());
        Assert.assertEquals(HttpStatus.OK, amIFollowerAgain.getStatusCode());
    }

    @Test
    public void unexistentUserFollowUnexistentUserTest()  {
        ResponseEntity<Void> unexistentUserFollowUnexistentUser = userProfileService.followUser(UNEXISTENT, UNEXISTENT);
        Assert.assertEquals(null, unexistentUserFollowUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserFollowUnexistentUser.getStatusCode());

        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);

        ResponseEntity<Void> userFollowUnexistentUser = userProfileService.followUser(u1.getLogin(), UNEXISTENT);
        Assert.assertEquals(null, userFollowUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, userFollowUnexistentUser.getStatusCode());

        ResponseEntity<Void> unexistentUserFollowUser = userProfileService.followUser(UNEXISTENT, u1.getLogin());
        Assert.assertEquals(null, unexistentUserFollowUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserFollowUser.getStatusCode());
    }

    @Test
    public void followUserTest()  {
        UserProfile u1 = new UserProfile(TEST_1, TEST_1_MAIL, TEST_1, PHONE_1, "");
        userProfileRepository.save(u1);
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);

        ResponseEntity<Void> followUser = userProfileService.followUser(u1.getLogin(), u2.getLogin());
        Assert.assertEquals(null, followUser.getBody());
        Assert.assertEquals(HttpStatus.OK, followUser.getStatusCode());

        ResponseEntity<Void> followUserAgain = userProfileService.followUser(u1.getLogin(), u2.getLogin());
        Assert.assertEquals(null, followUserAgain.getBody());
        Assert.assertEquals(HttpStatus.NOT_MODIFIED, followUserAgain.getStatusCode());
    }

    @Test
    public void unexistentUserUnfollowUnexistentUserTest(){
        ResponseEntity<Void> unexistentUserUnfollowUnexistentUser = userProfileService.unfollowUser(UNEXISTENT, UNEXISTENT);
        Assert.assertEquals(null, unexistentUserUnfollowUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserUnfollowUnexistentUser.getStatusCode());

        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);

        ResponseEntity<Void> userUnfollowUnexistentUser = userProfileService.unfollowUser(u2.getLogin(), UNEXISTENT);
        Assert.assertEquals(null, userUnfollowUnexistentUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, userUnfollowUnexistentUser.getStatusCode());

        ResponseEntity<Void> unexistentUserUnfollowUser = userProfileService.unfollowUser(UNEXISTENT, u2.getLogin());
        Assert.assertEquals(null, unexistentUserUnfollowUser.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUserUnfollowUser.getStatusCode());
    }

    @Test
    public void unfollowUserTest() {
        
        UserProfile u2 = new UserProfile(TEST_2, TEST_2_MAIL, TEST_2, PHONE_2, "");
        userProfileRepository.save(u2);
        UserProfile u3 = new UserProfile(TEST_3, TEST_3_MAIL, TEST_3, PHONE_3, "");
        userProfileRepository.save(u3);

        userProfileService.followUser(u2.getLogin(), u3.getLogin());

        ResponseEntity<Void> unfollowUser = userProfileService.unfollowUser(u2.getLogin(), u3.getLogin());
        Assert.assertEquals(null, unfollowUser.getBody());
        Assert.assertEquals(HttpStatus.OK, unfollowUser.getStatusCode());

        ResponseEntity<Void> unfollowUserAgain = userProfileService.unfollowUser(u2.getLogin(), u3.getLogin());
        Assert.assertEquals(null, unfollowUserAgain.getBody());
        Assert.assertEquals(HttpStatus.NOT_MODIFIED, unfollowUserAgain.getStatusCode());
    }

}
