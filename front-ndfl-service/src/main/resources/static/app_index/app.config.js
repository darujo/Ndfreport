angular.module('ndflService').config(["$ocLazyLoadProvider", function ($ocLazyLoadProvider) {
    let ver = "1.1"
    $ocLazyLoadProvider.config({
        'debug': true, // For debugging 'true/false'
        'events': true, // For Event 'true/false'
        'modules': [{ // Set modules initially
            name: 'orderList', // module
            files: ['order/order_list.js?ver='.toLowerCase() + ver]
        }, {
            name: 'welcome', // module
            files: ['welcome/welcome.js?ver=' + ver]
        }, {
            name: 'pay', // module
            files: ['pay/pay.js?ver=' + ver]
        }
        ]
    });
}]);