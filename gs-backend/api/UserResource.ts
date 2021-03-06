import IUser = require("../models/IUser");
var Promise = require('bluebird');
var JsSHA = require("jssha");
import express = require("express");

var Model = require("./../models/Schema");
Promise.promisifyAll(require("mongoose"));

let router = express.Router();

var hash = function (password) {
    var key = 23;
    let shaObj = new JsSHA('SHA-512', "TEXT");
    shaObj.setHMACKey(key, "TEXT");
    shaObj.update(password);
    var hmac = shaObj.getHMAC("HEX");

    console.log("Password hash: " + hmac);
    return hmac;
};

router.route("/")
    .get((req, res) => {
        Model.User.find({}).sort({alias: "asc", name: "asc"})
            .then(data => res.status(200).json(data))
            .catch(error => res.status(403).json({
                "error": true,
                "message": "Fetch failed"
            }));
    })
    .post((req, res) => {

        console.log("Login attempt");

        var match = process.env.main_password_hash === hash(req.body.password);
        if (match) {
            Model.User.findOne({name: req.body.userName})
                .then(data => {

                    if (data == null) {
                        data = new Model.User();
                        data.name = req.body.userName;
                        console.log(`User ${data.name} added`);
                    } else {
                        console.log(`User ${data.name} updated`);
                    }
                    data.alias = req.body.userNameAlias;

                    data.save().then(user => res.status(200).json(user));

                }).catch(error => res.status(403).json({
                "error": true,
                "message": "Not authorized"
            }));

        } else {
            res.status(401).json({
                "error": true,
                "message": "Login failed"
            });
        }

    })
;

export = router;