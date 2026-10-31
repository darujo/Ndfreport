angular.module('ndflService').controller('indexController', function ($rootScope, $scope, $http, $location, $localStorage) {
    const constPatchAuth = window.location.origin;

    $scope.loadFilter = null;
    const techUrl = "/sys/"
    let myHash;
    let myPath = undefined;
    $scope.tryToAuth = function () {
        $http.post(constPatchAuth + '/auth', $scope.user)
            .then(function successCallback(response) {
                if (response.data.token) {
                    $location.setToken($scope.user.username, response.data.token)

                    $scope.user = {
                        username: null,
                        password: null
                    };

                    console.log("-----1----", myHash)
                    $location.openPath(myHash, myFilter);
                    document.getElementById("DetailPrim").open = true;
                }
            }, function errorCallback(response) {
                console.log(response);
                if(response.data.message) {
                    alert(response.data.message)
                }
                else {
                    alert("Не удается авторизоваться!!!")
                }
            });
    };

    $location.backPage = function () {
        $scope.getUser();
        if (myHash === "userPassword".toLowerCase()) {
            console.log("-----2----")
            $location.path("").search({});
        } else {
            console.log("-----3----", myHash)
            $location.openPath(myHash, myFilter);
        }
    }

    $location.setToken = function (username, token) {
        $http.defaults.headers.common.Authorization = 'Bearer ' + token;
        $localStorage.authUser = {username: username, token: token};
        init();
    }
    $scope.getUser = function () {
        console.log("getUser");
        console.log($localStorage.authUser);
        if (typeof $localStorage.authUser !== "undefined") {
            let nikName = $localStorage.authUser.username;
            console.log("getUser")
            // document.getElementById("UserName").value = nikName;

            $http.get(constPatchAuth + '/users/user?nikName=' + nikName)
                .then(function successCallback(response) {
                    console.log(response)
                    $scope.UserLogin = response.data;
                    $location.UserLogin = $scope.UserLogin;
                    if ($scope.UserLogin.passwordChange && myPath !== undefined && !myPath.startsWith("/recovery_pass")) {
                        console.log("-----4----")
                        $location.openPath('/userPassword'.toLowerCase());

                    }


                    // document.getElementById("UserName").value = response.data.lastName + " " + response.data.firstName + " " + response.data.patronymic;
                }, function errorCallback(response) {
                    console.log(response);
                });
        }
    }
    $scope.tryToLogout = function () {
        $scope.clearUser();
        $scope.user = {
            username: null,
            password: null
        };
        // if ($scope.user.username) {
        //     $scope.user.username = null;
        // }
        // if ($scope.user.password) {
        //     $scope.user.password = null;
        // }
        $location.path('/').search({});
    };

    $scope.clearUser = function () {
        delete $localStorage.authUser;
        $http.defaults.headers.common.Authorization = '';
    };

    $scope.isUserLoggedIn = function (reg) {
        if ($localStorage.authUser || (reg && (location.hash.startsWith(techUrl, 2) || location.hash === techUrl))) {
            return true;
        } else {
            if (location.hash.substring(2) !== "/" && location.hash !== "/") {
                if (location.hash.startsWith(myPath, 2)) {
                    myPath = undefined;
                } else if (myPath !== undefined && (!myPath.startsWith(techUrl) && myPath !== techUrl)) {
                    console.log("-----5---- 111")
                    $location.openPath('/');
                }
                if (myPath === undefined
                    && (!location.hash.startsWith(techUrl, 2) && location.hash !== techUrl)
                ) {
                    console.log("-----5----222")
                    $location.openPath('/');
                }
            }
            return false;
        }


    };

    $scope.isUserLoggedInAndPasOk = function () {
        if (typeof $scope.UserLogin === "undefined") {
            return $scope.isUserLoggedIn();
        } else {
            return !$scope.UserLogin.passwordChange && $scope.isUserLoggedIn();
        }
    };


    $location.checkAuthorized = function (response, check404) {
        console.log("response.status");
        console.log(response.status);
        if (parseInt(response.status) === 500) {
            alert("Произошла ошибка. Обратитесь к администратору. " + response.data.message);
            return false;
        } else if (parseInt(response.status) === 404) {
            if (check404 && response.data.message) {
                alert(response.data.message);
            }
            return false;
        } else if (parseInt(response.status) === 403) {
            alert("Нет прав: " + response.data);
            return false;
        } else if (parseInt(response.status) === 401) {
            checkToken("Вы не авторизованы " + response.data);
            $scope.tryToLogout();
            return false;
        }
        return true;

    }

    $location.openPath = function (path, param) {
        console.log("установим страницу", path, myPath, location.hash)
        console.log(path)
        //myPath = path;
        if (path !== undefined && path !== "/") {
            myPath = path;
        } else {
            if (location.hash !== "/" && location.hash !== "#!/") {
                myPath = location.hash;
                if (myPath.startsWith("#/")) {
                    myPath = myPath.substring(2);
                }
                let filter = {}
                $location.parserFilter(filter)
                console.log("Получаем адрес")
                console.log(myHash)
                console.log(myFilter)
            }
        }
        if (param) {
            $location.path(path).search(param);
        } else {
            $location.path(path).search({});
        }

    }

    let myFilter = {};
    $location.parserFilter = function (filter) {
        console.log("parserFilter");
        console.log(location);
        console.log(location.href);
        let strPars;
        console.log(location.href.lastIndexOf("#!"))
        console.log(location.href.indexOf("?"))
        if (location.href.indexOf("?") > 0 && location.href.lastIndexOf("#!") > location.href.indexOf("?")) {
            strPars = location.href.substring(location.href.lastIndexOf("#!"));
            console.log(strPars)
            window.open(location.origin + "/" + strPars, "_self");
            return;
        } else {
            strPars = location.href;
        }
        if (strPars.indexOf("?") !== -1) {
            console.log(strPars)
            let paramsStr = new URLSearchParams(strPars.substring(strPars.lastIndexOf("?")));
            for (let [key, value] of paramsStr.entries()) {
                if (key.toLowerCase().indexOf("stage") !== -1
                    || key.toLowerCase().indexOf("size") !== -1
                    || key.toLowerCase().indexOf("sap") !== -1
                    || key.indexOf("period") !== -1
                    || key.toLowerCase().indexOf("kid") !== -1
                    || key.toLowerCase().indexOf("type") !== -1
                ) {
                    console.log("int");
                    console.log(key);
                    console.log(value);
                    filter[key] = parseInt(value);
                    if (isNaN(filter[key])) {
                        filter[key] = value;
                    }
                } else if (key.toLowerCase().indexOf("date") !== -1) {
                    console.log(key);

                    console.log(paramsStr.get(key))
                    console.log(new Date(paramsStr.get(key)));

                    filter[key] = new Date(paramsStr.get(key));
                } else if (key.indexOf("weekSplit") !== -1
                    || key.indexOf("workTask") !== -1
                    || key.indexOf("workTime") !== -1
                    || key.indexOf("workPercent") !== -1
                    || key.indexOf("addTotal") !== -1
                    || key.indexOf("ziSplit") !== -1
                    || key.indexOf("hideNotTime") !== -1
                    || key.indexOf("avail") !== -1
                    || key.indexOf("amountRate") !== -1

                ) {
                    console.log(key);

                    console.log(paramsStr.get(key))
                    console.log(value)
                    filter[key] = paramsStr.get(key) !== "false";
                } else if (key.indexOf("listId") !== -1) {
                    console.log("listId");
                    console.log(typeof filter[key]);
                    console.log(paramsStr.get(key));
                    console.log(paramsStr.getAll(key));

                    if (typeof filter[key] === "undefined") {
                        filter[key] = [];
                    }
                    filter[key].push(parseInt(value));
                    console.log(filter[key]);
                } else {
                    console.log(key);
                    paramsStr.get(key);
                    console.log(value);
                    filter[key] = paramsStr.get(key);
                }

            }
            myFilter = filter;
            myHash = location.hash.substring(location.hash.indexOf("/") + 1, location.hash.indexOf("?"));
        } else {
            myHash = location.hash.substring(location.hash.indexOf("/") + 1);
            myFilter = {};
        }
        console.log("myHash")
        console.log(myHash);
        console.log(filter);
        return filter;
    }
    
    let checkToken = function (message) {
        if ($localStorage.authUser) {
            $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.authUser.token;
            try {
                let jwt = $localStorage.authUser.token;
                let payLoad = JSON.parse(atob(jwt.split(".")[1]));
                let currTime = parseInt((new Date().getTime() / 1000).toString());
                if (currTime > payLoad.exp) {
                    alert("Токен просрочен авторизуйтесь заново.");
                    delete $localStorage.authUser;
                    $http.defaults.headers.common.Authorization = '';
                } else {
                    alert(message);
                }
            } catch (e) {

            }
        }
    }
    $scope.registration = function (){
        console.log("user reg")
        $location.openPath('/sys/registration')
        $scope.UserRegistr = true;
    }

    let init = function () {
        $scope.getUser();

    }
    if ($scope.isUserLoggedIn) {
        init();
    }

    $scope.request_pass = function () {
        $location.openPath('/sys/request_pass')
    }

    $scope.send_code = function () {
        $location.openPath('/sys/sendCode')
    }

    console.log("loan index.js end");
})