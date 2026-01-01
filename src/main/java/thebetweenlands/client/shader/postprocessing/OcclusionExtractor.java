package thebetweenlands.client.shader.postprocessing;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import thebetweenlands.common.TheBetweenlands;

import java.io.IOException;

import static com.mojang.blaze3d.platform.GlConst.*;

public class OcclusionExtractor extends PostChain implements AutoCloseable {

	// FBOs
	public RenderTarget worldDepth;
	public RenderTarget clipPlaneDepth;
	public RenderTarget occlusionOut;

	OcclusionExtractor(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget) throws IOException, JsonSyntaxException {
		super(textureManager, resourceProvider, screenTarget, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "shaders/post/occlusionextractor.json"));

		this.worldDepth = this.getTempTarget("s_world_depth");
		this.clipPlaneDepth = this.getTempTarget("s_clipPlane_depth");
		this.occlusionOut = this.getTempTarget("output");
	}

	public void setDepthTextures(RenderTarget worldDepth, RenderTarget clipPlaneDepth) {
		this.worldDepth.copyDepthFrom(worldDepth);
		this.clipPlaneDepth.copyDepthFrom(clipPlaneDepth);
	}
}
