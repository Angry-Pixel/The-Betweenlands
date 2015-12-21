package thebetweenlands.entities.mobs;

import net.minecraft.world.World;
import thebetweenlands.manual.ManualManager;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityBerserkerGuardian extends EntityTempleGuardian implements IEntityBL {
    public EntityBerserkerGuardian(World worldObj) {
        super(worldObj);
    }
    
	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:templeGuardianBerserkerLiving" + randomSound;
	}
	@Override
	public String pageName() {
		return "beserkerGuardian";
	}
}