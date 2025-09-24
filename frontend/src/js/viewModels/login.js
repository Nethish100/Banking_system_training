define(['knockout', 'jquery', 'appController'], function(ko, $, app) {
  function LoginViewModel() {
    var self = this;
    
    self.username = ko.observable('');
    self.password = ko.observable('');
    self.isLoading = ko.observable(false);
    self.errorMessage = ko.observable('');

    self.isFormValid = ko.computed(function() {
      return self.username().trim().length > 0 && self.password().trim().length > 0;
    });

    self.login = function() {
      if (!self.isFormValid()) {
        self.errorMessage('Please enter both username and password');
        return;
      }

      self.isLoading(true);
      self.errorMessage('');

      $.ajax({
        url: app.apiBaseUrl + '/auth/login',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({username: self.username().trim(), password: self.password()}),
        timeout: 10000,
        success: function(response) {
          if (response.token && response.username) {
            app.setAuthentication(response.token, response.username);
            app.router.go('dashboard');
          } else {
            self.errorMessage('Invalid response from server');
          }
        },
        error: function(xhr, status, error) {
          if (xhr.status === 401) self.errorMessage('Invalid username or password');
          else if (xhr.status === 0) self.errorMessage('Cannot connect to server.');
          else self.errorMessage('Login failed: ' + (xhr.responseJSON?.message || error));
          console.error('Login error:', xhr, status, error);
        },
        complete: function() {
          self.isLoading(false);
        }
      });
    };

    self.fillDemoCredentials = function() {
      self.username('admin');
      self.password('password');
    };

    self.clearForm = function() {
      self.username('');
      self.password('');
      self.errorMessage('');
    };

    self.handleSubmit = function() {
      self.login();
      return false;
    };

    if (app.isAuthenticated()) app.router.go('dashboard');
  }

  return LoginViewModel;
});
