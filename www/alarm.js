var Alarm = function() {};

Alarm.prototype.wakeup = function(interval, timeFrom, timeTo, success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","wakeup", [interval, timeFrom, timeTo]);
};

var alarm = new Alarm();
module.exports = alarm;