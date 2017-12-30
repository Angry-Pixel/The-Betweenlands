package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Created by Bart on 21-6-2015.
 */
public class ModelMudFlowerPot extends ModelBase {
    public ModelRenderer pot_base;
    public ModelRenderer pot;
    public ModelRenderer edge1;
    public ModelRenderer pot_base2;
    public ModelRenderer edge2;
    public ModelRenderer edge3;
    public ModelRenderer edge4;

    public ModelMudFlowerPot() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.edge4 = new ModelRenderer(this, 11, 26);
        this.edge4.setRotationPoint(0.0F, 18.0F, 0.0F);
        this.edge4.addBox(1.5F, -1.0F, -2.5F, 1, 1, 4, 0.0F);
        this.pot = new ModelRenderer(this, 0, 14);
        this.pot.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.pot.addBox(-2.5F, -3.0F, -2.5F, 5, 3, 5, 0.0F);
        this.edge2 = new ModelRenderer(this, 0, 26);
        this.edge2.setRotationPoint(0.0F, 18.0F, 0.0F);
        this.edge2.addBox(-2.5F, -1.0F, -1.5F, 1, 1, 4, 0.0F);
        this.edge3 = new ModelRenderer(this, 11, 23);
        this.edge3.setRotationPoint(0.0F, 18.0F, 0.0F);
        this.edge3.addBox(-1.5F, -1.0F, 1.5F, 4, 1, 1, 0.0F);
        this.pot_base = new ModelRenderer(this, 0, 0);
        this.pot_base.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.pot_base.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.pot_base2 = new ModelRenderer(this, 0, 9);
        this.pot_base2.setRotationPoint(0.0F, 22.0F, 0.0F);
        this.pot_base2.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
        this.edge1 = new ModelRenderer(this, 0, 23);
        this.edge1.setRotationPoint(0.0F, 18.0F, 0.0F);
        this.edge1.addBox(-2.5F, -1.0F, -2.5F, 4, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.edge4.render(f5);
        this.pot.render(f5);
        this.edge2.render(f5);
        this.edge3.render(f5);
        this.pot_base.render(f5);
        this.pot_base2.render(f5);
        this.edge1.render(f5);
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