import lime.app.Application;
import lime.graphics.Renderer;
import flash.display.*;
import flash.display3D.*;
import flash.display3D.textures.*;
import flash.utils.*;
import flash.geom.Matrix3D;
import flash.Lib;
import flash.geom.Rectangle;
import haxe.ds.Vector;
import lime.Assets;

class HaxeLimeRenderFlash extends HaxeLimeRenderImpl {
    private var stage:Stage;
    private var rootSprite:Sprite;
    private var textureProgram:Program3D;
    private var context:Context3D;
    private var textures:Array<RectangleTexture>;
    private var textureIndices:Array<Int>;
    static public inline var DEBUG:Bool = true;
	static private var CLIP_RECT = new Rectangle();
	private var lastClip:Rectangle = new Rectangle();
	private var batchClip:Rectangle = new Rectangle();

    public function new(rootSprite:Sprite) {
        this.rootSprite = rootSprite;
        this.stage = rootSprite.stage;

        textureIndices = [for (i in 1...1024) i];
        textures = [for (i in 0...1024) null];

        initializeFlash();
    }

    private function initializeFlash() {
        var W = stage.stageWidth;
        var H = stage.stageHeight;
        function __init(e) {
            stage.stage3Ds[0].removeEventListener(flash.events.Event.CONTEXT3D_CREATE, __init);
            trace('contex3d created');
            context = stage.stage3Ds[0].context3D;
            context.enableErrorChecking = DEBUG;
            context.configureBackBuffer(W, H, 4, true);

            var assembler = new AGALMiniAssembler();

            var shaderInfo = createShaderInfo(true, false, true, true);

            textureProgram = context.createProgram();
            textureProgram.upload(
                assembler.assemble(Context3DProgramType.VERTEX, shaderInfo.vertex.join("\n")),
                assembler.assemble(Context3DProgramType.FRAGMENT, shaderInfo.fragment.join("\n"))
            );
        }
        stage.stage3Ds[0].addEventListener(flash.events.Event.CONTEXT3D_CREATE, __init);
        stage.stage3Ds[0].requestContext3D();
        stage.addEventListener(flash.events.Event.RESIZE, function(e) {
            if (context == null) return;
            context.configureBackBuffer(stage.stageWidth, stage.stageHeight, 4, true);
            //render(renderer);
        });
    }

    static private function createShaderInfo(texture:Bool, compressed:Bool, tint:Bool, inmask:Bool):TextureInfo {
		var vertex = [];
		var fragment = [];
		var usePos = true;
		var useTex = false;
		var useCol1 = false;
		var useCol2 = false;
		var useCol3 = false;
		var premultiplied = !compressed;

		//tint = false;

		///////////////////////////////////
		// VERTEX
		///////////////////////////////////
		vertex.push("m44 op, va0, vc0");
		if (texture) {
			useTex = true;
			vertex.push("mov v0, va1");
			if (tint) {
				useCol1 = true;
				useCol2 = true;
				//useCol3 = true;
				vertex.push("mov v1, va2");
				vertex.push("mov vt0, va3");
				vertex.push("mul vt0, vt0, vc4.w");
				vertex.push("sub vt0, vt0, vc4.z");
				vertex.push("mov v2, vt0");
			}

		} else {
			vertex.push("mov v0, va2");
			useCol1 = true;
		}

		///////////////////////////////////
		// FRAGMENT
		///////////////////////////////////
		if (texture) {
			if (compressed) {
				fragment.push("tex ft0, v0.xyxx, fs0 <2d linear mipdisable clamp dxt5>");
			} else {
				fragment.push("tex ft0, v0.xyxx, fs0 <2d linear mipdisable clamp>");
			}

			if (tint) {
				if (premultiplied) fragment.push("div ft0.xyz, ft0.xyz, ft0.w");
				fragment.push("mul ft0, ft0, v1");
				fragment.push("add ft0, ft0, v2");
				if (premultiplied) fragment.push("mul ft0.xyz, ft0.xyz, ft0.w");
			}

			if (inmask) {
				fragment.push("sge ft1.x, fc0.y, ft0.w");
				fragment.push("neg ft1.x, ft1.x");
				fragment.push("kil ft1.x");
			}

			fragment.push("mov oc, ft0");
		} else {
			fragment.push("mov oc, v0");
		}

		return {
			vertex: vertex,
			fragment: fragment,
			options: [usePos, useTex, useCol1, useCol2, useCol3]
		};
	}

    private static var FRAGMENT_CONSTANTS = Vector.fromArrayCopy([
        -1,  // fc0.x
        0,   // fc0.y
        1,   // fc0.z
        2,   // fc0.w
        0.5, // fc1.x
        128, // fc1.y
        255, // fc1.z
        512  // fc1.w
    ]).toData();

    private static var VERTEX_CONSTANTS = Vector.fromArrayCopy([
        -1,  // vc4.x
        0,   // vc4.y
        1,   // vc4.z
        2,   // vc4.w
        0.5, // vc5.x
        128, // vc5.y
        255, // vc5.z
        512  // vc5.w
    ]).toData();

