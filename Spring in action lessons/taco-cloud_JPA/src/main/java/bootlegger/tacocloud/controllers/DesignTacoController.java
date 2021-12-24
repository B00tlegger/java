package bootlegger.tacocloud.controllers;

import bootlegger.tacocloud.model.Ingredient;
import bootlegger.tacocloud.model.Order;
import bootlegger.tacocloud.model.Taco;
import bootlegger.tacocloud.model.User;
import bootlegger.tacocloud.repositories.IngredientRepository;
import bootlegger.tacocloud.repositories.TacoRepository;
import bootlegger.tacocloud.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
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
    private UserRepository userRepository;

    @Autowired
    public DesignTacoController (IngredientRepository ingredientRepository, TacoRepository tacoRepository, UserRepository userRepository) {
        this.ingredientRepository = ingredientRepository;
        this.tacoRepository = tacoRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute(name = "order")
    public Order order () {
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco () {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm (Model model, Principal principal) {
        List<Ingredient> ingredients = (List<Ingredient>) ingredientRepository.findAll();
        Type[] types = Type.values();
        for(Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);
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
