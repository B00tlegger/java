package comedy.services;

import comedy.model.*;
import comedy.reposytories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostsService {
    private AttachmentRepository attachmentRepository;
    private HeaderRepository headerRepository;
    private OptionRepository optionRepository;
    private PostRepository postRepository;
    private VotingRepository votingRepository;

    @Autowired
    public PostsService(AttachmentRepository attachmentRepository,
                        HeaderRepository headerRepository,
                        OptionRepository optionRepository,
                        PostRepository postRepository,
                        VotingRepository votingRepository) {
        this.attachmentRepository = attachmentRepository;
        this.headerRepository = headerRepository;
        this.optionRepository = optionRepository;
        this.postRepository = postRepository;
        this.votingRepository = votingRepository;
    }

    public void savePost(Post post) {
        if (headerRepository.findById(post.getHeader().getTitle()).isEmpty()) {
            headerRepository.save(post.getHeader());
        }
        Voting voting = post.getVoting();
        if (voting != null) {
            if (votingRepository.findById(voting.getId()).isEmpty()) {
                for (Option option : post.getVoting().getOptions()) {
                    optionRepository.save(option);
                }
                votingRepository.save(voting);
            }
        }
        for (Attachment attachment : post.getAttachments()) {
            attachmentRepository.save(attachment);
        }
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return (List<Post>) postRepository.findAll();
    }

    @Cacheable(value = "posts", key = "#id")
    public Post getById(String id) {
        return postRepository.findById(id).isPresent()
                ? postRepository.findById(id).get()
                : null;
    }
}
