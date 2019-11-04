package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.model.tile.ModelDruidAltar;
import thebetweenlands.client.render.model.tile.ModelStone;
import thebetweenlands.common.block.container.BlockDruidAltar;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.StatePropertyHelper;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderDruidAltar extends TileEntitySpecialRenderer<TileEntityDruidAltar> {
	public static RenderDruidAltar instance;
	private final ModelDruidAltar model = new ModelDruidAltar();
	private final ModelStone stone = new ModelStone();
	private final ResourceLocation ACTIVE = new ResourceLocation("thebetweenlands:textures/tiles/druid_altar_active.png");
	private final ResourceLocation ACTIVEGLOW = new ResourceLocation("thebetweenlands:textures/tiles/druid_altar_active_glow.png");
	private final ResourceLocation NORMAL = new ResourceLocation("thebetweenlands:textures/tiles/druid_altar.png");
	private final ResourceLocation NORMALGLOW = new ResourceLocation("thebetweenlands:textures/tiles/druid_altar_glow.png");

	public RenderDruidAltar() {
	}

	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher renderer) {
		super.setRendererDispatcher(renderer);
		instance = this;
	}

	public void renderTileAsItem(double x, double y, double z) {
		bindTexture(NORMAL);
		
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		GlStateManager.pushMatrix();
		renderMainModel(x, y, z);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		renderStones(x, y, z, 0);
		GlStateManager.popMatrix();

		LightingUtil.INSTANCE.setLighting(255);

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		GlStateManager.depthMask(true);
		bindTexture(NORMALGLOW);

		GlStateManager.pushMatrix();
		renderMainModel(x, y, z);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		renderStones(x, y, z, 0);
		GlStateManager.popMatrix();

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();

		LightingUtil.INSTANCE.revert();
	}

	private void renderTile(TileEntityDruidAltar tile, double x, double y, double z, float partialTicks) {
		//Render main model
		if (tile.getBlockMetadata() == 1) {
			bindTexture(ACTIVE);
		} else {
			bindTexture(NORMAL);
		}
		GlStateManager.pushMatrix();
		renderMainModel(x, y, z);
		GlStateManager.popMatrix();

		//Update rotation
		float renderRotation = tile.rotation + (tile.rotation - tile.prevRotation) * partialTicks;

		//Render floating stones
		GlStateManager.pushMatrix();
		renderStones(x, y, z, renderRotation);
		GlStateManager.popMatrix();

		//Full brightness for items
		LightingUtil.INSTANCE.setLighting(255);

		//Animate the 4 talisman pieces
		double yOff = 1.2D;
		if (StatePropertyHelper.getStatePropertySafely(tile, BlockDruidAltar.class, BlockDruidAltar.ACTIVE, false) && tile.craftingProgress != 0) {
			yOff = Math.min(tile.renderYOffset + (tile.renderYOffset - tile.prevRenderYOffset) * partialTicks, TileEntityDruidAltar.FINAL_HEIGHT + 1.0D);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 3.1D, z + 0.5D);
			GlStateManager.rotate(renderRotation * 2.0f, 0f, 1f, 0f);
			double shineScale = 0.04f * Math.pow(1.0D - (TileEntityDruidAltar.FINAL_HEIGHT + 1.0D - yOff) / TileEntityDruidAltar.FINAL_HEIGHT, 12);
			GlStateManager.scale(shineScale, shineScale, shineScale);
			this.renderShine((float) Math.sin(Math.toRadians(renderRotation)) / 2.0f - 0.2f, (int) (80 * Math.pow(1.0D - (TileEntityDruidAltar.FINAL_HEIGHT + 1.0D - yOff) / TileEntityDruidAltar.FINAL_HEIGHT, 12)));
			GlStateManager.popMatrix();
		}
		for (int xi = 0; xi < 2; xi++) {
			for (int zi = 0; zi < 2; zi++) {
				ItemStack item = tile.getStackInSlot(zi * 2 + xi + 1);
				if (item.isEmpty()) {
					continue;
				}
				float xOff = xi == 0 ? -0.18f : 1.18f;
				float zOff = zi == 0 ? -0.18f : 1.18f;
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + xOff, y + 1, z + zOff);
				this.renderCone(5);
				GlStateManager.popMatrix();
				Vector3d midVec = new Vector3d();
				midVec.x = (float) x + 0.5f;
				midVec.z = (float) z + 0.5f;
				Vector3d diffVec = new Vector3d();
				diffVec.x = (float) x + xOff - midVec.x;
				diffVec.z = (float) z + zOff - midVec.z;
				double rProgress = 1.0D - Math.pow(1.0D - (TileEntityDruidAltar.FINAL_HEIGHT + 1.0D - yOff) / TileEntityDruidAltar.FINAL_HEIGHT, 6);
				diffVec.x *= rProgress;
				diffVec.z *= rProgress;
				midVec.x += diffVec.x;
				midVec.z += diffVec.z;
				GlStateManager.pushMatrix();
				GlStateManager.translate(midVec.x, y + yOff, midVec.z);
				GlStateManager.scale(0.3f, 0.3f, 0.3f);
				GlStateManager.rotate(-renderRotation * 2.0f, 0, 1, 0);
				renderItem(item);
				GlStateManager.popMatrix();
			}
		}

		//Render swamp talisman
		ItemStack itemTalisman = tile.getStackInSlot(0);
		if (!itemTalisman.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 3.1D, z + 0.5D);
			GlStateManager.rotate(renderRotation * 2.0f, 0, 1, 0);
			double shineScale = 0.04f;
			GlStateManager.scale(shineScale, shineScale, shineScale);
			this.renderShine((float) Math.sin(Math.toRadians(renderRotation)) / 2.0f - 0.2f, 80);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 3.1D, z + 0.5D);
			GlStateManager.scale(0.3f, 0.3f, 0.3f);
			GlStateManager.rotate(-renderRotation * 2.0f, 0, 1, 0);
			renderItem(itemTalisman);
			GlStateManager.popMatrix();
		}

		//Revert to prev lighting
		LightingUtil.INSTANCE.revert();

		//Render glow overlay
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		GlStateManager.depthMask(true);

		float lighting = 150f;
		if (tile.getBlockMetadata() == 1) {
			lighting = (float) Math.sin(Math.toRadians(renderRotation) * 4.0f) * 105.0f + 150.0f;
		}
		LightingUtil.INSTANCE.setLighting((int) lighting);

		if (tile.getBlockMetadata() == 1) {
			bindTexture(ACTIVEGLOW);
		} else {
			bindTexture(NORMALGLOW);
		}

		GlStateManager.pushMatrix();
		renderMainModel(x, y, z);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		renderStones(x, y, z, renderRotation);
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.popMatrix();

		LightingUtil.INSTANCE.revert();
	}

	private void renderMainModel(double x, double y, double z) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		model.renderAll(0.0625F);
		GlStateManager.popMatrix();
	}

	private void renderStones(double x, double y, double z, float rotation) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
		stone.renderAll();
		GlStateManager.popMatrix();
	}

	private void renderShine(float rotation, int iterations) {
		Random random = new Random(432L);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();
		float f1 = rotation;
		float f2 = 0.0f;
		if (f1 > 0.8F) {
			f2 = (f1 - 0.8F) / 0.2F;
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		for (int i = 0; (float) i < iterations; ++i) {
			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			float pos1 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float pos2 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (255.0F * (1.0F - f2))).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(0.0D, (double) pos1, (double) (1.0F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			tessellator.draw();
		}
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
	}

	private void renderCone(int faces) {
		GlStateManager.pushMatrix();
		float step = 360.0f / (float) faces;

		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);

		for (float i = 0; i < 360.0f; i += step) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			double lr = 0.1D;
			double ur = 0.3D;
			double height = 0.2D;
			double sin = Math.sin(Math.toRadians(i));
			double cos = Math.cos(Math.toRadians(i));
			double sin2 = Math.sin(Math.toRadians(i + step));
			double cos2 = Math.cos(Math.toRadians(i + step));

			buffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(sin * lr, 0, cos * lr).color(255, 255, 255, 0).endVertex();
			buffer.pos(sin2 * lr, 0, cos2 * lr).color(255, 255, 255, 0).endVertex();

			buffer.pos(sin2 * ur, height, cos2 * ur).color(0, 0, 255, 60).endVertex();
			buffer.pos(sin * ur, height, cos * ur).color(0, 0, 255, 60).endVertex();

			buffer.pos(sin * ur, height, cos * ur).color(0, 0, 255, 60).endVertex();
			buffer.pos(sin2 * ur, height, cos2 * ur).color(0, 0, 255, 60).endVertex();

			buffer.pos(sin2 * lr, 0, cos2 * lr).color(255, 255, 255, 0).endVertex();
			buffer.pos(sin * lr, 0, cos * lr).color(255, 255, 255, 0).endVertex();
			tessellator.draw();
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}


	@Override
	public void render(TileEntityDruidAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(te == null || !te.hasWorld()) {
			renderTileAsItem(x, y, z);
			return;
		}
		renderTile(te, x, y, z, partialTicks);
	}


	private void renderItem(ItemStack stack){
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.FIXED);
	}
}
