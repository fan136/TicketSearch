import {Component, Inject, OnInit, ViewChild, ElementRef, HostBinding, Renderer2} from '@angular/core';
import { environment } from './../environments/environment';
import { Injectable } from '@angular/core';
import {HttpClient, HttpRequest, HttpHeaders} from '@angular/common/http';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import * as moment from 'moment';
import {DOCUMENT} from '@angular/common';
import {trigger, state, style, animate, transition} from '@angular/animations';


declare var validation: any;
declare var google: any;
declare var renderMap: any;

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    animations: [
        trigger('slide', [
            state('left', style({ transform: 'translateX(0)' })),
            state('right', style({ transform: 'translateX(-50%)' })),
            transition('* => *', animate(300))
        ]),
    ]
})
@Injectable()

export class AppComponent implements OnInit {
    // title = 'My First Angular App';
    isButtonDisabled = true;
    isTypeinDisabled = true;
    search_radio_current_geo = 'checked';
    radioBtn = 'option1';
    lat = null;
    lon = null;
    // lat = 33.9779;
    // lon = -118.4525;
    venue_element = null;
    geoInfo = null;
    geoError;
    expressServer = window.location.origin;
    ticketJSON = null;
    suggestedWord = null;
    // search_keyword = '';
    search_category = 'All';
    search_distance = 10;
    search_unit = 'Miles';
    search_location;
    search_keyword = new FormControl();
    options: string[] = [];
    formValid = false;
    isResultsChosen = true;
    isFavoritesChosen = false;
    allEvents = null;
    showEventDetails = null;
    eventDetailName = '';
    selectedTab = 1;
    allArtists = [];
    allValidArtists = [];
    spotifyRequests = [];
    imgRequests = [];
    imgRequestsNames = [];
    venueName = null;
    venueObj = null;
    venueJson = null;
    mapObj;
    upJson;
    upcomingEvents = null;
    upcomingEventsSorted = null;
    sortType = 'Default';
    sortDirection = 'Ascending';
    show5 = true;
    showMoreLess = 'Show More';
    isOpen = false;
    artist_team = null;
    event_venue = null;
    event_date = null;
    event_cat = null;
    pricerange = null;
    ticket_status = null;
    buy_ticket = null;
    seat_map = null;
    storage = window.localStorage;
    allFavEvents = [];
    tweetText = '';
    submitted = false;
    showBar = false;
    isEntrySelected = false;
    listView = true;
    inFav = false;
    inDetail = false;
    returnFromFav = false;
    view = 1;
    originURL;
    @ViewChild('mapArea') mapAreaElement: ElementRef;
    constructor( private http: HttpClient, @Inject(DOCUMENT) document, private rd: Renderer2) {
        console.log(environment.production); // Logs false for default environment
    }
    ngOnInit() {
        if (!this.geoInfo) {
            this.getGeo();
        }
        console.log('ngOnInit');
        console.log(window);
        document.addEventListener('click', function(event) {
            validation(event);
        });
        this.loadFav();
        // window.localStorage.clear();
            //
            // console.log('start render map');
            // const dest = {lat: 100, lng: 100};
            // const map = new google.maps.Map(document.getElementById('mapArea'), {zoom: 13, center: dest});
            // const marker = new google.maps.Marker({position: dest, map: map});
    }
    public refresh() {
        window.location.reload();
    }
    private getGeo() {
        this.http.get('http://ip-api.com/json')
            .subscribe(data => {
                this.geoInfo = data;
                this.lat = this.geoInfo['lat'];
                this.lon = this.geoInfo['lon'];
                console.log(this.lat + ', ' + this.lon);
                },
                error1 => console.log('ip-api failed'),
                () => console.log('ip-api complete')
            );
    }
    public validCheck() {
        console.log('see:' + this.search_location);
        if (!this.lat || !this.lon || !this.search_keyword.value || !this.search_keyword.value.trim() ||
            (this.radioBtn === 'option2' && !this.search_location)) {
            this.isButtonDisabled = true;
        } else {
            this.isButtonDisabled = false;
        }
    }
    public searchSubmit() {
        this.showBar = true;
        this.submitted = true;
        console.log('search button clicked');
        let loc;
        if (this.search_location) {
            loc = this.search_location;
        } else {
            loc = 'null';
        }
        let url = this.expressServer + '/search/';
        url += encodeURI(this.search_keyword.value) + '/' + this.search_category + '/' + this.search_distance + '/'
            + this.search_unit + '/' + this.lat + '/' + this.lon + '/' + encodeURI(loc);
        console.log('send url to NodeJS:' + url);
        this.http.get(url)
            .subscribe(data => {
                this.ticketJSON = data;
                // console.log(this.ticketJSON);
                this.renderEvents(this.ticketJSON);
                this.showBar = false;
            },
            error1 => console.log('form get failed'),
            () => console.log('form get complete')
        );
    }
    public wordSuggest() {
        this.validCheck();
        this.options = [];
        const word = this.search_keyword.value;
        if (word !== '') {
            this.http.get(this.expressServer + '/autocompletion/' + word)
                .subscribe( data => {
                    this.suggestedWord = data;
                    console.log(this.suggestedWord);
                    // list candidates in options
                    let candidates = null;
                    if (this.suggestedWord._embedded && this.suggestedWord._embedded.attractions) {
                        candidates = this.suggestedWord._embedded.attractions;
                        for (const i of candidates) {
                            console.log(i);
                            this.options.push(i.name);
                        }
                    }
                },
                    error1 => console.log('word suggest failed'),
                    () => console.log('word suggest completed')
                );
        }
    }
    public clickOnRadio1() {
        this.isButtonDisabled = true;
    }

