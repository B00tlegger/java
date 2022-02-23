package bootlegger.assemblers;

import bootlegger.tacocloud.controllers.DesignTacoController;
import bootlegger.tacocloud.controllers.IngredientController;
import bootlegger.tacocloud.model.Ingredient;
import bootlegger.tacocloud.model.Taco;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class IngredientModelAssembler
        implements RepresentationModelAssembler<Ingredient, EntityModel<IngredientModel>> {
    @Override
    public EntityModel<IngredientModel> toModel (Ingredient entity) {
        IngredientModel ingredientModel = new IngredientModel(entity);
        return EntityModel.of(ingredientModel,
                              linkTo(methodOn(IngredientController.class).findById(entity.getId())).withSelfRel());
    }


    public CollectionModel<EntityModel<IngredientModel>> toCollectionModel (List<Ingredient> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }

    public List<EntityModel<IngredientModel>> toListModel (List<Ingredient> entities) {
        List<EntityModel<IngredientModel>> ingredients =
                entities.stream().map(this::toModel).collect(
                        Collectors.toList());
        return ingredients;
    }

}
