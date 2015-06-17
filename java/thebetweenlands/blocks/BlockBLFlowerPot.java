package thebetweenlands.blocks;

import net.minecraft.block.BlockFlowerPot;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 17-6-2015.
 */
public class BlockBLFlowerPot extends BlockFlowerPot {
    public BlockBLFlowerPot(){
        setCreativeTab(ModCreativeTabs.blocks);
        this.setBlockName("thebetweenlands.mudFlowerPot");
        setBlockTextureName("thebetweenlands:mudFlowerPot");
    }
}