    static private var _map3dRaw = new Vector<Float>(16);
    static public function createOrtho(left:Float, right:Float, bottom:Float, top:Float, near:Float = -1.0, far:Float = 1.0, target:Matrix3D = null):Matrix3D {
        var v = _map3dRaw;
        if (target == null) target = new Matrix3D();
        var a = 2.0 / (right - left);
        var b = 2.0 / (top - bottom);
        var c = -2.0 / (far - near);

        var tx = -(right + left) / (right - left);
        var ty = -(top + bottom) / (top - bottom);
        var tz = -(far + near) / (far - near);

        v[0] = a; v[1] = 0; v[2] = 0; v[3] = tx;
        v[4] = 0; v[5] = b; v[6] = 0; v[7] = ty;
        v[8] = 0; v[9] = 0; v[10] = c; v[11] = tz;
        v[12] = 0; v[13] = 0; v[14] = 0; v[15] = 1;

        target.copyRawDataFrom(v.toData());
        return target;
    }

    override public function isInitialized() {
        return context != null;
    }

    override public function _createTexture(image:lime.graphics.Image, imageFuture:lime.app.Future<lime.graphics.Image>, width:Int, height:Int):Int {
        var id = textureIndices.pop();
        var texture = context.createRectangleTexture(
            width, height,
            Context3DTextureFormat.BGRA,
            false
        );
        textures[id] = texture;
        if (image != null) {
            var bitmapData = image.src;
			if (bitmapData == null) {
				var data = image.buffer.data.toBytes().getData();
				var bmp:flash.display.BitmapData = new BitmapData(image.width, image.height);
				bmp.setPixels(bmp.rect, data);
				bitmapData = bmp;
			}
            texture.uploadFromBitmapData(bitmapData);
        } else {
            texture.uploadFromBitmapData(new flash.display.BitmapData(width, height));
        }

        if (imageFuture != null) {
            imageFuture.onComplete(function(image) {
                var bitmapData = image.src;
                texture.uploadFromBitmapData(bitmapData);
            });
        }

        return id;
    }

    override public function disposeTexture(id:Int) {
        trace('HaxeLimeRenderFlash.disposeTexture(id)');
        textures[id].dispose();
        textures[id] = null;
        textureIndices.push(id);
    }

    private var _projectionMatrix = new Matrix3D();

