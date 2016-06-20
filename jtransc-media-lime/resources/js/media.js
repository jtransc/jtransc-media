var Media = function() {
};
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
Media.IO = function() {
};

Media.IO.readBytesAsync = function(path, callback) {
	downloadBytes(path, callback);
};

////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
Media.EventLoop = function() {
};

/**
 *
 * @type HTMLCanvasElement
 */
var canvas = null;

function handleResize() {
	var width = window.innerWidth;
	var height = window.innerHeight;
	{% SMETHOD com.jtransc.media.JTranscWindow:setScreenSize %}(window.innerWidth, window.innerHeight);
	canvas.width = width;
	canvas.height = height;
	canvas.style.width = width + 'px';
	canvas.style.height = height + 'px';
}

Media.EventLoop.installEvents = function() {
	window.addEventListener('resize', handleResize);

	function mouseInfo() { return {% SFIELD com.jtransc.media.JTranscInput:mouseInfo %}; }
	function keyInfo() { return {% SFIELD com.jtransc.media.JTranscInput:keyInfo %}; }
	function inputImpl() { return {% SFIELD com.jtransc.media.JTranscInput:impl %}; }

	function setScreenXY(x, y) {
		mouseInfo()['{% METHOD com.jtransc.media.JTranscInput$MouseInfo:setScreenXY %}'](x|0, y|0);
	}

	/**
	 * @param e MouseEvent
	 */
	window.addEventListener('mouseup', function(e) {
		setScreenXY(e.clientX, e.clientY);
		mouseInfo()["{% FIELD com.jtransc.media.JTranscInput$MouseInfo:buttons %}"] &= ~(1 << e.which);
		inputImpl()["{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseUp %}"](mouseInfo());
	});
	window.addEventListener('mousedown', function(e) {
		setScreenXY(e.clientX, e.clientY);
		mouseInfo()["{% FIELD com.jtransc.media.JTranscInput$MouseInfo:buttons %}"] |= (1 << e.which);
		inputImpl()["{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseDown %}"](mouseInfo());
	});

	window.addEventListener('mousemove', function(e) {
		setScreenXY(e.clientX, e.clientY);
		inputImpl()["{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseMove %}"](mouseInfo());
	});

	window.addEventListener('mousewheel', function(e) {
		inputImpl()["{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseWheel %}"](e.deltaY|0);
	});

	window.addEventListener('keydown', function(e) {
		keyInfo()["{% FIELD com.jtransc.media.JTranscInput$KeyInfo:keyCode %}"] = e.keyCode | 0;
		inputImpl()["{% METHOD com.jtransc.media.JTranscInput$Handler:onKeyDown %}"](keyInfo());
	});

	window.addEventListener('keyup', function(e) {
		keyInfo()["{% FIELD com.jtransc.media.JTranscInput$KeyInfo:keyCode %}"] = e.keyCode | 0;
		inputImpl()["{% METHOD com.jtransc.media.JTranscInput$Handler:onKeyUp %}"](keyInfo());
	});

	handleResize();
};

/**
 * @type WebGLRenderingContext
 */
var GL = WebGLRenderingContext;
var gl = null;

Media.EventLoop.loopInit = function(init) {
	setTimeout(function() {
		//noinspection JSValidateTypes
		canvas = document.getElementById('jtransc_canvas');
    	if (!canvas) {
    		//noinspection JSValidateTypes
			canvas = document.createElement('canvas');
    		canvas.id = 'jtransc_canvas';
    		canvas.width = 640;
    		canvas.height = 480;
    		canvas.style.width = '640px';
    		canvas.style.height = '480px';
    		canvas.innerText = "Your browser doesn't appear to support the canvas element";
    		document.body.style.padding = '0';
    		document.body.style.margin = '0';
    		document.body.style.overflow = 'hidden';
    		document.body.appendChild(canvas);
    	}

    	gl = null;
    	try {
    		// Try to grab the standard context. If it fails, fallback to experimental.
    		gl = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
    	} catch(e) {
    	}

    	//gl.clearColor(1.0, 0.0, 0.0, 1.0);
    	//gl.clear(gl.COLOR_BUFFER_BIT);

    	init();

    	Media.Render.init();

		Media.EventLoop.installEvents();
	}, 0);
};

