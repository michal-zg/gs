import mongoose = require("mongoose");
import IEvent = require("./IEvent");
import INotification = require("./INotification");
import IUser = require("./IUser");
import Promise = require("bluebird");
Promise.promisifyAll(mongoose);

const mongoDbUrl = process.env.mongodb;
mongoose.connect(mongoDbUrl);

interface IEvenModel extends IEvent, mongoose.Document {
}

interface INotificationModel extends INotification, mongoose.Document {
}

interface IUserModel extends IUser, mongoose.Document {
}

const USER_MODEL_NAME = "User";
var userSchema = new mongoose.Schema({
    "id": mongoose.Schema.Types.ObjectId,
    "name": {
        type: String,
        required: [true, 'Nazwa użytkownika jest wymagana.'],
        min: [3, 'Nazwa użytkownika musi być dłuższa niż 3 znaki.']
    },
    "alias": {
        type: String,
        required: [true, 'Alias użytkownika jest wymagany.'],
        min: [3, 'Alias użytkownika musi być dłuższy niż 3 znaki.']
    },
    "creationDate": {
        type: Date,
        default: Date.now
    }
});

const EVENT_MODEL_NAME = "Event";
var eventSchema = new mongoose.Schema({
    "id": mongoose.Schema.Types.ObjectId,
    "creator": {
        type: mongoose.Schema.Types.ObjectId,
        ref: USER_MODEL_NAME,
        required: [true, 'Nazwa twórcy wydarzenia jest wymagana.']
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
        type: [{type: mongoose.Schema.Types.ObjectId, ref: USER_MODEL_NAME}]
    },
    "accountsConfirmed": {
        type: [{type: mongoose.Schema.Types.ObjectId, ref: USER_MODEL_NAME}]
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
        type: mongoose.Schema.Types.ObjectId, ref: EVENT_MODEL_NAME
    }
});


var Event = mongoose.model<IEvenModel>(EVENT_MODEL_NAME, eventSchema);

var Notification = mongoose.model<INotificationModel>("Notification", notificationSchema);

var User = mongoose.model<IUserModel>(USER_MODEL_NAME, userSchema);

export {Event, Notification, User, IEvenModel};