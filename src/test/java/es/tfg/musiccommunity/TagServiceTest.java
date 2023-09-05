package es.tfg.musiccommunity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.Discussion;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.DiscussionRepository;
import es.tfg.musiccommunity.repository.TagRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.TagService;
import es.tfg.musiccommunity.service.UserProfileService;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.TagDto;
import es.tfg.musiccommunity.service.dto.UserProfileInfoDto;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class TagServiceTest {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private TagService tagService;

    private static final String ETIQUETA_1 = "etiqueta1";
    private static final String ETIQUETA_2 = "etiqueta2";
    private static final String ETIQUETA_3 = "etiqueta3";
    private static final String ETIQUETA_4 = "etiqueta4";
    private static final String STRING_1 = "abcde";
    private static final String STRING_2 = "fghij";
    private static final String STRING_3 = "klmno";
    private static final String LUISIN = "luisin";
    private static final String LUISIN_MAIL = "luisin@mail.com";
    private static final String LUISIN_PHONE = "123459876";
    private static final String ALONSIN = "alonsin";
    private static final String ALONSIN_MAIL = "alonsin@mail.com";
    private static final String ALONSIN_PHONE = "982194635";

    @Test
    public void getZeroTagsTest() {
        ResponseEntity<List<TagDto>> tags = tagService.getAllTags();
        Assert.assertEquals(0, tags.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tags.getStatusCode());
    }

    @Test()
    public void getAllTagsTest() {
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        Tag t3 = new Tag(ETIQUETA_3);
        Tag t4 = new Tag(ETIQUETA_4);

        tagRepository.save(t1);
        tagRepository.save(t2);
        tagRepository.save(t3);
        tagRepository.save(t4);

        ResponseEntity<List<TagDto>> tagsDto = tagService.getAllTags();
        Assert.assertEquals(4, tagsDto.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tagsDto.getStatusCode());
    }

    @Test()
    public void getAllNewTagsTest() {
        /* PRIMERO VEMOS LAS ETIQUETAS EXISTENTES */
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t1);
        tagRepository.save(t2);

        /* PRIMERO VEMOS LAS ETIQUETAS EXISTENTES */
        ResponseEntity<List<TagDto>> tagsDto = tagService.getAllTags();
        Assert.assertEquals(2, tagsDto.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tagsDto.getStatusCode());

        /* AHORA UN USUARIO AÑADIRÁ NUEVAS ETIQUETAS AL EDITAR SU PERFIL */
        UserProfile luisin = new UserProfile(LUISIN, LUISIN_MAIL, LUISIN, LUISIN_PHONE, "");
        userProfileRepository.save(luisin);
        TagDto t3 = new TagDto(ETIQUETA_3);
        TagDto t4 = new TagDto(ETIQUETA_4);
        List<TagDto> tags = new ArrayList<>(5);
        tags.add(t3);
        tags.add(t4);
        UserProfileInfoDto updatedDto = new UserProfileInfoDto(null, null, null, LUISIN_PHONE, null, tags);
        userProfileService.updateUserInfo(LUISIN, updatedDto);
        ResponseEntity<List<TagDto>> tagsDto2 = tagService.getAllTags();
        Assert.assertEquals(4, tagsDto2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tagsDto2.getStatusCode());
    }

    @Test()
    public void getAllNewAndRepeatedTagsTest() {
        /* PRIMERO VEMOS LAS ETIQUETAS EXISTENTES */
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t1);
        tagRepository.save(t2);

        /* PRIMERO VEMOS LAS ETIQUETAS EXISTENTES */
        ResponseEntity<List<TagDto>> tagsDto = tagService.getAllTags();
        Assert.assertEquals(2, tagsDto.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tagsDto.getStatusCode());

        /* AHORA UN USUARIO AÑADIRÁ NUEVAS ETIQUETAS (UNA NUEVA Y OTRA EXISTENTE) AL EDITAR SU PERFIL */
        UserProfile luisin = new UserProfile(LUISIN, LUISIN_MAIL, LUISIN, LUISIN_PHONE, "");
        userProfileRepository.save(luisin);
        TagDto t3 = new TagDto(ETIQUETA_2);
        TagDto t4 = new TagDto(ETIQUETA_4);
        List<TagDto> tags = new ArrayList<>(5);
        tags.add(t3);
        tags.add(t4);
        UserProfileInfoDto updatedDto = new UserProfileInfoDto(null, null, null, LUISIN_PHONE, null, tags);
        userProfileService.updateUserInfo(LUISIN, updatedDto);
        ResponseEntity<List<TagDto>> tagsDto2 = tagService.getAllTags();
        Assert.assertEquals(3, tagsDto2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tagsDto2.getStatusCode());
    }

    @Test()
    public void getAllUsersByTagNotFoundTest() {
        ResponseEntity<List<UserProfileInfoDto>> users = tagService.getUsersByTag(ETIQUETA_1);
        Assert.assertEquals(null, users.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, users.getStatusCode());
    }

    @Test()
    public void getAllUsersByTagTest() {
        /* PRIMERO VEMOS LAS ETIQUETAS EXISTENTES */
        Tag t1 = new Tag(ETIQUETA_1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t1);
        tagRepository.save(t2);

        /* PRIMERO VEMOS LAS ETIQUETAS EXISTENTES */
        ResponseEntity<List<TagDto>> tagsDto = tagService.getAllTags();
        Assert.assertEquals(2, tagsDto.getBody().size());
        Assert.assertEquals(HttpStatus.OK, tagsDto.getStatusCode());

        /* AHORA UN USUARIO AÑADIRÁ NUEVAS ETIQUETAS (UNA NUEVA Y OTRA EXISTENTE) AL EDITAR SU PERFIL */
        UserProfile luisin = new UserProfile(LUISIN, LUISIN_MAIL, LUISIN, LUISIN_PHONE, "");
        userProfileRepository.save(luisin);
        TagDto t3 = new TagDto(ETIQUETA_2);
        TagDto t4 = new TagDto(ETIQUETA_4);
        List<TagDto> tags = new ArrayList<>(5);
        tags.add(t3);
        tags.add(t4);
        UserProfileInfoDto updatedDto = new UserProfileInfoDto(null, null, null, LUISIN_PHONE, null, tags);
        userProfileService.updateUserInfo(LUISIN, updatedDto);

        /* BUSCAMOS UNA ETIQUETA EXISTENTE PERO NO DEL USUARIO */
        ResponseEntity<List<UserProfileInfoDto>> users = tagService.getUsersByTag(ETIQUETA_1);
        Assert.assertEquals(0, users.getBody().size());
        Assert.assertEquals(HttpStatus.OK, users.getStatusCode());

        /* BUSCAMOS UNA ETIQUETA EXISTENTE DEL USUARIO */
        ResponseEntity<List<UserProfileInfoDto>> users2 = tagService.getUsersByTag(ETIQUETA_4);
        Assert.assertEquals(1, users2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, users2.getStatusCode());
        Assert.assertEquals(LUISIN, users2.getBody().get(0).getLogin());

        UserProfile alonsin = new UserProfile(ALONSIN, ALONSIN_MAIL, ALONSIN, ALONSIN_PHONE, "");
        userProfileRepository.save(alonsin);
        List<TagDto> tags2 = new ArrayList<>(5);
        tags2.add(new TagDto(t1.getTagName()));
        tags2.add(t4);
        UserProfileInfoDto updatedDto2 = new UserProfileInfoDto(null, null, null, ALONSIN_PHONE, null, tags2);
        userProfileService.updateUserInfo(ALONSIN, updatedDto2);

        ResponseEntity<List<UserProfileInfoDto>> users3 = tagService.getUsersByTag(ETIQUETA_4);
        Assert.assertEquals(2, users3.getBody().size());
        Assert.assertEquals(HttpStatus.OK, users3.getStatusCode());
        Assert.assertEquals(ALONSIN, users3.getBody().get(0).getLogin());
        Assert.assertEquals(LUISIN, users3.getBody().get(1).getLogin());
    }

    @Test
    public void getAllPostsByTagNotFoundTest() {
        ResponseEntity<List<PostDto>> posts = tagService.getPostsByTag(ETIQUETA_1);
        Assert.assertEquals(null, posts.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, posts.getStatusCode());
    }

    @Test
    public void getAllPostsByTagNameTest() {
        UserProfile alonsin = new UserProfile(ALONSIN, ALONSIN_MAIL, ALONSIN, ALONSIN_PHONE, "");
        userProfileRepository.save(alonsin);

        Tag t1 = new Tag(ETIQUETA_1);
        tagRepository.save(t1);
        Tag t2 = new Tag(ETIQUETA_2);
        tagRepository.save(t2);
        Tag t3 = new Tag(ETIQUETA_3);
        tagRepository.save(t3);
        Tag t4 = new Tag(ETIQUETA_4);
        tagRepository.save(t4);

        Set<Tag> tags1 = new HashSet<>();
        tags1.add(t1);
        tags1.add(t2);

        Set<Tag> tags2 = new HashSet<>();
        tags2.add(t2);
        tags2.add(t3);

        Set<Tag> tags3 = new HashSet<>();
        tags3.add(t3);
        tags3.add(t4);

        Discussion d1 = new Discussion(STRING_1, alonsin, STRING_1, tags1);
        discussionRepository.save(d1);
        Discussion d2 = new Discussion(STRING_2, alonsin, STRING_1, tags2);
        discussionRepository.save(d2);
        Discussion d3 = new Discussion(STRING_3, alonsin, STRING_1, tags3);
        discussionRepository.save(d3);

        ResponseEntity<List<PostDto>> response1 = tagService.getPostsByTag(ETIQUETA_1);
        Assert.assertEquals(1, response1.getBody().size());
        Assert.assertEquals(HttpStatus.OK, response1.getStatusCode());
        Assert.assertEquals(d1.getTitle(), response1.getBody().get(0).getTitle());
        Assert.assertEquals(d1.getDescription(), response1.getBody().get(0).getDescription());

        ResponseEntity<List<PostDto>> response2 = tagService.getPostsByTag(ETIQUETA_4);
        Assert.assertEquals(1, response2.getBody().size());
        Assert.assertEquals(HttpStatus.OK, response2.getStatusCode());
        Assert.assertEquals(d3.getTitle(), response2.getBody().get(0).getTitle());
        Assert.assertEquals(d3.getDescription(), response2.getBody().get(0).getDescription());

        ResponseEntity<List<PostDto>> response3 = tagService.getPostsByTag(ETIQUETA_2);
        Assert.assertEquals(2, response3.getBody().size());
        Assert.assertEquals(HttpStatus.OK, response3.getStatusCode());
        Assert.assertEquals(d1.getTitle(), response3.getBody().get(0).getTitle());
        Assert.assertEquals(d1.getDescription(), response3.getBody().get(0).getDescription());
        Assert.assertEquals(d2.getTitle(), response3.getBody().get(1).getTitle());
        Assert.assertEquals(d2.getDescription(), response3.getBody().get(1).getDescription());
    }

}