package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;

public class EntityFreshwaterUrchin extends EntityCreature implements IEntityBL {

	public EntityFreshwaterUrchin(World world) {
		super(world);
		setSize(0.6875F, 0.4375F);
		setPathPriority(PathNodeType.WATER, 4.0F);
		stepHeight = 1F;
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
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.DROWN)
			return false;
		return super.attackEntityFrom(source, damage);
	}

    @Override
    public void travel(float strafe, float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.1F);
                move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.75D;
				motionY *= 0.75D;
				motionZ *= 0.75D;
				motionY -= 0.006D;
            } else {
                move(MoverType.SELF, motionX, motionY, motionZ);
				motionX = 0D;
				motionY = 0D;
				motionZ = 0D;
				motionY -= 0.2D;
            }
        } else {
            super.travel(strafe, up, forward);
        }
    }
}
