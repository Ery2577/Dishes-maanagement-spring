package com.dishesManagement.TD5_dishes.Repository.Service;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Repository.Ingredient.FindIngredientsByDishIdAndFiltersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    private final FindIngredientsByDishIdAndFiltersRepository findIngredientsRepository;

    public DishService(FindIngredientsByDishIdAndFiltersRepository findIngredientsRepository) {
        this.findIngredientsRepository = findIngredientsRepository;
    }

    public List<Ingredient> getIngredientsByDish(Long dishId, String name) {
        return findIngredientsRepository.findByDishIdAndFilters(dishId, name);
    }
}