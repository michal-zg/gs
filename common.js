module.exports = {
    sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow: function (name, minimalLength) {

        var regExp = /^[a-zA-Z0-9]*$/;
        return name !== undefined && name !== null && typeof name == 'string' &&
            name.length >= minimalLength && regExp.test(name);
    }
}