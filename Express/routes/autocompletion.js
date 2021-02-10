var express = require('express');
var router = express.Router();
var rp = require('request-promise');
const ticket_master_apikey = "BDoEpLrrOHdC9czT69dplhTeFjUd93R3";

/* test json send fucntions */
router.get('/:word', function(req, res, next) {
    //example url
    //https://app.ticketmaster.com/discovery/v2/suggest?apikey=YOUR_API_KEY&keyword=lake
    let query = 'https://app.ticketmaster.com/discovery/v2/suggest?apikey='
        + ticket_master_apikey + '&keyword=' + req.params.word;
    console.log(query);
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