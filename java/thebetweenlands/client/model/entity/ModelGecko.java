package thebetweenlands.client.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLGecko - TripleHeadedSheep
 * Created using Tabula 4.1.1
 */
public class ModelGecko extends ModelBase {
    public ModelRenderer body_base;
    public ModelRenderer head;
    public ModelRenderer legleft_f1;
    public ModelRenderer legright_f1;
    public ModelRenderer legleft_b1;
    public ModelRenderer legright_b1;
    public ModelRenderer tail1;
    public ModelRenderer tail2;
    public ModelRenderer tail3;
    public ModelRenderer crane;
    public ModelRenderer cute_little_tongue_so_kawaii;
    public ModelRenderer legleft_f2;
    public ModelRenderer legright_f2;
    public ModelRenderer legleft_b2;
    public ModelRenderer legright_b2;

    public ModelGecko() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.crane = new ModelRenderer(this, 19, 7);
        this.crane.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.crane.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 4, 0.0F);
        this.setRotateAngle(crane, -0.136659280431156F, 0.0F, 0.0F);
        this.legright_b1 = new ModelRenderer(this, 34, 13);
        this.legright_b1.setRotationPoint(-2.0F, 22.0F, 0.5F);
        this.legright_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(legright_b1, 0.6373942428283291F, 0.31869712141416456F, 0.6829473363053812F);
        this.legright_f1 = new ModelRenderer(this, 34, 4);
        this.legright_f1.setRotationPoint(-2.0F, 21.5F, -3.0F);
        this.legright_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(legright_f1, 0.36425021489121656F, -0.18203784098300857F, 0.40980330836826856F);
        this.legleft_f2 = new ModelRenderer(this, 39, 0);
        this.legleft_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
        this.legleft_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
        this.setRotateAngle(legleft_f2, 0.8196066167365371F, 0.0F, 0.0F);
        this.legleft_b2 = new ModelRenderer(this, 39, 8);
        this.legleft_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
        this.legleft_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legleft_b2, 0.6373942428283291F, 0.0F, 0.0F);
        this.cute_little_tongue_so_kawaii = new ModelRenderer(this, 19, 12);
        this.cute_little_tongue_so_kawaii.setRotationPoint(0.0F, -1.0F, -4.0F);
        this.cute_little_tongue_so_kawaii.addBox(-0.5F, 0.0F, -1.0F, 1, 0, 2, 0.0F);
        this.setRotateAngle(cute_little_tongue_so_kawaii, 0.36425021489121656F, 0.0F, 0.0F);
        this.legright_f2 = new ModelRenderer(this, 39, 4);
        this.legright_f2.setRotationPoint(0.0F, 1.5F, -0.5F);
        this.legright_f2.addBox(-0.5F, -0.2F, -1.5F, 1, 1, 2, 0.0F);
        this.setRotateAngle(legright_f2, 0.8196066167365371F, 0.0F, 0.0F);
        this.tail1 = new ModelRenderer(this, 0, 8);
        this.tail1.setRotationPoint(0.0F, 0.0F, 5.0F);
        this.tail1.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3, 0.0F);
        this.setRotateAngle(tail1, 0.27314402793711257F, 0.0F, 0.0F);
        this.legleft_f1 = new ModelRenderer(this, 34, 0);
        this.legleft_f1.setRotationPoint(2.0F, 21.5F, -3.0F);
        this.legleft_f1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(legleft_f1, 0.36425021489121656F, 0.18203784098300857F, -0.40980330836826856F);
        this.head = new ModelRenderer(this, 19, 0);
        this.head.setRotationPoint(0.0F, 21.5F, -3.0F);
        this.head.addBox(-1.5F, -2.0F, -4.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(head, 0.22759093446006054F, 0.0F, 0.0F);
        this.legright_b2 = new ModelRenderer(this, 39, 13);
        this.legright_b2.setRotationPoint(0.0F, 1.5F, 0.0F);
        this.legright_b2.addBox(-0.5F, -0.2F, -0.4F, 1, 3, 1, 0.0F);
        this.setRotateAngle(legright_b2, 0.6373942428283291F, 0.0F, 0.0F);
        this.body_base = new ModelRenderer(this, 0, 0);
        this.body_base.setRotationPoint(0.0F, 22.0F, -4.0F);
        this.body_base.addBox(-2.0F, -2.0F, 0.0F, 4, 2, 5, 0.0F);
        this.setRotateAngle(body_base, -0.091106186954104F, 0.0F, 0.0F);
        this.tail3 = new ModelRenderer(this, 0, 20);
        this.tail3.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.tail3.addBox(-0.5F, -2.0F, 0.0F, 1, 3, 3, 0.0F);
        this.setRotateAngle(tail3, 0.27314402793711257F, 0.0F, 0.0F);
        this.legleft_b1 = new ModelRenderer(this, 34, 8);
        this.legleft_b1.setRotationPoint(2.0F, 22.0F, 0.5F);
        this.legleft_b1.addBox(-0.5F, -0.5F, -0.5F, 1, 2, 1, 0.0F);
        this.setRotateAngle(legleft_b1, 0.6373942428283291F, -0.31869712141416456F, -0.6829473363053812F);
        this.tail2 = new ModelRenderer(this, 0, 14);
        this.tail2.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.tail2.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        this.setRotateAngle(tail2, 0.27314402793711257F, 0.0F, 0.0F);
        this.head.addChild(this.crane);
        this.legleft_f1.addChild(this.legleft_f2);
        this.legleft_b1.addChild(this.legleft_b2);
        this.head.addChild(this.cute_little_tongue_so_kawaii);
        this.legright_f1.addChild(this.legright_f2);
        this.body_base.addChild(this.tail1);
        this.legright_b1.addChild(this.legright_b2);
        this.tail2.addChild(this.tail3);
        this.tail1.addChild(this.tail2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.legright_b1.render(f5);
        this.legright_f1.render(f5);
        this.legleft_f1.render(f5);
        this.head.render(f5);
        this.body_base.render(f5);
        this.legleft_b1.render(f5);
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
