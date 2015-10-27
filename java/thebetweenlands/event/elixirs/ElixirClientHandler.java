package thebetweenlands.event.elixirs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.herblore.elixirs.ElixirRegistry;

public class ElixirClientHandler {
	public static final ElixirClientHandler INSTANCE = new ElixirClientHandler();

	private static class EntityTrail {
		private static final int MAX_CACHE_SIZE = 80;
		private Entity entity;
		private List<Vec3> cachedPositions = new ArrayList<Vec3>();

		private EntityTrail(Entity entity) {
			this.entity = entity;
		}

		private void update(int strength) {
			if(this.entity != null && !this.entity.isDead) {
				Vec3 newPos = Vec3.createVectorHelper(this.entity.posX, this.entity.posY, this.entity.posZ);
				if(this.cachedPositions.size() > 0) {
					Vec3 lastPos = this.cachedPositions.get(this.cachedPositions.size() - 1);
					if(lastPos.distanceTo(newPos) > 0.5D) {
						this.cachedPositions.add(newPos);
					}
					if(this.cachedPositions.size() > MAX_CACHE_SIZE + MAX_CACHE_SIZE / 4 * (strength + 1)) {
						this.cachedPositions.remove(0);
					}
				} else {
					this.cachedPositions.add(newPos);
				}
			}
		}
	}
	private Map<Entity, EntityTrail> entityTrails = new HashMap<Entity, EntityTrail>();
	public EntityTrail getTrail(Entity entity) {
		EntityTrail trail = this.entityTrails.get(entity);
		if(trail == null) {
			trail = new EntityTrail(entity);
			this.entityTrails.put(entity, trail);
		}
		return trail;
	}
	private static class TrailPos {
		private final Vec3 pos;
		private Vec3 nextPos;
		private final Entity entity;
		private final int index;
		private TrailPos(Vec3 pos, int index, Entity entity) {
			this.pos = pos;
			this.index = index;
			this.entity = entity;
		}
	}
	private Vec3 playerPos;
	private final Comparator<TrailPos> dstSorter = new Comparator<TrailPos>() {
		@Override
		public int compare(TrailPos v1, TrailPos v2) {
			double d1 = v1.pos.distanceTo(playerPos);
			double d2 = v2.pos.distanceTo(playerPos);
			if (d1 < d2)
				return -1;
			else if (d1 > d2)
				return 1;
			else
				return 0;
		}
	};

	private final Method f_swingHand = ReflectionHelper.findMethod(Minecraft.class, null, new String[]{"func_147116_af", "al"});
	private final Method f_damageBlock = ReflectionHelper.findMethod(Minecraft.class, null, new String[]{"func_147115_a", "a"}, boolean.class);

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(event.phase == Phase.END) {
			if(player != null && player.worldObj != null && player.worldObj.isRemote && player == Minecraft.getMinecraft().thePlayer) {
				if(ElixirRegistry.EFFECT_HUNTERSSENSE.isActive(player)) {
					int strength = ElixirRegistry.EFFECT_HUNTERSSENSE.getStrength(player);
					World world = player.worldObj;
					List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, player.boundingBox.expand(50, 50, 50));
					List<TrailPos> availablePositions = new ArrayList<TrailPos>();
					for(Entity e : entityList) {
						if(e == player) continue;
						EntityTrail trail = this.getTrail(e);
						trail.update(strength);
						TrailPos lastPos = null;
						for(int i = 0; i < trail.cachedPositions.size(); i++) {
							Vec3 pos = trail.cachedPositions.get(i);
							if(lastPos != null) lastPos.nextPos = pos;
							TrailPos tp = new TrailPos(pos, i, e);
							availablePositions.add(tp);
							lastPos = tp;
						}
					}
					this.playerPos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
					Collections.sort(availablePositions, dstSorter);
					int maxPointCount = 200;
					int pointCount = Math.min(maxPointCount, availablePositions.size());
					int crawlTicks = MathHelper.floor_double(20.0F + 120.0F - 120.0F / 4.0F * strength);
					for(int i = 0; i < pointCount; i++) {
						TrailPos tp = availablePositions.get(i);
						if((player.ticksExisted - MathHelper.floor_float((float)crawlTicks / (float)EntityTrail.MAX_CACHE_SIZE * (float)tp.index)) % crawlTicks == 0) {
							Vec3 pos = tp.pos;
							if(tp.nextPos != null) {
								int subSegments = 10;
								Vec3 nextPos = tp.nextPos;
								for(int s = 0; s <= subSegments; s++) {
									if((player.ticksExisted - MathHelper.floor_float((float)crawlTicks / (float)EntityTrail.MAX_CACHE_SIZE * (float)(tp.index + s / (float)subSegments))) % crawlTicks == 0) {
										double tpx = (double) pos.xCoord + 0.5F;
										double tpy = (double) pos.yCoord + 0.05F;
										double tpz = (double) pos.zCoord + 0.5F;
										double tpx2 = (double) nextPos.xCoord + 0.5F;
										double tpy2 = (double) nextPos.yCoord;
										double tpz2 = (double) nextPos.zCoord + 0.5F;
										double tpxi = tpx + (tpx2 - tpx) / (double)subSegments * s;
										double tpyi = tpy + (tpy2 - tpy) / (double)subSegments * s;
										double tpzi = tpz + (tpz2 - tpz) / (double)subSegments * s;
										BLParticle.BUBBLE_PRUIFIER.spawn(world, (double) tpxi, (double) tpyi, (double) tpzi, 0.0D, 0.0D, 0.0D, 0);
									}
								}
							} else {
								double tpx = (double) pos.xCoord + 0.5F;
								double tpy = (double) pos.yCoord + 0.05F;
								double tpz = (double) pos.zCoord + 0.5F;
								BLParticle.BUBBLE_PRUIFIER.spawn(world, (double) tpx, (double) tpy, (double) tpz, 0.0D, 0.0D, 0.0D, 0);
							}
						}
					}
					Iterator<Entry<Entity, EntityTrail>> it = this.entityTrails.entrySet().iterator();
					Entry<Entity, EntityTrail> entry;
					while(it.hasNext() && (entry = it.next()) != null) {
						EntityTrail trail = entry.getValue();
						if(trail.entity == null || trail.entity.isDead || !entityList.contains(entry.getKey())) {
							it.remove();
						}
					}
				} else {
					this.entityTrails.clear();
				}

				if(ElixirRegistry.EFFECT_SWIFTARM.isActive(player)) {
					if(Minecraft.getMinecraft().gameSettings.keyBindAttack.getIsKeyPressed() && !player.isBlocking()) {
						try {
							MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
							if(target == null || target.entityHit != null || target.typeOfHit == MovingObjectType.MISS) {
								f_swingHand.invoke(Minecraft.getMinecraft());
							} else if(target != null) {
								if(!player.isSwingInProgress) {
									f_damageBlock.invoke(Minecraft.getMinecraft(), true);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				this.entityTrails.clear();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player != null && player.worldObj != null) {
			if(ElixirRegistry.EFFECT_SAGITTARIUS.isActive(player)) {
				ArrowPredictionRenderer.render(Math.min((ElixirRegistry.EFFECT_SAGITTARIUS.getStrength(player) + 1) / 3.0F, 1.0F));
			}
		}
	}

	/*@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {

	}*/
}
