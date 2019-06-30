package thebetweenlands.common.item.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfNoClip extends ItemRing {
	public static final String NBT_ACTIVE = "ring_of_no_clip.active";

	public ItemRingOfNoClip() {
		this.setMaxDamage(1800);
	}

	public void setActive(ItemStack stack, boolean active) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean(NBT_ACTIVE, active);
	}

	public boolean isActive(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_ACTIVE);
	}

	public boolean canPhase(EntityPlayer player, ItemStack stack) {
		return stack.getItemDamage() < stack.getMaxDamage() && player.isSneaking();
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(this.isActive(stack) && entity.ticksExisted % 20 == 0) {
			this.drainPower(stack, entity);
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean(NBT_ACTIVE, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return this.isActive(stack);
	}
}
