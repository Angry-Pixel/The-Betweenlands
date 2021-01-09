package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public class EntityFreshwaterUrchin extends EntityCreature implements IEntityBL {

	public EntityFreshwaterUrchin(World world) {
		super(world);
		setSize(0.7F, 0.5F);
		setPathPriority(PathNodeType.WATER, 4.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, 10.0F, 1D, 1D));
		tasks.addTask(1, new EntityAIWander(this, 1.0D, 40));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (inWater)
			setAir(300);
	}

}
