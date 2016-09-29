var Promise = require('bluebird');
import JsSHA = require("jssha");
import express = require("express");
import IUser = require("../models/IUser");

var Model = require("./../models/Schema");

Promise.promisifyAll(require("mongoose"));

let router = express.Router();

var hash = function (password) {
    var key = 't23jkascgfdb34';
    let shaObj = new JsSHA('SHA-512', "TEXT");
    shaObj.setHMACKey(key, "TEXT");
    shaObj.update(password);
    var hmac = shaObj.getHMAC("HEX");

    console.log("Password hash: " + hmac, key);
    return hmac;
};

router.route("/")
    .post((req, res) => {

        var match = process.env.main_password_hash === hash(req.body.password);
        if (match) {

            Model.User.findOne({'name': req.body.userName}).then(data => {

                if (data == null) {
                    data = new Model.User();
                    data.name = req.body.userName;
                }
                data.alias = req.body.userNameAlias;

                data.save();

            }).catch(error => res.status(403).json({
                "error": true,
                "message": "Not authorized"
            }));

            res.status(200).json(true);

        } else {
            res.status(401).json({
                "error": true,
                "message": "Login failed"
            });
        }

    });

export = router;