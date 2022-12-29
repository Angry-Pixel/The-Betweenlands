package thebetweenlands.common.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.block.plant.BlockMoss;
import thebetweenlands.common.block.plant.BlockThorns;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockOctine extends BasicBlock {

    public BlockOctine() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setHardness(1.5F);
        setResistance(10.0F);
        setLightLevel(0.875F);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (ItemRegistry.OCTINE_INGOT.isTinder(ItemStack.EMPTY, ItemStack.EMPTY, worldIn.getBlockState(fromPos))) {
            boolean isTouching = true;
            int x = pos.getX() - fromPos.getX();
            int y = pos.getY() - fromPos.getY();
            int z = pos.getZ() - fromPos.getZ();
            EnumFacing facing = EnumFacing.getFacingFromVector(x, y, z);

            IBlockState stateIn = worldIn.getBlockState(fromPos);
            if (stateIn.getBlock() instanceof BlockMoss) {
                if (facing.equals(stateIn.getValue(BlockDirectional.FACING)))
                    isTouching = false;
            } else if (stateIn.getBlock() instanceof BlockThorns) {
                PropertyBool side = null;
                switch (facing.getOpposite()) {
                    case UP:
                        side = BlockThorns.UP;
                        break;
                    case NORTH:
                        side = BlockThorns.NORTH;
                        break;
                    case SOUTH:
                        side = BlockThorns.SOUTH;
                        break;
                    case WEST:
                        side = BlockThorns.WEST;
                        break;
                    case EAST:
                        side = BlockThorns.EAST;
                        break;
                }
                if (side == null || stateIn.getValue(side))
                    isTouching = false;
            }
            if (isTouching)
                worldIn.setBlockState(fromPos, Blocks.FIRE.getDefaultState());
        }
    }

}
