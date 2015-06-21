package thebetweenlands.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import thebetweenlands.client.render.TextureDecay;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.IDecayable;
import thebetweenlands.utils.DecayableItemHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DecayTextureStitchHandler {
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre e) {
		if (e.map == Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)) {
			for (Item item : BLItemRegistry.ITEMS) {
				if (item instanceof IDecayable) {
					IDecayable decayableItem = (IDecayable) item;
					IIcon[] icons = decayableItem.getIcons();
					IIcon[][] decayIcons = new IIcon[icons.length][DecayableItemHelper.DECAY_STAGE_COUNT];
					for (int i = 0; i < icons.length; i++) {
						for (int n = 0; n < DecayableItemHelper.DECAY_STAGE_COUNT; n++) {
							String iconName = icons[i].getIconName();
							String decayIconName = iconName + "_decay_" + n;
							TextureDecay decayTexture = new TextureDecay(decayIconName, iconName, n);
							e.map.setTextureEntry(decayIconName, decayTexture);
							decayIcons[i][n] = decayTexture;
						}
					}
					decayableItem.setDecayIcons(decayIcons);
				}
			}
		}
	}
}
