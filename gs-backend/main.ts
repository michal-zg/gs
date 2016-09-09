import express = require("express");
import router = require("./api/EventResource");

let app = express();
let bodyParser = require("body-parser");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({"extended": false}));

app.use('/', router);

app.listen(3000);
console.log("Listening to PORT 3000"); 