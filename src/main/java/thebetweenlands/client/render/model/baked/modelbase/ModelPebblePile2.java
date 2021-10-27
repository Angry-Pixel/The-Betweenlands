package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelPebblePile2 extends ModelBase {
    public ModelRenderer pebble_1;
    public ModelRenderer pebble_2;
   
    public ModelPebblePile2() {
        this.textureWidth = 32;
        this.textureHeight = 32;

        this.pebble_2 = new ModelRenderer(this, 0, 7);
        this.pebble_2.setRotationPoint(-4.0F, 24.0F, -4.0F);
        this.pebble_2.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(pebble_2, 0.18203784098300857F, -0.5462880558742251F, 0.0F);
        this.pebble_1 = new ModelRenderer(this, 0, 0);
        this.pebble_1.setRotationPoint(-0.6F, 24.0F, 0.0F);
        this.pebble_1.addBox(-2.5F, -2.0F, -2.0F, 5, 2, 4, 0.0F);
        this.setRotateAngle(pebble_1, 0.0F, 0.5235987755982988F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.pebble_2.render(f5);
        this.pebble_1.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
