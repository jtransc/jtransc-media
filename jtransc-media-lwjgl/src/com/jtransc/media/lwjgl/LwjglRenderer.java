package com.jtransc.media.lwjgl;

import com.jtransc.FastMemory;
import com.jtransc.ds.IntStack;
import com.jtransc.media.JTranscRender;
import com.jtransc.media.JTranscWindow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

class LwjglRenderer implements JTranscRender.Impl {
	IntStack textureIds = new IntStack(2048);
	Texture[] textures = new Texture[2048];

	private long window;

	public LwjglRenderer(long window) {
		this.window = window;
		for (int n = 2047; n >= 0; n--) textureIds.push(n);
		System.out.println("com.jtransc.media.lwjgl.LwjglRenderer()");
		int blankTextureId = createTextureMemory(new int[]{0xFFFFFFFF}, 1, 1, JTranscRender.TYPE_RGBA, false);
		System.out.println("com.jtransc.media.lwjgl.LwjglRenderer() : " + blankTextureId);
	}

	@Override
	public int createTexture(String path, int width, int height, boolean mipmaps) {
		int textureId = textureIds.pop();
		File file = LwjglFiles.getResource(path);
		System.out.println("Loading texture... " + file.getAbsolutePath() + ", exists: " + file.exists());
		//textures[textureId] = new com.badlogic.gdx.graphics.com.jtransc.media.lwjgl.Texture(fileHandle.file().getAbsolutePath());
		textures[textureId] = new Texture(file);
		System.out.println(" ---> " + textureId);
		return textureId;
	}

	@Override
	public int createTextureMemory(int[] data, int width, int height, int format, boolean mipmaps) {
		int textureId = textureIds.pop();
		textures[textureId] = new Texture(data, width, height);
		return textureId;
	}

	@Override
	public int createTextureEncoded(byte[] data, int width, int height, boolean mipmaps) {
		int textureId = textureIds.pop();
		textures[textureId] = new Texture(new ByteArrayInputStream(data));
		return textureId;
	}

	@Override
	public void disposeTexture(int textureId) {
		textures[textureId].dispose();
		textures[textureId] = null;
		textureIds.push(textureId);
	}

	private Rectangle FULL_SCISSORS = new Rectangle(0, 0, 8192, 8192);

	private boolean glEnableDisable(int value, boolean enable) {
		if (enable) {
			glEnable(value);
		} else {
			glDisable(value);
		}
		return enable;
	}

	private ShaderProgram textureProgram;

	private ShaderProgram ShaderProgramAndCheck(String name, String vertex, String fragment) {
		ShaderProgram shader = new ShaderProgram(vertex, fragment);
		if (!shader.isCompiled()) throw new RuntimeException("Shader:$name :: " + shader.getLog());
		return shader;
	}

	private void initShadersOnce() {
		if (textureProgram != null) {
			return;
		}

		final String PREFIX =
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

		textureProgram = ShaderProgramAndCheck(
			"texture",

			PREFIX +
				"uniform mat4 u_matrix;\n" +
				"attribute vec2 a_position;\n" +
				"attribute vec2 a_texcoord;\n" +
				"attribute vec4 a_color;\n" +
				"attribute vec4 a_colorOffset;\n" +
				"varying MED vec2 v_texcoord;\n" +
				"varying MED vec4 v_color;\n" +
				"varying MED vec4 v_colorOffset;\n" +
				"void main() {\n" +
				"gl_Position = u_matrix * vec4(a_position, 0, 1);\n" +
				"v_texcoord = a_texcoord;\n" +
				"v_color = a_color;\n" +
				"v_colorOffset = (a_colorOffset - vec4(0.5, 0.5, 0.5, 0.5)) * 2.0;\n" +
				"}\n",

			PREFIX +
				"uniform sampler2D u_sampler;\n" +
				"varying MED vec4 v_color;\n" +
				"varying MED vec4 v_colorOffset;\n" +
				"varying MED vec2 v_texcoord;\n" +
				"void main() {\n" +
				"gl_FragColor = texture2D(u_sampler, v_texcoord.st);\n" +
				"if (gl_FragColor.a <= 0.0) discard;\n" +
				"//gl_FragColor.rgb /= gl_FragColor.a;//// alpha premultiplied is disable, we will study more in the future\n" +
				"gl_FragColor *= v_color;\n" +
				"gl_FragColor += v_colorOffset;\n" +
				//"gl_FragColor.r = 1f;\n" +
				//"gl_FragColor.a = 1f;\n" +
				"// gl_FragColor.rgb *= gl_FragColor.a;//// alpha premultiplied is disable, we will study more in the future\n" +
				"if (gl_FragColor.a <= 0.0) discard;\n" +
				"}\n"
		);
	}

