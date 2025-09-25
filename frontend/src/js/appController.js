define(['knockout', 'ojs/ojrouter', 'ojs/ojmodule-element-utils', 'ojs/ojknockouttemplateutils', 'ojs/ojarraydataprovider', 'jquery'], 
function(ko, Router, moduleUtils, KnockoutTemplateUtils, ArrayDataProvider, $) {

  function ControllerViewModel() {
    var self = this;

    // Router setup
    self.router = Router.rootInstance;
    self.router.configure({
      'dashboard': {label: 'Dashboard', isDefault: true},
      'customers': {label: 'Customers'},
      'accounts': {label: 'Accounts'}
    });

    // Authentication state - enabled for full login protection
    self.isAuthenticated = ko.observable(!!localStorage.getItem('authToken'));
    self.currentUser = ko.observable(localStorage.getItem('username') || '');

    // Base API URL
    self.apiBaseUrl = 'http://localhost:8080/api';

    // Navigation data
    self.navDataSource = ko.observableArray([
      {name: 'Dashboard', id: 'dashboard', iconClass: 'oj-navigationlist-item-icon oj-ux-ico-bar-chart'},
      {name: 'Customers', id: 'customers', iconClass: 'oj-navigationlist-item-icon oj-ux-ico-contact-group'},
      {name: 'Accounts', id: 'accounts', iconClass: 'oj-navigationlist-item-icon oj-ux-ico-credit-card'}
    ]);

    // Module configuration
    self.moduleConfig = ko.observable({'view':[], 'viewModel':null});

    // Load module dynamically
    self.loadModule = function() {
      ko.computed(function() {
        var name = self.router.moduleConfig.name();
        var viewPath = 'text!views/' + name + '.html';
        var modelPath = 'viewModels/' + name;
        moduleUtils.createView({'viewPath': viewPath}).then(function(view) {
          moduleUtils.createViewModel({'viewModelPath': modelPath}).then(function(vm) {
            self.moduleConfig({'view': view, 'viewModel': vm});
          }).catch(function(err) {
            console.error('Error loading viewModel:', err);
            self.moduleConfig({'view': '<h2>Error loading ViewModel</h2>', 'viewModel': {}});
          });
        }).catch(function(err) {
          console.error('Error loading view:', err);
          self.moduleConfig({'view': '<h2>Error loading View</h2>', 'viewModel': {}});
        });
      });
    };

    // Navigation
    self.navAction = function(event) {
      if (event.target.id !== event.currentTarget.id) return;
      var data = event.detail;
      if (data.value) self.router.go(data.value);
    };

    // Set authentication
    self.setAuthentication = function(token, username) {
      localStorage.setItem('authToken', token);
      localStorage.setItem('username', username);
      self.isAuthenticated(true);
      self.currentUser(username);
      console.log('User authenticated:', username);
    };

    // Logout
    self.logout = function() {
      localStorage.removeItem('authToken');
      localStorage.removeItem('username');
      self.isAuthenticated(false);
      self.currentUser('');
      console.log('User logged out');
      self.router.go('login');
    };

    // Redirect if not authenticated - disabled for development
    // self.router.currentState.subscribe(function(state) {
    //   if (state && state.id !== 'login' && !self.isAuthenticated()) {
    //     self.router.go('login');
    //   }
    // });

    // Global AJAX setup for JWT
    $(document).ajaxSend(function(event, jqxhr, settings) {
      var token = localStorage.getItem('authToken');
      if (token && !settings.url.includes('/auth/login')) {
        jqxhr.setRequestHeader('Authorization', 'Bearer ' + token);
      }
    });

    // Global AJAX error handling
    $(document).ajaxError(function(event, jqxhr) {
      if (jqxhr.status === 401 || jqxhr.status === 403) {
        console.warn('JWT expired or invalid - logging out');
        self.logout();
      }
    });

    // Initialize router
    Router.defaults['urlAdapter'] = new Router.urlParamAdapter();
    self.router.go().then(function() {
      self.loadModule();
    });

    console.log('AppController initialized');
  }

  return new ControllerViewModel();
});
