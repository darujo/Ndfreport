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
            name: 'confirm_email', // module
            files: ['user/confirm_email.js?ver='.toLowerCase() + ver]
        }, {
            name: 'request_pass', // module
            files: ['user/request_pass.js?ver='.toLowerCase() + ver]
        }, {
            name: 'recovery_password', // module
            files: ['user/recovery_password.js?ver='.toLowerCase() + ver]
        }, {
            name: 'recovery_pass', // module
            files: ['user/recovery_pass.js?ver='.toLowerCase() + ver]
        }, {
            name: 'send_code', // module
            files: ['user/send_code.js?ver='.toLowerCase() + ver]
        }, {
            name: 'order_add', // module
            files: ['order/order_add.js?ver=' + ver]
        }
        ]
    });
}]);