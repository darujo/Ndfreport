
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
            $scope.Pay = {isCompleted: true,
                merchantLogin:null,
                outSum:null,
            invId:null,
            signatureValue:null,
            isTest:null};
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
                if ($scope.Pay && $scope.Pay.isCompleted) {
                    $scope.Cancel();
                } else {
                    document.getElementById("MerchantLogin").value = $scope.Pay.merchantLogin;
                    document.getElementById("OutSum").value = $scope.Pay.outSum;
                    document.getElementById("InvId").value = $scope.Pay.invId;
                    document.getElementById("Description").value = $scope.Pay.description;
                    document.getElementById("SignatureValue").value = $scope.Pay.signatureValue;
                    document.getElementById("IsTest").value = $scope.Pay.isTest;
                    document.getElementById("PayRobo").submit();
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