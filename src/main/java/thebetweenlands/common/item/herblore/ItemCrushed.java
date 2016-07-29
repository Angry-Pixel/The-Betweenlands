package thebetweenlands.common.item.herblore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.CaseFormat;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.common.item.ICustomJsonGenerationItem;
import thebetweenlands.common.item.IGenericItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemCrushed extends Item implements ICustomJsonGenerationItem, ItemRegistry.ISubItemsItem {
	public ItemCrushed() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (stack.getItemDamage() == EnumItemCrushed.GROUND_DRIED_SWAMP_REED.ordinal()) {
			Block block = worldIn.getBlockState(pos).getBlock();
			if (block instanceof IGrowable) {
				IGrowable growable = (IGrowable) block;
				if (growable.canGrow(worldIn, pos, worldIn.getBlockState(pos), worldIn.isRemote)) {
					if (!worldIn.isRemote) {
						if (growable.canUseBonemeal(worldIn, worldIn.rand, pos, worldIn.getBlockState(pos))) {
							growable.grow(worldIn, worldIn.rand, pos, worldIn.getBlockState(pos));
						}
						--stack.stackSize;
					}
					return EnumActionResult.SUCCESS;
				}
			}
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public String getJsonText(String itemNAme) {
		return String.format(JsonRenderGenerator.ITEM_DEFAULT_FORMAT, "strictlyHerblore/ground/" + itemNAme);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (EnumItemCrushed type : EnumItemCrushed.values())
			list.add(type.create(1));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands." + IGenericItem.getFromStack(EnumItemCrushed.class, stack).getUnlocalizedName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknownCrushed";
		}
	}

    @Override
    public List<ResourceLocation> getModels() {
        return Stream.of(EnumItemCrushed.values()).map(t -> new ResourceLocation(ModInfo.ID, t.getModelName())).collect(Collectors.toList());
    }

	public enum EnumItemCrushed implements IGenericItem {
		GROUND_GENERIC_LEAF,
		GROUND_CATTAIL,
		GROUND_SWAMP_GRASS_TALL,
		GROUND_SHOOTS,
		GROUND_ARROW_ARUM,
		GROUND_BUTTON_BUSH,
		GROUND_MARSH_HIBISCUS,
		GROUND_PICKEREL_WEED,
		GROUND_SOFT_RUSH,
		GROUND_MARSH_MALLOW,
		GROUND_MILKWEED,
		GROUND_BLUE_IRIS,
		GROUND_COPPER_IRIS,
		GROUND_BLUE_EYED_GRASS,
		GROUND_BONESET,
		GROUND_BOTTLE_BRUSH_GRASS,
		GROUND_WEEDWOOD_BARK,
		GROUND_DRIED_SWAMP_REED,
		GROUND_ALGAE,
		GROUND_ANGLER_TOOTH,
		GROUND_BLACKHAT_MUSHROOM,
		GROUND_BLOOD_SNAIL_SHELL,
		GROUND_BOG_BEAN,
		GROUND_BROOM_SEDGE,
		GROUND_BULB_CAPPED_MUSHROOM,
		GROUND_CARDINAL_FLOWER,
		GROUND_CAVE_GRASS,
		GROUND_CAVE_MOSS,
		GROUND_CRIMSON_MIDDLE_GEM,
		GROUND_DEEP_WATER_CORAL,
		GROUND_FLATHEAD_MUSHROOM,
		GROUND_GOLDEN_CLUB,
		GROUND_GREEN_MIDDLE_GEM,
		GROUND_HANGER,
		GROUND_LICHEN,
		GROUND_MARSH_MARIGOLD,
		GROUND_MIRE_CORAL,
		GROUND_MIRE_SNAIL_SHELL,
		GROUND_MOSS,
		GROUND_NETTLE,
		GROUND_PHRAGMITES,
		GROUND_SLUDGECREEP,
		GROUND_SUNDEW,
		GROUND_SWAMP_KELP,
		GROUND_TANGLED_ROOTS,
		GROUND_AQUA_MIDDLE_GEM,
		GROUND_PITCHER_PLANT,
		GROUND_WATER_WEEDS,
		GROUND_VENUS_FLY_TRAP,
		GROUND_VOLARPAD,
		GROUND_THORNS,
		GROUND_POISON_IVY;

		private final String unlocalizedName;
		private final String modelName;

		EnumItemCrushed() {
			this.modelName = this.name().toLowerCase(Locale.ENGLISH);
			this.unlocalizedName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.modelName);
		}

		@Override
		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

        @Override
        public String getModelName() {
            return this.modelName;
        }

		@Override
		public int getID() {
			return this.ordinal();
		}

		@Override
		public Item getItem() {
			return ItemRegistry.ITEMS_CRUSHED;
		}
	}
}
