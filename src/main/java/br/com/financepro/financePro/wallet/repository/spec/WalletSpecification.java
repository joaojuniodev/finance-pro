package br.com.financepro.financePro.wallet.repository.spec;

import br.com.financepro.financePro.wallet.model.Wallet;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class WalletSpecification {

    private Specification<Wallet> spec;

    public WalletSpecification() {
        this.spec = Specification.where(null);
    }

    public Specification<Wallet> apply() {
        return this.spec;
    }

    public void addToSpecifications(UUID accountId) {
        hasAccount(accountId);
    }

    private void hasAccount(UUID accountId) {
        this.spec = this.spec.and((root, query, cb) ->
            cb.equal(root.get("account").get("id"), accountId));
    }
}
