package thebetweenlands.common.item.herblore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.List;

public class ItemDentrothystVial extends Item {

    public ItemDentrothystVial() {

        this.setHasSubtypes(true);
        this.setMaxDamage(0);

    }


    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, NonNullList list) {
        list.add(new ItemStack(item, 1, 0)); //green
        list.add(new ItemStack(item, 1, 1)); //green dirty
        list.add(new ItemStack(item, 1, 2)); //orange
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            switch(stack.getItemDamage()) {
                case 0:
                    return "item.thebetweenlands.elixir.dentrothystVial.green";
                case 1:
                    return "item.thebetweenlands.elixir.dentrothystVial.dirty";
                case 2:
                    return "item.thebetweenlands.elixir.dentrothystVial.orange";
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
/* tODO add vial
    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return world.getBlockState(pos).getBlock() == BlockRegistry.VIAL;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
    }*/
}
