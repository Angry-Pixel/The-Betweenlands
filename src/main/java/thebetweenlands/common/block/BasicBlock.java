package thebetweenlands.common.block;

import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
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

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (itemDropped != null)
            return itemDropped.get();
        else
            return super.getItemDropped(state, rand, fortune);
    }
}
