package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.Vec3;
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
    static final int SPAWN_SLUDGE_COOLDOWN = 150;
    int untilSpawnSludge = 0;

    @Override
    public String pageName() {
        return null;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(170.0D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(10);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getEntityToAttack() != null) {
//            if (untilSpawnMummy <= 0) spawnMummy();
            if (untilSpawnSludge <= 0) spawnSludge();
        }
        if(untilSpawnMummy > 0) untilSpawnMummy--;
        if(untilSpawnSludge > 0) untilSpawnSludge--;
    }

    private void spawnMummy() {
        EntityPeatMummy mummy = new EntityPeatMummy(worldObj);
        mummy.setPosition(posX + (rand.nextInt(6) - 3), posY, posZ + (rand.nextInt(6) - 3));
        /*if (mummy.getCanSpawnHere())*/ worldObj.spawnEntityInWorld(mummy);
        //else return;
        //TODO Mummy needs to check if appropriate spawn location
        untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
        mummy.setAttackTarget((EntityLivingBase) getEntityToAttack());
        mummy.setHealth(15);
    }

    private void spawnSludge() {
        untilSpawnSludge = SPAWN_SLUDGE_COOLDOWN;

        Vec3 look = this.getLookVec();
        double direction = Math.toRadians(renderYawOffset);
        EntitySludgeBall sludge = new EntitySludgeBall(worldObj, 0.000029f, this);
        sludge.setPosition(posX - Math.sin(direction) * 3.5, posY + height, posZ + Math.cos(direction) * 3.5);
        sludge.motionX = look.xCoord * 0.5D;
        sludge.motionY = look.yCoord;
        sludge.motionZ = look.zCoord* 0.5D;
        if (!worldObj.isRemote)
        	worldObj.spawnEntityInWorld(sludge);
        
    }
}
