import lime.app.Application;
import lime.graphics.Renderer;
import flash.display.*;
import flash.display3D.*;
import flash.display3D.textures.*;
import flash.utils.*;
import flash.geom.Matrix3D;
import flash.Lib;
import haxe.ds.Vector;
import lime.Assets;

class HaxeLimeRenderFlash extends HaxeLimeRenderImpl {
    private Stage stage;
    private Sprite rootSprite;
    private Program3D colorProgram;
    private Program3D textureProgram;
    private Context3D context;
    private RectangleTexture[] textures;
    private int[] textureIndices;
    static public final boolean DEBUG = true;

    public HaxeLimeRenderFlash(Sprite rootSprite) {
        this.rootSprite = rootSprite;
        this.stage = rootSprite.stage;

        textureIndices = [for (i in 1...1024) i];
        textures = [for (i in 0...1024) null];

        initializeFlash();
    }

    private void initializeFlash() {
        int W = stage.stageWidth;
        int H = stage.stageHeight;
        function __init(e) {
                stage.stage3Ds[0].removeEventListener(flash.events.Event.CONTEXT3D_CREATE, __init);
        trace('contex3d created');
        context = stage.stage3Ds[0].context3D;
        context.enableErrorChecking = DEBUG;
        context.configureBackBuffer(W, H, 4, true);
        //context.configureBackBuffer(640, 480, 4, true);

        var assembler = new AGALMiniAssembler();

        colorProgram = context.createProgram();
        colorProgram.upload(
                assembler.assemble(Context3DProgramType.VERTEX, [
                        "m44 op, va0, vc0",
                "mov v0, va1"
                //"mov v0, va2"
        ].join("\n")),
        assembler.assemble(Context3DProgramType.FRAGMENT, [
                "mov oc, v0"
                ].join("\n"))
        );

        textureProgram = context.createProgram();
        textureProgram.upload(
                assembler.assemble(Context3DProgramType.VERTEX, [
                        "m44 op, va0, vc0",
                "mov v0, va1"
        ].join("\n")),
        assembler.assemble(Context3DProgramType.FRAGMENT, [
                "tex ft0, v0.xyxx, fs0 <2d, linear, mipdisable, clamp>",
                "mov oc, ft0"
        ].join("\n"))
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

    static private double[] _map3dRaw = new double[16];
    static public Matrix3D createOrtho(double left, double right, double bottom, double top, double near = -1, double far = 1, Matrix3D target = null) {
        double[] v = _map3dRaw;
        if (target == null) target = new Matrix3D();
        double a = 2.0 / (right - left);
        double b = 2.0 / (top - bottom);
        double c = -2.0 / (far - near);

        double tx = -(right + left) / (right - left);
        double ty = -(top + bottom) / (top - bottom);
        double tz = -(far + near) / (far - near);

        v[0] = a; v[1] = 0; v[2] = 0; v[3] = tx;
        v[4] = 0; v[5] = b; v[6] = 0; v[7] = ty;
        v[8] = 0; v[9] = 0; v[10] = c; v[11] = tz;
        v[12] = 0; v[13] = 0; v[14] = 0; v[15] = 1;

        target.copyRawDataFrom(v.toData());
        return target;
    }

    @Override
    public boolean isInitialized() {
        return context != null;
    }

    @Override
    public int createTexture(String path, int width, int height) {
        trace('HaxeLimeRenderFlash.createTexture($path)');
        var image = Assets.getImage(path);
        var bitmapData = image.src;
        var id = textureIndices.pop();
        var texture = context.createRectangleTexture(
                width, height,
                Context3DTextureFormat.BGRA,
                false
        );
        textures[id] = texture;
        texture.uploadFromBitmapData(bitmapData);
        return id;
    }

    @Override
    public void disposeTexture(int id) {
        trace('HaxeLimeRenderFlash.disposeTexture(id)');
        textures[id].dispose();
        textures[id] = null;
        textureIndices.push(id);
    }

    private Matrix3D _projectionMatrix = new Matrix3D();

    @Override
    public void render(
            int width, int height,
            float[] _vertices, int vertexCount,
            char[] _indices, int indexCount,
            int[] _batches, int batchCount
    ) {
        Context3D context = this.context;

        if (context == null) {
            trace('context == null');
            return;
        }
        context.clear(0.2, 0.2, 0.2, 1);

        if (batchCount > 0) {
            int virtualWidth = this.stage.stageWidth;
            int virtualHeight = this.stage.stageHeight;

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

            createOrtho(0, virtualWidth, virtualHeight, 0, -1, 1, _projectionMatrix);
            context.setProgramConstantsFromMatrix(Context3DProgramType.VERTEX, 0, _projectionMatrix);
            context.setProgramConstantsFromVector(Context3DProgramType.VERTEX, 4, VERTEX_CONSTANTS);
            context.setProgramConstantsFromVector(Context3DProgramType.FRAGMENT, 0, FRAGMENT_CONSTANTS);

            context.setColorMask(true, true, true, true);
            context.setStencilReferenceValue(0, 0, 0);
            context.setDepthTest(false, Context3DCompareMode.ALWAYS);

            //context.setSamplerStateAt(0, Context3DWrapMode.CLAMP, Context3DTextureFilter.LINEAR, Context3DMipFilter.MIPNONE);

            for (batchId in 0 ... batchCount) {
                var batchOffset = batchId * 16;
                var indexStart    = _batches[batchOffset + 0];
                var triangleCount = _batches[batchOffset + 1];
                var textureId     = _batches[batchOffset + 2];
                var blendMode     = _batches[batchOffset + 3];
                var maskType      = _batches[batchOffset + 4];
                var stencilIndex  = _batches[batchOffset + 5];
                var scissorLeft   = _batches[batchOffset + 6];
                var scissorTop    = _batches[batchOffset + 7];
                var scissorRight  = _batches[batchOffset + 8];
                var scissorBottom = _batches[batchOffset + 9];

                var texture = textures[textureId];

                //context.setProgram(colorProgram);
                context.setProgram(textureProgram);
                context.setTextureAt(0, texture);

                boolean premultiplied = false;
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

                //trace(batch.indexStart + ' - ' + batch.count);
                context.drawTriangles(indexBuffer, indexStart, triangleCount);
            }

            indexBuffer.dispose();
            vertexBuffer.dispose();
        }

        context.present();
    }


}