	Matrix4 projection = new Matrix4();

	Rectangle lastClip = new Rectangle();
	Rectangle currentScissors = new Rectangle();

	private int _getWidthHeight(int index) {
		glfwGetWindowSize(window, LwglTemps.intBuffer(0), LwglTemps.intBuffer(1));
		return LwglTemps.intValue(index);
	}

	private int getWidth() {
		return _getWidthHeight(0);
	}

	private int getHeight() {
		return _getWidthHeight(1);
	}

	@Override
	public void render(FastMemory vertices, int vertexCount, short[] indices, int indexCount, int[] batches, int batchCount) {
		glDisable(GL_STENCIL_TEST);
		glDisable(GL_SCISSOR_TEST);
		glClearColor(0.4f, 0.4f, 0.4f, 1f);
		glClearStencil(0);
		glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

		glEnable(GL_BLEND);

		//if (true) return;

		//System.out.println("indexCount:" + indexCount + ", vertexCount: " + vertexCount);

		if (indexCount == 0 || vertexCount == 0) {
			return;
		}

		Mesh mesh = new Mesh(
			Mesh.VertexDataType.VertexArray, false, vertexCount, indexCount,
			new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"),
			new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texcoord"),
			new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"),
			new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_colorOffset")
		);

		double screenWidth = getWidth();
		double screenHeight = getHeight();
		double virtualActualWidth = JTranscWindow.getVirtualActualWidth();
		double virtualActualHeight = JTranscWindow.getVirtualActualHeight();
		double virtualScaleX = JTranscWindow.getVirtualScaleX();
		double virtualScaleY = JTranscWindow.getVirtualScaleY();

		projection.setToOrtho(0f, (float) virtualActualWidth, (float) virtualActualHeight, 0f, 0f, 1f);

		initShadersOnce();

		mesh.setIndices(indices, 0, indexCount);
		float[] vertexData = new float[vertexCount * 6];
		//System.out.println("--------");
		for (int n = 0; n < vertexData.length; n++) vertexData[n] = vertices.getAlignedFloat32(n);
		mesh.setVertices(vertexData, 0, vertexData.length);

		//com.jtransc.media.lwjgl.Rectangle lastClip = FULL_SCISSORS.clone();
		ShaderProgram program = this.textureProgram;

		//program.end();
		//program = createOrGetProgram(currentProgram);
		mesh.bind(program);
		program.begin();
		program.setUniformMatrix("u_matrix", projection);

		lastClip.set(0, 0, 8196, 8196);

		int lastMaskType = JTranscRender.MASK_NONE;
		int lastStencilIndex = -1;

		for (int batchId = 0; batchId < batchCount; batchId++) {
			int batchOffset = batchId * 16;
			int indexStart = batches[batchOffset + 0];
			int triangleCount = batches[batchOffset + 1];
			int textureId = batches[batchOffset + 2];
			int blendMode = batches[batchOffset + 3];
			int currentMaskType = batches[batchOffset + 4];
			int currentStencilIndex = batches[batchOffset + 5];
			int scissorLeft = batches[batchOffset + 6];
			int scissorTop = batches[batchOffset + 7];
			int scissorRight = batches[batchOffset + 8];
			int scissorBottom = batches[batchOffset + 9];

			currentScissors.set(scissorLeft, scissorTop, scissorRight - scissorLeft, scissorBottom - scissorTop);

			Texture glTexture = textures[textureId];

			if (glTexture == null) {
				glBindTexture(GL_TEXTURE_2D, 0);
			} else {
				glTexture.bind(0);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			}

			switch (blendMode) {
				default:
				case JTranscRender.BLEND_NORMAL:
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					//glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

					break;
				case JTranscRender.BLEND_ADD:
					glBlendFunc(GL_SRC_ALPHA, GL_ONE);
					//glBlendFunc(GL_ONE, GL_ONE);
					break;
			}

			if (!lastClip.equals(currentScissors)) {
				lastClip.set(currentScissors);
				//if (debugBatch) batchReasons.push(PrenderBatchReason.CLIP)

				if (glEnableDisable(GL_SCISSOR_TEST, lastClip.equals(FULL_SCISSORS))) {
					glScissor(
						(int) (lastClip.x * virtualScaleX),
						(int) (screenHeight - (lastClip.y + lastClip.height) * virtualScaleY),
						(int) (lastClip.width * virtualScaleX),
						(int) (lastClip.height * virtualScaleY)
					);
				}
			}

			if ((lastMaskType != currentMaskType) || (lastStencilIndex != currentStencilIndex)) {
				lastMaskType = currentMaskType;
				lastStencilIndex = currentStencilIndex;
				switch (currentMaskType) {
					case JTranscRender.MASK_NONE:
						glDisable(GL_STENCIL_TEST);
						glDepthMask(false);
						glColorMask(true, true, true, true);
						glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
						glStencilFunc(GL_EQUAL, 0x00, 0x00);
						glStencilMask(0x00);
						break;
					case JTranscRender.MASK_SHAPE:
						glEnable(GL_STENCIL_TEST);
						glDepthMask(true);
						glColorMask(false, false, false, false);
						glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
						glStencilFunc(GL_ALWAYS, currentStencilIndex, 0xFF);
						glStencilMask(0xFF); // write ref
						break;
					case JTranscRender.MASK_CONTENT:
						glEnable(GL_STENCIL_TEST);
						glDepthMask(true);
						glColorMask(true, true, true, true);
						glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
						glStencilFunc(GL_EQUAL, currentStencilIndex, 0xFF);
						glStencilMask(0x00);
						break;
					default:
						//if (debugBatch) batchReasons.push("mask unknown")
						break;
				}
			}

			//trace('batch:' + indexStart + ',' + triangleCount);

			//gl.glDrawElements(GL20.GL_TRIANGLES, triangleCount * 3, GL20.GL_UNSIGNED_SHORT, indexStart * 2);
			mesh.render(program, GL_TRIANGLES, indexStart, triangleCount * 3, false);
		}
		program.end();

		mesh.unbind(program);

		mesh.dispose();

		glfwSwapBuffers(window);
	}
}

