package thebetweenlands.client.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import thebetweenlands.common.herblore.aspect.Aspect;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.DiscoveryContainer;
import thebetweenlands.util.AspectIconRenderer;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;

public class AspectItemOverlayHandler {
    public static final AspectItemOverlayHandler INSTANCE = new AspectItemOverlayHandler();

    public static final DecimalFormat ASPECT_AMOUNT_FORMAT = new DecimalFormat("#.##");

    private Field f_theSlot = ReflectionHelper.findField(GuiContainer.class, "theSlot", "field_147006_u", "u");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Minecraft.getMinecraft().currentScreen instanceof GuiContainer && Minecraft.getMinecraft().thePlayer != null) {
            try {
                Slot slot = (Slot) f_theSlot.get((GuiContainer) Minecraft.getMinecraft().currentScreen);
                if(slot != null && slot.getHasStack()) {
                    ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
                    double mouseX = (Mouse.getX() * resolution.getScaledWidth_double()) / Minecraft.getMinecraft().displayWidth;
                    double mouseY = resolution.getScaledHeight_double() - (Mouse.getY() * resolution.getScaledHeight_double()) / Minecraft.getMinecraft().displayHeight - 1;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(mouseX + 8, mouseY, 200);
                    int yOffset = -40;
                    int width = 0;
                    List<Aspect> aspects = AspectManager.get(Minecraft.getMinecraft().theWorld).getDiscoveredAspects(AspectManager.getAspectItem(slot.getStack()), DiscoveryContainer.getMergedDiscoveryContainer(Minecraft.getMinecraft().thePlayer));
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableBlend();
                    RenderHelper.disableStandardItemLighting();
                    if(aspects != null && aspects.size() > 0) {
                        for(Aspect aspect : aspects) {
                            String aspectText = aspect.type.getName() + " (" + ASPECT_AMOUNT_FORMAT.format(aspect.getDisplayAmount()) + ")";
                            String aspectTypeText = aspect.type.getType();
                            Minecraft.getMinecraft().fontRendererObj.drawString(aspectText, 2 + 17, 2 + yOffset, 0xFFFFFFFF);
                            Minecraft.getMinecraft().fontRendererObj.drawString(aspectTypeText, 2 + 17, 2 + 9 + yOffset, 0xFFFFFFFF);
                            AspectIconRenderer.renderIcon(2, 2 + yOffset, 16, 16, aspect.type.getIcon());
                            int entryWidth = Math.max(Minecraft.getMinecraft().fontRendererObj.getStringWidth(aspectText) + 19, Minecraft.getMinecraft().fontRendererObj.getStringWidth(aspectTypeText) + 19);
                            if(entryWidth > width) {
                                width = entryWidth;
                            }
                            yOffset += 21;
                        }
                        GlStateManager.translate(0, 0, -10);
                        Gui.drawRect(0, -40, width + 1, yOffset, 0x90000000);
                        Gui.drawRect(1, -39, width, yOffset - 1, 0xAA000000);
                    }
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.popMatrix();
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableBlend();
                    GlStateManager.color(1, 1, 1, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
