package thebetweenlands.common.item.herblore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockAspectVial;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.util.ColorUtils;
import thebetweenlands.util.TranslationHelper;

public class ItemAspectVial extends Item implements ITintedItem, ItemRegistry.IMultipleItemModelDefinition {
    public ItemAspectVial() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);

        this.setCreativeTab(BLCreativeTabs.HERBLORE);
        this.setContainerItem(ItemRegistry.DENTROTHYST_VIAL);
        addPropertyOverride(new ResourceLocation("aspect"), (stack, worldIn, entityIn) -> {
            List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
            if (GuiScreen.isShiftKeyDown() && itemAspects.size() >= 1) {
                return AspectRegistry.ASPECT_TYPES.indexOf(itemAspects.get(0).type) + 1;
            }
            return 0;
        });
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();

        if (itemAspects.size() >= 1) {
            Aspect aspect = itemAspects.get(0);
            return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".filled.name", aspect.type.getName(), aspect.getRoundedDisplayAmount()).trim();
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            list.add(new ItemStack(this, 1, 0)); //green
            list.add(new ItemStack(this, 1, 1)); //orange

            //Add all aspects
            for (IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
                ItemStack stackGreen = new ItemStack(this, 1, 0);
                ItemAspectContainer greenAspectContainer = ItemAspectContainer.fromItem(stackGreen);
                greenAspectContainer.add(aspect, 2000);
                list.add(stackGreen);
                ItemStack stackOrange = new ItemStack(this, 1, 1);
                ItemAspectContainer orangeAspectContainer = ItemAspectContainer.fromItem(stackOrange);
                orangeAspectContainer.add(aspect, 2000);
                list.add(stackOrange);
            }
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
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
        ItemStack containerEmpty;
        switch (itemStack.getItemDamage()) {
            default:
            case 0:
                containerEmpty = ItemRegistry.DENTROTHYST_VIAL.createStack(0);
                break;
            case 1:
                containerEmpty = ItemRegistry.DENTROTHYST_VIAL.createStack(2);
                break;
        }
        return containerEmpty;
    }

    @Override
    public Map<Integer, ResourceLocation> getModels() {
        Map<Integer, ResourceLocation> models = new HashMap<>();
        models.put(0, new ResourceLocation(getRegistryName().toString() + "_green"));
        models.put(1, new ResourceLocation(getRegistryName().toString() + "_orange"));
        return models;
    }

    @Override
    public int getColorMultiplier(ItemStack stack, int tintIndex) {
        switch(tintIndex){
            case 0:
                List<Aspect> aspects = ItemAspectContainer.fromItem(stack).getAspects();
                if(aspects.size() > 0) {
                    Aspect aspect = aspects.get(0);
                    float[] aspectRGBA = ColorUtils.getRGBA(aspect.type.getColor());
                    return ColorUtils.toHex(aspectRGBA[0], aspectRGBA[1], aspectRGBA[2], 1.0F);
                }
                return 0xFFFFFFFF;
            case 2:
                return 0xFFFFFFFF;
        }
        return 0xFFFFFFFF;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (world != null) {
            List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
            if (!itemAspects.isEmpty()) {
                if(itemAspects.get(0).type == AspectRegistry.BYARIIS) {
                    tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.aspectvial.byariis.fuel"));
                } else if(itemAspects.get(0).type == AspectRegistry.FREIWYNN) {
                    tooltip.add(TranslationHelper.translateToLocal("tooltip.bl.aspectvial.freiwynn"));
                }
            }
        }
    }

    /**
     * Places an aspect vial with the specified aspects in it (can be null)
     * @param pos
     * @param vialType Vial type: 0: green, 1: orange
     * @param aspect
     */
    public static void placeAspectVial(World world, BlockPos pos, int vialType, Aspect aspect) {
        world.setBlockState(pos, BlockRegistry.ASPECT_VIAL_BLOCK.getDefaultState().withProperty(BlockAspectVial.TYPE, BlockDentrothyst.EnumDentrothyst.values()[vialType]), 2);
        TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(pos);
        if(tile != null)
            tile.setAspect(aspect);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return world.getBlockState(pos).getBlock() == BlockRegistry.ASPECT_VIAL_BLOCK;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
        if(player.isSneaking() && itemAspects.size() == 1 && facing == EnumFacing.UP) {
            if(world.isAirBlock(pos.up()) && BlockRegistry.ASPECT_VIAL_BLOCK.canPlaceBlockAt(world, pos.up())) {
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
