var express = require('express');
var router = express.Router();
var rp = require('request-promise');
const google_geo_apikey = "AIzaSyC8574CUAaLC-fbCPA6M-oQN0ZyL7TqAQ8";

/* test json send fucntions */
router.get('/:location', function(req, res, next) {
    let query = 'https://maps.googleapis.com/maps/api/geocode/json?address='
        + req.params.location + '&key=' + google_geo_apikey;
    getJson_fromUrl(query)
        .then(function (data) {
            //console.log(data);
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
            return repos;
        })
        .catch(function (err) {
            console.log('API call is googleGeo failed...');
        });
}
module.exports = router;