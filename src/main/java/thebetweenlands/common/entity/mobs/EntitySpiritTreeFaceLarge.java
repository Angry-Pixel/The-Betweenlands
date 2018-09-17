package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySpiritTreeFaceLarge extends EntitySpiritTreeFace {
	public EntitySpiritTreeFaceLarge(World world) {
		super(world);
		this.setSize(1.8F, 1.8F);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		//this.tasks.addTask(0, new EntityAILookIdle(this));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		EntityPlayer player = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 16, false);
		if(player != null)
			this.lookHelper.setLookPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ, 0, 0);

		//this.fixUnsuitablePosition();
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
