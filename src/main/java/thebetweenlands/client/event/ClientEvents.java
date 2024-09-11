package thebetweenlands.client.event;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.items.MobItem;

public class ClientEvents {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ClientEvents::renderMobsOnFrame);
	}

	static void renderMobsOnFrame(RenderItemInFrameEvent event) {
		if (!event.isCanceled() && event.getItemStack().getItem() instanceof MobItem mob) {
			Entity entity = mob.createCapturedEntity(event.getItemFrameEntity().level(), 0, 0, 0, event.getItemStack(), false);

			event.getPoseStack().scale(-0.5F, 0.5F, 0.5F);
			if (entity != null && !(entity instanceof PartEntity<?>)) {
				event.setCanceled(true);
				float horizontalOffset = 0;
				entity.moveTo(0, 0, 0, 0, 0);

				if (entity instanceof Anadia) {
					horizontalOffset = entity.getBbWidth() / 5;
				}
				event.getPoseStack().translate(-horizontalOffset, -entity.getBbHeight() / 4D, -0.05D);
				event.getPoseStack().mulPose(Axis.YP.rotationDegrees(90));
				entity.setXRot(0.0F);
				entity.tickCount = 0;
				Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0D, 0D, 0D, 0F, 0F, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
			}
		}
	}
}
