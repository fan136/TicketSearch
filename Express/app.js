var createError = require('http-errors');
var express = require('express');
var cors = require('cors');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var searchRouter = require('./routes/search');
var googleGeoRouter = require('./routes/googleGeo');
var autocompletionRouter = require('./routes/autocompletion');
var spotifyRouter = require('./routes/spotify');
var customSearchRouter = require('./routes/customSearch');
var venueRouter = require('./routes/venue');
// var songkickVenueRouter = require('./routes/songkickVenue');
// var songkickUpcomingRputer = require('./routes/songkickUpcoming');
var upcomingRouter = require('./routes/upcoming');


var geohash = require('ngeohash');
var app = express();

app.use(cors());
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/search', searchRouter);
app.use('/googleGeo', googleGeoRouter);
app.use('/autocompletion', autocompletionRouter);
app.use('/spotify', spotifyRouter);
app.use('/customSearch', customSearchRouter);
app.use('/venue', venueRouter);
// app.use('/songkickVenue', songkickVenueRouter);
// app.use('/songkickUpcoming', songkickUpcomingRputer);
app.use('/upcoming', upcomingRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;

// Listen to port 5000
app.listen(8081, function () {
    console.log('Dev app listening on port 8081!');
});
