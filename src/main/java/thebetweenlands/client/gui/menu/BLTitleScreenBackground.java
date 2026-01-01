package thebetweenlands.client.gui.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.shader.ShaderHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// NOTE: menu starfieldEffect has been moved to ShaderHelper
public class BLTitleScreenBackground {

	@Nullable
	private List<List<Firefly>> fireFlies;
	private final ResourceLocation[] layerTextures;
	private int layerTick;
	public int width;
	public int height;

	private final RandomSource random = RandomSource.create();

	public BLTitleScreenBackground(ResourceLocation texture, int layers) {
		this.layerTextures = new ResourceLocation[layers];
		for (int i = 0; i < layers; i++) {
			this.layerTextures[i] = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), texture.getPath() + "_" + i + ".png");
		}
	}

	public void init() {
		if (this.fireFlies == null) {
			this.fireFlies = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				this.fireFlies.add(new ArrayList<>());
			}
		}
	}

	public void onClose() {

	}

	public void tick() {
		this.layerTick++;

		//happens if screen wasnt initialized: if it wasnt dont try to tick anything
		if (this.fireFlies != null) {
			for (int i = 0; i < 3; i++) {
				List<Firefly> layer = this.fireFlies.get(i);

				Iterator<Firefly> it = layer.iterator();
				while (it.hasNext()) {
					Firefly firefly = it.next();

					if (firefly.getPosY() >= this.width + 40 ||
						firefly.getPosY() <= -40 ||
						firefly.getPosX() <= -40) {
						it.remove();
					} else {
						firefly.tick();
					}
				}

				if (this.random.nextInt(32) == 0 && layer.size() < 5) {
					layer.add(new Firefly(this.width + 50, this.random.nextInt(this.height), -this.random.nextFloat() * 0.8F, (this.random.nextFloat() - this.random.nextFloat()) * 1.5F));
				}
			}
		}
	}

	public void delete() {
		if (ShaderHelper.INSTANCE.menuStarfieldEffect != null) {
			ShaderHelper.INSTANCE.menuStarfieldEffect.close();
			ShaderHelper.INSTANCE.menuStarfieldEffect = null;
		}
	}

	public void render(GuiGraphics graphics, float partialTicks, float alpha) {
		graphics.fill(0, 0, this.width, this.height, 0xFF001000);

		graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		this.drawStarfield(graphics, partialTicks);

		for (int i = 0; i < this.layerTextures.length; i++) {
			if (i >= 1 && this.fireFlies != null) {
				List<Firefly> layer = this.fireFlies.get(i - 1);
				for (Firefly firefly : layer) {
					firefly.render(graphics, partialTicks);
				}
			}


			ResourceLocation layerTexture = this.layerTextures[i];
			float u = (((this.layerTick + partialTicks) / (float) (this.layerTextures.length - i)) / (float) (i + 1) + 1024.0F * i / 4.0F) / 4000.0F;
			float visibleU = ((float) this.width / this.height) * (256.0F / (1024.0F * i));
			RenderSystem.setShaderTexture(0, layerTexture);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			Matrix4f matrix4f = graphics.pose().last().pose();
			BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			builder.addVertex(matrix4f, 0.0F, this.height, 0.0F).setUv(u, 1.0F);
			builder.addVertex(matrix4f, this.width, this.height, 0.0F).setUv(u + visibleU, 1.0F);
			builder.addVertex(matrix4f, this.width, 0.0F, 0.0F).setUv(u + visibleU, 0.0F);
			builder.addVertex(matrix4f, 0.0F, 0.0F, 0.0F).setUv(u, 0.0F);
			BufferUploader.drawWithShader(builder.buildOrThrow());
		}

		graphics.fill(0, this.height - 30, this.width, this.height, 0x60000000);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	protected void drawStarfield(GuiGraphics graphics, float partialTicks) {
		if (ShaderHelper.INSTANCE.menuStarfieldEffect != null) {
			ShaderHelper.INSTANCE.menuStarfieldEffect.setOffset((this.layerTick + partialTicks) / 8000.0F, 0, 0);
			ShaderHelper.INSTANCE.menuStarfieldEffect.uploadUniforms(partialTicks);
			ShaderHelper.INSTANCE.menuStarfieldEffect.process(partialTicks);
			Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

			int renderDimension = Math.max(this.width, this.height);
			RenderSystem.setShaderTexture(0, ShaderHelper.INSTANCE.menuStarfieldEffect.starfieldTexture.getColorTextureId());
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.depthMask(false);
			graphics.pose().pushPose();
			Matrix4f matrix4f = graphics.pose().last().pose();
			graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			builder.addVertex(matrix4f, 0, renderDimension, 0).setUv(0, 0);
			builder.addVertex(matrix4f, renderDimension, renderDimension, 0).setUv(1, 0);
			builder.addVertex(matrix4f, renderDimension, 0, 0).setUv(1, 1);
			builder.addVertex(matrix4f, 0, 0, 0).setUv(0, 1);
			GameRenderer.getPositionTexShader().setDefaultUniforms(VertexFormat.Mode.QUADS, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), Minecraft.getInstance().getWindow());
			GameRenderer.getPositionTexShader().apply();
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			BufferUploader.draw(builder.buildOrThrow());
			GameRenderer.getPositionTexShader().clear();
			graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			graphics.pose().popPose();
		}
	}
}
