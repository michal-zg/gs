enum EventStatus {'new', 'canceled'}
interface IEvent {
    id:string;
    creator:string;
    name:string;
    date:Date;
    accountsRejected:string[];
    accountsConfirmed:string[];
    status:EventStatus;
}
export = IEvent ;