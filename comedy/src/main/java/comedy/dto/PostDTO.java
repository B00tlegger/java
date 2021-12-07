package comedy.dto;

import comedy.model.Post;
import lombok.Data;

import java.util.List;
@Data
public class PostDTO {
    private List<Post> posts;
}
