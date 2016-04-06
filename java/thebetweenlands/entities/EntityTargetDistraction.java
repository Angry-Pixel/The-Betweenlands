package thebetweenlands.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * An entity that serves as a distraction for the hardcoded EntityCreature targetting AI 
 */
public class EntityTargetDistraction extends EntityLivingBase {
	public EntityTargetDistraction(World world) {
		super(world);
	}

	@Override
	public boolean isEntityAlive() {
		return false;
	}

	@Override
	public ItemStack getHeldItem() {
		return null;
	}

	@Override
	public ItemStack getEquipmentInSlot(int slot) {
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int slot, ItemStack stack) { }

	@Override
	public ItemStack[] getLastActiveItems() {
		return new ItemStack[0];
	}

	@Override
	public void onUpdate() {
		this.isDead = true;
	}
}