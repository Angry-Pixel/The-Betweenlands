package thebetweenlands.items.loot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.IEquippable;

public abstract class ItemRing extends Item implements IEquippable {
	public ItemRing() {
		this.maxStackSize = 1;
	}

	public boolean isActive(ItemStack stack) {
		return stack.getItemDamage() < stack.getMaxDamage();
	}

	protected float getXPConversionRate(ItemStack stack, EntityPlayer player) {
		//1 xp = 5 damage repaired
		return 5.0F;
	}

	public void drainPower(ItemStack stack) {
		if(stack.getItemDamage() < stack.getMaxDamage() && stack.getItem() instanceof ItemRing && ((ItemRing)stack.getItem()).isActive(stack)) {
			stack.setItemDamage(stack.getItemDamage() + 1);
		}
	}

	@Override
	public EnumEquipmentCategory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentCategory.RING;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote && !player.isSneaking()) {
			if(stack.getItemDamage() > 0 && player.experienceTotal > 0) {
				int repairPerClick = 40;
				float conversion = this.getXPConversionRate(stack, player);
				float requiredRepair = Math.min(repairPerClick, stack.getItemDamage() / conversion);
				stack.setItemDamage(Math.max(0, stack.getItemDamage() - MathHelper.ceiling_float_int(Math.min(repairPerClick, player.experienceTotal) * conversion)));
				this.removeXp(player, MathHelper.ceiling_float_int(Math.min(requiredRepair, player.experienceTotal)));
			}
		}
		return stack;
	}

	protected void removeXp(EntityPlayer player, int amount) {
		int newXP = Math.max(player.experienceTotal - amount, 0);
		player.experienceTotal = 0;
		player.experienceLevel = 0;
		player.experience = 0;
		if(newXP > 0) {
			int xpCap = Integer.MAX_VALUE - player.experienceTotal;
			if (newXP > xpCap) {
				newXP = xpCap;
			}
			player.experience += (float)newXP / (float)player.xpBarCap();
			for (player.experienceTotal += newXP; player.experience >= 1.0F; player.experience /= (float)player.xpBarCap()) {
				player.experience = (player.experience - 1.0F) * (float)player.xpBarCap();
				player.experienceLevel += 1;
			}
		}
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return entity instanceof EntityPlayer && inventory.getEquipment(EnumEquipmentCategory.RING).size() == 0;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, EquipmentInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, EquipmentInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, EquipmentInventory inventory) { }

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return stack.getItemDamage() == 0 || player.experienceTotal == 0 || player.isSneaking();
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity) {
		if(entity.ticksExisted % 20 == 0) {
			this.drainPower(stack);
		}
	}
}
