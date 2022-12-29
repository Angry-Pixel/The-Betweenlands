package thebetweenlands.common.item.herblore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemCrushed extends Item implements ItemRegistry.IMultipleItemModelDefinition {
	public ItemCrushed() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() == EnumItemCrushed.GROUND_DRIED_SWAMP_REED.ordinal()) {
			Block block = worldIn.getBlockState(pos).getBlock();
			if (block instanceof IGrowable) {
				IGrowable growable = (IGrowable) block;
				if (growable.canGrow(worldIn, pos, worldIn.getBlockState(pos), worldIn.isRemote)) {
					if (!worldIn.isRemote) {
						if (growable.canUseBonemeal(worldIn, worldIn.rand, pos, worldIn.getBlockState(pos))) {
							growable.grow(worldIn, worldIn.rand, pos, worldIn.getBlockState(pos));
						}
						stack.shrink(1);
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (EnumItemCrushed type : EnumItemCrushed.values())
				items.add(type.create(1));
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumItemCrushed.class, stack).getTranslationKey();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown_crushed";
		}
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> models = new HashMap<>();
		for(EnumItemCrushed type : EnumItemCrushed.values())
			models.put(type.getID(), new ResourceLocation(ModInfo.ID, type.getModelName()));
		return models;
	}

	public enum EnumItemCrushed implements IGenericItem {
		GROUND_GENERIC_LEAF(0),
		GROUND_CATTAIL(1),
		GROUND_SWAMP_GRASS_TALL(2),
		GROUND_SHOOTS(3),
		GROUND_ARROW_ARUM(4),
		GROUND_BUTTON_BUSH(5),
		GROUND_MARSH_HIBISCUS(6),
		GROUND_PICKEREL_WEED(7),
		GROUND_SOFT_RUSH(8),
		GROUND_MARSH_MALLOW(9),
		GROUND_MILKWEED(10),
		GROUND_BLUE_IRIS(11),
		GROUND_COPPER_IRIS(12),
		GROUND_BLUE_EYED_GRASS(13),
		GROUND_BONESET(14),
		GROUND_BOTTLE_BRUSH_GRASS(15),
		GROUND_WEEDWOOD_BARK(16),
		GROUND_DRIED_SWAMP_REED(17),
		GROUND_ALGAE(18),
		GROUND_ANGLER_TOOTH(19),
		GROUND_BLACKHAT_MUSHROOM(20),
		GROUND_BLOOD_SNAIL_SHELL(21),
		GROUND_BOG_BEAN(22),
		GROUND_BROOM_SEDGE(23),
		GROUND_BULB_CAPPED_MUSHROOM(24),
		GROUND_CARDINAL_FLOWER(25),
		GROUND_CAVE_GRASS(26),
		GROUND_CAVE_MOSS(27),
		GROUND_CRIMSON_MIDDLE_GEM(28),
		GROUND_DEEP_WATER_CORAL(29),
		GROUND_FLATHEAD_MUSHROOM(30),
		GROUND_GOLDEN_CLUB(31),
		GROUND_GREEN_MIDDLE_GEM(32),
		GROUND_HANGER(33),
		GROUND_LICHEN(34),
		GROUND_MARSH_MARIGOLD(35),
		GROUND_MIRE_CORAL(36),
		GROUND_MIRE_SNAIL_SHELL(37),
		GROUND_MOSS(38),
		GROUND_NETTLE(39),
		GROUND_PHRAGMITES(40),
		GROUND_SLUDGECREEP(41),
		GROUND_SUNDEW(42),
		GROUND_SWAMP_KELP(43),
		GROUND_ROOTS(44),
		GROUND_AQUA_MIDDLE_GEM(45),
		GROUND_PITCHER_PLANT(46),
		GROUND_WATER_WEEDS(47),
		GROUND_VENUS_FLY_TRAP(48),
		GROUND_VOLARPAD(49),
		GROUND_THORNS(50),
		GROUND_POISON_IVY(51),
		GROUND_BLADDERWORT_FLOWER(52),
		GROUND_BLADDERWORT_STALK(53),
		GROUND_EDGE_SHROOM(54),
		GROUND_EDGE_MOSS(55),
		GROUND_EDGE_LEAF(56),
		GROUND_ROTBULB(57),
		GROUND_PALE_GRASS(58),
		GROUND_STRING_ROOTS(59),
		GROUND_CRYPTWEED(60),
		GROUND_BETWEENSTONE_PEBBLE(61);

		private final int id;
		private final String unlocalizedName;
		private final String modelName;

		EnumItemCrushed(int id) {
			this.id = id;
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = this.modelName;
		}

		@Override
		public String getTranslationKey() {
			return this.unlocalizedName;
		}

		@Override
		public String getModelName() {
			return this.modelName;
		}

		@Override
		public int getID() {
			return this.id;
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_CRUSHED;
		}
	}
}
