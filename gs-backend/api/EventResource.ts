import express = require("express");
import {Error} from "mongoose";
import {Response} from "express";

import Model = require("./../models/Schema");
import {ServerResponse, IncomingMessage} from "http";
import IEvent = require("../models/IEvent");

let router = express.Router();

//TODO: notyfikacje do innych zdarzeń
function notificationSave(name:string, event:IEvent, subject:string, message:string) {
    var notification = new Model.Notification();
    notification.subject = name + subject + '.';
    notification.message = message + '.';
    notification.event = event;
    notification.save();
}

function moveBeetweenArrays(element:string, arrayToAddTo:string[], arrayToRemoveFrom:string[]):string[] {
    if (typeof arrayToAddTo == "undefined"
        || arrayToAddTo == null) {
        arrayToAddTo = [];
    }

    if (arrayToRemoveFrom.indexOf(element) != -1) {
        arrayToRemoveFrom.splice(arrayToRemoveFrom.indexOf(element), 1)
    }

    if (arrayToAddTo.indexOf(element) == -1) {
        arrayToAddTo.push(element)
    }

    return arrayToAddTo;
}

function handleMongoErrorMessage(res:ServerResponse, err:any, message:string):void {
    handleMongoError(res, err, message, null);
}

function handleMongoErrorDataCallback(res:ServerResponse, err:any, responseReturnDataCallback:() => any):void {
    handleMongoError(res, err, null, responseReturnDataCallback);
}

function handleMongoError(res:ServerResponse, err:any, message:string, responseReturnDataCallback:() => any):void {
    let response;
    let status:number = 200;

    if (err) {
        let message;
        status = 400;
        if (err instanceof Error) {
            //mongoose validation error
            message = err;
            status = 400;
        } else {
            message = err;
        }
        response = {"error": true, "message": "Error fetching data " + message};

    } else {
        if (responseReturnDataCallback == null) {
            response = {"error": false, message: message};
        } else {
            response = responseReturnDataCallback();
        }

    }
    res.status(status).json(response);
}

function handle(req:IncomingMessage, res:ServerResponse, err, data, successHandler):void {
    let response:{};
    let status:number = 200;
    if (err) {
        response = {"error": true, "message": "Error fetching data" + err};
    } else {
        response = successHandler(data);
    }
    res.status(status).json(response);
}

router.route("/")
    .get((req, res) => {

        Model.Event.find({}, (err, data) => handle(req, res, err, data, (data) => {
            return data;
        }));
    })
    .post((req, res)=> {

        var event = new Model.Event();
        event.name = req.body.name;
        event.creator = req.body.creator;
        //dodanie twócy
        event.accountsConfirmed = [];
        event.accountsConfirmed.push(req.body.creator);

        //TODO: zaciagany zabawny losowy tekscik uzasadniajacy - twitter?
        event.save()
            .then(() => res.status(201).json(event._id))
            .catch(error => res.status(500).json({"name": req.params.name}))
            .then(() => notificationSave(event.creator, event, ' dodał ' + event.name, 'Data: ' + event.date + '\n' + 'Określ się w miarę szybko. Od tego zależy istnienei wszechświata.'));
    });

router.route("/:id")
    .get((req, res) => {

        Model.Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
            return {"error": false, "message": data};
        }));
    })
    .put(function (req, res) {

        Model.Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {

            if (req.body.name !== undefined) {
                // case where email needs to be updated.
                data.name = req.body.name;
            }

            data.save((err)=> handleMongoErrorMessage(res, err, 'Event added'));

            return {"error": false, "message": data};
        }));
    });

router.route("/:id/status")
    .get(function (req, res) {

        Model.Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
            return {"error": false, "status": data.status};
        }));
    })
    .put(function (req, res) {

        Model.Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
                var zmianaStatusuDozwolona = req.body.creator !== undefined && data.creator === req.body.creator;
                var podanyStatus = req.body.status !== undefined;
                if (zmianaStatusuDozwolona && podanyStatus) {

                    data.status = req.body.status;
                    data.save((err)=> handleMongoErrorMessage(res, err, "Data is updated for " + req.params.id));
                }
                else {
                    res.json({
                        "error": true,
                        "message": "Status change is only possible for event creator and requires status to be given."
                    });
                }
            }
            )
        )
        ;
    })
;


router.route("/:id/account/:name")
    .delete(function (req, res) {

        Model.Event.findById(req.params.id).then(data => {
            //usunięcie z listy jeśli na niej jest
            data.accountsRejected = moveBeetweenArrays(req.params.name, data.accountsRejected, data.accountsConfirmed);

            return data.save();
        }).then(data => {

            notificationSave(req.params.name, data, ' nie przybędzie ' + data.date, req.params.name + ' z jakiegoś powodu wycofał się z ' + data.name + ' ' + data.date);

            return data;
        }).then(data => res.status(200).json(/*{"name": req.params.name}*/data)).catch(error => res.status(400).json({
            "error": true,
            "message": "Error fetching data " + error
        }));

    })
    .post(function (req, res) {

        Model.Event.findById(req.params.id).then(data => {

                if (data != null) {
                    data.accountsConfirmed = moveBeetweenArrays(req.params.name, data.accountsConfirmed, data.accountsRejected);

                    notificationSave(req.params.name, data, ' przybędzie ' + data.date, req.params.name + ' będzie na: ' + data.name + ' dnia: ' + data.date);

                    data.save().then((data) => res.status(201).json(data/*req.params.name*/)).catch(error => res.status(500).json({"name": req.params.name}));
                } else {
                    res.status(404).json({"error": true, "message": "No data with id " + req.params.id});
                }

            }
        ).catch(error => res.status(400).json({
            "error": true,
            "message": "Error fetching data " + error
        }));

    });

export = router;