class Rectangle {
	public float x;
	public float y;
	public float width;
	public float height;

	public Rectangle() {
	}

	public Rectangle(float x, float y, float width, float height) {
		set(x, y, width, height);
	}

	public Rectangle set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}

	public Rectangle set(Rectangle that) {
		return set(that.x, that.y, that.width, that.height);
	}
}

class Texture {
	private int id;
	private File file;
	private InputStream is;
	private int[] data;
	private int width;
	private int height;

	private Texture() {
		id = glGenTextures();
	}

	public Texture(File file) {
		this();
		this.file = file;
		load();
	}

	public Texture(int[] data, int width, int height) {
		this();
		this.data = data;
		this.width = width;
		this.height = height;
		load();
	}

	public Texture(InputStream is) {
		this();
		this.is = is;
		load();
		//ImageIO.read(is);
	}

	public void bind(int textureUnit) {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	static public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void load() {
		try {
			int width = this.width;
			int height = this.height;
			int[] dataArray = this.data;

			if (file != null || is != null) {
				BufferedImage image = (file != null) ? ImageIO.read(file) : ImageIO.read(is);
				width = image.getWidth();
				height = image.getHeight();
				dataArray = new int[width * height];
				image.getRGB(0, 0, width, height, dataArray, 0, width);
				/*
				int n = 0;

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int value = 0;
						dataArray[n] = value;
						n++;
					}
				}
				*/
			}

			IntBuffer data = IntBuffer.allocate(dataArray.length);
			for (int n = 0; n < dataArray.length; n++) data.put(n, dataArray[n]);

			bind(0);
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void dispose() {
		glDeleteTextures(id);
		id = -1;
	}
}

class ShaderProgram {
	private boolean compiled;
	private String log;
	private String vertex, fragment;
	private int programId, vertexId, fragmentId;

	public ShaderProgram(String vertex, String fragment) {
		this.vertex = vertex;
		this.fragment = fragment;
		//throw new RuntimeException("Not implemented");
		this.compiled = true;
		this.programId = glCreateProgram();
		this.vertexId = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexId, vertex);
		glCompileShader(vertexId);

		this.fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentId, fragment);
		glCompileShader(fragmentId);

		glAttachShader(programId, vertexId);
		glAttachShader(programId, fragmentId);
		glLinkProgram(programId);
	}

	public boolean isCompiled() {
		return compiled;
	}

	public String getLog() {
		return log;
	}

	public void end() {
		glUseProgram(0);

	}

	public void begin() {
		glUseProgram(programId);
	}

	public void setUniformMatrix(String name, Matrix4 matrix) {
		int location = glGetUniformLocation(programId, name);
		matrix.data.clear();
		glUniformMatrix4fv(location, false, matrix.data);
	}

	public void disableVertexAttribute(String name) {
		disableVertexAttribute(getAttributeLocation(name));
	}

	public void disableVertexAttribute(int location) {
		glDisableVertexAttribArray(location);
	}

	public int getAttributeLocation(String name) {
		return glGetAttribLocation(programId, name);
	}

	public void enableVertexAttribute(int location) {
		glEnableVertexAttribArray(location);
	}

	public void setVertexAttribute(int location, int numComponents, int type, boolean normalized, int vertexSize, ByteBuffer buffer) {
		glVertexAttribPointer(location, numComponents, type, normalized, vertexSize, buffer);
	}
}

class Matrix4 {
	public FloatBuffer data = ByteBuffer.allocateDirect(16 * 4).asFloatBuffer();

