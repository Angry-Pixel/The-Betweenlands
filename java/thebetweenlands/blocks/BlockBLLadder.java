package thebetweenlands.blocks;

import net.minecraft.block.BlockLadder;
import thebetweenlands.creativetabs.BLCreativeTabs;

/**
 * Created by Bart on 16-6-2015.
 */
public class BlockBLLadder extends BlockLadder {

    public BlockBLLadder(String name){
        this.setBlockName("thebetweenlands." + name + "Ladder");
        setCreativeTab(BLCreativeTabs.blocks);
        this.setBlockTextureName("thebetweenlands:" + name + "Ladder");
        this.setHardness(1.0F);
        this.setStepSound(soundTypeLadder);
    }
}
