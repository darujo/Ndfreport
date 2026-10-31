angular.module('ndflService').config(function ($routeProvider) {
    let ver = "3.1";
    console.log(ver);
    $routeProvider
        .when('/', {
            templateUrl: 'welcome/welcome.html',
            controller: 'welcomeController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('welcome'); // Resolve promise and load before view
                }]
            }
        })
        .when('/order', {
            templateUrl: 'order/order_list.html',
            controller: 'orderListController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('orderList'); // Resolve promise and load before view
                }]
            }
        })
        .when('/order_add', {
            templateUrl: 'order/order_add.html',
            controller: 'orderAddController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('order_add'); // Resolve promise and load before view
                }]
            }
        })
        .when('/pay', {
            templateUrl: 'pay/pay.html',
            controller: 'payController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('pay'); // Resolve promise and load before view
                }]
            }
        })
        .when('/user', {
            templateUrl: 'user/user.html',
            controller: 'userController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('user'); // Resolve promise and load before view
                }]
            }
        })
        .when('/sys/registration', {
            templateUrl: 'user/registration.html',
            controller: 'userController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('user'); // Resolve promise and load before view
                }]
            }
        })
        .when('/sys/email_confirm', {
            templateUrl: 'welcome/welcome.html?ver='.toLowerCase() + ver,
            controller: 'emailConfirmController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('confirm_email'); // Resolve promise and load before view
                }]
            }
        })
        .when('/sys/request_pass', {
            templateUrl: 'user/request_pass.html?ver='.toLowerCase() + ver,
            controller: 'requestPassController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('request_pass'); // Resolve promise and load before view
                }]
            }
        })
        .when('/sys/recovery_password', {
            templateUrl: 'welcome/welcome.html?ver='.toLowerCase() + ver,
            controller: 'recoveryPasswordConfirmController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('recovery_password'); // Resolve promise and load before view
                }]
            }
        })
        .when('/recovery_pass', {
            templateUrl: 'user/recovery_pass.html?ver='.toLowerCase() + ver,
            controller: 'recoveryPassController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('recovery_pass'); // Resolve promise and load before view
                }]
            }
        })
        .when('/sys/sendCode', {
            templateUrl: 'user/send_code.html?ver='.toLowerCase() + ver,
            controller: 'sendCodeController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('send_code'); // Resolve promise and load before view
                }]
            }
        })
        .when('/password_change', {
            templateUrl: 'user/user_password.html',
            controller: 'userController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('password_change'); // Resolve promise and load before view
                }]
            }
        })
        .otherwise({
            redirectTo: '/'
        });
})
