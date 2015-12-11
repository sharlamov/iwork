
var map;

$(window).on('load resize', function(){
    resizeRegions();
});

function resizeRegions() {
    var title = $("#regionTitle");
    var hSize = $(window).height();
    var wSize = $(window).width();

    hSize = hSize - title.height() - title.offset().top
    //alert(hSize);

    var table = $('#inputDataForm\\:regionTable');
    var map = $("#mapdiv");
    table.css('height', hSize + 'px');
    table.css('width', (0.25*wSize) + 'px');
    map.css('height', hSize + 'px');
    map.css('width', (0.70*wSize) + 'px');
}

function updateDataMap(path, season, sc, filters) {

    if (map == undefined)
        return;

    $.getJSON(path + '/dataMap?season=' + season + '&sc=' + sc + '&tip=' + filters, function (result) {
        map = new AmCharts.AmMap();

        var dataProvider = {
            mapVar: AmCharts.maps.moldovaLow,
            getAreasFromMap: true,
            areas: result.areas,
            images: result.points,
            lines: result.lines
        };

        map.areasSettings = {
            rollOverOutlineColor: '#666666',
            rollOverColor: '#C9DFAF',
            color: 'white',
            outlineColor: '#666666',
            selectedColor: '#C9DFAF',
            balloonText: '[[title]] , [[customData]]',
        };

        map.dataProvider = dataProvider;
        // add events to recalculate map position when the map is moved or zoomed
        map.addListener('positionChanged', updateCustomMarkers);
        map.write('mapdiv');
    });
}

function renderMap(path, season, sc) {

    $.getJSON(path + '/dataMap?season=' + season + '&sc=' + sc, function (result) {
        map = new AmCharts.AmMap();

        var dataProvider = {
            mapVar: AmCharts.maps.moldovaLow,
            getAreasFromMap: true,
            areas: result.areas
        };

        map.areasSettings = {
            rollOverOutlineColor: '#666666',
            rollOverColor: '#C9DFAF',
            color: 'white',
            outlineColor: '#666666',
            selectedColor: '#C9DFAF',
            balloonText: '[[title]] , [[customData]]',
        };

        map.dataProvider = dataProvider;
        // add events to recalculate map position when the map is moved or zoomed
        map.addListener('positionChanged', updateCustomMarkers);
        map.write('mapdiv');
    });
}

// this function will take current images on the map and create HTML elements for them
function updateCustomMarkers(event) {
    // get map object
    var map = event.chart;
    // go through all of the images
    for (var x in map.dataProvider.images) {
        // get MapImage object
        var image = map.dataProvider.images[x];

        // check if it has corresponding HTML element
        if ('undefined' == typeof image.externalElement)
            image.externalElement = createCustomMarker(image);

        // reposition the element accoridng to coordinates
        image.externalElement.style.top = map.latitudeToY(image.latitude) + 'px';
        image.externalElement.style.left = map.longitudeToX(image.longitude) + 'px';
    }
}

// this function creates and returns a new marker element
function createCustomMarker(image) {
    // create holder
    var holder = document.createElement('div');
    holder.className = 'map-marker';
    holder.title = image.title;
    holder.style.position = 'absolute';

    // maybe add a link to it?
    if (undefined != image.url) {
        holder.onclick = function () {
            window.location.href = image.url;
        };
        holder.className += ' map-clickable';
    }

    // create dot
    var dot = document.createElement('div');
    dot.className = getColor('dot', image.color);
    holder.appendChild(dot);

    // create pulse
    var pulse = document.createElement('div');
    pulse.className = getColor('pulse', image.color);
    holder.appendChild(pulse);

    // append the marker to the map container
    image.chart.chartDiv.appendChild(holder);

    return holder;
}

function getColor(style, color) {
    var c = color.charAt(0).toUpperCase() + color.slice(1);
    return style + (style == 'dot' ? ' dot' : ' pulseColor') + c;
}