package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelPebblePile1 extends ModelBase {
    public ModelRenderer pebble_1;
   
    public ModelPebblePile1() {
        this.textureWidth = 32;
        this.textureHeight = 32;

        this.pebble_1 = new ModelRenderer(this, 0, 0);
        this.pebble_1.setRotationPoint(-0.6F, 24.0F, 0.0F);
        this.pebble_1.addBox(-2.5F, -2.0F, -2.0F, 5, 2, 4, 0.0F);
        this.setRotateAngle(pebble_1, 0.0F, 0.5235987755982988F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.pebble_1.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
