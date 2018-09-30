package thebetweenlands.common.block.container;

import net.minecraft.block.BlockJukebox;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.misc.ItemBLRecord;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockWeedwoodJukebox extends BlockJukebox {
    public BlockWeedwoodJukebox() {
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        ItemStack itemstack = ((BlockJukebox.TileEntityJukebox) world.getTileEntity(pos)).getRecord();
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemBLRecord)
            return Item.getIdFromItem(itemstack.getItem()) + 1 - Item.getIdFromItem(ItemRegistry.ASTATOS);
        else if (!itemstack.isEmpty() && itemstack.getItem() == ItemRegistry.GERTS_DONUT)
            return 15;
        return 0;
    }
}
