package thebetweenlands.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.common.component.entity.circlegem.CircleGemHelper;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.item.equipment.LurkerSkinPouchItem;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRenderingHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(RenderLivingEvent.Pre.class, EquipmentRenderingHandler::renderAmulets);
		NeoForge.EVENT_BUS.addListener(EquipmentRenderingHandler::renderPouch);
	}

	//TODO try to bring back the silly pulsing effect, couldnt get it working properly
	private static <T extends LivingEntity, M extends EntityModel<T>> void renderAmulets(RenderLivingEvent.Pre<T, M> event) {
		LivingEntity entity = event.getEntity();
		if (entity.isInvisibleTo(Minecraft.getInstance().player) || entity.isSpectator()) {
			return;
		}

		EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
		Container inv = data.getContainer(entity, EquipmentInventoryType.AMULET);
		List<ItemStack> items = new ArrayList<>(inv.getContainerSize());

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && CircleGemHelper.getGem(stack) != CircleGemType.NONE) {
				items.add(stack);
			}
		}

		int amulets = items.size();

		if (amulets > 0) {
			float degOffset = 360.0F / amulets;
			PoseStack stack = event.getPoseStack();
			stack.pushPose();

			int i = 0;
			for (ItemStack item : items) {
				stack.mulPose(Axis.YP.rotationDegrees(degOffset));

				CircleGemType gem = CircleGemHelper.getGem(item);
				ItemStack gemItem = null;

				switch (gem) {
					case CRIMSON:
						gemItem = ItemRegistry.CRIMSON_MIDDLE_GEM.toStack();
						break;
					case AQUA:
						gemItem = ItemRegistry.AQUA_MIDDLE_GEM.toStack();
						break;
					case GREEN:
						gemItem = ItemRegistry.GREEN_MIDDLE_GEM.toStack();
						break;
					default:
				}

				if (gemItem != null) {
					stack.pushPose();
					stack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + event.getPartialTick()) * 1.5F));
					double eyeHeight = entity.getEyeHeight();
					stack.translate(0, eyeHeight / 1.5D + Math.sin((entity.tickCount + event.getPartialTick()) / 60.0D + (double) i / amulets * Math.PI * 2.0D) / 2.0D * entity.getBbHeight() / 4.0D, entity.getBbWidth() / 1.25D);
					stack.scale(0.25F * entity.getBbHeight() / 2.0F, 0.25F * entity.getBbHeight() / 2.0F, 0.25F * entity.getBbHeight() / 2.0F);
					RenderSystem.enableBlend();
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);
					Minecraft.getInstance().getItemRenderer().renderStatic(gemItem, ItemDisplayContext.GUI, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, event.getPoseStack(), event.getMultiBufferSource(), null, entity.getId());
					stack.popPose();
					i++;
				}
			}

			stack.popPose();
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	public static void renderPouch(RenderPlayerEvent.Post event) {
		Player player = event.getEntity();
		if (player.isInvisibleTo(Minecraft.getInstance().player) || player.isSpectator()) {
			return;
		}
		EquipmentData data = player.getData(AttachmentRegistry.EQUIPMENT);
		Container inv = data.getContainer(player, EquipmentInventoryType.MISC);
		ItemStack pouch = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof LurkerSkinPouchItem) {
				pouch = stack;
				break;
			}
		}

		if (!pouch.isEmpty()) {
			PoseStack stack = event.getPoseStack();
			stack.pushPose();
			stack.translate(0.0D, 1.0D, 0.0D);
			stack.mulPose(Axis.YP.rotationDegrees(90 - Mth.lerp(event.getPartialTick(), player.yBodyRotO, player.yBodyRot)));
			stack.translate(player.isSecondaryUseActive() ? 0.25D : 0.0D, (player.isSecondaryUseActive() ? -0.15D : 0) - 0.2D, -0.25D);
			float limbSwingAmount = player.walkAnimation.speed(event.getPartialTick());
			float swing = (float) Math.sin((player.walkAnimation.position() - limbSwingAmount * (1.0F - event.getPartialTick())) / 1.4F) * limbSwingAmount;
			stack.mulPose(Axis.ZP.rotationDegrees(swing * 25.0F));
			stack.mulPose(Axis.XP.rotationDegrees(swing * 13.0F));
			stack.mulPose(Axis.ZP.rotationDegrees(swing * -10.0F));
			stack.translate(0, -0.1D, 0);
			RenderSystem.enableBlend();
			stack.pushPose();
			stack.translate(0, 0, 0.02D);
			stack.scale(0.4F, 0.4F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(pouch, ItemDisplayContext.GUI, event.getPackedLight(), OverlayTexture.NO_OVERLAY, event.getPoseStack(), event.getMultiBufferSource(), null, player.getId());
			stack.popPose();

			stack.pushPose();
			stack.scale(0.37F, 0.37F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(pouch, ItemDisplayContext.GUI, event.getPackedLight(), OverlayTexture.NO_OVERLAY, event.getPoseStack(), event.getMultiBufferSource(), null, player.getId());
			stack.popPose();
			stack.popPose();
		}
	}
}
