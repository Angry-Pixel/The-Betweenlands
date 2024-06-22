package thebetweenlands.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.*;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.savedata.AmateMapData;
import thebetweenlands.common.savedata.BLMapDecoration;

@SuppressWarnings({"JavadocReference", "unused", "RedundantSuppression", "deprecation"})
public class ASMHooks {
	/**
	 * Injection Point:<br>
	 * {@link net.minecraft.client.gui.MapRenderer.MapInstance#draw(PoseStack, MultiBufferSource, boolean, int)}<br>
	 * [BEFORE ISTORE 10]
	 */
	public static int mapRenderDecorations(int o, MapItemSavedData data, PoseStack stack, MultiBufferSource buffer, int light) {
		if (data instanceof AmateMapData mapData) {
			for (BLMapDecoration decoration : mapData.decorations.values()) {
				decoration.render(o, stack, buffer, light);
				o++;
			}
		}
		return o;
	}

	private static boolean isOurMap(ItemStack stack) {
		return stack.is(ItemRegistry.FILLED_AMATE_MAP.get());
	}

	/**
	 * Injection Point:<br>
	 * {@link net.minecraft.client.renderer.ItemInHandRenderer#renderArmWithItem(AbstractClientPlayer, float, float, InteractionHand, float, ItemStack, float, PoseStack, MultiBufferSource, int)} <br>
	 * [AFTER FIRST GETSTATIC {@link net.minecraft.world.item.Items#FILLED_MAP}]
	 */
	public static boolean shouldMapRender(boolean o, ItemStack stack) {
		return o || isOurMap(stack);
	}
}
