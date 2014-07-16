'use strict';


var picketlinkCertMgmtDirectives = angular.module('picketlinkCertMgmtDirectives',[]);

picketlinkCertMgmtDirectives.directive('ngFocus', [ function() {
	var FOCUS_CLASS = "ng-focused";
	return {
		restrict : 'A',
		require : 'ngModel',
		link : function(scope, element, attrs, ctrl) {
			ctrl.$focused = false;
			element.bind('focus', function(evt) {
				element.addClass(FOCUS_CLASS);
				scope.$apply(function() {
					ctrl.$focused = true;
				});
			}).bind('blur', function(evt) {
				element.removeClass(FOCUS_CLASS);
				scope.$apply(function() {
					ctrl.$focused = false;
				});
			});
		}
	};
} ]);

picketlinkCertMgmtDirectives.directive('regexValidate', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, elem, attr, ctrl) {
            var flags = attr.regexValidateFlags || '';
            var regex = new RegExp(attr.regexValidate, flags);            
            
            ctrl.$parsers.unshift(function(value) {
                var valid = regex.test(value);
                ctrl.$setValidity('regexValidate', valid);
                return valid ? value : undefined;
            });
            
            ctrl.$formatters.unshift(function(value) {
                ctrl.$setValidity('regexValidate', regex.test(value));
                return value;
            });
        }
    };
});