package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.attributes.BooleanAttribute;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTermite extends EntityMob implements IEntityBL {
	public static final IAttribute SMALL = (new BooleanAttribute(null, "bl.termiteSmall", false)).setDescription("Whether this is a small termite").setShouldWatch(true);

	public EntityTermite(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 0.6F);
	}

	/**
	 * Sets whether the Termite is the small variant
	 * @param small
	 */
	public void setSmall(boolean small) {
		this.getEntityAttribute(SMALL).setBaseValue(small ? 1 : 0);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		this.getAttributeMap().registerAttribute(SMALL);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.TERMITE_LIVING;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.CRUNCH;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH;
	}

	@Override
	public void onUpdate() {
		if(this.getEntityAttribute(SMALL).getAttributeValue() == 1) {
			this.setSize(0.45F, 0.3F);
		} else {
			this.setSize(0.9F, 0.6F);
		}

		super.onUpdate();
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TERMITE;
	}

	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
