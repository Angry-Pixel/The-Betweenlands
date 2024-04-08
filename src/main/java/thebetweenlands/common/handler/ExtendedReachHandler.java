package thebetweenlands.common.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Predicates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.serverbound.MessageExtendedReach;

public class ExtendedReachHandler {
    @SubscribeEvent
    public static void onMouseInput(MouseInputEvent event) {
    	if(Mouse.getEventButtonState() && Minecraft.getMinecraft().gameSettings.keyBindAttack.isActiveAndMatches(Mouse.getEventButton() - 100)) {
    		handleAttack();
    	}
    }
    
    @SubscribeEvent
    public static void onKeyboardInput(KeyInputEvent event) {
    	int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
    	if(Keyboard.getEventKeyState() && Minecraft.getMinecraft().gameSettings.keyBindAttack.isActiveAndMatches(i)) {
    		handleAttack();
    	}
    }
    
    private static void handleAttack() {
    	List<Entity> hitEntities = new ArrayList<>();
        if(extendedRayTrace(trace -> hitEntities.add(trace.entityHit))) {
        	Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            if (player != null) {
                ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (!stack.isEmpty() && stack.getItem() instanceof IExtendedReach) {
                	((IExtendedReach)stack.getItem()).onLeftClick(player, stack);
                }
            }
        	
        	TheBetweenlands.networkWrapper.sendToServer(new MessageExtendedReach(hitEntities));
        }
    }

    @SubscribeEvent
    public static void onCrosshairRenderPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            extendedRayTrace(trace -> {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.pointedEntity == null && mc.gameSettings.thirdPersonView == 0) {
                    if (mc.playerController.isSpectator() && mc.pointedEntity == null)
                    {
                        RayTraceResult raytraceresult = mc.objectMouseOver;

                        if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
                        {
                            return;
                        }

                        BlockPos blockpos = raytraceresult.getBlockPos();

                        IBlockState state = mc.world.getBlockState(blockpos);
                        if (!state.getBlock().hasTileEntity(state) || !(mc.world.getTileEntity(blockpos) instanceof IInventory))
                        {
                            return;
                        }
                    }

                    if (mc.gameSettings.attackIndicator == 1 && !(mc.gameSettings.showDebugInfo && !mc.gameSettings.hideGUI && !mc.player.hasReducedDebug() && !mc.gameSettings.reducedDebugInfo)) {
                        GuiIngame gui = mc.ingameGUI;

                        mc.getTextureManager().bindTexture(Gui.ICONS);
                        
                        float f = mc.player.getCooledAttackStrength(0.0F);
                        boolean flag = false;

                        if (trace.entityHit instanceof EntityLivingBase && f >= 1.0F)
                        {
                            flag = mc.player.getCooldownPeriod() > 5.0F;
                            flag = flag & trace.entityHit.isEntityAlive();
                        }

                        int l = event.getResolution().getScaledWidth();
                        int i1 = event.getResolution().getScaledHeight();
                        int i = i1 / 2 - 7 + 16;
                        int j = l / 2 - 8;

                        if (flag)
                        {
                            gui.drawTexturedModalRect(j, i, 68, 94, 16, 16);
                        }
                        else if (f < 1.0F)
                        {
                            int k = (int)(f * 17.0F);
                            gui.drawTexturedModalRect(j, i, 36, 94, 16, 4);
                            gui.drawTexturedModalRect(j, i, 52, 94, k, 4);
                        }
                    }
                }
            });
        }
    }

    private static boolean extendedRayTrace(Consumer<RayTraceResult> consumer) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player != null) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IExtendedReach) {
                    double reach = ((IExtendedReach) stack.getItem()).getReach(player, stack);
                    RayTraceResult trace = getExtendedRayTrace(reach);
                    if (trace != null && trace.entityHit != null && trace.entityHit.hurtResistantTime == 0 && trace.entityHit != player) {
                        consumer.accept(trace);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static RayTraceResult getExtendedRayTrace(double dist) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();

        RayTraceResult result = null;
        if (mc.world != null && viewEntity != null) {
            result = viewEntity.rayTrace(dist, 0.0F);
            Vec3d viewPos = viewEntity.getPositionEyes(0.0F);

            double calcdist = dist;
            if (result != null) {
                calcdist = result.hitVec.distanceTo(viewPos);
            }
            Vec3d startVec = viewEntity.getLook(1.0F);
            Vec3d endVec = viewPos.add(startVec.x * dist, startVec.y * dist, startVec.z * dist);

            Entity targetEntity = null;
            Vec3d hitVec = null;
            List<Entity> list = mc.world.getEntitiesInAABBexcluding(viewEntity, viewEntity.getEntityBoundingBox()
                    .expand(startVec.x * dist, startVec.y * dist, startVec.z * dist)
                    .grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, Predicates.notNull(), Entity::canBeCollidedWith));
            double entityDist = calcdist;
            for (Entity entity : list) {
                AxisAlignedBB entityAABB = entity.getEntityBoundingBox().grow((double) entity.getCollisionBorderSize());
                RayTraceResult traceResult = entityAABB.calculateIntercept(viewPos, endVec);

                if (entityAABB.contains(viewPos)) {
                    if (entityDist >= 0.0D) {
                        targetEntity = entity;
                        hitVec = traceResult == null ? viewPos : traceResult.hitVec;
                        entityDist = 0.0D;
                    }
                } else if (traceResult != null) {
                    double d1 = viewPos.distanceTo(traceResult.hitVec);
                    if ((d1 < entityDist) || (entityDist == 0.0D)) {
                        if (entity.getLowestRidingEntity() == viewEntity.getLowestRidingEntity() && !entity.canRiderInteract()) {
                            if (entityDist == 0.0D) {
                                targetEntity = entity;
                                hitVec = traceResult.hitVec;
                            }
                        } else {
                            targetEntity = entity;
                            hitVec = traceResult.hitVec;
                            entityDist = d1;
                        }
                    }
                }
            }
            if ((targetEntity != null) && ((entityDist < calcdist) || (result == null))) {
                result = new RayTraceResult(targetEntity, hitVec);
            }
        }
        return result;
    }

}
