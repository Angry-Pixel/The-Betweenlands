package thebetweenlands.client.shader.postprocessing;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;

public abstract class PostProcessingEffect<T extends PostProcessingEffect<?>> {
	//Clear colors
	private float cr, cg, cb, ca;

	//Additional stages
	private PostProcessingEffect<?>[] stages;

	//Buffers
	private static final FloatBuffer TEXEL_SIZE_BUFFER = BufferUtils.createFloatBuffer(2);
	private static final FloatBuffer CLEAR_COLOR_BUFFER = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer MATRIX4F_BUFFER = BufferUtils.createFloatBuffer(16);
	private static final FloatBuffer FLOAT_BUFFER_1 = BufferUtils.createFloatBuffer(1);
	private static final FloatBuffer FLOAT_BUFFER_2 = BufferUtils.createFloatBuffer(2);
	private static final FloatBuffer FLOAT_BUFFER_3 = BufferUtils.createFloatBuffer(3);
	private static final FloatBuffer FLOAT_BUFFER_4 = BufferUtils.createFloatBuffer(4);
	private static final IntBuffer INT_BUFFER_1 = BufferUtils.createIntBuffer(1);
	private static final IntBuffer INT_BUFFER_2 = BufferUtils.createIntBuffer(2);
	private static final IntBuffer INT_BUFFER_3 = BufferUtils.createIntBuffer(3);
	private static final IntBuffer INT_BUFFER_4 = BufferUtils.createIntBuffer(4);

	private int shaderProgramID = -1;
	private int diffuseSamplerUniformID = -1;
	private int texelSizeUniformID = -1;

	private boolean initialized = false;

	/**
	 * Initializes the effect. Requires an OpenGL context to work
	 */
	@SuppressWarnings("unchecked")
	public final T init() {
		//Only initialize once to prevent allocation memory leaks
		if(!this.initialized) {
			this.initShaders();
			this.stages = this.getStages();
			if(this.stages != null) {
				for(PostProcessingEffect<?> stage : this.stages) {
					stage.init();
				}
			}
			if(!this.initEffect() || this.shaderProgramID < 0) {
				throw new RuntimeException("Couldn't initialize shaders for post processing effect: " + this);
			}
			this.initialized = true;
		}
		return (T) this;
	}

	/**
	 * Sets the default background color
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final T setBackgroundColor(float r, float g, float b, float a) {
		this.cr = r;
		this.cg = g;
		this.cb = b;
		this.ca = a;
		return (T) this;
	}

	/**
	 * Returns the shader program ID
	 */
	public final int getShaderProgram() {
		return this.shaderProgramID;
	}

	/**
	 * Deletes all shaders and frees memory
	 */
	public final void delete() {
		if(this.shaderProgramID > 0)
			GlStateManager.glDeleteProgram(this.shaderProgramID);
		if(this.stages != null) {
			for(PostProcessingEffect<?> stage : this.stages) {
				stage.delete();
			}
		}
		this.deleteEffect();
	}

	/**
	 * Returns the effect builder
	 * @param dst
	 * @return
	 */
	public EffectBuilder<T> create(RenderTarget dst) {
		return new EffectBuilder<>(this, dst);
	}

	public static final class EffectBuilder<T extends PostProcessingEffect<?>> {
		private final PostProcessingEffect<T> effect;
		private final RenderTarget dst;

		private int src = -1;
		private RenderTarget blitFrfamebuffer = null;
		private RenderTarget prevFramebuffer = null;
		private double renderWidth = -1.0D;
		private double renderHeight = -1.0D;
		private boolean restore = true;
		private boolean mirrorX = false;
		private boolean mirrorY = false;
		private boolean clearDepth = true;
		private boolean clearColor = true;

		private EffectBuilder(PostProcessingEffect<T> effect, RenderTarget dst) {
			this.effect = effect;
			this.dst = dst;
		}

