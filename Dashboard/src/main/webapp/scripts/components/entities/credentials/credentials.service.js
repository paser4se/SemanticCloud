'use strict';

angular.module('dashboardApp')
    .factory('Credentials', function ($resource, DateUtils) {
        return $resource('api/credentialss/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
