package thebetweenlands.entities.mobs;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 2/23/16.
 */

public class EntitySludgeBall extends EntityThrowable {
    private int bounces = 0;
    float speed;
    EntityLivingBase owner;
    public EntitySludgeBall(World world) {
        super(world);
        setSize(1, 1);
        this.speed = 0.3f;
        this.owner = null;
    }
    public EntitySludgeBall(World world, float speed, EntityLivingBase owner) {
        super(world);
        setSize(1, 1);
        this.speed = speed;
        this.owner = owner;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    protected void onImpact(MovingObjectPosition collision) {
        if (collision.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && (collision.sideHit == 1 || collision.sideHit == 0)) {
            motionY *= -0.9;
            bounces++;
            if (bounces == 3)
            	explode();
            else {
                playSound("mob.slime.big", 1, 0.9f);
                spawnBounceParticles(8);
            }
        }
        else if (collision.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && (collision.sideHit == 2 || collision.sideHit == 3)) {
            motionZ *= -1;
            bounces++;
            if (bounces == 3) explode();
            System.out.println("Wall hit");
            //TODO Not printing, not bouncing
        }
        else if (collision.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && (collision.sideHit == 4 || collision.sideHit == 5)) {
            motionX *= -1;
            bounces++;
            if (bounces == 3) explode();
            System.out.println("Wall hit");
            //TODO Not printing, not bouncing
        }
        else if (collision.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) explode();
        //TODO Not colliding with entities
    }

    private void explode() {
        float radius = 3;
        AxisAlignedBB region = AxisAlignedBB.getBoundingBox(posX - radius, posY - 0.5, posZ - radius, posX + radius, Double.POSITIVE_INFINITY, posZ + radius);
        List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, region);
        double radiusSq = radius * radius;
        for (Entity entity : entities)
        {
            if (entity instanceof EntityLivingBase && !(entity instanceof EntityPeatMummy) && !(entity instanceof EntityDreadfulMummy) && getDistanceSqToEntity(entity) < radiusSq)
            {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 80, 2, false));
                if (owner != null) entity.attackEntityFrom(DamageSource.causeMobDamage(owner), 3);
            }
        }
        playSound("mob.slime.big", 1, 0.5f);
        playSound("mob.slime.small", 1, 0.5f);
        playSound("mob.slime.small", 1, 1f);
        playSound("mob.slime.big", 1, 1f);
        //TODO Better explosion particle effects
        spawnBounceParticles(20);
        setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.08F;
    }

    private void spawnBounceParticles(int amount) {
        for (int i = 0; i <= amount; i++) worldObj.spawnParticle("slime", posX + (amount/8) * (rand.nextFloat() - 0.5), posY + 0.3, posZ + (amount/8) * (rand.nextFloat() - 0.5), 0, 0, 0);
    }
}
