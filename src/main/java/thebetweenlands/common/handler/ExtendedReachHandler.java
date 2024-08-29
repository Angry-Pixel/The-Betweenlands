package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.common.TheBetweenlands;

@EventBusSubscriber(modid = TheBetweenlands.ID, bus = Bus.GAME)
public class ExtendedReachHandler {
	// TODO move SubscribeEvent to HandlerEvents

    @SubscribeEvent
    public static void attack(InputEvent.InteractionKeyMappingTriggered event) {
    	if(event.isAttack())
    		handleAttack();
    }
    
    public static void handleAttack() {
    	List<Entity> hitEntities = new ArrayList<>();
        if(extendedRayTrace(trace -> hitEntities.add(trace.getEntity()))) {
        	Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null) {
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (!stack.isEmpty() && stack.getItem() instanceof IExtendedReach) {
                	((IExtendedReach)stack.getItem()).onSwing(player, stack);
                }
            }
        	
//            PacketDistributor.sendToServer(new MessageExtendedReach(hitEntities));
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

        if (stack.getItem() instanceof IExtendedReach) {
            double reach = ((IExtendedReach) stack.getItem()).getReach(player, stack);
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
        if (mc.level != null && viewEntity != null) {
            Vec3 viewPos = viewEntity.getEyePosition(0.5F);
            Vec3 startVec = viewEntity.getViewVector(0.5F);
            Vec3 endVec = viewPos.add(startVec.x * dist, startVec.y * dist, startVec.z * dist);

            // Don't hit entities behind blocks
//            result = ProjectileUtil.getEntityHitResult(viewEntity, viewPos, endVec, new AABB(viewPos, endVec), entity -> entity != viewEntity && !entity.isSpectator() && entity.canBeCollidedWith(), dist);
            BlockHitResult blockedTrace = mc.level.clip(new ClipContext(viewPos, endVec, Block.COLLIDER, Fluid.NONE, viewEntity));

            double calcdist = dist;
            if (blockedTrace != null) {
                calcdist = blockedTrace.getLocation().distanceTo(viewPos);
            }

            Entity targetEntity = null;
            Vec3 hitVec = null;
            List<Entity> list = mc.level.getEntities(viewEntity, viewEntity.getBoundingBox()
                    .expandTowards(startVec.x * dist, startVec.y * dist, startVec.z * dist)
                    .inflate(1.0D, 1.0D, 1.0D), Predicates.<Entity>notNull().and(EntitySelector.CAN_BE_COLLIDED_WITH));
            double entityDist = calcdist;
            for (Entity entity : list) {
                AABB entityAABB = entity.getBoundingBox().inflate((double) entity.getPickRadius());
                Optional<Vec3> traceResult = entityAABB.clip(viewPos, endVec);

                if(traceResult.isEmpty()) continue;
                
                Vec3 traceHitVec = traceResult.get();
                
                if (entityAABB.contains(viewPos)) {
                    if (entityDist >= 0.0D) {
                        targetEntity = entity;
                        hitVec = traceResult == null ? viewPos : traceHitVec;
                        entityDist = 0.0D;
                    }
                } else if (traceResult != null) {
                    double d1 = viewPos.distanceTo(traceHitVec);
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
        }
        return result;
    }

}
