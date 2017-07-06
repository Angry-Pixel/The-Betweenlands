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
import net.minecraft.potion.Potion;
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
import thebetweenlands.api.misc.FogState;
import thebetweenlands.api.misc.Fog.MutableFog;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.world.shared.location.LocationAmbience;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;
import thebetweenlands.util.FogGenerator;
import thebetweenlands.util.config.ConfigHandler;

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
	public static boolean hasDenseFog() {
		World world = Minecraft.getMinecraft().world;
		if(world.provider instanceof WorldProviderBetweenlands && Minecraft.getMinecraft().player.posY > WorldProviderBetweenlands.CAVE_START) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
			if(eeRegistry.DENSE_FOG.isActive()) {
				return true;
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogRenderEvent(RenderFogEvent event) {
		farPlaneDistance = event.getFarPlaneDistance();
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null && renderView.dimension == ConfigHandler.dimensionId) {
			float partialTicks = (float) event.getRenderPartialTicks();
			Fog fog = state.getFog(partialTicks);
			float fogStart = currentFogStart = fog.getStart();
			float fogEnd = currentFogEnd = fog.getEnd();
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
			if(farPlaneDistance != 0.0F && player.dimension == ConfigHandler.dimensionId) {
				state.update(world, player.getPositionVector().addVector(0, player.getEyeHeight(), 0), farPlaneDistance, 0);
			}

			Biome biome = world.getBiomeForCoordsBody(player.getPosition());
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
				if(renderView.dimension == ConfigHandler.dimensionId) {
					double waterFogColorMultiplier = fogColorMultiplier / 2.0F;
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F * (float)waterFogColorMultiplier);
				} else {
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F);
				}
			} else if(renderView.dimension == ConfigHandler.dimensionId) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands) renderView.getEntityWorld().provider;
				Vec3d fogColor = provider.getFogColor(renderView.getEntityWorld().getCelestialAngle((float)event.getRenderPartialTicks()), (float)event.getRenderPartialTicks());
				event.setRed((float)fogColor.xCoord);
				event.setGreen((float)fogColor.yCoord);
				event.setBlue((float)fogColor.zCoord);
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
				if (renderView instanceof EntityLivingBase && ((EntityLivingBase)renderView).isPotionActive(Potion.getPotionById(13)/*Water breathing*/)) {
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
		FogState state = event.getFogState();
		Fog biomeFog = event.getBiomeFog();
		MutableFog fog = new MutableFog(event.getAmbientFog());

		float fogBrightness = 0;

		if(hasDenseFog()) {
			if(fogGenerator == null || fogGenerator.getSeed() != Minecraft.getMinecraft().world.getSeed()) {
				fogGenerator = new FogGenerator(Minecraft.getMinecraft().world.getSeed());
			}
			float lowViewDistanceFogReduction = state.getLowDistanceFogReduction(biomeFog.getEnd());
			float[] range = fogGenerator.getFogRange(0.2F, 1.0F);
			float denseFogStart = 0.0F;//state.getFixedFogStart(biomeFog.getStart()) / Math.max(8.0f * lowViewDistanceFogReduction, 1) * range[0];
			float denseFogEnd = state.getFixedFogEnd(biomeFog.getEnd()) / Math.max(3.0f * lowViewDistanceFogReduction, 1) * range[1];

			fog.setStart(Math.min(fog.getStart(), denseFogStart));
			fog.setEnd(Math.min(fog.getEnd(), denseFogEnd));

			final int transitionStart = WorldProviderBetweenlands.CAVE_START;
			final int transitionEnd = WorldProviderBetweenlands.CAVE_START - 15;
			float y = (float) event.getPosition().yCoord;

			if (y < transitionStart) {
				if (transitionEnd < y) {
					fogBrightness = (y - transitionEnd) / (transitionStart - transitionEnd) * 80;
				}
			} else {
				fogBrightness = 80;
			}
		}

		LocationAmbience ambience = LocationStorage.getAmbience(world, position);

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

		if(WorldProviderBetweenlands.getProvider(world).getEnvironmentEventRegistry().BLOODSKY.isActive()) {
			if(!ShaderHelper.INSTANCE.isWorldShaderActive()) {
				fog.setRed(0.74F).setGreen(0.18F).setBlue(0.08F);
			} else {
				fogBrightness = 0;
			}
		} else if(WorldProviderBetweenlands.getProvider(world).getEnvironmentEventRegistry().SPOOPY.isActive()) {
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
