package es.tfg.musiccommunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.ImgPostService;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImgPostServiceTest { 

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private ImgPostService imgPostService;

    private static final String USER_1 = "usuario1";
    private static final String USER_2 = "usuario2";
    private static final String USER_1_MAIL = "usuario1@mail.com";
    private static final String USER_2_MAIL = "usuario2@mail.com";
    private static final String NUMBER_1 = "123456789";
    private static final String NUMBER_2 = "763547676";
    private static final String UNEXISTENT = "unexistent";
    private static final String IMG_PRUEBA = "src/test/resources/test-files/1.png";
    private static final String IMG_PRUEBA_2 = "src/test/resources/test-files/2.png";
    private static final String IMG_PRUEBA_3 = "src/test/resources/test-files/3.png";
    private static final String FILE_INVALID_EXT = "src/test/resources/test-files/invalid.txt";
    private static final String IMG_EXT = "image/xyz";
    private static final String TIT_1 = "TITULO_1";
    private static final String DESCR_1 = "DESCRIPCION_1";

    @Test
    public void unexistentUnauthorizedAndStoreImgPostTest() throws IOException {
        UserProfile user = new UserProfile(USER_1, USER_1_MAIL, USER_1, NUMBER_1, "");
        userProfileRepository.save(user);

        UserProfile user2 = new UserProfile(USER_2, USER_2_MAIL, USER_2, NUMBER_2, "");
        userProfileRepository.save(user2);

        File img1 = new File(IMG_PRUEBA);
        FileInputStream input = new FileInputStream(img1);
        MultipartFile multipart = new MockMultipartFile(img1.getName(), img1.getName(), IMG_EXT, input);

        ResponseEntity<Long> unexistentPost = imgPostService.storeImgPost(multipart, UNEXISTENT, -1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentPost.getStatusCode());

        Discussion d1 = new Discussion(TIT_1, user, DESCR_1, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<Long> unexistentUser = imgPostService.storeImgPost(multipart, UNEXISTENT, d1.getId());
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentUser.getStatusCode());

        ResponseEntity<Long> unauthorizedUser = imgPostService.storeImgPost(multipart, user2.getLogin(), d1.getId());
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedUser.getStatusCode());
    }

    @Test
    public void invalidExtensionStoreImgPostTest() throws IOException {
        UserProfile user = new UserProfile(USER_1, USER_1_MAIL, USER_1, NUMBER_1, "");
        userProfileRepository.save(user);

        Discussion d1 = new Discussion(TIT_1, user, DESCR_1, new HashSet<>());
        discussionRepository.save(d1);

        File extImg = new File(FILE_INVALID_EXT);
        FileInputStream input2 = new FileInputStream(extImg);
        MultipartFile multipart2 = new MockMultipartFile(extImg.getName(), extImg.getName(), "text/plain", input2);

        ResponseEntity<Long> differenteExtension = imgPostService.storeImgPost(multipart2, user.getLogin(), d1.getId());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, differenteExtension.getStatusCode());
    }

    @Test
    public void storeImgPostTest() throws IOException {
        UserProfile user = new UserProfile(USER_1, USER_1_MAIL, USER_1, NUMBER_1, "");
        userProfileRepository.save(user);

        Discussion d1 = new Discussion(TIT_1, user, DESCR_1, new HashSet<>());
        discussionRepository.save(d1);

        File img2 = new File(IMG_PRUEBA_2);
        FileInputStream input = new FileInputStream(img2);
        MultipartFile multipart = new MockMultipartFile(img2.getName(), img2.getName(), IMG_EXT, input);

        imgPostService.storeImgPost(multipart, user.getLogin(), d1.getId());

        File img3 = new File(IMG_PRUEBA_3);
        FileInputStream input1 = new FileInputStream(img3);
        MultipartFile multipart1 = new MockMultipartFile(img3.getName(), img3.getName(), IMG_EXT, input1);

        ResponseEntity<Long> newImgPost = imgPostService.storeImgPost(multipart1, user.getLogin(), d1.getId());
        Assert.assertEquals(HttpStatus.OK, newImgPost.getStatusCode());
    }

    @Test
    public void getUnexistentPostAndNoImgPostTest() {
        UserProfile user = new UserProfile(USER_1, USER_1_MAIL, USER_1, NUMBER_1, "");
        userProfileRepository.save(user);

        ResponseEntity<Resource> unexistentPost = imgPostService.getImgFile(-1L);
        Assert.assertEquals(HttpStatus.NOT_FOUND, unexistentPost.getStatusCode());

        Discussion d1 = new Discussion(TIT_1, user, DESCR_1, new HashSet<>());
        discussionRepository.save(d1);

        ResponseEntity<Resource> noImgPost = imgPostService.getImgFile(d1.getId());
        Assert.assertEquals(HttpStatus.NO_CONTENT, noImgPost.getStatusCode());
    }

    @Test
    public void getImgPostTest() throws IOException {
        UserProfile user = new UserProfile(USER_1, USER_1_MAIL, USER_1, NUMBER_1, "");
        userProfileRepository.save(user);

        Discussion d1 = new Discussion(TIT_1, user, DESCR_1, new HashSet<>());
        discussionRepository.save(d1);

        File img2 = new File(IMG_PRUEBA_2);
        FileInputStream input = new FileInputStream(img2);
        MultipartFile multipart = new MockMultipartFile(img2.getName(), img2.getName(), IMG_EXT, input);

        imgPostService.storeImgPost(multipart, user.getLogin(), d1.getId());

        ResponseEntity<Resource> imgPost = imgPostService.getImgFile(d1.getId());
        Assert.assertEquals(HttpStatus.OK, imgPost.getStatusCode());
    }

}