    private renderEvents(jsonObj) {
        if (!jsonObj || !jsonObj.page || !jsonObj.page.totalElements || jsonObj.page.totalElements === 0) {
            this.allEvents = null;
        } else {
            this.allEvents = jsonObj._embedded.events;
            // sort event by date
            this.allEvents.sort(function (a, b) {
                if (Date.parse(a.dates.start.localDate) <= Date.parse(b.dates.start.localDate)) {
                    return -1;
                } else {
                    return 1;
                }
            });
            // this.storage.setItem('1', JSON.stringify(this.allEvents[0]));
            // this.allEvents[0] = JSON.parse(this.storage.getItem('1'));
        }
    }
    public getDates(event) {
        if (event && event.dates && event.dates.start && event.dates.start.localDate) {
            return event.dates.start.localDate;
        }
    }
    public getEvent(event) {
        if (event && event.name) {
            const name = event.name;
            if (name.length < 35) {
                return name;
            } else {
                let shortName = name.substring(0, 35);
                while (shortName.charAt(shortName.length - 1) === ' ') {
                    shortName = shortName.substring(0, shortName.length - 1);
                }
                shortName += '...';
                return shortName;
            }
        }
    }
    public getTooltip(event) {
        if (event && event.name && event.name.length >= 35) {
            return event.name;
        }
    }
    public getCategory(event) {
        let cat = '';
        if (event && event.classifications && event.classifications[0] && event.classifications[0].genre
            && event.classifications[0].genre.name) {
            cat += event.classifications[0].genre.name;
        }
        if (event && event.classifications && event.classifications[0] && event.classifications[0].segment
            && event.classifications[0].segment.name) {
            if (cat !== '') {
                cat += '-';
            }
            cat += event.classifications[0].segment.name;
        }
        if (cat !== '') {
            return cat;
        }
    }
    public getVenueInfo(event) {
        if (event && event._embedded && event._embedded.venues && event._embedded.venues[0] && event._embedded.venues[0].name) {
            return event._embedded.venues[0].name;
        }
    }
    public getEventDetails(event) {
        const event_json = event; // convert to event_json for code reuse from hw6
        if (event_json.name) {
            this.eventDetailName = event_json.name;
        }
        // artist/teams
        this.artist_team = '';
        if (event_json._embedded && event_json._embedded.attractions && event_json._embedded.attractions.length > 0) {
            const at = event_json._embedded.attractions;
            for (let i = 0; i < at.length; i++) {
                if (at[i].name) {
                    // if (at[i].url) {
                    //     artist_team += ('<a href=\"' + at[i].url + '\" target=\"_blank\">' + at[i].name + ' | ');
                    // } else {
                    //     artist_team += (at[i].name + ' | ');
                    // }
                    this.artist_team += (at[i].name + ' | ');
                }
            }
            if (this.artist_team !== '') {
                this.artist_team = this.artist_team.slice(0, -3);
            }
        }
        // venue
        this.event_venue = null;
        if (event_json._embedded && event_json._embedded.venues[0] && event_json._embedded.venues[0].name) {
            this.event_venue = event_json._embedded.venues[0].name;
        }
        // time
        this.event_date = null;
        if (event_json.dates && event_json.dates.start && event_json.dates.start.localDate) {
            const s = event_json.dates.start;
            this.event_date = moment(s.localDate).format('ll') + '  ' + s.localTime;
        }
        // cat
        this.event_cat = '';
        if (event_json.classifications && event_json.classifications[0]) {
            const classification = event_json.classifications[0];
            if (classification.genre && classification.genre.name) {
                this.event_cat += classification.genre.name;
            }
            if (classification.segment && classification.segment.name) {
                if (this.event_cat !== '') {
                    this.event_cat += ' | ';
                }
                this.event_cat += classification.segment.name;
            }
        }
        // price
        this.pricerange = '';
        if (event_json.priceRanges && event_json.priceRanges[0]) {
            const range = event_json.priceRanges[0];
            if (range.min) {
                this.pricerange += ('$' + range.min);
            }
            if (range.max) {
                if (this.pricerange) {
                    this.pricerange += ' ~ ';
                }
                this.pricerange += ('$' + range.max);
            }
        }
        // ticket status
        this.ticket_status = null;
        if (event_json.dates && event_json.dates.status && event_json.dates.status.code) {
            this.ticket_status = event_json.dates.status.code;
        }
        // buy
        this.buy_ticket = null;
        if (event_json.url) {
            this.buy_ticket = event_json.url;
        }
        // seat map
        this.seat_map = null;
        if (event_json.seatmap && event_json.seatmap.staticUrl) {
            this.seat_map = event_json.seatmap.staticUrl;
        }
        // tweet
        let tweet = '';
        tweet += 'Check out ' + this.eventDetailName + ' located at ' + this.event_venue + '. Website: '
            + event.url + '#CSCI571EventSearch';
        this.tweetText = 'https://twitter.com/intent/tweet?text=' + encodeURI(tweet);
    }
    public showEventEntry(event) {
        this.showBar = true;
        this.showEventDetails = event;
        this.getEventDetails(event);
        // (click)="getArtistList()"
        this.allArtists = this.showEventDetails._embedded.attractions;
        this.allValidArtists = [];
        this.spotifyRequests = [];
        this.imgRequests = [];
        this.imgRequestsNames = [];
        this.venueObj = this.showEventDetails._embedded.venues[0];
        this.venueName = this.venueObj.name;
        console.log(this.allArtists);
        let feedback1 = false;
        let feedback2 = false;
        let feedback3 = false;
        let feedback4 = false;
        let i = 0;
        for (i = 0; i < this.allArtists.length; i++) {
            const attraction = this.allArtists[i];
            // google custom search
            this.http.get(this.expressServer + '/customSearch/' + encodeURI(attraction.name))
                .subscribe( data => {
                        const json0 = data;
                        this.imgRequests.push(json0);
                        this.imgRequestsNames.push(attraction.name);
                        feedback1 = true;
                        this.showBar = false;
                    },
                    error1 => console.log('custom search failed'),
                    () => console.log('custom search completed')
                );
            if (attraction && attraction.classifications && attraction.classifications[0] &&
                attraction.classifications[0].segment && attraction.classifications[0].segment.name
                && attraction.classifications[0].segment.name === 'Music') {
                this.http.get(this.expressServer + '/spotify/' + encodeURI(attraction.name))
                    .subscribe( data => {
                            const json = data;
                            // this.spotifyRequests.push(json);
                            // this.allValidArtists.push(attraction.name);
                            this.pushSpotifyRequests(json, attraction.name);
                            feedback2 = true;
                            this.showBar = false;
                        },
                        error1 => console.log('spotify failed'),
                        () => console.log('spotify completed')
                    );
            } else {
                this.pushSpotifyRequests(null, null);
            }
        }
        this.http.get(this.expressServer + '/venue/' + encodeURI(this.venueName))
            .subscribe( data => {
                    const json = data;
                    this.venueJson = json;
                    if (this.venueJson && this.venueJson._embedded && this.venueJson._embedded.venues
                        && this.venueJson._embedded.venues[0]) {
                        this.venueJson = this.venueJson._embedded.venues[0];
                        renderMap(this.venueJson.location.latitude, this.venueJson.location.longitude);
                        feedback3 = true;
                        this.showBar = false;
                        // console.log(this.mapAreaElement);
                    } else {
                        this.venueJson = null;
                    }
                },
                error1 => console.log('venue search failed'),
                () => console.log('venue search completed')
            );
        this.http.get(this.expressServer + '/upcoming/' + encodeURI(this.venueName))
            .subscribe( data => {
                    this.upJson = data;
                    if (this.upJson && this.upJson.resultsPage && this.upJson.resultsPage.results
                        && this.upJson.resultsPage.results.event && this.upJson.resultsPage.results.event[0]) {
                        this.upcomingEvents = this.upJson.resultsPage.results.event;
                        this.upcomingEventsSorted = JSON.parse(JSON.stringify(this.upJson.resultsPage.results.event));
                    } else {
                        this.upcomingEvents = null;
                    }
                    feedback4 = true;
                    this.showBar = false;
                },
                error1 => console.log('upcoming search failed'),
                () => console.log('upcoming search completed')
            );
    }
    private pushSpotifyRequests(json, artistName) {
        if (json === null) {
            this.spotifyRequests.push(null);
        } else {
            const items = json.body.artists.items;
            let i = 0;
            for (i = 0; i < items.length; i++) {
                if (items[i].name === artistName) {
                    this.spotifyRequests.push(items[i]);
                    return;
                }
            }
        }

    }
    public sort() {
        switch (this.sortType) {
            case 'EventName':
                this.upcomingEventsSorted.sort((a, b) => (a.displayName <= b.displayName) ? -1 : 1);
                break;
            case 'Time':
                this.upcomingEventsSorted.sort((a, b) => (a.start.datetime <= b.start.datetime) ? -1 : 1);
                break;
            case 'Artist':
                this.upcomingEventsSorted.sort((a, b) => (a.performance[0].displayName <= b.performance[0].displayName) ? -1 : 1);
                break;
            case 'Type':
                this.upcomingEventsSorted.sort((a, b) => (a.type <= b.type) ? -1 : 1);
                break;
            default:
                this.upcomingEventsSorted = this.upcomingEvents;
                break;
        }
        if (this.sortDirection === 'Descending') {
            this.upcomingEventsSorted.reverse();
        }
    }
    public sortDir() {
        this.upcomingEventsSorted.reverse();
    }
    public formatDate(date) {
        return moment(date).format('ll');
    }
    public showMoreOrLess() {
        if (this.upcomingEventsSorted.length <= 5) {
            return;
        } else {
            this.show5 = !this.show5;
            if (this.showMoreLess === 'Show More') {
                this.showMoreLess = 'Show Less';
            } else {
                this.showMoreLess = 'Show More';
            }
            this.isOpen = !this.isOpen;
        }
    }
    private loadFav() {
        const len = this.storage.length;
        for (let i = 0; i < len; i ++) {
            let event = JSON.parse(this.storage.key(i));
            this.allFavEvents.push(event);
        }
        console.log(this.allFavEvents.length);
    }
    public setFav(event) {
        const eventStr = JSON.stringify(event);
        if (!this.storage.getItem(eventStr)) {
            this.storage.setItem(eventStr, 'yes');
            console.log(event);
            this.allFavEvents.push(event);
        } else {
            this.storage.removeItem(eventStr);
            for (let i = 0; i < this.allFavEvents.length; i++) {
                if (this.allFavEvents[i] === event) {
                    this.allFavEvents.splice(i,1);
                    break;
                }
            }
        }
        console.log(this.allFavEvents.length);
        // this.allFavEvents = JSON.parse(JSON.stringify(this.storage));
        // console.log(this.storage.getItem(this.storage.key(0)));
    }
    public getFav(event) {
        const eventStr = JSON.stringify(event);
        if (!this.storage.getItem(eventStr)) {
            return 'star_border';
        } else {
            return 'star';
        }
    }
}
