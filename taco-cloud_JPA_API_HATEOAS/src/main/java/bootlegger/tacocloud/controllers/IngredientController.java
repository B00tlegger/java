package bootlegger.tacocloud.controllers;

import bootlegger.tacocloud.model.Ingredient;
import bootlegger.tacocloud.repositories.IngredientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/ingredients", produces = "application/json")
@CrossOrigin(origins = "*")
public class IngredientController {

    private IngredientRepository ingredientRepository;

    public IngredientController (IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping
    public Iterable<Ingredient> allIngredients () {
        return ingredientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> findById (@PathVariable String id) {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        return ingredient.map(value -> new ResponseEntity<>(ingredient.get(), HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
}
