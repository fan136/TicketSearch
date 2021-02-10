let venueLat = null;
let venueLon = null;

function validation(event) {
    var isValid = false;
    // if (event.target !== document.getElementById("keyword")){
        // Fetch all the forms we want to apply custom Bootstrap validation styles to
        var form = document.getElementById('search');
        // var target = event;
        console.log(event);
        console.log(event.target.id);
        var target = event.target.id;
        if (target === 'keyword' || target === 'from_typein' || target === 'location' || target === 'current_geo'
            || target === 'clearBtn') {
            if (form.classList.contains('was-validated')) {
                form.classList.remove('was-validated');
            }
        } else {
            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
                console.log(form);
            }
            // else if (document.getElementById('search'))
            else {
                isValid = true;
            }
            form.classList.add('was-validated');
        }
        return isValid;
}

function renderMap(lat, lon){
    if (typeof initMap === 'function') {

        var dest = {lat: parseFloat(lat), lng:parseFloat(lon)};
        var map2 = new google.maps.Map(document.getElementById('mapArea'), {zoom: 13, center: dest});
        var marker = new google.maps.Marker({position: dest, map: map2});

    } else {
        let script =
            '    function initMap(){\n' +
            '        console.log("in init_map");\n' +
            '        var dest = {lat:' + lat + ', lng:' + lon + '};\n' +
            '        var map2 = new google.maps.Map(\n' +
            '        document.getElementById(\'mapArea\'), {zoom: 13, center: dest});\n' +
            '        var marker = new google.maps.Marker({position: dest, map: map2});\n' +
            '    }';
        let initFunc = document.createElement("SCRIPT");
        initFunc.id = "initFunc";
        initFunc.innerText = script;
        document.getElementById('appBody').appendChild(initFunc);


        var x = document.createElement("SCRIPT");
        x.defer="true";
        x.async="true";
        x.src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC8574CUAaLC-fbCPA6M-oQN0ZyL7TqAQ8&callback=initMap";
        document.getElementById('appBody').appendChild(x);
    }
}


