package thebetweenlands.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.effect.StarfieldEffect;
import thebetweenlands.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiBLMainMenu extends GuiMainMenu {
    public static final ResourceLocation LOGO_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/logo.png");
    public static final int LAYER_COUNT = 2;

    private List<GuiFirefly> fireFlies = new ArrayList<>();
    private ResourceLocation[] layerTextures = new ResourceLocation[GuiBLMainMenu.LAYER_COUNT];
    private int layerTick;

    private StarfieldEffect starfieldEffect;
    private Framebuffer starfieldTextureFBO = null;

    @Override
    public void initGui() {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(1, 5, this.height - 25, 80, 20, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, 90, this.height - 25, 80, 20, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(14, 175, this.height - 25, 80, 20, "Realms"));

        this.buttonList.add(new GuiButton(6, this.width - 100, this.height - 25, 20, 20, "M"));
        this.buttonList.add(new GuiButton(5, this.width - 75, this.height - 25, 20, 20, "L"));
        this.buttonList.add(new GuiButton(0, this.width - 50, this.height - 25, 20, 20, "O"));
        this.buttonList.add(new GuiButton(4, this.width - 25, this.height - 25, 20, 20, "Q"));

        for (int i = 0; i < this.layerTextures.length; i++) {
            this.layerTextures[i] = new ResourceLocation(ModInfo.ID, "textures/gui/main/layer" + i + ".png");
        }

        if (this.starfieldTextureFBO != null) {
            this.starfieldTextureFBO.deleteFramebuffer();
        }
        this.starfieldTextureFBO = new Framebuffer(this.width, this.height, false);

        if (this.starfieldEffect == null) {
            this.starfieldEffect = (StarfieldEffect) new StarfieldEffect(false).init();
        }
    }

    @Override
    public void updateScreen() {
        this.layerTick++;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, this.width, this.height, 0xFF001000);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        this.drawStarfield();

        for (int i = 0; i < this.layerTextures.length; i++) {
            ResourceLocation layerTexture = this.layerTextures[i];
            this.mc.getTextureManager().bindTexture(layerTexture);
            drawTexturedModalRect(0, 0, (layerTick / (float) (this.layerTextures.length - i)) + partialTicks / (float) (i + 1) + 1024 * i / 4.0F, 0, this.width, this.height, 1024 / (this.layerTextures.length - i) * (this.height / 128.0F), this.height);
        }

        Random rand = new Random();
        if (rand.nextInt(128) == 0) {
            fireFlies.add(new GuiFirefly(rand.nextInt(this.width), -50, (rand.nextFloat() - rand.nextFloat()), rand.nextFloat() - rand.nextFloat()));
        }

        for (int i = 0; i < fireFlies.size(); i++) {
            if (fireFlies.get(i).posY < this.height + 40) {
                fireFlies.get(i).drawFireFly(this.mc, this);
            } else {
                fireFlies.remove(i);
            }
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawRect(0, this.height - 30, this.width, this.height, 0x60000000);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        this.mc.getTextureManager().bindTexture(GuiBLMainMenu.LOGO_TEXTURE);
        drawTexturedModalRect(this.width / 2 - 161 / 2, 10, 0, 0, 161, 79);
        drawTexturedModalRect(0, 0, 239, 0, 17, 16);

        for (GuiButton button : (List<GuiButton>) this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY);
        }
    }

    protected void drawStarfield() {
        if (ShaderHelper.INSTANCE.canUseShaders() && this.starfieldEffect != null && this.starfieldTextureFBO != null) {
            this.starfieldEffect.setTimeScale(0.0000005F).setZoom(4.8F);
            this.starfieldEffect.setOffset(this.layerTick / 8000.0F, 0, 0);
            int renderDimension = Math.max(this.width, this.height);
            this.starfieldEffect.apply(-1, this.starfieldTextureFBO, null, Minecraft.getMinecraft().getFramebuffer(), renderDimension, renderDimension);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.starfieldTextureFBO.framebufferTexture);

            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(0, 0);
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex2d(0, this.height);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(this.width, this.height);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex2d(this.width, this.height);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex2d(this.width, 0);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex2d(0, 0);
            GL11.glEnd();
        }
    }

    public void drawTexturedModalRect(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        double f = 1.0F / textureWidth;
        double f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, this.zLevel, (u) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + height, this.zLevel, (u + width) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + 0, this.zLevel, (u + width) * f, v * f1);
        tessellator.addVertexWithUV(x + 0, y + 0, this.zLevel, u * f, v * f1);
        tessellator.draw();
    }
}
