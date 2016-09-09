var mongoose = require("mongoose");
var mongoDbIp = 'localhost';
mongoose.connect('mongodb://' + mongoDbIp + ':27017/game-scheduler');
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
var Event = mongoose.model("Event", eventSchema);
module.exports = Event;
//# sourceMappingURL=Event.js.map