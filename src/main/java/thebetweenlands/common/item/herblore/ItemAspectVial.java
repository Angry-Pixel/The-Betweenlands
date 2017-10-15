package thebetweenlands.common.item.herblore;

import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.handler.ScreenRenderHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedRecipeHelper;
import thebetweenlands.util.ColorUtils;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;

public class ItemAspectVial extends Item implements ITintedItem, ItemRegistry.ISingleJsonSubItems {
    public ItemAspectVial() {
        this.setUnlocalizedName("item.thebetweenlands.aspectVial");

        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);

        setCreativeTab(BLCreativeTabs.HERBLORE);
        this.setContainerItem(ItemRegistry.DENTROTHYST_VIAL);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();

        if (itemAspects.size() >= 1) {
            Aspect aspect = itemAspects.get(0);
            return super.getItemStackDisplayName(stack) + " - " + aspect.type.getName() + " (" + ScreenRenderHandler.ASPECT_AMOUNT_FORMAT.format(aspect.getDisplayAmount()) + ")";
        }
        return super.getItemStackDisplayName(stack);
    }


    /* TODO all this stuff

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        if(pass == 2) {
            List<Aspect> itemAspects = AspectManager.getDynamicAspects(stack);
            if(itemAspects.size() >= 1) {
                Aspect aspect = itemAspects.get(0);
                return this.aspectIcons[aspect.type.getIconIndex()];
            }
        }
        return super.getIcon(stack, pass);
    }
*/

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            list.add(new ItemStack(this, 1, 0)); //green
            list.add(new ItemStack(this, 1, 1)); //orange

            //Add all aspects
            for (IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
                ItemStack stackGreen = new ItemStack(this, 1, 0);
                ItemAspectContainer greenAspectContainer = ItemAspectContainer.fromItem(stackGreen);
                greenAspectContainer.add(aspect, 400);
                list.add(stackGreen);
                ItemStack stackOrange = new ItemStack(this, 1, 1);
                ItemAspectContainer orangeAspectContainer = ItemAspectContainer.fromItem(stackOrange);
                orangeAspectContainer.add(aspect, 400);
                list.add(stackOrange);
            }
        }
    }


    /*@Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata) {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 3 : 2;
    }*/

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            switch (stack.getItemDamage()) {
                case 0:
                    return "item.thebetweenlands.aspect_vial.green";
                case 1:
                    return "item.thebetweenlands.aspect_vial.orange";
            }
        } catch (Exception e) {
        }
        return "item.thebetweenlands.unknown";
    }


    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack containerDefault;
        switch (itemStack.getItemDamage()) {
            default:
            case 0:
                containerDefault = ItemRegistry.DENTROTHYST_VIAL.createStack(0);
                break;
            case 1:
                containerDefault = ItemRegistry.DENTROTHYST_VIAL.createStack(2);
                break;
        }
        ItemStack containerRubberBoots = AdvancedRecipeHelper.getContainerItem(itemStack, null, "rubberBoots");
        ItemStack containerBait = AdvancedRecipeHelper.getContainerItem(itemStack, null, "bait");
        return containerRubberBoots != null ? containerRubberBoots : (containerBait != null ? containerBait : containerDefault);
    }

    @Override
    public List<String> getTypes() {
        return Lists.newArrayList("green", "orange");
    }


    @Override
    public int getColorMultiplier(ItemStack stack, int tintIndex) {
        //TODO get color for the liquid somehow
        /*switch(tintIndex){
            case 0:
                //Liquid
                List<Aspect> aspects = AspectManager.get(world).getStaticAspects(stack);
                if(aspects.size() > 0) {
                    Aspect aspect = aspects.get(0);
                    float[] aspectRGBA = ColorUtils.getRGBA(aspect.type.getColor());
                    return ColorUtils.toHex(aspectRGBA[0], aspectRGBA[1], aspectRGBA[2], 1.0F);
                }
                return 0xFFFFFFFF;
            case 2:
                return 0xFFFFFFFF;
        }*/
        return 0xFFFFFFFF;
    }

    /*@Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
        return false;
    }*/

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (world != null) {
            List<Aspect> itemAspects = AspectManager.get(world).getStaticAspects(stack);
            if (!itemAspects.isEmpty() && itemAspects.get(0).type == AspectRegistry.BYARIIS) {
                tooltip.add(TranslationHelper.translateToLocal("aspectvial.byariis.fuel"));
            }
        }
        tooltip.add(TextFormatting.RED + "Not yet implemented!");
    }

    /**
     * Places an aspect vial with the specified aspects in it (can be null)
     * @param pos
     * @param vialType Vial type: 0: green, 1: orange
     * @param aspect
     */
    public static void placeAspectVial(World world, BlockPos pos, int vialType, Aspect aspect) {
        //TODO vial block
        /*world.setBlock(x, y, z, BLBlockRegistry.vial, vialType, 2);
        TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(pos);
        if(tile != null)
            tile.setAspect(aspect);*/
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        //TODO vial block
        return true;//world.getBlockState(pos).getBlock() == BlockRegistry.VIAL;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        List<Aspect> itemAspects = AspectManager.get(world).getStaticAspects(stack);
        if(player.isSneaking() && itemAspects.size() == 1 && facing == EnumFacing.UP) {
            if(world.isAirBlock(pos.up())) {
                if(!world.isRemote) {
                    ItemAspectVial.placeAspectVial(world, pos.up(), stack.getItemDamage(), itemAspects.get(0));
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }
}
