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
public class Option {
    @Id
    private int id;
    private String title;
    @Column(name = "aspect_ratio")
    private double aspectRatio;
    @Column(name = "view_order")
    private int viewOrder;
    @ManyToMany(mappedBy = "options", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Voting> votingList;
}
