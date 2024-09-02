package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    		extendedRayTrace(trace -> {
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
    		});
    	}
    }
    
    public static void handleAttack() {
    	List<Entity> hitEntities = new ArrayList<>();
        if(extendedRayTrace(trace -> hitEntities.add(trace.getEntity()))) {
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
    

    private static boolean extendedRayTrace(Consumer<EntityHitResult> consumer) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return false;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() instanceof IExtendedReach extendedReach) {
            double reach = extendedReach.getReach(player, stack);
            EntityHitResult trace = getExtendedRayTrace(reach);
            if (trace != null && trace.getEntity() != null && trace.getEntity().invulnerableTime == 0 && trace.getEntity() != player) {
                consumer.accept(trace);
            }
            return true;
        }
        return false;
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
//            Predicate<Entity> predicate = Predicates.<Entity>notNull().and(EntitySelector.NO_SPECTATORS).and(Entity::isPickable); // isPickable is the new name for the old canBeCollidedWith?
            Predicate<Entity> predicate = Predicates.<Entity>notNull().and(EntitySelector.NO_SPECTATORS).and(Entity::isAttackable).and(entity -> !entity.skipAttackInteraction(viewEntity));
            
            // TODO Leaving legacy code here because I haven't had enough time to fully consider it, maybe could be replaced by `result = ProjectileUtil.getEntityHitResult(level, viewEntity, startVec, endVec, boundingBox, predicate);` in future
            Entity targetEntity = null;
            Vec3 hitVec = null;
            List<Entity> list = level.getEntities(viewEntity, boundingBox, predicate);
            double entityDist = calcdist;
            for (Entity entity : list) {
                AABB entityAABB = entity.getBoundingBox().inflate((double) entity.getPickRadius());
                Optional<Vec3> traceResult = entityAABB.clip(startVec, endVec);

                if(!traceResult.isPresent()) continue;
                
                Vec3 traceHitVec = traceResult.get();
                
                if (entityAABB.contains(startVec)) {
                    if (entityDist >= 0.0D) {
                        targetEntity = entity;
                        hitVec = traceResult == null ? startVec : traceHitVec;
                        entityDist = 0.0D;
                    }
                } else if (traceResult != null) {
                    double d1 = startVec.distanceTo(traceHitVec);
                    if ((d1 < entityDist) || (entityDist == 0.0D)) {
                        if (entity.getRootVehicle() == viewEntity.getRootVehicle() && !entity.canRiderInteract()) {
                            if (entityDist == 0.0D) {
                                targetEntity = entity;
                                hitVec = traceHitVec;
                            }
                        } else {
                            targetEntity = entity;
                            hitVec = traceHitVec;
                            entityDist = d1;
                        }
                    }
                }
            }
            if ((targetEntity != null) && ((entityDist < calcdist) || (result == null))) {
                result = new EntityHitResult(targetEntity, hitVec);
            }
            
            // Test
            // result = ProjectileUtil.getEntityHitResult(level, viewEntity, startVec, endVec, boundingBox, predicate);
        }
        return result;
    }

}
