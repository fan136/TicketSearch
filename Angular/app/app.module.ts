import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms'; // <-- NgModel lives here
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule} from '@angular/common/http';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatTooltipModule} from '@angular/material/tooltip';
import {CommonModule} from '@angular/common';
import {RoundProgressModule} from 'angular-svg-round-progressbar';
import {AgmCoreModule} from '@agm/core';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
    declarations: [
    AppComponent
    ],
    imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatTooltipModule,
    BrowserAnimationsModule,
    CommonModule,
    RoundProgressModule,
    AgmCoreModule.forRoot({apiKey: 'AIzaSyC8574CUAaLC-fbCPA6M-oQN0ZyL7TqAQ8'})
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
