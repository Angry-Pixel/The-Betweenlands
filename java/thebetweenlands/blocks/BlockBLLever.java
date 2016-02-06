package thebetweenlands.blocks;

import net.minecraft.block.BlockLever;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 17-6-2015.
 */
public class BlockBLLever extends BlockLever {
    public BlockBLLever(){
        setCreativeTab(BLCreativeTabs.blocks);
        this.setBlockName("thebetweenlands.weedwoodLever");
        setBlockTextureName("thebetweenlands:weedwoodLever");
    }

    @Override
    public int getRenderType() {
        return ClientProxy.BlockRenderIDs.LEVER.id();
    }
}
