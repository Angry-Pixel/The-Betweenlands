package thebetweenlands.common.item.herblore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDentrothystVial extends Item implements ItemRegistry.ISingleJsonSubItems {

    public ItemDentrothystVial() {
        this.setCreativeTab(BLCreativeTabs.HERBLORE);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (isInCreativeTab(tab)) {
            list.add(new ItemStack(this, 1, 0)); //green
            list.add(new ItemStack(this, 1, 1)); //green dirty
            list.add(new ItemStack(this, 1, 2)); //orange
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String s = "item.thebetweenlands.elixir.dentrothyst_vial.";
        try {
            switch(stack.getItemDamage()) {
                case 0:
                    return s + "green";
                case 1:
                    return s + "dirty";
                case 2:
                    return s + "orange";
            }
        } catch (Exception e) { }
        return "item.thebetweenlands.unknown";
    }

    /**
     * Creates an item stack of the specified vial type.
     * Vial types: 0 = green, 1 = dirty, 2 = orange
     * @param vialType
     * @return
     */
    public ItemStack createStack(int vialType) {
        return new ItemStack(this, 1, vialType);
    }

    /**
     * Creates an item stack of the specified vial type.
     * Vial types: 0 = green, 1 = dirty, 2 = orange
     * @param vialType
     * @param size
     * @return
     */
    public ItemStack createStack(int vialType, int size) {
        return new ItemStack(this, size, vialType);
    }

    @Override
    public List<String> getTypes() {
        List<String> models = new ArrayList<String>();
        models.add("green");
        models.add("dirty");
        models.add("orange");
        return models;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        //TODO vial block
        return true; //world.getBlockState(pos).getBlock() == BlockRegistry.VIAL;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if(player.isSneaking() && facing == EnumFacing.UP && stack.getItemDamage() != 1) {
            if(world.isAirBlock(pos.up())) {
                if(!world.isRemote) {
                    ItemAspectVial.placeAspectVial(world, pos.up(), stack.getItemDamage() == 2 ? 1 : 0, null);
                    stack.shrink(1);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }
}
