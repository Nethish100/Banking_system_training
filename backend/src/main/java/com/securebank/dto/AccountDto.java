// AccountDto.java
package com.securebank.dto;

import com.securebank.entity.Account;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto {
    private String accountNo;
    
    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;
    
    @NotNull(message = "Account balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Account balance must be non-negative")
    private BigDecimal accountBalance;
    
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Constructors
    public AccountDto() {}
    
    public AccountDto(String accountHolderName, BigDecimal accountBalance, 
                     Account.AccountType accountType, Long customerId) {
        this.accountHolderName = accountHolderName;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
        this.customerId = customerId;
    }
    
    // Getters and Setters
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    public String getAccountHolderName() {
        return accountHolderName;
    }
    
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
    
    public BigDecimal getAccountBalance() {
        return accountBalance;
    }
    
    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
    
    public Account.AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}