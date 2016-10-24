import http = require('http');
import https = require('https');
import fs = require('fs');
import express = require("express");
import "log-timestamp";

import routerEvent = require("./api/EventResource");
import routerNotification = require("./api/NotificationResource");
import routerUser = require("./api/UserResource");

let app = express();
let bodyParser = require("body-parser");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended": false}));

app.use('/events/', routerEvent);
app.use('/notifications/', routerNotification);
app.use('/login/', routerUser);

var port = process.env.rest_port;
console.log("Listening to PORT " + port);

let serverOptions = {
    key: fs.readFileSync(process.env.tls_key_path),
    cert: fs.readFileSync(process.env.tls_cert_path)
};
https.createServer(serverOptions, app).listen(port);
