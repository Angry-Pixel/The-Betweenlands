package thebetweenlands.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import thebetweenlands.client.render.TextureCorrosion;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.utils.CorrodibleItemHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CorrosionTextureStitchHandler {
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre e) {
		if (e.map == Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)) {
			for (Item item : BLItemRegistry.ITEMS) {
				if (item instanceof ICorrodible) {
					ICorrodible corrodibleItem = (ICorrodible) item;
					IIcon[] icons = corrodibleItem.getIcons();
					IIcon[][] corrosionIcons = new IIcon[icons.length][CorrodibleItemHelper.CORROSION_STAGE_COUNT];
					for (int i = 0; i < icons.length; i++) {
						for (int n = 0; n < CorrodibleItemHelper.CORROSION_STAGE_COUNT; n++) {
							String iconName = icons[i].getIconName();
							String corrosionIconName = iconName + "_decay_" + n;
							TextureCorrosion corrosionTexture = new TextureCorrosion(corrosionIconName, iconName, n);
							e.map.setTextureEntry(corrosionIconName, corrosionTexture);
							corrosionIcons[i][n] = corrosionTexture;
						}
					}
					corrodibleItem.setCorrosionIcons(corrosionIcons);
				}
			}
		}
	}
}
