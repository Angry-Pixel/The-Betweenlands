package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockMobSpawner;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockDruidSpawner extends BlockMobSpawner {
    public BlockDruidSpawner() {
        setHardness(10.0F);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }
}
