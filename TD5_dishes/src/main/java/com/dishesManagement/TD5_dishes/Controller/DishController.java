package com.dishesManagement.TD5_dishes.Controller;

import com.dishesManagement.TD5_dishes.Entity.Dish;
import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Repository.Dish.FindAllDishesRepository;
import com.dishesManagement.TD5_dishes.Repository.Dish.FindDishByIdRepository;
import com.dishesManagement.TD5_dishes.Repository.Dish.SaveDishRepository;
import com.dishesManagement.TD5_dishes.Repository.Ingredient.FindIngredientsByDishIdAndFiltersRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final SaveDishRepository saveDishRepository;
    private final FindAllDishesRepository findAllDishesRepository;
    private final FindDishByIdRepository findDishByIdRepository;
    private final FindIngredientsByDishIdAndFiltersRepository findIngredientsRepo;

    public DishController(SaveDishRepository saveDishRepository,
                          FindAllDishesRepository findAllDishesRepository,
                          FindDishByIdRepository findDishByIdRepository,
                          FindIngredientsByDishIdAndFiltersRepository findIngredientsRepo) {
        this.saveDishRepository = saveDishRepository;
        this.findAllDishesRepository = findAllDishesRepository;
        this.findDishByIdRepository = findDishByIdRepository;
        this.findIngredientsRepo = findIngredientsRepo;
    }

    @PutMapping
    public Dish saveDish(@RequestBody Dish dish) {
        return saveDishRepository.execute(dish);
    }

    @GetMapping
    public List<Dish> findAll() {
        return findAllDishesRepository.execute();
    }

    @GetMapping("/{id}")
    public Dish findById(@PathVariable Integer id) {
        return findDishByIdRepository.execute(id);
    }

    @GetMapping("/{id}/ingredients")
    public List<Ingredient> findIngredientsByDish(
            @PathVariable Integer id,
            @RequestParam(required = false) String ingredientName) {
        return findIngredientsRepo.execute(id, ingredientName, );
    }

    @GetMapping("/{id}/ingredients")
    public List<Ingredient> findIngredientsByDish(
            @PathVariable Integer id,
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) Double ingredientPriceAround) {

        Dish dish = findDishByIdRepository.execute(id);
        if (dish == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Dish.id=" + id + " is not found"
            );
        }

        return findIngredientsRepo.execute(id, ingredientName, );
    }
}