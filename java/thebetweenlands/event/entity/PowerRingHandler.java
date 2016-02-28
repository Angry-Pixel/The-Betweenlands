package thebetweenlands.event.entity;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.loot.ItemRingOfPower;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.server.PacketPowerRingHit;

public class PowerRingHandler {
	@SubscribeEvent
	public void powerRingHandler(LivingHurtEvent event) {
		if(event.source.getEntity() instanceof EntityLivingBase){
			EntityLivingBase attacker = (EntityLivingBase)event.source.getEntity();
			if(attacker instanceof EntityPlayerMP) {
				EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(attacker);
				List<Equipment> equipmentList = equipmentInventory.getEquipment(EnumEquipmentCategory.RING);
				for(Equipment equipment : equipmentList) {
					if(equipment.item.getItem() instanceof ItemRingOfPower) {
						event.ammount *= 1.5f;
						if(event.entity != null) 
							TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketPowerRingHit(event.entity.getEntityId())), new TargetPoint(event.entity.dimension, event.entity.posX, event.entity.posY, event.entity.posZ, 64D));
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribePacket
	public static void onPowerRingHitPacket(PacketPowerRingHit packet) {
		Entity entityHit = packet.getEntity(TheBetweenlands.proxy.getClientWorld());
		if(entityHit != null) {
			BLParticle.GREEN_FLAME.spawn(entityHit.worldObj, entityHit.posX + entityHit.width / 2.0D, entityHit.posY + entityHit.height, entityHit.posZ, 0.08D, 0.05D, 0, 1F);
			BLParticle.GREEN_FLAME.spawn(entityHit.worldObj, entityHit.posX, entityHit.posY + entityHit.height, entityHit.posZ + entityHit.width / 2.0D, 0, 0.05D, 0.08D, 1F);
			BLParticle.GREEN_FLAME.spawn(entityHit.worldObj, entityHit.posX - entityHit.width / 2.0D, entityHit.posY + entityHit.height, entityHit.posZ, -0.08D, 0.05D, 0, 1F);
			BLParticle.GREEN_FLAME.spawn(entityHit.worldObj, entityHit.posX, entityHit.posY + entityHit.height, entityHit.posZ - entityHit.width / 2.0D, 0, 0.05D, -0.08D, 1F);
		}
	}
}
