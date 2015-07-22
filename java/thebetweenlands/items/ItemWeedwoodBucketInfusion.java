package thebetweenlands.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWeedwoodBucketInfusion extends Item {
	public ItemWeedwoodBucketInfusion() {
		this.setMaxStackSize(1);
		this.setUnlocalizedName("thebetweenlands.weedwoodBucketInfusion");
		this.setTextureName("thebetweenlands:weedwoodBucketInfusion");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		if (hasTag(stack))
			if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Infused")) {
				list.add(EnumChatFormatting.GREEN + "Infusion Contains:");
				// Below display name usage is for debug purposes.
				// Now the actual crushed item's ItemStack is stored in the bucket NBT,
				// properties will be retrieved in the Alembic's TE logic (TODO)
				list.add(ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("crushedItem0")).getDisplayName());
				list.add(ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("crushedItem1")).getDisplayName());
				list.add(ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("crushedItem2")).getDisplayName());
				list.add(ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("crushedItem3")).getDisplayName());
			} else
				list.add("This Infusion Contains Nothing");
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		stack.setTagCompound(new NBTTagCompound());
	}

	private boolean hasTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return false;
		}
		return true;
	}
}
