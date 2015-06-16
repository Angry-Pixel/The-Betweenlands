package thebetweenlands.client.event;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.render.TextureDecay;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.IDecayable;
import thebetweenlands.world.storage.BetweenlandsWorldData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DecayTextureStitchHandler {
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre e) {
		if (e.map == Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)) {
			for (Item item : BLItemRegistry.ITEMS) {
				if (item instanceof IDecayable) {
					IDecayable decayableItem = (IDecayable) item;
					IIcon[] icons = decayableItem.getIcons();
					IIcon[] decayIcons = new IIcon[icons.length];
					for (int i = 0; i < icons.length; i++) {
						String iconName = icons[i].getIconName();
						String decayIconName = iconName + "_decay";
						TextureDecay decayTexture = new TextureDecay(decayIconName, iconName);
						e.map.setTextureEntry(decayIconName, decayTexture);
						decayIcons[i] = decayTexture;
					}
					decayableItem.setDecayIcons(decayIcons);
				}
			}
		}
	}
}
