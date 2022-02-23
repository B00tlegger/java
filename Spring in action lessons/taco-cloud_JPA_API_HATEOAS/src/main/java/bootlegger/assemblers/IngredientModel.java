package bootlegger.assemblers;

import bootlegger.tacocloud.model.Ingredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
public class IngredientModel {
    private final String name;
    private final Ingredient.Type type;

    public IngredientModel (Ingredient ingredient) {
        this.name = ingredient.getName();
        this.type = ingredient.getType();
    }
}
