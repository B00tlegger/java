package bootlegger.tacocloud.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import java.util.Date;
import java.util.List;

@Data
public class Taco {
    private long id;
    private Date createAt;
    @NotNull
    @Size(min = 5, message = "Name must be al least 5 characters")
    private String name;
    @NotNull(message = "You must choose at least 1 ingredient")
    @Size(min = 1, message = "You must choose at least 1 ingredient")
    private List<Ingredient> ingredients;
}
