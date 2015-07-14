package thebetweenlands.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPestle extends Item { //Place Holder Code
	@SideOnly(Side.CLIENT)
	private IIcon iconAnimated;

	public ItemPestle() {
        setMaxDamage(128);
		maxStackSize = 1;
		setUnlocalizedName("thebetweenlands.pestle");
		setTextureName("thebetweenlands:pestle");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add("Place Pestle in Mortar");
	}
/* TODO make this work
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		iconAnimated = reg.registerIcon(getIconString() + "Animated");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int pass) {
		if(hasTag(stack) && stack.getTagCompound().getBoolean("active"))
			return iconAnimated;
		return itemIcon;
	}
*/
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int tick, boolean map) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
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
