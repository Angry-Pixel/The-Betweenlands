package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelWeedwoodBoat - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelWeedwoodBoat extends ModelBase {
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

    public ModelWeedwoodBoat() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.backrimdetail2 = new ModelRenderer(this, 70, 110);
        this.backrimdetail2.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.backrimdetail2.addBox(-2.0F, -10.0F, 0.0F, 4, 12, 2, 0.0F);
        this.piece3rb = new ModelRenderer(this, 157, 27);
        this.piece3rb.setRotationPoint(-6.0F, -8.0F, -2.0F);
        this.piece3rb.addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        this.piece1lb = new ModelRenderer(this, 49, 59);
        this.piece1lb.setRotationPoint(8.0F, 16.0F, 0.0F);
        this.piece1lb.addBox(0.0F, -6.0F, -11.0F, 2, 6, 22, 0.0F);
        this.fillupback1 = new ModelRenderer(this, 0, 92);
        this.fillupback1.setRotationPoint(0.0F, -2.0F, 11.0F);
        this.fillupback1.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 6, 0.0F);
        this.setRotateAngle(fillupback1, 0.40980330836826856F, 0.0F, 0.0F);
        this.piece3l = new ModelRenderer(this, 140, 9);
        this.piece3l.setRotationPoint(4.0F, -2.0F, -2.0F);
        this.piece3l.addBox(0.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        this.piece3r = new ModelRenderer(this, 157, 9);
        this.piece3r.setRotationPoint(-4.0F, -2.0F, -2.0F);
        this.piece3r.addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        this.piece1rb = new ModelRenderer(this, 49, 25);
        this.piece1rb.setRotationPoint(-8.0F, 16.0F, 0.0F);
        this.piece1rb.addBox(-2.0F, -6.0F, -11.0F, 2, 6, 22, 0.0F);
        this.piece2rb = new ModelRenderer(this, 100, 27);
        this.piece2rb.setRotationPoint(-6.0F, -8.0F, 0.0F);
        this.piece2rb.addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        this.paddler = new ModelRenderer(this, 180, 46);
        this.paddler.setRotationPoint(0.0F, 25.0F, 0.0F);
        this.paddler.addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1, 0.0F);
        this.backrimdetail = new ModelRenderer(this, 55, 113);
        this.backrimdetail.setRotationPoint(0.0F, 10.0F, 2.0F);
        this.backrimdetail.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 3, 0.0F);
        this.piece2back = new ModelRenderer(this, 100, 40);
        this.piece2back.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.piece2back.addBox(-6.0F, -8.0F, 0.0F, 12, 8, 2, 0.0F);
        this.backrim1 = new ModelRenderer(this, 55, 92);
        this.backrim1.setRotationPoint(0.0F, 0.0F, 6.0F);
        this.backrim1.addBox(-2.0F, -16.0F, -1.0F, 4, 16, 3, 0.0F);
        this.setRotateAngle(backrim1, -0.36425021489121656F, 0.0F, 0.0F);
        this.basepiece3 = new ModelRenderer(this, 140, 0);
        this.basepiece3.setRotationPoint(0.0F, 22.0F, -11.0F);
        this.basepiece3.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 6, 0.0F);
        this.setRotateAngle(basepiece3, -0.045553093477052F, 0.0F, 0.0F);
        this.piece1r = new ModelRenderer(this, 0, 25);
        this.piece1r.setRotationPoint(-6.0F, 22.0F, 0.0F);
        this.piece1r.addBox(-2.0F, -8.0F, -11.0F, 2, 10, 22, 0.0F);
        this.piece2r = new ModelRenderer(this, 100, 9);
        this.piece2r.setRotationPoint(-4.0F, -2.0F, 0.0F);
        this.piece2r.addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        this.basepiece1 = new ModelRenderer(this, 0, 0);
        this.basepiece1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.basepiece1.addBox(-6.0F, -2.0F, -11.0F, 12, 2, 22, 0.0F);
        this.boatbase = new ModelRenderer(this, 0, 92);
        this.boatbase.setRotationPoint(0.0F, 28.0F, 0.0F);
        this.boatbase.addBox(-2.0F, -4.0F, -11.0F, 4, 2, 22, 0.0F);
        this.piece2l = new ModelRenderer(this, 117, 9);
        this.piece2l.setRotationPoint(4.0F, -2.0F, 0.0F);
        this.piece2l.addBox(0.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        this.fillupfront1 = new ModelRenderer(this, 0, 103);
        this.fillupfront1.setRotationPoint(0.0F, -2.0F, -11.0F);
        this.fillupfront1.addBox(-2.0F, -4.0F, -6.0F, 4, 4, 6, 0.0F);
        this.setRotateAngle(fillupfront1, -0.40980330836826856F, 0.0F, 0.0F);
        this.frontrimdetail1 = new ModelRenderer(this, 105, 113);
        this.frontrimdetail1.setRotationPoint(0.0F, 2.0F, -2.0F);
        this.frontrimdetail1.addBox(-2.0F, 0.0F, -2.0F, 4, 4, 2, 0.0F);
        this.piece3front = new ModelRenderer(this, 140, 40);
        this.piece3front.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.piece3front.addBox(-6.0F, -8.0F, -2.0F, 12, 8, 2, 0.0F);
        this.frontrim1 = new ModelRenderer(this, 105, 92);
        this.frontrim1.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.frontrim1.addBox(-2.0F, -16.0F, -2.0F, 4, 16, 3, 0.0F);
        this.setRotateAngle(frontrim1, 0.36425021489121656F, 0.0F, 0.0F);
        this.frontrim2 = new ModelRenderer(this, 120, 92);
        this.frontrim2.setRotationPoint(0.0F, -16.0F, 0.0F);
        this.frontrim2.addBox(-2.0F, -4.0F, -7.0F, 4, 4, 8, 0.0F);
        this.piece2lb = new ModelRenderer(this, 117, 27);
        this.piece2lb.setRotationPoint(6.0F, -8.0F, 0.0F);
        this.piece2lb.addBox(0.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        this.paddlel = new ModelRenderer(this, 200, 46);
        this.paddlel.setRotationPoint(0.0F, 25.0F, 0.0F);
        this.paddlel.addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1, 0.0F);
        this.piece3frontb = new ModelRenderer(this, 140, 51);
        this.piece3frontb.setRotationPoint(0.0F, -8.0F, -4.0F);
        this.piece3frontb.addBox(-8.0F, -6.0F, -2.0F, 16, 6, 2, 0.0F);
        this.paddlepolel = new ModelRenderer(this, 200, 8);
        this.paddlepolel.setRotationPoint(1.0F, -1.0F, 2.0F);
        this.paddlepolel.addBox(-1.0F, -6.0F, -1.0F, 2, 35, 2, 0.0F);
        this.setRotateAngle(paddlepolel, 0.31869712141416456F, 0.0F, -1.0016444577195458F);
        this.piece2backb = new ModelRenderer(this, 100, 51);
        this.piece2backb.setRotationPoint(0.0F, -8.0F, 4.0F);
        this.piece2backb.addBox(-8.0F, -6.0F, 0.0F, 16, 6, 2, 0.0F);
        this.backrimdetail3 = new ModelRenderer(this, 55, 121);
        this.backrimdetail3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.backrimdetail3.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3, 0.0F);
        this.piece3lb = new ModelRenderer(this, 140, 27);
        this.piece3lb.setRotationPoint(6.0F, -8.0F, -2.0F);
        this.piece3lb.addBox(0.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        this.piece1l = new ModelRenderer(this, 0, 59);
        this.piece1l.setRotationPoint(6.0F, 22.0F, 0.0F);
        this.piece1l.addBox(0.0F, -8.0F, -11.0F, 2, 10, 22, 0.0F);
        this.backrim2 = new ModelRenderer(this, 70, 92);
        this.backrim2.setRotationPoint(0.0F, -16.0F, 0.0F);
        this.backrim2.addBox(-2.0F, -4.0F, -1.0F, 4, 4, 11, 0.0F);
        this.paddleloopl = new ModelRenderer(this, 200, 0);
        this.paddleloopl.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.paddleloopl.addBox(0.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
        this.paddlepoler = new ModelRenderer(this, 180, 8);
        this.paddlepoler.setRotationPoint(-1.0F, -1.0F, 2.0F);
        this.paddlepoler.addBox(-1.0F, -6.0F, -1.0F, 2, 35, 2, 0.0F);
        this.setRotateAngle(paddlepoler, 0.31869712141416456F, 0.0F, 1.0016444577195458F);
        this.basepiece2 = new ModelRenderer(this, 100, 0);
        this.basepiece2.setRotationPoint(0.0F, 22.0F, 11.0F);
        this.basepiece2.addBox(-4.0F, -2.0F, -2.0F, 8, 2, 6, 0.0F);
        this.setRotateAngle(basepiece2, 0.045553093477052F, 0.0F, 0.0F);
        this.paddleloopr = new ModelRenderer(this, 180, 0);
        this.paddleloopr.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.paddleloopr.addBox(-2.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
        this.backrimdetail.addChild(this.backrimdetail2);
        this.basepiece3.addChild(this.piece3rb);
        this.boatbase.addChild(this.fillupback1);
        this.basepiece3.addChild(this.piece3l);
        this.basepiece3.addChild(this.piece3r);
        this.basepiece2.addChild(this.piece2rb);
        this.paddlepoler.addChild(this.paddler);
        this.backrim2.addChild(this.backrimdetail);
        this.basepiece2.addChild(this.piece2back);
        this.fillupback1.addChild(this.backrim1);
        this.basepiece2.addChild(this.piece2r);
        this.basepiece2.addChild(this.piece2l);
        this.boatbase.addChild(this.fillupfront1);
        this.frontrim2.addChild(this.frontrimdetail1);
        this.basepiece3.addChild(this.piece3front);
        this.fillupfront1.addChild(this.frontrim1);
        this.frontrim1.addChild(this.frontrim2);
        this.basepiece2.addChild(this.piece2lb);
        this.paddlepolel.addChild(this.paddlel);
        this.basepiece3.addChild(this.piece3frontb);
        this.paddleloopl.addChild(this.paddlepolel);
        this.basepiece2.addChild(this.piece2backb);
        this.backrimdetail.addChild(this.backrimdetail3);
        this.basepiece3.addChild(this.piece3lb);
        this.backrim1.addChild(this.backrim2);
        this.piece1lb.addChild(this.paddleloopl);
        this.paddleloopr.addChild(this.paddlepoler);
        this.piece1rb.addChild(this.paddleloopr);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.piece1lb.render(f5);
        this.piece1rb.render(f5);
        this.basepiece3.render(f5);
        this.piece1r.render(f5);
        this.basepiece1.render(f5);
        this.boatbase.render(f5);
        this.piece1l.render(f5);
        this.basepiece2.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
