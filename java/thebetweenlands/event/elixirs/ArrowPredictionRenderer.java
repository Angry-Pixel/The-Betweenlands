package thebetweenlands.event.elixirs;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ArrowPredictionRenderer {
	private static class EntityArrowSilent extends EntityArrow {
		public EntityArrowSilent(World world, EntityLivingBase entity, float strength) {
			super(world, entity, strength);
		}

		@Override
		public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
			float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
			par1 /= (double)var9;
			par3 /= (double)var9;
			par5 /= (double)var9;
			par1 *= (double)par7;
			par3 *= (double)par7;
			par5 *= (double)par7;
			this.motionX = par1;
			this.motionY = par3;
			this.motionZ = par5;
			float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)var10) * 180.0D / Math.PI);
		}

		@Override
		public boolean isInWater() {
			return false;
		}

		@Override
		public boolean handleWaterMovement() {
			if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this)) {
				if (!this.inWater) {
					float var1 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

					if (var1 > 1.0F)
					{
						var1 = 1.0F;
					}

					float var2 = (float)MathHelper.floor_double(this.boundingBox.minY);
					int var3;
					float var4;
					float var5;
				}

				this.fallDistance = 0.0F;
				this.inWater = true;
			} else {
				this.inWater = false;
			}
			return this.inWater;
		}
	}

	public static void setRandomYawPitch() {
		randomYawPitchSet = false;
	}

	private static float randYaw = 0.0F;
	private static float randPitch = 0.0F;
	private static boolean randomYawPitchSet = false;
	private static float lastQuality = 0.0F;
	public static void render(float quality) {
		if(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() == null || Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow == false) {	
			randomYawPitchSet = false;
			return;
		}
		if(lastQuality != quality || !randomYawPitchSet) {
			randomYawPitchSet = true;
			lastQuality = quality;
			float maxOffset = 3.0F;
			randYaw = (maxOffset / 2.0F - Minecraft.getMinecraft().theWorld.rand.nextFloat() * maxOffset * 2.0F) * (1.0F - quality);
			randPitch = (maxOffset / 2.0F - Minecraft.getMinecraft().theWorld.rand.nextFloat() * maxOffset * 2.0F) * (1.0F - quality);
		}
		int maxDur = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem().getMaxItemUseDuration(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem()) - Minecraft.getMinecraft().thePlayer.getItemInUseCount();
		float strength = (float)maxDur / 20.0F;
		strength = (strength * strength + strength * 2.0F) / 3.0F;
		if(strength < 0.1f || strength > 1.0f) {
			strength = 1.0f;
		}
		double px = Minecraft.getMinecraft().thePlayer.posX;
		double py = Minecraft.getMinecraft().thePlayer.posY;
		double pz = Minecraft.getMinecraft().thePlayer.posZ;
		float pYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
		float pPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
		Minecraft.getMinecraft().thePlayer.posX = RenderManager.renderPosX;
		Minecraft.getMinecraft().thePlayer.posY = RenderManager.renderPosY;
		Minecraft.getMinecraft().thePlayer.posZ = RenderManager.renderPosZ;
		Minecraft.getMinecraft().thePlayer.rotationYaw += randYaw;
		Minecraft.getMinecraft().thePlayer.rotationPitch += randPitch;
		//TODO: Mabye find a better way to simulate an arrow
		EntityArrowSilent ea = new EntityArrowSilent(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer, strength * 2.0f);
		Minecraft.getMinecraft().thePlayer.posX = px;
		Minecraft.getMinecraft().thePlayer.posY = py;
		Minecraft.getMinecraft().thePlayer.posZ = pz;
		Minecraft.getMinecraft().thePlayer.rotationYaw = pYaw;
		Minecraft.getMinecraft().thePlayer.rotationPitch = pPitch;
		double rx = RenderManager.renderPosX;
		double ry = RenderManager.renderPosY;
		double rz = RenderManager.renderPosZ;
		double startX = rx - (double)(MathHelper.cos(Minecraft.getMinecraft().thePlayer.rotationYaw / 180.0F * (float)Math.PI) * 0.46F);
		double startY = ry - 0.10000000149011612D;
		double startZ = rz - (double)(MathHelper.sin(Minecraft.getMinecraft().thePlayer.rotationYaw / 180.0F * (float)Math.PI) * 0.46F);
		double lastX = startX, lastY = startY, lastZ = startZ;

		float alpha = quality / 1.3F;

		GL11.glPushMatrix();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(alpha * 3.5F);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);

		boolean drawing = true;
		GL11.glBegin(GL11.GL_LINES);
		for(int i = 0; i < 1000; i++) {
			ea.onUpdate();

			GL11.glVertex3d(lastX - rx, lastY - ry, lastZ - rz);
			GL11.glVertex3d(ea.posX - rx, ea.posY - ry, ea.posZ - rz);
			lastX = ea.posX;
			lastY = ea.posY;
			lastZ = ea.posZ;

			MovingObjectPosition collisionPoint = getCollision(ea);
			if(collisionPoint != null) {
				if(collisionPoint.typeOfHit == MovingObjectType.BLOCK) {
					drawing = false;
					GL11.glVertex3d(ea.posX - rx, ea.posY - ry, ea.posZ - rz);
					GL11.glVertex3d(collisionPoint.hitVec.xCoord-rx, collisionPoint.hitVec.yCoord-ry, collisionPoint.hitVec.zCoord-rz);
					GL11.glEnd();
					GL11.glLineWidth(2.0f);
					GL11.glColor4f(1.0f, 0.0f, 0.0f, quality);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glBegin(GL11.GL_LINES);
					if(collisionPoint.sideHit == 0) {
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord-0.001-ry, collisionPoint.hitVec.zCoord-0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord-0.001-ry, collisionPoint.hitVec.zCoord+0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord-0.001-ry, collisionPoint.hitVec.zCoord-0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord-0.001-ry, collisionPoint.hitVec.zCoord+0.1-rz);
					} else if(collisionPoint.sideHit == 1) {
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord+0.001-ry, collisionPoint.hitVec.zCoord-0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord+0.001-ry, collisionPoint.hitVec.zCoord+0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord+0.001-ry, collisionPoint.hitVec.zCoord-0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord+0.001-ry, collisionPoint.hitVec.zCoord+0.1-rz);
					} else if(collisionPoint.sideHit == 2) {
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord-0.001-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord-0.001-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord-0.001-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord-0.001-rz);
					} else if(collisionPoint.sideHit == 3) {
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord+0.001-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord+0.001-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.1-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord+0.001-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.1-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord+0.001-rz);
					} else if(collisionPoint.sideHit == 4) {
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.001-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord-0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.001-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord+0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.001-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord+0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord-0.001-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord-0.1-rz);
					} else if(collisionPoint.sideHit == 5) {
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.001-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord-0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.001-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord+0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.001-rx, collisionPoint.hitVec.yCoord-0.1-ry, collisionPoint.hitVec.zCoord+0.1-rz);
						GL11.glVertex3d(collisionPoint.hitVec.xCoord+0.001-rx, collisionPoint.hitVec.yCoord+0.1-ry, collisionPoint.hitVec.zCoord-0.1-rz);
					}
					GL11.glEnd();
				} else if(collisionPoint.typeOfHit == MovingObjectType.ENTITY) {
					Entity hitEntity = collisionPoint.entityHit;
					if(hitEntity != null) {

					}
				}
				break;
			}
		}
		if(drawing) GL11.glEnd();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}

	private static MovingObjectPosition getCollision(EntityArrowSilent ea) {
		Vec3 start = Vec3.createVectorHelper(ea.posX, ea.posY, ea.posZ);
		Vec3 dest = Vec3.createVectorHelper(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
		MovingObjectPosition hit = Minecraft.getMinecraft().theWorld.func_147447_a(start, dest, false, true, false);
		start = Vec3.createVectorHelper(ea.posX, ea.posY, ea.posZ);
		dest = Vec3.createVectorHelper(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
		if (hit != null) {
			dest = Vec3.createVectorHelper(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
		}
		Entity collidedEntity = null;
		List entityList = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(ea, ea.boundingBox.addCoord(ea.motionX, ea.motionY, ea.motionZ).expand(1.0D, 1.0D, 1.0D));
		double lastDistance = 0.0D;
		for (int c = 0; c < entityList.size(); ++c) {
			Entity currentEntity = (Entity)entityList.get(c);
			if (currentEntity.canBeCollidedWith() && (currentEntity != Minecraft.getMinecraft().thePlayer)) {
				AxisAlignedBB entityBoundingBox = currentEntity.boundingBox.expand((double)0.3F, (double)0.3F, (double)0.3F);
				MovingObjectPosition collision = entityBoundingBox.calculateIntercept(start, dest);
				if (collision != null) {
					double currentDistance = start.distanceTo(collision.hitVec);

					if (currentDistance < lastDistance || lastDistance == 0.0D) {
						collidedEntity = currentEntity;
						lastDistance = currentDistance;
					}
				}
			}
		}
		if (collidedEntity != null) {
			hit = new MovingObjectPosition(collidedEntity);
		}
		return hit;
	}
}
