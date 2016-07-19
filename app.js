var express = require("express");
var app = express();
var bodyParser = require("body-parser");
var Event = require("./models/mongo");
var router = express.Router();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended": false}));

router.get("/", function (req, res) {
    res.json({"error": false, "message": "Hello World"});
});

router.route("/event")
    .get(function (req, res) {
        var response = {};
        Event.find({}, function (err, data) {

            if (err) {
                response = {"error": true, "message": "Error fetching data"};
            } else {
                response = {"error": false, "message": data};
            }
            res.json(response);
        });
    }).post(function (req, res) {
    var event = new Event();
    event.name = req.body.name;
    event.accepted = req.body.accepted;

    event.save(function (err) {
        var response = {};
        if (err) {
            response = {"error": true, "message": "Error adding data"};
        } else {
            response = {"error": false, "message": "Event added"};
        }
        res.json(response);
    });
});

router.route("/event/:id")
    .get(function (req, res) {
        var response = {};
        Event.findById(req.params.id, function (err, data) {
            // This will run Mongo Query to fetch data based on ID.
            if (err) {
                response = {"error": true, "message": "Error fetching data"};
            } else {
                response = {"error": false, "message": data};
            }
            res.json(response);
        });
    })
    .put(function (req, res) {
        var response = {};
        // first find out record exists or not
        // if it does then update the record
        Event.findById(req.params.id, function (err, data) {
            if (err) {
                response = {"error": true, "message": "Error fetching data"};
            } else {
                // we got data from Mongo.
                // change it accordingly.
                if (req.body.name !== undefined) {
                    // case where email needs to be updated.
                    data.name = req.body.name;
                }

                data.save(function (err) {
                    if (err) {
                        response = {"error": true, "message": "Error updating data"};
                    } else {
                        response = {"error": false, "message": "Data is updated for " + req.params.id};
                    }
                    res.json(response);
                })
            }
        });
    })


app.use('/', router);

app.listen(3000);
console.log("Listening to PORT 3000");