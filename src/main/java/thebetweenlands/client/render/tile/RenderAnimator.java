package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.model.tile.ModelAnimator;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;
import thebetweenlands.util.GLUProjection;

import java.util.ArrayList;
import java.util.Random;

public class RenderAnimator extends TileEntitySpecialRenderer<TileEntityAnimator> {
    private static final ModelAnimator model = new ModelAnimator();
    private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/animator.png");
    public static RenderAnimator instance;
    private RenderManager renderManager;
    private double viewRot;

    public RenderAnimator() {
        renderManager = Minecraft.getMinecraft().getRenderManager();
    }

    @Override
    public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
        super.setRendererDispatcher(rendererDispatcherIn);
        instance = this;
    }

    public void renderTileAsItem(double x, double y, double z) {
        bindTexture(TEXTURE);
        GL11.glPushMatrix();
        renderMainModel(x, y, z);
        GL11.glPopMatrix();
    }

    private void renderMainModel(double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5F, y + 2F, z + 0.5F);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        model.renderAll(0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntityAnimator te, double x, double y, double z, float partialTicks, int destroyStage) {
        Random rand = new Random();
        ArrayList<GLUProjection.Vector3D> points = new ArrayList<GLUProjection.Vector3D>();
        viewRot = 180D + Math.toDegrees(Math.atan2(renderManager.viewerPosX - te.getPos().getX() - 0.5D, renderManager.viewerPosZ - te.getPos().getZ() - 0.5D));
        int meta = te.getBlockMetadata();
        bindTexture(TEXTURE);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
        GL11.glScalef(1F, -1F, -1F);
        GL11.glRotatef(meta * 90.0F - 180.0F, 0.0F, 1F, 0F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        model.render(null, 0, 0, 0, 0, 0, 0.0625F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();

        // Sulfur rendering
        if (te.getStackInSlot(2) != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5D, y + 0.27D, z + 0.5D);
            GL11.glRotated(180, 1, 0, 0);
            int items = te.getStackInSlot(2).stackSize;
            rand.setSeed((long) (te.getPos().getX() + te.getPos().getY() + te.getPos().getZ()));
            for (int i = 0; i < items; i++) {
                GL11.glPushMatrix();
                GL11.glTranslated(rand.nextFloat() / 3.0D - 1.0D / 6.0D, 0.0D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
                GL11.glRotated(rand.nextFloat() * 30.0D - 15.0D, 1, 0, 0);
                GL11.glRotated(rand.nextFloat() * 30.0D - 15.0D, 0, 0, 1);
                GL11.glScaled(0.125D, 0.125D, 0.125D);
                GL11.glRotated(90, 1, 0, 0);
                GL11.glRotated(rand.nextFloat() * 360.0F, 0, 0, 1);
                Minecraft.getMinecraft().getRenderItem().renderItem(ItemMisc.EnumItemMisc.SULFUR.create(1), ItemCameraTransforms.TransformType.GROUND);
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        }

        // Life crystal
        if (te.getStackInSlot(1) != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5D, y + 0.43D, z + 0.5D);
            GL11.glScaled(0.18D, 0.18D, 0.18D);
            GL11.glRotated(viewRot, 0, 1, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(ItemRegistry.LIFE_CRYSTAL), ItemCameraTransforms.TransformType.GROUND);
            GL11.glPopMatrix();
        }

        // Item
        ItemStack input = te.getStackInSlot(0);
        if (input != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5D, y + 1.43D, z + 0.5D);

            AnimatorRecipe recipe = AnimatorRecipe.getRecipe(input);

            if (!(input.getItem() instanceof ItemMonsterPlacer) && (recipe == null || recipe.getRenderEntity() == null)) {
                GL11.glScaled(0.3D, 0.3D, 0.3D);
                GL11.glRotated(viewRot, 0, 1, 0);
                Minecraft.getMinecraft().getRenderItem().renderItem(te.getStackInSlot(0), ItemCameraTransforms.TransformType.GROUND);
            } else {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.65F);
                Entity entity = null;
                if(recipe.getRenderEntity() != null) {
                    entity = recipe.getRenderEntity();
                } else if(input.getItem() instanceof ItemMonsterPlacer) {
                    entity = EntityList.createEntityByID(input.getItemDamage(), te.getWorld());
                }
                if (entity != null) {
                    GL11.glTranslated(0.0D, -entity.height/4.0D, 0.0D);
                    GL11.glRotated(viewRot +180, 0, 1, 0);
                    GL11.glScaled(0.75D, 0.75D, 0.75D);
                    entity.setWorld(te.getWorld());
                    entity.setRotationYawHead(0F);
                    entity.rotationPitch = 0F;
                    renderManager.doRenderEntity(entity, 0D, 0D, 0D, 0F, 0F, true);
                }
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
            GL11.glPopMatrix();
        }
    }
}
