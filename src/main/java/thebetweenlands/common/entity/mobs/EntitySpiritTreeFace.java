package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public abstract class EntitySpiritTreeFace extends EntityWallFace {
	public EntitySpiritTreeFace(World world) {
		super(world);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos) {
		return this.world.getBlockState(pos).getBlock() == BlockRegistry.LOG_SPIRIT_TREE;
	}

	@Override
	public boolean canMoveFaceInto(BlockPos pos) {
		return this.world.isAirBlock(pos);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.isMoving() && this.world.isRemote) {
			if(this.ticksExisted % 3 == 0) {
				EnumFacing facing = this.getFacing();
				double px = this.posX + facing.getFrontOffsetX() * this.width / 2;
				double py = this.posY + this.height / 2 + facing.getFrontOffsetY() * this.height / 2;
				double pz = this.posZ + facing.getFrontOffsetZ() * this.width / 2;
				for(int i = 0; i < 24; i++) {
					double rx = (this.world.rand.nextDouble() - 0.5D) * this.width;
					double ry = (this.world.rand.nextDouble() - 0.5D) * this.height;
					double rz = (this.world.rand.nextDouble() - 0.5D) * this.width;
					BlockPos pos = new BlockPos(px + rx, py + ry, pz + rz);
					IBlockState state = this.world.getBlockState(pos);
					if(!state.getBlock().isAir(state, this.world, pos)) {
						double mx = facing.getFrontOffsetX() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						double my = facing.getFrontOffsetY() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						double mz = facing.getFrontOffsetZ() * 0.15F + (this.world.rand.nextDouble() - 0.5D) * 0.25F;
						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + rx, py + ry, pz + rz, mx, my, mz, Block.getStateId(state));
					}
				}
			}
		}
	}
}
