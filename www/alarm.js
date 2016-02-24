var Alarm = function() {};

Alarm.prototype.say = function(success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","wakeup", []);
};

var alarm = new Alarm();
module.exports = alarm;