Media.EventLoop.loopLoop = function(update, render) {
	function frame() {
		update();
		render();
		requestAnimationFrame(frame);
	}
	frame();
};

////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
Media.Sound = function() {
};
Media.Sound.create = function(path) {
	return 0;
};
Media.Sound.play = function(id) {
};
Media.Sound.dispose = function(id) {
};

////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
Media.Texture = function() {
};

Media.Texture.availableIds = Array.apply(null, {length: 2048}).map(Number.call, Number);
Media.Texture.imagesById = {};
Media.Texture.texturesById = {};

Media.Texture.allocId = function() {
	return Media.Texture.availableIds.pop();
};

Media.Texture._createTextureAtId = function(id, image) {
	var glTexture = gl.createTexture();
	Media.Texture.texturesById[id] = glTexture;

	gl.bindTexture(gl.TEXTURE_2D, glTexture);

	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);

	gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, image);

	gl.bindTexture(gl.TEXTURE_2D, null);

};

Media.Texture.create = function(path, width, height, mipmaps) {
	var id = Media.Texture.allocId();

	var img = document.createElement('img');
	//Media.Texture.imagesById[id] = img;
	Media.Texture.imagesById[id] = null;
	img.onload = function() {
		Media.Texture._createTextureAtId(id, img);
	};
	img.src = path;

	return id;
};
Media.Texture.createMemory = function(data, width, height, mipmaps) {
	var id = Media.Texture.allocId();
	return id;
};
Media.Texture.createEncoded = function(data, width, height, mipmaps) {
	var id = Media.Texture.allocId();

	var img = document.createElement('img');
	var blob = new Blob( [ data ], { type: "image/jpeg" } );
	var urlCreator = window.URL || window.webkitURL;
	var imageUrl = urlCreator.createObjectURL( blob );
	Media.Texture.imagesById[id] = null;
	img.onload = function() {
		Media.Texture._createTextureAtId(id, img);
	};
	img.src = imageUrl;

	return id;
};
Media.Texture.dispose = function(id) {
	if (Media.Texture.texturesById[id] != null) {
		gl.deleteTexture(Media.Texture.texturesById[id]);
	}

	delete Media.Texture.imagesById[id];
	delete Media.Texture.texturesById[id];
	Media.Texture.availableIds.push(id);
};

////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
Media.Render = function() {
};

Media.Render.program = null;
Media.Render.glVertexAttribute = null;
Media.Render.glTextureAttribute = null;
Media.Render.glColor0Attribute = null;
Media.Render.glColor1Attribute = null;
Media.Render.glMatrixUniform = null;
Media.Render.glImageUniform = null;
Media.Render.indicesBuffer = null;
Media.Render.verticesBuffer = null;

