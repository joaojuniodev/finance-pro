package br.com.financepro.financePro.category.service;

import br.com.financepro.financePro.category.dto.CategoryRequestDTO;
import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.category.repository.spec.CategorySpecification;
import br.com.financepro.financePro.common.enums.CategoryType;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.category.CategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class.getName());

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private CategoryMapper mapper;

    public List<CategoryResponseDTO> getAll(CategoryType type) {
        log.info("Getting All Categories");

        CategorySpecification spec = new CategorySpecification();
        spec.addToSpecifications(type);

        return repository
            .findAll(spec.apply())
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public CategoryResponseDTO getById(UUID id) {
        log.info("Getting Category by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public CategoryResponseDTO create(CategoryRequestDTO category) {
        log.info("Creating Category");

        var categoryCreated = repository.save(mapper.toEntity(category));
        return mapper.toResponse(categoryCreated);
    }

    public CategoryResponseDTO update(CategoryRequestDTO category) {
        log.info("Updating Category");

        var entity = repository.findById(category.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + category.getId()));
        entity.setName(category.getName());
        entity.setType(category.getType());

        var categoryUpdated = repository.save(entity);
        return mapper.toResponse(categoryUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Category");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }
}