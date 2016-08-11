import mongoose = require("mongoose");
import IEvent = require("./IEvent");

const mongoDbIp = '192.168.1.102';
mongoose.connect('mongodb://'+mongoDbIp+':27017/game-scheduler');

interface IEvenModel extends IEvent, mongoose.Document {
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
        type: [String],
        required: [true, 'Lista zaakceptowanych uczestników wydarzenia jest wymagana.']
    },
    "status": {
        type: String,
        default: 'new',
        enum: ['new', 'canceled']
    }
});

var Event = mongoose.model<IEvenModel>("Event", eventSchema);

export = Event;