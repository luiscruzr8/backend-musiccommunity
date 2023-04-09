package es.tfg.musiccommunity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.tfg.musiccommunity.service.AnnouncementService;
import es.tfg.musiccommunity.service.DiscussionService;
import es.tfg.musiccommunity.service.EventService;
import es.tfg.musiccommunity.service.ImgPostService;
import es.tfg.musiccommunity.service.OpinionService;
import es.tfg.musiccommunity.service.PostService;
import es.tfg.musiccommunity.service.dto.AnnouncementDto;
import es.tfg.musiccommunity.service.dto.CommentDto;
import es.tfg.musiccommunity.service.dto.DiscussionDto;
import es.tfg.musiccommunity.service.dto.EventDto;
import es.tfg.musiccommunity.service.dto.OpinionDto;
import es.tfg.musiccommunity.service.dto.PostDto;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ImgPostService imgPostService;

    @Autowired
    private EventService eventService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private OpinionService opinionService;

    @GetMapping("user")
    public ResponseEntity<List<PostDto>> getUserPosts(@RequestParam(value="login", required=true) String param1, 
            @RequestParam(value="type", required=false) String param2, @RequestParam(value="keyword", required=false) String param3){
        String login = param1;
        String type = (param2 == null || param2.isEmpty()) ? "" : param2;
        String keyword = (param3 == null || param3.isEmpty()) ? "" : param3;
        return postService.getUserPosts(login, type, keyword);
    }

    @GetMapping("geo")
    public ResponseEntity<List<PostDto>> getPostsNearby(@RequestParam(value="latitude", required=true) double param1, 
            @RequestParam(value="longitude", required=true) double param2, @RequestParam(value="closest", required=true) boolean param3){
        return postService.getPostsNearby(param1, param2, param3);
    }
    
    @GetMapping("city")
    public ResponseEntity<List<PostDto>> getCityPosts(@RequestParam(value="cityName", required=true) String param1){
        return postService.getCityPosts(param1);
    }

    @GetMapping("{id}/img")
    public ResponseEntity<Resource> getImgPost(@PathVariable Long id){
        return imgPostService.getImgFile(id);
    }

    @PostMapping("{id}/img")
    public ResponseEntity<Long> storeImgPost(Authentication auth, @PathVariable Long id, @RequestParam("img") MultipartFile img){
        String login = auth.getName();
        return imgPostService.storeImgPost(img, login, id);
    }

    /********************************* EVENTS *********************************/

    @GetMapping("events")
    public ResponseEntity<List<EventDto>> getEvents(@RequestParam(value="keyword", required=false) String param) {
        String keyword = (param == null || param.isEmpty()) ? "" : param;
        return eventService.getAllEvents(keyword);
    }

    /* OJO, LOS IDS DE OTRO TIPO DE POST (QUE NO SEAN DE EVENTOS), DARAN UN ERROR 500 */
    @GetMapping("events/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id){
        return  eventService.getEventInfo(id);
    }

    @PostMapping("events")
    public ResponseEntity<Long> createEvent(Authentication auth, @RequestBody EventDto eventDto){
        String login = auth.getName();
        return eventService.createEvent(login, eventDto);
    }
     
    @PutMapping("events/{id}")
    public ResponseEntity<Long> updateEvent(Authentication auth, @RequestBody EventDto eventDto, @PathVariable Long id){
        String login = auth.getName();
        return eventService.updateEvent(login, eventDto, id);
    }

    @DeleteMapping("events/{id}")
    public ResponseEntity<Void> deleteEvent(Authentication auth, @PathVariable Long id){
        String login = auth.getName();
        return eventService.deleteEvent(login, id);
    }

    /********************************* ANNOUNCEMENTS *********************************/

    @GetMapping("announcements")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncements(@RequestParam(value="keyword", required=false) String param) {
        String keyword = (param == null || param.isEmpty()) ? "" : param;
        return announcementService.getAllAnnouncements(keyword);
    }

    /* OJO, LOS IDS DE OTRO TIPO DE POST (QUE NO SEAN DE ANUNCIO), DARAN UN ERROR 500 */
    @GetMapping("announcements/{id}")
    public ResponseEntity<AnnouncementDto> getAnnouncement(@PathVariable Long id){
        return  announcementService.getAnnouncementInfo(id);
    }

    @PostMapping("announcements")
    public ResponseEntity<Long> createAnnouncement(Authentication auth, @RequestBody AnnouncementDto announcementDto){
        String login = auth.getName();
        return announcementService.createAnnouncement(login, announcementDto);
    }
    
    @PutMapping("announcements/{id}")
    public ResponseEntity<Long> updateAnnouncement(Authentication auth, @RequestBody AnnouncementDto announcementDto, @PathVariable Long id){
        String login = auth.getName();
        return announcementService.updateAnnouncement(login, announcementDto, id);
    }

    @DeleteMapping("announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(Authentication auth, @PathVariable Long id){
        String login = auth.getName();
        return announcementService.deleteAnnouncement(login, id);
    }

    /********************************* DISCUSSIONS *********************************/

    @GetMapping("discussions")
    public ResponseEntity<List<DiscussionDto>> getDiscussions(@RequestParam(value="keyword", required=false) String param) {
        String keyword = (param == null || param.isEmpty()) ? "" : param;
        return discussionService.getAllDiscussions(keyword);
    }

    /* OJO, LOS IDS DE OTRO TIPO DE POST (QUE NO SEAN DE DISCUSION), DARAN UN ERROR 500 */
    @GetMapping("discussions/{id}")
    public ResponseEntity<DiscussionDto> getDiscussion(@PathVariable Long id){
        return  discussionService.getDiscussionInfo(id);
    }

    @PostMapping("discussions")
    public ResponseEntity<Long> createDiscussion(Authentication auth, @RequestBody DiscussionDto discussionDto){
        String login = auth.getName();
        return discussionService.createDiscussion(login, discussionDto);
    }

    @PutMapping("discussions/{id}")
    public ResponseEntity<Long> updateDiscussion(Authentication auth, @RequestBody DiscussionDto discussionDto, @PathVariable Long id){
        String login = auth.getName();
        return discussionService.updateDiscussion(login, discussionDto, id);
    }

    @DeleteMapping("discussions/{id}")
    public ResponseEntity<Void> deleteDiscussion(Authentication auth, @PathVariable Long id){
        String login = auth.getName();
        return discussionService.deleteDiscussion(login, id);
    }

    /********************************* OPINIONS *********************************/

    @GetMapping("opinions")
    public ResponseEntity<List<OpinionDto>> getOpinions(@RequestParam(value="keyword", required=false) String param) {
        String keyword = (param == null || param.isEmpty()) ? "" : param;
        return opinionService.getAllOpinions(keyword);
    }

    /* OJO, LOS IDS DE OTRO TIPO DE POST (QUE NO SEAN DE OPINION), DARAN UN ERROR 500 */
    @GetMapping("opinions/{id}")
    public ResponseEntity<OpinionDto> getOpinion(@PathVariable Long id){
        return opinionService.getOpinionInfo(id);
    }

    @PostMapping("opinions")
    public ResponseEntity<Long> createOpinion(Authentication auth, @RequestBody OpinionDto opinionDto){
        String login = auth.getName();
        return opinionService.createOpinion(login, opinionDto);
    }
    
    @PutMapping("opinions/{id}")
    public ResponseEntity<Long> updateOpinion(Authentication auth, @RequestBody OpinionDto opinionDto, @PathVariable Long id){
        String login = auth.getName();
        return opinionService.updateOpinion(login, opinionDto, id);
    }

    @DeleteMapping("opinions/{id}")
    public ResponseEntity<Void> deleteOpinion(Authentication auth, @PathVariable Long id){
        String login = auth.getName();
        return opinionService.deleteOpinion(login, id);
    }

    /********************************* COMMENTS *********************************/

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getPostComments(@PathVariable Long postId){
        return postService.getPostComments(postId);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Long> getMakeComment(@PathVariable Long postId, @RequestParam(value="responseTo", required=false) Long responseTo,
            Authentication auth, @RequestBody CommentDto newComment){
        String login = auth.getName();
        if (responseTo == null) {
            return postService.makeComment(login, postId, newComment);
        } else {
            return postService.makeResponseComment(login, postId, responseTo, newComment);
        }
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(Authentication auth, @PathVariable Long commentId){
        String login = auth.getName();
        return postService.deleteComment(login, commentId);
    }

}
