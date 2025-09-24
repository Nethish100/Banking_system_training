define(['knockout', 'jquery', 'appController'], function(ko, $, app) {
  function DashboardViewModel() {
    var self = this;
    
    // Observable properties for statistics
    self.totalCustomers = ko.observable(0);
    self.totalAccounts = ko.observable(0);
    self.totalBalance = ko.observable(0);
    self.monthlyGrowth = ko.observable(12.5);
    self.isLoading = ko.observable(true);
    self.errorMessage = ko.observable('');

    // Recent activities
    self.recentActivities = ko.observableArray([]);

    // Formatted values
    self.formattedTotalBalance = ko.computed(function() {
      return '$' + parseFloat(self.totalBalance()).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    });

    self.formattedGrowth = ko.computed(function() {
      return '+' + self.monthlyGrowth().toFixed(1) + '%';
    });

    // Load dashboard statistics
    self.loadStats = function() {
      self.isLoading(true);
      self.errorMessage('');

      // Load customers count
      var customersPromise = $.ajax({
        url: app.apiBaseUrl + '/customers/count',
        type: 'GET',
        timeout: 5000
      });

      // Load accounts count  
      var accountsPromise = $.ajax({
        url: app.apiBaseUrl + '/accounts/count',
        type: 'GET',
        timeout: 5000
      });

      // Load total balance
      var balancePromise = $.ajax({
        url: app.apiBaseUrl + '/accounts/total-balance',
        type: 'GET',
        timeout: 5000
      });

      // Wait for all requests to complete
      $.when(customersPromise, accountsPromise, balancePromise)
        .done(function(customersResult, accountsResult, balanceResult) {
          self.totalCustomers(customersResult[0] || 0);
          self.totalAccounts(accountsResult[0] || 0);
          self.totalBalance(balanceResult[0] || 0);
          
          // Load recent activities after stats
          self.loadRecentActivities();
        })
        .fail(function(xhr, status, error) {
          console.error('Failed to load dashboard statistics:', error);
          self.errorMessage('Failed to load dashboard data. Please try refreshing the page.');
        })
        .always(function() {
          self.isLoading(false);
        });
    };

    // Load recent activities (mock data for now)
    self.loadRecentActivities = function() {
      // In a real application, this would be an API call
      var mockActivities = [
        {
          id: 1,
          type: 'customer_created',
          description: 'New customer John Smith registered',
          timestamp: new Date(Date.now() - 1000 * 60 * 30), // 30 minutes ago
          icon: 'oj-ux-ico-add-edit-page'
        },
        {
          id: 2,
          type: 'account_created',
          description: 'Savings account ACC004 created',
          timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2 hours ago
          icon: 'oj-ux-ico-credit-card'
        },
        {
          id: 3,
          type: 'customer_updated',
          description: 'Customer Sarah Johnson updated profile',
          timestamp: new Date(Date.now() - 1000 * 60 * 60 * 4), // 4 hours ago
          icon: 'oj-ux-ico-edit'
        }
      ];

      self.recentActivities(mockActivities);
    };

    // Format timestamp for display
    self.formatTimestamp = function(timestamp) {
      var now = new Date();
      var diff = now - timestamp;
      var minutes = Math.floor(diff / (1000 * 60));
      var hours = Math.floor(diff / (1000 * 60 * 60));
      var days = Math.floor(diff / (1000 * 60 * 60 * 24));

      if (minutes < 60) {
        return minutes + ' minutes ago';
      } else if (hours < 24) {
        return hours + ' hours ago';
      } else {
        return days + ' days ago';
      }
    };

    // Refresh dashboard data
    self.refreshDashboard = function() {
      self.loadStats();
    };

    // Navigation helpers
    self.goToCustomers = function() {
      app.router.go('customers');
    };

    self.goToAccounts = function() {
      app.router.go('accounts');
    };

    // Initialize dashboard when view model is created
    self.loadStats();
  }

  return DashboardViewModel;
});