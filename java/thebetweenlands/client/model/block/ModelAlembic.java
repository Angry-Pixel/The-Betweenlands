package thebetweenlands.client.model.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelAlembic extends ModelBase {
	ModelRenderer alembic_base;
	ModelRenderer stand;
	ModelRenderer davids_jar;
	ModelRenderer alembic_midpiece;
	ModelRenderer alembic_top1;
	ModelRenderer alembic_top2;
	ModelRenderer alembic_pipe1;
	ModelRenderer alembic_pipe2;
	ModelRenderer alembic_pipe3;
	ModelRenderer leg1;
	ModelRenderer leg2;
	ModelRenderer leg3;
	ModelRenderer leg4;
	ModelRenderer firebowl;
	ModelRenderer davids_jartop1;
	ModelRenderer davids_jartop2;

	public ModelAlembic() {
		textureWidth = 64;
		textureHeight = 32;
		alembic_top2 = new ModelRenderer(this, 0, 27);
		alembic_top2.setRotationPoint(-2.5F, -9.0F, 0.0F);
		alembic_top2.addBox(0.0F, 0.0F, -2.0F, 4, 1, 4, 0.0F);
		setRotateAngle(alembic_top2, 0.0F, 0.0F, -0.22759093446006054F);
		leg4 = new ModelRenderer(this, 48, 20);
		leg4.setRotationPoint(-4.0F, 1.0F, -4.0F);
		leg4.addBox(0.1F, 0.0F, 0.1F, 2, 6, 2, 0.0F);
		setRotateAngle(leg4, -0.091106186954104F, 0.0F, 0.091106186954104F);
		alembic_pipe2 = new ModelRenderer(this, 25, 3);
		alembic_pipe2.setRotationPoint(3.0F, 0.0F, 0.0F);
		alembic_pipe2.addBox(0.0F, 0.0F, -0.49F, 4, 1, 1, 0.0F);
		setRotateAngle(alembic_pipe2, 0.0F, 0.0F, 0.5462880558742251F);
		alembic_midpiece = new ModelRenderer(this, 0, 13);
		alembic_midpiece.setRotationPoint(0.0F, -6.0F, 0.0F);
		alembic_midpiece.addBox(-2.0F, -1.0F, -2.0F, 4, 1, 4, 0.0F);
		leg1 = new ModelRenderer(this, 21, 20);
		leg1.setRotationPoint(4.0F, 1.0F, -4.0F);
		leg1.addBox(-2.1F, 0.0F, 0.1F, 2, 6, 2, 0.0F);
		setRotateAngle(leg1, -0.091106186954104F, 0.0F, -0.091106186954104F);
		alembic_pipe1 = new ModelRenderer(this, 25, 0);
		alembic_pipe1.setRotationPoint(4.0F, 0.0F, 0.0F);
		alembic_pipe1.addBox(0.0F, 0.0F, -0.5F, 3, 1, 1, 0.0F);
		setRotateAngle(alembic_pipe1, 0.0F, 0.0F, 0.36425021489121656F);
		leg3 = new ModelRenderer(this, 39, 20);
		leg3.setRotationPoint(-4.0F, 1.0F, 4.0F);
		leg3.addBox(0.1F, 0.0F, -2.1F, 2, 6, 2, 0.0F);
		setRotateAngle(leg3, 0.091106186954104F, 0.0F, 0.091106186954104F);
		davids_jartop1 = new ModelRenderer(this, 55, 2);
		davids_jartop1.setRotationPoint(0.0F, -4.0F, 0.0F);
		davids_jartop1.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2, 0.0F);
		davids_jar = new ModelRenderer(this, 43, 6);
		davids_jar.setRotationPoint(4.8F, 24.0F, -3.8F);
		davids_jar.addBox(-2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F);
		setRotateAngle(davids_jar, 0.0F, -0.6373942428283291F, 0.0F);
		stand = new ModelRenderer(this, 18, 9);
		stand.setRotationPoint(-2.0F, 17.0F, 2.0F);
		stand.addBox(-4.0F, 0.0F, -4.0F, 8, 2, 8, 0.0F);
		setRotateAngle(stand, 0.0F, 0.6829473363053812F, 0.0F);
		alembic_pipe3 = new ModelRenderer(this, 25, 6);
		alembic_pipe3.setRotationPoint(4.0F, 0.0F, 0.0F);
		alembic_pipe3.addBox(0.0F, 0.0F, -0.49F, 4, 1, 1, 0.0F);
		setRotateAngle(alembic_pipe3, 0.0F, 0.0F, 0.5462880558742251F);
		alembic_base = new ModelRenderer(this, 0, 0);
		alembic_base.setRotationPoint(-2.0F, 20.0F, 2.2F);
		alembic_base.addBox(-3.0F, -6.0F, -3.0F, 6, 7, 6, 0.0F);
		setRotateAngle(alembic_base, 0.091106186954104F, 0.6829473363053812F, 0.091106186954104F);
		firebowl = new ModelRenderer(this, 36, 0);
		firebowl.setRotationPoint(0.0F, 7.0F, 0.0F);
		firebowl.addBox(-2.0F, -1.01F, -2.0F, 4, 1, 4, 0.0F);
		setRotateAngle(firebowl, 0.0F, 0.22759093446006054F, 0.0F);
		alembic_top1 = new ModelRenderer(this, 0, 19);
		alembic_top1.setRotationPoint(0.0F, -7.0F, 0.0F);
		alembic_top1.addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5, 0.0F);
		leg2 = new ModelRenderer(this, 30, 20);
		leg2.setRotationPoint(4.0F, 1.0F, 4.0F);
		leg2.addBox(-2.1F, 0.0F, -2.1F, 2, 6, 2, 0.0F);
		setRotateAngle(leg2, 0.091106186954104F, 0.0F, -0.091106186954104F);
		davids_jartop2 = new ModelRenderer(this, 51, 15);
		davids_jartop2.setRotationPoint(0.0F, -5.0F, 0.0F);
		davids_jartop2.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
		alembic_base.addChild(alembic_top2);
		stand.addChild(leg4);
		alembic_pipe1.addChild(alembic_pipe2);
		alembic_base.addChild(alembic_midpiece);
		stand.addChild(leg1);
		alembic_top2.addChild(alembic_pipe1);
		stand.addChild(leg3);
		davids_jar.addChild(davids_jartop1);
		alembic_pipe2.addChild(alembic_pipe3);
		stand.addChild(firebowl);
		alembic_base.addChild(alembic_top1);
		stand.addChild(leg2);
		davids_jar.addChild(davids_jartop2);
	}

	public void render() { 
		GL11.glColorMask(false, false, false, false);
		davids_jar.render(0.0625F);
		stand.render(0.0625F);
		alembic_base.render(0.0625F);
		GL11.glColorMask(true, true, true, true);
		davids_jar.render(0.0625F);
		stand.render(0.0625F);
		alembic_base.render(0.0625F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
