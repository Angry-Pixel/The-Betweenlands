package thebetweenlands.event.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.event.debugging.DebugHandlerClient;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FogHandler {
	public static final FogHandler INSTANCE = new FogHandler();

	public float getCurrentFogStart() {
		return this.currentFogStart;
	}
	
	public float getCurrentFogEnd() {
		return this.currentFogEnd;
	}
	
	public int getCurrentFogMode() {
		return this.fogMode;
	}
	
	public boolean hasDenseFog() {
		World world = Minecraft.getMinecraft().theWorld;
		if(world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
			boolean denseFog = false;
			if((!ConfigHandler.DEBUG && eeRegistry.DENSE_FOG.isActive()) ||
					(DebugHandlerClient.INSTANCE.denseFog && ConfigHandler.DEBUG && !eeRegistry.DENSE_FOG.isActive()) ||
					(!DebugHandlerClient.INSTANCE.denseFog && ConfigHandler.DEBUG && eeRegistry.DENSE_FOG.isActive())) {
				denseFog = true;
			}
			return denseFog;
		}
		return false;
	}

	////// Biome specific fog + smooth transition //////
	private float currentFogStart;
	private float currentFogEnd;
	private float renderFogStart;
	private float renderFogEnd;
	private int fogMode;
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogRenderEvent(RenderFogEvent event) {
		World world = FMLClientHandler.instance().getWorldClient();
		if(world == null) {
			return;
		} else if(world.isRemote) {
			EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
			if(renderView == null || renderView.dimension != ConfigHandler.DIMENSION_ID) {
				return;
			}
			double partialTicks = event.renderPartialTicks;
			BiomeGenBase biome = world.getBiomeGenForCoords(
					MathHelper.floor_double(renderView.posX),
					MathHelper.floor_double(renderView.posZ));
			float fogStart = event.farPlaneDistance * 0.25F;
			float fogEnd = event.farPlaneDistance;
			if(biome instanceof BiomeGenBaseBetweenlands) {
				BiomeGenBaseBetweenlands bgbb = (BiomeGenBaseBetweenlands) biome;
				fogStart = bgbb.getFogStart(event.farPlaneDistance);
				fogEnd = bgbb.getFogEnd(event.farPlaneDistance);
			}
			
			//Dense fog
			if(this.hasDenseFog()) {
				fogStart /= 5.0f;
				fogEnd /= 3.0f;
			}
			
			//Underground fog
			if(renderView.posY < WorldProviderBetweenlands.CAVE_START) {
				float multiplier = ((float)(WorldProviderBetweenlands.CAVE_START - renderView.posY) / WorldProviderBetweenlands.CAVE_START);
				multiplier = 1.0F - multiplier;
				multiplier *= Math.pow(multiplier, 6);
				multiplier = multiplier * 0.9F + 0.1F;
				fogStart *= multiplier;
				fogEnd *= multiplier;
			}
			
			if(Math.abs(this.currentFogStart - fogStart) > 0.1f) {
				float currentFogStartIncr = Math.abs(this.currentFogStart - fogStart)/event.farPlaneDistance/60.f;
				if(this.currentFogStart > fogStart) {
					this.currentFogStart-=currentFogStartIncr;
				} else if(this.currentFogStart < fogStart) {
					this.currentFogStart+=currentFogStartIncr;
				}
			}
			if(Math.abs(this.currentFogEnd - fogEnd) > 0.1f) {
				float currentFogEndIncr = Math.abs(this.currentFogEnd - fogEnd)/event.farPlaneDistance/60.f;
				if(this.currentFogEnd > fogEnd) {
					this.currentFogEnd-=currentFogEndIncr;
				} else if(this.currentFogEnd < fogEnd) {
					this.currentFogEnd+=currentFogEndIncr;
				}
			}
			float prevRenderFogStart = this.renderFogStart;
			float prevRenderFogEnd = this.renderFogEnd;
			this.renderFogStart = this.currentFogStart;
			this.renderFogEnd = this.currentFogEnd;
			this.renderFogStart = (float) (prevRenderFogStart + (this.renderFogStart - prevRenderFogStart) * partialTicks);
			this.renderFogEnd = (float) (prevRenderFogEnd + (this.renderFogEnd - prevRenderFogEnd) * partialTicks);
			this.fogMode = GL11.GL_LINEAR;
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, this.renderFogStart);
			GL11.glFogf(GL11.GL_FOG_END, this.renderFogEnd);
		}
	}


	////// Underwater fog fix //////
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogColor(FogColors event) {
		World world = FMLClientHandler.instance().getWorldClient();
		if(world == null) {
			return;
		} else if(world.isRemote) {
			EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
			if(renderView == null || renderView.dimension != ConfigHandler.DIMENSION_ID) {
				return;
			}
			if(renderView.isInWater()) {
				Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, renderView, (float) event.renderPartialTicks);
				if(block instanceof BlockSwampWater) {
					BiomeGenBase biome = world.getBiomeGenForCoords(
							MathHelper.floor_double(renderView.posX),
							MathHelper.floor_double(renderView.posZ));
					if(biome instanceof BiomeGenBaseBetweenlands) {
						int colorMultiplier = biome.getWaterColorMultiplier();
						event.red = ((colorMultiplier & 16711680) >> 16) / 255.0F / 3.0F;
						event.green = ((colorMultiplier & 65280) >> 8) / 255.0F / 3.0F;
						event.blue = (colorMultiplier & 255) / 255.0F / 3.0F;
					}
				}
			}
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogDensity(FogDensity event) {
		World world = FMLClientHandler.instance().getWorldClient();
		if(world == null) {
			return;
		} else if(world.isRemote) {
			EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
			if(renderView == null || renderView.dimension != ConfigHandler.DIMENSION_ID) {
				return;
			}
			if(renderView.isInWater()) {
				Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(world, renderView, (float) event.renderPartialTicks);
				if(block instanceof BlockSwampWater) {
					this.fogMode = GL11.GL_EXP;
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
	                if (renderView.isPotionActive(Potion.waterBreathing)) {
	                	event.density = 0.1F;
	                } else {
	                	event.density = 0.4F;
	                }
					event.setCanceled(true);
				}
			}
		}
	}
}
