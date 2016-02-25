var Alarm = function() {};

Alarm.prototype.wakeup = function(success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","wakeup", []);
};

var alarm = new Alarm();
module.exports = alarm;