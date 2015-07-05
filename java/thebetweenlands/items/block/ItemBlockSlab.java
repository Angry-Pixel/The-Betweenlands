package thebetweenlands.items.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;
import thebetweenlands.blocks.BlockBLSlab;

public class ItemBlockSlab extends ItemSlab {
    public ItemBlockSlab(Block block, BlockBLSlab singleSlab, BlockBLSlab doubleSlab, Boolean isDoubleSlab) {
        super(block, singleSlab, doubleSlab, isDoubleSlab);
    }
}
