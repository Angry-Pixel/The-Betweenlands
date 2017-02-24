package thebetweenlands.client.render.model.entity.rowboat;

import java.util.EnumMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.common.entity.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.util.RotationOrder;

public class ModelWeedwoodRowboat extends ModelBase {
    private ModelRenderer keel;

    private ModelRenderer hullBottom;

    private ModelRenderer hullBottomLeft;

    private ModelRenderer hullBottomRight;

    private ModelRenderer hullGunwaleLeft;

    private ModelRenderer hullGunwaleRight;

    private ModelRenderer hullBow;

    private ModelRenderer hullStern;

    private ModelRenderer fillupback1;

    private ModelRenderer fillupfront1;

    private ModelRenderer backrim1;

    private ModelRenderer backrim2;

    private ModelRenderer backrimdetail;

    private ModelRenderer backrimdetail2;

    private ModelRenderer backrimdetail3;

    private ModelRenderer frontrim1;

    private ModelRenderer frontrim2;

    private ModelRenderer frontrimdetail1;

    private ModelRenderer oarlockRight;

    private AdvancedModelRenderer oarLoomRight;

    private ModelRenderer oarBladeRight;

    private ModelRenderer oarlockLeft;

    private AdvancedModelRenderer oarLoomLeft;

    private ModelRenderer oarBladeLeft;

    private EnumMap<ShipSide, ModelRenderer> oars;

    private ModelRenderer piece2r;

    private ModelRenderer piece2l;

    private ModelRenderer piece2rb;

    private ModelRenderer piece2lb;

    private ModelRenderer piece2back;

    private ModelRenderer piece2backb;

    private ModelRenderer piece3l;

    private ModelRenderer piece3r;

    private ModelRenderer piece3lb;

    private ModelRenderer piece3rb;

    private ModelRenderer piece3front;

    private ModelRenderer piece3frontb;

    public ModelWeedwoodRowboat() {
        textureWidth = 256;
        textureHeight = 128;
        init();
    }

