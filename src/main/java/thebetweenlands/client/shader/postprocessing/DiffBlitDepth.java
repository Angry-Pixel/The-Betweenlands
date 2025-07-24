package thebetweenlands.client.shader.postprocessing;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import thebetweenlands.common.TheBetweenlands;

import java.io.IOException;

public class DiffBlitDepth extends PostChain implements AutoCloseable {

	// Input buffers
	public RenderTarget BeforeTarget;
	public RenderTarget AfterTarget;
	public RenderTarget Output;
	public RenderTarget Base;

	public DiffBlitDepth(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget) throws IOException, JsonSyntaxException {
		super(textureManager, resourceProvider, screenTarget, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "shaders/post/diffblitdepth.json"));

		this.BeforeTarget = this.getTempTarget("beforeDepth");
		this.AfterTarget = this.getTempTarget("afterDepth");
		this.Output = this.getTempTarget("output");
		this.Base = this.getTempTarget("base");
	}
}