package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelPebblePile3Plants extends ModelBase {
    public ModelRenderer pebble_1;
    public ModelRenderer pebble_2;
    public ModelRenderer pebble_3;
    public ModelRenderer plant_1_a;
    public ModelRenderer plant_2_a;
    public ModelRenderer plant_3_a;
    public ModelRenderer plant_4_a;
    public ModelRenderer plant_1_b;
    public ModelRenderer plant_2_b;
    public ModelRenderer plant_3_b;
    public ModelRenderer plant_4_b;

    public ModelPebblePile3Plants() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.plant_1_b = new ModelRenderer(this, 26, 0);
        this.plant_1_b.setRotationPoint(-3.0F, 24.0F, 5.0F);
        this.plant_1_b.addBox(-1.5F, -5.0F, 0.0F, 3, 5, 0, 0.0F);
        this.setRotateAngle(plant_1_b, 0.0F, -0.7853981633974483F, 0.0F);
        this.plant_4_b = new ModelRenderer(this, 24, 20);
        this.plant_4_b.setRotationPoint(0.0F, 24.0F, -4.0F);
        this.plant_4_b.addBox(-1.0F, -5.0F, -0.0F, 2, 5, 0, 0.0F);
        this.setRotateAngle(plant_4_b, 0.0F, -0.7853981633974483F, 0.0F);
        this.plant_3_b = new ModelRenderer(this, 22, 16);
        this.plant_3_b.setRotationPoint(-5.0F, 24.0F, 0.0F);
        this.plant_3_b.addBox(-0.5F, -3.0F, 0.0F, 1, 3, 0, 0.0F);
        this.setRotateAngle(plant_3_b, 0.0F, -0.7853981633974483F, 0.0F);
        this.plant_2_a = new ModelRenderer(this, 19, 6);
        this.plant_2_a.setRotationPoint(4.5F, 24.0F, -0.5F);
        this.plant_2_a.addBox(-1.5F, -9.0F, 0.0F, 3, 9, 0, 0.0F);
        this.setRotateAngle(plant_2_a, 0.0F, 0.7853981633974483F, 0.0F);
        this.plant_4_a = new ModelRenderer(this, 19, 20);
        this.plant_4_a.setRotationPoint(0.0F, 24.0F, -4.0F);
        this.plant_4_a.addBox(-1.0F, -5.0F, -0.0F, 2, 5, 0, 0.0F);
        this.setRotateAngle(plant_4_a, 0.0F, 0.7853981633974483F, 0.0F);
        this.pebble_2 = new ModelRenderer(this, 0, 7);
        this.pebble_2.setRotationPoint(-4.0F, 24.0F, -4.0F);
        this.pebble_2.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(pebble_2, 0.18203784098300857F, -0.5462880558742251F, 0.0F);
        this.plant_2_b = new ModelRenderer(this, 26, 6);
        this.plant_2_b.setRotationPoint(4.5F, 24.0F, -0.5F);
        this.plant_2_b.addBox(-1.5F, -9.0F, 0.0F, 3, 9, 0, 0.0F);
        this.setRotateAngle(plant_2_b, 0.0F, -0.7853981633974483F, 0.0F);
        this.pebble_3 = new ModelRenderer(this, 0, 14);
        this.pebble_3.setRotationPoint(2.0F, 23.0F, 3.0F);
        this.pebble_3.addBox(-2.5F, -2.0F, -2.0F, 5, 2, 4, 0.0F);
        this.setRotateAngle(pebble_3, -0.5235987755982988F, -0.7853981633974483F, 0.6108652381980153F);
        this.plant_1_a = new ModelRenderer(this, 19, 0);
        this.plant_1_a.setRotationPoint(-3.0F, 24.0F, 5.0F);
        this.plant_1_a.addBox(-1.5F, -5.0F, 0.0F, 3, 5, 0, 0.0F);
        this.setRotateAngle(plant_1_a, 0.0F, 0.7853981633974483F, 0.0F);
        this.plant_3_a = new ModelRenderer(this, 19, 16);
        this.plant_3_a.setRotationPoint(-5.0F, 24.0F, 0.0F);
        this.plant_3_a.addBox(-0.5F, -3.0F, 0.0F, 1, 3, 0, 0.0F);
        this.setRotateAngle(plant_3_a, 0.0F, 0.7853981633974483F, 0.0F);
        this.pebble_1 = new ModelRenderer(this, 0, 0);
        this.pebble_1.setRotationPoint(-0.6F, 24.0F, 0.0F);
        this.pebble_1.addBox(-2.5F, -2.0F, -2.0F, 5, 2, 4, 0.0F);
        this.setRotateAngle(pebble_1, 0.0F, 0.5235987755982988F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.plant_1_b.render(f5);
        this.plant_4_b.render(f5);
        this.plant_3_b.render(f5);
        this.plant_2_a.render(f5);
        this.plant_4_a.render(f5);
        this.pebble_2.render(f5);
        this.plant_2_b.render(f5);
        this.pebble_3.render(f5);
        this.plant_1_a.render(f5);
        this.plant_3_a.render(f5);
        this.pebble_1.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
