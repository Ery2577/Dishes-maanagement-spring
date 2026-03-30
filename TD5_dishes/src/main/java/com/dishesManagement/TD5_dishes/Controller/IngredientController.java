package com.dishesManagement.TD5_dishes.Controller;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Entity.StockValue;
import com.dishesManagement.TD5_dishes.Repository.Ingredient.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final SaveIngredientRepository saveIngredientRepository;
    private final FindAllIngredientsRepository findAllIngredientsRepository;
    private final FindIngredientByIdRepository findIngredientByIdRepository;
    private final CreateIngredientsRepository createIngredientsRepository;
    private final GetStockValueRepository getStockValueRepository;

    public IngredientController(SaveIngredientRepository saveIngredientRepository,
                                FindAllIngredientsRepository findAllIngredientsRepository,
                                FindIngredientByIdRepository findIngredientByIdRepository,
                                CreateIngredientsRepository createIngredientsRepository,
                                GetStockValueRepository getStockValueRepository) {
        this.saveIngredientRepository = saveIngredientRepository;
        this.findAllIngredientsRepository = findAllIngredientsRepository;
        this.findIngredientByIdRepository = findIngredientByIdRepository;
        this.createIngredientsRepository = createIngredientsRepository;
        this.getStockValueRepository = getStockValueRepository;
    }

    @PutMapping
    public Ingredient saveIngredient(@RequestBody Ingredient ingredient) {
        return saveIngredientRepository.execute(ingredient);
    }

    @GetMapping
    public List<Ingredient> findAll() {
        return findAllIngredientsRepository.execute();
    }

    @GetMapping("/{id}")
    public Ingredient findById(@PathVariable Integer id) {
        return findIngredientByIdRepository.execute(id);
    }

    @PostMapping("/batch")
    public List<Ingredient> createIngredients(@RequestBody List<Ingredient> ingredients) {
        return createIngredientsRepository.execute(ingredients);
    }

    @GetMapping("/{id}/stock")
    public StockValue getStockValue(@PathVariable Integer id,
                                    @RequestParam(required = false) Instant t) {
        Instant timestamp = (t != null) ? t : Instant.now();
        return getStockValueRepository.execute(timestamp, id);
    }
}