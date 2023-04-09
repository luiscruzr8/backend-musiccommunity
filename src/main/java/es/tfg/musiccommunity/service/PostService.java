package es.tfg.musiccommunity.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.Comment;
import es.tfg.musiccommunity.model.Post;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.CommentRepository;
import es.tfg.musiccommunity.repository.PostRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.CommentDto;
import es.tfg.musiccommunity.service.dto.PostDto;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class PostService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommonService commonService;

    /* TODOS LOS POSTS DE UN USUARIO POR SU LOGIN */
    public ResponseEntity<List<PostDto>> getUserPosts(String login, String type, String keyword) {
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        List<Post> posts;
        if (type.isEmpty() && keyword.isEmpty()) {
            posts = postRepository.findByUser(user.get());
        } else if (!type.isEmpty() && keyword.isEmpty()) {
            posts = postRepository.findByUserAndPostType(user.get(), type);
        } else if (type.isEmpty() && !keyword.isEmpty()) {
            posts = postRepository.findByUserAndTitle(user.get(),keyword);
        } else {
            posts = postRepository.findByUserAndTypeAndTitle(user.get(), type,keyword);
        }
        List<PostDto> postsDto = new ArrayList<>(25);
        for (Post post : posts) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> postTags = post.getTags().stream().collect(Collectors.toList()) ;
            postTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : postTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            postsDto.add(new PostDto(post.getId(), post.getTitle(), post.getCreationDateTime(), post.getType(),
                post.getUser().getLogin(), post.getDescription(),tags));
        }
        return new ResponseEntity<>((postsDto), HttpStatus.OK);
    }

    public ResponseEntity<List<PostDto>> getPostsNearby(double lat, double lon, boolean closest){
        List<Post> posts;
        if (closest) {
            City city = cityRepository.findClosestCity(lat,lon);
            posts = postRepository.findByClosestCity(city);
        } else {
            List<City> cities = cityRepository.findCloserCities(lat,lon);
            posts = postRepository.findByCityIn(cities);
        }
        List<PostDto> postsDto = new ArrayList<>(25);
        for (Post post : posts) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> postTags = post.getTags().stream().collect(Collectors.toList()) ;
            postTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : postTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            postsDto.add(new PostDto(post.getId(), post.getTitle(), post.getCreationDateTime(), post.getType(),
                post.getUser().getLogin(), post.getDescription(),tags));
        }
        return new ResponseEntity<>((postsDto), HttpStatus.OK);
    }

    public ResponseEntity<List<PostDto>> getCityPosts(String cityName){
        List<Post> posts;
        Optional<City> city = cityRepository.findByName(cityName);
        if (!city.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        City c = city.get();
        posts = postRepository.findByClosestCity(c);
        List<PostDto> postsDto = new ArrayList<>(25);
        for (Post post : posts) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> postTags = post.getTags().stream().collect(Collectors.toList()) ;
            postTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : postTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            postsDto.add(new PostDto(post.getId(), post.getTitle(), post.getCreationDateTime(), post.getType(),
                post.getUser().getLogin(), post.getDescription(),tags));
        }
        return new ResponseEntity<>((postsDto), HttpStatus.OK);
    }

    /* COMENTARIOS DE UN POST */
    public ResponseEntity<List<CommentDto>> getPostComments(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        List<Comment> comments = commentRepository.findByPostOrderByCommentDateDesc(post.get());
        List<CommentDto> commentsDto = new ArrayList<>(5);
        for (Comment comment : comments) {
            List<Comment> responsesToComment = commentRepository.findByResponseToOrderByCommentDateDesc(comment);
            List<CommentDto> responsesToCommentDto = new ArrayList<>(5);
            for (Comment response : responsesToComment) {
                responsesToCommentDto.add(new CommentDto(response.getId(), response.getCommentText(),
                        response.getCommentDate(), response.getUser().getLogin(), new ArrayList<>(1)));
            }
            commentsDto.add(new CommentDto(comment.getId(), comment.getCommentText(), comment.getCommentDate(),
                    comment.getUser().getLogin(), responsesToCommentDto));
        }
        return new ResponseEntity<>((commentsDto), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> makeComment(String login, Long postId, CommentDto comment) {
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (user.isPresent()) {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                Comment newComment = new Comment(user.get(), post.get(), comment.getCommentText());
                commentRepository.save(newComment);
                notificationService.notifyNewCommentedPost(user.get(), post.get().getUser(), post.get(), newComment);
                return new ResponseEntity<>((newComment.getId()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<Long> makeResponseComment(String login, Long postId, Long responseTo,
            CommentDto response) {
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (user.isPresent()) {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                Optional<Comment> responseToComment = commentRepository.findById(responseTo);
                if (responseToComment.isPresent()) {
                    Comment newResponse = new Comment(user.get(), post.get(), response.getCommentText(),
                            responseToComment.get());
                    commentRepository.save(newResponse);
                    notificationService.notifyNewCommentedPost(user.get(), post.get().getUser(), post.get(), newResponse);
                    return new ResponseEntity<>((newResponse.getId()), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<Void> deleteComment(String login, Long commentId) {
        /* PRIMERO SE BUSCA EL COMENTARIO */
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()){
            /* SE COMPRUEBA QUE EL USUARIO SEA SU CREADOR */
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) {
                if (!user.get().equals(comment.get().getUser())) {
                    return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
                }
                List<Comment> responsesToComment = commentRepository.findByResponseToOrderByCommentDateDesc(comment.get());
                for (Comment response : responsesToComment) {
                    commonService.deleteNotificationsOfComment(response);
                    commentRepository.delete(response);
                }
                commonService.deleteNotificationsOfComment(comment.get());
                /* SE BORRA EL COMENTARIO Y SUS RESPUESTAS */
                commentRepository.delete(comment.get()); 
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

}