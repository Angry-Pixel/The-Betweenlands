package thebetweenlands.items.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;
import thebetweenlands.blocks.BlockBLSlabPlanks;
import thebetweenlands.blocks.BlockBLSlabStone;

public class ItemBlockSlab extends ItemSlab {

    public ItemBlockSlab(Block block, BlockBLSlabPlanks singleSlab, BlockBLSlabPlanks doubleSlab, Boolean isDoubleSlab) {
        super(block, singleSlab, doubleSlab, isDoubleSlab);
    }
    
    public ItemBlockSlab(Block block, BlockBLSlabStone singleSlab, BlockBLSlabStone doubleSlab, Boolean isDoubleSlab) {
        super(block, singleSlab, doubleSlab, isDoubleSlab);
    }

}
