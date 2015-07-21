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
				list.add(stack.getTagCompound().getString("crushedItem0"));
				list.add(stack.getTagCompound().getString("crushedItem1"));
				list.add(stack.getTagCompound().getString("crushedItem2"));
				list.add(stack.getTagCompound().getString("crushedItem3"));
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
