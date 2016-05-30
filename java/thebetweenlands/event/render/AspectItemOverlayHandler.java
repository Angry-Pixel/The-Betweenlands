package thebetweenlands.event.render;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.utils.AspectIconRenderer;

public class AspectItemOverlayHandler {
	public static final AspectItemOverlayHandler INSTANCE = new AspectItemOverlayHandler();

	public static final DecimalFormat ASPECT_AMOUNT_FORMAT = new DecimalFormat("#.##");

	private Field f_theSlot = ReflectionHelper.findField(GuiContainer.class, "theSlot", "field_147006_u", "u");

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderScreen(DrawScreenEvent.Post event) {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Minecraft.getMinecraft().currentScreen instanceof GuiContainer && Minecraft.getMinecraft().thePlayer != null) {
			try {
				Slot slot = (Slot) f_theSlot.get((GuiContainer) Minecraft.getMinecraft().currentScreen);
				if(slot != null && slot.getHasStack()) {
					ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
					double mouseX = (Mouse.getX() * resolution.getScaledWidth_double()) / Minecraft.getMinecraft().displayWidth;
					double mouseY = resolution.getScaledHeight_double() - (Mouse.getY() * resolution.getScaledHeight_double()) / Minecraft.getMinecraft().displayHeight - 1;
					GL11.glPushMatrix();
					GL11.glTranslated(mouseX + 8, mouseY, 200);
					int yOffset = -40;
					int width = 0;
					List<Aspect> aspects = AspectManager.get(Minecraft.getMinecraft().theWorld).getDiscoveredAspects(slot.getStack(), AspectManager.getMergedDiscoveryContainer(Minecraft.getMinecraft().thePlayer));
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_BLEND);
					RenderHelper.disableStandardItemLighting();
					if(aspects != null && aspects.size() > 0) {
						for(Aspect aspect : aspects) {
							String aspectText = aspect.type.getName() + " (" + ASPECT_AMOUNT_FORMAT.format(aspect.getAmount()) + ")";
							String aspectTypeText = aspect.type.getType();
							Minecraft.getMinecraft().fontRenderer.drawString(aspectText, 2 + 17, 2 + yOffset, 0xFFFFFFFF);
							Minecraft.getMinecraft().fontRenderer.drawString(aspectTypeText, 2 + 17, 2 + 9 + yOffset, 0xFFFFFFFF);
							AspectIconRenderer.renderIcon(2, 2 + yOffset, 16, 16, aspect.type.getIconIndex());
							int entryWidth = Math.max(Minecraft.getMinecraft().fontRenderer.getStringWidth(aspectText) + 19, Minecraft.getMinecraft().fontRenderer.getStringWidth(aspectTypeText) + 19);
							if(entryWidth > width) {
								width = entryWidth;
							}
							yOffset += 21;
						}
						GL11.glTranslated(0, 0, -10);
						Gui.drawRect(0, -40, width + 1, yOffset, 0x90000000);
						Gui.drawRect(1, -39, width, yOffset - 1, 0xAA000000);
					}
					RenderHelper.enableGUIStandardItemLighting();
					GL11.glPopMatrix();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glColor4f(1, 1, 1, 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