	static private final int getIndex(int x, int y) { return x * 4 + y; }
	//static private final int getIndex(int x, int y) { return y * 4 + x; }

	static private final int M00 = getIndex(0, 0);
	static private final int M10 = getIndex(1, 0);
	static private final int M20 = getIndex(2, 0);
	static private final int M30 = getIndex(3, 0);

	static private final int M01 = getIndex(0, 1);
	static private final int M11 = getIndex(1, 1);
	static private final int M21 = getIndex(2, 1);
	static private final int M31 = getIndex(3, 1);

	static private final int M02 = getIndex(0, 2);
	static private final int M12 = getIndex(1, 2);
	static private final int M22 = getIndex(2, 2);
	static private final int M32 = getIndex(3, 2);

	static private final int M03 = getIndex(0, 3);
	static private final int M13 = getIndex(1, 3);
	static private final int M23 = getIndex(2, 3);
	static private final int M33 = getIndex(3, 3);

	public void set(int index, float value) {
		//if (index < 0 || index >= 16) throw new IndexOutOfBoundsException();
		this.data.put(index, value);
	}

	public void set(int x, int y, float value) {
		this.data.put(getIndex(x, y), value);
	}

	public Matrix4 set(float M00, float M10, float M20, float M30, float M01, float M11, float M21, float M31, float M02, float M12, float M22, float M32, float M03, float M13, float M23, float M33) {
		this.set(0, M00);
		this.set(1, M10);
		this.set(2, M20);
		this.set(3, M30);

		this.set(4, M01);
		this.set(5, M11);
		this.set(6, M21);
		this.set(7, M31);

		this.set(8, M02);
		this.set(9, M12);
		this.set(10, M22);
		this.set(11, M32);

		this.set(12, M03);
		this.set(13, M13);
		this.set(14, M23);
		this.set(15, M33);

		return this;
	}

	public Matrix4 idt() {
		return set(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		);
	}

	public Matrix4 setToOrtho(float left, float right, float bottom, float top, float near, float far) {
		this.idt();
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);
		float z_orth = -2 / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		set(M00, x_orth);
		set(M10, 0);
		set(M20, 0);
		set(M30, 0);
		set(M01, 0);
		set(M11, y_orth);
		set(M21, 0);
		set(M31, 0);
		set(M02, 0);
		set(M12, 0);
		set(M22, z_orth);
		set(M32, 0);
		set(M03, tx);
		set(M13, ty);
		set(M23, tz);
		set(M33, 1);

		return this;
	}
}

class VertexAttribute {
	public int index;
	public int numComponents;
	public int type;
	public boolean normalized;
	public int offset;
	public String alias;
	public int byteSize;
	public VertexAttributes.Usage usage;
	public int count;

	public VertexAttribute(VertexAttributes.Usage usage, int count, String name) {
		this.usage = usage;
		this.byteSize = usage.byteSize * count;
	}
}

class VertexAttributes {
	VertexAttribute[] attributes;
	public int vertexSize;

	public VertexAttributes(VertexAttribute[] attributes) {
		this.attributes = attributes;
		int offset = 0;
		for (int n = 0; n < attributes.length; n++) {
			VertexAttribute a = attributes[n];
			a.index = n;
			a.offset = offset;
			offset += a.byteSize;
		}
		this.vertexSize = offset;
	}

	/*
	public void bind(com.jtransc.media.lwjgl.ShaderProgram program, Buffer buffer) {
		for (com.jtransc.media.lwjgl.VertexAttribute attribute : attributes) {
			program.enableVertexAttribute(attribute.index);
			program.setVertexAttribute(attribute.index, attribute.numComponents, attribute.type, attribute.normalized, vertexSize, buffer);
		}
	}

	public void unbind(com.jtransc.media.lwjgl.ShaderProgram program) {
		for (com.jtransc.media.lwjgl.VertexAttribute attribute : attributes) {
			program.disableVertexAttribute(attribute.index);
		}
	}
	*/

