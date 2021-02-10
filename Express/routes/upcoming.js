var express = require('express');
var router = express.Router();
var rp = require('request-promise');
const songkick_apikey = "vqZgZ8wxLg93CwBQ";

/* test json send fucntions */
router.get('/:venue', function(req, res, next) {
    let query = 'https://api.songkick.com/api/3.0/search/venues.json?query=' +
        req.params.venue +  '&apikey=' + songkick_apikey;
    getJson_fromUrl(query)
        .then(function (data) {
            //console.log(data);
            const venueJson = data;
            if (venueJson && venueJson.resultsPage && venueJson.resultsPage.results
                && venueJson.resultsPage.results.venue && venueJson.resultsPage.results.venue[0]) {
                const v = venueJson.resultsPage.results.venue[0];
                if (v.id) {
                    let query2 = 'https://api.songkick.com/api/3.0/venues/' + v.id +
                        '/calendar.json?apikey=' + songkick_apikey;
                    getJson_fromUrl(query2)
                        .then(function(data2) {
                        res.send(data2);
                    });
                }
            }
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