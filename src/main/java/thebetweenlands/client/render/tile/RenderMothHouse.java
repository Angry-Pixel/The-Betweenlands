package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
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

        int silkRenderStage = 0;

        if(te != null) {
            silkRenderStage = te.getSilkRenderStage();
        }

        silkRenderStage = Math.max(0, Math.min(silkRenderStage, 3));

        switch(silkRenderStage) {
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


        boolean onWall = StatePropertyHelper.getStatePropertySafely(te, BlockMothHouse.class, BlockMothHouse.ON_WALL, false);
        
        if(onWall) {
        	GlStateManager.translate(0, 0, 0.38f);
        }
        
        MODEL.render();
        
        ItemStack grubs = ItemStack.EMPTY;
        if(te != null) {
        	grubs = te.getStackInSlot(TileEntityMothHouse.SLOT_GRUBS);
        }
        if(!grubs.isEmpty()) {
        	renderGrubs(grubs);
        }

        GlStateManager.popMatrix();
    }
    
    private void renderGrubs(ItemStack stack) {
    	if(stack.getCount() > 0) {
    		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

    		switch(stack.getCount()) {
    		default:
    		case 6:
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.15D, 0.15D, 0.15D);
    			GlStateManager.translate(0.5D, 5.8D, 0.4D);
    			GlStateManager.rotate(10, 0, 0, 1);
    			GlStateManager.rotate(180, 1, 0, 0);
    			renderItem.renderItem(stack, TransformType.FIXED);
    			GlStateManager.popMatrix();
    		case 5:
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.15D, 0.15D, 0.15D);
    			GlStateManager.translate(-0.5D, 6.2D, 0.4D);
    			GlStateManager.rotate(-80, 0, 0, 1);
    			GlStateManager.rotate(180, 1, 0, 0);
    			renderItem.renderItem(stack, TransformType.FIXED);
    			GlStateManager.popMatrix();
    		case 4:
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.15D, 0.15D, 0.15D);
    			GlStateManager.translate(1.1D, 6.8D, 0.4D);
    			GlStateManager.rotate(-8, 0, 0, 1);
    			GlStateManager.rotate(180, 0, 1, 0);
    			GlStateManager.rotate(180, 1, 0, 0);
    			renderItem.renderItem(stack, TransformType.FIXED);
    			GlStateManager.popMatrix();
    		case 3:
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.15D, 0.15D, 0.15D);
    			GlStateManager.translate(0.3D, 8.6D, 0.4D);
    			GlStateManager.rotate(-75, 0, 0, 1);
    			GlStateManager.rotate(180, 0, 1, 0);
    			GlStateManager.rotate(180, 1, 0, 0);
    			renderItem.renderItem(stack, TransformType.FIXED);
    			GlStateManager.popMatrix();
    		case 2:
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.15D, 0.15D, 0.15D);
    			GlStateManager.translate(-1.0D, 8.0D, 0.4D);
    			GlStateManager.rotate(5, 0, 0, 1);
    			GlStateManager.rotate(180, 0, 1, 0);
    			GlStateManager.rotate(180, 1, 0, 0);
    			renderItem.renderItem(stack, TransformType.FIXED);
    			GlStateManager.popMatrix();
    		case 1:
    			GlStateManager.pushMatrix();
    			GlStateManager.scale(0.15D, 0.15D, 0.15D);
    			GlStateManager.translate(0.5D, 7.5D, 0.4D);
    			GlStateManager.rotate(-20, 0, 0, 1);
    			GlStateManager.rotate(180, 1, 0, 0);
    			renderItem.renderItem(stack, TransformType.FIXED);
    			GlStateManager.popMatrix();
    		}
    	}
    }
}
