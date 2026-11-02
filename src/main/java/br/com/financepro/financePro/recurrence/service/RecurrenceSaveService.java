package br.com.financepro.financePro.recurrence.service;

import br.com.financepro.financePro.common.enums.ExecutionType;
import br.com.financepro.financePro.common.enums.FrequencyType;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.invoice.service.InvoiceRecurrenceService;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.common.exceptions.RecurrenceSaveException;
import br.com.financepro.financePro.mapper.recurrence.RecurrenceMapper;
import br.com.financepro.financePro.recurrence.common.enums.RecurrenceStatus;
import br.com.financepro.financePro.recurrence.dto.request.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceConfirmResponseDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.recurrence.repository.RecurrenceRepository;
import br.com.financepro.financePro.transaction.service.TransactionExecutionService;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;
import java.util.UUID;

@Service
public class RecurrenceSaveService {

    private final Logger log = LoggerFactory.getLogger(RecurrenceSaveService.class.getName());

    @Autowired
    private RecurrenceRepository repository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RecurrenceExecutionService executionService;

    @Autowired
    private TransactionExecutionService transactionExecutionService;

    @Autowired
    private RecurrenceMapper mapper;

    @Autowired
    private InvoiceRecurrenceService invoiceRecurrences;

    @Transactional
    public RecurrenceResponseDTO create(RecurrenceRequestDTO recurrence) {
        log.info("Creating Recurrence");

        LocalDate today = LocalDate.now();
        validateRequest(recurrence, true);
        var entity = mapper.toEntity(recurrence);
        configureCardRecurrence(recurrence, entity);
        validateUpdate(recurrence, today);
        entity.setLastExecutionDate(null);
        entity.setNextExecutionDate(calculateFirstExecutionDate(entity, today, entity.getLastExecutionDate()));

        var recurrenceCreated = repository.save(entity);

        if (recurrence.getDayOneAlreadyOccurred() || recurrence.getMonthOfTheYearAlreadyOccurred()) {
            LocalDate of = recurrence.getFrequencyType() == FrequencyType.YEARLY
                ? dateInYear(today.getYear(), recurrence.getMonthOfTheYear(), recurrence.getDayOne())
                : dateInMonth(YearMonth.from(today), recurrence.getDayOne());
            transactionExecutionService.createByRecurrence(recurrenceCreated, of);
            recurrenceCreated.setLastExecutionDate(of);
        }

        if (recurrence.getDayTwoAlreadyOccurred()) {
            LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayTwo());
            transactionExecutionService.createByRecurrence(recurrenceCreated, of);
            recurrenceCreated.setLastExecutionDate(of);
        }

