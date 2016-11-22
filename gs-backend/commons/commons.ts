import IUser = require("../models/IUser");

function indexOf(arrayToRemoveFrom:IUser[], element:IUser):number {
    let fromIndex:number = arrayToRemoveFrom.map(u => u.id).indexOf(element.id);
    return fromIndex;
}

function isNotSet(array:any):boolean {
    return typeof array == "undefined"
        || array == null;
}
function set(array:IUser[]):IUser[] {
    if (isNotSet(array)) {
        array = [];
    }
    return array;
}

function moveBetweenArrays(element:IUser, arrayToAddTo:IUser[], arrayToRemoveFrom:IUser[]):IUser[] {

    arrayToAddTo = set(arrayToAddTo);
    arrayToRemoveFrom = set(arrayToRemoveFrom);

    if (isNotSet(element)) {
        return arrayToAddTo;
    }

    var fromIndex = indexOf(arrayToRemoveFrom, element);
    if (fromIndex != -1) {
        arrayToRemoveFrom.splice(arrayToRemoveFrom.indexOf(element), 1)
    }

    var toIndex = indexOf(arrayToAddTo, element);
    if (toIndex == -1) {
        arrayToAddTo.push(element)
    }

    return arrayToAddTo;
}

export {moveBetweenArrays};