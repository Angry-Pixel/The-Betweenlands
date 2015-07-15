package thebetweenlands.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPestle extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconStatic, iconAnimated;

	public ItemPestle() {
        setMaxDamage(128);
		maxStackSize = 1;
		setUnlocalizedName("thebetweenlands.pestle");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add("Place Pestle in Mortar");
		list.add(Math.round(100F - 100F / getMaxDamage() * getDamage(stack)) + "% Remaining: " + (getMaxDamage() - getDamage(stack)) +" more uses." );
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		iconStatic = reg.registerIcon("thebetweenlands:pestle");
		iconAnimated = reg.registerIcon("thebetweenlands:pestleAnimated");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) {
		if(hasTag(stack) && stack.getTagCompound().getBoolean("active"))
			return iconAnimated;
		return iconStatic;
	}

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
