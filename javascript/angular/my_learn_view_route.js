"use strict"

var viewRoute = angular.module('ViewRoute',[]);

var HomeController = viewRoute.controller('HomeController',['$scope',function($scope){
	console.log('init home...');
}]);

//This shouldn't be put into the controller definition block.
HomeController.resolve = {
	userData : function($http) {
			  console.log('get baidu');
			return $http({
				method: 'GET', 
				url: 'http://localhost'
			});
	}
};
 
 
var PortalController = viewRoute.controller('PortalController',['$scope',function($scope){
	console.log('init portal...');
}]);
PortalController.resolve = {
	userData : function($http) {
			 console.log('.....get sina');
			return $http({
				method: 'GET', 
				url: 'http://localhost'
			});
	}
};
  
angular.module('POC',['ViewRoute'])
        .config(['$routeProvider', function($routeProvider) {
			console.log('----');
			$routeProvider.when('/home', {templateUrl: 'partial/home.html', controller: 'HomeController', resolve: HomeController.resolve});
			$routeProvider.when('/portal', {templateUrl: 'partial/portal.html', controller: 'PortalController', resolve: PortalController.resolve, reloadOnSearch : false});
			$routeProvider.otherwise({redirectTo: '/portal'});
		  }]
		); 

