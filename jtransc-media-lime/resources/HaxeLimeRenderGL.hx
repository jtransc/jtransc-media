import lime.app.Application;
import lime.graphics.Renderer;
import lime.graphics.GLRenderContext;
import lime.graphics.opengl.*;
import lime.utils.*;
import lime.math.Matrix4;

class HaxeLimeRenderGL extends HaxeLimeRenderImpl {
    public var gl:GLRenderContext;

    private var glMatrixUniform:GLUniformLocation;
    private var glImageUniform:GLUniformLocation;
    private var glProgram:GLProgram;
    private var glTextureAttribute:Int;
    private var glVertexAttribute:Int;
    private var glColor0Attribute:Int;
    private var glColor1Attribute:Int;
    private var textures:Array<GLTexture>;
    private var textureIndices:Array<Int>;

    private var indicesBuffer:GLBuffer = null;
    private var verticesBuffer:GLBuffer = null;

    public function new(gl:GLRenderContext) {
        this.gl = gl;

        textureIndices = [for (i in 1...1024) i];
        textures = [for (i in 0...1024) null];

        init();
    }

    private function init() {
        var PREFIX = "
            #ifdef GL_ES
                #define LOWP lowp
                #define MED mediump
                #define HIGH highp
                precision mediump float;
            #else
                #define MED
                #define LOWP
                #define HIGH
            #endif

        ";

        var vertexSource = PREFIX + "
        	uniform mat4 u_matrix;

        	attribute vec2 a_position;
        	attribute vec2 a_texcoord;
            attribute vec4 a_color;
            attribute vec4 a_colorOffset;

        	varying MED vec2 v_texcoord;
            varying MED vec4 v_color;
            varying MED vec4 v_colorOffset;

        	void main() {
                gl_Position = u_matrix * vec4(a_position, 0, 1);
                v_texcoord = a_texcoord;
                v_color = a_color;
                v_colorOffset = (a_colorOffset - vec4(0.5, 0.5, 0.5, 0.5)) * 2.0;
        	}
        ";

        var fragmentSource = PREFIX + "
				uniform sampler2D u_sampler;

                varying MED vec4 v_color;
                varying MED vec4 v_colorOffset;

				varying MED vec2 v_texcoord;

				void main() {
                    gl_FragColor = texture2D(u_sampler, v_texcoord.st);
                    if (gl_FragColor.a <= 0.0) discard;
//                    gl_FragColor.rgb /= gl_FragColor.a;//// alpha premultiplied is disable, we will study more in the future
                    gl_FragColor *= v_color;
                    gl_FragColor += v_colorOffset;
//                    gl_FragColor.rgb *= gl_FragColor.a;//// alpha premultiplied is disable, we will study more in the future
                    if (gl_FragColor.a <= 0.0) discard;
				}
        ";

        glProgram = GLUtils.createProgram (vertexSource, fragmentSource);
        gl.useProgram(glProgram);

        // Attributes
        glVertexAttribute = gl.getAttribLocation(glProgram, "a_position");
        glTextureAttribute = gl.getAttribLocation(glProgram, "a_texcoord");
        glColor0Attribute = gl.getAttribLocation(glProgram, "a_color");
        glColor1Attribute = gl.getAttribLocation(glProgram, "a_colorOffset");

        // Uniforms
        glMatrixUniform = gl.getUniformLocation(glProgram, "u_matrix");
        glImageUniform = gl.getUniformLocation(glProgram, "u_sampler");
    }

    override public function isInitialized() {
        return true;
    }

    override public function _createTexture(image:lime.graphics.Image, imageFuture:lime.app.Future<lime.graphics.Image>, width:Int, height:Int):Int {
        var id = textureIndices.pop();
        var glTexture = textures[id] = gl.createTexture();

        gl.bindTexture(gl.TEXTURE_2D, glTexture);
        
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);

