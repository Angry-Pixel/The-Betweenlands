package thebetweenlands.client.render.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelPurifier;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.tile.TileEntityPurifier;

@SideOnly(Side.CLIENT)
public class RenderPurifier extends TileEntitySpecialRenderer<TileEntityPurifier> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/purifier.png");
	public static final ModelPurifier MODEL = new ModelPurifier();

	private final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

	@Override
	public void renderTileEntityAt(TileEntityPurifier tile, double x, double y, double z, float partialTickTime, int destroyStage) {
		this.bindTexture(TEXTURE);

		if (tile == null || !tile.hasWorldObj()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			MODEL.renderAll();
			GlStateManager.popMatrix();
			return;
		}

		IBlockState blockState = tile.getWorld().getBlockState(tile.getPos());
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		int rotation = blockState.getValue(BlockPurifier.FACING).getHorizontalIndex() * 90;
		GlStateManager.rotate(rotation - 180, 0, 1, 0);
		MODEL.renderAll();
		if (tile.isPurifying() && tile.lightOn)
			MODEL.renderFirePlate();
		GlStateManager.popMatrix();
		int amount = tile.waterTank.getFluidAmount();
		int capacity = tile.waterTank.getCapacity();
		float size = 1F / capacity * amount;


		if (tile.getStackInSlot(2) != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 0.27D, z + 0.5D);
			GlStateManager.rotate(180, 1, 0, 0);
			int items = tile.getStackInSlot(2).stackSize;
			Random rand = new Random(tile.getPos().toLong());
			for (int i = 0; i < items; i++) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(rand.nextFloat() / 3.0D - 1.0D / 6.0D, -0.25D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
				GlStateManager.rotate(rand.nextFloat() * 30.0F - 15.0F, 1, 0, 0);
				GlStateManager.rotate(rand.nextFloat() * 30.0F - 15.0F, 0, 0, 1);
				GlStateManager.scale(0.15F, 0.15F, 0.15F);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate(rand.nextFloat() * 360.0F, 0, 0, 1);
				ItemStack stack = tile.getStackInSlot(2);
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				texture.setBlurMipmap(false, false);
				renderItem.renderItem(stack, renderItem.getItemModelMesher().getItemModel(stack));
				texture.restoreLastBlurMipmap();
				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
		}

		if (amount >= 100) {
			Tessellator tesselator = Tessellator.getInstance();
			VertexBuffer buffer = tesselator.getBuffer();
			TextureAtlasSprite waterIcon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thebetweenlands:fluids/swamp_water_still");
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			float translationX = (float) x + 0.0F;
			float translationY = (float) (y + 0.35F + size * 0.5F);
			float translationZ = (float) z + 0.0F;
			buffer.setTranslation(translationX, translationY, translationZ);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			buffer.pos(0.1F, 0.0F, 0.1F).tex(waterIcon.getMinU(), waterIcon.getMinV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
			buffer.pos(0.1F, 0.0F, 0.9F).tex(waterIcon.getMinU(), waterIcon.getMaxV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
			buffer.pos(0.9F, 0.0F, 0.9F).tex(waterIcon.getMaxU(), waterIcon.getMaxV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
			buffer.pos(0.9F, 0.0F, 0.1F).tex(waterIcon.getMaxU(), waterIcon.getMinV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
			tesselator.draw();
			buffer.setTranslation(0.0F, 0.0F, 0.0F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}

