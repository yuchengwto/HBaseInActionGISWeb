// center of the map
var center = [40.743143, -73.951608];

// Create the map
var map = L.map('map').setView(center, 15);

// Set up the OSM layer
L.tileLayer(
  'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'Data © <a href="http://osm.org/copyright">OpenStreetMap</a>',
    maxZoom: 18
  }).addTo(map);


// Initialise the FeatureGroup to store editable layers
var editableLayers = new L.FeatureGroup();
map.addLayer(editableLayers);

var drawPluginOptions = {
  position: 'topright',
  draw: {
    polygon: {
      allowIntersection: false, // Restricts shapes to simple polygons
      drawError: {
        color: '#e1e100', // Color the shape will turn when intersects
        message: '<strong>Oh snap!<strong> you can\'t draw that!' // Message that will show when intersect
      },
      shapeOptions: {
        color: '#97009c'
      }
    },
    // disable toolbar item by setting it to false
    polyline: false,
    circle: false, // Turns off this drawing tool
    rectangle: false,
    marker: false,
    // circlemarker: false,
    },
  edit: {
    featureGroup: editableLayers, //REQUIRED!!
    remove: false
  }
};

// Initialise the draw control and pass it the FeatureGroup of editable layers
var drawControl = new L.Control.Draw(drawPluginOptions);
map.addControl(drawControl);

var editableLayers = new L.FeatureGroup();
map.addLayer(editableLayers);

map.on('draw:created', function(e) {
  var layer = e.layer;
  editableLayers.addLayer(layer);
  var geojson = JSON.stringify(layer.toGeoJSON());
  console.log(geojson);

  var multiPoints;
  $.ajax({
        type: 'POST',
        url: '/query',
        data: {'geojson': geojson},
        success: function (result) {
          multiPoints = L.geoJSON(result);
          multiPoints.addTo(map);
        },
        error: function () {
          alert("query fails.")
        }
  })

});
