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
public class Voting {
    @Id
    private int id;
    private String status;
    private boolean sorted;
    @JsonIgnore
    @OneToMany(mappedBy = "voting", fetch = FetchType.EAGER)
    private List<Post> posts;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "voting_option",
            joinColumns = @JoinColumn(name = "voting_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "option_id", referencedColumnName = "id"))
    private List<Option> options;
}
