package thebetweenlands.event.elixirs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.herblore.elixirs.effects.ElixirRegistry;

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
		private TrailPos(Vec3 pos, Entity entity) {
			this.pos = pos;
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

	@SubscribeEvent
	public void onWorldTick(PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if(event.phase == Phase.START && player != null && player.worldObj.isRemote && player == Minecraft.getMinecraft().thePlayer && ElixirRegistry.EFFECT_HUNTERSSENSE.isActive(player)) {
			World world = event.player.worldObj;
			int strength = ElixirRegistry.EFFECT_HUNTERSSENSE.getStrength(player);
			List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, player.boundingBox.expand(50, 50, 50));
			List<TrailPos> availablePositions = new ArrayList<TrailPos>();
			for(Entity e : entityList) {
				if(e == player) continue;
				EntityTrail trail = this.getTrail(e);
				trail.update(strength);
				TrailPos lastPos = null;
				for(Vec3 pos : trail.cachedPositions) {
					if(lastPos != null) lastPos.nextPos = pos;
					TrailPos tp = new TrailPos(pos, e);
					availablePositions.add(tp);
					lastPos = tp;
				}
			}
			this.playerPos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
			Collections.sort(availablePositions, dstSorter);
			int maxPointCount = 200;
			int pointCount = Math.min(maxPointCount, availablePositions.size());
			int crawlTicks = 80;
			for(int i = 0; i < pointCount; i++) {
				if((player.ticksExisted - MathHelper.floor_float((float)crawlTicks / (float)pointCount * (float)i)) % crawlTicks == 0) {
					Vec3 pos = availablePositions.get(i).pos;
					float xx = (float) pos.xCoord + 0.5F;
					float yy = (float) pos.yCoord + world.rand.nextFloat() * 0.1F;
					float zz = (float) pos.zCoord + 0.5F;
					//System.out.println(pos.xCoord + " " + pos.yCoord + " " + pos.zCoord);
					float fixedOffset = 0.25F;
					float randomOffset = world.rand.nextFloat() * 0.2F - 0.1F;
					BLParticle.BUBBLE_PRUIFIER.spawn(world, (double) (xx - fixedOffset), (double) yy, (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
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
		}
	}

	/*@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {

	}*/
}
