package bootlegger.tacocloud.controllers;

import bootlegger.assemblers.TacoModel;
import bootlegger.assemblers.TacoModelAssembler;
import bootlegger.tacocloud.model.Taco;
import bootlegger.tacocloud.repositories.TacoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequestMapping(value = "/design")
@CrossOrigin(origins = "*")
public class DesignTacoController {
    private TacoRepository tacoRepository;


    @Autowired
    public DesignTacoController (TacoRepository tacoRepository) {
        this.tacoRepository = tacoRepository;
    }

    @GetMapping("/recent")
    public CollectionModel<EntityModel<TacoModel>> recentTacos () {
        return new TacoModelAssembler().toCollectionModel(
                tacoRepository.findAll(PageRequest.of(0, 12, Sort.by("createdAt").descending())).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TacoModel>> tacoById (@PathVariable("id") long id) {
        Optional<Taco> taco = tacoRepository.findById(id);
        return taco.map(value -> new ResponseEntity<>(
                new TacoModelAssembler().toModel(taco.get()),
                HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco (@RequestBody Taco taco) {
        return tacoRepository.save(taco);
    }
}
