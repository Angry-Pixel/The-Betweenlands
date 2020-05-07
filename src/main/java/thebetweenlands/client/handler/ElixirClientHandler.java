package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.event.ArmSwingSpeedEvent;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;

public class ElixirClientHandler {

    public static final ElixirClientHandler INSTANCE = new ElixirClientHandler();

    public ElixirClientHandler() {}

    private static class EntityTrail {
        private static final int MAX_CACHE_SIZE = 80;
        private Entity entity;
        private List<Vec3d> cachedPositions = new ArrayList<>();

        private EntityTrail(Entity entity) {
            this.entity = entity;
        }

        private void update(int strength) {
            if(this.entity != null && !this.entity.isDead) {
                Vec3d newPos = new Vec3d(this.entity.posX, this.entity.posY, this.entity.posZ);
                if(this.cachedPositions.size() > 0) {
                    Vec3d lastPos = this.cachedPositions.get(this.cachedPositions.size() - 1);
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
        private final Vec3d pos;
        private Vec3d nextPos;
        private final Entity entity;
        private final int index;
        private TrailPos(Vec3d pos, int index, Entity entity) {
            this.pos = pos;
            this.index = index;
            this.entity = entity;
        }
    }
    private Vec3d playerPos;
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
    public void onArmSwingSpeed(ArmSwingSpeedEvent event) {
    	EntityLivingBase living = event.getEntityLiving();
    	if(ElixirEffectRegistry.EFFECT_SLUGARM.isActive(living)) {
            int strength = ElixirEffectRegistry.EFFECT_SLUGARM.getStrength(living);
            event.setSpeed(event.getSpeed() / (float)(2 << strength));
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        
        if(event.phase == TickEvent.Phase.END) {
            if(player != null && player.world != null && player.world.isRemote && player == Minecraft.getMinecraft().player) {
                if(ElixirEffectRegistry.EFFECT_HUNTERSSENSE.isActive(player)) {
                    int strength = ElixirEffectRegistry.EFFECT_HUNTERSSENSE.getStrength(player);
                    World world = player.world;
                    List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().grow(50, 50, 50));
                    List<TrailPos> availablePositions = new ArrayList<TrailPos>();
                    for(Entity e : entityList) {
                        if(e == player) continue;
                        EntityTrail trail = this.getTrail(e);
                        trail.update(strength);
                        TrailPos lastPos = null;
                        for(int i = 0; i < trail.cachedPositions.size(); i++) {
                            Vec3d pos = trail.cachedPositions.get(i);
                            if(lastPos != null) lastPos.nextPos = pos;
                            TrailPos tp = new TrailPos(pos, i, e);
                            availablePositions.add(tp);
                            lastPos = tp;
                        }
                    }
                    this.playerPos = new Vec3d(player.posX, player.posY, player.posZ);
                    availablePositions.sort(dstSorter);
                    int maxPointCount = 200;
                    int pointCount = Math.min(maxPointCount, availablePositions.size());
                    int crawlTicks = MathHelper.floor(20.0F + 120.0F - 120.0F / 4.0F * strength);
                    for(int i = 0; i < pointCount; i++) {
                        TrailPos tp = availablePositions.get(i);
                        if((player.ticksExisted - MathHelper.floor((float)crawlTicks / (float)EntityTrail.MAX_CACHE_SIZE * (float)tp.index)) % crawlTicks == 0) {
                            Vec3d pos = tp.pos;
                            if(tp.nextPos != null) {
                                int subSegments = 10;
                                Vec3d nextPos = tp.nextPos;
                                for(int s = 0; s <= subSegments; s++) {
                                    if((player.ticksExisted - MathHelper.floor((float)crawlTicks / (float)EntityTrail.MAX_CACHE_SIZE * (float)(tp.index + s / (float)subSegments))) % crawlTicks == 0) {
                                        double tpx = pos.x + 0.5F;
                                        double tpy = pos.y + 0.05F;
                                        double tpz = pos.z + 0.5F;
                                        double tpx2 = nextPos.x + 0.5F;
                                        double tpy2 = nextPos.y;
                                        double tpz2 = nextPos.z + 0.5F;
                                        double tpxi = tpx + (tpx2 - tpx) / (double)subSegments * s;
                                        double tpyi = tpy + (tpy2 - tpy) / (double)subSegments * s;
                                        double tpzi = tpz + (tpz2 - tpz) / (double)subSegments * s;
                                        BLParticles.PORTAL.spawn(world, tpxi, tpyi, tpzi, ParticleFactory.ParticleArgs.get().withScale(0.3F));
                                    }
                                }
                            } else {
                                double tpx = pos.x + 0.5F;
                                double tpy = pos.y + 0.05F;
                                double tpz = pos.z + 0.5F;
                                BLParticles.PORTAL.spawn(world, tpx, tpy, tpz, ParticleFactory.ParticleArgs.get().withScale(0.3F));
                            }
                        }
                    }
                    Iterator<Map.Entry<Entity, EntityTrail>> it = this.entityTrails.entrySet().iterator();
                    Map.Entry<Entity, EntityTrail> entry;
                    while(it.hasNext() && (entry = it.next()) != null) {
                        EntityTrail trail = entry.getValue();
                        if(trail.entity == null || trail.entity.isDead || !entityList.contains(entry.getKey())) {
                            it.remove();
                        }
                    }
                } else {
                    this.entityTrails.clear();
                }

                if(ElixirEffectRegistry.EFFECT_SWIFTARM.isActive(player)) {
                    if(Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() && !player.isActiveItemStackBlocking()) {
                        try {
                            RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
                            if(target == null || target.entityHit != null || target.typeOfHit == RayTraceResult.Type.MISS) {
                                Minecraft.getMinecraft().clickMouse();
                            } else if(target != null) {
                                if(!player.isSwingInProgress) {
                                    Minecraft.getMinecraft().sendClickBlockToController(true);
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
            
			if(player != null) {
				updatePlayerRootboundTicks(player);
			}
        }
    }
    
    //Ewww... but only way to set player rotation before rendering?...
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onFogColors(FogColors event) {
    	EntityPlayer player = Minecraft.getMinecraft().player;
    	if(player != null) {
    		NBTTagCompound nbt = player.getEntityData();
    		
	    	if(ElixirEffectRegistry.EFFECT_BASILISK.isActive(player) || ElixirEffectRegistry.EFFECT_PETRIFY.isActive(player)) {
	    		player.prevRotationPitch = player.rotationPitch = nbt.getFloat("thebetweenlands.petrify.pitch");
	    		player.prevRotationYaw = player.rotationYaw = nbt.getFloat("thebetweenlands.petrify.yaw");
	    		player.prevRotationYawHead = player.rotationYawHead = nbt.getFloat("thebetweenlands.petrify.yawHead");
			}
    	}
    }

    @SubscribeEvent
    public void onShootArrow(ArrowLooseEvent event) {
        if(event.getEntityPlayer() == Minecraft.getMinecraft().player) {
            ArrowPredictionRenderer.setRandomYawPitch();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player != null && player.world != null) {
            if(ElixirEffectRegistry.EFFECT_SAGITTARIUS.isActive(player)) {
                ArrowPredictionRenderer.render(Math.min((ElixirEffectRegistry.EFFECT_SAGITTARIUS.getStrength(player) + 1) / 3.0F, 1.0F));
            }
        }
    }

	@SideOnly(Side.CLIENT)
	private void updatePlayerRootboundTicks(EntityLivingBase entity) {
		NBTTagCompound nbt = entity.getEntityData();
		if(entity.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null || ElixirEffectRegistry.EFFECT_BASILISK.isActive(entity) || ElixirEffectRegistry.EFFECT_PETRIFY.isActive(entity)) {
			nbt.setInteger("thebetweenlands.stuckTicks", 5);
		} else {
			int rootBoundTicks = nbt.getInteger("thebetweenlands.stuckTicks");
			if(rootBoundTicks > 1) {
				nbt.setInteger("thebetweenlands.stuckTicks", rootBoundTicks - 1);
			} else {
				nbt.removeTag("thebetweenlands.stuckTicks");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFovUpdate(FOVUpdateEvent event) {
		EntityPlayer entity = event.getEntity();
		NBTTagCompound nbt = entity.getEntityData();
		
		//NBT is necessary so that FOV doesn't flicker when potion wears off .-.
		if(entity.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null || ElixirEffectRegistry.EFFECT_BASILISK.isActive(entity) || ElixirEffectRegistry.EFFECT_PETRIFY.isActive(entity) || nbt.hasKey("thebetweenlands.stuckTicks")) {
			event.setNewfov(1);
		}
	}
}
