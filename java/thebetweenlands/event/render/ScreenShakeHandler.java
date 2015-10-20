package thebetweenlands.event.render;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import thebetweenlands.entities.mobs.EntityPeatMummy;

public class ScreenShakeHandler {
	public static ScreenShakeHandler INSTANCE = new ScreenShakeHandler();

	private float getShakeStrength(float delta) {
		List<EntityPeatMummy> peatMummies = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(EntityPeatMummy.class, Minecraft.getMinecraft().renderViewEntity.boundingBox.expand(35, 35, 35));
		float screenShake = 0.0F;
		for(EntityPeatMummy peatMummy : peatMummies) {
			if(peatMummy.isScreaming()) {
				double dist = peatMummy.getDistanceToEntity(Minecraft.getMinecraft().renderViewEntity);
				float screamMult = (float) (1.0F - dist / 30.0F);
				if(dist >= 30.0F) {
					continue;
				}
				float screamShake = (float) ((Math.sin(peatMummy.getScreamingProgress(delta) * Math.PI) + 0.1F) * 0.15F * screamMult);
				screenShake += screamShake;
			}
		}
		return MathHelper.clamp_float(screenShake, 0.0F, 0.15F);
	}

	private double prevPosX;
	private double prevPosY;
	private double prevPosZ;
	private boolean didShake = false;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTickStart(RenderTickEvent event) {
		EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
		if(renderViewEntity == null) return;
		float shakeStrength = this.getShakeStrength(event.renderTickTime);
		if((shakeStrength == 0.0F || Minecraft.getMinecraft().isGamePaused()) && !this.didShake) return;
		if(event.phase == Phase.START) {
			this.prevPosX = renderViewEntity.posX;
			this.prevPosY = renderViewEntity.posY;
			this.prevPosZ = renderViewEntity.posZ;
			Random rnd = renderViewEntity.worldObj.rand;
			renderViewEntity.posX += rnd.nextFloat() * shakeStrength;
			renderViewEntity.posY += rnd.nextFloat() * shakeStrength;
			renderViewEntity.posZ += rnd.nextFloat() * shakeStrength;
			this.didShake = true;
		} else {
			renderViewEntity.posX = this.prevPosX;
			renderViewEntity.posY = this.prevPosY;
			renderViewEntity.posZ = this.prevPosZ;
			this.didShake = false;
		}
	}
}
