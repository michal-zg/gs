// load Unit.js module
var test = require('unit.js');

describe('Learning by the example', function () {

    it('splice existing', function () {
        var bla = ["b", "a", "d"];

        test.when('array splice', function () {
                bla.splice(bla.indexOf("a"), 1)
            })
            .then('test the "example" object', function () {
                test
                    .array(bla)
                    .is(["b", "d"]);
            })
    });

    it('splice NOT existing', function () {
        var bla = ["b", "a", "d"];

        test.when('array splice', function () {
                bla.splice(bla.indexOf("AA"), 1)
            })
            .then('test the "example" object', function () {
                test
                    .array(bla)
                    .is(["b", "a", "d"]);
            })
    });
});