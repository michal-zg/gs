import INotification = require("../models/INotification");
var Promise = require('bluebird');
// require('promise/lib/rejection-tracking').enable();
Promise.promisifyAll(require("mongoose"));

import express = require("express");

var Model = require("./../models/Schema");

let router = express.Router();

router.route("/last")
    .get((req, res) => {

        Model.Notification.findOne({})
            .populate({
                path: 'event',
                populate: { path: 'creator accountsRejected accountsConfirmed'}
            })
            //najpóźniejsza data
            .sort({date: -1})
            .then(data => res.status(200).json(data))
            .catch(error => res.status(400).json({
                "error": true,
                "message": "Error fetching data " + error
            }));
    });

router.route("/:date")
    .get((req, res) => {

        Model.Notification.find({}).where('date').gte(req.params.date)
            .populate({
                path: 'event',
                populate: { path: 'creator accountsRejected accountsConfirmed'}
            })
            .then(data => {

                console.log("Notification fetched for date: " + req.params.date);

                res.status(200).json(data);
            }).catch(error => res.status(400).json({
            "error": true,
            "message": "Error fetching data " + error
        }));
    });


export = router;