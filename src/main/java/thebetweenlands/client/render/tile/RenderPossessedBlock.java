package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelPossessedBlock;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.tile.TileEntityPossessedBlock;
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderPossessedBlock extends TileEntitySpecialRenderer<TileEntityPossessedBlock> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/possessed_block.png");
    private final ModelPossessedBlock model = new ModelPossessedBlock();

    @Override
    public void render(TileEntityPossessedBlock te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        EnumFacing facing = StatePropertyHelper.getStatePropertySafely(te, BlockPossessedBlock.class, BlockPossessedBlock.FACING, EnumFacing.NORTH);

        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.scale(-1, -1, 1);

        switch (facing) {
            case NORTH:
                GlStateManager.rotate(0F, 0.0F, 1F, 0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180F, 0.0F, 1F, 0F);
                break;
            case WEST:
                GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
                break;
            case EAST:
                GlStateManager.rotate(90F, 0.0F, 1F, 0F);
                break;
        }

        model.render(te);
        GlStateManager.popMatrix();
    }
}