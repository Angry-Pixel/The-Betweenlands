package thebetweenlands.client.event.handler;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.BlockSwampWater;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationStorage;
import thebetweenlands.util.config.ConfigHandler;

public class FogHandler {
	private FogHandler() { }

	////// Biome specific fog + smooth transition //////
	private static float currentFogStart = -1.0F;
	private static float currentFogEnd = -1.0F;
	private static float lastFogStart = -1.0F;
	private static float lastFogEnd = -1.0F;
	private static float currentFogColorMultiplier = -1.0F;
	private static float lastFogColorMultiplier = -1.0F;
	private static float farPlaneDistance = 0.0F;
	private static int fogMode;

	public static float getCurrentFogStart() {
		return currentFogStart;
	}

	public static float getCurrentFogEnd() {
		return currentFogEnd;
	}

	public static int getCurrentFogMode() {
		return fogMode;
	}

	public static boolean hasDenseFog() {
		World world = Minecraft.getMinecraft().theWorld;
		if(world.provider instanceof WorldProviderBetweenlands && Minecraft.getMinecraft().thePlayer.posY > WorldProviderBetweenlands.CAVE_START) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
			boolean denseFog = false;
			if((!ConfigHandler.debug && eeRegistry.DENSE_FOG.isActive())/* ||
					(DebugHandlerClient.INSTANCE.denseFog && ConfigHandler.DEBUG && !eeRegistry.DENSE_FOG.isActive()) ||
					(!DebugHandlerClient.INSTANCE.denseFog && ConfigHandler.DEBUG && eeRegistry.DENSE_FOG.isActive())*/) {
				denseFog = true;
			}
			return denseFog;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogRenderEvent(RenderFogEvent event) {
		farPlaneDistance = event.getFarPlaneDistance();
		fogMode = event.getFogMode();
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null && renderView.dimension == ConfigHandler.dimensionId) {
			float partialTicks = (float) event.getRenderPartialTicks();
			float fogStart = currentFogStart + (currentFogStart - lastFogStart) * partialTicks;
			float fogEnd = currentFogEnd + (currentFogEnd - lastFogEnd) * partialTicks;
			fogMode = GL11.GL_LINEAR;
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, fogStart);
			GL11.glFogf(GL11.GL_FOG_END, fogEnd);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		World world = TheBetweenlands.proxy.getClientWorld();
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();

		if(world == null || player == null || farPlaneDistance == 0.0F || player.dimension != ConfigHandler.dimensionId) return;

		if(world.provider instanceof WorldProviderBetweenlands) {
			((WorldProviderBetweenlands)world.provider).updateFogColors();
		}

		Biome biome = world.getBiomeGenForCoords(player.getPosition());

		float fogStart = farPlaneDistance * 0.75F;
		float fogEnd = farPlaneDistance;

		//Use the same values regardless the view distance for fairness
		float defaultFogStart = Math.min(192.0F * 0.75F, fogStart);
		float defaultFogEnd = Math.min(192.0F, fogEnd);

		if(biome instanceof BiomeBetweenlands) {
			BiomeBetweenlands biomeBetweenlands = (BiomeBetweenlands) biome;
			fogStart = biomeBetweenlands.getFogStart(farPlaneDistance, 0);
			fogEnd = biomeBetweenlands.getFogEnd(farPlaneDistance, 0);
		}

		//TODO: Elixirs
		float uncloudedStrength = 0.0F;
		/*if(ElixirEffectRegistry.EFFECT_UNCLOUDED.isActive(player)) {
			uncloudedStrength += Math.min((ElixirEffectRegistry.EFFECT_UNCLOUDED.getStrength(player) + 1) / 3.0F, 1.0F);
		}

		if(ElixirEffectRegistry.EFFECT_FOGGEDMIND.isActive(player)) {
			float additionalFogStrength = (ElixirEffectRegistry.EFFECT_FOGGEDMIND.getStrength(player) + 1) * 0.85F;
			fogStart /= additionalFogStrength * 2.0F;
			fogEnd /= additionalFogStrength;
		}*/

		//Reduced fog for those players with really low view distance
		float lowViewDistanceFogReduction = fogEnd > 64 ? 1.0F : (64.0F - fogEnd) / 64.0F;

		//Dense fog
		if(hasDenseFog()) {
			defaultFogStart = fogStart = defaultFogStart / Math.max(5.0f / (1.0F + uncloudedStrength * 4.0F) * lowViewDistanceFogReduction, 1);
			defaultFogEnd = fogEnd = defaultFogEnd / Math.max(3.0f / (1.0F + uncloudedStrength * 2.0F) * lowViewDistanceFogReduction, 1);
		}

		//Underground fog
		float multiplier = 1.0F;
		if(player.posY < WorldProviderBetweenlands.CAVE_START) {
			multiplier = ((float)(WorldProviderBetweenlands.CAVE_START - player.posY) / WorldProviderBetweenlands.CAVE_START);
			multiplier = 1.0F - multiplier;
			multiplier *= Math.pow(multiplier, 8.5);
			multiplier = multiplier * 0.95F + 0.05F;
			if(player.posY <= WorldProviderBetweenlands.PITSTONE_HEIGHT) {
				float targettedMultiplier = 0.3F;
				if(multiplier < targettedMultiplier) {
					multiplier += Math.pow(((targettedMultiplier - multiplier) / WorldProviderBetweenlands.PITSTONE_HEIGHT * (WorldProviderBetweenlands.PITSTONE_HEIGHT - player.posY)), 0.85F);
				}
			}
			multiplier = MathHelper.clamp_float(multiplier, 0, 1);
			multiplier = Math.min(multiplier / (float)Math.pow(lowViewDistanceFogReduction, 1.0F + (1.0F - multiplier) * 1.5F), 1.0F);
			defaultFogStart = fogStart = defaultFogStart * Math.min(multiplier * (1.0F + uncloudedStrength * (1.0F / multiplier - 1.0F)), 1.0F);
			defaultFogEnd = fogEnd = defaultFogEnd * Math.min((multiplier * 1.5F) * (1.0F + uncloudedStrength * (1.0F / (multiplier * 1.5F) - 1.0F)), 1.0F);
		}

		//Location fog
		LocationAmbience ambience = LocationStorage.getAmbience(player);
		if(ambience != null) {
			if(ambience.hasFogRange()) {
				fogStart = ambience.getFogStart();
				fogEnd = ambience.getFogEnd();
			}
			if(ambience.hasFogRangeMultiplier()) {
				float rangeMultiplier = ambience.getFogRangeMultiplier();
				if(rangeMultiplier < 1.0F)
					rangeMultiplier = Math.min(rangeMultiplier / (float)Math.pow(lowViewDistanceFogReduction, 2.0F - rangeMultiplier), 1.0F);
				fogStart *= ambience.getFogRangeMultiplier();
				fogEnd *= ambience.getFogRangeMultiplier();
			}
			if(ambience.hasFogColorMultiplier()) {
				multiplier = ambience.getFogColorMultiplier();
			}
		}


		fogEnd = MathHelper.clamp_float(fogEnd, 3, farPlaneDistance);
		fogStart = MathHelper.clamp_float(fogStart, 1, fogEnd);

		if(currentFogStart < 0.0F || currentFogEnd < 0.0F) {
			currentFogStart = fogStart;
			currentFogEnd = fogEnd;
		}

		float fogDistIncrMultiplier = player.posY <= WorldProviderBetweenlands.CAVE_START ? 2.0F : 1.0F;
		lastFogStart = currentFogStart;
		lastFogEnd = currentFogEnd;
		if(Math.abs(currentFogStart - fogStart) > fogDistIncrMultiplier) {
			float currentFogStartIncr = Math.abs(currentFogStart - fogStart)/farPlaneDistance/2.0f*fogDistIncrMultiplier;
			if(currentFogStart > fogStart) {
				currentFogStart-=currentFogStartIncr;
			} else if(currentFogStart < fogStart) {
				currentFogStart+=currentFogStartIncr;
			}
		}
		if(Math.abs(currentFogEnd - fogEnd) > fogDistIncrMultiplier) {
			float currentFogEndIncr = Math.abs(currentFogEnd - fogEnd)/farPlaneDistance/2.0f*fogDistIncrMultiplier;
			if(currentFogEnd > fogEnd) {
				currentFogEnd-=currentFogEndIncr;
			} else if(currentFogEnd < fogEnd) {
				currentFogEnd+=currentFogEndIncr;
			}
		}

		float targettedFogColorMultiplier = MathHelper.clamp_float(multiplier * 2.0F, 0.0F, 1.0F);
		if(currentFogColorMultiplier < 0.0F) {
			currentFogColorMultiplier = targettedFogColorMultiplier;
			lastFogColorMultiplier = targettedFogColorMultiplier;
		}
		lastFogColorMultiplier = currentFogColorMultiplier;
		float fogColorMultiplierIncr = 0.005F;
		if(Math.abs(currentFogColorMultiplier - targettedFogColorMultiplier) > fogColorMultiplierIncr) {
			if(currentFogColorMultiplier > targettedFogColorMultiplier) {
				currentFogColorMultiplier-=fogColorMultiplierIncr;
			} else if(currentFogColorMultiplier < targettedFogColorMultiplier) {
				currentFogColorMultiplier+=fogColorMultiplierIncr;
			}
		} else {
			currentFogColorMultiplier = targettedFogColorMultiplier;
		}
	}

