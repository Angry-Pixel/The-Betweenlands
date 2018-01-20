package thebetweenlands.client.gui.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.gui.GuiFirefly;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.Starfield;

public class GuiMainMenuBackground extends GuiScreen {
	private List<List<GuiFirefly>> fireFlies;
	private ResourceLocation[] layerTextures;
	private int layerTick;

	private Random random = new Random();
	private Starfield starfieldEffect;
	private Framebuffer starfieldTextureFBO = null;

	public GuiMainMenuBackground(ResourceLocation texture, int layers) {
		this.layerTextures = new ResourceLocation[layers];
		for(int i = 0; i < layers; i++) {
			this.layerTextures[i] = new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath() + "_" + i + ".png");
		}
	}

	@Override
	public void initGui() {
		if(this.fireFlies == null) {
			this.fireFlies = new ArrayList<>();
			for(int i = 0; i < 3; i++) {
				this.fireFlies.add(new ArrayList<>());
			}
		}

		this.delete();

		if (ShaderHelper.INSTANCE.canUseShaders()) {
			this.starfieldTextureFBO = new Framebuffer(this.width, this.height, false);
			this.starfieldEffect = new Starfield(false).init();
			this.starfieldEffect.setTimeScale(0.00000000005F).setZoom(4.8F);
		}
	}

	@Override
	public void onGuiClosed() {
		this.delete();
	}

	@Override
	public void updateScreen() {
		this.layerTick++;

		for(int i = 0; i < 3; i++) {
			List<GuiFirefly> layer = this.fireFlies.get(i);

			Iterator<GuiFirefly> it = layer.iterator();
			while(it.hasNext()) {
				GuiFirefly firefly = it.next();

				if (firefly.getPosY() >= this.width + 40 ||
						firefly.getPosY() <= -40 ||
						firefly.getPosX() <= -40) {
					it.remove();
				} else {
					firefly.update();
				}
			}

			if (this.random.nextInt(32) == 0 && layer.size() < 5) {
				layer.add(new GuiFirefly(this.width + 50, this.random.nextInt(this.height), -this.random.nextFloat() * 0.8F, (this.random.nextFloat() - this.random.nextFloat()) * 1.5F));
			}
		}
	}

	public void delete() {
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, this.width, this.height, 0xFF001000);

		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		this.drawStarfield(partialTicks);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

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

}