    private void init() {
        backrimdetail2 = new ModelRenderer(this, 70, 110);
        backrimdetail2.setRotationPoint(0.0F, 0.0F, 3.0F);
        backrimdetail2.addBox(-2.0F, -10.0F, 0.0F, 4, 12, 2, 0.0F);
        piece3rb = new ModelRenderer(this, 157, 27);
        piece3rb.setRotationPoint(-6.0F, -8.0F, -2.0F);
        piece3rb.addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        hullGunwaleRight = new ModelRenderer(this, 49, 59);
        hullGunwaleRight.setRotationPoint(8.0F, 16.0F, 0.0F);
        hullGunwaleRight.addBox(0.0F, -6.0F, -11.0F, 2, 6, 22, 0.0F);
        fillupback1 = new ModelRenderer(this, 0, 92);
        fillupback1.setRotationPoint(0.0F, -2.0F, 11.0F);
        fillupback1.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 6, -0.1F);
        setRotateAngle(fillupback1, 0.40980330836826856F, 0.0F, 0.0F);
        piece3l = new ModelRenderer(this, 140, 9);
        piece3l.setRotationPoint(4.0F, -2.0F, -2.0F);
        piece3l.addBox(0.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        piece3r = new ModelRenderer(this, 157, 9);
        piece3r.setRotationPoint(-4.0F, -2.0F, -2.0F);
        piece3r.addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        hullGunwaleLeft = new ModelRenderer(this, 49, 25);
        hullGunwaleLeft.setRotationPoint(-8.0F, 16.0F, 0.0F);
        hullGunwaleLeft.addBox(-2.0F, -6.0F, -11.0F, 2, 6, 22, 0.0F);
        piece2rb = new ModelRenderer(this, 100, 27);
        piece2rb.setRotationPoint(-6.0F, -8.0F, 0.0F);
        piece2rb.addBox(-2.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        oarBladeRight = new ModelRenderer(this, 180, 46);
        oarBladeRight.setRotationPoint(0.0F, 25.0F, 0.0F);
        oarBladeRight.addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1, 0.0F);
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
        hullStern = new ModelRenderer(this, 140, 0);
        hullStern.setRotationPoint(0.0F, 22.0F, -11.0F);
        hullStern.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 6, 0.0F);
        setRotateAngle(hullStern, -0.045553093477052F, 0.0F, 0.0F);
        hullBottomLeft = new ModelRenderer(this, 0, 25);
        hullBottomLeft.setRotationPoint(-6.0F, 22.0F, 0.0F);
        hullBottomLeft.addBox(-2.0F, -8.0F, -11.0F, 2, 10, 22, 0.0F);
        piece2r = new ModelRenderer(this, 100, 9);
        piece2r.setRotationPoint(-4.0F, -2.0F, 0.0F);
        piece2r.addBox(-2.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        hullBottom = new ModelRenderer(this, 0, 0);
        hullBottom.setRotationPoint(0.0F, 24.0F, 0.0F);
        hullBottom.addBox(-6.0F, -2.0F, -11.0F, 12, 2, 22, 0.0F);
        keel = new ModelRenderer(this, 0, 92);
        keel.setRotationPoint(0.0F, 28.0F, 0.0F);
        keel.addBox(-2.0F, -4.0F, -11.0F, 4, 2, 22, 0.0F);
        piece2l = new ModelRenderer(this, 117, 9);
        piece2l.setRotationPoint(4.0F, -2.0F, 0.0F);
        piece2l.addBox(0.0F, -8.0F, -2.0F, 2, 10, 6, 0.0F);
        fillupfront1 = new ModelRenderer(this, 0, 103);
        fillupfront1.setRotationPoint(0.0F, -2.0F, -11.0F);
        fillupfront1.addBox(-2.0F, -4.0F, -6.0F, 4, 4, 6, -0.1F);
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
        oarBladeLeft = new ModelRenderer(this, 200, 46);
        oarBladeLeft.setRotationPoint(0.0F, 25.0F, 0.0F);
        oarBladeLeft.addBox(-3.0F, 0.0F, -0.5F, 6, 12, 1, 0.0F);
        piece3frontb = new ModelRenderer(this, 140, 51);
        piece3frontb.setRotationPoint(0.0F, -8.0F, -4.0F);
        piece3frontb.addBox(-8.0F, -6.0F, -2.0F, 16, 6, 2, 0.0F);
        oarLoomLeft = new AdvancedModelRenderer(this, 200, 8);
        oarLoomLeft.setRotationOrder(RotationOrder.ZXY);
        oarLoomLeft.setRotationPoint(1.0F, -1.0F, 2.0F);
        oarLoomLeft.addBox(-1.0F, -8.0F, -1.0F, 2, 35, 2, 0.0F);
        setRotateAngle(oarLoomLeft, 0.31869712141416456F, 0.0F, -1.0016444577195458F);
        piece2backb = new ModelRenderer(this, 100, 51);
        piece2backb.setRotationPoint(0.0F, -8.0F, 4.0F);
        piece2backb.addBox(-8.0F, -6.0F, 0.0F, 16, 6, 2, 0.0F);
        backrimdetail3 = new ModelRenderer(this, 55, 121);
        backrimdetail3.setRotationPoint(0.0F, -3.0F, 0.0F);
        backrimdetail3.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 3, 0.0F);
        piece3lb = new ModelRenderer(this, 140, 27);
        piece3lb.setRotationPoint(6.0F, -8.0F, -2.0F);
        piece3lb.addBox(0.0F, -6.0F, -2.0F, 2, 6, 6, 0.0F);
        hullBottomRight = new ModelRenderer(this, 0, 59);
        hullBottomRight.setRotationPoint(6.0F, 22.0F, 0.0F);
        hullBottomRight.addBox(0.0F, -8.0F, -11.0F, 2, 10, 22, 0.0F);
        backrim2 = new ModelRenderer(this, 70, 92);
        backrim2.setRotationPoint(0.0F, -16.0F, 0.0F);
        backrim2.addBox(-2.0F, -4.0F, -1.0F, 4, 4, 11, 0.0F);
        oarlockLeft = new ModelRenderer(this, 200, 0);
        oarlockLeft.setRotationPoint(0.0F, -6.0F, -5.0F);
        oarlockLeft.addBox(0.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
        oarLoomRight = new AdvancedModelRenderer(this, 180, 8);
        oarLoomRight.setRotationOrder(RotationOrder.ZXY);
        oarLoomRight.setRotationPoint(-1.0F, -1.0F, 2.0F);
        oarLoomRight.addBox(-1.0F, -8.0F, -1.0F, 2, 35, 2, 0.0F);
        setRotateAngle(oarLoomRight, 0.31869712141416456F, 0.0F, 1.0016444577195458F);
        hullBow = new ModelRenderer(this, 100, 0);
        hullBow.setRotationPoint(0.0F, 22.0F, 11.0F);
        hullBow.addBox(-4.0F, -2.0F, -2.0F, 8, 2, 6, 0.0F);
        setRotateAngle(hullBow, 0.045553093477052F, 0.0F, 0.0F);
        oarlockRight = new ModelRenderer(this, 180, 0);
        oarlockRight.setRotationPoint(0.0F, -6.0F, -5.0F);
        oarlockRight.addBox(-2.0F, -3.0F, 0.0F, 2, 3, 4, 0.0F);
        ModelRenderer seat = new ModelRenderer(this, 100, 60);
        seat.setRotationPoint(0, -10, 2);
        seat.addBox(-6, 0, 0, 12, 2, 5);
        hullBottom.addChild(seat);
        backrimdetail.addChild(backrimdetail2);
        hullStern.addChild(piece3rb);
        keel.addChild(fillupback1);
        hullStern.addChild(piece3l);
        hullStern.addChild(piece3r);
        hullBow.addChild(piece2rb);
        oarLoomRight.addChild(oarBladeRight);
        backrim2.addChild(backrimdetail);
        hullBow.addChild(piece2back);
        fillupback1.addChild(backrim1);
        hullBow.addChild(piece2r);
        hullBow.addChild(piece2l);
        keel.addChild(fillupfront1);
        frontrim2.addChild(frontrimdetail1);
        hullStern.addChild(piece3front);
        fillupfront1.addChild(frontrim1);
        frontrim1.addChild(frontrim2);
        hullBow.addChild(piece2lb);
        oarLoomLeft.addChild(oarBladeLeft);
        hullStern.addChild(piece3frontb);
        oarlockLeft.addChild(oarLoomLeft);
        hullBow.addChild(piece2backb);
        backrimdetail.addChild(backrimdetail3);
        hullStern.addChild(piece3lb);
        backrim1.addChild(backrim2);
        hullGunwaleRight.addChild(oarlockLeft);
        oarlockRight.addChild(oarLoomRight);
        hullGunwaleLeft.addChild(oarlockRight);
        oars = ShipSide.newEnumMap(ModelRenderer.class, oarLoomLeft, oarLoomRight);
    }

    public void render(EntityWeedwoodRowboat rowboat, float scale, float delta) {
        keel.render(scale);
        hullBottom.render(scale);
        hullBottomLeft.render(scale);
        hullBottomRight.render(scale);
        hullBow.render(scale);
        hullStern.render(scale);
        hullGunwaleLeft.render(scale);
        hullGunwaleRight.render(scale);
    }

    public ModelRenderer getOar(ShipSide side) {
        return oars.get(side);
    }

    public void animateOar(EntityWeedwoodRowboat rowboat, ShipSide side, float delta) {
        float theta = rowboat.getRowProgress(side, delta);
        ModelRenderer oar = getOar(side);
        oar.rotateAngleX = rowboat.getOarRotationX(side, theta, delta);
        oar.rotateAngleY = rowboat.getOarRotationY(side, theta);
        oar.rotateAngleZ = rowboat.getOarRotationZ(side, theta, delta);
    }

    private static void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
