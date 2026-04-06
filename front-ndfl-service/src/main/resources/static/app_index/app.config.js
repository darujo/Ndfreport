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
        }, {
            name: 'user', // module
            files: ['user/user.js?ver=' + ver]
        }, {
            name: 'password_change', // module
            files: ['user/user_password.js?ver=' + ver]
        }, {
            name: 'order_add', // module
            files: ['order/order_add.js?ver=' + ver]
        }
        ]
    });
}]);