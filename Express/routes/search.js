var express = require('express');
var router = express.Router();
var request = require('request');
var rp = require('request-promise');
const ticket_master_apikey="BDoEpLrrOHdC9czT69dplhTeFjUd93R3";
var geohash = require('ngeohash');
/* GET search page. */
router.get('/:keyword/:category/:distance/:unit/:lat/:lon/:from_typein', function(req, res, next) {

    get_ticketMaster_url(req.params, function(url) {
        console.log('ticket url: ' + url);
        getJson_fromUrl(url)
            .then(function (data) {
                //console.log(data);
                res.send(data);
            });
        });
});

function getJson_fromUrl(url) { // part of the code was from the documentation of request-promise
    console.log(url);
    var options = {
        uri: url,
        headers: {
            'User-Agent': 'Request-Promise'
        },
        json: true // Automatically parses the JSON string in the response
    };
    return rp(options)
        .then(function (repos) {
            //console.log(typeof repos);
            return repos;
        })
        .catch(function (err) {
            console.log('API call in getJson failed...');
        });
}

function get_ticketMaster_url(para, callback){
    var from_typein = encodeURI(para.from_typein);
    var lat = para.lat;
    var lon = para.lon;
    var category = para.category;
    var segmentId="";
    var url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" +
        ticket_master_apikey;
    var hashed;
    var unit = para.unit;

    if (unit=='Miles'){
        unit = 'miles';
    } else {
        unit = 'km';
    }

    switch (category){
        case "Default":
            break;
        case "All":
            break;
        case "Music":
            segmentId="KZFzniwnSyZfZ7v7nJ";
            break;
        case "Sports":
            segmentId="KZFzniwnSyZfZ7v7nE";
            break;
        case "ArtsTheatre":
            segmentId="KZFzniwnSyZfZ7v7na";
            break;
        case "Film":
            segmentId="KZFzniwnSyZfZ7v7nn";
            break;
        case "Miscellaneous":
            segmentId="KZFzniwnSyZfZ7v7n1";
            break;
        default:
            break;
    }
    url += ("&radius=" + para.distance + "&unit=" + unit +
        "&segmentId=" + segmentId +  "&keyword=" + encodeURI(para.keyword) + "&geoPoint=");


    if (from_typein==='null') {
        hashed = geohash.encode(lat, lon);
        url += hashed;
        callback(url);
    }else{
        getJson_fromUrl('http://giddytent780hw8.us-east-2.elasticbeanstalk.com/googleGeo/' + from_typein).then(function(jsonObj) {
            var geoContent = jsonObj.results[0].geometry.location;
            lat = geoContent.lat;
            lon = geoContent.lng;
            console.log('lat= ' + lat);
            hashed = geohash.encode(lat, lon);
            url += hashed;
            console.log(url);
            callback(url);
        });
    }
}

module.exports = router;