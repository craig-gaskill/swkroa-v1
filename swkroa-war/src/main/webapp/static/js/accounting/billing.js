/**
 * (c) 2014 CAGST Solutions: http://www.cagst.com/solutions
 *
 * Provides functionality needed for the Deposit pages.
 *
 * Author:  Craig Gaskill
 */

swkroaApp.controller('billingController', ['$scope', '$http',
    function($scope, $http) {

  $scope.billingRuns = [];

  $scope.getBillingRuns = function() {
    var url = "/api/billing/";

    $http.get(url).success(function(data) {
      $scope.billingRuns = data;
    });
  };

  $scope.getBillingRuns();
}]);
