package thebetweenlands.client.render.entity.projectile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.projectiles.EntityBLArrow;
import thebetweenlands.items.bow.EnumArrowType;

@SideOnly(Side.CLIENT)
public class RenderBLArrow extends RenderArrow {
	private static ResourceLocation texture1 = new ResourceLocation("thebetweenlands:textures/entity/anglerToothArrow.png");
	private static ResourceLocation texture2 = new ResourceLocation("thebetweenlands:textures/entity/poisonedAnglerToothArrow.png");
	private static ResourceLocation texture3 = new ResourceLocation("thebetweenlands:textures/entity/octineArrow.png");
	private static ResourceLocation texture4 = new ResourceLocation("thebetweenlands:textures/entity/basiliskArrow.png");

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
		//renderArrow((EntityBLArrow) entity, x, y, z, yaw, tick);
		super.doRender(entity, x, y, z, yaw, tick);

		EntityBLArrow entityArrow = (EntityBLArrow) entity;
		if(entityArrow.getArrowType() == EnumArrowType.OCTINE)
			if (ShaderHelper.INSTANCE.canUseShaders())
				ShaderHelper.INSTANCE.addDynLight(new LightSource(entityArrow.posX, entityArrow.posY, entityArrow.posZ, 2, 2, 0, 0));
	}

	/*public void renderArrow(EntityBLArrow entityArrow, double x, double y, double z, float yaw, float tick) {
		if(entityArrow.getArrowType() == EnumArrowType.OCTINE)
			if (ShaderHelper.INSTANCE.canUseShaders())
				ShaderHelper.INSTANCE.addDynLight(new LightSource(entityArrow.posX, entityArrow.posY, entityArrow.posZ, 2, 0x100500));

		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(getEntityTexture(entityArrow));

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * tick - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * tick, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		byte b0 = 0;
		float f2 = 0.0F;
		float f3 = 0.5F;
		float f4 = (float) (0 + b0 * 10) / 32.0F;
		float f5 = (float) (5 + b0 * 10) / 32.0F;
		float f6 = 0.0F;
		float f7 = 0.15625F;
		float f8 = (float) (5 + b0 * 10) / 32.0F;
		float f9 = (float) (10 + b0 * 10) / 32.0F;
		float f10 = 0.05625F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float f11 = (float) entityArrow.arrowShake - tick;

		if (f11 > 0.0F) {
			float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
			GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
		}

		GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(f10, f10, f10);
		GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
		GL11.glNormal3f(f10, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) f7, (double) f8);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) f7, (double) f9);
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) f6, (double) f9);
		tessellator.draw();
		GL11.glNormal3f(-f10, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double) f6, (double) f8);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double) f7, (double) f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double) f7, (double) f9);
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double) f6, (double) f9);
		tessellator.draw();

		for (int i = 0; i < 4; ++i) {
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, f10);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double) f2, (double) f4);
			tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double) f3, (double) f4);
			tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double) f3, (double) f5);
			tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double) f2, (double) f5);
			tessellator.draw();
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}*/

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		switch(((EntityBLArrow)entity).getArrowType()) {
		case ANGLER_POISON:
			return texture2;
		case OCTINE:
			return texture3;
		case BASILISK:
			return texture4;
		case DEFAULT:
		default:
			return texture1;
		}
	}
}