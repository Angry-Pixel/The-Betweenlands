package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGecko extends EntityCreature implements IEntityBL {
    public EntityGecko(World worldObj)
    {
        super(worldObj);
        getNavigator().setAvoidsWater(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 0.3D));
        tasks.addTask(5, new EntityAIWander(this, 0.3D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }
    
	@Override
	protected String getLivingSound() {
		int randomSound = rand.nextInt(3) + 1;
		return "thebetweenlands:geckoLiving" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:geckoHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:geckoDeath";
	}
}
