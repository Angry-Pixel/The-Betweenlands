package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntitySporeling;

public class BlockFungusCrop extends BlockGenericCrop {
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(this.isDecayed(worldIn, pos)) {
			if(!worldIn.isRemote && rand.nextInt(6) == 0) {
				EntitySporeling sporeling = new EntitySporeling(worldIn);
				sporeling.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
				worldIn.spawnEntityInWorld(sporeling);
				worldIn.setBlockToAir(pos);
			}
		} else {
			super.updateTick(worldIn, pos, state, rand);
		}
	}
}
