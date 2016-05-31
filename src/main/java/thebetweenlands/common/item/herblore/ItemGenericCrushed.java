package thebetweenlands.common.item.herblore;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.common.item.ICustomJsonGenerationItem;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemGenericCrushed extends Item implements ICustomJsonGenerationItem, ItemRegistry.ISubItemsItem {
    public ItemGenericCrushed() {
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public static ItemStack createStack(EnumItemGenericCrushed enumItemGeneric) {
        return createStack(enumItemGeneric, 1);
    }

    public static ItemStack createStack(EnumItemGenericCrushed enumItemGeneric, int size) {
        return new ItemStack(ItemRegistry.itemsGeneric, size, enumItemGeneric.ordinal());
    }

    public static EnumItemGenericCrushed getEnumFromID(int id) {
        for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
            EnumItemGenericCrushed enumGeneric = EnumItemGenericCrushed.VALUES[i];
            if (enumGeneric.ordinal() == id) return enumGeneric;
        }
        return EnumItemGenericCrushed.INVALID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumItemGenericCrushed itemGeneric : EnumItemGenericCrushed.values()) {
            list.add(new ItemStack(item, 1, itemGeneric.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            return "item.thebetweenlands." + getEnumFromID(stack.getItemDamage()).name;
        } catch (Exception e) {
            return "item.thebetweenlands.unknownCrushed";
        }
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (stack.getItemDamage() == EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED.ordinal()) {
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
    public List<String> getModels() {
        List<String> models = new ArrayList<String>();
        for (EnumItemGenericCrushed type : EnumItemGenericCrushed.values())
            models.add(type.name);
        return models;
    }

    public enum EnumItemGenericCrushed {
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
        GROUND_POISON_IVY,

        //KEEP AT BOTTOM
        INVALID;

        public static final EnumItemGenericCrushed[] VALUES = values();

        public final String name;


        EnumItemGenericCrushed() {
            name = name().toLowerCase(Locale.ENGLISH);
        }
    }
}
