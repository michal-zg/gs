import IUser = require("./IUser");
enum EventStatus {'new', 'canceled'}
interface IEvent {
    id:string;
    creator:IUser;
    name:string;
    date:Date;
    accountsRejected:IUser[];
    accountsConfirmed:IUser[];
    status:EventStatus;
}
export = IEvent ;