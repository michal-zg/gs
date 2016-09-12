import express = require("express");
import routerEvent = require("./api/EventResource");

import routerNotification = require("./api/NotificationResource");

let app = express();
let bodyParser = require("body-parser");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended": false}));

app.use('/events/', routerEvent);
app.use('/notifications/', routerNotification);

var port = process.env.port;
console.log("Listening to PORT " + port);
app.listen(port);
