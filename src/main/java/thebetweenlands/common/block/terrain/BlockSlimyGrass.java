package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSlimyGrass extends Block
{

    public BlockSlimyGrass() {
        super(Material.GRASS);
        setHardness(0.5F);
        setSoundType(SoundType.PLANT);
        setHarvestLevel("shovel", 0);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        //setBlockName("thebetweenlands.slimyGrass");
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if( !world.isRemote ) {
            if( world.getLight(pos.up()) < 4 && world.getBlockLightOpacity(pos.up()) > 2 ) {
                world.setBlockState(pos, BlockRegistry.SLIMY_DIRT.getDefaultState());
            } else if( world.getLight(pos.up()) >= 9 ) {
                for( int l = 0; l < 4; ++l ) {
                    BlockPos target = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                    Block block = world.getBlockState(target.up()).getBlock();

                    if( world.getBlockState(target).getBlock() == Blocks.DIRT
                            && world.getBlockState(target).getBlock().getMetaFromState(world.getBlockState(target)) == 0
                            && world.getLight(target.up()) >= 4
                            && world.getBlockLightOpacity(target.up()) <= 2 )
                    {
                        world.setBlockState(target, BlockRegistry.SLIMY_GRASS.getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return BlockRegistry.SLIMY_DIRT.getItemDropped(state, rand, fortune);
    }
}
