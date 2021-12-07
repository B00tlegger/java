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
public class Header {
    @Id
    private String title;
    @Column(name = "avatarImage")
    private String avatarImage;
    @Column(name = "header_image")
    private String headerImage;
    @OneToMany(mappedBy = "header", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Post> posts;
}
