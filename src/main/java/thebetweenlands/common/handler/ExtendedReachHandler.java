package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.common.network.serverbound.ExtendedReachAttackPacket;

public class ExtendedReachHandler {

    public static void onAttackEvent(InputEvent.InteractionKeyMappingTriggered event) {
    	if(event.isAttack())
    		handleAttack();
    }

    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_full");
    
    public static void onCrosshairRenderPost(RenderGuiLayerEvent.Post event) {
    	if(event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
    		EntityHitResult trace = extendedRayTrace(true);
    		
    		if(trace == null) {
    			return;
    		}
    		
            Minecraft mc = Minecraft.getInstance();

            // Don't render in third person
	        Options options = mc.options;
	        if (!options.getCameraType().isFirstPerson()) {
	        	return;
            }

        	// Don't render in spectator
            if (mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            	return;
            }

        	// Don't render if F3 is up
            GuiGraphics guiGraphics = event.getGuiGraphics();
            if (mc.getDebugOverlay().showDebugScreen() && !mc.player.isReducedDebugInfo() && !options.reducedDebugInfo().get()) {
            	return;
            }
            
            // Don't render crosshair attack indicator
            if (mc.options.attackIndicator().get() != AttackIndicatorStatus.CROSSHAIR) {
            	return;
            }

            float f = mc.player.getAttackStrengthScale(0.0F);
            
            // Vanilla always renders a crosshair while recharging
            if(f < 1.0F)
            	return;
            
            // Do checks for if we're rendering a crosshair already
            boolean flag = false;
            if (mc.crosshairPickEntity != null && mc.crosshairPickEntity instanceof LivingEntity) {
                flag = mc.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag &= mc.crosshairPickEntity.isAlive();
            }
            
            // Already rendering a normal crosshair, don't double up
            if(flag)
            	return;

            // Check that we can render our own crosshair
            flag = false;
            if (trace.getEntity() != null && trace.getEntity() instanceof LivingEntity) {
                flag = mc.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag &= trace.getEntity().isAlive();
            }
            
            // Render us a crosshair
            if(flag) {
	            RenderSystem.enableBlend();
	            
                RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
                );
                
                int j = guiGraphics.guiHeight() / 2 - 7 + 16;
                int k = guiGraphics.guiWidth() / 2 - 8;
                guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
                
                RenderSystem.defaultBlendFunc();
                
                RenderSystem.disableBlend();
            }
    	}
    }
    
    public static void handleAttack() {
    	List<Entity> hitEntities = new ArrayList<>();
        if(extendedRayTraceAll(trace -> hitEntities.add(trace.getEntity()), false)) {
        	Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null) {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (!stack.isEmpty() && stack.getItem() instanceof IExtendedReach extendedReach) {
                	extendedReach.onSwing(player, stack);
                }
            }
        	
            PacketDistributor.sendToServer(new ExtendedReachAttackPacket(hitEntities));
        }
    }

    private static boolean extendedRayTraceAll(Consumer<EntityHitResult> consumer, boolean ignoreInvulnerability) {
        Minecraft mc = Minecraft.getInstance();
        
        // Assume player is view entity (IExtendedReach.getReach() requires a player argument)
        Player player = mc.player;
        
        if (player == null || !player.isAddedToLevel() || player.level() == null) {
            return false;
        }

        ItemStack stack = player.getWeaponItem();
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() instanceof IExtendedReach extendedReach) {
            double reach = extendedReach.getReach(player, stack);

            // There is no easy way to move this code to a separate function without having to replicate it all
            
        	Level level = player.level();
        	
            Vec3 dirVec = player.getViewVector(0.0F);

        	// Note: startVec here is NOT equivalent to startVec in the 1.12 code
            Vec3 startVec = player.getEyePosition();
            Vec3 endVec = startVec.add(dirVec.scale(reach));

            // Don't hit entities behind blocks
            BlockHitResult blockedTrace = level.clip(new ClipContext(startVec, endVec, Block.COLLIDER, Fluid.NONE, player));

            double maxDistance = reach;
            if (blockedTrace.getType() != HitResult.Type.MISS) {
            	endVec = blockedTrace.getLocation();
                maxDistance = blockedTrace.getLocation().distanceTo(startVec);
            }
        	
            AABB boundingBox = player.getBoundingBox().expandTowards(dirVec.scale(maxDistance)).inflate(1.0D);
            
            Predicate<Entity> predicate = Predicates.<Entity>notNull().and(EntitySelector.NO_SPECTATORS).and(Entity::isPickable).and(Entity::isAttackable); // isPickable is the new name for the old canBeCollidedWith?
            
            final double maxDistanceSq = maxDistance * maxDistance;
            
            // Epically epic priority queue so it always returns the closest entity first
            PriorityQueue<Pair<Double, EntityHitResult>> results = new PriorityQueue<Pair<Double, EntityHitResult>>((o1, o2) -> Mth.sign(o1.getFirst() - o2.getFirst()));
            
            List<Entity> list = level.getEntities(player, boundingBox, predicate);
            for (Entity entity : list) {
                Vec3 hitVec = null;
                
                AABB entityAABB = entity.getBoundingBox().inflate((double) entity.getPickRadius());
                Optional<Vec3> traceResult = entityAABB.clip(startVec, endVec);
                
                if (entityAABB.contains(startVec)) {
                	hitVec = traceResult.orElse(startVec);
                } else if (traceResult.isPresent()) {
                    Vec3 traceHitVec = traceResult.get();
                    
                    if(maxDistanceSq == 0.0D || (startVec.distanceToSqr(traceHitVec) < maxDistanceSq && !(entity.getRootVehicle() == player.getRootVehicle() && !entity.canRiderInteract()))) {
                    	hitVec = traceHitVec;
                    }
                }
                
                if(hitVec != null) {
                	EntityHitResult result = new EntityHitResult(entity, hitVec);
                	results.add(new Pair<Double, EntityHitResult>(startVec.distanceToSqr(hitVec), result));
                }
            }
            
            for(Pair<Double, EntityHitResult> pair : results) {
                EntityHitResult trace = pair.getSecond();
                if (trace != null && trace.getType() != Type.MISS && trace.getEntity() != null && (ignoreInvulnerability || trace.getEntity().invulnerableTime == 0) && trace.getEntity() != player) {
                	consumer.accept(pair.getSecond());
                }
            }
            
            return true;
        }
        return false;
    }
    

    private static EntityHitResult extendedRayTrace(boolean ignoreInvulnerability) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return null;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty()) {
            return null;
        }

        if (stack.getItem() instanceof IExtendedReach extendedReach) {
            double reach = extendedReach.getReach(player, stack);
            EntityHitResult trace = getExtendedRayTrace(reach);
            if (trace != null && trace.getType() != Type.MISS && trace.getEntity() != null && (ignoreInvulnerability || trace.getEntity().invulnerableTime == 0) && trace.getEntity() != player) {
                return trace;
            }
        }
        return null;
    }

    
    public static EntityHitResult getExtendedRayTrace(double dist) {
        Minecraft mc = Minecraft.getInstance();
        Entity viewEntity = mc.getCameraEntity();

        EntityHitResult result = null;
        if (viewEntity != null && viewEntity.isAddedToLevel() && viewEntity.level() != null) {
        	Level level = viewEntity.level();
        	
            Vec3 dirVec = viewEntity.getViewVector(0.0F);

        	// Note: startVec here is NOT equivalent to startVec in the 1.12 code
            Vec3 startVec = viewEntity.getEyePosition();
            Vec3 endVec = startVec.add(dirVec.scale(dist));
        	
            // Don't hit entities behind blocks
            BlockHitResult blockedTrace = level.clip(new ClipContext(startVec, endVec, Block.COLLIDER, Fluid.NONE, viewEntity));

            double calcdist = dist;
            if (blockedTrace.getType() != HitResult.Type.MISS) {
            	endVec = blockedTrace.getLocation();
                calcdist = blockedTrace.getLocation().distanceTo(startVec);
            }
        	

            AABB boundingBox = viewEntity.getBoundingBox().expandTowards(dirVec.scale(calcdist)).inflate(1.0D);
            
            Predicate<Entity> predicate = Predicates.<Entity>notNull().and(EntitySelector.NO_SPECTATORS).and(Entity::isPickable).and(Entity::isAttackable); // isPickable is the new name for the old canBeCollidedWith?
            
            result = ProjectileUtil.getEntityHitResult(viewEntity, startVec, endVec, boundingBox, predicate, calcdist * calcdist);
        }
        return result;
    }

}
