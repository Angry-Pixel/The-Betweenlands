package thebetweenlands.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.AttachmentRegistry;

public class EquipmentOverlay {

	public static void renderEquipment(GuiGraphics graphics, DeltaTracker tracker) {
		Minecraft minecraft = Minecraft.getInstance();
		Gui gui = minecraft.gui;
		Player player = gui.getCameraPlayer();
		int width = graphics.guiWidth();
		int height = graphics.guiHeight();
		if (player != null && !player.isSpectator()) {
			if (BetweenlandsConfig.equipmentVisible) {
				EquipmentData data = player.getData(AttachmentRegistry.EQUIPMENT);
				HumanoidArm offhand = player.getMainArm().getOpposite();

				int posX;
				int posY;

				boolean isOnOppositeSide = BetweenlandsConfig.equipmentHotbarSide == 1;
				boolean showOnRightSide = (offhand == HumanoidArm.LEFT) != isOnOppositeSide;

				posY = switch (BetweenlandsConfig.equipmentZone) {
					case 1 -> {
						posX = 0;
						yield 0;
					}
					case 2 -> {
						posX = width - 18;
						yield 0;
					}
					case 3 -> {
						posX = width - 18;
						yield height - 18;
					}
					case 4 -> {
						posX = 0;
						yield height - 18;
					}
					case 5 -> {
						posX = 0;
						yield height / 2;
					}
					case 6 -> {
						posX = width / 2;
						yield 0;
					}
					case 7 -> {
						posX = width - 18;
						yield height / 2;
					}
					case 8 -> {
						posX = width / 2;
						yield height - 18;
					}
					default -> {
						if (showOnRightSide) {
							posX = width / 2 + 93;
							if (isOnOppositeSide && !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
								posX += 30;
							}
						} else {
							posX = width / 2 - 93 - 16;
							if (isOnOppositeSide && !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
								posX -= 30;
							}
						}
						yield height - 19;
					}
				};

				posX += BetweenlandsConfig.equipmentOffsetX;
				posY += BetweenlandsConfig.equipmentOffsetY;

				int yOffset = 0;
				PoseStack stack = graphics.pose();

				for (EquipmentInventoryType type : EquipmentInventoryType.values()) {
					Container inv = data.getContainer(player, type);

					int xOffset = 0;

					boolean hadItem = false;

					for (int i = 0; i < inv.getContainerSize(); i++) {
						ItemStack item = inv.getItem(i);

						if (!item.isEmpty()) {
							float scale = 1.0F;

							stack.pushPose();
							stack.translate(posX + xOffset, posY + yOffset, 0);
							stack.scale(scale, scale, scale);

							graphics.renderFakeItem(item, 0, 0);
							graphics.renderItemDecorations(Minecraft.getInstance().font, item, 0, 0, null);

							stack.popPose();

							if (showOnRightSide) {
								xOffset += BetweenlandsConfig.equipmentHorizontalSpacing;
							} else {
								xOffset -= BetweenlandsConfig.equipmentHorizontalSpacing;
							}

							hadItem = true;
						}
					}

					if (hadItem) {
						yOffset += BetweenlandsConfig.equipmentVerticalSpacing;
					}
				}
			}
		}
	}
}
