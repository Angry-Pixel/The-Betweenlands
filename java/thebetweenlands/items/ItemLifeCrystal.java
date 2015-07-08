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
        setMaxDamage(4);
		maxStackSize = 1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add(100 - 25 * getDamage(stack) + "% Remaining");
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
	public IIcon getIconFromDamage(int meta) {
		if (meta < 0 || meta >= this.icons.length)
			return null;
		return this.icons[meta];
	}
}
