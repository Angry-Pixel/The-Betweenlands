package thebetweenlands.client.render.shader;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.client.util.JsonException;

public class CShaderManager extends ShaderManager {
	public CShaderManager(IResourceManager resourceManager, String shaderName)
			throws JsonException {
		super(resourceManager, shaderName);
	}

	//Setting up samplers
	@Override
	public void func_147995_c() {
		super.func_147995_c();
	}
}
