package comedy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@ToString
@NoArgsConstructor
public class Attachment {
    @Id
    private String image;
    @Column(name = "image_width")
    private int imageWidth;
    @Column(name = "image_height")
    private int imageHeight;
    @Column(columnDefinition = "TEXT")
    private String base64;
    @Column(name = "aspect_ratio")
    private double aspectRatio;
    @Column(name = "embedded_video")
    private String embeddedVideo;
    @Column(name = "video_id")
    private String videoId;
    @Column(name = "video_type")
    private String videoType;
    private String type;
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "attachments")
    @JsonIgnore
    private List<Post> posts;

}
