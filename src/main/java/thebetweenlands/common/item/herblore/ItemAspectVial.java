package thebetweenlands.common.item.herblore;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.handler.ScreenRenderHandler;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedRecipeHelper;

import javax.annotation.Nullable;

public class ItemAspectVial extends Item {
    public ItemAspectVial() {
        this.setUnlocalizedName("item.thebetweenlands.aspectVial");

        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);

        setCreativeTab(null);
        this.setContainerItem(ItemRegistry.DENTROTHYST_VIAL);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.RED + "Not yet implemented!");
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
    public int getColorFromItemStack(ItemStack stack, int pass) {
        switch(pass){
            case 0:
                //Liquid
                List<Aspect> aspects = AspectManager.getDynamicAspects(stack);
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

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 0 ? this.iconLiquid : super.getIconFromDamageForRenderPass(damage, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return damage % 2 == 0 ? this.itemIcon : this.iconVialOrange;
    }

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
                    return "item.thebetweenlands.aspectVial.green";
                case 1:
                    return "item.thebetweenlands.aspectVial.orange";
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

    /*
    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lst, boolean debug) {
        if(!AspectManager.getDynamicAspects(stack).isEmpty() && AspectManager.getDynamicAspects(stack).get(0).type == AspectRegistry.BYARIIS) {
            lst.add(StatCollector.translateToLocal("aspectvial.byariis.fuel"));
        }
    }

    /**
     * Places an aspect vial with the specified aspects in it (can be null)
     * @param x
     * @param y
     * @param z
     * @param vialType Vial type: 0: green, 1: orange
     * @param aspect
     */
    /*TODO vial block
    public static void placeAspectVial(World world, int x, int y, int z, int vialType, Aspect aspect) {
        world.setBlock(x, y, z, BLBlockRegistry.vial, vialType, 2);
        TileEntityAspectVial tile = (TileEntityAspectVial) world.getTileEntity(x, y, z);
        if(tile != null)
            tile.setAspect(aspect);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return world.getBlockState(pos).getBlock() == BlockRegistry.VIAL;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        List<Aspect> itemAspects = AspectManager.get(world).getStaticAspects(stack);
        if(player.isSneaking() && itemAspects.size() == 1 && facing == EnumFacing.UP) {
            if(world.isAirBlock(pos.up())) {
                if(!world.isRemote) {
                    ItemAspectVial.placeAspectVial(world, pos.up(), stack.getItemDamage(), itemAspects.get(0));
                    stack.stackSize--;
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }*/
}
