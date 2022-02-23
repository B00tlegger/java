package bootlegger.tacocloud.repositories;

import bootlegger.tacocloud.model.Ingredient;

public interface IngredientRepository {
    Iterable<Ingredient> findAll();
    Ingredient findOne(String id);
    Ingredient save (Ingredient ingredient);
}
