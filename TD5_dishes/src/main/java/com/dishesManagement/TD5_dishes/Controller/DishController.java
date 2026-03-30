package com.dishesManagement.TD5_dishes.Controller;

import com.dishesManagement.TD5_dishes.Entity.Dish;
import com.dishesManagement.TD5_dishes.Repository.Dish.FindAllDishesRepository;
import com.dishesManagement.TD5_dishes.Repository.Dish.FindDishByIdRepository;
import com.dishesManagement.TD5_dishes.Repository.Dish.SaveDishRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final SaveDishRepository saveDishRepository;
    private final FindAllDishesRepository findAllDishesRepository;
    private final FindDishByIdRepository findDishByIdRepository;

    public DishController(SaveDishRepository saveDishRepository,
                          FindAllDishesRepository findAllDishesRepository,
                          FindDishByIdRepository findDishByIdRepository) {
        this.saveDishRepository = saveDishRepository;
        this.findAllDishesRepository = findAllDishesRepository;
        this.findDishByIdRepository = findDishByIdRepository;
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
}