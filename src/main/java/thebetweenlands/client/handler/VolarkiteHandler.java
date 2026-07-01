package thebetweenlands.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import thebetweenlands.client.renderer.entity.volarkite.VolarkiteRenderer;
import thebetweenlands.common.entity.VolarkiteEntity;

public class VolarkiteHandler {

    private static boolean isRenderingPlayer = false;

	public static void removeHandWhenVolarkiting(RenderHandEvent event) {
		if (Minecraft.getInstance().player.getVehicle() instanceof VolarkiteEntity || Minecraft.getInstance().player.hasPassenger(entity -> entity instanceof VolarkiteEntity)) {
			event.setCanceled(true);
		}
	}

    public static void replacePlayerRenderer(RenderPlayerEvent.Pre event) {
        if (isRenderingPlayer || (event.getEntity() == Minecraft.getInstance().player && Minecraft.getInstance().screen instanceof EffectRenderingInventoryScreen<?>))
        	return;

        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
        Entity mount = player.getVehicle();
        VolarkiteEntity kite;

        if (mount instanceof VolarkiteEntity)
            kite = (VolarkiteEntity) mount;
        else
            kite = (VolarkiteEntity) player.getPassengers().stream().filter(e -> e instanceof VolarkiteEntity).findAny().orElse(null);

        if (kite != null) {
            event.setCanceled(true);
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            float kiteYaw = Mth.lerp(event.getPartialTick(), kite.yRotO, kite.getYRot());

            poseStack.mulPose(Axis.YP.rotationDegrees(-kiteYaw));
            poseStack.translate(0, 1.0D, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(event.getPartialTick(), kite.zRotO, kite.zRot)));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(event.getPartialTick(), kite.xRotO, kite.getXRot())));
            poseStack.translate(0, -1.0D, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(kiteYaw));
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(event.getPartialTick(), player.yBodyRotO, player.yBodyRot) - kiteYaw));

			if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(kite) instanceof VolarkiteRenderer renderer) {
				isRenderingPlayer = true;
				renderer.renderRider(player, event.getPartialTick(), poseStack, event.getMultiBufferSource(), event.getPackedLight());
				isRenderingPlayer = false;
			}

            poseStack.popPose();
        }
    }
}
