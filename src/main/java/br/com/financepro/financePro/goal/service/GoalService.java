package br.com.financepro.financePro.goal.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.dto.GoalResponseDTO;
import br.com.financepro.financePro.goal.exception.InvalidGoalException;
import br.com.financepro.financePro.goal.model.Goal;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.goal.GoalMapper;
import br.com.financepro.financePro.goal.repository.GoalRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository repository;
    private final GoalMapper mapper;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final WalletRepository walletRepository;

    public GoalService(GoalRepository repository, GoalMapper mapper, AccountRepository accountRepository,
                       CategoryRepository categoryRepository, WalletRepository walletRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public List<GoalResponseDTO> getAll(String username) {
        return repository.findByAccountIdOrderByNameAsc(ownedAccount(username).getId())
            .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<GoalResponseDTO> getByAccount(UUID accountId, String username) {
        requireAccount(accountId, username);
        return repository.findByAccountIdOrderByNameAsc(accountId)
            .stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GoalResponseDTO getById(UUID id, String username) {
        return mapper.toResponse(ownedGoal(id, ownedAccount(username).getId()));
    }

    @Transactional
    public GoalResponseDTO create(GoalRequestDTO request, String username) {
        validate(request, true);
        Account account = ownedAccount(username);
        Goal goal = mapper.toEntity(request);
        goal.setName(request.getName().trim());
        goal.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        goal.setAccount(account);
        goal.setCategory(ownedCategory(request.getCategoryId(), account.getId()));
        Goal saved = repository.save(goal);
        if (request.getWalletId() != null) {
            Wallet wallet = walletRepository.findById(request.getWalletId())
                .filter(value -> value.getAccount() != null && account.getId().equals(value.getAccount().getId()))
                .orElseThrow(() -> new NotFoundException("Carteira não encontrada para esta conta."));
            if (wallet.getGoal() != null) throw new InvalidGoalException("Esta carteira já possui uma meta.");
            wallet.setGoal(saved);
            walletRepository.save(wallet);
        }
        return mapper.toResponse(saved);
    }

    @Transactional
    public GoalResponseDTO update(GoalRequestDTO request, String username) {
        validate(request, false);
        UUID accountId = ownedAccount(username).getId();
        Goal goal = ownedGoal(request.getId(), accountId);
        goal.setName(request.getName().trim());
        goal.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        goal.setTotalAmount(request.getTotalAmount());
        goal.setCurrentAmount(request.getCurrentAmount());
        goal.setCategory(ownedCategory(request.getCategoryId(), accountId));
        return mapper.toResponse(repository.save(goal));
    }

    @Transactional
    public GoalResponseDTO incrementAmount(UUID id, BigDecimal amount, String username) {
        if (!validMoney(amount, false)) throw new InvalidGoalException("Informe um valor positivo com até duas casas decimais.");
        Goal goal = ownedGoal(id, ownedAccount(username).getId());
        BigDecimal updated = goal.getCurrentAmount().add(amount);
        if (updated.compareTo(goal.getTotalAmount()) > 0) {
            throw new InvalidGoalException("O valor acumulado não pode superar o objetivo.");
        }
        goal.setCurrentAmount(updated);
        return mapper.toResponse(repository.save(goal));
    }

    @Transactional
    public void delete(UUID id, String username) {
        Goal goal = ownedGoal(id, ownedAccount(username).getId());
        if (goal.getWallet() != null) {
            goal.getWallet().setGoal(null);
            walletRepository.save(goal.getWallet());
        }
        repository.delete(goal);
    }

    private Account ownedAccount(String username) {
        return accountRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Conta não encontrada."));
    }

    private void requireAccount(UUID accountId, String username) {
        if (!ownedAccount(username).getId().equals(accountId)) {
            throw new NotFoundException("Conta não encontrada.");
        }
    }

    private Goal ownedGoal(UUID id, UUID accountId) {
        return repository.findByIdAndAccountId(id, accountId)
            .orElseThrow(() -> new NotFoundException("Meta não encontrada."));
    }

    private Category ownedCategory(UUID id, UUID accountId) {
        if (id == null) return null;
        return categoryRepository.findById(id)
            .filter(category -> Boolean.TRUE.equals(category.getSystem())
                || (category.getAccount() != null && accountId.equals(category.getAccount().getId())))
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada."));
    }

    private void validate(GoalRequestDTO request, boolean creating) {
        if (request == null || (creating ? request.getId() != null : request.getId() == null)
            || request.getName() == null || request.getName().isBlank()
            || request.getName().trim().length() > 255
            || (request.getDescription() != null && request.getDescription().trim().length() > 255)
            || !validMoney(request.getTotalAmount(), false)
            || !validMoney(request.getCurrentAmount(), true)
            || request.getCurrentAmount().compareTo(request.getTotalAmount()) > 0) {
            throw new InvalidGoalException("Revise o nome, o objetivo e o valor inicial da meta.");
        }
    }

    private boolean validMoney(BigDecimal value, boolean allowZero) {
        return value != null && (allowZero ? value.signum() >= 0 : value.signum() > 0)
            && value.stripTrailingZeros().scale() <= 2
            && value.precision() - value.scale() <= 36;
    }
}
