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
import thebetweenlands.common.item.ICustomResourceLocationItem;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemGenericCrushed extends Item implements ICustomResourceLocationItem {
    public ItemGenericCrushed() {
        setMaxDamage(0);
        setHasSubtypes(true);
        this.setUnlocalizedName("thebetweenlands.unknownCrushed");
    }

    public static ItemStack createStack(EnumItemGenericCrushed enumCrushed) {
        return createStack(enumCrushed, 1);
    }

    public static ItemStack createStack(EnumItemGenericCrushed enumCrushed, int size) {
        return new ItemStack(ItemRegistry.itemsGenericCrushed, size, enumCrushed.id);
    }

    public static ItemStack createStack(Item item, int size, int meta) {
        return new ItemStack(item, size, meta);
    }

    public static EnumItemGenericCrushed getEnumFromID(int id) {
        for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
            EnumItemGenericCrushed enumGeneric = EnumItemGenericCrushed.VALUES[i];
            if (enumGeneric.id == id) return enumGeneric;
        }
        return EnumItemGenericCrushed.INVALID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < EnumItemGenericCrushed.VALUES.length; i++) {
            if (EnumItemGenericCrushed.VALUES[i] != EnumItemGenericCrushed.INVALID)
                list.add(new ItemStack(item, 1, EnumItemGenericCrushed.VALUES[i].id));
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
        if (stack.getItemDamage() == EnumItemGenericCrushed.GROUND_DRIED_SWAMP_REED.id) {
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
    public String getCustomResourceLocation(int meta) {
        return "strictlyHerblore/ground/" + getEnumFromID(meta).name;
    }

    public enum EnumItemGenericCrushed {
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

        public static final EnumItemGenericCrushed[] VALUES = values();
        public final String name;
        public final int id;

        EnumItemGenericCrushed(String unlocName, int id) {
            name = unlocName;
            this.id = id;
        }
    }
}
