package com.securebank.service;

import com.securebank.dto.AccountDto;
import com.securebank.entity.Account;
import com.securebank.entity.Customer;
import com.securebank.repository.AccountRepository;
import com.securebank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAllOrderByCreatedDateDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountByNumber(String accountNo) {
        Account account = accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + accountNo));
        return convertToDto(account);
    }

    public AccountDto createAccount(AccountDto accountDto) {
        // Validate customer exists
        Customer customer = customerRepository.findById(accountDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + accountDto.getCustomerId()));

        // Generate account number
        String accountNo = generateAccountNumber();

        Account account = new Account();
        account.setAccountNo(accountNo);
        account.setAccountHolderName(accountDto.getAccountHolderName());
        account.setAccountType(accountDto.getAccountType());
        account.setAccountBalance(accountDto.getAccountBalance());
        account.setCustomer(customer);
        account.setCreatedDate(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        return convertToDto(savedAccount);
    }

    public void deleteAccount(String accountNo) {
        Account account = accountRepository.findById(accountNo)
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + accountNo));
        
        accountRepository.delete(account);
    }

    public long getAccountCount() {
        return accountRepository.countAccounts();
    }

    public BigDecimal getTotalBalance() {
        return accountRepository.getTotalBalance();
    }

    public List<AccountDto> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AccountDto> getAccountsByType(Account.AccountType accountType) {
        return accountRepository.findByAccountType(accountType)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalBalanceByType(Account.AccountType accountType) {
        return accountRepository.getTotalBalanceByType(accountType);
    }

    public List<AccountDto> getAccountsWithHighBalances(BigDecimal minBalance) {
        return accountRepository.findByAccountBalanceGreaterThan(minBalance)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public long getAccountCountByType(Account.AccountType accountType) {
        return accountRepository.countByAccountType(accountType);
    }

    public List<AccountDto> getRecentAccounts(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return accountRepository.findByCreatedDateAfter(cutoffDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper method to convert Account entity to DTO
    private AccountDto convertToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setAccountNo(account.getAccountNo());
        dto.setAccountHolderName(account.getAccountHolderName());
        dto.setAccountType(account.getAccountType());
        dto.setAccountBalance(account.getAccountBalance());
        dto.setCustomerId(account.getCustomer().getCustomerId());
        dto.setCreatedDate(account.getCreatedDate());
        dto.setUpdatedDate(account.getUpdatedDate());
        return dto;
    }

    // Helper method to generate account number
    private String generateAccountNumber() {
        String prefix = "ACC";
        long count = accountRepository.count();
        String suffix = String.format("%06d", count + 1);
        String accountNo = prefix + suffix;
        
        // Ensure uniqueness
        while (accountRepository.existsByAccountNo(accountNo)) {
            count++;
            suffix = String.format("%06d", count + 1);
            accountNo = prefix + suffix;
        }
        
        return accountNo;
    }
}