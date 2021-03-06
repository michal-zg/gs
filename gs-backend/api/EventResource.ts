import express = require("express");
import res = require("~express/lib/response");
import moment = require('moment');
import Promise = require("bluebird");
Promise.promisifyAll(require("mongoose"));

import * as commons from "../commons/commons";
import * as Model from "./../models/Schema";
import IEvent = require("../models/IEvent");
import IUser = require("../models/IUser");

import mongoose = require("mongoose");

let router = express.Router();

//TODO: notyfikacje do innych zdarzeń
function notificationSave(name:string, event:IEvent, subject:string, message:string) {
    var notification = new Model.Notification();
    notification.subject = name + subject + '.';
    notification.message = message + '.';
    notification.event = event;
    notification.save();
}

router.route("/")
    .get((req, res) => {

        let yesterdayMidnight = moment(Date.now()).hours(0).minutes(0).seconds(0).milliseconds(0).subtract({days: 1}).toDate();
        console.log("Events fetched for date: " + yesterdayMidnight);

        Model.Event.find({date: {$gte: yesterdayMidnight}})
            .populate('creator accountsRejected accountsConfirmed')
            .then(data => res.status(200).json(data))
            .catch(error => res.status(500).json({
                "error": true,
                "message": "Error fetching data " + error
            }));
    })
    .post((req, res)=> {


        Model.User.findOne({name: req.body.creator.name})
            .then(user => {

                    var event = new Model.Event();
                    event.name = req.body.name;
                    event.creator = user._id;
                    event.date = req.body.date;
                    //dodanie twócy
                    event.accountsConfirmed = [];
                    event.accountsConfirmed.push(user._id);

                    //TODO: zaciagany zabawny losowy tekscik uzasadniajacy - twitter?
                return event.save()
                        .then(data => {
                            res.status(201).json(data._id);
                            return data;
                        })
                        .then(data => notificationSave(user.alias, event, ' dodał ' + data.name, 'Data: ' + data.date + '\n' + 'Określ się w miarę szybko. Od tego zależy istnienei wszechświata.'))
                        .catch(error => res.status(500).json({"name": req.params.name}));
                }
            ).catch(error => res.status(500).json({
            "error": true,
            "message": "Error fetching data " + error
        }));
    });

router.route("/:id")
    .get((req, res) => {
        Model.Event.findById(req.params.id)
            .populate('creator accountsRejected accountsConfirmed')
            .then(data => res.status(200).json({"error": false, "message": data}))
            .catch(error => res.status(400).json({error: true, message: "Error fetching data " + error}));
    })
    .put(function (req, res) {

        Model.Event.findById(req.params.id)
            .populate('creator accountsRejected accountsConfirmed')
            .then(data => {
                if (req.body.name !== undefined) {
                    data.name = req.body.name;
                }

                data.save().then(data => res.status(200).json({"error": false, "message": data}));
            }).catch(error => res.status(400).json({error: true, message: "Error " + error}));
    });

router.route("/:id/status")
    .get(function (req, res) {

        Model.Event.findById(req.params.id)
            .populate('creator accountsRejected accountsConfirmed')
            .then(data => res.status(200).json({"error": false, "status": data.status}))
            .catch(error => res.status(400).json({error: true, message: "Error fetching data " + error}));
    })
    .put(function (req, res) {

        Model.Event.findById(req.params.id)
            .populate('creator accountsRejected accountsConfirmed')
            .then(data => {
                var zmianaStatusuDozwolona = req.body.creator !== undefined && data.creator === req.body.creator;
                var podanyStatus = req.body.status !== undefined;
                if (zmianaStatusuDozwolona && podanyStatus) {
                    data.status = req.body.status;
                    data.save().then(data => res.status(200).json({
                        "error": false,
                        message: "Data for " + req.params.id + " updated."
                    }));
                } else {
                    res.json({
                        "error": true,
                        "message": "Status change is only possible for event creator and requires status to be given."
                    });
                }
            })
            .catch(error => res.status(400).json({error: true, message: "Error fetching data " + error}));

    })
;


router.route("/:id/account/:name")
    .delete(function (req, res) {

        Model.User.findOne({name: req.params.name})
            .then(user => {

                return Model.Event.findById(req.params.id)
                    .populate('creator accountsRejected accountsConfirmed')
                    .then(data => {

                        console.log("Delete - rejected" + JSON.stringify(data.accountsRejected));
                        console.log("Delete - confirmed" + JSON.stringify(data.accountsConfirmed));

                        //usunięcie z listy jeśli na niej jest
                        data.accountsRejected = commons.moveBetweenArrays(user, data.accountsRejected, data.accountsConfirmed);


                        return data.save();
                    })
                    .then(data => {

                        console.log("Delete after - rejected" + JSON.stringify(data.accountsRejected));
                        console.log("Delete after - confirmed" + JSON.stringify(data.accountsConfirmed));

                        notificationSave(user.alias, data, ' nie przybędzie ' + data.date, req.params.name + ' z jakiegoś powodu wycofał się z ' + data.name + ' ' + data.date);

                        return data;
                    })
                    .then(data => res.status(200).json(data))
            })
            .catch(error => res.status(400).json({
                "error": true,
                "message": "Error fetching data " + error
            }));

    })
    .post(function (req, res) {

        Model.User.findOne({name: req.params.name}).then(user => {
            return Model.Event.findById(req.params.id)
                .populate('creator accountsRejected accountsConfirmed')
                .then(data => {
                        if (data != null) {
                            console.log("Add - rejected" + JSON.stringify(data.accountsRejected));
                            console.log("Add - confirmed" + JSON.stringify(data.accountsConfirmed));

                            data.accountsConfirmed = commons.moveBetweenArrays(user, data.accountsConfirmed, data.accountsRejected);

                            data.save().then(data => {

                                console.log("Add after - rejected" + JSON.stringify(data.accountsRejected));
                                console.log("Add after - confirmed" + JSON.stringify(data.accountsConfirmed));

                                notificationSave(user.alias, data, ' przybędzie ' + data.date, user.alias + ' będzie na: ' + data.name + ' dnia: ' + data.date);
                                res.status(201).json(data);
                            }).catch(error => res.status(500).json({"name": req.params.name}));

                        } else {
                            res.status(404).json({"error": true, "message": "No data with id " + req.params.id});
                        }
                    }
                );
        }).catch(error => res.status(400).json({
            "error": true,
            "message": "Error fetching data " + error
        }));

    });

export = router;