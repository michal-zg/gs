import INotification = require("../models/INotification");
var Promise = require('bluebird');
require('promise/lib/rejection-tracking').enable();
Promise.promisifyAll(require("mongoose"));

import express = require("express");

var Model = require("./../models/Schema");

let router = express.Router();

router.route("/")
    .get((req, res) => {

        Model.Notification.find({}).where('date').gt(req.body.datetime).then(data => res.status(200).json(data)).catch(error => res.status(400).json({
            "error": true,
            "message": "Error fetching data " + error
        }));
    });

router.route("/last")
    .get((req, res) => {

        Model.Notification.find({}).then(data => res.status(200).json(data)).catch(error => res.status(400).json({
            "error": true,
            "message": "Error fetching data " + error
        }));
    });


export = router;