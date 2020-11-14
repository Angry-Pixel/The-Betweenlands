package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLRunicBeetle - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelRunicBeetle extends ModelBase {
    public ModelRenderer thorax_main;
    public ModelRenderer abdomen;
    public ModelRenderer head;
    public ModelRenderer elytra_left;
    public ModelRenderer elytra_right;
    public ModelRenderer leg_left_front1a;
    public ModelRenderer leg_left_mid1a;
    public ModelRenderer leg_left_back1a;
    public ModelRenderer leg_right_front1a;
    public ModelRenderer leg_right_mid1a;
    public ModelRenderer leg_right_back1a;
    public ModelRenderer jaw_left1a;
    public ModelRenderer jaw_right1a;
    public ModelRenderer antennae_left1a;
    public ModelRenderer antennae_right1a;
    public ModelRenderer jaw_left1b;
    public ModelRenderer jaw_right1b;
    public ModelRenderer antennae_left1b;
    public ModelRenderer antennae_right1b;
    public ModelRenderer leg_left_front1b;
    public ModelRenderer leg_left_mid1b;
    public ModelRenderer leg_left_back1b;
    public ModelRenderer leg_right_front1b;
    public ModelRenderer leg_right_mid1b;
    public ModelRenderer leg_right_back1b;

    public ModelRunicBeetle() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.thorax_main = new ModelRenderer(this, 0, 0);
        this.thorax_main.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.thorax_main.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
        this.leg_left_back1a = new ModelRenderer(this, 26, 15);
        this.leg_left_back1a.setRotationPoint(0.5F, 2.0F, 1.5F);
        this.leg_left_back1a.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
        this.setRotateAngle(leg_left_back1a, -1.2747884856566583F, -2.86844862565268F, 0.0F);
        this.abdomen = new ModelRenderer(this, 0, 5);
        this.abdomen.setRotationPoint(0.01F, 0.0F, 2.0F);
        this.abdomen.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
        this.setRotateAngle(abdomen, -0.136659280431156F, 0.0F, 0.0F);
        this.leg_right_front1a = new ModelRenderer(this, 20, 21);
        this.leg_right_front1a.setRotationPoint(-0.5F, 2.0F, 0.5F);
        this.leg_right_front1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
        this.setRotateAngle(leg_right_front1a, -1.1383037381507017F, 1.0927506446736497F, 0.0F);
        this.leg_left_mid1b = new ModelRenderer(this, 23, 18);
        this.leg_left_mid1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.leg_left_mid1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
        this.setRotateAngle(leg_left_mid1b, -0.27314402793711257F, 0.0F, 0.0F);
        this.leg_left_mid1a = new ModelRenderer(this, 23, 15);
        this.leg_left_mid1a.setRotationPoint(0.5F, 2.0F, 1.0F);
        this.leg_left_mid1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
        this.setRotateAngle(leg_left_mid1a, -1.1383037381507017F, -1.9577358219620393F, 0.0F);
        this.leg_right_mid1b = new ModelRenderer(this, 23, 24);
        this.leg_right_mid1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.leg_right_mid1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
        this.setRotateAngle(leg_right_mid1b, -0.27314402793711257F, 0.0F, 0.0F);
        this.elytra_right = new ModelRenderer(this, 20, 8);
        this.elytra_right.setRotationPoint(0.0F, -0.2F, 0.0F);
        this.elytra_right.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(elytra_right, 0.0F, 0.0F, -0.091106186954104F);
        this.antennae_left1a = new ModelRenderer(this, -3, 24);
        this.antennae_left1a.setRotationPoint(0.0F, 0.01F, -2.0F);
        this.antennae_left1a.addBox(0.0F, 0.0F, -2.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(antennae_left1a, 0.0F, -0.40980330836826856F, 0.0F);
        this.antennae_right1a = new ModelRenderer(this, 6, 24);
        this.antennae_right1a.setRotationPoint(0.0F, 0.01F, -2.0F);
        this.antennae_right1a.addBox(-4.0F, 0.0F, -2.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(antennae_right1a, 0.0F, 0.40980330836826856F, 0.0F);
        this.leg_left_back1b = new ModelRenderer(this, 26, 19);
        this.leg_left_back1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.leg_left_back1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
        this.setRotateAngle(leg_left_back1b, -0.18203784098300857F, 0.0F, 0.0F);
        this.jaw_right1b = new ModelRenderer(this, 9, 20);
        this.jaw_right1b.setRotationPoint(0.0F, 1.0F, -2.0F);
        this.jaw_right1b.addBox(-1.0F, -1.0F, -2.0F, 1, 1, 2, 0.0F);
        this.setRotateAngle(jaw_right1b, -0.5009094953223726F, 0.0F, 0.0F);
        this.leg_left_front1a = new ModelRenderer(this, 20, 15);
        this.leg_left_front1a.setRotationPoint(0.5F, 2.0F, 0.5F);
        this.leg_left_front1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
        this.setRotateAngle(leg_left_front1a, -1.1383037381507017F, -1.0927506446736497F, 0.0F);
        this.leg_right_front1b = new ModelRenderer(this, 20, 24);
        this.leg_right_front1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.leg_right_front1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
        this.setRotateAngle(leg_right_front1b, -0.27314402793711257F, 0.0F, 0.0F);
        this.elytra_left = new ModelRenderer(this, 20, 1);
        this.elytra_left.setRotationPoint(0.0F, -0.2F, 0.0F);
        this.elytra_left.addBox(0.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F);
        this.setRotateAngle(elytra_left, 0.0F, 0.0F, 0.091106186954104F);
        this.leg_left_front1b = new ModelRenderer(this, 20, 18);
        this.leg_left_front1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.leg_left_front1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
        this.setRotateAngle(leg_left_front1b, -0.27314402793711257F, 0.0F, 0.0F);
        this.leg_right_back1a = new ModelRenderer(this, 26, 21);
        this.leg_right_back1a.setRotationPoint(-0.5F, 2.0F, 1.5F);
        this.leg_right_back1a.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
        this.setRotateAngle(leg_right_back1a, -1.2747884856566583F, 2.86844862565268F, 0.0F);
        this.jaw_left1b = new ModelRenderer(this, 0, 20);
        this.jaw_left1b.setRotationPoint(0.0F, 1.0F, -2.0F);
        this.jaw_left1b.addBox(0.0F, -1.0F, -2.0F, 1, 1, 2, 0.0F);
        this.setRotateAngle(jaw_left1b, -0.5009094953223726F, 0.0F, 0.0F);
        this.antennae_left1b = new ModelRenderer(this, -3, 28);
        this.antennae_left1b.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.antennae_left1b.addBox(0.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(antennae_left1b, 0.4553564018453205F, 0.0F, 0.0F);
        this.antennae_right1b = new ModelRenderer(this, 6, 28);
        this.antennae_right1b.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.antennae_right1b.addBox(-4.0F, 0.0F, -3.0F, 4, 0, 3, 0.0F);
        this.setRotateAngle(antennae_right1b, 0.4553564018453205F, 0.0F, 0.0F);
        this.jaw_right1a = new ModelRenderer(this, 9, 15);
        this.jaw_right1a.setRotationPoint(-0.25F, 1.0F, -1.0F);
        this.jaw_right1a.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(jaw_right1a, 0.31869712141416456F, 0.18203784098300857F, 0.0F);
        this.head = new ModelRenderer(this, 0, 10);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(head, 0.18203784098300857F, 0.0F, 0.0F);
        this.leg_right_mid1a = new ModelRenderer(this, 23, 21);
        this.leg_right_mid1a.setRotationPoint(-0.5F, 2.0F, 1.0F);
        this.leg_right_mid1a.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 0, 0.0F);
        this.setRotateAngle(leg_right_mid1a, -1.1383037381507017F, 1.9577358219620393F, 0.0F);
        this.leg_right_back1b = new ModelRenderer(this, 26, 25);
        this.leg_right_back1b.setRotationPoint(0.0F, 3.0F, 0.0F);
        this.leg_right_back1b.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 0, 0.0F);
        this.setRotateAngle(leg_right_back1b, -0.18203784098300857F, 0.0F, 0.0F);
        this.jaw_left1a = new ModelRenderer(this, 0, 15);
        this.jaw_left1a.setRotationPoint(0.25F, 1.0F, -1.0F);
        this.jaw_left1a.addBox(0.0F, 0.0F, -2.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(jaw_left1a, 0.31869712141416456F, -0.18203784098300857F, 0.0F);
        this.thorax_main.addChild(this.leg_left_back1a);
        this.thorax_main.addChild(this.abdomen);
        this.thorax_main.addChild(this.leg_right_front1a);
        this.leg_left_mid1a.addChild(this.leg_left_mid1b);
        this.thorax_main.addChild(this.leg_left_mid1a);
        this.leg_right_mid1a.addChild(this.leg_right_mid1b);
        this.thorax_main.addChild(this.elytra_right);
        this.head.addChild(this.antennae_left1a);
        this.head.addChild(this.antennae_right1a);
        this.leg_left_back1a.addChild(this.leg_left_back1b);
        this.jaw_right1a.addChild(this.jaw_right1b);
        this.thorax_main.addChild(this.leg_left_front1a);
        this.leg_right_front1a.addChild(this.leg_right_front1b);
        this.thorax_main.addChild(this.elytra_left);
        this.leg_left_front1a.addChild(this.leg_left_front1b);
        this.thorax_main.addChild(this.leg_right_back1a);
        this.jaw_left1a.addChild(this.jaw_left1b);
        this.antennae_left1a.addChild(this.antennae_left1b);
        this.antennae_right1a.addChild(this.antennae_right1b);
        this.head.addChild(this.jaw_right1a);
        this.thorax_main.addChild(this.head);
        this.thorax_main.addChild(this.leg_right_mid1a);
        this.leg_right_back1a.addChild(this.leg_right_back1b);
        this.head.addChild(this.jaw_left1a);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.thorax_main.render(f5);
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
