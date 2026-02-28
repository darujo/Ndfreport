(function () {
    angular.module('ndflService', ['ngRoute', 'ngStorage', 'oc.lazyLoad', 'dnd']).run(run);

    function run($rootScope, $http, $localStorage, $location) {
        $rootScope.$on('$routeChangeStart', function (event, next, current) {
            if (typeof current !== 'undefined') {
                console.log("detail close");
                document.getElementById("DetailPrim").open = false;
            } else {
                console.log("detail open");
                document.getElementById("DetailPrim").open = true;
            }
        });
        $rootScope.$on('$routeChangeSuccess', function () {
            if (location.hash !== "#!/") {
                $location.parserFilter("");
            }

        });
        console.log($localStorage.authUser)
        if ($localStorage.authUser) {
            $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.authUser.token;
            try {
                let jwt = $localStorage.authUser.token;
                let payLoad = JSON.parse(atob(jwt.split(".")[1]));
                let currTime = parseInt((new Date().getTime() / 1000).toString());
                if (currTime > payLoad.exp) {
                    console.log("токен просрочен");
                    delete $localStorage.authUser;
                    $http.defaults.headers.common.Authorization = '';
                }

            } catch (e) {

            }
        }

    }

})();