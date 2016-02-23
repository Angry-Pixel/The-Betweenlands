package thebetweenlands.entities.mobs;

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

    @Override
    public String pageName() {
        return null;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2);
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(110.0D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);
        getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
    }
}
