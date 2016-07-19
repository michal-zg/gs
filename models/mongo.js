var mongoose    =   require("mongoose");

mongoose.connect('mongodb://localhost:27017/game-scheduler');

var eventSchema =   mongoose.Schema({
    "id" : mongoose.Schema.Types.ObjectId,
    "name" : String,
    "date" : { type: Date, default: Date.now },
    "accepted" : [String]
});
var Event = mongoose.model('event',eventSchema);
module.exports = Event;