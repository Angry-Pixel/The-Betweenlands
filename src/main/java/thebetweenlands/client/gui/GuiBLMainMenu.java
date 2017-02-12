package thebetweenlands.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.Starfield;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.config.ConfigHandler;

public class GuiBLMainMenu extends GuiMainMenu {
	public static final ResourceLocation LOGO_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/main/logo.png");
	public static final int LAYER_COUNT = 4;

	private List<List<GuiFirefly>> fireFlies;
	private ResourceLocation[] layerTextures = new ResourceLocation[GuiBLMainMenu.LAYER_COUNT];
	private int layerTick;

	private Random random = new Random();
	private Starfield starfieldEffect;
	private Framebuffer starfieldTextureFBO = null;

	@SubscribeEvent
	public static void onGuiOpened(GuiOpenEvent event) {
		if (ConfigHandler.blMainMenu && event.getGui() instanceof GuiMainMenu && !(event.getGui() instanceof GuiBLMainMenu)) {
			event.setGui(new GuiBLMainMenu());
		}
	}

	@Override
	public void initGui() {
		if(this.fireFlies == null) {
			this.fireFlies = new ArrayList<List<GuiFirefly>>();
			for(int i = 0; i < 3; i++) {
				this.fireFlies.add(new ArrayList<GuiFirefly>());
			}
		}

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(1, 5, this.height - 25, 80, 20, I18n.format("menu.singleplayer")));
		this.buttonList.add(new GuiButton(2, 90, this.height - 25, 80, 20, I18n.format("menu.multiplayer")));
		this.buttonList.add(new GuiButton(14, 175, this.height - 25, 80, 20, I18n.format("menu.online", new Object[0]).replace("Minecraft", "").trim()));

		this.buttonList.add(new GuiButton(6, this.width - 100, this.height - 25, 20, 20, "M"));
		this.buttonList.add(new GuiButton(5, this.width - 75, this.height - 25, 20, 20, "L"));
		this.buttonList.add(new GuiButton(0, this.width - 50, this.height - 25, 20, 20, "O"));
		this.buttonList.add(new GuiButton(4, this.width - 25, this.height - 25, 20, 20, "Q"));

		for (int i = 0; i < this.layerTextures.length; i++) {
			this.layerTextures[i] = new ResourceLocation(ModInfo.ID, "textures/gui/main/layer_" + i + ".png");
		}

		if (this.starfieldTextureFBO != null) {
			this.starfieldTextureFBO.deleteFramebuffer();
			this.starfieldTextureFBO = null;
		}

		if (this.starfieldEffect != null) {
			this.starfieldEffect.delete();
			this.starfieldEffect = null;
		}

		if (ShaderHelper.INSTANCE.canUseShaders()) {
			this.starfieldTextureFBO = new Framebuffer(this.width, this.height, false);
			this.starfieldEffect = (Starfield) new Starfield(false).init();
			this.starfieldEffect.setTimeScale(0.00000000005F).setZoom(4.8F);
		}
	}

	@Override
	public void onGuiClosed() {
		if (this.starfieldTextureFBO != null) {
			this.starfieldTextureFBO.deleteFramebuffer();
			this.starfieldTextureFBO = null;
		}

		if (this.starfieldEffect != null) {
			this.starfieldEffect.delete();
			this.starfieldEffect = null;
		}
	}

	@Override
	public void updateScreen() {
		this.layerTick++;

		ScaledResolution res = new ScaledResolution(this.mc);

		for(int i = 0; i < 3; i++) {
			List<GuiFirefly> layer = this.fireFlies.get(i);

			Iterator<GuiFirefly> it = layer.iterator();
			while(it.hasNext()) {
				GuiFirefly firefly = it.next();

				if (firefly.getPosY() >= res.getScaledHeight() + 40 ||
						firefly.getPosY() <= -40 ||
						firefly.getPosX() <= -40) {
					it.remove();
				} else {
					firefly.update();
				}
			}

			if (this.random.nextInt(32) == 0 && layer.size() < 5) {
				layer.add(new GuiFirefly(res.getScaledWidth() + 50, this.random.nextInt(this.height), -this.random.nextFloat() * 0.8F, (this.random.nextFloat() - this.random.nextFloat()) * 1.5F));
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, this.width, this.height, 0xFF001000);

		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		this.drawStarfield(partialTicks);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		for(int i = 0; i < this.layerTextures.length; i++) {
			if(i >= 1) {
				List<GuiFirefly> layer = this.fireFlies.get(i - 1);
				for (GuiFirefly firefly : layer) {
					firefly.drawFireFly(this.mc, partialTicks);
				}
			}

			ResourceLocation layerTexture = this.layerTextures[i];
			this.mc.getTextureManager().bindTexture(layerTexture);

			double u = (((this.layerTick + partialTicks) / (float) (this.layerTextures.length - i)) / (float) (i + 1) + 1024 * i / 4.0F) / 4000.0F;

			double visibleU = (this.width / (double)this.height) * (256.0D / (1024.0D * i));

			double x = 0;
			double y = 0;

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			buffer.pos(x + 0, y + this.height, this.zLevel).tex(u, 1).endVertex();
			buffer.pos(x + this.width, y + this.height, this.zLevel).tex(u + visibleU, 1).endVertex();
			buffer.pos(x + this.width, y + 0, this.zLevel).tex(u + visibleU, 0).endVertex();
			buffer.pos(x + 0, y + 0, this.zLevel).tex(u, 0).endVertex();
			tessellator.draw();
		}

		GlStateManager.disableTexture2D();
		drawRect(0, this.height - 30, this.width, this.height, 0x60000000);
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		this.mc.getTextureManager().bindTexture(GuiBLMainMenu.LOGO_TEXTURE);

		this.drawTexturedModalRect(this.width / 2 - 161 / 2, 20 + MathHelper.sin(((float) layerTick + partialTicks) / 16.0F) * 6.0F, 0, 0, 161, 79, 256, 256);
		this.drawTexturedModalRect(0, 0, 239, 0, 17, 16);

		for (GuiButton button : (List<GuiButton>) this.buttonList) {
			button.drawButton(this.mc, mouseX, mouseY);
		}
	}

	protected void drawStarfield(float partialTicks) {
		if (ShaderHelper.INSTANCE.canUseShaders() && this.starfieldEffect != null && this.starfieldTextureFBO != null) {
			this.starfieldEffect.setOffset((this.layerTick + partialTicks) / 8000.0F, 0, 0);
			int renderDimension = Math.max(this.width, this.height);
			this.starfieldEffect.create(this.starfieldTextureFBO)
			.setPreviousFramebuffer(Minecraft.getMinecraft().getFramebuffer())
			.setRenderDimensions(renderDimension, renderDimension)
			.render(partialTicks);

			GlStateManager.bindTexture(this.starfieldTextureFBO.framebufferTexture);

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

	protected void drawTexturedModalRect(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
		double uscale = 1.0F / textureWidth;
		double vscale = 1.0F / textureHeight;

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x + 0, y + height, this.zLevel).tex(u * uscale, (v + height) * vscale).endVertex();
		buffer.pos(x + width, y + height, this.zLevel).tex((u + width) * uscale, (v + height) * vscale).endVertex();
		buffer.pos(x + width, y + 0, this.zLevel).tex((u + width) * uscale, v * vscale).endVertex();
		buffer.pos(x + 0, y + 0, this.zLevel).tex(u * uscale, v * vscale).endVertex();
		tessellator.draw();
	}
}
