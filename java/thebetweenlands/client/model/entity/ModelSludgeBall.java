package thebetweenlands.client.model.entity;

import net.minecraft.entity.Entity;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.client.model.MowzieModelRenderer;

/**
 * Created by jnad325 on 2/28/16.
 */
public class ModelSludgeBall extends MowzieModelBase {
    MowzieModelRenderer slime1;
    MowzieModelRenderer slime2;
    MowzieModelRenderer slime3;

    public ModelSludgeBall() {
        textureWidth = 128;
        textureHeight = 64;

        this.slime2 = new MowzieModelRenderer(this, 40, 32);
        this.slime2.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.slime2.addBox(-7.0F, -9.0F, -7.0F, 14, 2, 14, 0.0F);
        this.slime3 = new MowzieModelRenderer(this, 40, 48);
        this.slime3.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.slime3.addBox(-7.0F, 7.0F, -7.0F, 14, 2, 14, 0.0F);
        this.slime1 = new MowzieModelRenderer(this, 40, 0);
        this.slime1.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.slime1.addBox(-9.0F, -7.0F, -9.0F, 18, 14, 18, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        slime1.render(f5);
        slime2.render(f5);
        slime3.render(f5);
    }
}
