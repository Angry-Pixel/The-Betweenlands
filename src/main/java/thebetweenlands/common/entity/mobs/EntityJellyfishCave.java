package thebetweenlands.common.entity.mobs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityShock;

public class EntityJellyfishCave extends EntityJellyfish implements IMob {
	protected static final byte EVENT_SPARK = 80;

	public EntityJellyfishCave(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIAttackMelee(this, 0.5D, false) {
			@Override
			protected void checkAndPerformAttack(EntityLivingBase enemy, double distToEnemySqr) {
				//No melee attacks
			}
		});
		this.tasks.addTask(1, new EntityAIMoveTowardsRestriction(this, 0.4D));
		this.tasks.addTask(2, new EntityAIWander(this, 0.5D, 20));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
			EntityLivingBase target = this.getAttackTarget();

			if(target != null) {
				double dst = this.getDistance(target);

				if(dst < 6.0f && this.world.rand.nextInt(20) == 0) {
					this.world.setEntityState(this, EVENT_SPARK);
				}

				if(dst < 3.0f && this.ticksExisted % 20 == 0) {
					this.world.spawnEntity(new EntityShock(this.world, this, this, 2.0f, true));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_SPARK) {
			this.spawnLightningArcs();
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnLightningArcs() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		if(view != null && view.getDistance(this) < 16) {
			float ox = this.world.rand.nextFloat() - 0.5f + (float)this.motionX;
			float oy = this.world.rand.nextFloat() - 0.5f + (float)this.motionY;
			float oz = this.world.rand.nextFloat() - 0.5f + (float)this.motionZ;

			Particle particle = BLParticles.LIGHTNING_ARC.create(this.world, this.posX, this.posY + this.height * 0.5f, this.posZ, 
					ParticleArgs.get()
					.withMotion(this.motionX, this.motionY, this.motionZ)
					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
					.withData(new Vec3d(this.posX + ox, this.posY + this.height * 0.5f + oy, this.posZ + oz)));

			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
		}
	}
}
