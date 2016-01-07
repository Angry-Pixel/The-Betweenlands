package thebetweenlands.items.herblore;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.IManualEntryItem;

public class ItemGenericCrushed extends Item implements IManualEntryItem {
	public static ItemStack createStack(EnumItemGenericCrushed enumCrushed) {
		return createStack(enumCrushed, 1);
	}

	public static ItemStack createStack(EnumItemGenericCrushed enumCrushed, int size) {
		return new ItemStack(BLItemRegistry.itemsGenericCrushed, size, enumCrushed.id);
	}

	public static ItemStack createStack(Item item, int size, int meta) {
		return new ItemStack(item, size, meta);
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemGenericCrushed() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		int maxID = 0;
		for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
			EnumItemGenericCrushed enumGeneric = EnumItemGenericCrushed.VALUES[i];
			if(enumGeneric != EnumItemGenericCrushed.INVALID) {
				int enumID = enumGeneric.id;
				if(enumID > maxID) {
					maxID = enumID;
				}
			}
		}
		icons = new IIcon[maxID + 1];
		for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
			EnumItemGenericCrushed enumGeneric = EnumItemGenericCrushed.VALUES[i];
			if(enumGeneric != EnumItemGenericCrushed.INVALID) {
				icons[enumGeneric.id] = reg.registerIcon("thebetweenlands:strictlyHerblore/ground/" + enumGeneric.iconName);
			}
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
		for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
			if(EnumItemGenericCrushed.VALUES[i] != EnumItemGenericCrushed.INVALID) list.add(new ItemStack(item, 1, EnumItemGenericCrushed.VALUES[i].id));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + getEnumFromID(stack.getItemDamage()).iconName;
		} catch (Exception e) {
			return "item.thebetweenlands.unknownCrushed";
		}
	}

	public static EnumItemGenericCrushed getEnumFromID(int id) {
		for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
			EnumItemGenericCrushed enumGeneric = EnumItemGenericCrushed.VALUES[i];
			if(enumGeneric.id == id) return enumGeneric;
		}
		return EnumItemGenericCrushed.INVALID;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (stack.getItemDamage() == EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED.id) {
			Block block = world.getBlock(x, y, z);
			if (block instanceof IGrowable) {
				if(!world.isRemote) {
					((IGrowable)block).func_149853_b(world, world.rand, x, y, z);
					--stack.stackSize;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public String manualName(int meta) {
		return "genericItemsCrushed";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{3};
	}

	@Override
	public int metas() {
		return 43;
	}

	public static enum EnumItemGenericCrushed {
		INVALID("invalid", 1024),
		GROUND_GENERIC_LEAF("groundGenericLeaf", 0), GROUND_CATTAIL("groundCatTail", 1), GROUND_SWAMP_GRASS_TALL("groundSwampTallgrass", 2), GROUND_SHOOTS("groundShoots", 3), 
		GROUND_ARROW_ARUM("groundArrowArum", 4), GROUND_BUTTON_BUSH("groundButtonBush", 5), GROUND_MARSH_HIBISCUS("groundMarshHibiscus", 6), 
		GROUND_PICKEREL_WEED("groundPickerelWeed", 7), GROUND_SOFT_RUSH("groundSoftRush", 8), GROUND_MARSH_MALLOW("groundMarshMallow", 9), 
		GROUND_MILKWEED("groundMilkweed", 10), GROUND_BLUE_IRIS("groundBlueIris", 11), GROUND_COPPER_IRIS("groundCopperIris", 12), GROUND_BLUE_EYED_GRASS("groundBlueEyedGrass", 13), 
		GROUND_BONESET("groundBoneset", 14), GROUND_BOTTLE_BRUSH_GRASS("groundBottleBrushGrass", 15), GROUND_WEEDWOOD_BARK("groundWeedwoodBark", 16),
		GROUND_DRIED_SWAMP_REED("groundDriedSwampReed", 17), GROUND_ALGAE("groundAlgae", 18), GROUND_ANGLER_TOOTH("groundAnglerTooth", 19), 
		GROUND_BLACKHAT_MUSHROOM("groundBlackHatMushroom", 20), GROUND_BLOOD_SNAIL_SHELL("groundBloodSnailShell", 21), GROUND_BOG_BEAN("groundBogBean", 22),
		GROUND_BROOM_SEDGE("groundBroomSedge", 23), GROUND_BULB_CAPPED_MUSHROOM("groundBulbCappedMushroom", 24), GROUND_CARDINAL_FLOWER("groundCardinalFlower", 25),
		GROUND_CAVE_GRASS("groundCaveGrass", 26), GROUND_CAVE_MOSS("groundCaveMoss", 27), GROUND_CRIMSON_MIDDLE_GEM("groundCrimsonMiddleGem", 28),
		GROUND_DEEP_WATER_CORAL("groundDeepWaterCoral", 29), GROUND_FLATHEAD_MUSHROOM("groundFlatheadMushroom", 30), GROUND_GOLDEN_CLUB("groundGoldenClub", 31),
		GROUND_GREEN_MIDDLE_GEM("groundGreenMiddleGem", 32), GROUND_HANGER("groundHanger", 33), GROUND_LICHEN("groundLichen", 34), GROUND_MARSH_MARIGOLD("groundMarshMarigold", 35),
		GROUND_MIRE_CORAL("groundMireCoral", 36), GROUND_MIRE_SNAIL_SHELL("groundMireSnailShell", 37), GROUND_MOSS("groundMoss", 38), GROUND_NETTLE("groundNettle", 39),
		GROUND_PHRAGMITES("groundPhragmites", 40), GROUND_SLUDGECREEP("groundSludgecreep", 41), GROUND_SUNDEW("groundSundew", 42), GROUND_SWAMP_KELP("groundSwampKelp", 43),
		GROUND_TANGLED_ROOTS("groundTangledRoot", 44), GROUND_AQUA_MIDDLE_GEM("groundAquaMiddleGem", 45), GROUND_PITCHER_PLANT("groundPitcherPlant", 46), 
		GROUND_WATER_WEEDS("groundWaterWeeds", 47), GROUND_VENUS_FLY_TRAP("groundVenusFlyTrap", 48), GROUND_VOLARPAD("groundVolarpad", 49), GROUND_THORNS("groundThorns", 50),
		GROUND_POISON_IVY("groundPoisonIvy", 51);

		public final String iconName;
		public final int id;

		private EnumItemGenericCrushed(String unlocName, int id) {
			iconName = unlocName;
			this.id = id;
		}

		public static final EnumItemGenericCrushed[] VALUES = values();
	}

}
