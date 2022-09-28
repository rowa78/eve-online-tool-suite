package de.ronnywalter.eve.rest;

import de.ronnywalter.eve.dto.TypeCategoryDTO;
import de.ronnywalter.eve.dto.TypeGroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "eve-backend/typecategories", decode404 = true)
public interface TypeCategoryService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    List<TypeCategoryDTO> getTypeCategories();

    @RequestMapping(method = RequestMethod.GET, value = "/{typeCategoryId}")
    TypeCategoryDTO getTypeCategory(@PathVariable("typeCategoryId") int typeCategoryId);

    @PostMapping()
    TypeCategoryDTO createTypeCategory(@RequestBody TypeCategoryDTO typeCategoryDTO);

}
