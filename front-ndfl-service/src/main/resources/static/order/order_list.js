angular.module('ndflService').controller('orderListController', function ($scope, $http, $location) {

    const constPatchOrder = window.location.origin + '/order-service/order';
    // const constPatchAdmin = window.location.origin + '/admin/projects';

    $scope.loadOrder = function () {
        $scope.findPage(0);
    };


    $scope.findPage = function () {
        console.log("findPage");
        console.log("запрос данных проектов");
        if ($scope.load) {
            alert("Подождите обрабатывается предыдущий запрос")
        } else {
            $scope.load = true;
            $scope.OrderList = null;
            let Filter;
            Filter = $scope.Filt;
            console.log(Filter);
            $http({
                url: constPatchOrder,
                method: "get",
                params: {
                    code: Filter ? Filter.code : null,
                    name: Filter ? Filter.name : null

                }


            }).then(function (response) {
                console.log("response :");
                console.log(response);
                console.log("response,data :");
                console.log(response.data);
                $scope.OrderList = response.data._embedded.orderDTOList;
                $scope.load = false;
            }, function errorCallback(response) {
                $scope.load = false;
                console.log(response)
                if ($location.checkAuthorized(response)) {
                    //     alert(response.data.message);
                }

            });
        }
    };
    $scope.filterOrder = function () {
        console.log("filterProject")
        // $location.saveFilter("orderListFilter", $scope.Filt);
        $scope.findPage();
    };

    $scope.createOrder = function () {
        console.log("createOrder");
        $location.path('/orderEdit').search({orderId: null});
    };

    $scope.payOrder = function (orderId) {
        console.log("pay");
        $location.path('/pay').search({orderId: orderId});
    };

    $scope.deleteOrder = function (orderId) {
        $http.delete(constPatchOrder + "/" + orderId)
            .then(function (response) {
                console.log("Delete response")
                console.log(response);
                $scope.loadOrder();
            }, function errorCallback(response) {
                console.log(response)
                if ($location.checkAuthorized(response)) {
                    alert(response.data.message);
                }
            });
    };

    $scope.clearFilter = function (load) {
        console.log("clearFilter");
        if (!load) {
            $scope.Filt = {};
        }
        console.log($scope.Filt);
        if (load) {
            // $scope.filterRole();
        }
    }

    // $scope.Filt = $location.getFilter("orderFilter");

    $scope.clearFilter(false);
    console.log("Start");
    console.log("Show ok");

    $scope.loadOrder();
})