        if (image != null) {
            gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, image.buffer.width, image.buffer.height, 0, gl.RGBA, gl.UNSIGNED_BYTE, image.data);
        } else {
            var imageBuffer = new lime.graphics.ImageBuffer(new lime.utils.UInt8Array([0,0,0,0]), 1, 1);
            gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, 1, 1, 0, gl.RGBA, gl.UNSIGNED_BYTE, imageBuffer.data);
        }

        if (imageFuture != null) {
            imageFuture.onComplete(function(image) {
                gl.bindTexture(gl.TEXTURE_2D, glTexture);
                gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, image.buffer.width, image.buffer.height, 0, gl.RGBA, gl.UNSIGNED_BYTE, image.data);
                gl.bindTexture(gl.TEXTURE_2D, null);
            });
        }

        gl.bindTexture(gl.TEXTURE_2D, null);

        return id;
    }

    override public function disposeTexture(id:Int) {
        trace('HaxeLimeRenderGL.disposeTexture(id)');
        gl.deleteTexture(textures[id]);
        textures[id] = null;
        textureIndices.push(id);
    }

    private var lastClip = new lime.math.Rectangle(0, 0, 8196, 8196);
    private var currentScissors = new lime.math.Rectangle(0, 0, 8196, 8196);
    private var FULL_SCISSORS = new lime.math.Rectangle(0, 0, 8196, 8196);

    override public function render(
        _vertices:haxe.io.Float32Array, vertexCount:Int,
        _indices:haxe.io.UInt16Array, indexCount:Int,
        _batches:haxe.io.Int32Array, batchCount:Int
    ) {
        var virtualActualWidth = getVirtualActualWidth();
        var virtualActualHeight = getVirtualActualHeight();
        var screenWidth = getScreenWidth();
        var screenHeight = getScreenHeight();
        var virtualScaleX = getVirtualScaleX();
        var virtualScaleY = getVirtualScaleY();


        var indicesData = lime.utils.UInt16Array.fromBytes(_indices.view.buffer, 0, indexCount);
        var verticesData = lime.utils.Float32Array.fromBytes(_vertices.view.buffer, 0, vertexCount * 6);

        gl.enable(gl.BLEND);
        gl.viewport(0, 0, Std.int(screenWidth), Std.int(screenHeight));

        gl.clearColor(0.2, 0.2, 0.2, 1.0);
        gl.clearStencil(0);
        gl.clear(gl.COLOR_BUFFER_BIT | gl.STENCIL_BUFFER_BIT);

        gl.useProgram(glProgram);

        var matrix = Matrix4.createOrtho(0, virtualActualWidth, virtualActualHeight, 0, -1000, 1000);
        gl.uniformMatrix4fv(glMatrixUniform, false, matrix);
        gl.uniform1i(glImageUniform, 0);

        if (indicesBuffer == null) indicesBuffer = gl.createBuffer();
        if (verticesBuffer == null) verticesBuffer = gl.createBuffer();

        gl.bindBuffer(gl.ARRAY_BUFFER, verticesBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, verticesData, gl.STATIC_DRAW);

        gl.enableVertexAttribArray(glVertexAttribute);
        gl.enableVertexAttribArray(glTextureAttribute);
        gl.enableVertexAttribArray(glColor0Attribute);
        gl.enableVertexAttribArray(glColor1Attribute);

        var STRIDE = 6 * 4;
        gl.bindBuffer(gl.ARRAY_BUFFER, verticesBuffer);
        gl.vertexAttribPointer(glVertexAttribute , 2, gl.FLOAT, false, STRIDE, 0 * 4);
        gl.vertexAttribPointer(glTextureAttribute, 2, gl.FLOAT, false, STRIDE, 2 * 4);
        gl.vertexAttribPointer(glColor0Attribute , 4, gl.UNSIGNED_BYTE, true, STRIDE, 4 * 4);
        gl.vertexAttribPointer(glColor1Attribute , 4, gl.UNSIGNED_BYTE, true, STRIDE, 5 * 4);

        gl.enable(gl.BLEND);
        #if desktop gl.enable(gl.TEXTURE_2D); #end

        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indicesBuffer);
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, indicesData, gl.STATIC_DRAW);

        lastClip.copyFrom(FULL_SCISSORS);
        gl.disable(gl.SCISSOR_TEST);
        gl.disable(gl.STENCIL_TEST);
        gl.depthMask(false);
        gl.colorMask(true, true, true, true);
        gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
        gl.stencilFunc(gl.EQUAL, 0x00, 0x00);
        gl.stencilMask(0x00);

        gl.activeTexture(gl.TEXTURE0);
        gl.bindTexture(gl.TEXTURE_2D, null);

        var lastMaskType = HaxeLimeRenderImpl.MASK_NONE;
        var lastStencilIndex = -1;

        for (batchId in 0 ... batchCount) {
            var batchOffset = batchId * 16;
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

            currentScissors.setTo(scissorLeft, scissorTop, scissorRight - scissorLeft, scissorBottom - scissorTop);

            var glTexture = textures[textureId];

            gl.activeTexture(gl.TEXTURE0);
            gl.bindTexture(gl.TEXTURE_2D, glTexture);

            var premultiplied = false;
            if (!premultiplied) {
                switch (blendMode) {
                    case HaxeLimeRenderImpl.BLEND_NONE    : gl.blendFunc(gl.ONE, gl.ZERO);
                    case HaxeLimeRenderImpl.BLEND_NORMAL  : gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_ADD     : gl.blendFunc(gl.SRC_ALPHA, gl.ONE);
                    case HaxeLimeRenderImpl.BLEND_MULTIPLY: gl.blendFunc(gl.DST_COLOR, gl.ONE_MINUS_SRC_COLOR);
                    case HaxeLimeRenderImpl.BLEND_SCREEN  : gl.blendFunc(gl.SRC_ALPHA, gl.ONE);
                    case HaxeLimeRenderImpl.BLEND_ERASE   : gl.blendFunc(gl.ZERO, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_BELOW   : gl.blendFunc(gl.ONE_MINUS_DST_ALPHA, gl.DST_ALPHA);
                    default: gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
                }
            } else {
                switch (blendMode) {
                    case HaxeLimeRenderImpl.BLEND_NONE    : gl.blendFunc(gl.ONE, gl.ZERO);
                    case HaxeLimeRenderImpl.BLEND_NORMAL  : gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_ADD     : gl.blendFunc(gl.ONE, gl.ONE);
                    case HaxeLimeRenderImpl.BLEND_MULTIPLY: gl.blendFunc(gl.DST_COLOR, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_SCREEN  : gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_COLOR);
                    case HaxeLimeRenderImpl.BLEND_ERASE   : gl.blendFunc(gl.ZERO, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_BELOW   : gl.blendFunc(gl.ONE_MINUS_DST_ALPHA, gl.DST_ALPHA);
                    default: gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
                }
            }

            if (!lastClip.equals(currentScissors)) {
                lastClip.copyFrom(currentScissors);
                //if (debugBatch) batchReasons.push(PrenderBatchReason.CLIP)

                if (glEnableDisable(gl, gl.SCISSOR_TEST, lastClip.equals(FULL_SCISSORS))) {
                    gl.scissor(
                        Std.int(lastClip.x * virtualScaleX),
                        Std.int(screenHeight - (lastClip.y + lastClip.height) * virtualScaleY),
                        Std.int(lastClip.width * virtualScaleX),
                        Std.int(lastClip.height * virtualScaleY)
                    );
                }
            }

            if ((lastMaskType != currentMaskType) || (lastStencilIndex != currentStencilIndex)) {
                lastMaskType = currentMaskType;
                lastStencilIndex = currentStencilIndex;
                switch (currentMaskType) {
                    case HaxeLimeRenderImpl.MASK_NONE:
                        gl.disable(gl.STENCIL_TEST);
                        gl.depthMask(false);
                        gl.colorMask(true, true, true, true);
                        gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
                        gl.stencilFunc(gl.EQUAL, 0x00, 0x00);
                        gl.stencilMask(0x00);
                    case HaxeLimeRenderImpl.MASK_SHAPE:
                        gl.enable(gl.STENCIL_TEST);
                        gl.depthMask(true);
                        gl.colorMask(false, false, false, false);
                        gl.stencilOp(gl.REPLACE, gl.REPLACE, gl.REPLACE);
                        gl.stencilFunc(gl.ALWAYS, currentStencilIndex, 0xFF);
                        gl.stencilMask(0xFF); // write ref
                    case HaxeLimeRenderImpl.MASK_CONTENT:
                        gl.enable(gl.STENCIL_TEST);
                        gl.depthMask(true);
                        gl.colorMask(true, true, true, true);
                        gl.stencilOp(gl.KEEP, gl.KEEP, gl.KEEP);
                        gl.stencilFunc(gl.EQUAL, currentStencilIndex, 0xFF);
                        gl.stencilMask(0x00);
                    default:
                        //if (debugBatch) batchReasons.push("mask unknown")
                }
            }

            //trace('batch:' + indexStart + ',' + triangleCount);

            gl.drawElements(gl.TRIANGLES, triangleCount * 3, gl.UNSIGNED_SHORT, indexStart * 2);
        }

        //gl.deleteBuffer(verticesBuffer);
        //gl.deleteBuffer(indicesBuffer);
    }

    static private function glEnableDisable(gl:GLRenderContext, type:Int, cond:Bool) {
        if (cond) {
            gl.enable(type);
        } else {
            gl.disable(type);
        }
        return cond;
    }
}

/*
class WrappedGLTexture {
    public var texture: GLTexture;
}
*/

