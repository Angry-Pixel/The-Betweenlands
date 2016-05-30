package thebetweenlands.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.effect.StarfieldEffect;
import thebetweenlands.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class GuiBLMainMenu extends GuiMainMenu {
	public static final ResourceLocation LOGO_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/logo.png");
	public static final int LAYER_COUNT = 4;

	private List<GuiFirefly> fireFlies = new ArrayList<>();
	private ResourceLocation[] layerTextures = new ResourceLocation[GuiBLMainMenu.LAYER_COUNT];
	private int layerTick;

	private Random random = new Random();
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

		if (ShaderHelper.INSTANCE.canUseShaders()) {
			if (this.starfieldTextureFBO != null) {
				this.starfieldTextureFBO.deleteFramebuffer();
			}
			this.starfieldTextureFBO = new Framebuffer(this.width, this.height, false);

			if (this.starfieldEffect == null) {
				this.starfieldEffect = (StarfieldEffect) new StarfieldEffect(false).init();
			}
		}
	}

	@Override
	public void updateScreen() {
		this.layerTick++;

		if (random.nextInt(32) == 0) {
			fireFlies.add(new GuiFirefly(this.width + 50, random.nextInt(this.height), random.nextFloat() - random.nextFloat(), random.nextFloat() - random.nextFloat()));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, this.width, this.height, 0xFF001000);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawStarfield();

		for(int i = 0; i < this.layerTextures.length; i++) {
			if(i == this.layerTextures.length - 1) {
				for (GuiFirefly firefly : this.fireFlies) {
					if (firefly.posY < this.height + 40) {
						firefly.drawFireFly(this.mc);
					}
				}
			}

			ResourceLocation layerTexture = this.layerTextures[i];
			this.mc.getTextureManager().bindTexture(layerTexture);

			double u = ((layerTick / (float) (this.layerTextures.length - i)) + partialTicks / (float) (i + 1) + 1024 * i / 4.0F) / 4000.0F;
			double v = 0.0F;

			double visibleU = (this.width / (double)this.height) * (256.0D / (1024.0D * i));

			double x = 0;
			double y = 0;

			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(x + 0, y + height, zLevel, u, 1);
			tessellator.addVertexWithUV(x + width, y + height, zLevel, u + visibleU, 1);
			tessellator.addVertexWithUV(x + width, y + 0, zLevel, u + visibleU, 0);
			tessellator.addVertexWithUV(x + 0, y + 0, zLevel, u, 0);
			tessellator.draw();
		}

		for (int i = 0; i < this.fireFlies.size(); i++) {
			if (this.fireFlies.get(i).posY >= this.height + 40) {
				this.fireFlies.remove(i);
			}
		}

		//int i = this.layerTextures.length - 1;
		//ResourceLocation layerTexture = this.layerTextures[i];
		//this.mc.getTextureManager().bindTexture(layerTexture);
		//drawTexturedModalRect(0, 0, (layerTick / (float) (this.layerTextures.length - i)) + partialTicks / (float) (i + 1) + 1024 * i / 4.0F, 0, this.width, this.height, 1024 / (this.layerTextures.length - i) * (this.height / 128.0F), this.height, this.zLevel);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		drawRect(0, this.height - 30, this.width, this.height, 0x60000000);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		this.mc.getTextureManager().bindTexture(GuiBLMainMenu.LOGO_TEXTURE);
		drawTexturedModalRect(this.width / 2 - 161 / 2, 20 + MathHelper.sin(((float) layerTick + partialTicks) / 16.0F) * 6.0F, 0, 0, 161, 79, 256, 256, zLevel);
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
			this.starfieldEffect.create(this.starfieldTextureFBO)
			.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
			.setRenderDimensions(renderDimension, renderDimension)
			.render();
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

	public void drawTexturedModalRect(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight, double zLevel) {
		double f = 1.0F / textureWidth;
		double f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + height, zLevel, (u) * f, (v + height) * f1);
		tessellator.addVertexWithUV(x + width, y + height, zLevel, (u + width) * f, (v + height) * f1);
		tessellator.addVertexWithUV(x + width, y + 0, zLevel, (u + width) * f, v * f1);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, u * f, v * f1);
		tessellator.draw();
	}
}
