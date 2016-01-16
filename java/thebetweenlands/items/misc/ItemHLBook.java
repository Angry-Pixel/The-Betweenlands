package thebetweenlands.items.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.herblore.aspects.DiscoveryContainer;
import thebetweenlands.herblore.aspects.IDiscoveryProvider;
import thebetweenlands.proxy.CommonProxy;

/**
 * Created by Bart on 06/12/2015.
 */
public class ItemHLBook extends Item implements IDiscoveryProvider<ItemStack> {
	public ItemHLBook() {
		this.setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		player.openGui(TheBetweenlands.instance, CommonProxy.GUI_HL, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return itemStack;
	}

	@Override
	public DiscoveryContainer getContainer(ItemStack stack) {
		if(stack != null) {
			if(stack.stackTagCompound == null)
				stack.stackTagCompound = new NBTTagCompound();
			return new DiscoveryContainer(this, stack).updateFromNBT(stack.stackTagCompound, false);
		}
		return null;
	}

	@Override
	public void saveContainer(ItemStack stack, DiscoveryContainer container) {
		if(stack != null) {
			if(stack.stackTagCompound == null)
				stack.stackTagCompound = new NBTTagCompound();
			container.writeToNBT(stack.stackTagCompound);
		}
	}
}