package thebetweenlands.entities.mobs;

import net.minecraft.world.World;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityBerserkerGuardian extends EntityTempleGuardian implements IEntityBL {
	public EntityBerserkerGuardian(World worldObj) {
		super(worldObj);
		this.setSize(1.1F, 2.5F);
	}

	@Override
	protected String getLivingSound() {
		if (!getActive()) return null;
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:templeGuardianBerserkerLiving" + randomSound;
	}
	@Override
	public String pageName() {
		return "beserkerGuardian";
	}
}