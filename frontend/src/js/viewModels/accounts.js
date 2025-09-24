define(['knockout', 'jquery', 'appController', 'ojs/ojarraydataprovider'], 
function(ko, $, app, ArrayDataProvider) {
  function AccountsViewModel() {
    var self = this;

    // Observables
    self.accounts = ko.observableArray([]);
    self.customers = ko.observableArray([]);
    self.accountsDataProvider = new ArrayDataProvider(self.accounts, {keyAttributes: 'accountNo'});
    self.isLoading = ko.observable(false);
    self.errorMessage = ko.observable('');

    self.accountNo = ko.observable();
    self.accountHolderName = ko.observable('');
    self.accountType = ko.observable('');
    self.accountBalance = ko.observable(0);
    self.customerId = ko.observable();
    self.showDialog = ko.observable(false);

    self.accountTypeOptions = [
      {value: 'SAVING', label: 'Savings Account'},
      {value: 'CURRENT', label: 'Current Account'}
    ];

    self.isFormValid = ko.computed(function() {
      return self.accountHolderName().trim().length > 0 &&
             self.accountType().length > 0 &&
             self.accountBalance() >= 0 &&
             self.customerId();
    });

    self.columnArray = [
      {headerText: 'Account No', field: 'accountNo', resizable: 'enabled', sortable: 'enabled'},
      {headerText: 'Holder Name', field: 'accountHolderName', resizable: 'enabled', sortable: 'enabled'},
      {headerText: 'Type', field: 'accountType', resizable: 'enabled', sortable: 'enabled', template: 'accountTypeTemplate'},
      {headerText: 'Balance', field: 'accountBalance', resizable: 'enabled', sortable: 'enabled', template: 'balanceTemplate', className: 'oj-helper-text-align-end'},
      {headerText: 'Customer', field: 'customerId', headerClassName: 'oj-sm-only-hide', className: 'oj-sm-only-hide', resizable: 'enabled', sortable: 'enabled'},
      {headerText: 'Actions', template: 'actionTemplate', resizable: 'enabled', sortable: 'disabled'}
    ];

    self.loadAccounts = function() {
      self.isLoading(true);
      self.errorMessage('');
      $.ajax({
        url: app.apiBaseUrl + '/accounts',
        type: 'GET',
        timeout: 10000,
        success: function(data) { self.accounts(data || []); },
        error: function(xhr, status, error) { self.errorMessage('Failed to load accounts: ' + (xhr.responseJSON?.message || error)); self.accounts([]); },
        complete: function() { self.isLoading(false); }
      });
    };

    self.loadCustomers = function() {
      $.ajax({
        url: app.apiBaseUrl + '/customers',
        type: 'GET',
        timeout: 10000,
        success: function(data) { self.customers(data || []); },
        error: function(xhr, status, error) { console.error('Failed to load customers:', error); }
      });
    };

    self.openAddDialog = function() { self.resetForm(); self.showDialog(true); };

    self.saveAccount = function() {
      if (!self.isFormValid()) { alert('Please fill all required fields'); return; }

      var accountData = {
        accountHolderName: self.accountHolderName().trim(),
        accountType: self.accountType(),
        accountBalance: parseFloat(self.accountBalance()) || 0,
        customerId: parseInt(self.customerId())
      };

      var url = app.apiBaseUrl + '/accounts';
      var method = 'POST';
      if (self.accountNo()) { url += '/' + self.accountNo(); method = 'PUT'; accountData.accountNo = self.accountNo(); }

      $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(accountData),
        timeout: 10000,
        success: function() { self.loadAccounts(); self.closeDialog(); console.log('Account saved'); },
        error: function(xhr, status, error) { alert('Failed to save account: ' + (xhr.responseJSON?.message || error)); }
      });
    };

    self.deleteAccount = function(account) {
      if (confirm('Are you sure you want to delete "' + account.accountNo + '"?')) {
        $.ajax({
          url: app.apiBaseUrl + '/accounts/' + account.accountNo,
          type: 'DELETE',
          timeout: 10000,
          success: function() { self.loadAccounts(); console.log('Account deleted'); },
          error: function(xhr, status, error) { alert('Failed to delete account: ' + (xhr.responseJSON?.message || error)); }
        });
      }
    };

    self.formatBalance = function(balance) { return '$' + parseFloat(balance || 0).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2}); };
    self.getAccountTypeLabel = function(type) { var option = self.accountTypeOptions.find(opt => opt.value === type); return option ? option.label : type; };
    self.getCustomerName = function(customerId) { var customer = self.customers().find(c => c.customerId === customerId); return customer ? customer.name : 'Unknown'; };

    self.closeDialog = function() { self.showDialog(false); self.resetForm(); };
    self.resetForm = function() { self.accountNo(null); self.accountHolderName(''); self.accountType(''); self.accountBalance(0); self.customerId(null); };
    self.refreshAccounts = function() { self.loadAccounts(); self.loadCustomers(); };

    self.onCustomerChange = function() {
      var selectedCustomerId = self.customerId();
      if (selectedCustomerId) {
        var customer = self.customers().find(c => c.customerId === parseInt(selectedCustomerId));
        if (customer) self.accountHolderName(customer.name);
      }
    };
    self.customerId.subscribe(self.onCustomerChange);

    self.loadAccounts();
    self.loadCustomers();
  }

  return AccountsViewModel;
});
