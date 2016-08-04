import express = require("express");

import Event = require("../models/Event");

let router = express.Router();

router.get("/", (req, res) => {
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
    });

export = router;