		/**
		 * Sets the source texture the effect should read from.
		 * <p><b>Note:</b> If the source is the same as the destination a blit buffer is required
		 * @param src
		 * @return
		 */
		public EffectBuilder<T> setSource(int src) {
			this.src = src;
			return this;
		}

		/**
		 * Sets the blit FBO if this effect requires one
		 * @param blitFramebuffer
		 * @return
		 */
		public EffectBuilder<T> setBlitFramebuffer(RenderTarget blitFramebuffer) {
			this.blitFrfamebuffer = blitFramebuffer;
			return this;
		}

		/**
		 * Sets which FBO should be bound after applying the effect
		 * @param prevFramebuffer
		 * @return
		 */
		public EffectBuilder<T> setPreviousFramebuffer(RenderTarget prevFramebuffer) {
			this.prevFramebuffer = prevFramebuffer;
			return this;
		}

		/**
		 * Sets the render dimensions for this effect.
		 * Uses the destination FBO dimensions by default
		 * @param renderWidth
		 * @param renderHeight
		 * @return
		 */
		public EffectBuilder<T> setRenderDimensions(double renderWidth, double renderHeight) {
			this.renderWidth = renderWidth;
			this.renderHeight = renderHeight;
			return this;
		}

		/**
		 * Sets whether the GL states should be restored after applying the effect.
		 * True by default
		 * @param restore
		 * @return
		 */
		public EffectBuilder<T> setRestoreGlState(boolean restore) {
			this.restore = restore;
			return this;
		}

		/**
		 * Sets the renderer to mirror the X axis
		 * @param mirror
		 * @return
		 */
		public EffectBuilder<T> setMirrorX(boolean mirror) {
			this.mirrorX = mirror;
			return this;
		}

		/**
		 * Sets the renderer to mirror the Y axis
		 * @param mirror
		 * @return
		 */
		public EffectBuilder<T> setMirrorY(boolean mirror) {
			this.mirrorY = mirror;
			return this;
		}

		/**
		 * Sets whether the destination FBO depth buffer should be cleared
		 * @param clearDepth
		 * @return
		 */
		public EffectBuilder<T> setClearDepth(boolean clearDepth) {
			this.clearDepth = clearDepth;
			return this;
		}

		/**
		 * Sets whether the destination FBO color buffer should be cleared
		 * @param clearColor
		 * @return
		 */
		public EffectBuilder<T> setClearColor(boolean clearColor) {
			this.clearColor = clearColor;
			return this;
		}

		public void render(float partialTicks) {
			double renderWidth = this.renderWidth;
			double renderHeight = this.renderHeight;
			if(renderWidth < 0.0D || renderHeight < 0.0D) {
				renderWidth = this.dst.viewWidth;
				renderHeight = this.dst.viewHeight;
			}
			this.effect.render(partialTicks, this.src, this.dst, this.blitFrfamebuffer, this.prevFramebuffer, renderWidth, renderHeight, this.restore, this.mirrorX, this.mirrorY, this.clearDepth, this.clearColor);
		}
	}

	private void render(float partialTicks, int src, RenderTarget dst, RenderTarget blitBuffer, RenderTarget prev, double renderWidth, double renderHeight, boolean restore, boolean mirrorX, boolean mirrorY, boolean clearDepth, boolean clearColor) {
		if(this.shaderProgramID == -1 || dst == null) return;

		RenderTarget intermediateDst = dst;

		//If source is the same as the destination then use a blit buffer
		if(src == dst.getColorTextureId()) {
			intermediateDst = blitBuffer;
		}


		//Bind destination FBO
		intermediateDst.bindWrite(true);

		int prevShaderProgram = 0;

		if(restore) {
			//Backup attributes
			GL11.glPushAttrib(
					GL11.GL_MATRIX_MODE |
					GL11.GL_VIEWPORT_BIT |
					GL11.GL_TRANSFORM_BIT
					);
			prevShaderProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
			GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, CLEAR_COLOR_BUFFER);

			//Backup matrices
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
		}

