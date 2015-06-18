package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.StatCollector;
import thebetweenlands.entities.EntityBLArrow;

import java.util.List;

public class ItemBLArrow extends Item {
	
	private static String type;

	public ItemBLArrow(String string) {
		super();
		type = string;
		setTextureName("thebetweenlands:" + type + "Item");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		ItemBLArrow item = (ItemBLArrow) stack.getItem();
		if (item == BLItemRegistry.octineArrow)
			list.add(StatCollector.translateToLocal("arrow.caution"));
		if (item == BLItemRegistry.basiliskArrow)
			list.add(StatCollector.translateToLocal("arrow.stunning"));
	}


	public static DamageSource causeArrowDamage(EntityBLArrow entityBLArrow, Entity entity) {
		//TODO make these types

		switch (type){
			case "poisonedAnglerToothArrow":
				return (new EntityDamageSourceIndirect("PoisonedAnglerToothArrow", entityBLArrow, entity)).setProjectile();
			case "octineArrow":
				return (new EntityDamageSourceIndirect("OctineArrow", entityBLArrow, entity)).setProjectile();
			case "basiliskArrow":
				return (new EntityDamageSourceIndirect("basiliskArrow", entityBLArrow, entity)).setProjectile();
			default:
				return (new EntityDamageSourceIndirect("AnglerToothArrow", entityBLArrow, entity)).setProjectile();
		}
	}

	
}
