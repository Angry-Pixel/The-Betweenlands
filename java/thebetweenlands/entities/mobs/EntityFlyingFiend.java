package thebetweenlands.entities.mobs;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.world.World;

public class EntityFlyingFiend extends EntityBat {

	public EntityFlyingFiend(World world) {
		super(world);
	}
	
    @Override
    protected String getLivingSound() {
        int randomSound = rand.nextInt(4) + 1;
        return "thebetweenlands:flyingFiendLiving" + randomSound;
    }

	@Override
	protected String getHurtSound() {
		if (this.rand.nextBoolean()) {
			return "thebetweenlands:flyingFiendHurt1";
		} else {
			return "thebetweenlands:flyingFiendHurt2";
		}
	}

    @Override
    protected String getDeathSound() {
        return "thebetweenlands:flyingFiendDeath";
    }

}
