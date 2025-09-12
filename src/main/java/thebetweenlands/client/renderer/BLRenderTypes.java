package thebetweenlands.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.event.BetweenlandsShaders;

public class BLRenderTypes extends RenderType {

	public static final RenderStateShard.TransparencyStateShard EYE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
		"eye_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	public static final RenderStateShard.TransparencyStateShard WIGHT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
		"wight_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	public static final RenderType DRUID_CONE = create(
		"thebetweenlands:druid_cone",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.TRIANGLE_FAN,
		1536,
		false,
		false,
		RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_LIGHTNING_SHADER)
			.setCullState(CULL)
			.setWriteMaskState(COLOR_WRITE)
			.setTransparencyState(LIGHTNING_TRANSPARENCY)
			.createCompositeState(false)
	);

	public static RenderType animatedLayer(ResourceLocation location, float u, float v) {
		return create(
			"thebetweenlands:animated",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_BREEZE_WIND_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false)
		);
	}

	public static RenderType primordialShield(ResourceLocation location, float u, float v, boolean depthMask) {
		return create(
			"thebetweenlands:primordial_shield",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.TRIANGLES,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(new ShaderStateShard(() -> BetweenlandsShaders.PRIMORDIAL_SHIELD))
				.setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
				.setTransparencyState(LIGHTNING_TRANSPARENCY)
				.setWriteMaskState(depthMask ? COLOR_DEPTH_WRITE : COLOR_WRITE)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false)
		);
	}

	public static RenderType primordialShieldFiller() {
		return create(
			"thebetweenlands:primordial_shield_filler",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.TRIANGLES,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
				.setTransparencyState(LIGHTNING_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false)
		);
	}

	public static RenderType translucentCulling(ResourceLocation location) {
		return create(
			"thebetweenlands:translucent_culling",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			1536,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
				.setTransparencyState(WIGHT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setWriteMaskState(DEPTH_WRITE)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));
	}

	public BLRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType druidCone() {
		return DRUID_CONE;
	}
}
