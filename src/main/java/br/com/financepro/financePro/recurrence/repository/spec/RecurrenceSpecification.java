package br.com.financepro.financePro.recurrence.repository.spec;

import br.com.financepro.financePro.recurrence.model.Recurrence;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RecurrenceSpecification {

    private Specification<Recurrence> spec;

    public RecurrenceSpecification() {
        this.spec = Specification.where(null);
    }

    public Specification<Recurrence> apply() {
        return this.spec;
    }

    public void addSpecifications(UUID accountId) {
        hasAccount(accountId);
    }

    private void hasAccount(UUID accountId) {
        this.spec = this.spec.and((root, query, cb) ->
            cb.equal(root.get("account").get("id"), accountId));
    }
}