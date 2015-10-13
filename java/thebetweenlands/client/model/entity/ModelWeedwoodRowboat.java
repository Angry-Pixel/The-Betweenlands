package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWeedwoodRowboat extends ModelBase {
	public ModelRenderer boatbase;

	public ModelRenderer basepiece1;

	public ModelRenderer piece1r;

	public ModelRenderer piece1l;

	public ModelRenderer piece1rb;

	public ModelRenderer piece1lb;

	public ModelRenderer basepiece2;

	public ModelRenderer basepiece3;

	public ModelRenderer fillupback1;

	public ModelRenderer fillupfront1;

	public ModelRenderer backrim1;

	public ModelRenderer backrim2;

	public ModelRenderer backrimdetail;

	public ModelRenderer backrimdetail2;

	public ModelRenderer backrimdetail3;

	public ModelRenderer frontrim1;

	public ModelRenderer frontrim2;

	public ModelRenderer frontrimdetail1;

	public ModelRenderer paddleloopr;

	public ModelRenderer paddlepoler;

	public ModelRenderer paddler;

	public ModelRenderer paddleloopl;

	public ModelRenderer paddlepolel;

	public ModelRenderer paddlel;

	public ModelRenderer piece2r;

	public ModelRenderer piece2l;

	public ModelRenderer piece2rb;

	public ModelRenderer piece2lb;

	public ModelRenderer piece2back;

	public ModelRenderer piece2backb;

	public ModelRenderer piece3l;

	public ModelRenderer piece3r;

	public ModelRenderer piece3lb;

	public ModelRenderer piece3rb;

	public ModelRenderer piece3front;

	public ModelRenderer piece3frontb;

	public ModelWeedwoodRowboat() {
		textureWidth = 256;
		textureHeight = 128;
		backrimdetail2 = new ModelRenderer(this, 70, 110);
		backrimdetail2.setRotationPoint(0.0F, 0.0F, 3.0F);
		backrimdetail2.addBox(-2.0F, -10.0F, 0.0F, 4, 12, 2, 0.0F);
		piece3rb = new ModelRenderer(this, 157, 27);
		piece3rb.setRotationPoint(-6.0F, -8.0F, -2.0F);
		piece3rb.addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
		piece1lb = new ModelRenderer(this, 49, 59);
		piece1lb.setRotationPoint(8.0F, 16.0F, 0.0F);
		piece1lb.addBox(0.0F, -6.0F, -11.0F, 2, 6, 22, 0.0F);
		fillupback1 = new ModelRenderer(this, 0, 92);
		fillupback1.setRotationPoint(0.0F, -2.0F, 11.0F);
		fillupback1.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 6, 0.0F);
		setRotateAngle(fillupback1, 0.40980330836826856F, 0.0F, 0.0F);
		piece3l = new ModelRenderer(this, 140, 9);
		piece3l.setRotationPoint(4.0F, -2.0F, -2.0F);
		piece3l.addBox(0.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
		piece3r = new ModelRenderer(this, 157, 9);
		piece3r.setRotationPoint(-4.0F, -2.0F, -2.0F);
		piece3r.addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
		piece1rb = new ModelRenderer(this, 49, 25);
		piece1rb.setRotationPoint(-8.0F, 16.0F, 0.0F);
		piece1rb.addBox(-2.0F, -6.0F, -11.0F, 2, 6, 22, 0.0F);
		piece2rb = new ModelRenderer(this, 100, 27);
		piece2rb.setRotationPoint(-6.0F, -8.0F, 0.0F);
		piece2rb.addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
		paddler = new ModelRenderer(this, 180, 46);
		paddler.setRotationPoint(0.0F, 25.0F, 0.0F);
		paddler.addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1, 0.0F);
		backrimdetail = new ModelRenderer(this, 55, 113);
		backrimdetail.setRotationPoint(0.0F, 10.0F, 2.0F);
		backrimdetail.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 3, 0.0F);
		piece2back = new ModelRenderer(this, 100, 40);
		piece2back.setRotationPoint(0.0F, 0.0F, 4.0F);
		piece2back.addBox(-6.0F, -8.0F, 0.0F, 12, 8, 2, 0.0F);
		backrim1 = new ModelRenderer(this, 55, 92);
		backrim1.setRotationPoint(0.0F, 0.0F, 6.0F);
		backrim1.addBox(-2.0F, -16.0F, -1.0F, 4, 16, 3, 0.0F);
		setRotateAngle(backrim1, -0.36425021489121656F, 0.0F, 0.0F);
		basepiece3 = new ModelRenderer(this, 140, 0);
		basepiece3.setRotationPoint(0.0F, 22.0F, -11.0F);
		basepiece3.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 6, 0.0F);
		setRotateAngle(basepiece3, -0.045553093477052F, 0.0F, 0.0F);
		piece1r = new ModelRenderer(this, 0, 25);
		piece1r.setRotationPoint(-6.0F, 22.0F, 0.0F);
		piece1r.addBox(-2.0F, -8.0F, -11.0F, 2, 10, 22, 0.0F);
		piece2r = new ModelRenderer(this, 100, 9);
		piece2r.setRotationPoint(-4.0F, -2.0F, 0.0F);
		piece2r.addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
		basepiece1 = new ModelRenderer(this, 0, 0);
		basepiece1.setRotationPoint(0.0F, 24.0F, 0.0F);
		basepiece1.addBox(-6.0F, -2.0F, -11.0F, 12, 2, 22, 0.0F);
		boatbase = new ModelRenderer(this, 0, 92);
		boatbase.setRotationPoint(0.0F, 28.0F, 0.0F);
		boatbase.addBox(-2.0F, -4.0F, -11.0F, 4, 2, 22, 0.0F);
		piece2l = new ModelRenderer(this, 117, 9);
		piece2l.setRotationPoint(4.0F, -2.0F, 0.0F);
		piece2l.addBox(0.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
		fillupfront1 = new ModelRenderer(this, 0, 103);
		fillupfront1.setRotationPoint(0.0F, -2.0F, -11.0F);
		fillupfront1.addBox(-2.0F, -4.0F, -6.0F, 4, 4, 6, 0.0F);
		setRotateAngle(fillupfront1, -0.40980330836826856F, 0.0F, 0.0F);
		frontrimdetail1 = new ModelRenderer(this, 105, 113);
		frontrimdetail1.setRotationPoint(0.0F, 2.0F, -2.0F);
		frontrimdetail1.addBox(-2.0F, 0.0F, -2.0F, 4, 4, 2, 0.0F);
		piece3front = new ModelRenderer(this, 140, 40);
		piece3front.setRotationPoint(0.0F, 0.0F, -4.0F);
		piece3front.addBox(-6.0F, -8.0F, -2.0F, 12, 8, 2, 0.0F);
		frontrim1 = new ModelRenderer(this, 105, 92);
		frontrim1.setRotationPoint(0.0F, 0.0F, -6.0F);
		frontrim1.addBox(-2.0F, -16.0F, -2.0F, 4, 16, 3, 0.0F);
		setRotateAngle(frontrim1, 0.36425021489121656F, 0.0F, 0.0F);
		frontrim2 = new ModelRenderer(this, 120, 92);
		frontrim2.setRotationPoint(0.0F, -16.0F, 0.0F);
		frontrim2.addBox(-2.0F, -4.0F, -7.0F, 4, 4, 8, 0.0F);
		piece2lb = new ModelRenderer(this, 117, 27);
		piece2lb.setRotationPoint(6.0F, -8.0F, 0.0F);
		piece2lb.addBox(0.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
		paddlel = new ModelRenderer(this, 200, 46);
		paddlel.setRotationPoint(0.0F, 25.0F, 0.0F);
		paddlel.addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1, 0.0F);
		piece3frontb = new ModelRenderer(this, 140, 51);
		piece3frontb.setRotationPoint(0.0F, -8.0F, -4.0F);
		piece3frontb.addBox(-8.0F, -6.0F, -2.0F, 16, 6, 2, 0.0F);
		paddlepolel = new ModelRenderer(this, 200, 8);
		paddlepolel.setRotationPoint(1.0F, -1.0F, 2.0F);
		paddlepolel.addBox(-1.0F, -6.0F, -1.0F, 2, 35, 2, 0.0F);
		setRotateAngle(paddlepolel, 0.31869712141416456F, 0.0F, -1.0016444577195458F);
		piece2backb = new ModelRenderer(this, 100, 51);
		piece2backb.setRotationPoint(0.0F, -8.0F, 4.0F);
		piece2backb.addBox(-8.0F, -6.0F, 0.0F, 16, 6, 2, 0.0F);
		backrimdetail3 = new ModelRenderer(this, 55, 121);
		backrimdetail3.setRotationPoint(0.0F, -3.0F, 0.0F);
		backrimdetail3.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3, 0.0F);
		piece3lb = new ModelRenderer(this, 140, 27);
		piece3lb.setRotationPoint(6.0F, -8.0F, -2.0F);
		piece3lb.addBox(0.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
		piece1l = new ModelRenderer(this, 0, 59);
		piece1l.setRotationPoint(6.0F, 22.0F, 0.0F);
		piece1l.addBox(0.0F, -8.0F, -11.0F, 2, 10, 22, 0.0F);
		backrim2 = new ModelRenderer(this, 70, 92);
		backrim2.setRotationPoint(0.0F, -16.0F, 0.0F);
		backrim2.addBox(-2.0F, -4.0F, -1.0F, 4, 4, 11, 0.0F);
		paddleloopl = new ModelRenderer(this, 200, 0);
		paddleloopl.setRotationPoint(0.0F, -6.0F, 0.0F);
		paddleloopl.addBox(0.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
		paddlepoler = new ModelRenderer(this, 180, 8);
		paddlepoler.setRotationPoint(-1.0F, -1.0F, 2.0F);
		paddlepoler.addBox(-1.0F, -6.0F, -1.0F, 2, 35, 2, 0.0F);
		setRotateAngle(paddlepoler, 0.31869712141416456F, 0.0F, 1.0016444577195458F);
		basepiece2 = new ModelRenderer(this, 100, 0);
		basepiece2.setRotationPoint(0.0F, 22.0F, 11.0F);
		basepiece2.addBox(-4.0F, -2.0F, -2.0F, 8, 2, 6, 0.0F);
		setRotateAngle(basepiece2, 0.045553093477052F, 0.0F, 0.0F);
		paddleloopr = new ModelRenderer(this, 180, 0);
		paddleloopr.setRotationPoint(0.0F, -6.0F, 0.0F);
		paddleloopr.addBox(-2.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
		backrimdetail.addChild(backrimdetail2);
		basepiece3.addChild(piece3rb);
		boatbase.addChild(fillupback1);
		basepiece3.addChild(piece3l);
		basepiece3.addChild(piece3r);
		basepiece2.addChild(piece2rb);
		paddlepoler.addChild(paddler);
		backrim2.addChild(backrimdetail);
		basepiece2.addChild(piece2back);
		fillupback1.addChild(backrim1);
		basepiece2.addChild(piece2r);
		basepiece2.addChild(piece2l);
		boatbase.addChild(fillupfront1);
		frontrim2.addChild(frontrimdetail1);
		basepiece3.addChild(piece3front);
		fillupfront1.addChild(frontrim1);
		frontrim1.addChild(frontrim2);
		basepiece2.addChild(piece2lb);
		paddlepolel.addChild(paddlel);
		basepiece3.addChild(piece3frontb);
		paddleloopl.addChild(paddlepolel);
		basepiece2.addChild(piece2backb);
		backrimdetail.addChild(backrimdetail3);
		basepiece3.addChild(piece3lb);
		backrim1.addChild(backrim2);
		piece1lb.addChild(paddleloopl);
		paddleloopr.addChild(paddlepoler);
		piece1rb.addChild(paddleloopr);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void render(Entity entity, float swing, float speed, float age, float yaw, float pitch, float scale) {
		piece1lb.render(scale);
		piece1rb.render(scale);
		basepiece3.render(scale);
		piece1r.render(scale);
		basepiece1.render(scale);
		boatbase.render(scale);
		piece1l.render(scale);
		basepiece2.render(scale);
	}
}
