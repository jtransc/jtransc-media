import flash.display.BitmapData;
import flash.display.Sprite;
import flash.display.Stage;
import flash.display3D.*;
import flash.display3D.textures.RectangleTexture;
import flash.events.Event;
import flash.geom.Matrix3D;
import haxe.ds.Vector;
import jtransc.IntStack;
import lime.Assets;
import lime.graphics.Image;

class HaxeLimeRenderFlash extends HaxeLimeRenderImpl {
    private Stage stage;
    private Sprite rootSprite;
    private Program3D colorProgram;
    private Program3D textureProgram;
    private Context3D context;
    private RectangleTexture[] textures;
    private IntStack textureIndices;
    static public final boolean DEBUG = true;

    public HaxeLimeRenderFlash(Sprite rootSprite) {
        this.rootSprite = rootSprite;
        this.stage = rootSprite.stage;

        //textureIndices =[for (i in 1.. .1024)i];
        //textures =[for (i in 0.. .1024)null];

        initializeFlash();
    }

    private void initializeFlash__init(Event e) {
        stage.stage3Ds[0].removeEventListener(flash.events.Event.CONTEXT3D_CREATE, __init);
        System.out.println("contex3d created");
        context = stage.stage3Ds[0].context3D;
        context.enableErrorChecking = DEBUG;
        context.configureBackBuffer(W, H, 4, true);
        //context.configureBackBuffer(640, 480, 4, true);

        AGALMiniAssembler assembler = new AGALMiniAssembler();

        colorProgram = context.createProgram();
        colorProgram.upload(
                assembler.assemble(Context3DProgramType.VERTEX,
                        "m44 op, va0, vc0\n" + "mov v0, va1"
                        //"mov v0, va2"
                ),

                assembler.assemble(Context3DProgramType.FRAGMENT, "mov oc, v0")
        );

        textureProgram = context.createProgram();
        textureProgram.upload(
                assembler.assemble(
                        Context3DProgramType.VERTEX,
                        "m44 op, va0, vc0\n" +
                                "mov v0, va1"
                ),
                assembler.assemble(Context3DProgramType.FRAGMENT,
                        "tex ft0, v0.xyxx, fs0 <2d, linear, mipdisable, clamp>\n" +
                                "mov oc, ft0"
                )
        );
    }

    private void initializeFlash__resize(Event e) {
        if (context == null) return;
        context.configureBackBuffer(stage.stageWidth, stage.stageHeight, 4, true);
        //render(renderer);
    }

    private int W;
    private int H;

    private void initializeFlash() {
        W = stage.stageWidth;
        H = stage.stageHeight;
        stage.stage3Ds[0].addEventListener(flash.events.Event.CONTEXT3D_CREATE, initializeFlash__init);
        stage.stage3Ds[0].requestContext3D();
        stage.addEventListener(flash.events.Event.RESIZE, initializeFlash__resize);
    }

    private static double[] FRAGMENT_CONSTANTS = new double[]{
            -1,  // fc0.x
            0,   // fc0.y
            1,   // fc0.z
            2,   // fc0.w
            0.5, // fc1.x
            128, // fc1.y
            255, // fc1.z
            512  // fc1.w
    };

    private static double[] VERTEX_CONSTANTS = new double[]{
            -1,  // vc4.x
            0,   // vc4.y
            1,   // vc4.z
            2,   // vc4.w
            0.5, // vc5.x
            128, // vc5.y
            255, // vc5.z
            512  // vc5.w
    };

    static private double[] _map3dRaw = new double[16];

    static public Matrix3D createOrtho(double left, double right, double bottom, double top) {
        return createOrtho(left, right, bottom, top, -1, 1, null);
    }

    static public Matrix3D createOrtho(double left, double right, double bottom, double top, double near, double far, Matrix3D target) {
        double[] v = _map3dRaw;
        if (target == null) target = new Matrix3D();
        double a = 2.0 / (right - left);
        double b = 2.0 / (top - bottom);
        double c = -2.0 / (far - near);

        double tx = -(right + left) / (right - left);
        double ty = -(top + bottom) / (top - bottom);
        double tz = -(far + near) / (far - near);

        v[0] = a;
        v[1] = 0;
        v[2] = 0;
        v[3] = tx;
        v[4] = 0;
        v[5] = b;
        v[6] = 0;
        v[7] = ty;
        v[8] = 0;
        v[9] = 0;
        v[10] = c;
        v[11] = tz;
        v[12] = 0;
        v[13] = 0;
        v[14] = 0;
        v[15] = 1;

        target.copyRawDataFrom(v.toData());
        return target;
    }

