var functions = require('firebase-functions');

// Import and initialize the Firebase Admin SDK.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotifications = functions.database.ref('/chats/{chatID}').onWrite(event => {
  const snapshot = event.data;
  // Only send a notification when a new message has been created.
  if (snapshot.previous.val()) {
    return;
  }

  // Notification details.
  const text = snapshot.val().message;
  const toUser = snapshot.val().toUserID;
  const fromUser = snapshot.val().fromUserID;
var toUserDtl;
var fromUserDtl;
return admin.database().ref('users').orderByKey().equalTo(fromUser).on("child_added", function(snapshot) {
  fromUserDtl = snapshot.val();
return admin.database().ref('users').orderByKey().equalTo(toUser).on("child_added", function(snapshot) {
  toUserDtl = snapshot.val();
 const payload = {
    data: {
      title: fromUserDtl.displayName,
      body: text ? (text.length <= 100 ? text : text.substring(0, 97) + '...') : ''
    }
  };
  return admin.messaging().sendToDevice(toUserDtl.fcmToken, payload).then(response => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', toUserDtl.fcmToken, error);
            // Cleanup the tokens who are not registered anymore.
            // if (error.code === 'messaging/invalid-registration-token' ||
            //     error.code === 'messaging/registration-token-not-registered') {
            //   tokensToRemove.push(allTokens.ref.child(tokens[index]).remove());
            // }
          }
        });
        //return Promise.all(tokensToRemove);
      });
});
});
});