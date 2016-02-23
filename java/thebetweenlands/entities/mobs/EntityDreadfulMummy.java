package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 2/13/16.
 */
public class EntityDreadfulMummy extends EntityMob implements IEntityBL {
    public EntityDreadfulMummy(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    static final int SPAWN_MUMMY_COOLDOWN = 300;
    int untilSpawnMummy = 0;
    @Override
    public String pageName() {
        return null;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.9);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(110.0D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getEntityToAttack() != null) {
            if (untilSpawnMummy <= 0) spawnMummy();
        }
        if(untilSpawnMummy > 0) untilSpawnMummy--;
    }

    private void spawnMummy() {
        untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
        EntityPeatMummy mummy = new EntityPeatMummy(worldObj);
        mummy.setPosition(posX + (rand.nextInt(6) - 3), posY, posZ + (rand.nextInt(6) - 3));
        worldObj.spawnEntityInWorld(mummy);
        mummy.setAttackTarget((EntityLivingBase) getEntityToAttack());
        mummy.setHealth(20);
    }
}
