package thebetweenlands.common.entity.mobs;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySpiritTreeFaceSmall extends EntitySpiritTreeFace {
	public EntitySpiritTreeFaceSmall(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		//this.tasks.addTask(0, new EntityAILookIdle(this));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.fixUnsuitablePosition();
	}

	@Override
	protected void fixUnsuitablePosition() {
		if(this.ticksExisted % 10 == 0) {
			for(int i = 0; i < 160; i++) {
				float rx = this.world.rand.nextFloat() * 2 - 1;
				float ry = this.world.rand.nextFloat() * 2 - 1;
				float rz = this.world.rand.nextFloat() * 2 - 1;
				EnumFacing rndDir = EnumFacing.getFacingFromVector(rx, ry, rz);
				BlockPos rndPos = new BlockPos(this.posX + this.world.rand.nextInt(8) - 4, this.posY + this.height / 2 + this.world.rand.nextInt(8) - 4, this.posZ + this.world.rand.nextInt(8) - 4);
				if(this.canAnchorAt(rndPos, rndDir, EnumFacing.UP)) {
					this.lookHelper.setLookPosition(this.posX + rx, this.posY + this.getEyeHeight() + ry, this.posZ + rz, 0, 0);
					this.moveHelper.setMoveTo(rndPos.getX(), rndPos.getY(), rndPos.getZ(), 1);
				}
			}
		}
	}

	@Override
	public boolean isActive() {
		//return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
		return true;
	}
}
