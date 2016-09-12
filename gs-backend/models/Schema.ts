import mongoose = require("mongoose");
import IEvent = require("./IEvent");
import INotification = require("./INotification");

const mongoDbUrl = process.env.mongodb;
mongoose.connect(mongoDbUrl);

interface IEvenModel extends IEvent, mongoose.Document {
}

interface INotificationModel extends INotification, mongoose.Document {
}

var eventSchema = new mongoose.Schema({
    "id": mongoose.Schema.Types.ObjectId,
    "creator": {
        type: String,
        required: [true, 'Nazwa twórcy wydarzenia jest wymagana.'],
        min: [6, 'Nazwa twórcy wydarzenia musi być duższa niż 6 znaków.']
    },
    "name": {
        type: String,
        required: [true, 'Nazwa wydarzenia jest wymagana.'],
        min: [3, 'Nazwa wydarzenia musi być duższa niż 3 znaków.']
    },
    "date": {
        type: Date,
        default: Date.now
    },
    "accountsRejected": {
        type: [String]
    },
    "accountsConfirmed": {
        type: [String]
        // ,        required: [true, 'Lista zaakceptowanych uczestników wydarzenia jest wymagana.']
    },
    "status": {
        type: String,
        default: 'new',
        enum: ['new', 'canceled']
    }
});


var notificationSchema = new mongoose.Schema({
    "id": mongoose.Schema.Types.ObjectId,
    "subject": {
        type: String,
        required: [true, 'Temat notyfikacji jest wymagany.'],
        min: [3, 'Temat notyfikacji musi być duższy niż 3 znaki.']
    },
    "message": {
        type: String,
        required: [true, 'Treść notyfikacji jest wymagany.'],
        min: [5, 'Treść notyfikacji musi być duższy niż 5 znaków.']
    },
    "date": {
        type: Date,
        default: Date.now
    },
    "event": {
        type: mongoose.Schema.Types.ObjectId, ref: 'Event'
    }
});


var Event = mongoose.model<IEvenModel>("Event", eventSchema);

var Notification = mongoose.model<INotificationModel>("Notification", notificationSchema);

export = {Event, Notification}