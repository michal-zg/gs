var test = require('unit.js');

var commons = require('../common.js');

describe('sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow', function () {
    it('string za krotki', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('dupa', 6);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });


    it('undefined', function () {
        var ok;

        test.when(function () {
            var dupa;
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow(dupa, 6);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });

    it('null', function () {
        var ok;

        test.when(function () {
            var dupa = null;
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow(dupa, 6);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });

    it('typeof string', function () {
        var ok;

        test.when(function () {
            var dupa = 4576;
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow(dupa, 2);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });
    it('string rownej dlugosci min', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('dupa', 4);
        }).then('', function () {
            test.bool(ok).isTrue();
        });
    });

    it('string > dlugosc min', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('dupapa', 4);
        }).then('', function () {
            test.bool(ok).isTrue();
        });
    });

    it('string ze znakiem specjalnym', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('dupa-', 4);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });

    it('jedna spacja', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow(' ', 1);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });

    it('dwie spacje', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('  ', 2);
        }).then('', function () {
            test.bool(ok).isFalse();
        });
    });

    it('duże litery', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('DUPA', 4);
        }).then('', function () {
            test.bool(ok).isTrue();
        });
    });

    it('duże litery i cyfry', function () {
        var ok;
        test.when(function () {
            ok = commons.sprawdzenieCzyStringNiepustyIDluzszyOdIZDozwolonychZnakow('Dupa13', 3);
        }).then('', function () {
            test.bool(ok).isTrue();
        });
    });
});