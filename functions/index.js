const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
//exports.addMessage = functions.https.onRequest(async (req, res) => {
//  // Grab the text parameter.
//  const account_from = req.query.account_from;
//  // Push the new message into the Realtime Database using the Firebase Admin SDK.
//  const snapshot = await admin.database().ref('/messages').push({original: original});
//  // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
//  res.redirect(303, snapshot.ref.toString());
//});

// Listens for new messages added to /messages/:pushId/original and creates an
// uppercase version of the message to /messages/:pushId/uppercase
exports.runTransaction = functions.database.ref('/transactionlog/{pushId}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const ref = snapshot;
      const fromId = ref.child('fromId').val();
      const toId = ref.child('toId').val();
      const amount = ref.child('amount').val();
      const type = ref.child('type').val();
      var mod = null;
      if (ref.child('modificator') !== null) {
         mod = ref.child('modificator').val();
      }
      console.log('runTransaction', context.params.pushId, ' from:', fromId, ' to:', toId, ' amount:', amount);
      const myDB = admin.database();
      var userFrom;
      var userTo;

      const ph = myDB.ref('pccredentials holder');

//      var Queue = require('blocking-queue');
//      var QueueConsumer = require('blocking-queue').QueueConsumer;
//      var q = new Queue();
//      const creds = myDB.child('pccredentials holder');
//      const userFrom = myDB.child('Users/' + creds.child(fromId).val());
//      const userTo = myDB.child('Users/' + creds.child(toId).val());
//.orderByChild('pccredentials holder').equalTo(fromId).limitToFirst(1)

      ph.once('value',
              function(snapshot1) {

              console.log('sp1:', snapshot1.val());
              userFrom =  snapshot1.child(fromId).val();
              userTo = snapshot1.child(toId).val();
              console.log('UF:', userFrom, ' UT:', userTo);

              const userRefer = myDB.ref('Users');

              userRefer.once('value',
                    function(snapshot2) {

                            console.log('sp:', snapshot2.val());
                            console.log('UF:', userFrom, ' UT:', userTo);
                            var fromBalance = snapshot2.child(userFrom).child(type).val(); // get from DB
                            var toBalance = snapshot2.child(userTo).child(type).val(); // get from DB

                            console.log('runTransaction', context.params.pushId, ' fromB:', fromBalance, ' toB:', toBalance, ' amount:', amount);

                            if (mod === 'send') {
                                if (fromBalance >= amount) {
                                    fromBalance -= amount;
                                    toBalance += amount;
                                    snapshot2.child(userFrom).ref.update({[type] : fromBalance});
                                    snapshot2.child(userTo).ref.update({[type] : toBalance});
                                    // push to DB
                                } else
                                    ref.ref.update({'status' : 'fail'});
                            } else if (mod === 'take') {
                                console.log('prop', snapshot2.child(userFrom).child('property').val().split(',')[0]);
                                var mset = new Set(snapshot2.child(userFrom).child('property').val().split(','));
                                if (mset.has('STB') && userTo === 'Sturgeon bank' || mset.has('BBGB') && userTo === 'BB government bank' || mset.has('BBIT') && userTo === 'BBIT' || mset.has(userTo)) {
                                    if (toBalance >= amount) {
                                        fromBalance += amount;
                                        toBalance -= amount;
                                        snapshot2.child(userFrom).ref.update({[type] : fromBalance});
                                        snapshot2.child(userTo).ref.update({[type] : toBalance});
                                        // push to DB
                                    } else
                                        ref.ref.update({'status' : 'fail'});
                                } else
                                    ref.ref.update({'status' : 'fail'});
                            } else {
                                if (fromBalance >= amount) {
                                    fromBalance -= amount;
                                    toBalance += amount;
                                    snapshot2.child(userFrom).ref.update({[type] : fromBalance});
                                    snapshot2.child(userTo).ref.update({[type] : toBalance});
                                    // push to DB
                                } else
                                    ref.ref.update({'status' : 'fail'});
                            }
//                            setTimeout(function(){
//                                userRefer.off();
//                            }, 2000);
                    }
              );
//              setTimeout(function(){
//                   ph.off('value', originalCallback, this);
//               }, 2000);
//              var status = 'ok';
//              // You must return a Promise when performing asynchronous tasks inside a Functions such as
//              // writing to the Firebase Realtime Database.
//              // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
//              return ref.child('status').set(status);
//              snapshot.keys().forEach(function(sh) {
//                               if(sh.val === fromId){
//                                   userFrom = sh.val();
//                                   console.log('runTransaction', context.params.pushId, ' fromB:', userFrom, ' toB:', userTo, ' amount:', amount);
//                                   return true;
//                               }
//                               if(sh.val === toId){
//                                   userFrom = sh.val();
//                                   console.log('runTransaction', context.params.pushId, ' fromB:', userFrom, ' toB:', userTo, ' amount:', amount);
//                                   return true;
//                               }
//
//              });

              }
      );

//      myDB.ref('pccredentials holder').orderByKey().equalTo(toId.toString()).limitToFirst(1).on('value',
//                    function(snapshot) {
//                          userTo = snapshot.val();
//                    }
//      );


//      console.log('runTransaction', context.params.pushId, ' fromB:', userFrom, ' toB:', userTo, ' amount:', amount);
//      var fromBalance = userFrom.child('pcredentials').val(); // get from DB
//      var toBalance = userTo.child('pcredentials').val(); // get from DB
//
//      console.log('runTransaction', context.params.pushId, ' fromB:', fromBalance, ' toB:', toBalance, ' amount:', amount);
//
//      fromBalance -= amount;
//      toBalance += amount;
//      userFrom.child('pcredentials').set(fromBalance);
//      userTo.child('pcredentials').set(toBalance);
//      // push to DB
      var status = 'ok';
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return ref.ref.update({'status' : status});
    });

exports.addMessage = functions.https.onCall((data, context) => {
    const myDB = admin.database();
    const mailref = myDB.ref('Mail');

    const body = data.Body;
    const head = data.Head;
    const from = data.From;
    const uto = data.TO;

    console.log('h:', head, ' f:', from, ' b:', body, ' u:', uto);

    const pch = myDB.ref('pccredentials holder');
    pch.once('value',
                  function(snapshot11) {
                    const uT = snapshot11.child(uto).val();
                    console.log('ut:', uT);
                    mailref.once('value',
                        function(snapshot21) {
                            var usermail = snapshot21.child(uT);
                            var mn = 'Mail' + (usermail.child('Num').val() + 1);
                            usermail.child(mn).ref.set({'Body' : body, 'From' : from, 'Heading' : head});
                            var num = usermail.child('Num').val() + 1;
                            usermail.ref.update({'Num' : num});
                        }
                    );
                  }
    );
});

//Это то, что берёт из базы данных и отправляет клиенту
exports.returnVersion = functions.https.onCall((data, context) => {
    const myDB = admin.database();
    var trans = 1;
    return myDB.ref('Version').once('value',
                  function(snapshot111) {
                    console.log('sp:', snapshot111.val());
                    trans = snapshot111.val();
                  }
    ).then(() => {
         return trans;
       });
});