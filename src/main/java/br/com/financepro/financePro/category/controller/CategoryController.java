package br.com.financepro.financePro.category.controller;

import br.com.financepro.financePro.category.controller.doc.CategoryControllerDocs;
import br.com.financepro.financePro.category.dto.CategoryRequestDTO;
import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.category.service.CategoryService;
import br.com.financepro.financePro.common.enums.CategoryType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Category")
@RestController
@RequestMapping("/api/categories/v1")
public class CategoryController implements CategoryControllerDocs {

    @Autowired
    private CategoryService service;

    @GetMapping
    @Override
    public ResponseEntity<List<CategoryResponseDTO>> getAll(
        @RequestParam(required = false, name = "type") CategoryType type
    ) {
        return ResponseEntity.ok().body(service.getAll(type));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody CategoryRequestDTO category) {
        return ResponseEntity.ok().body(service.create(category));
    }

    @PutMapping
    @Override
    public ResponseEntity<CategoryResponseDTO> update(@RequestBody CategoryRequestDTO category) {
        return ResponseEntity.ok().body(service.update(category));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}