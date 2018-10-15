package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.model.entity.ModelSmolSludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySmolSludgeWorm;


public class RenderSmolSludgeWorm extends RenderLiving <EntitySmolSludgeWorm> {
	ModelSmolSludgeWorm model = new ModelSmolSludgeWorm();
    public RenderSmolSludgeWorm(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSmolSludgeWorm(), 0.5F);
    }

    @Override
	public void doRender(EntitySmolSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {

    	GL11.glPushMatrix();
    	if (entity.hurtResistantTime <= 40 && entity.hurtResistantTime >= 35){
    	    float red =  0.8F;
    	    float green = 0.2F;
    	    float blue = 0.2F;
    	    
    	    GlStateManager.color(red, green, blue);
    	}
	/*	bindTexture(getEntityTexture(entity));
		GL11.glTranslated(x, y + 1.5, z);
		//GL11.glScalef(0.75F, 0.75F, 0.75F);
		GL11.glRotatef(180, 1, 0, 0);
		model.render(entity, yaw, partialTicks, 0.0F, 0.0F, 0.0F, 0.0625F);
		model.renderBody(entity, yaw, partialTicks, 0.0F, 0.0F, 0.0F, 0.0625F);
		*/GL11.glPopMatrix();
		

		if (!entity.debugHitboxes) {
			renderDebugBoundingBox(entity.sludge_worm_1, x, y, z, yaw, partialTicks, entity.sludge_worm_1.posX - entity.posX, entity.sludge_worm_1.posY - entity.posY, entity.sludge_worm_1.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_2, x, y, z, yaw, partialTicks, entity.sludge_worm_2.posX - entity.posX, entity.sludge_worm_2.posY - entity.posY, entity.sludge_worm_2.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_3, x, y, z, yaw, partialTicks, entity.sludge_worm_3.posX - entity.posX, entity.sludge_worm_3.posY - entity.posY, entity.sludge_worm_3.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_4, x, y, z, yaw, partialTicks, entity.sludge_worm_4.posX - entity.posX, entity.sludge_worm_4.posY - entity.posY, entity.sludge_worm_4.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_5, x, y, z, yaw, partialTicks, entity.sludge_worm_5.posX - entity.posX, entity.sludge_worm_5.posY - entity.posY, entity.sludge_worm_5.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_6, x, y, z, yaw, partialTicks, entity.sludge_worm_6.posX - entity.posX, entity.sludge_worm_6.posY - entity.posY, entity.sludge_worm_6.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_7, x, y, z, yaw, partialTicks, entity.sludge_worm_7.posX - entity.posX, entity.sludge_worm_7.posY - entity.posY, entity.sludge_worm_7.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_8, x, y, z, yaw, partialTicks, entity.sludge_worm_8.posX - entity.posX, entity.sludge_worm_8.posY - entity.posY, entity.sludge_worm_8.posZ - entity.posZ);
			renderDebugBoundingBox(entity.sludge_worm_9, x, y, z, yaw, partialTicks, entity.sludge_worm_9.posX - entity.posX, entity.sludge_worm_9.posY - entity.posY, entity.sludge_worm_9.posZ - entity.posZ);
		}
	}
    
    
    
	private void renderDebugBoundingBox(MultiPartEntityPart part, double x, double y, double z, float yaw, float partialTicks, double xOff, double yOff, double zOff) {

		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		
		float f2 = part.width / 2.0F;
		AxisAlignedBB axisalignedbb = part.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - part.posX + x + xOff, axisalignedbb.minY - part.posY + y + yOff, axisalignedbb.minZ - part.posZ + z + zOff, axisalignedbb.maxX - part.posX + x + xOff, axisalignedbb.maxY - part.posY + y + yOff, axisalignedbb.maxZ - part.posZ + z + zOff);
		RenderGlobal.renderFilledBox(axisalignedbb1, 0.2235294117647059F, 0.5F, 0.2235294117647059F, 1F);
		//Tessellator tessellator = Tessellator.getInstance();
		//BufferBuilder bufferbuilder = tessellator.getBuffer();
		//Vec3d vec3 = part.getLook(partialTicks);
		//bufferbuilder.begin(3, DefaultVertexFormats.POSITION_TEX_COLOR);
		//tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
	
    
	@Override
	protected ResourceLocation getEntityTexture(EntitySmolSludgeWorm entity) {
		return new ResourceLocation("thebetweenlands:textures/entity/sludge.png");
	}
	
}