    override public function render(
        _vertices:haxe.io.Float32Array, vertexCount:Int,
        _indices:haxe.io.UInt16Array, indexCount:Int,
        _batches:haxe.io.Int32Array, batchCount:Int
    ) {
        var context = this.context;

        if (context == null) {
            trace('context == null');
            return;
        }
        var c_r = 0, c_g = 0, c_b = 0, c_a = 1;
        context.clear(c_r, c_g, c_b, c_a, 1, 0, Context3DClearMask.ALL);

        if (batchCount > 0) {
            var verticesOut = new Vector<Float>(vertexCount * 6);
            var indicesOut = new Vector<UInt>(indexCount);
            for (n in 0 ... verticesOut.length) verticesOut[n] = _vertices[n];
            for (n in 0 ... indicesOut.length) indicesOut[n] = _indices[n];

            var vertexBuffer = context.createVertexBuffer(Std.int(verticesOut.length / 6), 6);
            var indexBuffer = context.createIndexBuffer(indicesOut.length);

            vertexBuffer.uploadFromVector(verticesOut.toData(), 0, Std.int(verticesOut.length / 6));
            indexBuffer.uploadFromVector(indicesOut.toData(), 0, indicesOut.length);

            context.setVertexBufferAt(0, vertexBuffer, 0, Context3DVertexBufferFormat.FLOAT_2);
            context.setVertexBufferAt(1, vertexBuffer, 2, Context3DVertexBufferFormat.FLOAT_2);
			context.setVertexBufferAt(2, vertexBuffer, 4, Context3DVertexBufferFormat.BYTES_4);
			context.setVertexBufferAt(3, vertexBuffer, 5, Context3DVertexBufferFormat.BYTES_4);

            createOrtho(0, getVirtualActualWidth(), getVirtualActualHeight(), 0, -1, 1, _projectionMatrix);
            context.setProgramConstantsFromMatrix(Context3DProgramType.VERTEX, 0, _projectionMatrix);
            context.setProgramConstantsFromVector(Context3DProgramType.VERTEX, 4, VERTEX_CONSTANTS);
            context.setProgramConstantsFromVector(Context3DProgramType.FRAGMENT, 0, FRAGMENT_CONSTANTS);

            context.setColorMask(true, true, true, true);
            context.setStencilReferenceValue(0, 0, 0);
            context.setDepthTest(false, Context3DCompareMode.ALWAYS);

            //context.setSamplerStateAt(0, Context3DWrapMode.CLAMP, Context3DTextureFilter.LINEAR, Context3DMipFilter.MIPNONE);

			lastClip.setTo(0, 0, 8192, 8192);

			var virtualScaleX = 1.0;
			var virtualScaleY = 1.0;

			var lastMaskType = -1;
			var lastStencilIndex = -1;

            for (batchId in 0 ... batchCount) {
                var batchOffset = batchId * 16;
                var indexStart    = _batches[batchOffset + 0];
                var triangleCount = _batches[batchOffset + 1];
                var textureId     = _batches[batchOffset + 2];
                var blendMode     = _batches[batchOffset + 3];
                var maskType      = _batches[batchOffset + 4];
                var stencilIndex  = _batches[batchOffset + 5];
                batchClip.left    = _batches[batchOffset + 6];
                batchClip.top     = _batches[batchOffset + 7];
                batchClip.right   = _batches[batchOffset + 8];
                batchClip.bottom  = _batches[batchOffset + 9];

                var texture = textures[textureId];

                //context.setProgram(colorProgram);
                context.setProgram(textureProgram);
                context.setTextureAt(0, texture);

                var premultiplied = true;
                if (!premultiplied) {
                    switch (blendMode) {
                        case HaxeLimeRenderImpl.BLEND_NONE    :context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ZERO);
                        case HaxeLimeRenderImpl.BLEND_NORMAL  :context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_ADD     :context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE);
                        case HaxeLimeRenderImpl.BLEND_MULTIPLY:context.setBlendFactors(Context3DBlendFactor.DESTINATION_COLOR, Context3DBlendFactor.ONE_MINUS_SOURCE_COLOR);
                        case HaxeLimeRenderImpl.BLEND_SCREEN  :context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE);
                        case HaxeLimeRenderImpl.BLEND_ERASE   :context.setBlendFactors(Context3DBlendFactor.ZERO, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_BELOW   :context.setBlendFactors(Context3DBlendFactor.ONE_MINUS_DESTINATION_ALPHA, Context3DBlendFactor.DESTINATION_ALPHA);
                        default:context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                    }
                } else {
                    switch (blendMode) {
                        case HaxeLimeRenderImpl.BLEND_NONE    : context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ZERO);
                        case HaxeLimeRenderImpl.BLEND_NORMAL  : context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_ADD     : context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE);
                        case HaxeLimeRenderImpl.BLEND_MULTIPLY: context.setBlendFactors(Context3DBlendFactor.DESTINATION_COLOR, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_SCREEN  : context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE_MINUS_SOURCE_COLOR);
                        case HaxeLimeRenderImpl.BLEND_ERASE   : context.setBlendFactors(Context3DBlendFactor.ZERO, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_BELOW   : context.setBlendFactors(Context3DBlendFactor.ONE_MINUS_DESTINATION_ALPHA, Context3DBlendFactor.DESTINATION_ALPHA);
                        default: context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                    }
                }

                // Scissors
				if (!lastClip.equals(batchClip)) {
					lastClip.copyFrom(batchClip);
					CLIP_RECT.setTo(lastClip.x * virtualScaleX, lastClip.y * virtualScaleY, lastClip.width * virtualScaleX, lastClip.height * virtualScaleY);
					context.setScissorRectangle(CLIP_RECT);
				}

				// Mask/Stencil
				if ((lastMaskType != maskType) || (lastStencilIndex != stencilIndex)) {
					lastMaskType = maskType;
					lastStencilIndex = stencilIndex;
					switch (maskType) {
						case HaxeLimeRenderImpl.MASK_NONE:
							context.setColorMask(true, true, true, true);
							context.setStencilActions(
								Context3DTriangleFace.FRONT_AND_BACK, Context3DCompareMode.EQUAL,
								Context3DStencilAction.KEEP, Context3DStencilAction.KEEP, Context3DStencilAction.KEEP
							);
							context.setStencilReferenceValue(0, 0, 0);
						case HaxeLimeRenderImpl.MASK_SHAPE:
							context.setColorMask(false, false, false, false);
							context.setStencilActions(
								Context3DTriangleFace.FRONT_AND_BACK, Context3DCompareMode.ALWAYS,
								Context3DStencilAction.SET, Context3DStencilAction.SET, Context3DStencilAction.SET
							);
							context.setStencilReferenceValue(stencilIndex, 0xFF, 0xFF);
						case HaxeLimeRenderImpl.MASK_CONTENT:
							context.setColorMask(true, true, true, true);
							context.setStencilReferenceValue(stencilIndex, 0xFF, 0x00);
							context.setStencilActions(
								Context3DTriangleFace.FRONT_AND_BACK, Context3DCompareMode.EQUAL,
								Context3DStencilAction.KEEP, Context3DStencilAction.KEEP, Context3DStencilAction.KEEP
							);
						default:
					}
				}

                //trace(batch.indexStart + ' - ' + batch.count);
                context.drawTriangles(indexBuffer, indexStart, triangleCount);
            }

            indexBuffer.dispose();
            vertexBuffer.dispose();
        }

        context.present();
    }
}

typedef TextureInfo = {
	var vertex: Array<String>;
	var fragment: Array<String>;
	var options: Array<Bool>;
};

