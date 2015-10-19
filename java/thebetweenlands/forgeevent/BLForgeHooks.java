package thebetweenlands.forgeevent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.forgeevent.client.GetMouseOverEvent;
import thebetweenlands.forgeevent.client.ClientAttackEvent;
import thebetweenlands.forgeevent.entity.LivingSetRevengeTargetEvent;
import thebetweenlands.forgeevent.entity.player.PlayerEventGetHurtSound;
import thebetweenlands.utils.confighandler.ConfigHandler;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

public final class BLForgeHooks {
	private BLForgeHooks() {}

	public static void onLivingSetRevengeTarget(EntityLivingBase entity, EntityLivingBase target) {
		MinecraftForge.EVENT_BUS.post(new LivingSetRevengeTargetEvent(entity, target));
	}

	public static String onPlayerGetHurtSound(EntityPlayer player) {
		PlayerEventGetHurtSound event = new PlayerEventGetHurtSound(player, "game.player.hurt");
		MinecraftForge.EVENT_BUS.post(event);
		return event.hurtSound;
	}

	public static EntityPlayerMP createPlayerForUser(ServerConfigurationManager serverConfigurationManager, GameProfile gameProfile) {
		UUID uuid = EntityPlayer.func_146094_a(gameProfile);
		ArrayList sameUUIDPlayers = Lists.newArrayList();
		for (int i = 0; i < serverConfigurationManager.playerEntityList.size(); i++) {
			EntityPlayerMP player = (EntityPlayerMP) serverConfigurationManager.playerEntityList.get(i);
			if (player.getUniqueID().equals(uuid)) {
				sameUUIDPlayers.add(player);
			}
		}
		Iterator<EntityPlayerMP> playerIterator = sameUUIDPlayers.iterator();
		while (playerIterator.hasNext()) {
			EntityPlayerMP player = playerIterator.next();
			player.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
		}
		int dimensionId = TheBetweenlands.proxy.getDebugHandler().isInDebugWorld() ? ConfigHandler.DIMENSION_ID : 0;
		MinecraftServer mcServer = serverConfigurationManager.getServerInstance();
		ItemInWorldManager itemInWorldManager;
		WorldServer world = mcServer.worldServerForDimension(dimensionId);
		if (mcServer.isDemo()) {
			itemInWorldManager = new DemoWorldManager(world);
		} else {
			itemInWorldManager = new ItemInWorldManager(world);
		}
		return new EntityPlayerMP(mcServer, world, gameProfile, itemInWorldManager);
	}

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
}
