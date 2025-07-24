package thebetweenlands.client.shader.postprocessing;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.io.IOException;

public class Starfield extends PostChain implements AutoCloseable {
	private final boolean faded;

	private float timeScale = 1.0F;
	private float zoom = 1.0F;
	private float offsetX = 0.0F;
	private float offsetY = 0.0F;
	private float offsetZ = 0.0F;

	private static final int STARFIELD_INDEX = 0;	// The Starfield pass index

	// Textures
	public RenderTarget starfieldTexture;

	// Uniforms
	@Nullable
	public Uniform timeUniform;
	@Nullable
	public Uniform timeScaleUniform;
	@Nullable
	public Uniform zoomUniform;
	@Nullable
	public Uniform offsetXUniform;
	@Nullable
	public Uniform offsetYUniform;
	@Nullable
	public Uniform offsetZUniform;

	public Starfield(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget, boolean faded, int width, int height) throws IOException, JsonSyntaxException {
		super(textureManager, resourceProvider, screenTarget, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, faded ? "shaders/post/starfield_faded.json" : "shaders/post/starfield.json"));

		/* 	PostChain structure:
		 * 		0 - 	Starfield shader 		= STARFIELD_INDEX
		*/

		// Get uniforms
		this.faded = faded;
		this.timeUniform = getUniform(STARFIELD_INDEX, "u_msTime");
		this.timeScaleUniform = getUniform(STARFIELD_INDEX, "u_timeScale");
		this.zoomUniform = getUniform(STARFIELD_INDEX, "u_zoom");
		this.offsetXUniform = getUniform(STARFIELD_INDEX, "u_offsetX");
		this.offsetYUniform = getUniform(STARFIELD_INDEX, "u_offsetY");
		this.offsetZUniform = getUniform(STARFIELD_INDEX, "u_offsetZ");

		// Get output target
		//this.addTempTarget("output", width, height);
		this.starfieldTexture = this.getTempTarget("output");
		this.starfieldTexture.resize(width, height, Minecraft.ON_OSX);
	}

	public Starfield setTimeScale(float timeScale) {
		this.timeScale = timeScale;
		return this;
	}

	public Starfield setZoom(float zoom) {
		this.zoom = zoom;
		return this;
	}

	public Starfield setOffset(float x, float y, float z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		return this;
	}

	protected void uploadUniforms(float partialTicks) {
		timeUniform.set(System.nanoTime() / 1000000.0F);
		timeScaleUniform.set(this.timeScale);
		zoomUniform.set(this.zoom);
		offsetXUniform.set(this.offsetX);
		offsetYUniform.set(this.offsetY);
		offsetZUniform.set(this.offsetZ);
	}

	/**
	 * Used to target a specific PostPass uniform value.
	 * @param index
	 * @param name
	 * @return uniform in PostPass index (index) with key of (name)
	 */
	public Uniform getUniform(int index, String name) {
		if (passes.isEmpty()) return null;
		return this.passes.get(index).getEffect().getUniform(name);
	}
}
