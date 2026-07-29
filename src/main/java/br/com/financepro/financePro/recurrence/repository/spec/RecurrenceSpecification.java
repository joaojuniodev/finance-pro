package br.com.financepro.financePro.recurrence.repository.spec;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.service.params.RecurrenceSearchParams;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecurrenceSpecification {

    private Specification<Recurrence> spec;

    public RecurrenceSpecification(RecurrenceSearchParams params) {
        this.spec = Specification.where(null);
        addSpecifications(params);
    }

    public Specification<Recurrence> apply() {
        return this.spec;
    }

    private void addSpecifications(RecurrenceSearchParams params) {
        if (params.accountId() != null) hasAccount(params.accountId());
        if (params.search() != null) search(params.search());
        if (params.type() != null) hasType(params.type());
        if (params.frequencyType() != null) hasFrequency(params.frequencyType());
        if (params.executionType() != null) hasExecutionType(params.executionType());
    }

    private void hasAccount(UUID accountId) {
        this.spec = this.spec.and((root, query, cb) ->
            cb.equal(root.get("account").get("id"), accountId));
    }

    private void search(String search) {
        this.spec = this.spec.and((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate descriptionLike = cb.like(
                cb.lower(root.get("description")),
                "%" + search + "%"
            );

            Predicate categoryLike = cb.like(
                cb.lower(root.get("category").get("name")),
                "%" + search + "%"
            );

            predicates.add(cb.or(descriptionLike, categoryLike));
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    private void hasType(RecurrenceType type) {
        this.spec = this.spec.and((root, query, cb) ->
            cb.equal(root.get("type"), type));
    }

    private void hasFrequency(FrequencyType frequencyType) {
        this.spec = this.spec.and((root, query, cb) ->
            cb.equal(root.get("frequencyType"), frequencyType));
    }

    private void hasExecutionType(ExecutionType executionType) {
        this.spec = this.spec.and((root, query, cb) ->
            cb.equal(root.get("executionType"), executionType));
    }
}