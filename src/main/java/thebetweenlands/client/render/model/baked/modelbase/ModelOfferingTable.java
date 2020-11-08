package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLOfferingTable - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelOfferingTable extends ModelBase {
    public ModelRenderer base_slab;
    public ModelRenderer slab_edge_b1;
    public ModelRenderer slab_edge_f1;
    public ModelRenderer stand_left1a;
    public ModelRenderer stand_right1a;
    public ModelRenderer stand_left1b;
    public ModelRenderer stand_right1b;

    public ModelOfferingTable() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.slab_edge_f1 = new ModelRenderer(this, 0, 14);
        this.slab_edge_f1.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.slab_edge_f1.addBox(-6.01F, -2.0F, -2.0F, 12, 2, 2, 0.0F);
        this.setRotateAngle(slab_edge_f1, -0.4553564018453205F, 0.0F, 0.0F);
        this.stand_left1a = new ModelRenderer(this, 0, 19);
        this.stand_left1a.setRotationPoint(3.0F, 0.0F, 0.0F);
        this.stand_left1a.addBox(0.0F, 0.0F, -3.0F, 2, 2, 6, 0.0F);
        this.setRotateAngle(stand_left1a, 0.0F, 0.0F, -0.4553564018453205F);
        this.stand_right1a = new ModelRenderer(this, 0, 28);
        this.stand_right1a.setRotationPoint(-3.0F, 0.0F, 0.0F);
        this.stand_right1a.addBox(-2.0F, 0.0F, -3.0F, 2, 2, 6, 0.0F);
        this.setRotateAngle(stand_right1a, 0.0F, 0.0F, 0.4553564018453205F);
        this.base_slab = new ModelRenderer(this, 0, 0);
        this.base_slab.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.base_slab.addBox(-6.0F, -2.0F, -3.0F, 12, 2, 6, 0.0F);
        this.slab_edge_b1 = new ModelRenderer(this, 0, 9);
        this.slab_edge_b1.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.slab_edge_b1.addBox(-5.99F, -2.0F, 0.0F, 12, 2, 2, 0.0F);
        this.setRotateAngle(slab_edge_b1, 0.4553564018453205F, 0.0F, 0.0F);
        this.stand_left1b = new ModelRenderer(this, 17, 19);
        this.stand_left1b.setRotationPoint(2.0F, 2.0F, 0.0F);
        this.stand_left1b.addBox(-3.0F, 0.0F, -3.01F, 3, 2, 6, 0.0F);
        this.setRotateAngle(stand_left1b, 0.0F, 0.0F, 0.27314402793711257F);
        this.stand_right1b = new ModelRenderer(this, 17, 28);
        this.stand_right1b.setRotationPoint(-2.0F, 2.0F, 0.0F);
        this.stand_right1b.addBox(0.0F, 0.0F, -3.01F, 3, 2, 6, 0.0F);
        this.setRotateAngle(stand_right1b, 0.0F, 0.0F, -0.27314402793711257F);
        this.base_slab.addChild(this.slab_edge_f1);
        this.base_slab.addChild(this.stand_left1a);
        this.base_slab.addChild(this.stand_right1a);
        this.base_slab.addChild(this.slab_edge_b1);
        this.stand_left1a.addChild(this.stand_left1b);
        this.stand_right1a.addChild(this.stand_right1b);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.base_slab.render(f5);
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
