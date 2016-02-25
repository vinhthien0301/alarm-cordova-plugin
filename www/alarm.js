var Alarm = function() {};

Alarm.prototype.wakeup = function(success, fail, interval, timeFrom, timeTo) {
    cordova.exec(success, fail, "AlarmPlugin","wakeup", [interval, timeFrom, timeTo]);
};

var alarm = new Alarm();
module.exports = alarm;