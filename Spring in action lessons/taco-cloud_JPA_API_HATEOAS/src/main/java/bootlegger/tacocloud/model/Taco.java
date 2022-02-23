package bootlegger.tacocloud.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Taco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date createdAt;
    @NotNull
    @Size(min = 5, message = "Name must be al least 5 characters")
    private String name;
    @NotNull(message = "You must choose at least 1 ingredient")
    @Size(min = 1, message = "You must choose at least 1 ingredient")
    @ManyToMany(targetEntity = Ingredient.class)
    private List<Ingredient> ingredients;

    @PrePersist
    public void setCreateAt () {
        this.createdAt = new Date();
    }
}
