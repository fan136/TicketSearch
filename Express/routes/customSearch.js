var express = require('express');
var router = express.Router();
var request = require('request');
var rp = require('request-promise');
const google_geo_apikey = "AIzaSyC8574CUAaLC-fbCPA6M-oQN0ZyL7TqAQ8";
const google_search_engine = "008493028251880565055:6udy5rbkbiq";


/* test json send fucntions */
router.get('/:keyword', function(req, res, next) {
    const query = "https://www.googleapis.com/customsearch/v1?q=" + req.params.keyword + "&cx=" +
        google_search_engine + "&imgSize=huge&imgType=news&num=8&searchType=image&key=" + google_geo_apikey;
    console.log(query);
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


