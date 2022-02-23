package bootlegger.assemblers;

import bootlegger.tacocloud.controllers.DesignTacoController;
import bootlegger.tacocloud.model.Taco;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


public class TacoModelAssembler implements RepresentationModelAssembler<Taco, EntityModel<TacoModel>> {

    @Override
    public EntityModel<TacoModel> toModel (Taco entity) {
        TacoModel taco = new TacoModel(entity);
        return EntityModel.of(taco,
                              linkTo(methodOn(DesignTacoController.class).tacoById(entity.getId())).withSelfRel());
    }

    public CollectionModel<EntityModel<TacoModel>> toCollectionModel (List<Taco> entities) {
       List <EntityModel<TacoModel>> tacos = entities.stream()
                .map(this::toModel).collect(
                        Collectors.toList());
        return CollectionModel.of(tacos, linkTo(methodOn(DesignTacoController.class).recentTacos()).withRel("recents"));
    }
}
