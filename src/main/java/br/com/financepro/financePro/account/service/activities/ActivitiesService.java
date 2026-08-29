package br.com.financepro.financePro.account.service.activities;

import br.com.financepro.financePro.account.dto.response.ActivitiesResponseDTO;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.recurrence.repository.spec.RecurrenceSpecification;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.transaction.repository.spec.TransactionSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ActivitiesService {

    private final Logger log = LoggerFactory.getLogger(ActivitiesService.class.getName());

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private RecurrenceMapper recurrenceMapper;

    public ActivitiesResponseDTO getActivities(UUID accountId, Integer month, Integer year) {
        log.info("Getting Activities by Account");

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        TransactionSpecification txSpec = new TransactionSpecification();
        txSpec.addToSpecifications(
            accountId,
            null,
            month == null ? currentMonth : month,
            year == null ? currentYear : year
        );

        RecurrenceSpecification recSpec = new RecurrenceSpecification();
        recSpec.addSpecifications(accountId);

        var transactions = transactionRepository.findAll(txSpec.apply())
            .stream()
            .map(transactionMapper::toResponse)
            .toList();
        var recurrences = recurrenceRepository.findAll(recSpec.apply())
            .stream()
            .map(recurrenceMapper::toResponse)
            .toList();

        return new ActivitiesResponseDTO(
            transactions,
            recurrences
        );
    }
}