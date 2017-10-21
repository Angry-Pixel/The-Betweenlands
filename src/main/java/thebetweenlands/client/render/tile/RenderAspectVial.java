package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.model.tile.ModelAlembic;
import thebetweenlands.common.block.container.BlockAspectVial;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.util.ColorUtils;

@SideOnly(Side.CLIENT)
public class RenderAspectVial extends TileEntitySpecialRenderer<TileEntityAspectVial> {

    private static final ModelAlembic MODEL = new ModelAlembic();
    public static final ResourceLocation TEXTURE1 = new ResourceLocation("thebetweenlands:textures/tiles/vial_block_green.png");
    public static final ResourceLocation TEXTURE2 = new ResourceLocation("thebetweenlands:textures/tiles/vial_block_orange.png");

    @Override
    public void render(TileEntityAspectVial te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockDentrothyst.EnumDentrothyst type = te.getWorld().getBlockState(te.getPos()).getValue(BlockAspectVial.TYPE);
        switch(type) {
            default:
            case GREEN:
                bindTexture(TEXTURE1);
                break;
            case ORANGE:
                bindTexture(TEXTURE2);
                break;
        }
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1F, -1F, -1F);
        GL11.glScaled(2, 2, 2);
        GL11.glTranslated(-0.3F, -0.75F, 0.25F);

        if(te.getAspect() != null) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            //TODO figure out how to get the color
            //float[] aspectRGBA = ColorUtils.getRGBA(te.getAspect().type.getColor());
            float[] aspectRGBA = ColorUtils.getRGBA(0xFFFFFFFF);
            GL11.glColor4f(aspectRGBA[0] * 2, aspectRGBA[1] * 2, aspectRGBA[2] * 2, 0.98F);

            float filled = te.getAspect().amount / TileEntityAspectVial.MAX_AMOUNT;

            if(filled != 0.0F) {
                GL11.glPushMatrix();
                GL11.glTranslated(0, -(23.5F * 0.0625F) * filled + (23.5F * 0.0625F), 0);
                GL11.glScalef(1, filled, 1);
                GL11.glFrontFace(GL11.GL_CW);
                MODEL.jar_liquid.render(0.0625F);
                GL11.glFrontFace(GL11.GL_CCW);
                GL11.glPopMatrix();
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1.0F);
        }

        MODEL.davids_jar.render(0.0625F);
        GL11.glPopMatrix();
    }
}
