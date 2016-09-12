import express = require("express");
import routerEvent = require("./api/EventResource");

import routerNotification = require("./api/NotificationResource");

let app = express();
let bodyParser = require("body-parser");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended": false}));

app.use('/events/', routerEvent);
app.use('/notifications/', routerNotification);

app.listen(3000);
console.log("Listening to PORT 3000"); 