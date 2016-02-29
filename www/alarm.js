var Alarm = function() {};

Alarm.prototype.schedule = function(interval, timeFrom, timeTo, mp3, textMessage, success, fail) {
    cordova.exec(success, fail, "AlarmPlugin","schedule", [interval, timeFrom, timeTo, mp3, textMessage]);
};

var alarm = new Alarm();
module.exports = alarm;