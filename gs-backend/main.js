var express = require("express");
var router = require("./api/EventResource");
var app = express();
var bodyParser = require("body-parser");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ "extended": false }));
app.use('/', router);
app.listen(3000);
console.log("Listening to PORT 3000");
//# sourceMappingURL=main.js.map