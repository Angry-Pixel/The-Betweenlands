package thebetweenlands.common.block.terrain;


import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockBetweenlandsBedrock extends Block {
    public BlockBetweenlandsBedrock() {
        super(Material.ROCK);
        setResistance(6000000.0F);
        setSoundType(SoundType.STONE);
        disableStats();
        setCreativeTab(BLCreativeTabs.BLOCKS);
        setBlockUnbreakable();
    }
}
