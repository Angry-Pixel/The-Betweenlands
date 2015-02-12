package thebetweenlands.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SwampTalisman extends Item {

	public enum TALISMAN {
		swampTalisman,
		swampTalisman1,
		swampTalisman2,
		swampTalisman3,
		swampTalisman4
	}

	public static ItemStack createStack(TALISMAN swampTalisman) {
		return createStack(swampTalisman, 1);
	}

	public static ItemStack createStack(TALISMAN swampTalisman, int size) {
		return new ItemStack(BLItemRegistry.swampTalisman, size, swampTalisman.ordinal());
	}

	@SideOnly(Side.CLIENT)
	private static IIcon[] icons;

	public SwampTalisman() {
		setMaxDamage(0);
		setHasSubtypes(true);
		setUnlocalizedName("thebetweenlands.swampTalisman");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[TALISMAN.values().length];
		int i = 0;
		for (TALISMAN d : TALISMAN.values())
			icons[i++] = reg.registerIcon("thebetweenlands:" + d.name());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if (meta < 0 || meta >= icons.length)
			return null;
		return icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < TALISMAN .values().length; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + stack.getItemDamage();
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			//maybe do something here?
		}
		//if (!player.capabilities.isCreativeMode) {
		//	--is.stackSize;
		//	return true;
		//}
		return false;
		}
}