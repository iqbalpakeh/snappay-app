const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.transactionNotifications = functions.database.ref('/merchant/{merchantId}/transactions')
    .onWrite(event => {

    	const uid = event.params.merchantId;
    	const tokenPromise = admin.database().ref('/merchant/' + uid + '/token').once('value');	
    	console.log('@1 uid = ', uid);

    	return Promise.resolve(tokenPromise).then(result => {
    		
    		const tokenSnapshot = result;
    		const token = tokenSnapshot.val();
			console.log('@1 token = ', token);

			const payload = {
            	data: {
              		title: 'Your transaction approved',
              		body: 'Check your transaction details'
            	}
        	};	

			return admin.messaging().sendToDevice(token, payload)
			  	.then(function(response) {
    				console.log("@1 Successfully sent message:", response);
  				})
  				.catch(function(error) {
    				console.log("@1 Error sending message:", error);
  				});
    	});      	
    }
);
