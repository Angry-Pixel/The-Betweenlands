package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTamedSpiritTreeFace extends EntitySpiritTreeFaceSmall {
	public EntityTamedSpiritTreeFace(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, 10, false, false, e -> IMob.VISIBLE_MOB_SELECTOR.apply(e) && e instanceof EntityTamedSpiritTreeFace == false));

		this.tasks.addTask(0, new AITrackTarget(this, true, 16.0D));
		this.tasks.addTask(1, new AIAttackMelee(this, 1, true));
		this.tasks.addTask(2, new AISpit(this));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	protected void fixUnsuitablePosition() {
		this.onDeath(DamageSource.OUT_OF_WORLD);
		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.getImmediateSource() instanceof EntityPlayer) {
			return super.attackEntityFrom(source, amount);
		}
		return false;
	}
}