	public enum Usage {
		TextureCoordinates(GL_FLOAT, 4),
		ColorPacked(GL_BYTE, 1),
		Position(GL_FLOAT, 4);

		public int type;
		public int byteSize;

		Usage(int type, int byteSize) {
			this.type = type;
			this.byteSize = byteSize;
		}
	}

	public VertexAttribute get(int index) {
		return attributes[index];
	}

	public int size() {
		return attributes.length;
	}
}

class Mesh {
	private final VertexArray vertices;
	private final IndexArray indices;

	public Mesh(VertexDataType vertexArray, boolean b, int vertexCount, int indexCount, VertexAttribute... attributes) {
		vertices = new VertexArray(vertexCount, attributes);
		indices = new IndexArray(indexCount);
		//isVertexArray = true;
	}

	public void render(ShaderProgram program, int primitiveType, int offset, int count, boolean b) {
		program.begin();
		//glDrawElements(mode, vertexCount);

		vertices.bind(program);

		ByteBuffer indexBuffer = indices.getByteBuffer();
		//int oldPosition = indexBuffer.position();
		//int oldLimit = indexBuffer.limit();
		indexBuffer.position(offset * 2);
		indexBuffer.limit(offset * 2 + count * 2);

		glDrawElements(primitiveType, count, GL_UNSIGNED_SHORT, indexBuffer);

		vertices.unbind(program);

		program.end();
	}

	public void bind(ShaderProgram program) {
		program.begin();
	}


	public void unbind(ShaderProgram program) {
		program.end();
	}

	public void dispose() {
		vertices.dispose();
		indices.dispose();
	}

	public void setIndices(short[] indices, int offset, int length) {
		this.indices.setIndices(indices, offset, length);
	}

	public void setVertices(float[] vertexData, int offset, int length) {
		this.vertices.setVertices(vertexData, offset, length);
	}

	public enum VertexDataType {
		VertexArray
	}
}


class VertexArray {
	final VertexAttributes attributes;
	final FloatBuffer buffer;
	final ByteBuffer byteBuffer;
	boolean isBound = false;

	public VertexArray(int numVertices, VertexAttribute... attributes) {
		this(numVertices, new VertexAttributes(attributes));
	}

	public VertexArray(int numVertices, VertexAttributes attributes) {
		this.attributes = attributes;
		byteBuffer = ByteBuffer.allocateDirect(this.attributes.vertexSize * numVertices);
		buffer = byteBuffer.asFloatBuffer();
		buffer.flip();
		byteBuffer.flip();
	}

	public void dispose() {
		//BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
	}

	public FloatBuffer getBuffer() {
		return buffer;
	}

	public void setVertices(float[] vertices, int offset, int count) {
		byteBuffer.clear();
		buffer.clear();
		buffer.put(vertices, offset, count);
		buffer.position(0);
		buffer.limit(count);
		byteBuffer.position(0);
		byteBuffer.limit(count * 4);
	}

	public void bind(final ShaderProgram shader) {
		final int numAttributes = attributes.size();
		byteBuffer.limit(buffer.limit() * 4);

		for (int i = 0; i < numAttributes; i++) {
			final VertexAttribute attribute = attributes.get(i);
			final int location = shader.getAttributeLocation(attribute.alias);
			if (location < 0) continue;
			shader.enableVertexAttribute(location);

			byteBuffer.position(attribute.offset);
			shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, attributes.vertexSize, byteBuffer);
		}

		isBound = true;
	}

	public void unbind(ShaderProgram shader) {
		final int numAttributes = attributes.size();
		for (int i = 0; i < numAttributes; i++) {
			shader.disableVertexAttribute(attributes.get(i).alias);
		}
		isBound = false;
	}

	public VertexAttributes getAttributes() {
		return attributes;
	}
}

class IndexArray {
	ShortBuffer buffer;
	ByteBuffer byteBuffer;

	public IndexArray(int maxIndices) {
		byteBuffer = ByteBuffer.allocateDirect(maxIndices * 2);
		buffer = byteBuffer.asShortBuffer();
		buffer.flip();
		byteBuffer.flip();
	}

	public void setIndices(short[] indices, int offset, int count) {
		buffer.clear();
		buffer.put(indices, offset, count);
		buffer.flip();
		byteBuffer.position(0);
		byteBuffer.limit(count * 2);
	}

	public ShortBuffer getBuffer() {
		return buffer;
	}

	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	public void dispose() {

	}
}
