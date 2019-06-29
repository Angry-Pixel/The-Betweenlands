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
	public static final String NBT_PHASING = "ring_of_no_clip.phasing";

	public ItemRingOfNoClip() {
		this.setMaxDamage(1800);
	}

	public void setPhasing(ItemStack stack) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean(NBT_PHASING, true);
	}

	public boolean isPhasing(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(NBT_PHASING);
	}

	public boolean canPhase(EntityPlayer player, ItemStack stack) {
		return true;
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(this.isPhasing(stack) && entity.ticksExisted % 20 == 0) {
			this.drainPower(stack, entity);
		}

		if(!entity.world.isRemote) {
			NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.setBoolean(NBT_PHASING, false);
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean(NBT_PHASING, false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return this.isPhasing(stack);
	}
}
