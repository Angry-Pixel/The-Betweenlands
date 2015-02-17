package thebetweenlands.event.listener;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;

import org.lwjgl.opengl.GL11;

import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.BiomeGenBaseBetweenlands;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GenericListener {
	public static final GenericListener INSTANCE = new GenericListener();


	private float currentFogStart;
	private float currentFogEnd;
	private float renderFogStart;
	private float renderFogEnd;
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
			if(Math.abs(this.currentFogStart - fogStart) > 0.1f) {
				float currentFogStartIncr = Math.abs(this.currentFogStart - fogStart)/event.farPlaneDistance/10.f;
				if(this.currentFogStart > fogStart) {
					this.currentFogStart-=currentFogStartIncr;
				} else if(this.currentFogStart < fogStart) {
					this.currentFogStart+=currentFogStartIncr;
				}
			}
			if(Math.abs(this.currentFogEnd - fogEnd) > 0.1f) {
				float currentFogEndIncr = Math.abs(this.currentFogEnd - fogEnd)/event.farPlaneDistance/10.f;
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
			GL11.glFogf(GL11.GL_FOG_START, this.renderFogStart);
			GL11.glFogf(GL11.GL_FOG_END, this.renderFogEnd);
		}
	}
}
