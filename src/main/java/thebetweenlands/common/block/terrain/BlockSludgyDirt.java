package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.events.EventSpoopy;

import java.util.Random;

public class BlockSludgyDirt extends Block
{


    public BlockSludgyDirt() {
        super(Material.GRASS);
        setHardness(0.5F);
        setSoundType(SoundType.SAND);
        setHarvestLevel("shovel", 0);
        setCreativeTab(BLCreativeTabs.BLOCKS);
        //setBlockName("thebetweenlands.sludgyDirt");
        setTickRandomly(true);
    }


    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return BlockRegistry.SWAMP_DIRT.getItemDropped(state, rand, fortune);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World world, BlockPos pos) {
        float f = 0.125F;
        return new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)((float)(pos.getY() + 1) - f), (double)(pos.getZ() + 1));
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity){
        if(entity instanceof IEntityBL == false) entity.setInWeb();
    }


    @Override
    public boolean isOpaqueCube(IBlockState s) {
        return false;
    }

    //@Override
    //public int getRenderBlockPass (){return 1;}

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess iblockaccess, BlockPos pos, EnumFacing side) {
        Block block = iblockaccess.getBlockState(pos).getBlock();
        return block != BlockRegistry.SLUDGY_DIRT;
    }
}
