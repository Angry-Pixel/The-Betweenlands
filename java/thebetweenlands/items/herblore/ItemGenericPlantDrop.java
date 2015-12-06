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
		return new ItemStack(BLItemRegistry.itemsGenericPlantDrop, size, enumPlantDrop.id);
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
		GENERIC_LEAF("genericLeaf", 0), ALGAE("algae", 1), ARROW_ARUM_LEAF("arrowArumLeaf", 2), BLUE_EYED_GRASS_FLOWERS("blueEyedGrassFlowers", 3), BLUE_IRIS_PETAL("blueIrisPetals", 4),
		MIRE_CORAL("mireCoral", 5), DEEP_WATER_CORAL("deepWaterCoral", 6), BOG_BEAN_FLOWER("bogBeanFlower", 7), BONESET_FLOWERS("bonesetFlowers", 8),
		BOTTLE_BRUSH_GRASS_BLADES("bottleBrushGrassBlades", 9), BROOM_SEDGE_LEAVES("broomSedgeLeaves", 10), BUTTON_BUSH_FLOWERS("buttonBushFlowers", 11),
		CARDINAL_FLOWER_PETALS("cardinalFlowerPetals", 12), CATTAIL_HEAD("cattailHead", 13), CAVE_GRASS_BLADES("caveGrassBlades", 14), 
		COPPER_IRIS_PETALS("copperIrisPetals", 15), GOLDEN_CLUB_FLOWERS("goldenClubFlowers", 16), LICHEN("lichen", 17), MARSH_HIBISCUS_FLOWER("marshHibiscusFlower", 18),
		MARSH_MALLOW_FLOWER("marshMallowFlower", 19), MARSH_MARIGOLD_FLOWER("marshMarigoldFlower", 20), NETTLE_LEAF("nettleLeaf", 21), PHRAGMITE_STEMS("phragmiteStems", 22),
		PICKEREL_WEED_FLOWER("pickerelWeedFlower", 23), SHOOT_LEAVES("shootLeaves", 24), SLUDGECREEP_LEAVES("sludgecreepLeaves", 25), SOFT_RUSH_LEAVES("softRushLeaves", 26),
		SUNDEW_HEAD("sundewHead", 27), SWAMP_TALL_GRASS_BLADES("swampTallGrassBlades", 28), CAVE_MOSS("caveMoss", 29), MOSS("moss", 30), MILK_WEED("milkWeed", 31), 
		HANGER("hanger", 32), PITCHER_PLANT_TRAP("pitcherPlantTrap", 33), WATER_WEEDS("waterWeeds", 34), VENUS_FLY_TRAP("venusFlyTrap", 35), VOLARPAD("volarpad", 36), THORNS("thorns", 37),
		POISON_IVY("poisonIvy", 38);

		public final String iconName;
		public final int id;

		private EnumItemPlantDrop(String unlocName, int id) {
			iconName = unlocName;
			this.id = id;
		}

		public static final EnumItemPlantDrop[] VALUES = values();
	}

}
