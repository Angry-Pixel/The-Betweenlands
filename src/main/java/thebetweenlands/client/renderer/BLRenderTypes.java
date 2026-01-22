package thebetweenlands.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.event.BetweenlandsShaders;
import thebetweenlands.client.model.block.DungeonDoorRunesModel;

import java.util.OptionalDouble;
import java.util.function.Function;

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

	//DEBUG_LINE_STRIP but with transparency support
	public static final Function<Double, RenderType.CompositeRenderType> EQUIPMENT_LINES = Util.memoize(
		p_286162_ -> create(
			"thebetweenlands:equipment_lines",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.DEBUG_LINE_STRIP,
			1536,
			RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(p_286162_)))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.createCompositeState(false)
		)
	);

	public static RenderType equipmentLines(double width) {
		return EQUIPMENT_LINES.apply(width);
	}

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

	public static RenderType pitChains(ResourceLocation location, float u, float v) {
		return create(
			"thebetweenlands:pit_chains",
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
				.setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
				.setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
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

	// Creates a new dungeon door rune RenderType, without memoizing it
	// However, memoized is preferred because there should only around 8 rune textures
	@Deprecated
	public static RenderType dungeonDoorRunesUnmemoized(ResourceLocation texture) {
		return create(
				"thebetweenlands:dungeon_door_runes",
				BLVertexFormats.DUNGEON_DOOR_RUNE,
				VertexFormat.Mode.QUADS,
				RenderType.TRANSIENT_BUFFER_SIZE,
				true,
				false, 
				RenderType.CompositeState.builder()
					.setShaderState(new ShaderStateShard(() -> BetweenlandsShaders.DUNGEON_DOOR_RUNES))
					.setTextureState(
							MultiTextureStateShard.builder()
								.add(DungeonDoorRunesModel.TEXTURE_RUNE_GLOW, false, false) // Rune glow is texture 0
								.add(texture, false, false) // Mask texture is texture 1
								.build()
						)
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setCullState(NO_CULL)
					.setLightmapState(NO_LIGHTMAP)
					.setLayeringState(POLYGON_OFFSET_LAYERING)
					.setOverlayState(NO_OVERLAY)
					.createCompositeState(false) // false because no outline
			);
	}
	
	// We can memoize this one, it should only have 8 or so values
	public static final Function<ResourceLocation, RenderType> DUNGEON_DOOR_RUNES = Util.memoize(BLRenderTypes::dungeonDoorRunesUnmemoized);
	
	public static RenderType dungeonDoorRunes(ResourceLocation maskTexture) {
		return DUNGEON_DOOR_RUNES.apply(maskTexture);
	}
	
	public BLRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType druidCone() {
		return DRUID_CONE;
	}
}