Media.Render.init = function() {
	function compileShader(source, type) {
		var shader = gl.createShader (type);
		gl.shaderSource (shader, source);
		gl.compileShader (shader);

		if (gl.getShaderParameter (shader, gl.COMPILE_STATUS) == 0) {
			throw "Error compiling shader : " + type;
		}

		return shader;
	}

	function createProgram(vertexSource, fragmentSource) {
		var vertexShader = compileShader (vertexSource, gl.VERTEX_SHADER);
		var fragmentShader = compileShader (fragmentSource, gl.FRAGMENT_SHADER);

		var program = gl.createProgram ();
		gl.attachShader (program, vertexShader);
		gl.attachShader (program, fragmentShader);
		gl.linkProgram (program);

		if (gl.getProgramParameter (program, gl.LINK_STATUS) == 0) {
			var error = gl.getProgramInfoLog(program);
			gl.deleteProgram(program);
			throw "Unable to initialize the shader program : " + error;
		}

		return program;
	}


	Media.Render.program = createProgram([
		'precision mediump float;',
		'uniform mat4 u_matrix;',
		'',
		'attribute vec2 a_position;',
		'attribute vec2 a_texcoord;',
		'attribute vec4 a_color;',
		'attribute vec4 a_colorOffset;',
		'',
		'varying mediump vec2 v_texcoord;',
		'varying mediump vec4 v_color;',
		'varying mediump vec4 v_colorOffset;',
		'',
		'void main() {',
		'	gl_Position = u_matrix * vec4(a_position, 0, 1);',
		'	v_texcoord = a_texcoord;',
		'	v_color = a_color;',
		'	v_colorOffset = (a_colorOffset - vec4(0.5, 0.5, 0.5, 0.5)) * 2.0;',
		'}'
	].join("\n"), [
		'uniform sampler2D u_sampler;',
		'//',
		'varying mediump vec4 v_color;',
		'varying mediump vec4 v_colorOffset;',
		'//',
		'varying mediump vec2 v_texcoord;',
		'//',
		'void main() {',
		'	gl_FragColor = texture2D(u_sampler, v_texcoord.st);',
		'	if (gl_FragColor.a <= 0.0) discard;',
		'	// gl_FragColor.rgb /= gl_FragColor.a;//// alpha premultiplied is disable, we will study more in the future',
		'	gl_FragColor *= v_color;',
		'	gl_FragColor += v_colorOffset;',
		'	// gl_FragColor.rgb *= gl_FragColor.a;//// alpha premultiplied is disable, we will study more in the future',
		'	if (gl_FragColor.a <= 0.0) discard;',
		'}'
	].join("\n"));

	gl.useProgram(Media.Render.program);

	 // Attributes
	Media.Render.glVertexAttribute = gl.getAttribLocation(Media.Render.program, "a_position");
	Media.Render.glTextureAttribute = gl.getAttribLocation(Media.Render.program, "a_texcoord");
	Media.Render.glColor0Attribute = gl.getAttribLocation(Media.Render.program, "a_color");
	Media.Render.glColor1Attribute = gl.getAttribLocation(Media.Render.program, "a_colorOffset");

	// Uniforms
	Media.Render.glMatrixUniform = gl.getUniformLocation(Media.Render.program, "u_matrix");
	Media.Render.glImageUniform = gl.getUniformLocation(Media.Render.program, "u_sampler");
};

function glEnableDisable(gl, type, cond) {
	if (cond) {
		gl.enable(type);
	} else {
		gl.disable(type);
	}
	return cond;
}

var MASK_NONE = 0;
var MASK_SHAPE = 1;
var MASK_CONTENT = 2;

//noinspection JSUnusedGlobalSymbols
var BLEND_INVALID = -1;
//noinspection JSUnusedGlobalSymbols
var BLEND_AUTO = 0;
var BLEND_NORMAL = 1;
var BLEND_MULTIPLY = 3;
var BLEND_SCREEN = 4;
var BLEND_ADD = 8;
var BLEND_ERASE = 12;
var BLEND_NONE = 15;
var BLEND_BELOW = 16;
//noinspection JSUnusedGlobalSymbols
var BLEND_MAX = 17;

var FULL_SCISSORS = [0,0,2048,2048];
var currentScissors = [0,0,2048,2048];
var lastClip = [0,0,2048,2048];

//Media.Render.render = function(FastMemory vertices, int vertexCount, short[] indices, int indexCount, int[] batches, int batchCount) {

