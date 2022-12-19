package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.SoundRegistry;

public class BlockGenericCollapsing extends BlockFalling {

    public BlockGenericCollapsing(Material material) {
        super(material);
        fallInstantly = false;
        this.setCreativeTab(BLCreativeTabs.BLOCKS);
    }

    public BlockGenericCollapsing setSoundType2(SoundType sound) {
        super.setSoundType(sound);
        return this;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote)
            createFallingBlock(worldIn, pos);
    }

    private void createFallingBlock(World world, BlockPos pos) {
        if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
            int i = 32;

            if (!fallInstantly && world.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                if (!world.isRemote) {
                    world.playSound((double) pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundRegistry.CRUMBLE, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), world.getBlockState(pos));
                    this.onStartFalling(entityfallingblock);
                    world.spawnEntity(entityfallingblock);
                }
            } else {
                world.setBlockToAir(pos);

                while (canFallThrough(world.getBlockState(pos.down())) && pos.getY() > 0)
                    pos = pos.down();

                if (pos.getY() > 0)
                    world.setBlockState(pos, this.getDefaultState());
            }
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote)
            if(entityIn instanceof EntityPlayer && !entityIn.isSneaking())
                worldIn.scheduleBlockUpdate(pos, this, this.tickRate(worldIn), 0);
    }

    @Override
    public void onEndFalling(World world, BlockPos pos, IBlockState p_176502_3_, IBlockState p_176502_4_) {
        if (!world.isRemote) {
            world.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), blockSoundType.getStepSound(), SoundCategory.BLOCKS, (blockSoundType.getVolume() + 1.0F) / 2.0F, blockSoundType.getPitch() * 0.8F, false);
            world.playEvent(null, 2001, pos.up(), Block.getIdFromBlock(world.getBlockState(pos).getBlock()));
            world.setBlockToAir(pos);
        }
    }
}
