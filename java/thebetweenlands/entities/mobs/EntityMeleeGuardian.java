package thebetweenlands.entities.mobs;

import net.minecraft.world.World;

public class EntityMeleeGuardian extends EntityTempleGuardian implements IEntityBL {

    public EntityMeleeGuardian(World worldObj) {super(worldObj);}

    @Override

    protected String getLivingSound() {if (!getActive()) return null;
        int randomSound = rand.nextInt(3) + 1;
        return "thebetweenlands:templeGuardianMeleeLiving" + randomSound;}

    @Override
    public String pageName() {return "meleeGuardian";}
}