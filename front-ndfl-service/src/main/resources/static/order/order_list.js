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
                $scope.load = false;
                $scope.OrderList = response.data._embedded.orderDTOList;

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
        $location.path('/order_add').search({orderId: null});
    };

    $scope.payOrder = function (orderId) {
        console.log("pay");
        $location.path('/pay').search({orderId: orderId});
    };
    $scope.getOrderDoc = function (orderId) {
        console.log("getOrderDoc");
        window.location =constPatchOrder +'/document?orderId=' +  orderId;
    };

    $scope.getOrderDocAll = function () {
        console.log("getOrderDoc");
        window.location =constPatchOrder +'/document';
    };
    $scope.getOrderDoc777 = function () {
        $http.get(constPatchOrder + "/document"
            , {
                responseType: 'arraybuffer',
                params: {
                    //Required params
                },
            }).then(function (response) {

                console.log(response.headers('content-disposition'))

                let downloadLink = document.createElement("a");

            document.body.appendChild(downloadLink);
            downloadLink.style = "display: none";
            console.log(response)
            let fName = "response.zip";
            const contentDisposition = response.headers('Content-Disposition');
            if (contentDisposition) {
                const fileNameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                const matches = fileNameRegex.exec(contentDisposition);
                if (matches != null && matches[1]) {
                    fName = matches[1].replace(/['"]/g, '');
                }
            }
            let file = new Blob([response.data], {type: 'application/*'});
//Blob, client side object created to with holding browser specific download popup, on the URL created with the help of window obj.

            downloadLink.href = (window.URL || window.webkitURL).createObjectURL(file);
            downloadLink.download = fName;
            downloadLink.click();
            return response;
        });
    }
    $scope.getOrderDoc7778 = function () {
        console.log("getOrderDoc");
        $http({
            url: constPatchOrder + "/document",
            method: "get",
            // params: {
            //     orderId: orderId
            // },
            transformResponse: angular.identity,
            responseType: 'blob'


        }).then(function (response) {
            console.log("response :");
            console.log(response);
            let filename = "1.zip"
            let dataType = "application/octet-stream";
            console.log(dataType)
            let binaryData = [];
            binaryData.push(response);
            let downloadLink = document.createElement('a');
            downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
            if (filename) {
                downloadLink.setAttribute('download', filename);
            }
            document.body.appendChild(downloadLink);
            downloadLink.click();
            // $scope.OrderList = response.data._embedded.orderDTOList;
            // $scope.load = false;
        }, function errorCallback(response) {
            // $scope.load = false;
            console.log(response)
            if ($location.checkAuthorized(response)) {
                //     alert(response.data.message);
            }

        });
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