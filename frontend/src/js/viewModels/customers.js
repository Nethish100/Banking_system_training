define(['knockout', 'jquery', 'appController', 'ojs/ojarraydataprovider'], 
function(ko, $, app, ArrayDataProvider) {
  function CustomersViewModel() {
    var self = this;

    // Observable arrays and properties
    self.customers = ko.observableArray([]);
    self.customersDataProvider = new ArrayDataProvider(self.customers, {keyAttributes: 'customerId'});
    self.isLoading = ko.observable(false);
    self.errorMessage = ko.observable('');

    // Form fields
    self.customerId = ko.observable();
    self.customerName = ko.observable('');
    self.customerEmail = ko.observable('');
    self.customerMobile = ko.observable('');
    self.customerAddress = ko.observable('');
    self.isEditing = ko.observable(false);

    // Dialog state
    self.showDialog = ko.observable(false);

    // Validation
    self.isFormValid = ko.computed(function() {
      return self.customerName().trim().length > 0 &&
             self.customerEmail().trim().length > 0 &&
             self.customerMobile().trim().length > 0 &&
             self.customerAddress().trim().length > 0 &&
             self.isValidEmail(self.customerEmail());
    });

    self.isValidEmail = function(email) {
      var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      return emailRegex.test(email);
    };

    // Columns for table
    self.columnArray = [
      {headerText: 'ID', field: 'customerId', headerClassName: 'oj-sm-only-hide', className: 'oj-sm-only-hide', resizable: 'enabled', sortable: 'enabled'},
      {headerText: 'Name', field: 'name', resizable: 'enabled', sortable: 'enabled'},
      {headerText: 'Email', field: 'email', resizable: 'enabled', sortable: 'enabled'},
      {headerText: 'Mobile', field: 'mobileNumber', headerClassName: 'oj-sm-only-hide', className: 'oj-sm-only-hide', resizable: 'enabled'},
      {headerText: 'Address', field: 'address', headerClassName: 'oj-md-only-hide', className: 'oj-md-only-hide', resizable: 'enabled'},
      {headerText: 'Actions', template: 'actionTemplate', resizable: 'enabled', sortable: 'disabled'}
    ];

    // Load customers
    self.loadCustomers = function() {
      self.isLoading(true);
      self.errorMessage('');
      $.ajax({
        url: app.apiBaseUrl + '/customers',
        type: 'GET',
        timeout: 10000,
        success: function(data) { self.customers(data || []); },
        error: function(xhr, status, error) {
          console.error('Failed to load customers:', xhr, status, error);
          self.errorMessage('Failed to load customers: ' + (xhr.responseJSON?.message || error));
          self.customers([]);
        },
        complete: function() { self.isLoading(false); }
      });
    };

    // Dialog actions
    self.openAddDialog = function() { self.resetForm(); self.isEditing(false); self.showDialog(true); };
    self.editCustomer = function(customer) {
      self.customerId(customer.customerId);
      self.customerName(customer.name || '');
      self.customerEmail(customer.email || '');
      self.customerMobile(customer.mobileNumber || '');
      self.customerAddress(customer.address || '');
      self.isEditing(true);
      self.showDialog(true);
    };
    self.closeDialog = function() { self.showDialog(false); self.resetForm(); };
    self.resetForm = function() {
      self.customerId(null);
      self.customerName('');
      self.customerEmail('');
      self.customerMobile('');
      self.customerAddress('');
    };

    // Save customer
    self.saveCustomer = function() {
      if (!self.isFormValid()) { alert('Please fill all required fields with valid data'); return; }

      var customerData = {
        name: self.customerName().trim(),
        email: self.customerEmail().trim(),
        mobileNumber: self.customerMobile().trim(),
        address: self.customerAddress().trim()
      };

      var url = app.apiBaseUrl + '/customers';
      var method = 'POST';
      if (self.isEditing()) { url += '/' + self.customerId(); method = 'PUT'; customerData.customerId = self.customerId(); }

      $.ajax({
        url: url,
        type: method,
        contentType: 'application/json',
        data: JSON.stringify(customerData),
        timeout: 10000,
        success: function() { self.loadCustomers(); self.closeDialog(); console.log(self.isEditing() ? 'Customer updated' : 'Customer created'); },
        error: function(xhr, status, error) { alert('Failed to save customer: ' + (xhr.responseJSON?.message || error)); }
      });
    };

    // Delete customer
    self.deleteCustomer = function(customer) {
      if (confirm('Are you sure you want to delete "' + customer.name + '"?')) {
        $.ajax({
          url: app.apiBaseUrl + '/customers/' + customer.customerId,
          type: 'DELETE',
          timeout: 10000,
          success: function() { self.loadCustomers(); console.log('Customer deleted'); },
          error: function(xhr, status, error) { alert('Failed to delete customer: ' + (xhr.responseJSON?.message || error)); }
        });
      }
    };

    self.refreshCustomers = function() { self.loadCustomers(); };
    self.loadCustomers(); // Initialize
  }

  return CustomersViewModel;
});
