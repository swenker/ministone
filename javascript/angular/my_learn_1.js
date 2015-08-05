"use strict"

var myLearn1 = angular.module('myLearn1',[]);

myLearn1.controller('handleGreetingController',['$scope',function($scope){
	$scope.greeting = "Hola";	
	$scope.sayHello = function(user){
		//return $scope.greeting +","+user;
	}
}]);

myLearn1.controller('handleSleepController',['$scope','gowork',function($scope, gowork){
	$scope.time = "now";	
	console.log(typeof(gowork));
	$scope.reason = gowork;
	console.log($scope.reason);
	$scope.sleep = function(){
		return "sleep "+$scope.time 
	}
	//console.log(typeof($scope.sleep));
}]);

//Service 
myLearn1.factory('gowork',['$window',function(win){
	//The return must be a function? 
	//alert(''+$location.path());
	return function(){win.alert('abc');}
}]);


