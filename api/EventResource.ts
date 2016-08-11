import express = require("express");
var HttpStatus = require('http-status-codes');

import Event = require("../models/Event");
import {ServerResponse, IncomingMessage} from "http";

let router = express.Router();

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

function handleMongoError(res:ServerResponse, err:any, message:string):void {
    let response:{};
    let status:number = HttpStatus.OK;

    if (err) {
        response = {"error": true, "message": "Error fetching data" + err};
        status = HttpStatus.NOT_FOUND_ERR;
    } else {
        response = {"error": false, "message": message};
    }
    res.status(status).json(response);
}

function handle(req:IncomingMessage, res:ServerResponse, err, data, successHandler):void {
    let response:{};
    let status:number = HttpStatus.OK;
    if (err) {
        response = {"error": true, "message": "Error fetching data" + err};
    } else {
        response = successHandler(data);
    }
    res.json(response);
}
router.route("/event")
    .get((req, res) => {

        Event.find({}, (err, data) => handle(req, res, err, data, (data) => {
            return {"error": false, "message": data};
        }));
    })
    .post((req, res)=> {

        var event = new Event();
        event.name = req.body.name;
        event.creator = req.body.creator;
        //dodanie twócy
        event.accountsConfirmed = [];
        event.accountsConfirmed.push(req.body.creator);

        event.save((err)=> handleMongoError(res, err, 'Event added'));
    });

router.route("/event/:id")
    .get((req, res) => {

        Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
            return {"error": false, "message": data};
        }));
    })
    .put(function (req, res) {

        Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {

            if (req.body.name !== undefined) {
                // case where email needs to be updated.
                data.name = req.body.name;
            }

            data.save((err)=> handleMongoError(res, err, 'Event added'));

            return {"error": false, "message": data};
        }));
    });

router.route("/event/:id/status")
    .get(function (req, res) {

        Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
            return {"error": false, "status": data.status};
        }));
    })
    .put(function (req, res) {
        var response = {};

        Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
                var zmianaStatusuDozwolona = req.body.creator !== undefined && data.creator === req.body.creator;
                var podanyStatus = req.body.status !== undefined;
                if (zmianaStatusuDozwolona && podanyStatus) {

                    data.status = req.body.status;
                    data.save((err)=> handleMongoError(res, err, "Data is updated for " + req.params.id));
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


router.route("/event/:id/account/:name")
    .delete(function (req, res) {
        var response = {};

        Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
            //usunięcie z listy jeśli na niej jest
            data.accountsRejected = moveBeetweenArrays(req.params.name, data.accountsRejected, data.accountsConfirmed);

            data.save((err)=> handleMongoError(res, err, {"name": req.params.name}.toString()));
        }));
    })
    .post(function (req, res) {

        Event.findById(req.params.id, (err, data) => handle(req, res, err, data, (data) => {
            if (data != null) {
                data.accountsConfirmed = moveBeetweenArrays(req.params.name, data.accountsConfirmed, data.accountsRejected);

                data.save((err)=> handleMongoError(res, err, {"name": req.params.name}.toString()));
            } else {
                res.status(404).json({"error": true, "message": "No data with id " + req.params.id});
            }

        }));
    });

export = router;