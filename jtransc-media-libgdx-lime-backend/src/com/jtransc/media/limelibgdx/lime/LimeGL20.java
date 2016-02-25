package com.jtransc.media.limelibgdx.lime;

import com.badlogic.gdx.graphics.GL20;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import lime.graphics.opengl.GL;

public class LimeGL20 implements GL20 {
	@Override
	public void glActiveTexture(int texture) {
		GL.activeTexture(texture);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		GL.bindTexture(target, texture);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		GL.blendFunc(sfactor, dfactor);
	}

	@Override
	public void glClear(int mask) {
		GL.clear(mask);
	}

	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		GL.clearColor(red, green, blue, alpha);
	}

	@Override
	public void glClearDepthf(float depth) {
		GL.clearDepth(depth);
	}

	@Override
	public void glClearStencil(int s) {
		GL.clearStencil(s);
	}

	@Override
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		GL.colorMask(red, green, blue, alpha);
	}

	@Override
	public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
		GL.compressedTexImage2D(target, level, internalformat, width, height, border, data);
	}

	@Override
	public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
		GL.compressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, data);
	}

	@Override
	public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
		GL.copyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	@Override
	public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
		GL.copyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public void glCullFace(int mode) {
		GL.cullFace(mode);
	}

	@Override
	public void glDeleteTextures(int n, IntBuffer textures) {
		for (int i = 0; i < n; i++) GL.deleteTexture(textures.get(i));
	}

	@Override
	public void glDeleteTexture(int texture) {
		GL.deleteTexture(texture);
	}

	@Override
	public void glDepthFunc(int func) {
		GL.depthFunc(func);
	}

	@Override
	public void glDepthMask(boolean flag) {
		GL.depthMask(flag);
	}

	@Override
	public void glDepthRangef(float zNear, float zFar) {
		GL.depthRange(zNear, zFar);
	}

	@Override
	public void glDisable(int cap) {
		GL.disable(cap);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		GL.drawArrays(mode, first, count);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		GL.drawElements(mode, count, type, indices);
	}

	@Override
	public void glEnable(int cap) {
		GL.enable(cap);
	}

	@Override
	public void glFinish() {
		GL.finish();
	}

	@Override
	public void glFlush() {
		GL.flush();
	}

	@Override
	public void glFrontFace(int mode) {
		GL.frontFace(mode);
	}

	@Override
	public void glGenTextures(int n, IntBuffer textures) {
		for (int i = 0; i < n; i++) textures[i] = glGenTexture();
	}

	@Override
	public int glGenTexture() {
		return GL.createTexture();
	}

	@Override
	public int glGetError() {
		return GL.getError();
	}

	@Override
	public void glGetIntegerv(int pname, IntBuffer params) {
		return GL.getIntegerv(pname, params);
	}

	@Override
	public String glGetString(int name) {
		return GL.getString(name);
	}

	@Override
	public void glHint(int target, int mode) {
		GL.hint(target, mode);
	}

	@Override
	public void glLineWidth(float width) {
		GL.lineWidth(width);
	}

	@Override
	public void glPixelStorei(int pname, int param) {
		GL.pixelStorei(pname, param);
	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		GL.polygonOffset(factor, units);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		GL.readPixels(x, y, width, height, format, type, pixels);
	}

	@Override
	public void glScissor(int x, int y, int width, int height) {
		GL.scissor(x, y, width, height);
	}

	@Override
	public void glStencilFunc(int func, int ref, int mask) {
		GL.stencilFunc(func, ref, mask);
	}

	@Override
	public void glStencilMask(int mask) {
		GL.stencilMask(mask);
	}

	@Override
	public void glStencilOp(int fail, int zfail, int zpass) {
		GL.stencilOp(fail, zfail, zpass);
	}

	@Override
	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
		GL.texImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		GL.texParameterf(target, pname, param);
	}

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
		GL.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		GL.viewport(x, y, width, height);
	}

	@Override
	public void glAttachShader(int program, int shader) {
		GL.attachShader(program, shader);
	}

	@Override
	public void glBindAttribLocation(int program, int index, String name) {
		GL.bindAttribLocation(program, index, name);
	}

	@Override
	public void glBindBuffer(int target, int buffer) {
		GL.bindBuffer(target, buffer);
	}

	@Override
	public void glBindFramebuffer(int target, int framebuffer) {
		GL.bindFramebuffer(target, framebuffer);
	}

	@Override
	public void glBindRenderbuffer(int target, int renderbuffer) {
		GL.bindRenderbuffer(target, renderbuffer);
	}

	@Override
	public void glBlendColor(float red, float green, float blue, float alpha) {
		GL.blendColor(red, green, blue, alpha);
	}

	@Override
	public void glBlendEquation(int mode) {
		GL.blendEquation(mode);
	}

	@Override
	public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
		GL.blendEquationSeparate(modeRGB, modeAlpha);
	}

	@Override
	public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		GL.blendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	@Override
	public void glBufferData(int target, int size, Buffer data, int usage) {
		GL.bufferData(target, size, data, usage);
	}

	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data) {
		GL.bufferSubData(target, offset, size, data);
	}

	@Override
	public int glCheckFramebufferStatus(int target) {
		return GL.checkFramebufferStatus(target);
	}

	@Override
	public void glCompileShader(int shader) {
		GL.compileShader(shader);
	}

	@Override
	public int glCreateProgram() {
		return GL.createProgram();
	}

	@Override
	public int glCreateShader(int type) {
		return GL.createShader(type);
	}

	@Override
	public void glDeleteBuffer(int buffer) {
		GL.deleteBuffer(buffer);
	}

	@Override
	public void glDeleteBuffers(int n, IntBuffer buffers) {
		for (int i = 0; i < n; i++) glDeleteBuffer(buffers.get(i));
	}

	@Override
	public void glDeleteFramebuffer(int framebuffer) {
		GL.deleteFramebuffer(framebuffer);
	}

	@Override
	public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
		for (int i = 0; i < n; i++) glDeleteFramebuffer(framebuffers.get(i));
	}

	@Override
	public void glDeleteProgram(int program) {
		GL.deleteProgram(program);
	}

	@Override
	public void glDeleteRenderbuffer(int renderbuffer) {
		GL.deleteRenderbuffer(renderbuffer);
	}

	@Override
	public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
		for (int i = 0; i < n; i++) glDeleteRenderbuffer(renderbuffers.get(i));
	}

	@Override
	public void glDeleteShader(int shader) {
		GL.deleteShader(shader);
	}

	@Override
	public void glDetachShader(int program, int shader) {
		GL.detachShader(program, shader);
	}

	@Override
	public void glDisableVertexAttribArray(int index) {
		GL.disableVertexAttribArray(index);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, int indices) {
		GL.drawElements(mode, count, type, indices);
	}

	@Override
	public void glEnableVertexAttribArray(int index) {
		GL.enableVertexAttribArray(index);
	}

	@Override
	public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
		GL.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		GL.framebufferTexture2D(target, attachment, textarget, texture, level);
	}

	@Override
	public int glGenBuffer() {
		return GL.createBuffer();
	}

	@Override
	public void glGenBuffers(int n, IntBuffer buffers) {
		for (int i = 0; i < n; i++) buffers.put(i, glGenBuffers());
	}

	@Override
	public void glGenerateMipmap(int target) {
		GL.generateMipmap(target);
	}

	@Override
	public int glGenFramebuffer() {
		return GL.createFramebuffer();
	}

	@Override
	public void glGenFramebuffers(int n, IntBuffer framebuffers) {
		for (int i = 0; i < n; i++) framebuffers.put(i, glGenFramebuffer());
	}

	@Override
	public int glGenRenderbuffer() {
		return GL.createRenderbuffer();
	}

	@Override
	public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
		for (int i = 0; i < n; i++) renderbuffers.put(i, glGenRenderbuffer());
	}

	@Override
	public String glGetActiveAttrib(int program, int index, IntBuffer size, Buffer type) {
		return GL.getActiveAttrib(program, index, size, type);
	}

	@Override
	public String glGetActiveUniform(int program, int index, IntBuffer size, Buffer type) {
		return GL.getActiveUniform(program, index, size, type);
	}

	@Override
	public void glGetAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
		GL.getAttachedShaders(program, maxcount, buffer, shaders)
	}

	@Override
	public int glGetAttribLocation(int program, String name) {
		return GL.getAttribLocation(program, name);
	}

	@Override
	public void glGetBooleanv(int pname, Buffer params) {
		GL.getBooleanv(pname, params);
	}

	@Override
	public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
		GL.getBufferParameteriv(target, name, params);
	}

	@Override
	public void glGetFloatv(int pname, FloatBuffer params) {
		GL.getFloatv(pname, params);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
		GL.getFramebufferAttachmentParameter(target, attachment, pname, params);
	}

	@Override
	public void glGetProgramiv(int program, int pname, IntBuffer params) {
		GL.getProgramiv(program, pname, params);
	}

	@Override
	public String glGetProgramInfoLog(int program) {
		return GL.getProgramInfoLog(program);
	}

	@Override
	public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
		GL.getRenderbufferParameteriv(target, pname, params);
	}

	@Override
	public void glGetShaderiv(int shader, int pname, IntBuffer params) {
		GL.getShaderiv(shader, pname, params);
	}

	@Override
	public String glGetShaderInfoLog(int shader) {
		return GL.getShaderInfoLog(shader);
	}

	@Override
	public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
		GL.getShaderPrecisionFormat(shadertype, precisiontype, range, precision);
	}

	@Override
	public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
		GL.getTexParameter(target, pname, params);
	}

	@Override
	public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
		GL.getTexParameter(target, pname, params);
	}

	@Override
	public void glGetUniformfv(int program, int location, FloatBuffer params) {
		GL.getUniform(program, location, params);
	}

	@Override
	public void glGetUniformiv(int program, int location, IntBuffer params) {
		GL.getUniform(program, location, params);
	}

	@Override
	public int glGetUniformLocation(int program, String name) {
		return GL.getUniformLocation(program, name);
	}

	@Override
	public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
		GL.getVertexAttrib(index, pname, params);
	}

	@Override
	public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
		GL.getVertexAttrib(index, pname, params);
	}

	@Override
	public void glGetVertexAttribPointerv(int index, int pname, Buffer pointer) {
		//GL.getVertexAttrib(index, pname, params);
	}

	@Override
	public boolean glIsBuffer(int buffer) {
		return GL.isBuffer(buffer);
	}

	@Override
	public boolean glIsEnabled(int cap) {
		return GL.isEnabled(cap);
	}

	@Override
	public boolean glIsFramebuffer(int framebuffer) {
		return GL.isFramebuffer(framebuffer);
	}

	@Override
	public boolean glIsProgram(int program) {
		return GL.isProgram(program);
	}

	@Override
	public boolean glIsRenderbuffer(int renderbuffer) {
		return GL.isRenderbuffer(renderbuffer);
	}

	@Override
	public boolean glIsShader(int shader) {
		return GL.isShader(shader);
	}

	@Override
	public boolean glIsTexture(int texture) {
		return GL.isTexture(texture);
	}

	@Override
	public void glLinkProgram(int program) {
		GL.linkProgram(program);
	}

	@Override
	public void glReleaseShaderCompiler() {
		GL.releaseShaderCompiler();
	}

	@Override
	public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
		GL.renderbufferStorage(target, internalformat, width, height);
	}

	@Override
	public void glSampleCoverage(float value, boolean invert) {
		GL.sampleCoverage(value, invert);
	}

	@Override
	public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
		GL.shaderBinary(n, shaders, binaryformat, binary, length);
	}

	@Override
	public void glShaderSource(int shader, String string) {
		GL.shaderSource(shader, string);
	}

	@Override
	public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
		GL.stencilFuncSeparate(face, func, ref, mask);
	}

	@Override
	public void glStencilMaskSeparate(int face, int mask) {
		GL.stencilMaskSeparate(face, mask);
	}

	@Override
	public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
		GL.stencilOpSeparate(face, fail, zfail, zpass);
	}

	@Override
	public void glTexParameterfv(int target, int pname, FloatBuffer params) {
		GL.texParameterf(target, pname, params);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		GL.texParameteri(target, pname, param);
	}

	@Override
	public void glTexParameteriv(int target, int pname, IntBuffer params) {
		GL.texParameteri(target, pname, params);
	}

	@Override
	public void glUniform1f(int location, float x) {
		GL.uniform1f(location, x);
	}

	@Override
	public void glUniform1fv(int location, int count, FloatBuffer v) {
		GL.uniform1fv(location, count, v);
	}

	@Override
	public void glUniform1fv(int location, int count, float[] v, int offset) {
		GL.uniform1fv(location, count, v, offset);
	}

	@Override
	public void glUniform1i(int location, int x) {
		GL.uniform1i(location, x);
	}

	@Override
	public void glUniform1iv(int location, int count, IntBuffer v) {

	}

	@Override
	public void glUniform1iv(int location, int count, int[] v, int offset) {

	}

	@Override
	public void glUniform2f(int location, float x, float y) {

	}

	@Override
	public void glUniform2fv(int location, int count, FloatBuffer v) {

	}

	@Override
	public void glUniform2fv(int location, int count, float[] v, int offset) {

	}

	@Override
	public void glUniform2i(int location, int x, int y) {

	}

	@Override
	public void glUniform2iv(int location, int count, IntBuffer v) {

	}

	@Override
	public void glUniform2iv(int location, int count, int[] v, int offset) {

	}

	@Override
	public void glUniform3f(int location, float x, float y, float z) {

	}

	@Override
	public void glUniform3fv(int location, int count, FloatBuffer v) {

	}

	@Override
	public void glUniform3fv(int location, int count, float[] v, int offset) {

	}

	@Override
	public void glUniform3i(int location, int x, int y, int z) {

	}

	@Override
	public void glUniform3iv(int location, int count, IntBuffer v) {

	}

	@Override
	public void glUniform3iv(int location, int count, int[] v, int offset) {

	}

	@Override
	public void glUniform4f(int location, float x, float y, float z, float w) {

	}

	@Override
	public void glUniform4fv(int location, int count, FloatBuffer v) {

	}

	@Override
	public void glUniform4fv(int location, int count, float[] v, int offset) {

	}

	@Override
	public void glUniform4i(int location, int x, int y, int z, int w) {

	}

	@Override
	public void glUniform4iv(int location, int count, IntBuffer v) {

	}

	@Override
	public void glUniform4iv(int location, int count, int[] v, int offset) {

	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {

	}

	@Override
	public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {

	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {

	}

	@Override
	public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {

	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {

	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {

	}

	@Override
	public void glUseProgram(int program) {

	}

	@Override
	public void glValidateProgram(int program) {

	}

	@Override
	public void glVertexAttrib1f(int indx, float x) {

	}

	@Override
	public void glVertexAttrib1fv(int indx, FloatBuffer values) {

	}

	@Override
	public void glVertexAttrib2f(int indx, float x, float y) {

	}

	@Override
	public void glVertexAttrib2fv(int indx, FloatBuffer values) {

	}

	@Override
	public void glVertexAttrib3f(int indx, float x, float y, float z) {

	}

	@Override
	public void glVertexAttrib3fv(int indx, FloatBuffer values) {

	}

	@Override
	public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {

	}

	@Override
	public void glVertexAttrib4fv(int indx, FloatBuffer values) {

	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {

	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {

	}
}
