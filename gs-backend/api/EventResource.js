var express = require("express");
var mongoose_1 = require("mongoose");
var HttpStatus = require('http-status-codes');
var Event = require("../models/Event");
var router = express.Router();
function moveBeetweenArrays(element, arrayToAddTo, arrayToRemoveFrom) {
    if (typeof arrayToAddTo == "undefined"
        || arrayToAddTo == null) {
        arrayToAddTo = [];
    }
    if (arrayToRemoveFrom.indexOf(element) != -1) {
        arrayToRemoveFrom.splice(arrayToRemoveFrom.indexOf(element), 1);
    }
    if (arrayToAddTo.indexOf(element) == -1) {
        arrayToAddTo.push(element);
    }
    return arrayToAddTo;
}
function handleMongoError(res, err, message) {
    handleMongoError(res, err, message, null);
}
function handleMongoError(res, err, responseReturnDataCallback) {
    var response;
    var status = 200;
    if (err) {
        var message;
        status = 400;
        if (err instanceof mongoose_1.Error) {
            //mongoose validation error
            message = err;
            status = 400;
        }
        else {
            message = err;
        }
        response = { "error": true, "message": "Error fetching data " + message };
    }
    else {
        if (responseReturnDataCallback == null) {
            response = { "error": false, message: message };
        }
        else {
            response = responseReturnDataCallback();
        }
    }
    res.status(status).json(response);
}
function handle(req, res, err, data, successHandler) {
    var response;
    var status = 200;
    if (err) {
        response = { "error": true, "message": "Error fetching data" + err };
    }
    else {
        response = successHandler(data);
    }
    res.status(status).json(response);
}
router.route("/events")
    .get(function (req, res) {
    Event.find({}, function (err, data) { return handle(req, res, err, data, function (data) {
        return { "error": false, "message": data };
    }); });
})
    .post(function (req, res) {
    var event = new Event();
    event.name = req.body.name;
    event.creator = req.body.creator;
    //dodanie twócy
    event.accountsConfirmed = [];
    event.accountsConfirmed.push(req.body.creator);
    event.save((function (err) { return handleMongoError(res, err, function () { return event; }); }));
});
router.route("/events/:id")
    .get(function (req, res) {
    Event.findById(req.params.id, function (err, data) { return handle(req, res, err, data, function (data) {
        return { "error": false, "message": data };
    }); });
})
    .put(function (req, res) {
    Event.findById(req.params.id, function (err, data) { return handle(req, res, err, data, function (data) {
        if (req.body.name !== undefined) {
            // case where email needs to be updated.
            data.name = req.body.name;
        }
        data.save(function (err) { return handleMongoError(res, err, 'Event added'); });
        return { "error": false, "message": data };
    }); });
});
router.route("/events/:id/status")
    .get(function (req, res) {
    Event.findById(req.params.id, function (err, data) { return handle(req, res, err, data, function (data) {
        return { "error": false, "status": data.status };
    }); });
})
    .put(function (req, res) {
    Event.findById(req.params.id, function (err, data) { return handle(req, res, err, data, function (data) {
        var zmianaStatusuDozwolona = req.body.creator !== undefined && data.creator === req.body.creator;
        var podanyStatus = req.body.status !== undefined;
        if (zmianaStatusuDozwolona && podanyStatus) {
            data.status = req.body.status;
            data.save(function (err) { return handleMongoError(res, err, "Data is updated for " + req.params.id); });
        }
        else {
            res.json({
                "error": true,
                "message": "Status change is only possible for event creator and requires status to be given."
            });
        }
    }); });
});
router.route("/events/:id/account/:name")
    .delete(function (req, res) {
    Event.findById(req.params.id, function (err, data) { return handle(req, res, err, data, function (data) {
        //usunięcie z listy jeśli na niej jest
        data.accountsRejected = moveBeetweenArrays(req.params.name, data.accountsRejected, data.accountsConfirmed);
        data.save(function (err) { return handleMongoError(res, err, { "name": req.params.name }.toString()); });
    }); });
})
    .post(function (req, res) {
    Event.findById(req.params.id, function (err, data) { return handle(req, res, err, data, function (data) {
        if (data != null) {
            data.accountsConfirmed = moveBeetweenArrays(req.params.name, data.accountsConfirmed, data.accountsRejected);
            data.save(function (err) { return handleMongoError(res, err, { "name": req.params.name }.toString()); });
        }
        else {
            res.status(404).json({ "error": true, "message": "No data with id " + req.params.id });
        }
    }); });
});
module.exports = router;
//# sourceMappingURL=EventResource.js.map