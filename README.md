Backend code for LQSAndroid.

Current features:
- User registration
- User login
- New ESP32 microcontrollers automatically register themselves and send their data
- Users can 'follow' ESP32 microcontrollers to get their collected data
- Users can change the graph color of subscribed controllers

Planned features:
- Firebase Push notification, if threshold of eCO2 or TVOC values is exceeded
- Users can set their notification interval (after a push notification there won't be another for e.g. 30 minutes)
- Users can subscribe to controllers to just see the values but without getting push notifications
