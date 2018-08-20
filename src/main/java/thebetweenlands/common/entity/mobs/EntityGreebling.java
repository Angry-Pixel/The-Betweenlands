package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;

import java.util.List;

/**
 * Created by Josh on 8/19/2018.
 */
public class EntityGreebling extends EntityCreature implements IEntityBL {
    public int disappearTimer = 0;

    public EntityGreebling(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (disappearTimer > 0) disappearTimer++;
        if (disappearTimer == 5) disappear();
        if (disappearTimer > 8) setDead();
        List<EntityPlayer> nearPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(4.5, 5, 4.5), e -> !e.capabilities.isCreativeMode && !e.isInvisible());
        if (disappearTimer == 0 && !nearPlayers.isEmpty()) disappearTimer++;
    }

    private void disappear() {
        if(world.isRemote) {
            int leafCount = 40;
            float x = (float) (posX);
            float y = (float) (posY + 1.3F);
            float z = (float) (posZ);
            while (leafCount-- > 0) {
                float dx = world.rand.nextFloat() * 1 - 0.5f;
                float dy = world.rand.nextFloat() * 1f - 0.1F;
                float dz = world.rand.nextFloat() * 1 - 0.5f;
                float mag = 0.08F + world.rand.nextFloat() * 0.07F;
                BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
            }
        }
    }
}
