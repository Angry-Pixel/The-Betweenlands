package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.ModCreativeTabs;

/**
 * Created by Bart on 23-10-2015.
 */
public class BlockRope extends Block {
    public BlockRope() {
        super(Material.plants);
        setCreativeTab(ModCreativeTabs.blocks);
        this.setBlockTextureName("thebeteenlands:rope");
        this.setBlockName("thebeteenlands.rope");
    }
}
