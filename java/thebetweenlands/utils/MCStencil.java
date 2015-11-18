package thebetweenlands.utils;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class MCStencil {
	/**Checks if screen has been resized/FBO has been set up and then updates the FBO**/
	public static void checkSetupFBO() {
		//Gets the FBO of Minecraft
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
		//Check if FBO isn't null
		if(fbo != null) {
			//Checks if screen has been resized or new FBO has been created
			if(fbo.depthBuffer > -1) {
				//Sets up the FBO with depth and stencil extensions (24/8 bit)
				setupFBO(fbo);
				//Reset the ID to prevent multiple FBO's
				fbo.depthBuffer = -1;
			}
		}
	}

	/**
	 * Sets up the FBO with depth and stencil
	 * @param fbo Framebuffer
	 */
	public static void setupFBO(Framebuffer fbo) {
		//Deletes old render buffer extensions such as depth
		//Args: Render Buffer ID
		EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
		//Generates a new render buffer ID for the depth and stencil extension
		int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
		//Binds new render buffer by ID
		//Args: Target (GL_RENDERBUFFER_EXT), ID
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
		//Adds the depth and stencil extension
		//Args: Target (GL_RENDERBUFFER_EXT), Extension (GL_DEPTH_STENCIL_EXT), Width, Height
		EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		//Adds the stencil attachment
		//Args: Target (GL_FRAMEBUFFER_EXT), Attachment (GL_STENCIL_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
		//Adds the depth attachment
		//Args: Target (GL_FRAMEBUFFER_EXT), Attachment (GL_DEPTH_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
	}
}