		//Set up 2D matrices
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, intermediateDst.viewWidth, intermediateDst.viewHeight, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		//Clear buffers
		if(clearDepth) {
			RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
		}
		if(clearColor) {
			RenderSystem.clearColor(this.cr, this.cg, this.cb, this.ca);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
		}

		//Use shader
		GlStateManager._glUseProgram(this.shaderProgramID);

		//Upload sampler uniform (src texture ID)
		if(this.diffuseSamplerUniformID >= 0 && src >= 0) {
			this.uploadSampler(this.diffuseSamplerUniformID, src, 0);
		}

		//Upload texel size uniform
		if(this.texelSizeUniformID >= 0) {
			TEXEL_SIZE_BUFFER.position(0);
			TEXEL_SIZE_BUFFER.put(1.0F / (float)intermediateDst.viewWidth);
			TEXEL_SIZE_BUFFER.put(1.0F / (float)intermediateDst.viewHeight);
			TEXEL_SIZE_BUFFER.flip();
			GlStateManager._glUniform1(this.texelSizeUniformID, TEXEL_SIZE_BUFFER);
		}

		//Uploads additional uniforms
		this.uploadUniforms(partialTicks);

		//Render texture
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2f(mirrorX ? 1 : 0, mirrorY ? 1 : 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glTexCoord2f(mirrorX ? 1 : 0, mirrorY ? 0 : 1);
		GL11.glVertex3f(0, (float)renderHeight, 0);
		GL11.glTexCoord2f(mirrorX ? 0 : 1, mirrorY ? 0 : 1);
		GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
		GL11.glTexCoord2f(mirrorX ? 0 : 1, mirrorY ? 0 : 1);
		GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
		GL11.glTexCoord2f(mirrorX ? 0 : 1, mirrorY ? 1 : 0);
		GL11.glVertex3f((float)renderWidth, 0, 0);
		GL11.glTexCoord2f(mirrorX ? 1 : 0, mirrorY ? 1 : 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glEnd();

		//Apply additional stages
		if(blitBuffer != null && this.stages != null) {
			for(PostProcessingEffect<?> stage : this.stages) {
				//Render to blit buffer
				stage.render(partialTicks, intermediateDst.getColorTextureId(), blitBuffer, intermediateDst, null, renderWidth, renderHeight, false, false, false, clearDepth, clearColor);

				//Render from blit buffer to destination buffer
				intermediateDst.bindWrite(true);
				GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glTexCoord2f(mirrorX ? 1 : 0, mirrorY ? 1 : 0);
				GL11.glVertex3f(0, 0, 0);
				GL11.glTexCoord2f(mirrorX ? 1 : 0, mirrorY ? 0 : 1);
				GL11.glVertex3f(0, (float)renderHeight, 0);
				GL11.glTexCoord2f(mirrorX ? 0 : 1, mirrorY ? 0 : 1);
				GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
				GL11.glTexCoord2f(mirrorX ? 0 : 1, mirrorY ? 0 : 1);
				GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
				GL11.glTexCoord2f(mirrorX ? 0 : 1, mirrorY ? 1 : 0);
				GL11.glVertex3f((float)renderWidth, 0, 0);
				GL11.glTexCoord2f(mirrorX ? 1 : 0, mirrorY ? 1 : 0);
				GL11.glVertex3f(0, 0, 0);
				GL11.glEnd();
			}
		}

		//Don't use any shader to copy from blit buffer to destination
		GlStateManager._glUseProgram(0);

		if(src == dst.getColorTextureId()) {
			//Set up 2D matrices
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, dst.viewWidth, dst.viewHeight, 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

			dst.bindWrite(true);
			RenderSystem.bindTexture(intermediateDst.getColorTextureId());
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(0, 0, 0);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(0, (float)renderHeight, 0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f((float)renderWidth, (float)renderHeight, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f((float)renderWidth, 0, 0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(0, 0, 0);
			GL11.glEnd();
		}

		//Bind previous shader
		GlStateManager._glUseProgram(prevShaderProgram);

		this.postRender(partialTicks);

		if(restore) {
			//Restore matrices
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();

			//Restore attributes
			GL11.glPopAttrib();
			RenderSystem.clearColor(CLEAR_COLOR_BUFFER.get(0), CLEAR_COLOR_BUFFER.get(1), CLEAR_COLOR_BUFFER.get(2), CLEAR_COLOR_BUFFER.get(3));

			//Restore matrices
			GL11.glPopMatrix();

			//Bind previous FBO
			if(prev != null)
				prev.bindWrite(true);
		}
	}

	/**
	 * Initializes the shaders
	 */
	private void initShaders() {
		if(this.shaderProgramID == -1) {
			this.shaderProgramID = GlStateManager.glCreateProgram();
			int vertexShaderID = -1;
			int fragmentShaderID = -1;
			ResourceLocation[] shaderLocations = this.getShaders();
			try {
				String[] shaders = new String[2];
				for(int i = 0; i < 2; i++) {
					StringWriter strBuf = new StringWriter();
					IOUtils.copy(Minecraft.getInstance().getResourceManager().getResourceOrThrow(shaderLocations[i]).open(), strBuf, "UTF-8");
					shaders[i] = strBuf.toString();
				}
				vertexShaderID = createShader(shaders[0], ARBVertexShader.GL_VERTEX_SHADER_ARB);
				fragmentShaderID = createShader(shaders[1], ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
			} catch(Exception ex) {
				this.shaderProgramID = -1;
				vertexShaderID = -1;
				fragmentShaderID = -1;

				throw new RuntimeException(String.format("Error creating shaders %s and %s", shaderLocations[0], shaderLocations[1]), ex);
			}
			if(this.shaderProgramID != -1 && vertexShaderID != -1 && fragmentShaderID != -1) {
				//Attach and link vertex and fragment shader to shader program
				GlStateManager.glAttachShader(this.shaderProgramID, vertexShaderID);
				GlStateManager.glAttachShader(this.shaderProgramID, fragmentShaderID);
				GlStateManager.glLinkProgram(this.shaderProgramID);

				//Check for errors
				if (GlStateManager.glGetProgrami(this.shaderProgramID, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
					throw new RuntimeException(String.format("Error creating shaders %s and %s: %s", shaderLocations[0], shaderLocations[1], getLogInfoProgram(this.shaderProgramID)));
				}
				GL20.glValidateProgram(this.shaderProgramID);
				//ARBShaderObjects.glValidateProgramARB(this.shaderProgramID);
				if (GlStateManager.glGetProgrami(this.shaderProgramID, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
					throw new RuntimeException(String.format("Error creating shaders %s and %s: %s", shaderLocations[0], shaderLocations[1], getLogInfoProgram(this.shaderProgramID)));
				}

				//Delete vertex and fragment shader
				GlStateManager.glDeleteShader(vertexShaderID);
				GlStateManager.glDeleteShader(fragmentShaderID);

				//Get uniforms
				this.diffuseSamplerUniformID = GlStateManager._glGetUniformLocation(this.shaderProgramID, "s_diffuse");
				this.texelSizeUniformID = GlStateManager._glGetUniformLocation(this.shaderProgramID, "u_oneTexel");

				//Unbind shader
				GlStateManager._glUseProgram(0);
			} else {
				if(vertexShaderID != -1)
					GlStateManager.glDeleteShader(vertexShaderID);
				if(fragmentShaderID != 1)
					GlStateManager.glDeleteShader(fragmentShaderID);
				if(this.shaderProgramID != -1)
					GlStateManager.glDeleteProgram(this.shaderProgramID);
			}
		}
	}

	/**
	 * Compiles and creates a shader from the shader code.
	 * @param shaderCode
	 * @param shaderType
	 * @return
	 * @throws Exception
	 */
	private static int createShader(String shaderCode, int shaderType) throws Exception {
		int shader = 0;
		try {
			shader = GlStateManager.glCreateShader(shaderType);
			if(shader == 0) {
				return 0;
			}
			byte[] shaderCodeBytes = shaderCode.getBytes();
			ByteBuffer shaderCodeByteBuffer = BufferUtils.createByteBuffer(shaderCodeBytes.length);
			shaderCodeByteBuffer.put(shaderCodeBytes);
			shaderCodeByteBuffer.position(0);
			//OpenGLHelper.glShaderSource(shader, shaderCodeByteBuffer);
			GlStateManager.glCompileShader(shader);
			if (GlStateManager.glGetShaderi(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
				String shaderTypeName = shaderType == ARBVertexShader.GL_VERTEX_SHADER_ARB ? "vertex" : shaderType == ARBFragmentShader.GL_FRAGMENT_SHADER_ARB ? "fragment" : "";
				throw new RuntimeException("Error creating " + shaderTypeName + " shader: " + getLogInfoShader(shader));
			}
			return shader;
		} catch(Exception exc) {
			GlStateManager.glDeleteShader(shader);
			throw exc;
		}
	}
	private static String getLogInfoShader(int obj) {
		return GlStateManager.glGetShaderInfoLog(obj, GlStateManager.glGetShaderi(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	private static String getLogInfoProgram(int obj) {
		return GlStateManager.glGetProgramInfoLog(obj, GlStateManager.glGetProgrami(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	/**
	 * Returns the shader code. [0] = vertex shader, [1] = fragment shader
	 * @return
	 */
	protected abstract ResourceLocation[] getShaders();

	/**
	 * Uploads any additional uniforms
	 */
	protected void uploadUniforms(float partialTicks) {}

	/**
	 * Returns additional stages.
	 * <p><b>Note:</b> If additional stages are used a blit buffer is required
	 * @return
	 */
	protected PostProcessingEffect<?>[] getStages() { return null; }

	/**
	 * Used to delete additional things and free memory
	 */
	protected void deleteEffect() {}

	/**
	 * Used to initialize the effect with additional things such as getting uniform locations.
	 * Return false if something during initializiation goes wrong.
	 */
	protected boolean initEffect() { return true; }

	/**
	 * Called after the shader was rendered to the screen
	 */
	protected void postRender(float partialTicks) {}

	/**
	 * Uploads up to 4 floats
	 * @param values
	 * @return
	 */
	protected final void uploadFloat(int uniform, float... values) {
		if(uniform >= 0) {
			switch(values.length) {
			default:
			case 1:
				this.setFloats(FLOAT_BUFFER_1, values);
				GlStateManager._glUniform1(uniform, FLOAT_BUFFER_1);
				break;
			case 2:
				this.setFloats(FLOAT_BUFFER_2, values);
				GlStateManager._glUniform2(uniform, FLOAT_BUFFER_2);
				break;
			case 3:
				this.setFloats(FLOAT_BUFFER_3, values);
				GlStateManager._glUniform3(uniform, FLOAT_BUFFER_3);
				break;
			case 4:
				this.setFloats(FLOAT_BUFFER_4, values);
				GlStateManager._glUniform4(uniform, FLOAT_BUFFER_4);
				break;
			}
		}
	}

	private void setFloats(FloatBuffer buffer, float[] values) {
		buffer.position(0);
		for (float value : values) {
			buffer.put(value);
		}
		buffer.flip();
	}

	/**
	 * Uploads up to 4 integers
	 * @param values
	 * @return
	 */
	protected final void uploadInt(int uniform, int... values) {
		if(uniform >= 0) {
			switch(values.length) {
			default:
			case 1:
				this.setInts(INT_BUFFER_1, values);
				GlStateManager._glUniform1(uniform, INT_BUFFER_1);
				break;
			case 2:
				this.setInts(INT_BUFFER_2, values);
				GlStateManager._glUniform2(uniform, INT_BUFFER_2);
				break;
			case 3:
				this.setInts(INT_BUFFER_3, values);
				GlStateManager._glUniform3(uniform, INT_BUFFER_3);
				break;
			case 4:
				this.setInts(INT_BUFFER_4, values);
				GlStateManager._glUniform4(uniform, INT_BUFFER_4);
				break;
			}
		}
	}

	private void setInts(IntBuffer buffer, int[] values) {
		buffer.position(0);
		for (int value : values) {
			buffer.put(value);
		}
		buffer.flip();
	}

	/**
	 * Uploads an int buffer
	 * @param buffer
	 * @return
	 */
	protected final void uploadIntArray(int uniform, IntBuffer buffer) {
		if(uniform >= 0) {
			GlStateManager._glUniform1(uniform, buffer);
		}
	}

	/**
	 * Uploads a float buffer
	 * @param buffer
	 * @return
	 */
	protected final void uploadFloatArray(int uniform, FloatBuffer buffer) {
		if(uniform >= 0) {
			GlStateManager._glUniform1(uniform, buffer);
		}
	}

	/**
	 * Uploads a sampler.
	 * Texture unit 0 is reserved for the default diffuse sampler
	 * @param uniform
	 * @param texture
	 * @param textureUnit
	 */
	protected final void uploadSampler(int uniform, int texture, int textureUnit) {
		if(uniform >= 0 && textureUnit >= 0) {
			RenderSystem.activeTexture(33984 + textureUnit);
			RenderSystem.bindTexture(texture);
			GlStateManager._glUniform1i(uniform, textureUnit);
			RenderSystem.activeTexture(33984);
		}
	}

	/**
	 * Uploads a sampler.
	 * Texture unit 0 is reserved for the default diffuse sampler
	 * @param uniform
	 * @param texture
	 * @param textureUnit
	 */
	protected final void uploadSampler(int uniform, ResourceLocation texture, int textureUnit) {
		if(uniform >= 0 && textureUnit >= 0) {
			RenderSystem.activeTexture(33984 + textureUnit);
			RenderSystem.setShaderTexture(0, texture);
			GlStateManager._glUniform1i(uniform, textureUnit);
			RenderSystem.activeTexture(33984);
		}
	}

	/**
	 * Uploads a matrix
	 * @param uniform
	 * @param matrix
	 */
	protected final void uploadMatrix4f(int uniform, Matrix4f matrix) {
		if(uniform >= 0) {
			MATRIX4F_BUFFER.position(0);
			MATRIX4F_BUFFER.put(0, matrix.m00());
			MATRIX4F_BUFFER.put(1, matrix.m01());
			MATRIX4F_BUFFER.put(2, matrix.m02());
			MATRIX4F_BUFFER.put(3, matrix.m03());
			MATRIX4F_BUFFER.put(4, matrix.m10());
			MATRIX4F_BUFFER.put(5, matrix.m11());
			MATRIX4F_BUFFER.put(6, matrix.m12());
			MATRIX4F_BUFFER.put(7, matrix.m13());
			MATRIX4F_BUFFER.put(8, matrix.m20());
			MATRIX4F_BUFFER.put(9, matrix.m21());
			MATRIX4F_BUFFER.put(10, matrix.m22());
			MATRIX4F_BUFFER.put(11, matrix.m23());
			MATRIX4F_BUFFER.put(12, matrix.m30());
			MATRIX4F_BUFFER.put(13, matrix.m31());
			MATRIX4F_BUFFER.put(14, matrix.m32());
			MATRIX4F_BUFFER.put(15, matrix.m33());
			GlStateManager._glUniformMatrix4(uniform, true, MATRIX4F_BUFFER);
		}
	}

	/**
	 * Returns the uniform location
	 * @param name
	 * @return
	 */
	protected final int getUniform(String name) {
		return GlStateManager._glGetUniformLocation(this.getShaderProgram(), name);
	}
}
