import IEvent = require("./IEvent");
interface INotification {
    id:string;
    subject:string;
    message:string;
    date:Date;

    event: IEvent;
}
export = INotification ;