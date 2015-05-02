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
		string = type;
		setTextureName("thebetweenlands:" + type + "Item");
	}

	public static DamageSource causeArrowDamage(EntityBLArrow entityBLArrow, Entity entity) {
		//TODO make these types
	//	if(type.equals("anglerToothArrow"))
	//	return (new EntityDamageSourceIndirect("AnglerToothArrow", entityBLArrow, entity)).setProjectile();

	//	if(type.equals("poisonedAnglerToothArrow"))
		//	return (new EntityDamageSourceIndirect("PoisonedAnglerToothArrow", entityBLArrow, entity)).setProjectile();

	//	if(type.equals("octineArrow"))
	//		return (new EntityDamageSourceIndirect("OctineArrow", entityBLArrow, entity)).setProjectile();

		return (new EntityDamageSourceIndirect("AnglerToothArrow", entityBLArrow, entity)).setProjectile();
	}
	
}
