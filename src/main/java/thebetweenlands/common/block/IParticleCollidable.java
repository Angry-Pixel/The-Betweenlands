package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IParticleCollidable {

     void onParticleCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, EnumFacing side, Particle particle);
}
