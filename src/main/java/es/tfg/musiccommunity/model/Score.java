package es.tfg.musiccommunity.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "score_name")
    private String scoreName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "upload_date_time")
    private LocalDateTime uploadDateTime;

    @Column(name = "is_public")
    private boolean isPublic;

    @Lob
    private byte[] data; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserProfile user;

    public Score() {}

    public Score(String scoreName, String fileType, byte[] data, UserProfile user) {
        this.scoreName = scoreName;
        this.fileType = fileType;
        this.data = data;
        this.user = user;
        this.uploadDateTime = LocalDateTime.now();
        this.isPublic = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

}
