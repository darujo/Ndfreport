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
        .when('/pay', {
            templateUrl: 'pay/pay.html',
            controller: 'payController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('pay'); // Resolve promise and load before view
                }]
            }
        })
        .otherwise({
            redirectTo: '/'
        });
});