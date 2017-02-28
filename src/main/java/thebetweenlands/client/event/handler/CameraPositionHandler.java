package thebetweenlands.client.event.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.api.entity.IEntityCameraOffset;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.item.equipment.ItemRingOfSummoning;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.location.LocationCragrockTower;

public class CameraPositionHandler {
	public static CameraPositionHandler INSTANCE = new CameraPositionHandler();

	private float getShakeStrength(float delta) {
		float screenShake = 0.0F;
		World world = Minecraft.getMinecraft().theWorld;
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderViewEntity != null) {
			for(Entity entity : (List<Entity>) world.loadedEntityList) {
				if(entity instanceof IEntityScreenShake) {
					IEntityScreenShake shake = (IEntityScreenShake) entity;
					screenShake += shake.getShakeIntensity(renderViewEntity, delta);
				}
			}
		}
		return MathHelper.clamp_float(screenShake, 0.0F, 0.15F);
	}

	private double prevPosX;
	private double prevPosY;
	private double prevPosZ;
	private boolean didChange = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTickStart(RenderTickEvent event) {
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();

		if(renderViewEntity != null) {

			float shakeStrength = this.getShakeStrength(event.renderTickTime);
			List<IEntityCameraOffset> offsetEntities = new ArrayList<IEntityCameraOffset>();
			for(Entity entity : (List<Entity>) renderViewEntity.worldObj.loadedEntityList) {
				if(entity instanceof IEntityCameraOffset)
					offsetEntities.add((IEntityCameraOffset)entity);
			}

			World world = renderViewEntity.worldObj;
			BetweenlandsWorldData worldData = BetweenlandsWorldData.forWorld(world);

			//Crumbling cragrock tower
			List<LocationCragrockTower> towers = worldData.getSharedStorageAt(LocationCragrockTower.class, location -> location.getInnerBoundingBox().expand(4, 4, 4).isVecInside(renderViewEntity.getPositionVector()), renderViewEntity.posX, renderViewEntity.posZ);
			for(LocationCragrockTower tower : towers) {
				if(tower.isCrumbling()) {
					shakeStrength += Math.min(Math.pow((tower.getCrumblingTicks() + event.renderTickTime) / 400.0f, 4) * 0.08f, 0.08f);
				}
			}


			//Ring of Summoning
			List<EntityPlayer> nearbyPlayers = renderViewEntity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, renderViewEntity.getEntityBoundingBox().expand(32, 32, 32), entity -> entity.getDistanceToEntity(renderViewEntity) <= 32.0D);

			for(EntityPlayer player : nearbyPlayers) {
				if(player.hasCapability(CapabilityRegistry.CAPABILITY_SUMMON, null)) {
					ISummoningCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SUMMON, null);

					if(cap.isActive()) {
						shakeStrength += (ItemRingOfSummoning.MAX_USE_TIME - cap.getActiveTicks()) / (float)ItemRingOfSummoning.MAX_USE_TIME * 0.1F + 0.01F;
					}
				}
			}



			boolean shouldChange = shakeStrength > 0.0F || !offsetEntities.isEmpty();

			if((shouldChange && !Minecraft.getMinecraft().isGamePaused()) || this.didChange) {
				if(event.phase == Phase.START) {
					this.prevPosX = renderViewEntity.posX;
					this.prevPosY = renderViewEntity.posY;
					this.prevPosZ = renderViewEntity.posZ;
					Random rnd = renderViewEntity.worldObj.rand;
					renderViewEntity.posX += rnd.nextFloat() * shakeStrength;
					renderViewEntity.posY += rnd.nextFloat() * shakeStrength;
					renderViewEntity.posZ += rnd.nextFloat() * shakeStrength;
					if(!offsetEntities.isEmpty()) {
						for(IEntityCameraOffset offset : offsetEntities)
							if(offset.applyOffset(renderViewEntity, event.renderTickTime))
								break;
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
	}
}