    @Override
    public boolean isInitialized() {
        return context != null;
    }

    @Override
    public int createTexture(String path, int width, int height) {
        System.out.println("HaxeLimeRenderFlash.createTexture(" + path + ")");
        Image image = Assets.getImage_s(path, true);
        BitmapData bitmapData = (BitmapData) image.src;
        int id = textureIndices.pop();
        RectangleTexture texture = context.createRectangleTexture(
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
        System.out.println("HaxeLimeRenderFlash.disposeTexture(id)");
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
            System.out.println("context == null");
            return;
        }
        context.clear(0.2, 0.2, 0.2, 1);

        if (batchCount > 0) {
            int virtualWidth = this.stage.stageWidth;
            int virtualHeight = this.stage.stageHeight;

            //float[] verticesOut = new Vector<Float>(vertexCount * 6);
            //int[] indicesOut = new Vector<UInt>(indexCount);

            float[] verticesOut = new float[vertexCount * 6];
            int[] indicesOut = new int[indexCount];

            for (int n = 0; n < verticesOut.length; n++) verticesOut[n] = _vertices[n];
            for (int n = 0; n < indicesOut.length; n++) indicesOut[n] = _indices[n];

            VertexBuffer3D vertexBuffer = context.createVertexBuffer(verticesOut.length / 6, 6);
            IndexBuffer3D indexBuffer = context.createIndexBuffer(indicesOut.length);

            vertexBuffer.uploadFromVector(verticesOut.toData(), 0, verticesOut.length / 6);
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

            for (int batchId = 0; batchId < batchCount; batchId++) {
                int batchOffset = batchId * 16;
                int indexStart = _batches[batchOffset + 0];
                int triangleCount = _batches[batchOffset + 1];
                int textureId = _batches[batchOffset + 2];
                int blendMode = _batches[batchOffset + 3];
                int maskType = _batches[batchOffset + 4];
                int stencilIndex = _batches[batchOffset + 5];
                int scissorLeft = _batches[batchOffset + 6];
                int scissorTop = _batches[batchOffset + 7];
                int scissorRight = _batches[batchOffset + 8];
                int scissorBottom = _batches[batchOffset + 9];

                RectangleTexture texture = textures[textureId];

                //context.setProgram(colorProgram);
                context.setProgram(textureProgram);
                context.setTextureAt(0, texture);

                boolean premultiplied = false;
                if (!premultiplied) {
                    switch (blendMode) {
                        case HaxeLimeRenderImpl.BLEND_NONE:
                            context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ZERO);
                        case HaxeLimeRenderImpl.BLEND_NORMAL:
                            context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_ADD:
                            context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE);
                        case HaxeLimeRenderImpl.BLEND_MULTIPLY:
                            context.setBlendFactors(Context3DBlendFactor.DESTINATION_COLOR, Context3DBlendFactor.ONE_MINUS_SOURCE_COLOR);
                        case HaxeLimeRenderImpl.BLEND_SCREEN:
                            context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE);
                        case HaxeLimeRenderImpl.BLEND_ERASE:
                            context.setBlendFactors(Context3DBlendFactor.ZERO, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_BELOW:
                            context.setBlendFactors(Context3DBlendFactor.ONE_MINUS_DESTINATION_ALPHA, Context3DBlendFactor.DESTINATION_ALPHA);
                        default:
                            context.setBlendFactors(Context3DBlendFactor.SOURCE_ALPHA, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                    }
                } else {
                    switch (blendMode) {
                        case HaxeLimeRenderImpl.BLEND_NONE:
                            context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ZERO);
                        case HaxeLimeRenderImpl.BLEND_NORMAL:
                            context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_ADD:
                            context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE);
                        case HaxeLimeRenderImpl.BLEND_MULTIPLY:
                            context.setBlendFactors(Context3DBlendFactor.DESTINATION_COLOR, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_SCREEN:
                            context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE_MINUS_SOURCE_COLOR);
                        case HaxeLimeRenderImpl.BLEND_ERASE:
                            context.setBlendFactors(Context3DBlendFactor.ZERO, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
                        case HaxeLimeRenderImpl.BLEND_BELOW:
                            context.setBlendFactors(Context3DBlendFactor.ONE_MINUS_DESTINATION_ALPHA, Context3DBlendFactor.DESTINATION_ALPHA);
                        default:
                            context.setBlendFactors(Context3DBlendFactor.ONE, Context3DBlendFactor.ONE_MINUS_SOURCE_ALPHA);
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
