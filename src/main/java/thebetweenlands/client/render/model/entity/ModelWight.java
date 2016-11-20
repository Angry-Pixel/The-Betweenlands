package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityWight;

@SideOnly(Side.CLIENT)
public class ModelWight extends ModelBase {
    private boolean renderHeadOnly = false;

    ModelRenderer body_base;
    ModelRenderer neck;
    ModelRenderer chest_left;
    ModelRenderer chest_right;
    ModelRenderer armright;
    ModelRenderer armleft;
    ModelRenderer legleft;
    ModelRenderer legright;
    ModelRenderer head1;
    ModelRenderer head3;
    ModelRenderer jaw;
    ModelRenderer head2;
    ModelRenderer headpieceleft1;
    ModelRenderer headpieceright1;
    ModelRenderer headpieceleft2;
    ModelRenderer headpieceright2;
    ModelRenderer cap;
    ModelRenderer jawpieceleft;
    ModelRenderer jawpieceright;
    ModelRenderer jawpiecemid;

    public ModelWight() {
        textureWidth = 128;
        textureHeight = 64;
        neck = new ModelRenderer(this, 50, 0);
        neck.setRotationPoint(0.0F, -7.8F, -0.2F);
        neck.addBox(-1.5F, -3.0F, -1.5F, 3, 4, 3, 0.0F);
        setRotation(neck, 0.8196066167365371F, 0.0F, 0.0F);
        head3 = new ModelRenderer(this, 85, 8);
        head3.setRotationPoint(0.0F, -1.4F, 0.2F);
        head3.addBox(-3.5F, -1.2F, -2.2F, 7, 2, 3, 0.0F);
        setRotation(head3, -0.36425021489121656F, 0.0F, 0.0F);
        armleft = new ModelRenderer(this, 9, 28);
        armleft.setRotationPoint(4.6F, -4.6F, -1.5F);
        armleft.addBox(0.0F, -1.0F, -1.0F, 2, 22, 2, 0.0F);
        setRotation(armleft, -0.045553093477052F, -0.136659280431156F, 0.0F);
        jawpieceleft = new ModelRenderer(this, 110, 0);
        jawpieceleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        jawpieceleft.addBox(2.0F, -1.3F, -5.0F, 1, 1, 3, 0.0F);
        cap = new ModelRenderer(this, 50, 27);
        cap.setRotationPoint(0.0F, 0.75F, -5.3F);
        cap.addBox(-4.5F, -5.0F, 0.0F, 9, 10, 9, 0.0F);
        headpieceright2 = new ModelRenderer(this, 73, 22);
        headpieceright2.setRotationPoint(0.0F, 0.0F, 0.0F);
        headpieceright2.addBox(-3.0F, 1.3F, -3.8F, 1, 1, 3, 0.0F);
        jawpieceright = new ModelRenderer(this, 110, 5);
        jawpieceright.setRotationPoint(0.0F, 0.0F, 0.0F);
        jawpieceright.addBox(-3.0F, -1.3F, -5.0F, 1, 1, 3, 0.0F);
        jaw = new ModelRenderer(this, 85, 0);
        jaw.setRotationPoint(0.0F, -2.0F, -1.7F);
        jaw.addBox(-3.0F, -0.3F, -5.0F, 6, 1, 6, 0.0F);
        setRotation(jaw, 0.5009094953223726F, 0.0F, 0.0F);
        body_base = new ModelRenderer(this, 0, 0);
        body_base.setRotationPoint(0.0F, -2.5F, 1.7F);
        body_base.addBox(-4.0F, -1.3F, -2.1F, 8, 8, 5, 0.0F);
        setRotation(body_base, 0.045553093477052F, 0.0F, 0.0F);
        headpieceright1 = new ModelRenderer(this, 57, 22);
        headpieceright1.setRotationPoint(0.0F, 0.0F, 0.0F);
        headpieceright1.addBox(-3.0F, 1.3F, -4.8F, 2, 1, 1, 0.0F);
        legright = new ModelRenderer(this, 27, 28);
        legright.setRotationPoint(-2.3F, 6.7F, 0.0F);
        legright.addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2, 0.0F);
        setRotation(legright, -0.045553093477052F, 0.0F, 0.0F);
        chest_right = new ModelRenderer(this, 23, 14);
        chest_right.setRotationPoint(0.0F, 0.0F, 0.0F);
        chest_right.addBox(-4.6F, -6.3F, -2.8F, 5, 6, 6, 0.0F);
        setRotation(chest_right, 0.2832669375986797F, 0.136659280431156F, 0.03665191429188092F);
        headpieceleft1 = new ModelRenderer(this, 50, 22);
        headpieceleft1.setRotationPoint(0.0F, 0.0F, 0.0F);
        headpieceleft1.addBox(1.0F, 1.3F, -4.8F, 2, 1, 1, 0.0F);
        legleft = new ModelRenderer(this, 18, 28);
        legleft.setRotationPoint(2.3F, 6.7F, 0.0F);
        legleft.addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2, 0.0F);
        setRotation(legleft, -0.045553093477052F, 0.0F, 0.0F);
        chest_left = new ModelRenderer(this, 0, 14);
        chest_left.setRotationPoint(0.0F, 0.0F, 0.0F);
        chest_left.addBox(-0.4F, -6.3F, -2.8F, 5, 6, 6, 0.0F);
        setRotation(chest_left, 0.2832669375986797F, -0.136659280431156F, -0.03665191429188092F);
        head2 = new ModelRenderer(this, 85, 14);
        head2.setRotationPoint(0.0F, -5.5F, -0.8F);
        head2.addBox(-4.0F, 1.3F, -0.8F, 8, 1, 4, 0.0F);
        setRotation(head2, -0.36425021489121656F, 0.0F, 0.0F);
        jawpiecemid = new ModelRenderer(this, 110, 10);
        jawpiecemid.setRotationPoint(0.0F, 0.0F, 0.0F);
        jawpiecemid.addBox(-1.0F, -1.3F, -5.0F, 2, 1, 1, 0.0F);
        armright = new ModelRenderer(this, 0, 28);
        armright.setRotationPoint(-4.6F, -4.6F, -1.5F);
        armright.addBox(-2.0F, -1.0F, -1.0F, 2, 22, 2, 0.0F);
        setRotation(armright, -0.045553093477052F, 0.136659280431156F, 0.0F);
        head1 = new ModelRenderer(this, 50, 8);
        head1.setRotationPoint(0.0F, -5.5F, -0.8F);
        head1.addBox(-4.0F, -3.7F, -4.8F, 8, 5, 8, 0.0F);
        setRotation(head1, -0.36425021489121656F, 0.0F, 0.0F);
        headpieceleft2 = new ModelRenderer(this, 64, 22);
        headpieceleft2.setRotationPoint(0.0F, 0.0F, 0.0F);
        headpieceleft2.addBox(2.0F, 1.3F, -3.8F, 1, 1, 3, 0.0F);
        neck.addChild(head3);
        body_base.addChild(armleft);
        jaw.addChild(jawpieceleft);
        head1.addChild(cap);
        head1.addChild(headpieceright2);
        jaw.addChild(jawpieceright);
        neck.addChild(jaw);
        head1.addChild(headpieceright1);
        body_base.addChild(legright);
        body_base.addChild(chest_right);
        head1.addChild(headpieceleft1);
        body_base.addChild(legleft);
        body_base.addChild(chest_left);
        neck.addChild(head2);
        jaw.addChild(jawpiecemid);
        body_base.addChild(armright);
        neck.addChild(head1);
        head1.addChild(headpieceleft2);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        if(entity instanceof EntityWight) {
            EntityWight wight = (EntityWight) entity;
            neck.rotateAngleX = 0.4F + wight.getHidingAnimation(entityTickTime - entity.ticksExisted);
            jaw.rotateAngleX = -0.4F + 1F - wight.getHidingAnimation(entityTickTime - entity.ticksExisted);
        } else {
            neck.rotateAngleX = 0.4F;
            jaw.rotateAngleX = -0.4F + 1;
        }
        armright.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
        armleft.rotateAngleX = -MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
        legleft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
        legright.rotateAngleX = -MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        if(!this.renderHeadOnly) {
            neck.render(unitPixel);
            body_base.render(unitPixel);
        } else {
            head1.render(unitPixel);
            head3.render(unitPixel);
            jaw.render(unitPixel);
            head2.render(unitPixel);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public ModelWight setRenderHeadOnly(boolean headOnly) {
        this.renderHeadOnly = headOnly;
        return this;
    }
}
