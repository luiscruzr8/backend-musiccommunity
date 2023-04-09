package es.tfg.musiccommunity.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.model.ImgPost;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.ImgPostRepository;
import es.tfg.musiccommunity.repository.PostRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;

@Service
public class ImgPostService { 

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImgPostRepository imgPostRepository;

    public ResponseEntity<Long> storeImgPost(MultipartFile file, String login, Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Post p = post.get();
        String imgName = String.format("%s.%s", p.getId().toString(), StringUtils.getFilenameExtension(file.getOriginalFilename())); 
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserProfile u = user.get();
        if (u != p.getUser()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            // Check if the file's type is not image
            if(!file.getContentType().contains("image")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Optional<ImgPost> img = imgPostRepository.findByPost(p);
            if (img.isPresent()) {
                ImgPost imgPost = img.get();
                imgPostRepository.delete(imgPost);
            }
            ImgPost newImgPost = new ImgPost(file.getContentType(), imgName, file.getBytes(), u, p);
            imgPostRepository.save(newImgPost);
            return new ResponseEntity<>(newImgPost.getId(),HttpStatus.OK);

        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Resource> getImgFile(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Post p = post.get();
        Optional<ImgPost> img = imgPostRepository.findByPost(p);
        if (!img.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        ImgPost imgPost = img.get();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(imgPost.getFileType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imgPost.getImgName() + "\"")
            .body(new ByteArrayResource(imgPost.getData()));
    }

}