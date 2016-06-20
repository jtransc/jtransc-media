function downloadBytes(url, callback) {
	var oReq = new XMLHttpRequest();
	oReq.open("GET", url, true);
	oReq.responseType = "arraybuffer";

	oReq.onload = function (oEvent) {
	  var arrayBuffer = oReq.response; // Note: not oReq.responseText
	  if (arrayBuffer) {
	  	var array = new Uint8Array(arrayBuffer);
	  	//if (url.match(/(png|jpg)$/)) {
	  	//	var img = document.createElement('img');
		//	var arrayBufferView = new Uint8Array( this.response );
		//	var blob = new Blob( [ arrayBufferView ], { type: "image/jpeg" } );
		//	var urlCreator = window.URL || window.webkitURL;
		//	var imageUrl = urlCreator.createObjectURL( blob );
		//	img.onload = function() {
		//		callback(array, img);
		//	};
		//	img.onerror = function() {
		//		callback(array, null);
		//	};
		//	img.src = imageUrl;
//
	  	//} else {
			callback(array, null);
	  	//}
	  } else {
	  	callback(null);
	  }
	};

	oReq.onerror = function (oEvent) {
		callback(null);
	};

	oReq.send(null);
}

function createOrtho(x0, x1,  y0, y1, zNear, zFar) {
	var sx = 1.0 / (x1 - x0);
	var sy = 1.0 / (y1 - y0);
	var sz = 1.0 / (zFar - zNear);

	return new Float32Array ([
		2.0 * sx,     0,          0,                 0,
		0,            2.0 * sy,   0,                 0,
		0,            0,          -2.0 * sz,         0,
		- (x0 + x1) * sx, - (y0 + y1) * sy, - (zNear + zFar) * sz,  1,
	]);
}