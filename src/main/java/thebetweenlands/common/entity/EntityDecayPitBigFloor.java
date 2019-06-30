package thebetweenlands.common.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thebetweenlands.common.entity.mobs.EntitySludgeJet;

public class EntityDecayPitBigFloor extends Entity {
	public float animationTicks = 0;
	public float animationTicksPrev = 0;

	public EntityDecayPitBigFloor(World world) {
		super(world);
		setSize(15F, 1.0625F);
		ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;

		animationTicks += 1F;
		if (animationTicks >= 360F)
			animationTicks = animationTicksPrev = 0;

		if (!getEntityWorld().isRemote) {

			if (animationTicks == 15 || animationTicks == 195) {
				spawnSludgeJet(posX + 5.5D, posY + 1D, posZ - 2D);
				spawnSludgeJet(posX - 5.5D, posY + 1D, posZ + 2D);
			}

			if (animationTicks == 60 || animationTicks == 240) {
				spawnSludgeJet(posX + 2D, posY + 1D, posZ - 5.5D);
				spawnSludgeJet(posX - 2D, posY + 1D, posZ + 5.5D);
			}

			if (animationTicks == 105 || animationTicks == 285) {
				spawnSludgeJet(posX - 2D, posY + 1D, posZ - 5.5D);
				spawnSludgeJet(posX + 2D, posY + 1D, posZ + 5.5D);
			}

			if (animationTicks == 150 || animationTicks == 330) {
				spawnSludgeJet(posX - 5.5D, posY + 1D, posZ - 2D);
				spawnSludgeJet(posX + 5.5D, posY + 1D, posZ + 2D);
			}

		}

		checkSurfaceCollisions();
	}

	private Entity checkSurfaceCollisions() {
		boolean reverse = false;
		for (Entity entity : getEntityAbove()) {
			if (entity != null && !(entity instanceof EntitySludgeJet)) {
				if (getDistance(entity) >= 4.25F - entity.width * 0.5F && getDistance(entity) <= 7.5F + entity.width * 0.5F) {
					reverse = false;
					if (entity.posY <= posY + height - 0.0625D) {
						entity.motionX = 0D;
						entity.motionY = 0.1D;
						entity.motionZ = 0D;
					} else if (entity.motionY < 0) {
						entity.motionY = 0;
						checkJumpOnTopOfAABB(entity);
					}
				}

				if (getDistance(entity) < 4.25F - entity.width * 0.5F && getDistance(entity) >= 2.5F + entity.width * 0.5F) {
					if (entity.posY <= posY + 0.0625D) {
					reverse = true;
					checkJumpOnTopOfAABB(entity);
					}
				}

				if (getDistance(entity) >= 2.5F + entity.width * 0.5F) {
					Vec3d center = new Vec3d(this.posX, 0, this.posZ);
					Vec3d entityOffset = new Vec3d(entity.posX, 0, entity.posZ);

					double dist = entityOffset.distanceTo(center);
					double circumference = 2 * Math.PI * dist;
					double speed = circumference / 360 * (reverse ? 1F : 0.75F) /* angle per tick */;

					Vec3d push = new Vec3d(0, 1, 0).crossProduct(entityOffset.subtract(center).normalize()).normalize().scale(reverse ? -speed : speed);

					if (!entity.world.isRemote || entity instanceof EntityPlayer) {
						entity.move(MoverType.SELF, push.x, 0, push.z);
					}
				}
			}
		}
		return null;
	}

	public void checkJumpOnTopOfAABB(Entity entity) {
		if (entity.getEntityWorld().isRemote && entity instanceof EntityPlayer) {
			boolean jump = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
			if (jump)
				((EntityPlayer) entity).jump();
		}
	}

	public List<Entity> getEntityAbove() {
		return getEntityWorld().<Entity>getEntitiesWithinAABB(Entity.class, getEntityBoundingBox(), EntitySelectors.IS_ALIVE);
    }

	private void spawnSludgeJet(double posX, double posY, double posZ) {
		EntitySludgeJet jet = new EntitySludgeJet(getEntityWorld());
		jet.setPosition(posX, posY, posZ);
		getEntityWorld().spawnEntity(jet);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

	@SubscribeEvent (priority = EventPriority.LOWEST)
	public void onClientTick(TickEvent.ClientTickEvent event) throws Exception {
		boolean jump = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
				if(jump){
					System.out.println("jumping");
		}
	}
}