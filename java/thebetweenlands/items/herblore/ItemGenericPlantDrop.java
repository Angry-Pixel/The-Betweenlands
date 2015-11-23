package thebetweenlands.items.herblore;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thebetweenlands.items.BLItemRegistry;

public class ItemGenericPlantDrop extends Item {
	public static ItemStack createStack(EnumItemPlantDrop enumPlantDrop) {
		return createStack(enumPlantDrop, 1);
	}

	public static ItemStack createStack(EnumItemPlantDrop enumPlantDrop, int size) {
		return new ItemStack(BLItemRegistry.itemsGenericPlantDrop, size, enumPlantDrop.ordinal());
	}

	public static ItemStack createStack(Item item, int size, int meta) {
		return new ItemStack(item, size, meta);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemGenericPlantDrop() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		icons = new IIcon[EnumItemPlantDrop.VALUES.length];
		for (int i = 0; i < EnumItemPlantDrop.VALUES.length; i++) {
			icons[i] = reg.registerIcon("thebetweenlands:strictlyHerblore/plantDrops/" + EnumItemPlantDrop.VALUES[i].iconName);
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
		for (int i = 0; i < EnumItemPlantDrop.VALUES.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + EnumItemPlantDrop.VALUES[stack.getItemDamage()].iconName;
		} catch (Exception e) {
			return "item.thebetweenlands.unknownPlantDrop";
		}
	}

	public static enum EnumItemPlantDrop {
		GENERIC_LEAF("genericLeaf"), ALGAE("algae"), ARROW_ARUM_LEAF("arrowArumLeaf"), BLUE_EYED_GRASS_FLOWERS("blueEyedGrassFlowers"), BLUE_IRIS_PETAL("blueIrisPetals"),
		MIRE_CORAL("mireCoral"), DEEP_WATER_CORAL("deepWaterCoral"), BOG_BEAN_FLOWER("bogBeanFlower"), BONESET_FLOWERS("bonesetFlowers"),
		BOTTLE_BRUSH_GRASS_BLADES("bottleBrushGrassBlades"), BROOM_SEDGE_LEAVES("broomSedgeLeaves"), BUTTON_BUSH_FLOWERS("buttonBushFlowers"),
		CARDINAL_FLOWER_PETALS("cardinalFlowerPetals"), CATTAIL_HEAD("cattailHead"), CAVE_GRASS_BLADES("caveGrassBlades"), 
		COPPER_IRIS_PETALS("copperIrisPetals"), GOLDEN_CLUB_FLOWERS("goldenClubFlowers"), LICHEN("lichen"), MARSH_HIBISCUS_FLOWER("marshHibiscusFlower"),
		MARSH_MALLOW_FLOWER("marshMallowFlower"), MARSH_MARIGOLD_FLOWER("marshMarigoldFlower"), NETTLE_LEAF("nettleLeaf"), PHRAGMITE_STEMS("phragmiteStems"),
		PICKEREL_WEED_FLOWER("pickerelWeedFlower"), SHOOT_LEAVES("shootLeaves"), SLUDGECREEP_LEAVES("sludgecreepLeaves"), SOFT_RUSH_LEAVES("softRushLeaves"),
		SUNDEW_HEAD("sundewHead"), SWAMP_TALL_GRASS_BLADES("swampTallGrassBlades"), CAVE_MOSS("caveMoss"), MOSS("moss"), MILK_WEED("milkWeed"), 
		HANGER("hanger"), PITCHER_PLANT_TRAP("pitcherPlantTrap"), WATER_WEEDS("waterWeeds"), VENUS_FLY_TRAP("venusFlyTrap"), VOLARPAD("volarpad"), THORNS("thorns"),
		POISON_IVY("poisonIvy");

		public final String iconName;

		private EnumItemPlantDrop(String unlocName) {
			iconName = unlocName;
		}

		public static final EnumItemPlantDrop[] VALUES = values();
	}

}
