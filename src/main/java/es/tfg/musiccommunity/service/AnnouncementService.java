package es.tfg.musiccommunity.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.tfg.musiccommunity.model.Announcement;
import es.tfg.musiccommunity.model.City;
import es.tfg.musiccommunity.model.ImgPost;
import es.tfg.musiccommunity.model.Tag;
import es.tfg.musiccommunity.model.UserProfile;
import es.tfg.musiccommunity.repository.AnnouncementRepository;
import es.tfg.musiccommunity.repository.CityRepository;
import es.tfg.musiccommunity.repository.ImgPostRepository;
import es.tfg.musiccommunity.repository.UserProfileRepository;
import es.tfg.musiccommunity.service.dto.AnnouncementDto;
import es.tfg.musiccommunity.service.dto.TagDto;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ImgPostRepository imgPostRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private NotificationService notificationService;

    /* TODOS LOS ANUNCIOS */
    public ResponseEntity<List<AnnouncementDto>> getAllAnnouncements(String keyword) {
        List<Announcement> announcements;
        if (keyword.isEmpty()) {
            announcements = announcementRepository.findThemAllOrderByCreationDateTimeAsc();
        } else {
            announcements = announcementRepository.findThemAllByTitleOrderByCreationDateTimeAsc(keyword);
        }
        List<AnnouncementDto> announDtos = new ArrayList<>(25);
        for (Announcement ad : announcements) {
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> adTags = ad.getTags().stream().collect(Collectors.toList()) ;
            adTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
            for (Tag tag : adTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            announDtos.add(new AnnouncementDto(ad.getId(), ad.getTitle(), ad.getCreationDateTime(), ad.getType(),
                    ad.getCity().getName(), ad.getUser().getLogin(), ad.getDescription(), ad.getEndDate(),
                    ad.getContactPhone(),tags));
        }
        return new ResponseEntity<>((announDtos), HttpStatus.OK);
    }

    /* INFORMACION DE UN ANUNCIO POR ID */
    public ResponseEntity<AnnouncementDto> getAnnouncementInfo(Long id) {
        Optional<Announcement> an = announcementRepository.findById(id);
        if (!an.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            Announcement announcement = an.get();
            List<TagDto> tags = new ArrayList<>(5);
            List<Tag> announcementTags = announcement.getTags().stream().collect(Collectors.toList()) ;
            announcementTags.sort((a,b) -> a.getTagName().compareTo(b.getTagName()));
                for (Tag tag : announcementTags) {
                tags.add(new TagDto(tag.getTagName()));
            }
            AnnouncementDto anDto = new AnnouncementDto(announcement.getId(), announcement.getTitle(), announcement.getCreationDateTime(),
                announcement.getType(), announcement.getCity().getName(), announcement.getUser().getLogin(),
                announcement.getDescription(), announcement.getEndDate(), announcement.getContactPhone(),tags);
            return new ResponseEntity<>((anDto), HttpStatus.OK);
        }
    }
    

    /* CREACIÓN DE UN ANUNCIO */
    @Transactional
    public ResponseEntity<Long> createAnnouncement(String login, AnnouncementDto anData) {
        Set<Tag> tags;
        /* COMPROBAMOS QUE LA FECHA DE FIN SEA POSTERIOR O IGUAL A HOY */
        /* "FECHA-FIN-ANTES-DE-AHORA */
        if (anData.getEndDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        String pattern = "\\d{9}";
        if (!anData.getContactPhone().matches(pattern)){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        Optional<City> city = cityRepository.findByName(anData.getCityName());
        if (user.isPresent() && city.isPresent()) {
            tags = commonService.handleTags(anData.getTags());
            Announcement announce = new Announcement(anData.getTitle(), city.get(), user.get(), anData.getDescription(),
            anData.getEndDate(), anData.getContactPhone(),tags);
            /* Guardamos el anuncio */
            announce = announcementRepository.save(announce);
            notificationService.notifyNewPost(user.get(), user.get().getFollowers(), announce);
            return new ResponseEntity<>((announce.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /* ACTUALIZACIÓN DE UN ANUNCIO */
    @Transactional
    public ResponseEntity<Long> updateAnnouncement(String login, AnnouncementDto updatedAnnounce,
            Long advertId) {
        Set<Tag> tags;
        String pattern = "\\d{9}";
        /* COMPROBAMOS QUE LA FECHA DE FIN SEA POSTERIOR O IGUAL A HOY */
        /* "FECHA-FIN-ANTES-DE-AHORA */                             /* TELEFONO INVALIDO */
        if (updatedAnnounce.getEndDate().isBefore(LocalDate.now()) || !updatedAnnounce.getContactPhone().matches(pattern)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Optional<Announcement> announceToUpdate = announcementRepository.findById(advertId);
        if (announceToUpdate.isPresent()) {
            /* COMPROBAMOS QUE QUIEN ACTUALIZA SEA EL MISMO CREADOR */
            Optional<UserProfile> user = userProfileRepository.findByLogin(login);
            if (user.isPresent()) {
                if (!announceToUpdate.get().getUser().equals(user.get())) {
                    return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
                }
                Announcement announce = announceToUpdate.get();
                /* BUSCAMOS LA CIUDAD A ACTUALIZAR (SUPONEMOS QUE NO SEA NINGUNA QUE NO ESTÉ EN  NUESTRO SELECTOR) */
                Optional<City> city = cityRepository.findByName(updatedAnnounce.getCityName());
                if (city.isPresent()) {
                    tags = commonService.handleTags(updatedAnnounce.getTags());
                    announce.setTitle(updatedAnnounce.getTitle());
                    announce.setCity(city.get());
                    announce.setDescription(updatedAnnounce.getDescription());
                    announce.setEndDate(updatedAnnounce.getEndDate());
                    announce.setContactPhone(updatedAnnounce.getContactPhone());
                    announce.setTags(tags);
                    announce = announcementRepository.save(announce);

                    return new ResponseEntity<>((announce.getId()), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
    }

    /* BORRADO DE UN ANUNCIO */
    @Transactional
    public ResponseEntity<Void> deleteAnnouncement(String login, Long advertId) {
        /* PRIMERO SE BUSCA EL ANUNCIO */
        Optional<Announcement> announce = announcementRepository.findById(advertId);
        if (!announce.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        }
        /* SE COMPRUEBA QUE EL USUARIO SEA SU CREADOR */
        Optional<UserProfile> user = userProfileRepository.findByLogin(login);
        if (!user.isPresent()) {
            return new ResponseEntity<>((null), HttpStatus.NOT_FOUND);
        } else {
            if (!user.get().equals(announce.get().getUser())) {
                return new ResponseEntity<>((null), HttpStatus.UNAUTHORIZED);
            }
            /* BORRAMOS IMAGEN */
            Optional<ImgPost> imgPost = imgPostRepository.findByPost(announce.get());
            if (imgPost.isPresent()) {
                imgPostRepository.delete(imgPost.get());
            }
            /* BORRAMOS NOTIFICACIONES */
            commonService.deleteNotificationsOfPost(announce.get());
            /* BORRAMOS COMENTARIOS */
            commonService.deleteComments(announce.get());
            /* BORRAMOS RECOMENDACIONES */
            commonService.deleteRecommendations(announce.get());
            /* SE BORRA EL ANUNCIO */
            announcementRepository.delete(announce.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
