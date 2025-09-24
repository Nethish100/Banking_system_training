package com.securebank.repository;

import com.securebank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Account entity
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    
    /**
     * Find accounts by customer ID
     * @param customerId the customer ID to search for
     * @return List of accounts for the customer
     */
    List<Account> findByCustomer_CustomerId(Long customerId);
    
    /**
     * Find accounts by account type
     * @param accountType the account type to search for
     * @return List of accounts with the specified type
     */
    List<Account> findByAccountType(Account.AccountType accountType);
    
    /**
     * Find accounts with balance greater than specified amount
     * @param balance the minimum balance
     * @return List of accounts with balance greater than the amount
     */
    List<Account> findByAccountBalanceGreaterThan(BigDecimal balance);
    
    /**
     * Find accounts with balance between specified amounts
     * @param minBalance the minimum balance
     * @param maxBalance the maximum balance
     * @return List of accounts with balance in the range
     */
    List<Account> findByAccountBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);
    
    /**
     * Count total number of accounts
     * @return total count of accounts
     */
    @Query("SELECT COUNT(a) FROM Account a")
    long countAccounts();
    
    /**
     * Get total balance across all accounts
     * @return sum of all account balances
     */
    @Query("SELECT COALESCE(SUM(a.accountBalance), 0) FROM Account a")
    BigDecimal getTotalBalance();
    
    /**
     * Get total balance by account type
     * @param accountType the account type
     * @return sum of balances for the account type
     */
    @Query("SELECT COALESCE(SUM(a.accountBalance), 0) FROM Account a WHERE a.accountType = :accountType")
    BigDecimal getTotalBalanceByType(@Param("accountType") Account.AccountType accountType);
    
    /**
     * Find accounts ordered by creation date descending
     * @return List of accounts ordered by newest first
     */
    @Query("SELECT a FROM Account a ORDER BY a.createdDate DESC")
    List<Account> findAllOrderByCreatedDateDesc();
    
    /**
     * Find accounts created after a certain date
     * @param date the date to filter by
     * @return List of accounts created after the date
     */
    List<Account> findByCreatedDateAfter(LocalDateTime date);
    
    /**
     * Find accounts by customer name (case-insensitive)
     * @param customerName the customer name to search for
     * @return List of accounts for customers with matching names
     */
    @Query("SELECT a FROM Account a WHERE LOWER(a.customer.name) LIKE LOWER(CONCAT('%', :customerName, '%'))")
    List<Account> findByCustomerNameContainingIgnoreCase(@Param("customerName") String customerName);
    
    /**
     * Find accounts by account holder name (case-insensitive)
     * @param accountHolderName the account holder name to search for
     * @return List of accounts with matching account holder names
     */
    @Query("SELECT a FROM Account a WHERE LOWER(a.accountHolderName) LIKE LOWER(CONCAT('%', :accountHolderName, '%'))")
    List<Account> findByAccountHolderNameContainingIgnoreCase(@Param("accountHolderName") String accountHolderName);
    
    /**
     * Count accounts by type
     * @param accountType the account type
     * @return count of accounts of the specified type
     */
    long countByAccountType(Account.AccountType accountType);
    
    /**
     * Find accounts with highest balances (top N)
     * @param limit the number of accounts to return
     * @return List of accounts with highest balances
     */
    @Query(value = "SELECT a FROM Account a ORDER BY a.accountBalance DESC LIMIT :limit")
    List<Account> findTopAccountsByBalance(@Param("limit") int limit);
    
    /**
     * Check if account number exists
     * @param accountNo the account number to check
     * @return true if account number exists
     */
    boolean existsByAccountNo(String accountNo);
}