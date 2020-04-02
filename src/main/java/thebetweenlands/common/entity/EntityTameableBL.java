package thebetweenlands.common.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public abstract class EntityTameableBL extends EntityTameable implements IEntityBL {
	public EntityTameableBL(World worldIn) {
		super(worldIn);
	}

	@Override
	public boolean getCanSpawnHere() {
		IBlockState state = this.world.getBlockState((new BlockPos(this)).down());
		return state.canEntitySpawn(this) && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
	}
	
	@Override
	public float getBlockPathWeight(BlockPos pos) {
        return 0.0F;
    }
}
