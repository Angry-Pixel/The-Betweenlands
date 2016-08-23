package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class EntityTermite extends EntityMob implements IEntityBL{
    public EntityTermite(World worldIn) {
        super(worldIn);
        setSize(0.9F, 0.6F);
        tasks.addTask(1, new EntityAIAttackMelee(this, 1d, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return worldObj.checkNoEntityCollision(this.getEntityBoundingBox()) && worldObj.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && !worldObj.containsAnyLiquid(this.getEntityBoundingBox());
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
        playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.TERMITE_LIVING;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SQUISH;
    }
}
