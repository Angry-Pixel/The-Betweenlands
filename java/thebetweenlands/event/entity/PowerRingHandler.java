package thebetweenlands.event.entity;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.loot.ItemRingOfPower;

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
						BLParticle.GREEN_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX + event.entity.width, event.entity.posY + event.entity.height, event.entity.posZ);
						BLParticle.GREEN_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX, event.entity.posY + event.entity.height, event.entity.posZ + event.entity.width);
						BLParticle.GREEN_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX - event.entity.width, event.entity.posY + event.entity.height, event.entity.posZ);
						BLParticle.GREEN_FLAME.spawn(((EntityPlayerMP) attacker).worldObj, event.entity.posX, event.entity.posY + event.entity.height, event.entity.posZ - event.entity.width);
					}
				}
			}
		}
	}
}
