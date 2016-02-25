var Alarm = function() {};

Alarm.prototype.startService = function(success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","startService", []);
};

Alarm.prototype.stopService = function(success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","stopService", []);
};

Alarm.prototype.wakeup = function(interval, timeFrom, timeTo, success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","schedule", [interval, timeFrom, timeTo]);
};

var alarm = new Alarm();
module.exports = alarm;