package br.com.financepro.financePro.invoice.repository;

import br.com.financepro.financePro.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Query("select i from Invoice i where i.id = :id and i.wallet.account.user.username = :username")
    Optional<Invoice> findOwned(@Param("id") UUID id, @Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Invoice i where i.id = :id and i.wallet.account.user.username = :username")
    Optional<Invoice> findOwnedForUpdate(@Param("id") UUID id, @Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Invoice i where i.wallet.id = :walletId and i.status <> br.com.financepro.financePro.common.enums.InvoiceStatus.PAID order by i.closingDate")
    List<Invoice> findOpenByWalletForUpdate(@Param("walletId") UUID walletId);

    Optional<Invoice> findFirstByWalletIdAndClosingDateLessThanOrderByClosingDateDesc(UUID walletId, LocalDate closingDate);

    boolean existsByWalletIdAndClosingDateBetween(
        UUID walletId,
        LocalDate startDate,
        LocalDate endDate
    );
}
