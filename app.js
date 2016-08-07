var express = require("express");
var app = express();
var bodyParser = require("body-parser");
var Event = require("./models/event-schema");
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
                response = {"error": true, "message": "Error fetching data" + err};
            } else {
                response = {"error": false, "message": data};
            }
            res.json(response);
        });
    })
    .post(function (req, res) {
        //TODO: walidacja? czy mongoose wystarczy?
        var event = new Event();
        event.name = req.body.name;
        event.creator = req.body.creator;
        //dodanie twócy
        event.accountsConfirmed = [];
        event.accountsConfirmed.push(req.body.creator);

        event.save(function (err) {
            var response = {};
            if (err) {
                response = {"error": true, "message": "Error adding data" + err};
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
                        response = {"error": true, "message": "Error updating data" + err};
                    } else {
                        response = {"error": false, "message": "Data is updated for " + req.params.id};
                    }
                    res.json(response);
                })
            }
        });
    });

router.route("/event/:id/status")
    .get(function (req, res) {
        var response = {};

        Event.findById(req.params.id, function (err, data) {
            if (err) {
                response = {"error": true, "message": "Error fetching data"};
            } else {
                response = {"error": false, "status": data.status};
            }
            res.json(response);
        });
    })
    .put(function (req, res) {
        var response = {};

        Event.findById(req.params.id, function (err, data) {
            if (err) {
                response = {"error": true, "message": "Error fetching data"};
            } else {
                if (req.body.status !== undefined) {
                    data.status = req.body.status;
                }

                data.save(function (err) {
                    if (err) {
                        response = {"error": true, "message": "Error updating data" + err};
                    } else {
                        response = {"error": false, "message": "Data is updated for " + req.params.id};
                    }
                    res.json(response);
                })
            }
        });
    });


router.route("/event/:id/account/:name")
    .delete(function (req, res) {
        var response = {};

        Event.findById(req.params.id, function (err, data) {
            if (err) {
                response = {"error": true, "message": "Error fetching data"};
            } else {
                //usunięcie z listy jeśli na niej jest
                if (typeof data.accountsRejected == "undefined"
                    || data.accountsRejected == null) {
                    data.accountsRejected = [];
                }

                if (data.accountsConfirmed.indexOf(req.params.name) != -1) {
                    data.accountsConfirmed.splice(data.accountsConfirmed.indexOf(req.params.name), 1)
                }

                if (data.accountsRejected.indexOf(req.params.name) == -1) {
                    data.accountsRejected.push(req.params.name)
                }

                data.save(function (err) {
                    if (err) {
                        response = {"error": true, "message": "Error updating data" + err};
                    } else {
                        response = {"error": false, "name": req.params.name};
                    }
                    res.json(response);
                })
            }
        });
    })
    .post(function (req, res) {
        var response = {};

        Event.findById(req.params.id, function (err, data) {
            if (err) {
                response = {"error": true, "message": "Error fetching data"};

                res.json(response);
            } else if (data != null) {
                if (typeof data.accountsConfirmed == "undefined"
                    || data.accountsConfirmed == null) {
                    data.accountsConfirmed = [];
                }

                if (data.accountsConfirmed.indexOf(req.params.name) == -1) {
                    data.accountsConfirmed.push(req.params.name)
                }

                data.save(function (err) {
                    if (err) {
                        response = {"error": true, "message": "Error updating data" + err};
                    } else {
                        response = {"error": false, "message": "Data is updated for " + req.params.name};
                    }
                    res.json(response);
                })
            } else {
                response = {"error": true, "message": "No data with id " + req.params.id};
                res.status(404).json(response);
            }

        });
    });


app.use('/', router);

app.listen(3000);
console.log("Listening to PORT 3000");