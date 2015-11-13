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
import thebetweenlands.manual.gui.entries.IManualEntryItem;

public class ItemGenericCrushed extends Item implements IManualEntryItem {
	public static ItemStack createStack(EnumItemGenericCrushed enumCrushed) {
		return createStack(enumCrushed, 1);
	}

	public static ItemStack createStack(EnumItemGenericCrushed enumCrushed, int size) {
		return new ItemStack(BLItemRegistry.itemsGenericCrushed, size, enumCrushed.ordinal());
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
		icons = new IIcon[EnumItemGenericCrushed.VALUES.length];
		for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
			icons[i] = reg.registerIcon("thebetweenlands:strictlyHerblore/ground/" + EnumItemGenericCrushed.VALUES[i].iconName);
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
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + EnumItemGenericCrushed.VALUES[stack.getItemDamage()].iconName;
		} catch (Exception e) {
			return "item.thebetweenlands.unknownCrushed";
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (stack.getItemDamage() == EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED.ordinal()) {
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
		GROUND_CATTAIL("groundCatTail"), GROUND_SWAMP_GRASS_TALL("groundSwampTallgrass"), GROUND_SHOOTS("groundShoots"), 
		GROUND_ARROW_ARUM("groundArrowArum"), GROUND_BUTTON_BUSH("groundButtonBush"), GROUND_MARSH_HIBISCUS("groundMarshHibiscus"), 
		GROUND_PICKEREL_WEED("groundPickerelWeed"), GROUND_SOFT_RUSH("groundSoftRush"), GROUND_MARSH_MALLOW("groundMarshMallow"), 
		GROUND_MILKWEED("groundMilkweed"), GROUND_BLUE_IRIS("groundBlueIris"), GROUND_COPPER_IRIS("groundCopperIris"), GROUND_BLUE_EYED_GRASS("groundBlueEyedGrass"), 
		GROUND_BONESET("groundBoneset"), GROUND_BOTTLE_BRUSH_GRASS("groundBottleBrushGrass"), GROUND_WEEDWOOD_BARK("groundWeedwoodBark"),
		GROUND_DRIED_SWAMP_REED("groundDriedSwampReed"), GROUND_ALGAE("groundAlgae"), GROUND_ANGLER_TOOTH("groundAnglerTooth"), 
		GROUND_BLACKHAT_MUSHROOM("groundBlackHatMushroom"), GROUND_BLOOD_SNAIL_SHELL("groundBloodSnailShell"), GROUND_BOG_BEAN("groundBogBean"),
		GROUND_BROOM_SEDGE("groundBroomSedge"), GROUND_BULB_CAPPED_MUSHROOM("groundBulbCappedMushroom"), GROUND_CARDINAL_FLOWER("groundCardinalFlower"),
		GROUND_CAVE_GRASS("groundCaveGrass"), GROUND_CAVE_MOSS("groundCaveMoss"), GROUND_CRIMSON_MIDDLE_GEM("groundCrimsonMiddleGem"),
		GROUND_DEEP_WATER_CORAL("groundDeepWaterCoral"), GROUND_FLATHEAD_MUSHROOM("groundFlatheadMushroom"), GROUND_GOLDEN_CLUB("groundGoldenClub"),
		GROUND_GREEN_MIDDLE_GEM("groundGreenMiddleGem"), GROUND_HANGER("groundHanger"), GROUND_LICHEN("groundLichen"), GROUND_MARSH_MARIGOLD("groundMarshMarigold"),
		GROUND_MIRE_CORAL("groundMireCoral"), GROUND_MIRE_SNAIL_SHELL("groundMireSnailShell"), GROUND_MOSS("groundMoss"), GROUND_NETTLE("groundNettle"),
		GROUND_PHRAGMITES("groundPhragmites"), GROUND_SLUDGECREEP("groundSludgecreep"), GROUND_SUNDEW("groundSundew"), GROUND_SWAMP_KELP("groundSwampKelp"),
		GROUND_TANGLED_ROOTS("groundTangledRoot"), GROUND_AQUA_MIDDLE_GEM("groundAquaMiddleGem");

		public final String iconName;

		private EnumItemGenericCrushed(String unlocName) {
			iconName = unlocName;
		}

		public static final EnumItemGenericCrushed[] VALUES = values();
	}

}
