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
        .when('/', {
            templateUrl: 'agreement/agreement.html',
            controller: 'agreementController',
            resolve: {
                LazyLoadCtrl: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load('agreement'); // Resolve promise and load before view
                }]
            }
        })
        .otherwise({
            redirectTo: '/'
        });
});