package thebetweenlands.client.event.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.IEntityCameraOffset;
import thebetweenlands.common.entity.IEntityScreenShake;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.location.LocationCragrockTower;

public class CameraPositionHandler {
	public static CameraPositionHandler INSTANCE = new CameraPositionHandler();

	private float getShakeStrength(float delta) {
		float screenShake = 0.0F;
		World world = Minecraft.getMinecraft().theWorld;
		Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
		for(Entity entity : (List<Entity>) world.loadedEntityList) {
			if(entity instanceof IEntityScreenShake) {
				IEntityScreenShake shake = (IEntityScreenShake) entity;
				screenShake += shake.getShakeIntensity(renderViewEntity, delta);
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

			//TODO Ring of Summoning
			/*for(EntityPlayer player : (List<EntityPlayer>)renderViewEntity.worldObj.playerEntities) {
			if(player.getDistanceToEntity(renderViewEntity) < 32.0D) {
				EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(player);
				for(Equipment equipment : equipmentInventory.getEquipment(EnumEquipmentCategory.RING)) {
					if(equipment.item.getItem() == BLItemRegistry.ringOfSummoning && equipment.item.stackTagCompound != null && equipment.item.stackTagCompound.hasKey("useTime")) {
						int useTime = equipment.item.stackTagCompound.getInteger("useTime");
						if(useTime < ItemRingOfSummoning.MAX_USE_TIME && (!equipment.item.stackTagCompound.hasKey("useCooldown") || equipment.item.stackTagCompound.getInteger("useCooldown") <= 0)) {
							EntityPropertiesRingInput prop = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesRingInput.class);
							if(prop != null) {
								if(prop.isInUse()) {
									shakeStrength += (ItemRingOfSummoning.MAX_USE_TIME - useTime) / (float)ItemRingOfSummoning.MAX_USE_TIME * 0.1F + 0.01F;
								}
							}
						}
					}
				}
			}*/



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
