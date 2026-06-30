package thebetweenlands.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import thebetweenlands.common.entity.VolarkiteEntity;

public class PlayerVolakiteRenderHandler {
    private static boolean isRenderingPlayer = false;

    public static void volarkitePlayerRenderPre(RenderPlayerEvent.Pre event) {
        if (isRenderingPlayer)
        	return;

        AbstractClientPlayer player = (AbstractClientPlayer) event.getEntity();
        Entity mount = player.getVehicle();
        VolarkiteEntity kite = null;

        if (mount instanceof VolarkiteEntity)
            kite = (VolarkiteEntity) mount;
        else
            kite = (VolarkiteEntity) player.getPassengers().stream().filter(e -> e instanceof VolarkiteEntity).findAny().orElse(null);

        if (kite != null) {
            event.setCanceled(true);
            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();

            float kiteYaw = Mth.lerp(event.getPartialTick(), kite.yRotO, kite.getYRot());

            poseStack.mulPose(Axis.YN.rotationDegrees(kiteYaw));
            poseStack.translate(0, 1.0D, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(event.getPartialTick(), kite.prevRotationRoll, kite.rotationRoll)));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(event.getPartialTick(), kite.xRotO, kite.getXRot())));
            poseStack.translate(0, -1.0D, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(kiteYaw));
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(event.getPartialTick(), player.yRotO, player.getYRot()) - kiteYaw));

            isRenderingPlayer = true;
            event.getRenderer().render(player, player.getYRot(), event.getPartialTick(), poseStack, event.getMultiBufferSource(), event.getPackedLight());
            isRenderingPlayer = false;
 
            poseStack.popPose();
        }
    }
}
