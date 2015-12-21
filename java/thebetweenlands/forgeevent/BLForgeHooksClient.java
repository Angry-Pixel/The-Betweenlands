package thebetweenlands.forgeevent;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.forgeevent.client.ClientAttackEvent;
import thebetweenlands.forgeevent.client.GetMouseOverEvent;
import thebetweenlands.forgeevent.client.RenderEntitiesEvent;

public class BLForgeHooksClient {
	private BLForgeHooksClient() {}

	public static Entity getMouseOver(float delta) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityRenderer renderer = mc.entityRenderer;
		Entity pointedEntity = null;
		if (mc.renderViewEntity != null) {
			if (mc.theWorld != null) {
				mc.pointedEntity = null;
				if (postGetMouseEventPre(delta)) {
					// Nothing to see here, just a bit unconventional 'miss' HitResult...
					mc.objectMouseOver = new MovingObjectPosition(0, -1, 0, -1, Vec3.createVectorHelper(0, 0, 0), false);
					return null;
				}
				double reach = (double) mc.playerController.getBlockReachDistance();
				mc.objectMouseOver = mc.renderViewEntity.rayTrace(reach, delta);
				double hitDist = reach;
				Vec3 viewerPos = mc.renderViewEntity.getPosition(delta);
				if (mc.playerController.extendedReach()) {
					reach = 6;
					hitDist = 6;
				} else {
					if (reach > 3) {
						hitDist = 3;
					}
					reach = hitDist;
				}
				if (mc.objectMouseOver != null) {
					hitDist = mc.objectMouseOver.hitVec.distanceTo(viewerPos);
				}
				Vec3 lookVec = mc.renderViewEntity.getLook(delta);
				Vec3 lookEndPoint = viewerPos.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
				Vec3 hitVec = null;
				final float expansion = 1;
				List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach).expand(expansion, expansion, expansion));
				double nearestEntityDist = hitDist;
				for (Entity entity : entities) {
					if (entity.canBeCollidedWith()) {
						float border = entity.getCollisionBorderSize();
						AxisAlignedBB axisalignedbb = entity.boundingBox.expand(border, border, border);
						MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(viewerPos, lookEndPoint);
						if (axisalignedbb.isVecInside(viewerPos)) {
							if (0 < nearestEntityDist || nearestEntityDist == 0) {
								pointedEntity = entity;
								hitVec = movingobjectposition == null ? viewerPos : movingobjectposition.hitVec;
								nearestEntityDist = 0;
							}
						} else if (movingobjectposition != null) {
							double entityHitDist = viewerPos.distanceTo(movingobjectposition.hitVec);
							if (entityHitDist < nearestEntityDist || nearestEntityDist == 0) {
								if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract()) {
									if (nearestEntityDist == 0) {
										pointedEntity = entity;
										hitVec = movingobjectposition.hitVec;
									}
								} else {
									pointedEntity = entity;
									hitVec = movingobjectposition.hitVec;
									nearestEntityDist = entityHitDist;
								}
							}
						}
					}
				}
				if (pointedEntity != null && (nearestEntityDist < hitDist || mc.objectMouseOver == null)) {
					mc.objectMouseOver = new MovingObjectPosition(pointedEntity, hitVec);
					if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
						mc.pointedEntity = pointedEntity;
					}
				}
				postGetMouseEventPost(delta);
			}
		}
		return pointedEntity;
	}

	private static boolean postGetMouseEventPre(float delta) {
		return MinecraftForge.EVENT_BUS.post(new GetMouseOverEvent.Pre(delta));
	}

	private static void postGetMouseEventPost(float delta) {
		MinecraftForge.EVENT_BUS.post(new GetMouseOverEvent.Post(delta));
	}

	public static int handlePlayerAttackInput(int leftClickCounter) {
		Minecraft mc = Minecraft.getMinecraft();
		if (leftClickCounter <= 0) {
			if (postPlayerAttackEvent()) {
				return leftClickCounter;
			}
			mc.thePlayer.swingItem();
			if (mc.objectMouseOver == null) {
				if (mc.playerController.isNotCreative()) {
					leftClickCounter = 10;
				}
			} else {
				switch (mc.objectMouseOver.typeOfHit) {
					case ENTITY:
						mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
						break;
					case BLOCK:
						int x = mc.objectMouseOver.blockX;
						int y = mc.objectMouseOver.blockY;
						int z = mc.objectMouseOver.blockZ;
						if (mc.theWorld.getBlock(x, y, z).getMaterial() == Material.air) {
							if (mc.playerController.isNotCreative()) {
								leftClickCounter = 10;
							}
						} else {
							mc.playerController.clickBlock(x, y, z, mc.objectMouseOver.sideHit);
						}
					default:
						break;
				}
			}
		}
		return leftClickCounter;
	}

	private static boolean postPlayerAttackEvent() {
		return MinecraftForge.EVENT_BUS.post(new ClientAttackEvent());
	}

	public static void postPreRenderEntitiesEvent() {
		MinecraftForge.EVENT_BUS.post(new RenderEntitiesEvent.Pre());
	}

	public static void postPostRenderEntitiesEvent() {
		MinecraftForge.EVENT_BUS.post(new RenderEntitiesEvent.Post());
	}
}
