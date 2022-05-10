package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelMothHouse;
import thebetweenlands.common.block.container.BlockMothHouse;
import thebetweenlands.common.tile.TileEntityMothHouse;
import thebetweenlands.util.StatePropertyHelper;

public class RenderMothHouse extends TileEntitySpecialRenderer<TileEntityMothHouse> {
    public static final ModelMothHouse MODEL = new ModelMothHouse();

    public static ResourceLocation TEXTURE_STAGE_0 = new ResourceLocation("thebetweenlands:textures/tiles/mothhouse_0.png");
    public static ResourceLocation TEXTURE_STAGE_1 = new ResourceLocation("thebetweenlands:textures/tiles/mothhouse_1.png");
    public static ResourceLocation TEXTURE_STAGE_2 = new ResourceLocation("thebetweenlands:textures/tiles/mothhouse_2.png");
    public static ResourceLocation TEXTURE_STAGE_3 = new ResourceLocation("thebetweenlands:textures/tiles/mothhouse_3.png");

    @Override
    public void render(TileEntityMothHouse te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        EnumFacing rotation = StatePropertyHelper.getStatePropertySafely(te, BlockMothHouse.class, BlockMothHouse.FACING, EnumFacing.NORTH);

        int silkCount = 0;

        if(te != null) {
            silkCount = te.getSilkCount();
        }

        silkCount = Math.max(0, Math.min(silkCount, 3));

        switch(silkCount) {
            case 0:
                bindTexture(TEXTURE_STAGE_0);
                break;

            case 1:
                bindTexture(TEXTURE_STAGE_1);
                break;

            case 2:
                bindTexture(TEXTURE_STAGE_2);
                break;

            case 3:
                bindTexture(TEXTURE_STAGE_3);
                break;
        }

        GlStateManager.pushMatrix();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.scale(1F, -1F, -1F);

        switch (rotation) {
            case SOUTH:
                GlStateManager.rotate(180F, 0.0F, 1F, 0F);
                break;
            case NORTH:
                GlStateManager.rotate(0F, 0.0F, 1F, 0F);
                break;
            case EAST:
                GlStateManager.rotate(90F, 0.0F, 1F, 0F);
                break;
            case WEST:
                GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
                break;
        }

        MODEL.render();

        GlStateManager.popMatrix();
    }
}
