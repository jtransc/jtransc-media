import lime.Assets;
import lime.graphics.GLRenderContext;
import lime.graphics.opengl.*;
import lime.math.Matrix4;
import lime.utils.*;

class HaxeLimeRenderGL extends HaxeLimeRenderImpl {
    static public boolean DESKTOP = false;

    public GLRenderContext gl;

    private GLUniformLocation glMatrixUniform;
    private GLUniformLocation glImageUniform;
    private GLProgram glProgram;
    private GLTexture glTexture;
    private int glTextureAttribute;
    private int glVertexAttribute;
    private int glColor0Attribute;
    private int glColor1Attribute;
    private GLTexture[] textures;
    private int[] textureIndices;

    private GLBuffer indicesBuffer = null;
    private GLBuffer verticesBuffer = null;

    boolean ENABLE_COLORS = false;

    public HaxeLimeRenderGL(GLRenderContext gl) {
        this.gl = gl;

        textureIndices =[for (i in 1.. .1024)i];
        textures =[for (i in 0.. .1024)null];

        init();
    }

    private void init() {
        String PREFIX =
                "#ifdef GL_ES\n" +
                        "#define LOWP lowp\n" +
                        "#define MED mediump\n" +
                        "#define HIGH highp\n" +
                        "precision mediump float;\n" +
                        "#else\n" +
                        "#define MED\n" +
                        "#define LOWP\n" +
                        "#define HIGH\n" +
                        "#endif\n";

        if (ENABLE_COLORS) {
            PREFIX += "#define A_COLORS 1\n";
        }

        String vertexSource = PREFIX +
                "uniform mat4 u_matrix;\n" +
                "\n" +
                "attribute vec2 a_position;\n" +
                "attribute vec2 a_texcoord;\n" +
                "#ifdef A_COLORS\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec4 a_colorOffset;\n" +
                "#endif\n" +
                "\n" +
                "varying MED vec2 v_texcoord;\n" +
                "#ifdef A_COLORS\n" +
                "varying MED vec4 v_color;\n" +
                "varying MED vec4 v_colorOffset;\n" +
                "#endif\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = u_matrix * vec4(a_position, 0, 1);\n" +
                "    v_texcoord = a_texcoord;\n" +
                "    #ifdef A_COLORS\n" +
                "    v_color = a_color;\n" +
                "    v_colorOffset = (a_colorOffset - vec4(0.5, 0.5, 0.5, 0.5)) * 2.0;\n" +
                "    #endif\n" +
                "}\n";

        String fragmentSource = PREFIX +
                "uniform sampler2D u_sampler;\n" +
                "\n" +
                "#ifdef A_COLORS\n" +
                "varying MED vec4 v_color;\n" +
                "varying MED vec4 v_colorOffset;\n" +
                "#endif\n" +
                "\n" +
                "varying MED vec2 v_texcoord;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(u_sampler, v_texcoord.st);\n" +
                "    if (gl_FragColor.a <= 0.0) discard;\n" +
                "    #ifdef A_COLORS\n" +
                "    gl_FragColor.rgb /= gl_FragColor.a;\n" +
                "    gl_FragColor *= v_color;\n" +
                "    gl_FragColor += v_colorOffset;\n" +
                "    gl_FragColor.rgb *= gl_FragColor.a;\n" +
                "    #endif\n" +
                "    if (gl_FragColor.a <= 0.0) discard;\n" +
                "}\n";

        glProgram = GLUtils.createProgram(vertexSource, fragmentSource);
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

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public int createTexture(String path, int width, int height) {
        trace('HaxeLimeRenderGL.createTexture($path)');
        //path = 'assets/image.png';
        //trace('HaxeLimeRenderGL.createTexture[2]($path)');
        var image = Assets.getImage(path);
        var id = textureIndices.pop();
        var glTexture = textures[id] = gl.createTexture();

        gl.bindTexture(gl.TEXTURE_2D, glTexture);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
        #if js
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, image.src);
        #else
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, image.buffer.width, image.buffer.height, 0, gl.RGBA, gl.UNSIGNED_BYTE, image.data);
        #end
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
        gl.bindTexture(gl.TEXTURE_2D, null);


