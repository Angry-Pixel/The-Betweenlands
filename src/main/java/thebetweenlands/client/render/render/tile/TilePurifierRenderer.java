package thebetweenlands.client.render.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.models.tile.ModelPurifier;
import thebetweenlands.common.block.container.BlockPurifier;
import thebetweenlands.common.tileentity.TileEntityPurifier;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class TilePurifierRenderer extends TileEntitySpecialRenderer<TileEntityPurifier> {
    public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/purifier.png");
    private final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    private final ModelPurifier model = new ModelPurifier();

    @Override
    public void renderTileEntityAt(TileEntityPurifier tile, double x, double y, double z, float partialTickTime, int destroyStage) {
        IBlockState blockState = tile.getWorld().getBlockState(tile.getPos());
        bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.scale(1F, -1F, -1F);
        int rotation = blockState.getValue(BlockPurifier.FACING).getHorizontalIndex() * 90;
        GlStateManager.rotate(rotation - 180, 0, 1, 0);
        model.renderAll();
        if (tile.isPurifying() && tile.lightOn)
            model.renderFirePlate();
        GlStateManager.popMatrix();
        int amount = tile.waterTank.getFluidAmount();
        int capacity = tile.waterTank.getCapacity();
        float size = 1F / capacity * amount;

		/*if (purifier.getStackInSlot(2) != null) {
            GlStateManager.pushMatrix();
			GL11.glTranslated(x + 0.25D, y + 0.45D + size * 0.125F, z + 0.5D);
			GlStateManager.scale(0.5D, 0.5D, 0.5D);
			ItemRenderHelper.renderItemIn3D(purifier.getStackInSlot(2));
			GlStateManager.popMatrix();
		}*/

        if (tile.getStackInSlot(2) != null) {
            GlStateManager.pushMatrix();
            GL11.glTranslated(x + 0.5D, y + 0.27D, z + 0.5D);
            GlStateManager.rotate(180, 1, 0, 0);
            int items = tile.getStackInSlot(2).stackSize;
            Random rand = new Random(tile.getPos().toLong());
            for (int i = 0; i < items; i++) {
                GlStateManager.pushMatrix();
                GL11.glTranslated(rand.nextFloat() / 3.0D - 1.0D / 6.0D, -0.25D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
                GlStateManager.rotate(rand.nextFloat() * 30.0F - 15.0F, 1, 0, 0);
                GlStateManager.rotate(rand.nextFloat() * 30.0F - 15.0F, 0, 0, 1);
                GlStateManager.scale(0.15F, 0.15F, 0.15F);
                GlStateManager.rotate(90, 1, 0, 0);
                GlStateManager.rotate(rand.nextFloat() * 360.0F, 0, 0, 1);
                ItemStack stack = tile.getStackInSlot(2);
                renderItem.renderItem(stack, renderItem.getItemModelMesher().getItemModel(stack));
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
        }

//        if (amount >= 100) {
//            Tessellator tesselator = Tessellator.getInstance();
//            VertexBuffer buffer = tesselator.getBuffer();
//            TextureAtlasSprite waterIcon = ((BlockSwampWater) BlockRegistry.swampWater).getWaterIcon(1);
//            GlStateManager.pushMatrix();
//            GlStateManager.enableBlend();
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//            float translationX = (float) x + 0.0F;
//            float translationY = (float) (y + 0.35F + size * 0.5F);
//            float translationZ = (float) z + 0.0F;
//            buffer.setTranslation(translationX, translationY, translationZ);
//            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//            buffer.pos(0.1F, 0.0F, 0.1F).tex(waterIcon.getMinU(), waterIcon.getMinV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
//            buffer.pos(0.1F, 0.0F, 0.9F).tex(waterIcon.getMinU(), waterIcon.getMaxV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
//            buffer.pos(0.9F, 0.0F, 0.9F).tex(waterIcon.getMaxU(), waterIcon.getMaxV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
//            buffer.pos(0.9F, 0.0F, 0.1F).tex(waterIcon.getMaxU(), waterIcon.getMinV()).color(0.2F, 0.6F, 0.4F, 1.0F).endVertex();
//            tesselator.draw();
//            buffer.setTranslation(0.0F, 0.0F, 0.0F);
//            GL11.glDisable(GL11.GL_BLEND);
//            GlStateManager.popMatrix();
//        }
    }
}

