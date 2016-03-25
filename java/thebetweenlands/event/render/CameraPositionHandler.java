package thebetweenlands.event.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.entities.ICameraOffset;
import thebetweenlands.entities.IScreenShake;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesRingInput;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.BLItemRegistry;

public class CameraPositionHandler {
	public static CameraPositionHandler INSTANCE = new CameraPositionHandler();

	private float getShakeStrength(float delta) {
		float screenShake = 0.0F;
		World world = Minecraft.getMinecraft().theWorld;
		EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
		for(Entity entity : (List<Entity>) world.loadedEntityList) {
			if(entity instanceof IScreenShake) {
				IScreenShake shake = (IScreenShake) entity;
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
		EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
		if(renderViewEntity == null)
			return;

		float shakeStrength = this.getShakeStrength(event.renderTickTime);
		List<ICameraOffset> offsetEntities = new ArrayList<ICameraOffset>();
		for(Entity entity : (List<Entity>) renderViewEntity.worldObj.loadedEntityList) {
			if(entity instanceof ICameraOffset)
				offsetEntities.add((ICameraOffset)entity);
		}


		//Ring of Summoning
		for(EntityPlayer player : (List<EntityPlayer>)renderViewEntity.worldObj.playerEntities) {
			if(player.getDistanceToEntity(renderViewEntity) < 32.0D) {
				EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(player);
				for(Equipment equipment : equipmentInventory.getEquipment(EnumEquipmentCategory.RING)) {
					if(equipment.item.getItem() == BLItemRegistry.ringOfSummoning) {
						EntityPropertiesRingInput prop = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesRingInput.class);
						if(prop != null) {
							if(prop.isInUse()) {
								shakeStrength += 0.1F;
							}
						}
					}
				}
			}
		}



		boolean shouldChange = shakeStrength > 0.0F || !offsetEntities.isEmpty();

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
			if(!offsetEntities.isEmpty()) {
				for(ICameraOffset offset : offsetEntities)
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
