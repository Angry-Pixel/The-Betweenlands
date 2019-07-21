package thebetweenlands.client.handler;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.FogMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.event.UpdateFogEvent;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.api.misc.Fog.MutableFog;
import thebetweenlands.api.misc.FogState;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.FogGenerator;

public class FogHandler {
	private FogHandler() { }

	////// Biome specific fog + smooth transition //////
	private static FogState state = new FogState();
	private static float currentFogStart = -1.0F;
	private static float currentFogEnd = -1.0F;
	private static float farPlaneDistance = 0.0F;
	private static int fogMode;
	private static FogGenerator fogGenerator;

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
	public static boolean hasDenseFog(World world) {
		BLEnvironmentEventRegistry eeRegistry = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry();
		return eeRegistry.denseFog.isActive() && Minecraft.getMinecraft().player.posY > WorldProviderBetweenlands.CAVE_START;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogRenderEvent(RenderFogEvent event) {
		farPlaneDistance = event.getFarPlaneDistance();
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null && renderView.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			float partialTicks = (float) event.getRenderPartialTicks();
			Fog fog = state.getFog(partialTicks);
			Fog currentFog = state.getFog(1.0F);
			currentFogStart = currentFog.getStart();
			currentFogEnd = currentFog.getEnd();
			float fogStart = fog.getStart();
			float fogEnd = fog.getEnd();
			fogMode = fog.getGlFogType();
			switch(fog.getFogType()) {
			default:
			case LINEAR:
				GlStateManager.setFog(FogMode.LINEAR);
				break;
			case EXP:
				GlStateManager.setFog(FogMode.EXP);
				GlStateManager.setFogDensity(fog.getDensity());
				break;
			case EXP2:
				GlStateManager.setFog(FogMode.EXP2);
				GlStateManager.setFogDensity(fog.getDensity());
				break;
			}
			GlStateManager.setFogStart(fogStart);
			GlStateManager.setFogEnd(fogEnd);
		} else {
			currentFogStart = farPlaneDistance * 0.75F;
			currentFogEnd = farPlaneDistance;
			fogMode = GL11.GL_LINEAR;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		World world = TheBetweenlands.proxy.getClientWorld();
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();

		if(world != null && player != null) {
			if(farPlaneDistance != 0.0F && world.provider instanceof WorldProviderBetweenlands) {
				state.update(world, player.getPositionVector().add(0, player.getEyeHeight(), 0), farPlaneDistance, 0);
			}

			Biome biome = world.getBiome(player.getPosition());
			if(biome instanceof BiomeBetweenlands) {
				((BiomeBetweenlands)biome).updateFog();
			}
		}
	}

	////// Underwater fog fix & Dark fog in caves //////
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogColor(FogColors event) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null) {
			IBlockState blockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(renderView.world, renderView, (float) event.getRenderPartialTicks());
			Fog fog = state.getFog((float)event.getRenderPartialTicks());
			float fogColorMultiplier = fog.getColorMultiplier();
			if(blockState.getBlock() instanceof BlockSwampWater) {
				BlockPos pos = new BlockPos(ActiveRenderInfo.projectViewFromEntity(renderView, (float) event.getRenderPartialTicks()));
				int colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(blockState, renderView.world, pos, 0);
				if(renderView.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
					double waterFogColorMultiplier = fogColorMultiplier / 2.0F;
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F * (float)waterFogColorMultiplier);
				} else {
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F);
				}
			} else if(renderView.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands) renderView.getEntityWorld().provider;
				Vec3d fogColor = provider.getFogColor(renderView.getEntityWorld().getCelestialAngle((float)event.getRenderPartialTicks()), (float)event.getRenderPartialTicks());
				event.setRed((float)fogColor.x);
				event.setGreen((float)fogColor.y);
				event.setBlue((float)fogColor.z);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogDensity(FogDensity event) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null) {
			Block block = ActiveRenderInfo.getBlockStateAtEntityViewpoint(renderView.world, renderView, (float) event.getRenderPartialTicks()).getBlock();
			if(block instanceof BlockSwampWater) {
				fogMode = GL11.GL_EXP;
				GlStateManager.setFog(FogMode.EXP);
				if (renderView instanceof EntityLivingBase && ((EntityLivingBase)renderView).isPotionActive(MobEffects.WATER_BREATHING/*Water breathing*/)) {
					event.setDensity(0.1F);
				} else {
					event.setDensity(0.4F);
				}
				event.setCanceled(true);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void updateFog(UpdateFogEvent event) {
		Vec3d position = event.getPosition();
		World world = event.getWorld();
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		FogState state = event.getFogState();
		Fog biomeFog = event.getBiomeFog();
		MutableFog fog = new MutableFog(event.getAmbientFog());

		LocationAmbience ambience = LocationStorage.getAmbience(world, position);
		
		if(ambience != null && !ambience.hasCaveFog()) {
			fog.setStart(biomeFog.getStart());
			fog.setEnd(biomeFog.getEnd());
		}
		
		float fogBrightness = 0;

		float uncloudedStrength = 0.0F;
		if(ElixirEffectRegistry.EFFECT_UNCLOUDED.isActive(player)) {
			uncloudedStrength += Math.min((ElixirEffectRegistry.EFFECT_UNCLOUDED.getStrength(player) + 1) / 3.0F, 1.0F);
		}

		if(ElixirEffectRegistry.EFFECT_FOGGEDMIND.isActive(player)) {
			float additionalFogStrength = (ElixirEffectRegistry.EFFECT_FOGGEDMIND.getStrength(player) + 1) * 0.85F;
			fog.setStart(fog.getStart() / (additionalFogStrength * 2.0F));
			fog.setEnd((fog.getEnd() / additionalFogStrength));
		}

		if(hasDenseFog(world)) {
			if(fogGenerator == null || fogGenerator.getSeed() != Minecraft.getMinecraft().world.getSeed()) {
				fogGenerator = new FogGenerator(Minecraft.getMinecraft().world.getSeed());
			}
			float lowViewDistanceFogReduction = biomeFog.getEnd() > 64 ? 1.0F : (64.0F - biomeFog.getEnd()) / 64.0F;
			float[] range = fogGenerator.getFogRange(0.2F, 1.0F);
			float denseFogStart = state.getFixedFogStart(biomeFog.getStart()) / Math.max(8.0f / (1.0F + uncloudedStrength * 4.0F) * lowViewDistanceFogReduction, 1) * range[0];
			float denseFogEnd = state.getFixedFogEnd(biomeFog.getEnd()) / Math.max(3.0f/ (1.0F + uncloudedStrength * 2.0F) * lowViewDistanceFogReduction, 1) * range[1];

			fog.setStart(Math.min(fog.getStart(), denseFogStart));
			fog.setEnd(Math.min(fog.getEnd(), denseFogEnd));

			final int transitionStart = WorldProviderBetweenlands.CAVE_START;
			final int transitionEnd = WorldProviderBetweenlands.CAVE_START - 15;
			float y = (float) event.getPosition().y;

			if (y < transitionStart) {
				if (transitionEnd < y) {
					fogBrightness = (y - transitionEnd) / (transitionStart - transitionEnd) * 80;
				}
			} else {
				fogBrightness = 80;
			}
		}

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
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

		WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
		BLEnvironmentEventRegistry reg = provider.getEnvironmentEventRegistry();

		if(reg.bloodSky.isActive()) {
			if(!ShaderHelper.INSTANCE.isWorldShaderActive()) {
				fog.setRed(0.74F).setGreen(0.18F).setBlue(0.08F);
			} else {
				fogBrightness = 0;
			}
		} else if(reg.spoopy.isActive()) {
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
