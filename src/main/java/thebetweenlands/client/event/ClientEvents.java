package thebetweenlands.client.event;

import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.client.handler.AmbienceHandler;
import thebetweenlands.client.handler.BossHandler;
import thebetweenlands.client.handler.CameraPositionHandler;
import thebetweenlands.client.handler.ElixirClientHandler;
import thebetweenlands.client.handler.EquipmentRenderingHandler;
import thebetweenlands.client.handler.InputHandler;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.handler.MainMenuHandler;
import thebetweenlands.client.handler.MusicHandler;
import thebetweenlands.client.handler.ScreenHandler;
import thebetweenlands.client.handler.ShaderHandler;
import thebetweenlands.client.handler.equipment.RadialMenuHandler;
import thebetweenlands.common.block.structure.DungeonDoorRunesBlock;
import thebetweenlands.client.handler.*;
import thebetweenlands.client.sky.BLSkyRenderer;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.handler.EntityUnmountHandler;
import thebetweenlands.common.handler.EnvironmentEventHandler;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.item.shield.SwatShieldItem;
import thebetweenlands.common.item.tool.SlingshotItem;
import thebetweenlands.util.RenderUtils;

import java.util.Optional;

public class ClientEvents {

	public static void init() {
		AmbienceHandler.init();
		BossHandler.init();
		CameraPositionHandler.INSTANCE.init();
		ClientSwingHandler.init();
		ElixirClientHandler.init();
		EquipmentRenderingHandler.init();
		ItemTooltipHandler.init();
		MainMenuHandler.init();
		MusicHandler.INSTANCE.init();
		RadialMenuHandler.INSTANCE.init();
		ScreenHandler.init();

		NeoForge.EVENT_BUS.addListener(FoodSicknessHandler::tickSicknessClient);

		NeoForge.EVENT_BUS.addListener(RenderUtils::incrementTickCounter);
		NeoForge.EVENT_BUS.addListener(RenderUtils::tickFrameCounter);

		NeoForge.EVENT_BUS.addListener(ClientEvents::renderMobsOnFrame);
		NeoForge.EVENT_BUS.addListener(ClientEvents::changeFOV);
		NeoForge.EVENT_BUS.addListener(ClientEvents::removeBlockHitboxes);

		NeoForge.EVENT_BUS.addListener(ShaderHandler::onRenderWorldLast);
		NeoForge.EVENT_BUS.addListener(ShaderHandler::onRenderWeather);

		NeoForge.EVENT_BUS.addListener(EntityUnmountHandler::onRenderHUD);
		NeoForge.EVENT_BUS.addListener(InputHandler::handleKeybindInputs);
		NeoForge.EVENT_BUS.addListener(InputHandler::performDoubleJump);

		NeoForge.EVENT_BUS.addListener(BLSkyRenderer::onClientTick);

		NeoForge.EVENT_BUS.addListener(FogHandler::onFogRenderEvent);
		NeoForge.EVENT_BUS.addListener(FogHandler::onClientTick);
		NeoForge.EVENT_BUS.addListener(FogHandler::onFogColor);
		NeoForge.EVENT_BUS.addListener(FogHandler::updateFog);

		NeoForge.EVENT_BUS.addListener(ArmSwingSpeedHandler::fireArmSwingEvent);
	}

	static void renderMobsOnFrame(RenderItemInFrameEvent event) {
		if (!event.isCanceled() && event.getItemStack().getItem() instanceof MobItem<?> mob) {
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

	static void changeFOV(ComputeFovModifierEvent event) {
		ItemStack activeItem = event.getPlayer().getUseItem();
		if (activeItem.getItem() instanceof SwatShieldItem shield && SwatShieldItem.isPreparingCharge(activeItem, event.getPlayer())) {
			int preparingTicks = shield.getPreparingChargeTicks(activeItem, event.getPlayer());
			float progress = Math.min(shield.getChargeTime(activeItem, event.getPlayer(), preparingTicks) / (float) shield.getMaxChargeTime(activeItem, event.getPlayer()), 1);
			event.setNewFovModifier(1.0F - progress * 0.25F);
		}

		if (activeItem.getItem() instanceof SlingshotItem) {
			int usedTicks = activeItem.getItem().getUseDuration(activeItem, event.getPlayer()) - event.getPlayer().getUseItemRemainingTicks();
			float strength = (float) usedTicks / 20.0F;
			strength = (strength * strength + strength * 2.0F) / 3.0F * 1.15F;
			if (strength > 1.0F) {
				strength = 1.0F;
			}
			event.setNewFovModifier(1.0F - strength * 0.25F);
		}
	}

	static void removeBlockHitboxes(RenderHighlightEvent.Block event) {
		Level level = Minecraft.getInstance().level;
		if (level.getBlockState(event.getTarget().getBlockPos()).getBlock() instanceof DungeonDoorRunesBlock) {
			event.setCanceled(true);
		}
	}

}
