package thebetweenlands.client.model.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelPeatMummy extends ModelBase {
    public ModelRenderer body_base;
    public ModelRenderer shoulderright;
    public ModelRenderer shoulderleft;
    public ModelRenderer neck;
    public ModelRenderer armright;
    public ModelRenderer armleft;
    public ModelRenderer sexybutt;
    public ModelRenderer head1;
    public ModelRenderer head2;
    public ModelRenderer jaw;
    public ModelRenderer teeth2;
    public ModelRenderer hair;
    public ModelRenderer teeth1;
    public ModelRenderer cheecktissueright;
    public ModelRenderer cheecktissue2;
    public ModelRenderer armright2;
    public ModelRenderer armleft2;
    public ModelRenderer legright;
    public ModelRenderer legleft;
    public ModelRenderer legright2;
    public ModelRenderer legleft2;

    public ModelPeatMummy() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.legright2 = new ModelRenderer(this, 99, 12);
        this.legright2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.legright2.addBox(-2.0F, 0.0F, -1.0F, 2, 10, 2, 0.0F);
        this.setRotateAngle(legright2, 1.2292353921796064F, 0.0F, 0.0F);
        this.shoulderright = new ModelRenderer(this, 0, 17);
        this.shoulderright.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shoulderright.addBox(-4.8F, -5.0F, -3.0F, 5, 6, 7, 0.0F);
        this.setRotateAngle(shoulderright, -0.091106186954104F, 0.091106186954104F, 0.0F);
        this.armleft = new ModelRenderer(this, 19, 32);
        this.armleft.setRotationPoint(4.0F, -2.0F, 2.2F);
        this.armleft.addBox(0.0F, -1.0F, -1.5F, 2, 10, 2, 0.0F);
        this.setRotateAngle(armleft, -0.9560913642424937F, -0.136659280431156F, -0.8196066167365371F);
        this.teeth1 = new ModelRenderer(this, 82, 30);
        this.teeth1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.teeth1.addBox(-2.5F, -2.0F, -7.7F, 5, 1, 6, 0.0F);
        this.body_base = new ModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 10.0F, -4.0F);
        this.body_base.addBox(-4.0F, 0.0F, -2.2F, 8, 10, 6, 0.0F);
        this.setRotateAngle(body_base, 1.3203415791337103F, 0.0F, 0.0F);
        this.neck = new ModelRenderer(this, 55, 0);
        this.neck.setRotationPoint(0.0F, -4.4F, 3.0F);
        this.neck.addBox(-1.5F, -3.8F, -1.5F, 3, 4, 3, 0.0F);
        this.setRotateAngle(neck, -0.40980330836826856F, 0.0F, 0.0F);
        this.head1 = new ModelRenderer(this, 55, 9);
        this.head1.setRotationPoint(0.0F, -2.6F, 0.0F);
        this.head1.addBox(-4.0F, -5.0F, -8.0F, 8, 5, 8, 0.0F);
        this.setRotateAngle(head1, -0.9105382707654417F, 0.0F, 0.0F);
        this.hair = new ModelRenderer(this, 40, 42);
        this.hair.setRotationPoint(0.0F, -0.0F, 0.0F);
        this.hair.addBox(-4.5F, -5.1F, -8.5F, 9, 12, 9, 0.0F);
        this.armright = new ModelRenderer(this, 0, 32);
        this.armright.setRotationPoint(-4.0F, -2.0F, 2.2F);
        this.armright.addBox(-2.0F, -1.0F, -1.5F, 2, 10, 2, 0.0F);
        this.setRotateAngle(armright, -0.9560913642424937F, 0.136659280431156F, 0.8196066167365371F);
        this.cheecktissue2 = new ModelRenderer(this, 92, 34);
        this.cheecktissue2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cheecktissue2.addBox(2.8F, -4.9F, -6.0F, 0, 5, 4, 0.0F);
        this.setRotateAngle(cheecktissue2, -0.18203784098300857F, 0.0F, 0.091106186954104F);
        this.cheecktissueright = new ModelRenderer(this, 82, 34);
        this.cheecktissueright.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cheecktissueright.addBox(-2.4F, -4.3F, -7.0F, 0, 5, 4, 0.0F);
        this.setRotateAngle(cheecktissueright, -0.27314402793711257F, 0.0F, -0.27314402793711257F);
        this.shoulderleft = new ModelRenderer(this, 25, 17);
        this.shoulderleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shoulderleft.addBox(-0.2F, -5.0F, -3.0F, 5, 6, 7, 0.0F);
        this.setRotateAngle(shoulderleft, -0.091106186954104F, -0.091106186954104F, 0.0F);
        this.legleft2 = new ModelRenderer(this, 117, 12);
        this.legleft2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.legleft2.addBox(0.0F, 0.0F, -1.0F, 2, 10, 2, 0.0F);
        this.setRotateAngle(legleft2, 1.2292353921796064F, 0.0F, 0.0F);
        this.jaw = new ModelRenderer(this, 55, 30);
        this.jaw.setRotationPoint(0.0F, 1.1F, 0.0F);
        this.jaw.addBox(-3.0F, -1.0F, -8.0F, 6, 2, 7, 0.0F);
        this.setRotateAngle(jaw, 0.5918411493512771F, 0.0F, 0.18203784098300857F);
        this.legleft = new ModelRenderer(this, 108, 12);
        this.legleft.setRotationPoint(4.0F, 3.5F, 1.5F);
        this.legleft.addBox(0.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F);
        this.setRotateAngle(legleft, -1.1838568316277536F, -1.1383037381507017F, 0.091106186954104F);
        this.armright2 = new ModelRenderer(this, 9, 32);
        this.armright2.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.armright2.addBox(-2.0F, 0.0F, -1.5F, 2, 10, 2, 0.0F);
        this.setRotateAngle(armright2, -0.5918411493512771F, 0.0F, 0.0F);
        this.legright = new ModelRenderer(this, 90, 12);
        this.legright.setRotationPoint(-4.0F, 3.5F, 1.5F);
        this.legright.addBox(-2.0F, -1.0F, -1.0F, 2, 9, 2, 0.0F);
        this.setRotateAngle(legright, -1.1838568316277536F, 1.1383037381507017F, -0.091106186954104F);
        this.armleft2 = new ModelRenderer(this, 28, 32);
        this.armleft2.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.armleft2.addBox(0.0F, 0.0F, -1.5F, 2, 10, 2, 0.0F);
        this.setRotateAngle(armleft2, -0.5918411493512771F, 0.0F, 0.0F);
        this.teeth2 = new ModelRenderer(this, 82, 44);
        this.teeth2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.teeth2.addBox(-3.0F, 0.0F, -7.8F, 6, 1, 5, 0.0F);
        this.sexybutt = new ModelRenderer(this, 90, 0);
        this.sexybutt.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.sexybutt.addBox(-4.5F, -1.4F, -2.0F, 9, 5, 6, 0.0F);
        this.setRotateAngle(sexybutt, -0.5462880558742251F, 0.0F, 0.0F);
        this.head2 = new ModelRenderer(this, 55, 23);
        this.head2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head2.addBox(-3.5F, 0.0F, -3.0F, 7, 3, 3, 0.0F);
        this.legright.addChild(this.legright2);
        this.body_base.addChild(this.shoulderright);
        this.body_base.addChild(this.armleft);
        this.jaw.addChild(this.teeth1);
        this.body_base.addChild(this.neck);
        this.neck.addChild(this.head1);
        this.head1.addChild(this.hair);
        this.body_base.addChild(this.armright);
        this.jaw.addChild(this.cheecktissue2);
        this.jaw.addChild(this.cheecktissueright);
        this.body_base.addChild(this.shoulderleft);
        this.legleft.addChild(this.legleft2);
        this.head1.addChild(this.jaw);
        this.sexybutt.addChild(this.legleft);
        this.armright.addChild(this.armright2);
        this.sexybutt.addChild(this.legright);
        this.armleft.addChild(this.armleft2);
        this.head1.addChild(this.teeth2);
        this.body_base.addChild(this.sexybutt);
        this.head1.addChild(this.head2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.body_base.render(f5);
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
