package thebetweenlands.common.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BasicBlock extends Block {
    private Supplier<Item> itemDropped;

    public BasicBlock(Material material) {
        super(material);
        this.setCreativeTab(BLCreativeTabs.BLOCKS);
    }

    public BasicBlock setSoundType2(SoundType sound) {
        super.setSoundType(sound);
        return this;
    }

    public BasicBlock setHarvestLevel2(String toolClass, int level) {
        super.setHarvestLevel(toolClass, level);
        return this;
    }

    public BasicBlock setDefaultCreativeTab() {
        super.setCreativeTab(BLCreativeTabs.BLOCKS);
        return this;
    }

    public BasicBlock setItemDropped(Supplier<Item> itemDropped) {
        this.itemDropped = itemDropped;
        return this;
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
        if (itemDropped != null)
            return itemDropped.get();
        else
            return Items.AIR;
    }
}
