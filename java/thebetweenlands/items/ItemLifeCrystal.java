package thebetweenlands.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLifeCrystal extends Item { //Place Holder Code
	public static final String[] iconPaths = new String[] { "lifeCrystal0", "lifeCrystal1", "lifeCrystal2", "lifeCrystal3", "lifeCrystal4" };
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemLifeCrystal() {
        setMaxDamage(128);
		maxStackSize = 1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add(Math.round(100F - 100F / getMaxDamage() * getDamage(stack)) + "% Remaining");
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[iconPaths.length];
		int i = 0;
		for (String path : iconPaths)
            this.icons[i++] = reg.registerIcon("thebetweenlands:" + path);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		if (damage < 0 || damage > getMaxDamage())
			return null;
		if(damage == getMaxDamage())
			return icons[4];
		if(damage > 96 && damage < getMaxDamage())
			return icons[3];
		if(damage > 64 && damage <= 96)
			return icons[2];
		if(damage > 32 && damage <= 64)
			return icons[1];
		if(damage >= 0 && damage <= 32)
			return icons[0];
		return icons[0];
	}
}
