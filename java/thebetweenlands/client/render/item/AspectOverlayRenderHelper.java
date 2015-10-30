package thebetweenlands.client.render.item;

import java.lang.reflect.Method;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.herblore.aspects.ItemAspect;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntry;
import net.minecraftforge.client.MinecraftForgeClient;

public class AspectOverlayRenderHelper {
	public static RenderItem renderItem = new RenderItem();
	private static Method f_isMouseOverSlot = ReflectionHelper.findMethod(GuiContainer.class, null, new String[]{"isMouseOverSlot", "func_146981_a", "a"}, Slot.class, int.class, int.class);
	private static boolean ignoreHook = false;

	public static boolean shouldIgnoreHook() {
		return ignoreHook;
	}

	public static void renderOverlay(ItemRenderType type, ItemStack itemStack) {
		if(ignoreHook || type != ItemRenderType.INVENTORY) return;

		IItemRenderer itemRenderer = MinecraftForgeClient.getItemRenderer(itemStack, ItemRenderType.INVENTORY);
		boolean restoreMatrix = false;
		if(itemRenderer == null || itemRenderer.getClass() == ItemAspectOverlayRenderer.class) {
			ignoreHook = true;
			GL11.glEnable(GL11.GL_LIGHTING);
			renderItem.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemStack, 0, 0);
			GL11.glDisable(GL11.GL_LIGHTING);
			ignoreHook = false;
		}

		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

		if(itemStack != null) {
			if(Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
				GuiContainer guiContainer = (GuiContainer)Minecraft.getMinecraft().currentScreen;
				if(guiContainer.isShiftKeyDown()) {
					Container container = guiContainer.inventorySlots;
					for(Slot slot : (List<Slot>)container.inventorySlots) {
						if(slot.getStack() != null && slot.getStack().equals(itemStack)) {
							try {
								if((boolean) f_isMouseOverSlot.invoke(guiContainer, slot, 
										(int)(Mouse.getX() / (double)Minecraft.getMinecraft().displayWidth * scale.getScaledWidth()),
										(int)((Minecraft.getMinecraft().displayHeight-Mouse.getY()) / (double)Minecraft.getMinecraft().displayHeight * scale.getScaledHeight()))) {
									GL11.glPushMatrix();
									GL11.glTranslated(0, 0, 200);
									int yOffset = 0;
									int width = 0;
									List<ItemAspect> aspects = AspectRecipes.REGISTRY.getItemAspects(new ItemEntry(itemStack));
									GL11.glEnable(GL11.GL_TEXTURE_2D);
									GL11.glEnable(GL11.GL_BLEND);
									if(aspects != null && aspects.size() > 0) {
										for(ItemAspect aspect : aspects) {
											String aspectText = aspect.aspect.getName() + " (" + aspect.amount + ")";
											Minecraft.getMinecraft().fontRenderer.drawString(aspectText, 3, 18 + yOffset, 0xFFFFFFFF);
											int strWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(aspectText);
											if(strWidth > width) {
												width = strWidth;
											}
											yOffset += 10;
										}
									} else {
										String text = "No aspects";
										Minecraft.getMinecraft().fontRenderer.drawString(text, 3, 18, 0xFFFFFFFF);
										width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
										yOffset = 10;
									}
									GL11.glTranslated(0, 0, -1);
									Gui.drawRect(2, 17, 3 + width, 17 + yOffset, 0x95000000);
									GL11.glPopMatrix();
								}
								GL11.glEnable(GL11.GL_TEXTURE_2D);
								GL11.glEnable(GL11.GL_BLEND);
								GL11.glColor4f(1, 1, 1, 1);
								return;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}
