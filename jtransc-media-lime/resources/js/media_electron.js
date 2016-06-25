var ipcRenderer = null;

if (typeof require !== 'undefined') {
	var electron = require('electron');
	if (electron) {
		ipcRenderer = electron.ipcRenderer;
		console.log = function() { ipcRenderer.send('console.log', Array.from(arguments)); };
		console.warn = function() { ipcRenderer.send('console.warn', Array.from(arguments)); };
		console.error = function() { ipcRenderer.send('console.error', Array.from(arguments)); };
		window.onerror = function myErrorHandler(errorMsg, url, lineNumber) {
            //alert("Error occured: " + errorMsg);//or any message
            ipcRenderer.send('window.error', Array.from(arguments));
            return false;
        }
	}
}
