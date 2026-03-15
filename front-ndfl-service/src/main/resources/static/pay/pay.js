angular.module('ndflService').controller('payController', function ($scope, $http, $location) {

    const constPatchPay = window.location.origin + '/pay-service';
    // const constPatchAdmin = window.location.origin + '/admin/projects';

    $scope.loadPay = function (orderId) {
        console.log("loadPay");
        console.log("запрос данных платежу");
        if ($scope.load) {
            alert("Подождите обрабатывается предыдущий запрос по платежам")
        } else {
            $scope.load = true;
            $scope.Pay = null;
            let Filter;
            Filter = $scope.Filt;
            console.log(Filter);
            $http({
                url: constPatchPay + "/order",
                method: "get",
                params: {
                    orderId: orderId
                }


            }).then(function (response) {
                console.log("response :");
                console.log(response);
                console.log("response,data :");
                console.log(response.data);
                $scope.Pay = response.data;
                if($scope.Pay.isCompleted){
                    $scope.Cancel();
                } else{
                    // document.getElementById("PayRobo").submit();
                }
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

    $scope.Cancel = function () {
        $location.path('/order');
    }



    $scope.Filt = {}
    $location.parserFilter($scope.Filt);
    let orderId = $scope.Filt.orderId;
    console.log(orderId);

    $scope.loadPay(orderId);
})