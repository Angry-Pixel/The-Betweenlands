package thebetweenlands.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMaterialsCrushed extends Item {
	public static ItemStack createStack(EnumMaterialsCrushed materialCrushed) {
		return createStack(materialCrushed, 1);
	}

	public static ItemStack createStack(EnumMaterialsCrushed materialCrushed, int size) {
		return new ItemStack(BLItemRegistry.materialCrushed, size, materialCrushed.ordinal());
	}

	public static ItemStack createStack(Item item, int size, int meta) {
		return new ItemStack(item, size, meta);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemMaterialsCrushed() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[EnumMaterialsCrushed.VALUES.length];
		for (int i = 0; i < EnumMaterialsCrushed.VALUES.length; i++) {
			icons[i] = reg.registerIcon("thebetweenlands:" + EnumMaterialsCrushed.VALUES[i].iconName);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		if (meta < 0 || meta >= icons.length) {
			return null;
		}

		return icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < EnumMaterialsCrushed.VALUES.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + EnumMaterialsCrushed.VALUES[stack.getItemDamage()].iconName;
		} catch (Exception e) {
			return "item.thebetweenlands.unknownSoldier";
		}
	}

	public static enum EnumMaterialsCrushed {
		GROUND_CAT_TAIL("groundCatTail"), GROUND_SWAMP_GRASS_TALL("groundSwampTallGrass"), GROUND_SHOOTS("groundShoots"), 
		GROUND_ARROW_ARUM("groundArrowArum"), GROUND_BUTTON_BUSH("groundButtonBush"), GROUND_MARSH_HIBISCUS("groundMarshHibiscus"), 
		GROUND_PICKEREL_WEED("groundPickerelWeed"), GROUND_SOFT_RUSH("groundSoftRush"), GROUND_MARSH_MALLOW("groundMarshMallow"), 
		GROUND_MILKWEED("groundMilkweed"), GROUND_BLUE_IRIS("groundBlueIris"), GROUND_COPPER_IRIS("groundCopperIris"), GROUND_BLUE_EYED_GRASS("groundBlueEyedGrass"), 
		GROUND_BONESET("groundBoneset"), GROUND_BOTTLE_BRUSH_GRASS("groundBottleBrushGrass"), GROUND_WEEDWOOD_BARK("groundWeedwoodBark"),
		GROUND_DRIED_SWAMP_REED("groundDriedSwampReed");

		public final String iconName;

		private EnumMaterialsCrushed(String unlocName) {
			iconName = unlocName;
		}

		public static final EnumMaterialsCrushed[] VALUES = values();
	}

}
