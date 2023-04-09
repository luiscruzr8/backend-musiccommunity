package es.tfg.musiccommunity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.tfg.musiccommunity.service.TagService;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.TagDto;
import es.tfg.musiccommunity.service.dto.UserProfileInfoDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("")
    public ResponseEntity<List<TagDto>> getAllTags() {
        return tagService.getAllTags();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileInfoDto>> getAllUsersByTagName(@RequestParam(value="tagName", required=true) String tagName) {
        return tagService.getUsersByTag(tagName);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostDto>> getAllPostsByTagName(@RequestParam(value="tagName", required=true) String tagName) {
        return tagService.getPostsByTag(tagName);
    }
}
