var express = require('express');
var router = express.Router();
var request = require('request');
var rp = require('request-promise');
const google_geo_apikey = "AIzaSyC8574CUAaLC-fbCPA6M-oQN0ZyL7TqAQ8";

/* test json send fucntions */
router.get('/', function(req, res, next) {
    let query = 'https://maps.googleapis.com/maps/api/geocode/json?address='
        + encodeURI('los angeles') + '&key=' + google_geo_apikey;
    getJson_fromUrl('http://ip-api.com/json')
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


// if (from_typein) {
//
//     let geoContent = getJson_fromUrl(query);
//     console.log(geoContent);
//     let geoContentLatLon = geoContent.results[0].geometry.location;
//     lat = geoContentLatLon.lat;
//     lon = geoContentLatLon.lon;
// }
// return geohash.encode(lat, lon);