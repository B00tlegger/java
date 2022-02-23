package bootlegger.tacocloud.controllers;

import bootlegger.tacocloud.model.Ingredient;
import bootlegger.tacocloud.model.Order;
import bootlegger.tacocloud.model.Taco;
import bootlegger.tacocloud.repositories.IngredientRepository;
import bootlegger.tacocloud.repositories.TacoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static bootlegger.tacocloud.model.Ingredient.Type;

@Controller
@Slf4j
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
    private IngredientRepository ingredientRepository;
    private TacoRepository tacoRepository;

    public DesignTacoController (IngredientRepository ingredientRepository, TacoRepository tacoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.tacoRepository = tacoRepository;
    }

//    @ModelAttribute
//    public void addIngredientsToModel (Model model) {
//
//    }

    @ModelAttribute(name = "order")
    public Order order () {
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco () {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm (Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.addAll((Collection<? extends Ingredient>) ingredientRepository.findAll());
        Type[] types = Ingredient.Type.values();
        for(Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
        model.addAttribute("design", new Taco());
        return "design";
    }

    @PostMapping
    public String processDesign (@Valid Taco design, Errors errors,
                                 @ModelAttribute Order order) {
        if(errors.hasErrors()) {
            return "design";
        }
        Taco taco = tacoRepository.save(design);
        order.addTaco(taco);
        log.info("Processing design: " + design);
        return "redirect:/orders/current";
    }

    public List<Ingredient> filterByType (List<Ingredient> ingredientList, Type type) {
        return ingredientList.stream().filter(ingredient -> ingredient.getType().equals(type))
                             .collect(Collectors.toList());
    }
}
