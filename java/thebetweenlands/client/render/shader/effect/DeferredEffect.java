package thebetweenlands.client.render.shader.effect;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public abstract class DeferredEffect {
	//Clear colors
	private float cr, cg, cb, ca;

	//Additional stages
	private DeferredEffect[] stages;

	//Buffers
	private static final FloatBuffer TEXEL_SIZE_BUFFER = BufferUtils.createFloatBuffer(2);

	private int shaderProgramID = -1;
	private int diffuseSamplerUniformID = -1;
	private int texelSizeUniformID = -1;

	/**
	 * Initializes the effect. Requires an OpenGL context to work
	 */
	public final DeferredEffect init() {
		this.initShaders();
		this.stages = this.getStages();
		if(this.stages != null && this.stages.length > 0) {
			for(DeferredEffect stage : this.stages) {
				stage.init();
			}
		}
		if(!this.initEffect()) {
			throw new RuntimeException("Couldn't initialize shaders for deferred effect: " + this.toString());
		}
		return this;
	}

	/**
	 * Sets the default background color
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 * @return
	 */
	public final DeferredEffect setBackgroundColor(float r, float g, float b, float a) {
		this.cr = r;
		this.cg = g;
		this.cb = b;
		this.ca = a;
		return this;
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
		OpenGlHelper.func_153187_e(this.shaderProgramID);
		if(this.stages != null && this.stages.length > 0) {
			for(DeferredEffect stage : this.stages) {
				stage.delete();
			}
		}
		this.deleteEffect();
	}

	/**
	 * Applies the effect to a texture and renders it to the destination FBO.
	 * Handles perspective only. Blending, texture mode, lighting etc. have to be
	 * handled manually.
	 * @param src Source texture ID (optional)
	 * @param dst Destination FBO
	 * @param prev Previous FBO (optional)
	 * @param renderWidth Render width in pixels (relative to dst FBO size)
	 * @param renderHeight Render height in pixels (relative to dst FBO size)
	 */
	public final void apply(int src, Framebuffer dst, Framebuffer blitBuffer, Framebuffer prev, double renderWidth, double renderHeight) {
		this.apply(src, dst, blitBuffer, prev, renderWidth, renderHeight, true);
	}

	private final void apply(int src, Framebuffer dst, Framebuffer blitBuffer, Framebuffer prev, double renderWidth, double renderHeight, boolean restore) {
		if(this.shaderProgramID == -1 || dst == null) return;

		//Bind destination FBO
		dst.bindFramebuffer(true);

		if(restore) {
			//Backup attributes
			GL11.glPushAttrib(GL11.GL_MATRIX_MODE);
			GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
			GL11.glPushAttrib(GL11.GL_TRANSFORM_BIT);

			//Backup matrices
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();

			//Set up 2D matrices
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, dst.framebufferWidth, dst.framebufferHeight, 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
		}

		//Clear buffers
		GL11.glClearColor(this.cr, this.cg, this.cb, this.ca);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

		//Use shader
		OpenGlHelper.func_153161_d(this.shaderProgramID);

		//Upload sampler uniform (src texture ID)
		if(this.diffuseSamplerUniformID >= 0 && src >= 0) {
			OpenGlHelper.func_153163_f(this.diffuseSamplerUniformID, 0);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, src);
		}

		//Upload texel size uniform
		if(this.texelSizeUniformID >= 0) {
			TEXEL_SIZE_BUFFER.position(0);
			TEXEL_SIZE_BUFFER.put(1.0F / (float)Minecraft.getMinecraft().displayWidth);
			TEXEL_SIZE_BUFFER.put(1.0F / (float)Minecraft.getMinecraft().displayHeight);
			TEXEL_SIZE_BUFFER.flip();
			OpenGlHelper.func_153177_b(this.texelSizeUniformID, TEXEL_SIZE_BUFFER);
		}

		//Uploads additional uniforms
		this.uploadUniforms();

		//Render texture
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex2d(0, 0);
		GL11.glTexCoord2d(0.0D, 0.0D);
		GL11.glVertex2d(0, renderHeight);
		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex2d(renderWidth, renderHeight);
		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex2d(renderWidth, renderHeight);
		GL11.glTexCoord2d(1.0D, 1.0D);
		GL11.glVertex2d(renderWidth, 0);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex2d(0, 0);
		GL11.glEnd();

		//Apply additional stages
		if(blitBuffer != null && this.stages != null && this.stages.length > 0) {
			for(DeferredEffect stage : this.stages) {
				//Render to blit buffer
				stage.apply(dst.framebufferTexture, blitBuffer, dst, null, renderWidth, renderHeight, false);

				//Render from blit buffer to destination buffer
				dst.bindFramebuffer(false);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, blitBuffer.framebufferTexture);
				GL11.glBegin(GL11.GL_TRIANGLES);
				GL11.glTexCoord2d(0, 1);
				GL11.glVertex2d(0, 0);
				GL11.glTexCoord2d(0, 0);
				GL11.glVertex2d(0, renderHeight);
				GL11.glTexCoord2d(1, 0);
				GL11.glVertex2d(renderWidth, renderHeight);
				GL11.glTexCoord2d(1, 0);
				GL11.glVertex2d(renderWidth, renderHeight);
				GL11.glTexCoord2d(1, 1);
				GL11.glVertex2d(renderWidth, 0);
				GL11.glTexCoord2d(0, 1);
				GL11.glVertex2d(0, 0);
				GL11.glEnd();
			}
		}

		//Unbind shader
		OpenGlHelper.func_153161_d(0);

		if(restore) {
			//Restore matrices
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();

			//Restore attributes
			GL11.glPopAttrib();
			GL11.glPopAttrib();
			GL11.glPopAttrib();

			//Restore matrices
			GL11.glPopMatrix();

			//Bind previous FBO
			if(prev != null) prev.bindFramebuffer(true);
		}
	}

	/**
	 * Initializes the shaders
	 */
	private void initShaders() {
		if(this.shaderProgramID == -1) {
			this.shaderProgramID = OpenGlHelper.func_153183_d();
			int vertexShaderID = -1;
			int fragmentShaderID = -1;
			try {
				ResourceLocation[] shaderLocations = this.getShaders();
				String[] shaders = new String[2];
				for(int i = 0; i < 2; i++) {
					StringWriter strBuf = new StringWriter();
					IOUtils.copy(Minecraft.getMinecraft().getResourceManager().getResource(shaderLocations[i]).getInputStream(), strBuf, "UTF-8");
					shaders[i] = strBuf.toString();
				}
				vertexShaderID = this.createShader(shaders[0], ARBVertexShader.GL_VERTEX_SHADER_ARB);
				fragmentShaderID = this.createShader(shaders[1], ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
			} catch(Exception ex) {
				this.shaderProgramID = -1;
				vertexShaderID = -1;
				fragmentShaderID = -1;

				throw new RuntimeException("Error creating shader", ex);
			}
			if(this.shaderProgramID != -1 && vertexShaderID != -1 && fragmentShaderID != -1) {
				//Attach and link vertex and fragment shader to shader program
				OpenGlHelper.func_153178_b(this.shaderProgramID, vertexShaderID);
				OpenGlHelper.func_153178_b(this.shaderProgramID, fragmentShaderID);
				OpenGlHelper.func_153179_f(this.shaderProgramID);

				//Check for errors
				if (OpenGlHelper.func_153175_a(this.shaderProgramID, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
					throw new RuntimeException("Error creating shader: " + getLogInfoProgram(this.shaderProgramID));
				}
				GL20.glValidateProgram(this.shaderProgramID);
				//ARBShaderObjects.glValidateProgramARB(this.shaderProgramID);
				if (OpenGlHelper.func_153175_a(this.shaderProgramID, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
					throw new RuntimeException("Error creating shader: " + getLogInfoProgram(this.shaderProgramID));
				}

				//Delete vertex and fragment shader
				OpenGlHelper.func_153180_a(vertexShaderID);
				OpenGlHelper.func_153180_a(fragmentShaderID);

				//Get uniforms
				this.diffuseSamplerUniformID = OpenGlHelper.func_153194_a(this.shaderProgramID, "s_diffuse");
				this.texelSizeUniformID = OpenGlHelper.func_153194_a(this.shaderProgramID, "u_oneTexel");

				//Unbind shader
				OpenGlHelper.func_153161_d(0);
			} else {
				if(vertexShaderID != -1)
					OpenGlHelper.func_153180_a(vertexShaderID);
				if(fragmentShaderID != 1)
					OpenGlHelper.func_153180_a(fragmentShaderID);
				if(this.shaderProgramID != -1)
					OpenGlHelper.func_153187_e(this.shaderProgramID);
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
			shader = OpenGlHelper.func_153195_b(shaderType);
			if(shader == 0) {
				return 0;
			}
			byte[] shaderCodeBytes = shaderCode.getBytes();
			ByteBuffer shaderCodeByteBuffer = BufferUtils.createByteBuffer(shaderCodeBytes.length);
			shaderCodeByteBuffer.put(shaderCodeBytes);
			shaderCodeByteBuffer.position(0);
			OpenGlHelper.func_153169_a(shader, shaderCodeByteBuffer);
			OpenGlHelper.func_153170_c(shader);
			if (OpenGlHelper.func_153157_c(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
				throw new RuntimeException("Error creating shader: " + getLogInfoShader(shader));
			}
			return shader;
		} catch(Exception exc) {
			OpenGlHelper.func_153180_a(shader);
			throw exc;
		}
	}
	private static String getLogInfoShader(int obj) {
		return OpenGlHelper.func_153158_d(obj, OpenGlHelper.func_153157_c(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	private static String getLogInfoProgram(int obj) {
		return OpenGlHelper.func_153166_e(obj, OpenGlHelper.func_153175_a(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	/**
	 * Returns the shader code. [0] = vertex shader, [1] = fragment shader
	 * @return
	 */
	protected abstract ResourceLocation[] getShaders();

	/**
	 * Uploads any additional uniforms
	 */
	protected void uploadUniforms() {}

	/**
	 * Returns additional stages
	 * @return
	 */
	protected DeferredEffect[] getStages() { return null; }

	/**
	 * Used to delete additional things and free memory
	 */
	protected void deleteEffect() {}

	/**
	 * Used to initialize the effect with additional things such as getting uniform locations.
	 * Return false if something during initializiation goes wrong.
	 */
	protected boolean initEffect() { return true; }

	protected FloatBuffer getSingleFloatBuffer(float value) {
		FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(1);
		floatBuffer.put(value);
		floatBuffer.position(0);
		return floatBuffer;
	}
}
