package thebetweenlands.client.shader.postprocessing;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.RenderUtils;

import javax.annotation.Nullable;
import java.io.IOException;

public class Warp extends PostChain implements AutoCloseable {

	public static final ResourceLocation GAS_PARTICLE_TEXTURE = TheBetweenlands.prefix("textures/particle/gas_cloud.png");

	// Attributes
	private float scale = 40.0F;
	private float timeScale = 0.00004F;
	private float multipier = 3.55F;
	private float xOffset = 0.0F;
	private float yOffset = 0.0F;
	private float warpX = 1.0F;
	private float warpY = 1.0F;

	private static final int WARP_INDEX = 0;

	// Textures
	public RenderTarget BaseTexture;
	public RenderTarget gasTextureTarget;

	// Uniforms
	@Nullable
	public Uniform u_texel;
	@Nullable
	public Uniform u_mstime;
	@Nullable
	public Uniform u_scale;
	@Nullable
	public Uniform u_timeScale;
	@Nullable
	public Uniform u_multipier;
	@Nullable
	public Uniform u_xOffset;
	@Nullable
	public Uniform u_yOffset;
	@Nullable
	public Uniform u_warpX;
	@Nullable
	public Uniform u_warpY;

	public Warp(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget) throws IOException, JsonSyntaxException {
		super(textureManager, resourceProvider, screenTarget, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "shaders/post/warp.json"));

		/* 	PostChain structure:
		 * 		0 - 	Warp shader 		= WARP_INDEX
		 */

		// Get uniforms
		this.u_texel = this.getUniform(WARP_INDEX,"u_oneTexel");
		this.u_mstime = this.getUniform(WARP_INDEX,"u_msTime");
		this.u_scale = this.getUniform(WARP_INDEX,"u_scale");
		this.u_timeScale = this.getUniform(WARP_INDEX,"u_timeScale");
		this.u_multipier = this.getUniform(WARP_INDEX,"u_multiplier");
		this.u_xOffset = this.getUniform(WARP_INDEX,"u_xOffset");
		this.u_yOffset = this.getUniform(WARP_INDEX,"u_yOffset");
		this.u_warpX = this.getUniform(WARP_INDEX,"u_warpX");
		this.u_warpY = this.getUniform(WARP_INDEX,"u_warpY");

		//this.setSampler("s_diffuse", textureManager.getTexture(GAS_PARTICLE_TEXTURE).getId());

		// Get warp shader targets
		this.BaseTexture = this.getTempTarget("input");
		this.BaseTexture.setClearColor(1,1,1,1);
		this.BaseTexture.clear(false);
		this.gasTextureTarget = this.getTempTarget("output");
	}

	public Warp setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public Warp setTimeScale(float timeScale) {
		this.timeScale = timeScale;
		return this;
	}

	public Warp setMultiplier(float multiplier) {
		this.multipier = multiplier;
		return this;
	}

	public Warp setOffset(float xOffset, float yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		return this;
	}

	public Warp setWarpDir(float warpX, float warpY) {
		this.warpX = warpX;
		this.warpY = warpY;
		return this;
	}

	protected void uploadUniforms(float partialTicks) {
		//Update gas texture
		float worldTimeInterp = RenderUtils.getRenderTickCounter() + partialTicks;
		float offsetX = ((float) Math.sin((worldTimeInterp / 20.0F) % (Math.PI * 2.0D)) + 1.0F) / 600.0F;
		float offsetY = ((float) Math.cos((worldTimeInterp / 20.0F) % (Math.PI * 2.0D)) + 1.0F) / 600.0F;
		this.xOffset = offsetX;
		this.yOffset = offsetY;
		this.warpX = 0.75f;
		this.warpY = 0.75f;
		this.scale = 1.8F;

		u_mstime.set((float)System.nanoTime() / 1000000.0F);
		u_scale.set(this.scale);
		u_timeScale.set(this.timeScale);
		u_multipier.set(this.multipier);
		u_xOffset.set(this.xOffset);
		u_yOffset.set(this.yOffset);
		u_warpX.set(this.warpX);
		u_warpY.set(this.warpY);
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

	public void setSampler(String name, int renderTargetId) {
		for (PostPass postpass : this.passes) {
			postpass.getEffect().setSampler(name, () -> renderTargetId);
		}
	}
}