package thebetweenlands.client.handler;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import thebetweenlands.api.environment.fog.BiomeFogBetweenlands;
import thebetweenlands.api.environment.fog.BiomeFogMarsh;
import thebetweenlands.api.event.UpdateFogEvent;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.api.misc.Fog.MutableFog;
import thebetweenlands.api.misc.FogState;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.sky.BLSkyRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLFluidTagGenerator;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.FogGenerator;

import java.util.HashMap;
import java.util.Map;

public class FogHandler {
	private FogHandler() { }

	////// Biome specific fog + smooth transition //////
	private static FogState state = new FogState();
	private static float currentFogStart = -1.0F;
	private static float currentFogEnd = -1.0F;
	private static float farPlaneDistance = 0.0F;
	private static int fogMode;
	private static FogGenerator fogGenerator;

	// Static biome color fog color & range lists
	public static Map<ResourceKey<Biome>, BiomeFogBetweenlands> BIOME_FOGS = new HashMap<>();
	static {
		BIOME_FOGS.put(BiomeRegistry.COARSE_ISLANDS, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.DEEP_WATERS, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.MARSH, new BiomeFogMarsh());
		BIOME_FOGS.put(BiomeRegistry.ERODED_MARSH, new BiomeFogMarsh());
		BIOME_FOGS.put(BiomeRegistry.PATCHY_ISLANDS, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.RAISED_ISLES, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.SLUDGE_PLAINS, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.SLUDGE_PLAINS_CLEARING, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.SWAMPLANDS, new BiomeFogBetweenlands());
		BIOME_FOGS.put(BiomeRegistry.SWAMPLANDS_CLEARING, new BiomeFogBetweenlands());
	}

	/**
	 * Returns the fog state
	 * @return
	 */
	public static FogState getFogState() {
		return state;
	}

	/**
	 * Returns the current fog start
	 * @return
	 */
	public static float getCurrentFogStart() {
		return currentFogStart;
	}

	/**
	 * Returns the current fog end
	 * @return
	 */
	public static float getCurrentFogEnd() {
		return currentFogEnd;
	}

	/**
	 * Returns the current fog mode
	 * @return
	 */
	public static int getCurrentFogMode() {
		return fogMode;
	}

	/**
	 * Returns whether the "Dense Fog" event is active
	 * @return
	 */
	public static boolean hasDenseFog(Level level) {
		BLEnvironmentEventRegistry eeRegistry = WorldStorageGetter.getNullable(level).getEnvironmentEventRegistry();
		return eeRegistry.isEventActive(EnvironmentEventRegistry.DENSE_FOG.getId()) && Minecraft.getInstance().player.yo > TheBetweenlands.CAVE_START;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onFogRenderEvent(ViewportEvent.RenderFog event) {
		farPlaneDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
		Entity renderView = Minecraft.getInstance().cameraEntity;
		if (renderView != null && renderView.level().dimension() == DimensionRegistries.DIMENSION_KEY && !BLSkyRenderer.drawOverworldSky) {
			float partialTicks = (float) event.getPartialTick();
			Fog fog = state.getFog(partialTicks);
			Fog currentFog = state.getFog(1.0F);
			currentFogStart = currentFog.getStart();
			currentFogEnd = currentFog.getEnd();
			float fogStart = fog.getStart();
			float fogEnd = fog.getEnd();
			fogMode = fog.getGlFogType();
			event.setFogShape(FogShape.SPHERE);
			FogType type = event.getType();

			// Fog overrides
			BlockState blockState = Minecraft.getInstance().level.getBlockState(renderView.blockPosition());
			switch (type) {
				case NONE:	// Air
					event.setNearPlaneDistance(fogStart);
					event.setFarPlaneDistance(fogEnd);
					event.setCanceled(true);
					break;
				case WATER:
					// Approximated swamp water fog
					if (blockState.getFluidState().is(BLFluidTagGenerator.SWAMP_WATER)) {
						fogStart = -2;
						fogEnd = 7;
						event.setNearPlaneDistance(fogStart);
						event.setFarPlaneDistance(fogEnd);
						event.setCanceled(true);
						return;
					}
					// Use vanilla fog
					event.setCanceled(false);
					return;
				default:
					// Use vanilla fog
					event.setCanceled(false);
            }
		}
	}

	/**
	 * Tick fog state logic
	 * @param event
	 */
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		Player player = mc.player;

		if(level != null && player != null) {
			if(farPlaneDistance != 0.0F && level.dimension() == DimensionRegistries.DIMENSION_KEY) {
				state.update(level, player.getPosition(mc.getTimer().getGameTimeDeltaTicks()).add(0, player.getEyeHeight(), 0), farPlaneDistance, 0);
			}

			BiomeFogBetweenlands biomefog = BIOME_FOGS.get(level.getBiome(player.blockPosition()).getKey());
			if(biomefog != null) {
				biomefog.updateFog();
			}
		}
	}

