package thebetweenlands.entities.mobs;

import net.minecraft.world.World;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityMeleeGuardian extends EntityTempleGuardian implements IEntityBL {
    public EntityMeleeGuardian(World worldObj)
    {
        super(worldObj);
    }
    
	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:templeGuardianMeleeLiving" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:templeGuardianHurt" + randomSound;
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:templeGuardianDeath";
	}
}