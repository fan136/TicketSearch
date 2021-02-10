var express = require('express');
var router = express.Router();
var request = require('request');
var rp = require('request-promise');
const ticket_master_apikey="BDoEpLrrOHdC9czT69dplhTeFjUd93R3";

/* test json send fucntions */
router.get('/:venue', function(req, res, next) {
    let query = "https://app.ticketmaster.com/discovery/v2/venues?apikey=" + ticket_master_apikey
        +"&&keyword=" + encodeURI(req.params.venue);
    getJson_fromUrl(query)
        .then(function (data) {
            console.log(data);
            res.send(data);
        });
});


function getJson_fromUrl(url) { // part of the code was from the documentation of request-promise
    var options = {
        uri: url,
        headers: {
            'User-Agent': 'Request-Promise'
        },
        json: true // Automatically parses the JSON string in the response
    };

    return rp(options)
        .then(function (repos) {
            console.log('User has %d repos', repos.length);
            return repos;
        })
        .catch(function (err) {
            console.log('API call failed...');
        });
}
module.exports = router;

