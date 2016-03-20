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
import thebetweenlands.entities.mobs.EntityDreadfulMummy;
import thebetweenlands.entities.mobs.EntityPeatMummy;

public class CameraPositionHandler {
	public static CameraPositionHandler INSTANCE = new CameraPositionHandler();

	private float getShakeStrength(float delta) {
		float screenShake = 0.0F;
		List<EntityPeatMummy> peatMummies = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(EntityPeatMummy.class, Minecraft.getMinecraft().renderViewEntity.boundingBox.expand(35, 35, 35));
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
		List<EntityDreadfulMummy> dreadfulMummies = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(EntityDreadfulMummy.class, Minecraft.getMinecraft().renderViewEntity.boundingBox.expand(35, 35, 35));
		for(EntityDreadfulMummy mummy : dreadfulMummies) {
			if(mummy.deathTicks > 0) {
				double dist = mummy.getDistanceToEntity(Minecraft.getMinecraft().renderViewEntity);
				float screamMult = (float) (1.0F - dist / 30.0F);
				if(dist >= 30.0F) {
					continue;
				}
				float screamShake = (float) ((Math.sin(mummy.deathTicks / 120.0D * Math.PI) + 0.1F) * 0.15F * screamMult);
				screenShake += screamShake;
			}
		}
		return MathHelper.clamp_float(screenShake, 0.0F, 0.15F);
	}

	private EntityDreadfulMummy getAttackingMummy() {
		List<EntityDreadfulMummy> mummies = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(EntityDreadfulMummy.class, Minecraft.getMinecraft().renderViewEntity.boundingBox.expand(10, 10, 10));
		for(EntityDreadfulMummy mummy : mummies) {
			if(mummy.currentEatPrey == Minecraft.getMinecraft().thePlayer)
				return mummy;
		}
		return null;
	}

	private double prevPosX;
	private double prevPosY;
	private double prevPosZ;
	private boolean didChange = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTickStart(RenderTickEvent event) {
		EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
		if(renderViewEntity == null)
			return;

		float shakeStrength = this.getShakeStrength(event.renderTickTime);
		EntityDreadfulMummy attackingMummy = this.getAttackingMummy();

		boolean shouldChange = shakeStrength > 0.0F || attackingMummy != null;

		if((!shouldChange || Minecraft.getMinecraft().isGamePaused()) && !this.didChange)
			return;

		if(event.phase == Phase.START) {
			this.prevPosX = renderViewEntity.posX;
			this.prevPosY = renderViewEntity.posY;
			this.prevPosZ = renderViewEntity.posZ;
			Random rnd = renderViewEntity.worldObj.rand;
			renderViewEntity.posX += rnd.nextFloat() * shakeStrength;
			renderViewEntity.posY += rnd.nextFloat() * shakeStrength;
			renderViewEntity.posZ += rnd.nextFloat() * shakeStrength;
			if(attackingMummy != null) {
				double direction = Math.toRadians(attackingMummy.prevRenderYawOffset + (attackingMummy.renderYawOffset - attackingMummy.prevRenderYawOffset) * event.renderTickTime);
				renderViewEntity.prevRotationYaw = renderViewEntity.rotationYaw = (float) (Math.toDegrees(direction) + 180);
				renderViewEntity.prevRotationPitch = renderViewEntity.rotationPitch = 0;
				renderViewEntity.setRotationYawHead((float) (Math.toDegrees(direction) + 180));
			}
			this.didChange = true;
		} else {
			renderViewEntity.posX = this.prevPosX;
			renderViewEntity.posY = this.prevPosY;
			renderViewEntity.posZ = this.prevPosZ;
			this.didChange = false;
		}
	}
}
