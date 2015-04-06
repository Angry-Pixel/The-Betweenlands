package thebetweenlands.entities.entityAI;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.IBlockAccess;

public class EntityAIBLBreakDoor extends EntityAIBLBlockInteract {
	private int breakingTime;
	private int maxBreakingTime;
	private int breakProgress = -1;

	public EntityAIBLBreakDoor(EntityLiving entityLiving, Block target, int actionTimeSeconds) {
		super(entityLiving, target);
		maxBreakingTime = actionTimeSeconds * 20;
	}

	@Override
	public boolean shouldExecute() {
		return !super.shouldExecute() ? false : (!theEntity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") ? false : !isValidBlockMeta(theEntity.worldObj, entityPosX, entityPosY, entityPosZ));
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		breakingTime = 0;
	}

	@Override
	public boolean continueExecuting() {
		double d0 = theEntity.getDistanceSq((double) entityPosX, (double) entityPosY, (double) entityPosZ);
		return breakingTime < maxBreakingTime && !isValidBlockMeta(theEntity.worldObj, entityPosX, entityPosY, entityPosZ) && d0 < 4.0D;
	}

	@Override
	public void resetTask() {
		super.resetTask();
		theEntity.worldObj.destroyBlockInWorldPartially(theEntity.getEntityId(), entityPosX, entityPosY, entityPosZ, -1);
	}

	@Override
	public void updateTask() {
		super.updateTask();

		if (theEntity.getRNG().nextInt(20) == 0) {
			theEntity.worldObj.playAuxSFX(1010, entityPosX, entityPosY, entityPosZ, 0);
		}

		++breakingTime;
		int i = (int) ((float) breakingTime / maxBreakingTime * 10.0F);

		if (i != breakProgress) {
			theEntity.worldObj.destroyBlockInWorldPartially(theEntity.getEntityId(), entityPosX, entityPosY, entityPosZ, i);
			breakProgress = i;
		}

		if (breakingTime == maxBreakingTime) {
			theEntity.worldObj.setBlockToAir(entityPosX, entityPosY, entityPosZ);
			theEntity.worldObj.playAuxSFX(1012, entityPosX, entityPosY, entityPosZ, 0);
			theEntity.worldObj.playAuxSFX(2001, entityPosX, entityPosY, entityPosZ, Block.getIdFromBlock(targetBlock));
		}
	}

	public boolean isValidBlockMeta(IBlockAccess world, int x, int y, int z) {
		return (checkBlockMeta(world, x, y, z) & 4) != 0;
	}

	public int checkBlockMeta(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean flag = (meta & 8) != 0;
		int i1;
		int j1;

		if (flag) {
			i1 = world.getBlockMetadata(x, y - 1, z);
			j1 = meta;
		} else {
			i1 = meta;
			j1 = world.getBlockMetadata(x, y + 1, z);
		}

		boolean flag1 = (j1 & 1) != 0;
		return i1 & 7 | (flag ? 8 : 0) | (flag1 ? 16 : 0);
	}
}