	////// Underwater fog fix & Dark fog in caves //////
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onFogColor(ViewportEvent.ComputeFogColor event) {
		Minecraft mc = Minecraft.getInstance();
		Entity renderView = mc.cameraEntity;
		Camera camera = mc.gameRenderer.getMainCamera();
		ClientLevel level = mc.level;
		if(renderView != null && !BLSkyRenderer.drawOverworldSky) {
			BlockState blockState = mc.level.getBlockState(renderView.blockPosition());
			Fog fog = state.getFog((float)event.getPartialTick());
			float fogColorMultiplier = fog.getColorMultiplier();
			if(camera.getFluidInCamera() == FogType.WATER && blockState.getFluidState().is(BLFluidTagGenerator.SWAMP_WATER)) {
				BlockPos pos = renderView.blockPosition();
				int colorMultiplier = level.getBlockTint(pos, BiomeColors.WATER_COLOR_RESOLVER);//getBiome(pos).value().getWaterColor();
				if(level.dimension() == DimensionRegistries.DIMENSION_KEY) {
					double waterFogColorMultiplier = fogColorMultiplier / 2.0F;
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F * (float)waterFogColorMultiplier);
				} else {
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F);
				}
			} else if(level.dimension() == DimensionRegistries.DIMENSION_KEY) {
				Vec3 fogColor = level.effects().getBrightnessDependentFogColor(new Vec3(event.getRed(), event.getGreen(), event.getBlue()), 0.0f);
				event.setRed((float)fogColor.x);
				event.setGreen((float)fogColor.y);
				event.setBlue((float)fogColor.z);
			}
		}
	}

	// Keep for 1.21.2+
	/*
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onFogDensity(FogDensity event) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null) {
			Block block = ActiveRenderInfo.getBlockStateAtEntityViewpoint(renderView.world, renderView, (float) event.getRenderPartialTicks()).getBlock();
			if(block instanceof BlockSwampWater) {
				fogMode = GL11.GL_EXP;
				GlStateManager.setFog(FogMode.EXP);
				if (renderView instanceof EntityLivingBase && ((EntityLivingBase)renderView).isPotionActive(MobEffects.WATER_BREATHING/*Water breathing*//*)) {
					event.setDensity(0.1F);
				} else {
					event.setDensity(0.4F);
				}
				event.setCanceled(true);
			}
		}
	}
	*/

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void updateFog(UpdateFogEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Vec3 position = event.getPosition();
		Level level = event.getWorld();
		Player player = mc.player;
		FogState state = event.getFogState();
		Fog biomeFog = event.getBiomeFog();
		MutableFog fog = new MutableFog(event.getAmbientFog());

		LocationAmbience ambience = LocationStorage.getAmbience(level, position);

		if(ambience != null && !ambience.hasCaveFog()) {
			fog.setStart(biomeFog.getStart());
			fog.setEnd(biomeFog.getEnd());
		}

		float fogBrightness = 0;

		float uncloudedStrength = 0.0F;
		if(player.hasEffect(ElixirEffectRegistry.EFFECT_UNCLOUDED.get().getElixirEffect())) {
			uncloudedStrength += Math.min((ElixirEffectRegistry.EFFECT_UNCLOUDED.get().getStrength(player) + 1) / 3.0F, 1.0F);
		}

		if(player.hasEffect(ElixirEffectRegistry.EFFECT_FOGGEDMIND.get().getElixirEffect())) {
			float additionalFogStrength = (ElixirEffectRegistry.EFFECT_FOGGEDMIND.get().getStrength(player) + 1) * 0.85F;
			fog.setStart(fog.getStart() / (additionalFogStrength * 2.0F));
			fog.setEnd((fog.getEnd() / additionalFogStrength));
		}

		if(hasDenseFog(level)) {
			if(fogGenerator == null) { //|| fogGenerator.getSeed() != mc.level.getSeed()) {
				fogGenerator = new FogGenerator(0);
			}
			float lowViewDistanceFogReduction = biomeFog.getEnd() > 64 ? 1.0F : (64.0F - biomeFog.getEnd()) / 64.0F;
			float[] range = fogGenerator.getFogRange(0.2F, 1.0F);
			float denseFogStart = state.getFixedFogStart(biomeFog.getStart()) / Math.max(8.0f / (1.0F + uncloudedStrength * 4.0F) * lowViewDistanceFogReduction, 1) * range[0];
			float denseFogEnd = state.getFixedFogEnd(biomeFog.getEnd()) / Math.max(3.0f/ (1.0F + uncloudedStrength * 2.0F) * lowViewDistanceFogReduction, 1) * range[1];

			fog.setStart(Math.min(fog.getStart(), -2));
			fog.setEnd(Math.min(fog.getEnd(), denseFogEnd));

			final int transitionStart = TheBetweenlands.CAVE_START;
			final int transitionEnd = TheBetweenlands.CAVE_START - 15;
			float y = (float) event.getPosition().y;

			if (y < transitionStart) {
				if (transitionEnd < y) {
					fogBrightness = (y - transitionEnd) / (transitionStart - transitionEnd) * 80;
				}
			} else {
				fogBrightness = 80;
			}
		}

		BetweenlandsWorldStorage worldStorage = WorldStorageGetter.getNullable(level);
		double closestSpiritTree = -1;
		for(ILocalStorage storage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
			if(storage instanceof LocationStorage && ((LocationStorage)storage).getType() == EnumLocationType.SPIRIT_TREE) {
				double dist = position.distanceTo(storage.getBoundingBox().getCenter());
				if(dist < 75) {
					if(closestSpiritTree < 0 || dist < closestSpiritTree) {
						closestSpiritTree = dist;
					}
				}
			}
		}
		if(closestSpiritTree >= 0) {
			float strength = 1.0F - (float)Math.max(0, (closestSpiritTree - 16) / (75.0F - 16));
			fog.setStart(fog.getStart() * (1 - strength));
			fog.setEnd(fog.getEnd() + (40 - fog.getEnd()) * strength);
			fog.setRed(fog.getRed() + (0.58F - fog.getRed()) * strength).setGreen(fog.getGreen() + (0.58F - fog.getGreen()) * strength).setBlue(fog.getBlue() + (0.58F - fog.getBlue()) * strength);
			fog.setDistanceIncrementMultiplier(4.0F);
		}

		if(ambience != null) {
			if(ambience.hasFogBrightness()) {
				fogBrightness = ambience.getFogBrightness();
			}

			if(ambience.hasFogColor()) {
				int[] color = ambience.getFogColor();
				fog.setRed(color[0] / 255.0F).setGreen(color[1] / 255.0F).setBlue(color[2] / 255.0F);
			}

			if(ambience.hasFogColorMultiplier()) {
				fog.setColorMultiplier(ambience.getFogColorMultiplier());
			}

			if(ambience.hasFogRange()) {
				fog.setStart(ambience.getFogStart());
				fog.setEnd(ambience.getFogEnd());
			}

			if(ambience.hasFogRangeMultiplier()) {
				fog.setStart(fog.getStart() * ambience.getFogRangeMultiplier());
				fog.setStart(fog.getEnd() * ambience.getFogRangeMultiplier());
			}
		}

		BLEnvironmentEventRegistry reg = WorldStorageGetter.getNullable(level).getEnvironmentEventRegistry();

		if(reg.isEventActive(EnvironmentEventRegistry.BLOOD_SKY.getId())) {
			if(!ShaderHelper.INSTANCE.isWorldShaderActive()) {
				fog.setRed(0.74F).setGreen(0.18F).setBlue(0.08F);
			} else {
				fogBrightness = 0;
			}
		} else if(reg.isEventActive(EnvironmentEventRegistry.SPOOPY.getId())) {
			if(!ShaderHelper.INSTANCE.isWorldShaderActive()) {
				fog.setRed(0.4F).setGreen(0.22F).setBlue(0.08F);
			} else {
				fogBrightness = 0;
			}
		}

		float[] color = new float[] { fog.getRed(), fog.getGreen(), fog.getBlue() };
		for(int i = 0; i < 3; i++) {
			float diff = 1.0F - color[i];
			color[i] = color[i] + (diff * fogBrightness / 255.0F);
		}
		fog.setRed(color[0]).setGreen(color[1]).setBlue(color[2]);

		state.setTargetFog(fog);
	}
}