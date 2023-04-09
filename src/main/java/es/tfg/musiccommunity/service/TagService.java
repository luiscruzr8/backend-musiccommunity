package es.tfg.musiccommunity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.PostRepository;
import es.tfg.musiccommunity.repository.TagRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.TagDto;
import es.tfg.musiccommunity.service.dto.UserProfileInfoDto;

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public ResponseEntity<List<TagDto>> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        tags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
        List<TagDto> tagsDto = new ArrayList<>(25);
        for (Tag tag : tags) {
            tagsDto.add(new TagDto(tag.getTagName()));
        }
        return new ResponseEntity<>((tagsDto), HttpStatus.OK);
    }

    public ResponseEntity<List<UserProfileInfoDto>> getUsersByTag(String tagName) {
        Optional<Tag> optTag = tagRepository.findByTagName(tagName);
        if (!optTag.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        Tag tag = optTag.get();
        List<UserProfile> users = userProfileRepository.findAllByInterestsOrderByLoginAsc(tag);
        List<UserProfileInfoDto> usersDto = new ArrayList<>(25);
        for (UserProfile u : users) {
            List<TagDto> interests = new ArrayList<>(5);
            List<Tag> userInterests = u.getInterests().stream().collect(Collectors.toList());
            userInterests.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag aux : userInterests) {
                interests.add(new TagDto(aux.getTagName()));
            }
            usersDto.add(new UserProfileInfoDto(u.getId(), u.getLogin(), u.getEmail(), u.getPhone(), 
                u.getBio(), interests));
        }
        return new ResponseEntity<>((usersDto), HttpStatus.OK);
    }

    public ResponseEntity<List<PostDto>> getPostsByTag(String tagName) {
        Optional<Tag> optTag = tagRepository.findByTagName(tagName);
        if (!optTag.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        Tag tag = optTag.get();
        List<Post> posts = postRepository.findAllByTagsOrderByCreationDateTimeAsc(tag);
        List<PostDto> postsDto = new ArrayList<>(25);
        for (Post p : posts) {
            List<TagDto> tagsDto = new ArrayList<>(5);
            List<Tag> tags = p.getTags().stream().collect(Collectors.toList());
            tags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag aux : tags) {
                tagsDto.add(new TagDto(aux.getTagName()));
            }
            postsDto.add(new PostDto(p.getId(), p.getTitle(), p.getCreationDateTime(), p.getType(), p.getUser().getLogin(), p.getDescription(), tagsDto));
        }
        return new ResponseEntity<>((postsDto), HttpStatus.OK);
    }
}
