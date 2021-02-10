var express = require('express');
var router = express.Router();
var request = require('request');
var rp = require('request-promise');
var SpotifyWebApi = require('spotify-web-api-node');
var spotifyApi = new SpotifyWebApi({
    clientId: 'd68e180facac43b89ebddd90786d4ab3',
    clientSecret: '81de741512c54aae87e616758759f3d2',
    redirectUri: 'http://www.example.com/callback'
});

router.get('/:artist', function(req, res, next) {
    getResult(req.params.artist, res);
});

function getResult (artist, res) {
    spotifyApi.searchArtists(artist)
        .then(function(data) {
            console.log('Search artists, data.body');
            res.send(data);
        }, function(err) {
            console.error(err.statusCode);
            if (err.statusCode === 401) {
                resetToken(function () {
                    getResult(artist, res);
                });
            }
        });
}

function resetToken(callback) {
    // Retrieve an access token.
    spotifyApi.clientCredentialsGrant().then(
        function(data) {
            console.log('The access token expires in ' + data.body['expires_in']);
            console.log('The access token is ' + data.body['access_token']);

            // Save the access token so that it's used in future calls
            spotifyApi.setAccessToken(data.body['access_token']);
            callback();
        },
        function(err) {
            console.log('Something went wrong when retrieving an access token', err);
        }
    );
}




module.exports = router;
