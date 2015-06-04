package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import thebetweenlands.entities.EntityBLArrow;

public class ItemBLArrow extends Item {
	
	private static String type;

	public ItemBLArrow(String string) {
		super();
		type = string;
		setTextureName("thebetweenlands:" + type + "Item");
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
