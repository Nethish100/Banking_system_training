requirejs.config({
  baseUrl: 'js',
  
  // Path mappings for the logical module names
  paths: {
    'knockout': 'libs/knockout/knockout-3.5.1.debug',
    'jquery': 'libs/jquery/jquery-3.6.0',
    'jqueryui-amd': 'libs/jquery/jqueryui-amd-1.13.0',
    'ojs': 'libs/oj/v14.1.0/debug',
    'ojL10n': 'libs/oj/v14.1.0/ojL10n',
    'ojtranslations': 'libs/oj/v14.1.0/resources',
    'text': 'libs/require/text',
    'signals': 'libs/js-signals/signals',
    'hammerjs': 'libs/hammer/hammer-2.0.8',
    'ojdnd': 'libs/dnd-polyfill/dnd-polyfill-1.0.2',
    'css': 'libs/require-css/css'
  },

  // Shim configurations for modules that do not expose AMD
  shim: {
    'jquery': {
      exports: ['jQuery', '$']
    },
    'knockout': {
      exports: 'ko'
    }
  },

  // This section configures the i18n plugin
  config: {
    ojL10n: {
      merge: {
        'ojtranslations/nls/ojtranslations': 'resources/nls/myTranslations'
      }
    }
  }
});

/**
 * A top-level require call executed by the Application.
 */
require(['ojs/ojbootstrap', 'knockout', 'appController', 'ojs/ojlogger', 'ojs/ojknockout',
  'ojs/ojmodule', 'ojs/ojrouter', 'ojs/ojnavigationlist', 'ojs/ojbutton', 'ojs/ojtoolbar', 
  'ojs/ojmenu', 'ojs/ojtable', 'ojs/ojarraydataprovider', 'ojs/ojinputtext', 'ojs/ojformlayout',
  'ojs/ojvalidationgroup', 'ojs/ojdialog', 'ojs/ojselectsingle', 'ojs/ojinputnumber', 
  'ojs/ojinputpassword', 'ojs/ojtextarea', 'ojs/ojprogress-circle', 'ojs/ojmessage-banner']
function (Bootstrap, ko, app, Logger) {
  
  Bootstrap.whenDocumentReady().then(function () {
    function init() {
      // Bind your ViewModel for the content of the whole page body.
      Logger.info('Application starting...');
      ko.applyBindings(app, document.getElementById('globalBody'));
      Logger.info('Application started successfully');
    }

    // If running in a hybrid environment, we need to wait for the deviceready 
    if (document.body.classList.contains('oj-hybrid')) {
      document.addEventListener("deviceready", init);
    } else {
      init();
    }
  }).catch(function(error) {
    Logger.error('Bootstrap failed: ', error);
  });
});