	////// Underwater fog fix & Dark fog in caves //////
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogColor(FogColors event) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null) {
			IBlockState blockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(renderView.worldObj, renderView, (float) event.getRenderPartialTicks());
			float fogColorMultiplier = (float) (currentFogColorMultiplier + (currentFogColorMultiplier - lastFogColorMultiplier) * event.getRenderPartialTicks());
			if(blockState.getBlock() instanceof BlockSwampWater) {
				BlockPos pos = new BlockPos(ActiveRenderInfo.projectViewFromEntity(renderView, (float) event.getRenderPartialTicks()));
				int colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(blockState, renderView.worldObj, pos, 0);
				if(renderView.dimension == ConfigHandler.dimensionId) {
					double waterFogColorMultiplier = Math.pow(fogColorMultiplier, 6);
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F * (float)waterFogColorMultiplier);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F * (float)waterFogColorMultiplier);
				} else {
					event.setRed((float)(colorMultiplier >> 16 & 255) / 255.0F);
					event.setGreen((float)(colorMultiplier >> 8 & 255) / 255.0F);
					event.setBlue((float)(colorMultiplier & 255) / 255.0F);
				}
			} else if(renderView.dimension == ConfigHandler.dimensionId) {
				event.setRed(event.getRed() * fogColorMultiplier);
				event.setGreen(event.getGreen() * fogColorMultiplier);
				event.setBlue(event.getBlue() * fogColorMultiplier);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onFogDensity(FogDensity event) {
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null) {
			Block block = ActiveRenderInfo.getBlockStateAtEntityViewpoint(renderView.worldObj, renderView, (float) event.getRenderPartialTicks()).getBlock();
			if(block instanceof BlockSwampWater) {
				fogMode = GL11.GL_EXP;
				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
				if (renderView instanceof EntityLivingBase && ((EntityLivingBase)renderView).isPotionActive(Potion.getPotionById(13)/*Water breathing*/)) {
					event.setDensity(0.1F);
				} else {
					event.setDensity(0.4F);
				}
				event.setCanceled(true);
			}
		}
	}
}
