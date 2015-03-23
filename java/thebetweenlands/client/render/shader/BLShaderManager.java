package thebetweenlands.client.render.shader;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.client.util.JsonException;

public class BLShaderManager extends ShaderManager {
	//This buffer holds the depth information gathered in DebugHandler#renderHand
	public Framebuffer depthBuffer;
	
	public BLShaderManager(IResourceManager resourceManager, String shaderName)
			throws JsonException {
		super(resourceManager, shaderName);
		System.out.println("New shader manager: " + shaderName);
		this.depthBuffer = new Framebuffer(Minecraft.getMinecraft().getFramebuffer().framebufferWidth, Minecraft.getMinecraft().getFramebuffer().framebufferHeight, true);
		System.out.println("Created depth buffer framebuffer");
	}

	@Override
	public void func_147995_c() {
		//System.out.println("Setting up samplers");
		super.func_147995_c();
	}
}
