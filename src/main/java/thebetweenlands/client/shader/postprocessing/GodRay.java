package thebetweenlands.client.shader.postprocessing;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL14;
import thebetweenlands.common.TheBetweenlands;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.IntSupplier;

import static com.mojang.blaze3d.platform.GlConst.*;

public class GodRay extends PostChain implements AutoCloseable {
	private final RenderTarget occlusionTarget;
	private float godRayX = 0.5F;
	private float godRayY = 0.5F;

	private float exposure = 1.0F;
	private float decay = 1.0F;
	private float density = 1.0F;
	private float weight = 1.0F;
	private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	private float illuminationDecay = 1.0F;

	private static final int GODRAY_INDEX = 0;	// The GodRay pass index
	private static final int BLIT_INDEX = 1;

	// Textures
	public RenderTarget occlusionMap;

	@Nullable
	public Uniform godRayXUniform;
	@Nullable
	public Uniform godRayYUniform;
	@Nullable
	public Uniform exposureUniform;
	@Nullable
	public Uniform decayUniform;
	@Nullable
	public Uniform densityUniform;
	@Nullable
	public Uniform weightUniform;
	@Nullable
	public Uniform colorUniform;
	@Nullable
	public Uniform illuminationDecayUniform;

	GodRay(TextureManager textureManager, ResourceProvider resourceProvider, RenderTarget screenTarget, RenderTarget occlusionTarget) throws IOException, JsonSyntaxException {
		super(textureManager, resourceProvider, screenTarget, ResourceLocation.fromNamespaceAndPath(TheBetweenlands.ID, "shaders/post/godray.json"));
		this.occlusionTarget = occlusionTarget;

		// TODO: consider combining with OcclusionExtractor

		/* 	PostChain structure:
		 * 		0 - 	GodRay shader 		= STARFIELD_INDEX
		 * 		1 -	 	Blit to screen		= BLIT_INDEX
		 */

		this.godRayXUniform = getUniform(GODRAY_INDEX, "u_godRayX");
		this.godRayYUniform = getUniform(GODRAY_INDEX, "u_godRayY");
		this.exposureUniform = getUniform(GODRAY_INDEX, "u_exposure");
		this.decayUniform = getUniform(GODRAY_INDEX, "u_decay");
		this.densityUniform = getUniform(GODRAY_INDEX, "u_density");
		this.weightUniform = getUniform(GODRAY_INDEX, "u_weight");
		this.colorUniform = getUniform(GODRAY_INDEX, "u_color");
		this.illuminationDecayUniform = getUniform(GODRAY_INDEX, "u_illuminationDecay");

		PostPass godRayPass = this.passes.get(GODRAY_INDEX);
		godRayPass.auxAssets.set(0, (IntSupplier) () -> occlusionTarget.getColorTextureId());
	}

	public GodRay setOcclusionMap(RenderTarget setMap) {
		PostPass godRayPass = this.passes.get(GODRAY_INDEX);
		godRayPass.auxAssets.set(0, (IntSupplier) () -> setMap.getColorTextureId());
		return this;
	}

	public GodRay setRayPos(float x, float y) {
		this.godRayX = x;
		this.godRayY = y;
		return this;
	}

	public GodRay setParams(float exposure, float decay, float density, float weight, float illuminationDecay, Vector4f color) {
		this.exposure = exposure;
		this.decay = decay;
		this.density = density;
		this.weight = weight;
		this.color = color;
		this.illuminationDecay = illuminationDecay;
		return this;
	}

	public void uploadUniforms(float partialTicks) {
		this.godRayXUniform.set(this.godRayX);
		this.godRayYUniform.set(this.godRayY);
		this.exposureUniform.set(this.exposure);
		this.decayUniform.set(this.decay);
		this.densityUniform.set(this.density);
		this.weightUniform.set(this.weight);
		this.colorUniform.set(this.color);
		this.illuminationDecayUniform.set(this.illuminationDecay);
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