        recurrenceCreated.setNextExecutionDate(calculateFirstExecutionDate(recurrenceCreated, today, recurrenceCreated.getLastExecutionDate()));
        repository.save(recurrenceCreated);
        if (isCreditCard(recurrenceCreated)) invoiceRecurrences.addToOpenInvoice(recurrenceCreated);
        return mapper.toResponse(recurrenceCreated);
    }

    @Transactional
    public RecurrenceResponseDTO update(RecurrenceRequestDTO recurrence, String username) {
        log.info("Updating Recurrence");

        LocalDate today = LocalDate.now();
        validateRequest(recurrence, false);
        var entity = repository.findByIdAndAccountUserUsername(recurrence.getId(), username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrence.getId()));
        if (!entity.getAccount().getId().equals(recurrence.getAccountId())) throw new NotFoundException("Recorrência não encontrada.");
        var requested = mapper.toEntity(recurrence);
        configureCardRecurrence(recurrence, requested);
        validateUpdate(recurrence, today);
        entity.setWallet(requested.getWallet());
        entity.setCategory(requested.getCategory());
        entity.setAmount(recurrence.getAmount());
        entity.setDescription(recurrence.getDescription());
        entity.setType(recurrence.getType());
        entity.setExecutionType(recurrence.getExecutionType());
        entity.setFrequencyType(recurrence.getFrequencyType());
        entity.setMonthOfTheYear(recurrence.getMonthOfTheYear());
        var recurrenceInDatabase = repository.save(entity);

        updateExecution(recurrence, recurrenceInDatabase, today);

        var recurrenceUpdated = repository.save(recurrenceInDatabase);
        if (isCreditCard(recurrenceUpdated)) invoiceRecurrences.addToOpenInvoice(recurrenceUpdated);
        return mapper.toResponse(recurrenceUpdated);
    }

    @Transactional
    public void confirm(UUID recurrenceId, String username) {
        var entity = repository.findByIdAndAccountUserUsername(recurrenceId, username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        if (isCreditCard(entity)) throw new RecurrenceSaveException("Esta recorrência é paga na fatura do cartão.");
        executionService.execute(entity, LocalDate.now(), true);
    }

    @Transactional
    public void pause(UUID recurrenceId, String username) {
        var entity = repository.findByIdAndAccountUserUsername(recurrenceId, username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        entity.setStatus(RecurrenceStatus.PAUSED);
        repository.save(entity);
    }

    @Transactional
    public void activate(UUID recurrenceId, String username) {
        var entity = repository.findByIdAndAccountUserUsername(recurrenceId, username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        entity.setStatus(RecurrenceStatus.ACTIVE);
        repository.save(entity);
        if (isCreditCard(entity)) invoiceRecurrences.addToOpenInvoice(entity);
    }

    @Transactional
    public void finish(UUID recurrenceId, String username) {
        var entity = repository.findByIdAndAccountUserUsername(recurrenceId, username)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + recurrenceId));
        entity.setStatus(RecurrenceStatus.ENDED);
        repository.save(entity);
    }

    @Transactional
    public void delete(UUID id, String username) {
        log.info("Deleting Recurrence");

        var entity = repository.findByIdAndAccountUserUsername(id, username)
            .orElseThrow(() -> new NotFoundException("Recorrência não encontrada."));
        transactionRepository.detachRecurrence(id);
        repository.delete(entity);
    }

    private void validateRequest(RecurrenceRequestDTO request, boolean creating) {
        if (request == null || (creating ? request.getId() != null : request.getId() == null)
            || request.getAccountId() == null || request.getCategoryId() == null || request.getWalletId() == null
            || request.getAmount() == null || request.getAmount().signum() <= 0 || request.getAmount().stripTrailingZeros().scale() > 2
            || request.getDescription() == null || request.getDescription().isBlank() || request.getDescription().length() > 200
            || request.getType() == null || request.getStatus() == null || request.getFrequencyType() == null || request.getExecutionType() == null
            || request.getDayOne() == null || request.getDayOne() < 1 || request.getDayOne() > 31
            || (request.getFrequencyType() == FrequencyType.BIWEEKLY && (request.getDayTwo() == null || request.getDayTwo() <= request.getDayOne() || request.getDayTwo() > 31))
            || (request.getFrequencyType() == FrequencyType.YEARLY && (request.getMonthOfTheYear() == null || request.getMonthOfTheYear() < 1 || request.getMonthOfTheYear() > 12))) {
            throw new RecurrenceSaveException("Confira os dados e as datas da recorrência.");
        }
        request.setDayOneAlreadyOccurred(Boolean.TRUE.equals(request.getDayOneAlreadyOccurred()));
        request.setDayTwoAlreadyOccurred(Boolean.TRUE.equals(request.getDayTwoAlreadyOccurred()));
        request.setMonthOfTheYearAlreadyOccurred(Boolean.TRUE.equals(request.getMonthOfTheYearAlreadyOccurred()));
    }

    private boolean isCreditCard(Recurrence recurrence) {
        return recurrence.getWallet() != null && recurrence.getWallet().getType() == WalletType.CREDIT_CARD;
    }

    private void configureCardRecurrence(RecurrenceRequestDTO request, Recurrence recurrence) {
        if (!isCreditCard(recurrence)) return;
        if (request.getType() != RecurrenceType.DEBIT) {
            throw new RecurrenceSaveException("Recorrências no cartão devem ser despesas.");
        }
        request.setExecutionType(ExecutionType.MANUALLY);
        recurrence.setExecutionType(ExecutionType.MANUALLY);
    }

    private void validateUpdate(RecurrenceRequestDTO recurrence, LocalDate today) {
        boolean anyAlreadyOccurredFlag = Boolean.TRUE.equals(recurrence.getDayOneAlreadyOccurred())
            || Boolean.TRUE.equals(recurrence.getDayTwoAlreadyOccurred())
            || Boolean.TRUE.equals(recurrence.getMonthOfTheYearAlreadyOccurred());

        if (anyAlreadyOccurredFlag && recurrence.getExecutionType() == ExecutionType.MANUALLY) {
            throw new RecurrenceSaveException("A Recorrência não pode ser efetivada se o tipo de execução é manual");
        }

        if (recurrence.getFrequencyType() == FrequencyType.MONTHLY
            && recurrence.getDayOne() != null
            && Boolean.TRUE.equals(recurrence.getDayOneAlreadyOccurred())
            && recurrence.getDayOne() > today.getDayOfMonth()) {
            throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o dia precisa ser anterior ao atual.");
        }

        if (recurrence.getFrequencyType() == FrequencyType.BIWEEKLY) {
            if (recurrence.getDayOne() != null
                && Boolean.TRUE.equals(recurrence.getDayOneAlreadyOccurred())
                && recurrence.getDayOne() > today.getDayOfMonth()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o dia precisa ser anterior ao atual.");
            }
            if (recurrence.getDayTwo() != null
                && Boolean.TRUE.equals(recurrence.getDayTwoAlreadyOccurred())
                && recurrence.getDayTwo() > today.getDayOfMonth()) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, o segundo dia precisa ser anterior ao atual.");
            }
        }

        if (recurrence.getFrequencyType() == FrequencyType.YEARLY
            && recurrence.getMonthOfTheYear() != null
            && recurrence.getDayOne() != null
            && Boolean.TRUE.equals(recurrence.getMonthOfTheYearAlreadyOccurred())) {

            YearMonth targetYearMonth = YearMonth.of(today.getYear(), recurrence.getMonthOfTheYear());
            LocalDate targetDate = dateInMonth(targetYearMonth, recurrence.getDayOne());

            if (targetDate.isAfter(today)) {
                throw new RecurrenceSaveException("Para que a Recorrência já tenha ocorrido, a data (mês e dia) precisa ser anterior ou igual à data atual.");
            }
        }
    }

    private void updateExecution(RecurrenceRequestDTO recurrence, Recurrence recurrenceInDatabase, LocalDate today) {
        switch (recurrence.getFrequencyType()) {
            case MONTHLY -> {
                if (!Objects.equals(recurrenceInDatabase.getDayOne(), recurrence.getDayOne())) {
                    recurrenceInDatabase.setDayOne(recurrence.getDayOne());
                }

                if (recurrence.getDayOneAlreadyOccurred()) {
                    LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayOne());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }

                recurrenceInDatabase.setNextExecutionDate(
                    calculateFirstExecutionDate(recurrenceInDatabase, today, recurrenceInDatabase.getLastExecutionDate())
                );
            }
            case BIWEEKLY -> {
                if (!Objects.equals(recurrenceInDatabase.getDayOne(), recurrence.getDayOne())) {
                    recurrenceInDatabase.setDayOne(recurrence.getDayOne());
                }
                if (!Objects.equals(recurrenceInDatabase.getDayTwo(), recurrence.getDayTwo())) {
                    recurrenceInDatabase.setDayTwo(recurrence.getDayTwo());
                }

                if (recurrence.getDayOneAlreadyOccurred()) {
                    LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayOne());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }
                if (recurrence.getDayTwoAlreadyOccurred()) {
                    LocalDate of = dateInMonth(YearMonth.from(today), recurrence.getDayTwo());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }

                recurrenceInDatabase.setNextExecutionDate(
                    calculateFirstExecutionDate(recurrenceInDatabase, today, recurrenceInDatabase.getLastExecutionDate())
                );
            }
            case YEARLY -> {
                if (!Objects.equals(recurrenceInDatabase.getMonthOfTheYear(), recurrence.getMonthOfTheYear())) {
                    recurrenceInDatabase.setMonthOfTheYear(recurrence.getMonthOfTheYear());
                }
                if (!Objects.equals(recurrenceInDatabase.getDayOne(), recurrence.getDayOne())) {
                    recurrenceInDatabase.setDayOne(recurrence.getDayOne());
                }

                if (recurrence.getMonthOfTheYearAlreadyOccurred() || recurrence.getDayOneAlreadyOccurred()) {
                    LocalDate of = dateInYear(today.getYear(), recurrence.getMonthOfTheYear(), recurrence.getDayOne());
                    transactionExecutionService.createByRecurrence(recurrenceInDatabase, of);
                    recurrenceInDatabase.setLastExecutionDate(of);
                }

                recurrenceInDatabase.setNextExecutionDate(
                    calculateFirstExecutionDate(recurrenceInDatabase, today, recurrenceInDatabase.getLastExecutionDate())
                );
            }
        }
    }

    private LocalDate calculateFirstExecutionDate(Recurrence recurrence, LocalDate today, LocalDate lastExecutionDate) {
        return switch (recurrence.getFrequencyType()) {
            case MONTHLY -> {

                YearMonth current = YearMonth.from(today);

                if (lastExecutionDate != null &&
                    YearMonth.from(lastExecutionDate).equals(current)) {

                    yield dateInMonth(current.plusMonths(1), recurrence.getDayOne());
                }

                LocalDate candidate = dateInMonth(current, recurrence.getDayOne());

                if (candidate.isBefore(today)) {
                    candidate = dateInMonth(current.plusMonths(1), recurrence.getDayOne());
                }

                yield candidate;
            }

            case BIWEEKLY -> {
                YearMonth currentMonth = YearMonth.from(today);

                LocalDate firstDate = dateInMonth(currentMonth, recurrence.getDayOne());
                LocalDate secondDate = dateInMonth(currentMonth, recurrence.getDayTwo());

                if (!firstDate.isBefore(today) && (lastExecutionDate == null || firstDate.isAfter(lastExecutionDate))) {
                    yield firstDate;
                }

                if (!secondDate.isBefore(today) && (lastExecutionDate == null || secondDate.isAfter(lastExecutionDate))) {
                    yield secondDate;
                }

                yield dateInMonth(currentMonth.plusMonths(1), recurrence.getDayOne());
            }

            case YEARLY -> {
                LocalDate candidate = dateInYear(
                    today.getYear(),
                    recurrence.getMonthOfTheYear(),
                    recurrence.getDayOne()
                );

                if (candidate.isBefore(today) || (lastExecutionDate != null && !candidate.isAfter(lastExecutionDate))) {
                    candidate = dateInYear(
                        today.getYear() + 1,
                        recurrence.getMonthOfTheYear(),
                        recurrence.getDayOne()
                    );
                }

                yield candidate;
            }
        };
    }

    private LocalDate dateInMonth(YearMonth yearMonth, Integer day) {
        int validDay = Math.min(day, yearMonth.lengthOfMonth());
        return yearMonth.atDay(validDay);
    }

    private LocalDate dateInYear(int year, Integer month, Integer day) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return dateInMonth(yearMonth, day);
    }
}
