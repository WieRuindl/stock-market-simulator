$(function() {
	WebSocketClient.init();
});

new function() {

	var ws = null;
	var connected = false;

	var serverUrl;
	var connectionStatus;
	
	var connectButton;
	var disconnectButton; 

	WebSocketClient = {
		init: function() {
			serverUrl = $('#serverUrl');
			connectionStatus = $('#connectionStatus');			
			connectButton = $('#connectButton');
			disconnectButton = $('#disconnectButton'); 
			
			connectButton.click(function(e) {
				open();
			});
		
			disconnectButton.click(function(e) {
				close();
			});
		}
	};


	var open = function() {
		var url = serverUrl.val();
		ws = new WebSocket(url);

		ws.onopen = onOpen;
		ws.onclose = onClose;
		ws.onmessage = onMessage;
		ws.onerror = onError;
	}
	
	var close = function() {
		if (ws) {
			console.log('CLOSING ...');
			ws.close();
		}
		connected = false;
		connectionStatus.text('CLOSED');

		serverUrl.removeAttr('disabled');
		connectButton.show();
		disconnectButton.hide();
	}
	
	var onOpen = function() {
		console.log('OPENED: ' + serverUrl.val());
		connected = true;
		connectionStatus.text('OPENED');
		serverUrl.attr('disabled', 'disabled');
		connectButton.hide();
		disconnectButton.show();
	};
	
	var onClose = function() {
		console.log('CLOSED: ' + serverUrl.val());
		ws = null;
	};
	
	var onMessage = function(event) {
		var data = event.data;
		addMessage(data);
	};
	
	var onError = function(event) {
		alert(event.data);
	}
	
	var addMessage = function(data, type) {
		var msg = $('<pre>').text(data);
		if (type === 'SENT') {
			msg.addClass('sent');
		}
		var messages = $('#messages');
		messages.append(msg);
		
		var msgBox = messages.get(0);
		while (msgBox.childNodes.length > 1000) {
			msgBox.removeChild(msgBox.firstChild);
		}
		msgBox.scrollTop = msgBox.scrollHeight;
	}
}