        return id;
    }

    @Override
    public void disposeTexture(int id) {
        trace('HaxeLimeRenderGL.disposeTexture(id)');
        gl.deleteTexture(textures[id]);
        textures[id] = null;
        textureIndices.push(id);
    }

    @Override
    public void render(
            int width, int height,
            float[] _vertices, int vertexCount,
            char[] _indices, int indexCount,
            int[] _batches, int batchCount
    ) {

        lime.utils.UInt16Array indicesData = lime.utils.UInt16Array.fromBytes(_indices.view.buffer, 0, indexCount);
        lime.utils.Float32Array verticesData = lime.utils.Float32Array.fromBytes(_vertices.view.buffer, 0, vertexCount * 6);

        gl.enable(gl.BLEND);
        gl.viewport(0, 0, width, height);

        gl.clearColor(0.2, 0.2, 0.2, 1.0);
        gl.clear(gl.COLOR_BUFFER_BIT);

        gl.useProgram(glProgram);

        Matrix4 matrix = Matrix4.createOrtho(0, width, height, 0, -1000, 1000);
        gl.uniformMatrix4fv(glMatrixUniform, false, matrix);
        gl.uniform1i(glImageUniform, 0);

        if (indicesBuffer == null) indicesBuffer = gl.createBuffer();
        if (verticesBuffer == null) verticesBuffer = gl.createBuffer();

        gl.bindBuffer(gl.ARRAY_BUFFER, verticesBuffer);
        gl.bufferData(gl.ARRAY_BUFFER, verticesData, gl.STATIC_DRAW);

        gl.enableVertexAttribArray(glVertexAttribute);
        gl.enableVertexAttribArray(glTextureAttribute);
        if (ENABLE_COLORS) {
            gl.enableVertexAttribArray(glColor0Attribute);
            gl.enableVertexAttribArray(glColor1Attribute);
        }

        final int STRIDE = 6 * 4;
        gl.bindBuffer(gl.ARRAY_BUFFER, verticesBuffer);
        gl.vertexAttribPointer(glVertexAttribute, 2, gl.FLOAT, false, STRIDE, 0 * 4);
        gl.vertexAttribPointer(glTextureAttribute, 2, gl.FLOAT, false, STRIDE, 2 * 4);
        if (ENABLE_COLORS) {
            gl.vertexAttribPointer(glColor0Attribute, 4, gl.UNSIGNED_BYTE, false, STRIDE, 4 * 4);
            gl.vertexAttribPointer(glColor1Attribute, 4, gl.UNSIGNED_BYTE, false, STRIDE, 5 * 4);
        }

        gl.enable(gl.BLEND);
        if (DESKTOP) {
            gl.enable(gl.TEXTURE_2D);
        }

        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indicesBuffer);
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, indicesData, gl.STATIC_DRAW);

        for (int batchId = 0; batchId < batchCount; batchId++) {
            final int batchOffset = batchId * 16;
            final int indexStart = _batches[batchOffset + 0];
            final int triangleCount = _batches[batchOffset + 1];
            final int textureId = _batches[batchOffset + 2];
            final int blendMode = _batches[batchOffset + 3];
            final int maskType = _batches[batchOffset + 4];
            final int stencilIndex = _batches[batchOffset + 5];
            final int scissorLeft = _batches[batchOffset + 6];
            final int scissorTop = _batches[batchOffset + 7];
            final int scissorRight = _batches[batchOffset + 8];
            final int scissorBottom = _batches[batchOffset + 9];

            GLTexture glTexture = textures[textureId];

            gl.activeTexture(gl.TEXTURE0);
            gl.bindTexture(gl.TEXTURE_2D, glTexture);

            final boolean premultiplied = false;
            if (!premultiplied) {
                switch (blendMode) {
                    case HaxeLimeRenderImpl.BLEND_NONE:
                        gl.blendFunc(gl.ONE, gl.ZERO);
                    case HaxeLimeRenderImpl.BLEND_NORMAL:
                        gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_ADD:
                        gl.blendFunc(gl.SRC_ALPHA, gl.ONE);
                    case HaxeLimeRenderImpl.BLEND_MULTIPLY:
                        gl.blendFunc(gl.DST_COLOR, gl.ONE_MINUS_SRC_COLOR);
                    case HaxeLimeRenderImpl.BLEND_SCREEN:
                        gl.blendFunc(gl.SRC_ALPHA, gl.ONE);
                    case HaxeLimeRenderImpl.BLEND_ERASE:
                        gl.blendFunc(gl.ZERO, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_BELOW:
                        gl.blendFunc(gl.ONE_MINUS_DST_ALPHA, gl.DST_ALPHA);
                    default:
                        gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
                }
            } else {
                switch (blendMode) {
                    case HaxeLimeRenderImpl.BLEND_NONE:
                        gl.blendFunc(gl.ONE, gl.ZERO);
                    case HaxeLimeRenderImpl.BLEND_NORMAL:
                        gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_ADD:
                        gl.blendFunc(gl.ONE, gl.ONE);
                    case HaxeLimeRenderImpl.BLEND_MULTIPLY:
                        gl.blendFunc(gl.DST_COLOR, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_SCREEN:
                        gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_COLOR);
                    case HaxeLimeRenderImpl.BLEND_ERASE:
                        gl.blendFunc(gl.ZERO, gl.ONE_MINUS_SRC_ALPHA);
                    case HaxeLimeRenderImpl.BLEND_BELOW:
                        gl.blendFunc(gl.ONE_MINUS_DST_ALPHA, gl.DST_ALPHA);
                    default:
                        gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
                }
            }

            //trace('batch:' + indexStart + ',' + triangleCount);

            gl.drawElements(gl.TRIANGLES, triangleCount * 3, gl.UNSIGNED_SHORT, indexStart * 2);
        }

        //gl.deleteBuffer(verticesBuffer);
        //gl.deleteBuffer(indicesBuffer);
    }
}