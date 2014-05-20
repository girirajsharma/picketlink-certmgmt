'use strict';

/* Controllers */

var picketlinkCertMgmtControllers = angular.module(
		'picketlinkCertMgmtControllers', []);

picketlinkCertMgmtControllers.controller('CreateCertificateController',function($scope, $http) {
					$scope.errors = [];
					$scope.msgs = [];	
					$scope.resetForm = function(userForm) {
						angular.copy({},form);
					};
					$scope.create = function() {
						$scope.errors.splice(0, $scope.errors.length);
	                    $scope.msgs.splice(0, $scope.msgs.length);
						$http(
								{
									method : 'POST',
									url : '/picketlink-certmgmt-server/X509v1Certificate/create',
									params : {
										'alias' : '$scope.alias',
										'subjectDN' : '$scope.subjectDN',
										'keyPassword' : '$scope.keyPassword',
										'validity' : '$scope.validity'
									}
								}).success(
								function(data, status, headers, config) {
									if(data.status == 200) {
										resetForm(userForm);
										$scope.msgs.push({ type: 'success', msg: 'X509v1 Certificate successfully created.' });
									}else {
										$scope.errors.push({ type: 'danger', msg: 'X509v1 Certificate failed to create(Server).' });
									};
								}).error(function(data, status) {
									$scope.errors.push({ type: 'danger', msg: 'X509v1 Certificate failed to create.(Client)' });
						});
					};
				});

picketlinkCertMgmtControllers.controller('ViewCertificateController',function($scope, $rootScope, $location) {
	$scope.errors = [];
	$scope.msgs = [];
	$rootScope.Certificate = [];
	
	$scope.viewCertificate = function() {
		$scope.errors.splice(0, $scope.errors.length);
        $scope.msgs.splice(0, $scope.msgs.length);
		$http(
				{
					method : 'GET',
					url : '/picketlink-certmgmt-server/X509v1Certificate/'+ $scope.keyPassword
				}).success(
				function(data, status, headers, config) {
					if(data.status == 200) {
						$scope.$apply(function () {
							$rootScope.Certificate = data;
						});
					}else {
						$scope.errors.push({ type: 'danger', msg: 'Failed to retrieve X509v1 Certificate.(Server)' });
					};
				}).error(function(data, status, headers, config) {
					$scope.errors.push({ type: 'danger', msg: 'Failed to retrieve X509v1 Certificate.(Client)' });
		});
		$scope.$apply(function () {
			$location.path('/view/'+$scope.keyPassword);
		});
	};
});

picketlinkCertMgmtControllers.controller('GetCertificateController',function($scope, $http, $location) {
	$scope.errors = [];
	$scope.msgs = [];
	$scope.state = true;
	$scope.deleteCertificate = function() {
		$scope.errors.splice(0, $scope.errors.length);
        $scope.msgs.splice(0, $scope.msgs.length);
		$http(
				{
					method : 'DELETE',
					url : '/picketlink-certmgmt-server/X509v1Certificate/'+$scope.keyPassword
				}).success(
				function(data, status, headers, config) {
					if(data.status == 200) {
						$scope.state = false;
						$scope.errors.push({ type: 'success', msg: 'Successfully deleted X509v1 Certificate' });
					}else {
						$scope.errors.push({ type: 'danger', msg: 'Failed to delete X509v1 Certificate.(Server)'});
					};
				}).error(function(data, status, headers, config) {
					$scope.errors.push({ type: 'danger', msg: 'Failed to delete X509v1 Certificate.(Client)' });
		});
	};
});

picketlinkCertMgmtControllers.controller('UpdateCertificateController',function($scope, $http, $location) {
	$scope.errors = [];
	$scope.msgs = [];	
	$scope.updateCertificate = function() {
		$scope.errors.splice(0, $scope.errors.length);
        $scope.msgs.splice(0, $scope.msgs.length);
		$http(
				{
					method : 'POST',
					url : '/picketlink-certmgmt-server/X509v1Certificate/'+$scope.keyPassword,
					params : {
						'alias' : '$scope.alias',
						'subjectDN' : '$scope.subjectDN',
						'keyPassword' : '$scope.keyPassword',
						'validity' : '$scope.validity'
					}
				}).success(
				function(data, status, headers, config) {
					if(data.status == 200) {
						$scope.$apply(function () {
							$scope.errors.push({ type: 'success', msg: 'Successfully updated X509v1 Certificate' });
						});
					}else {
						$scope.errors.push({ type: 'danger', msg: 'Failed to update X509v1 Certificate.(Server)'});
					};
				}).error(function(data, status, headers, config) {
					$scope.errors.push({ type: 'danger', msg: 'Failed to update X509v1 Certificate.(Client)'});
		});
	};
});