//noinspection PointlessArithmeticExpressionJS
Media.Render.render = function(vertices, vertexCount, indices, indexCount, batches, batchCount) {
	var _batches = batches;

	//var virtualActualWidth = getVirtualActualWidth();
	//var virtualActualHeight = getVirtualActualHeight();
	//var screenWidth = getScreenWidth();
	//var screenHeight = getScreenHeight();
	//var virtualScaleX = getVirtualScaleX();
	//var virtualScaleY = getVirtualScaleY();

	//var virtualActualWidth = 640;
	//var virtualActualHeight = 480;
	//var screenWidth = 640;
	//var screenHeight = 480;
	//var virtualScaleX = 1;
	//var virtualScaleY = 1;

	var virtualActualWidth = {% SMETHOD com.jtransc.media.JTranscWindow:getVirtualActualWidth %}();
	var virtualActualHeight = {% SMETHOD com.jtransc.media.JTranscWindow:getVirtualActualHeight %}();
	var screenWidth = {% SMETHOD com.jtransc.media.JTranscWindow:getScreenWidth %}();
	var screenHeight = {% SMETHOD com.jtransc.media.JTranscWindow:getScreenHeight %}();
	var virtualScaleX = {% SMETHOD com.jtransc.media.JTranscWindow:getVirtualScaleX %}();
	var virtualScaleY = {% SMETHOD com.jtransc.media.JTranscWindow:getVirtualScaleY %}();

	var indicesData = indices;
	var verticesData = vertices;

	gl.enable(gl.BLEND);
	gl.viewport(0, 0, screenWidth|0, screenHeight|0);

	gl.clearColor(0.2, 0.2, 0.2, 1.0);
	gl.clearStencil(0);

	gl.clear(gl.COLOR_BUFFER_BIT | gl.STENCIL_BUFFER_BIT);

	gl.useProgram(Media.Render.program);

	var matrix = createOrtho(0, virtualActualWidth, virtualActualHeight, 0, -1000, 1000);
	gl.uniformMatrix4fv(Media.Render.glMatrixUniform, false, matrix);
	gl.uniform1i(Media.Render.glImageUniform, 0);

	if (Media.Render.indicesBuffer == null) Media.Render.indicesBuffer = gl.createBuffer();
	if (Media.Render.verticesBuffer == null) Media.Render.verticesBuffer = gl.createBuffer();

	gl.bindBuffer(gl.ARRAY_BUFFER, Media.Render.verticesBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, verticesData, gl.STATIC_DRAW);

	gl.enableVertexAttribArray(Media.Render.glVertexAttribute);
	gl.enableVertexAttribArray(Media.Render.glTextureAttribute);
	gl.enableVertexAttribArray(Media.Render.glColor0Attribute);
	gl.enableVertexAttribArray(Media.Render.glColor1Attribute);

	var STRIDE = 6 * 4;
	gl.bindBuffer(gl.ARRAY_BUFFER, Media.Render.verticesBuffer);
	//noinspection PointlessArithmeticExpressionJS
	gl.vertexAttribPointer(Media.Render.glVertexAttribute , 2, gl.FLOAT, false, STRIDE, 0 * 4);
	gl.vertexAttribPointer(Media.Render.glTextureAttribute, 2, gl.FLOAT, false, STRIDE, 2 * 4);
	gl.vertexAttribPointer(Media.Render.glColor0Attribute , 4, gl.UNSIGNED_BYTE, true, STRIDE, 4 * 4);
	gl.vertexAttribPointer(Media.Render.glColor1Attribute , 4, gl.UNSIGNED_BYTE, true, STRIDE, 5 * 4);

	gl.enable(gl.BLEND);

	gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, Media.Render.indicesBuffer);
	gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, indicesData, gl.STATIC_DRAW);

	lastClip[0] = FULL_SCISSORS[0];
	lastClip[1] = FULL_SCISSORS[1];
	lastClip[2] = FULL_SCISSORS[2];
	lastClip[3] = FULL_SCISSORS[3];

	gl.disable(gl.SCISSOR_TEST);
	gl.disable(gl.STENCIL_TEST);
	gl.depthMask(false);
	gl.colorMask(true, true, true, true);
	gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
	gl.stencilFunc(gl.EQUAL, 0x00, 0x00);
	gl.stencilMask(0x00);

	gl.activeTexture(gl.TEXTURE0);
	gl.bindTexture(gl.TEXTURE_2D, null);

	var lastMaskType = MASK_NONE;
	var lastStencilIndex = -1;

	for (var batchId = 0; batchId < batchCount; batchId++) {
		var batchOffset = batchId * 16;
		//noinspection PointlessArithmeticExpressionJS
		var indexStart      = _batches[batchOffset + 0];
		var triangleCount   = _batches[batchOffset + 1];
		var textureId       = _batches[batchOffset + 2];
		var blendMode       = _batches[batchOffset + 3];
		var currentMaskType = _batches[batchOffset + 4];
		var currentStencilIndex    = _batches[batchOffset + 5];
		var scissorLeft     = _batches[batchOffset + 6];
		var scissorTop      = _batches[batchOffset + 7];
		var scissorRight    = _batches[batchOffset + 8];
		var scissorBottom   = _batches[batchOffset + 9];

		currentScissors[0] = scissorLeft;
		currentScissors[1] = scissorTop;
		currentScissors[2] = scissorRight - scissorLeft;
		currentScissors[3] = scissorBottom - scissorTop;

		var glTexture = Media.Texture.texturesById[textureId];

		gl.activeTexture(gl.TEXTURE0);
		gl.bindTexture(gl.TEXTURE_2D, glTexture);

		var premultiplied = false;
		if (!premultiplied) {
			switch (blendMode) {
				case BLEND_NONE    : gl.blendFunc(gl.ONE, gl.ZERO); break;
				case BLEND_NORMAL  : gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA); break;
				case BLEND_ADD     : gl.blendFunc(gl.SRC_ALPHA, gl.ONE); break;
				case BLEND_MULTIPLY: gl.blendFunc(gl.DST_COLOR, gl.ONE_MINUS_SRC_COLOR); break;
				case BLEND_SCREEN  : gl.blendFunc(gl.SRC_ALPHA, gl.ONE); break;
				case BLEND_ERASE   : gl.blendFunc(gl.ZERO, gl.ONE_MINUS_SRC_ALPHA); break;
				case BLEND_BELOW   : gl.blendFunc(gl.ONE_MINUS_DST_ALPHA, gl.DST_ALPHA); break;
				default: gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA); break;
			}
		} else {
			switch (blendMode) {
				case BLEND_NONE    : gl.blendFunc(gl.ONE, gl.ZERO); break;
				case BLEND_NORMAL  : gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA); break;
				case BLEND_ADD     : gl.blendFunc(gl.ONE, gl.ONE); break;
				case BLEND_MULTIPLY: gl.blendFunc(gl.DST_COLOR, gl.ONE_MINUS_SRC_ALPHA); break;
				case BLEND_SCREEN  : gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_COLOR); break;
				case BLEND_ERASE   : gl.blendFunc(gl.ZERO, gl.ONE_MINUS_SRC_ALPHA); break;
				case BLEND_BELOW   : gl.blendFunc(gl.ONE_MINUS_DST_ALPHA, gl.DST_ALPHA); break;
				default: gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA); break;
			}
		}

		if (String(lastClip) != String(currentScissors)) {
			lastClip[0] = currentScissors[0];
			lastClip[1] = currentScissors[1];
			lastClip[2] = currentScissors[2];
			lastClip[3] = currentScissors[3];
			//if (debugBatch) batchReasons.push(PrenderBatchReason.CLIP)

			if (glEnableDisable(gl, gl.SCISSOR_TEST, String(lastClip) == String(FULL_SCISSORS))) {
				gl.scissor(
					lastClip.x * virtualScaleX,
					screenHeight - (lastClip.y + lastClip.height) * virtualScaleY,
					lastClip.width * virtualScaleX,
					lastClip.height * virtualScaleY
				);
			}
		}

		if ((lastMaskType != currentMaskType) || (lastStencilIndex != currentStencilIndex)) {
			lastMaskType = currentMaskType;
			lastStencilIndex = currentStencilIndex;
			switch (currentMaskType) {
				case MASK_NONE:
					gl.disable(gl.STENCIL_TEST);
					gl.depthMask(false);
					gl.colorMask(true, true, true, true);
					gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
					gl.stencilFunc(gl.EQUAL, 0x00, 0x00);
					gl.stencilMask(0x00);
					break;
				case MASK_SHAPE:
					gl.enable(gl.STENCIL_TEST);
					gl.depthMask(true);
					gl.colorMask(false, false, false, false);
					gl.stencilOp(gl.REPLACE, gl.REPLACE, gl.REPLACE);
					gl.stencilFunc(gl.ALWAYS, currentStencilIndex, 0xFF);
					gl.stencilMask(0xFF); // write ref
					break;
				case MASK_CONTENT:
					gl.enable(gl.STENCIL_TEST);
					gl.depthMask(true);
					gl.colorMask(true, true, true, true);
					gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
					gl.stencilFunc(gl.EQUAL, currentStencilIndex, 0xFF);
					gl.stencilMask(0x00);
					break;
				default:
					//if (debugBatch) batchReasons.push("mask unknown")
					break;
			}
		}

		//trace('batch:' + indexStart + ',' + triangleCount);

		gl.drawElements(gl.TRIANGLES, triangleCount * 3, gl.UNSIGNED_SHORT, indexStart * 2);
	}

	//gl.deleteBuffer(verticesBuffer);
	//gl.deleteBuffer(indicesBuffer);
};
