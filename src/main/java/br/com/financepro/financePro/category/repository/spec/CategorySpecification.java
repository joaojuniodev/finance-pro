package br.com.financepro.financePro.category.repository.spec;

import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.common.enums.CategoryType;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    private Specification<Category> spec;

    public CategorySpecification() {
        this.spec = Specification.where(null);
    }

    public Specification<Category> apply() {
        return this.spec;
    }

    public void addToSpecifications(CategoryType type) {
        hasType(type);
    }

    private void hasType(CategoryType type) {
        this.spec = this.spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
    }
}