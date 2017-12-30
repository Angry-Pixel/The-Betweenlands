package thebetweenlands.client.render.model.entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;

@SideOnly(Side.CLIENT)
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

    public void render(float unitPixel) {
        slime1.render(unitPixel);
        slime2.render(unitPixel);
        slime3.render(unitPixel);
    }
}
