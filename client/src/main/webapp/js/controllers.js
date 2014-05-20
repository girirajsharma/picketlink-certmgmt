'use strict';

/* Controllers */

var picketlinkApp = angular.module('picketlinkApp', []);

picketlinkApp
		.controller(
				'PicketlinkListCtrl',
				function($scope) {
					$scope.picketlinkFeatures = [
							{
								'name' : 'Java EE Application Security',
								'snippet' : 'Authentication API, Authorization and Permission API, Session Based Identity',
								'age' : 1
							},
							{
								'name' : 'Identity Management',
								'snippet' : 'Built-in Identity Stores for Databases and LDAP, Supports Java EE and Java SE Platforms ',
								'age' : 2
							},
							{
								'name' : 'Federation',
								'snippet' : 'SAML (v2.0 and v1.1), OAuth2, XACML v2, OpenID, WS-Trust STS',
								'age' : 3
							},
							{
								'name' : 'Social Login',
								'snippet' : 'Facebook Connect, Twitter login, Google+ login',
								'age' : 4
							} ];

					$scope.orderProp = 'age';
				});
