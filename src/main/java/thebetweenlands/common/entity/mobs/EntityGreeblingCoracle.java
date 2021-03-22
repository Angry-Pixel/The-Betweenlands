package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import net.minecraft.entity.ai.EntityAILookIdle;

public class EntityGreeblingCoracle extends EntityCreature implements IEntityBL {
    public EntityGreeblingCoracle(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(3, new EntityAILookIdle(this));
    }
}
