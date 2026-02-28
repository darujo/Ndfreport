angular.module('ndflService').config(["$ocLazyLoadProvider", function ($ocLazyLoadProvider) {
    let ver = "1.1"
    $ocLazyLoadProvider.config({
        'debug': true, // For debugging 'true/false'
        'events': true, // For Event 'true/false'
        'modules': [{ // Set modules initially
            name: 'agreement', // module
            files: ['agreement/agreement.js?ver='.toLowerCase() + ver]
        }, {
            name: 'welcome', // module
            files: ['welcome/welcome.js?ver=' + ver]
        }
        ]
    });
}]);