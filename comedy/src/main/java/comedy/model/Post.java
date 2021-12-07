package comedy.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
@ToString
@NoArgsConstructor
public class Post {
    @Id
    private String id;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_attachment",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_image", referencedColumnName = "image"))
    private List<Attachment> attachments;
    @Column(name = "additional_attachments_count")
    private int additionalAttachmentsCount;
    @Column(columnDefinition = "TEXT")
    private String text;
    @Column(name = "open_post", columnDefinition = "")
    private boolean openPost;
    @Column(name = "post_type")
    private String postType;
    private Long date;
    @Column(name = "feed_pixel")
    private String feedPixel;
    @Column(name = "post_pixel")
    private String postPixel;
    @Column(name = "disable_comments")
    private boolean disableComments;
    @Column(name = "button_text")
    private String buttonText;
    @Column(name = "button_link")
    private String buttonLink;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voting_id", columnDefinition = "Integer")
    private Voting voting;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "title")
    private Header header;


}
