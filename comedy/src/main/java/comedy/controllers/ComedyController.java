package comedy.controllers;

import comedy.dto.PostDTO;
import comedy.model.Post;
import comedy.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class ComedyController {
    @Autowired
    private PostsService postsService;
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/getPosts")
    public ResponseEntity savePosts(){
        try {
            URI url = new URI("https", "tnt-club.com", "/api/mobile/feed/main", null);
            new Thread(()->{
                for (Post post : restTemplate.getForEntity(url, PostDTO.class).getBody().getPosts()) {
                    postsService.savePost(post);
                }
            }).start();

        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Post> getAllPosts() throws URISyntaxException {

        return postsService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id){
        return postsService.getById(id) != null
                ? ResponseEntity.ok(postsService.getById(id))
                : ResponseEntity.notFound().build();
    }
}
