package bootlegger.assemblers;

import bootlegger.tacocloud.model.Ingredient;
import bootlegger.tacocloud.model.Taco;
import lombok.Data;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Relation(value = "taco", collectionRelation = "tacos")
public class TacoModel {

    private Date createdAt;
    private String name;
    private List<EntityModel<IngredientModel>> ingredients;

    public TacoModel (Taco taco) {
        this.createdAt = taco.getCreatedAt();
        this.name = taco.getName();
        this.ingredients = new IngredientModelAssembler().toListModel(taco.getIngredients());
    }
}
