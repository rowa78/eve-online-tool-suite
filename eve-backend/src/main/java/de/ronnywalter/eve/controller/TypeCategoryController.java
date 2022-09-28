package de.ronnywalter.eve.controller;

import de.ronnywalter.eve.dto.TypeCategoryDTO;
import de.ronnywalter.eve.model.TypeCategory;
import de.ronnywalter.eve.service.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("typecategories")
@RequiredArgsConstructor
@Slf4j
public class TypeCategoryController extends AbstractController{

    private final TypeService typeService;

    @GetMapping(value = "")
    public List<TypeCategoryDTO> getTypeCategories() {
        return mapList(typeService.getTypeCategories(), TypeCategoryDTO.class);
    }

    @GetMapping(value = "/{categoryId}")
    public TypeCategoryDTO getTypeCategory(@PathVariable int categoryId) {
        return map(typeService.getTypeCategory(categoryId), TypeCategoryDTO.class);
    }

    @PostMapping(value = "")
    public TypeCategoryDTO saveTypeCategory(@RequestBody TypeCategoryDTO typeDTO) {
        return map(typeService.saveTypeCategory(map(typeDTO, TypeCategory.class)), TypeCategoryDTO.class);
    }

}
