package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWoodSupportBeam1 extends ModelBase {
    public ModelRenderer beampart1;
    public ModelRenderer beampart2;
    public ModelRenderer beampart3;
    public ModelRenderer ropeboi;

    public ModelWoodSupportBeam1() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.ropeboi = new ModelRenderer(this, 25, 0);
        this.ropeboi.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.ropeboi.addBox(-3.5F, -1.0F, -6.5F, 7, 2, 7, 0.0F);
        this.beampart1 = new ModelRenderer(this, 0, 0);
        this.beampart1.setRotationPoint(0.0F, 24.0F, 8.0F);
        this.beampart1.addBox(-3.0F, -8.0F, 0.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart1, 0.5462880558742251F, 0.0F, 0.0F);
        this.beampart2 = new ModelRenderer(this, 0, 15);
        this.beampart2.setRotationPoint(0.0F, -8.0F, 6.0F);
        this.beampart2.addBox(-3.05F, -10.0F, -6.0F, 6, 10, 6, 0.0F);
        this.setRotateAngle(beampart2, 0.36425021489121656F, 0.0F, 0.0F);
        this.beampart3 = new ModelRenderer(this, 0, 32);
        this.beampart3.setRotationPoint(0.0F, -10.0F, 0.0F);
        this.beampart3.addBox(-3.0F, -8.0F, -6.0F, 6, 8, 6, 0.0F);
        this.setRotateAngle(beampart3, 0.36425021489121656F, 0.0F, 0.0F);
        this.beampart3.addChild(this.ropeboi);
        this.beampart1.addChild(this.beampart2);
        this.beampart2.addChild(this.beampart3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.beampart1.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
