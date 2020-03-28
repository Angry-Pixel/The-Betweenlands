package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.draeton.EntityDraeton;

/**
 * BLDraetonAddonFurnace - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelDraetonUpgradeFurnace extends ModelBase {
    public ModelRenderer furnace_mainrotation;
    public ModelRenderer furnace_base;
    public ModelRenderer support1a;
    public ModelRenderer support2a;
    public ModelRenderer furnace_topside1;
    public ModelRenderer furnace_topside2;
    public ModelRenderer burnhatch;
    public ModelRenderer support1b;
    public ModelRenderer support2b;
    public ModelRenderer cornerpiece1;
    public ModelRenderer connection1;
    public ModelRenderer furnace_top1;
    public ModelRenderer cornerpiece2;
    public ModelRenderer connection2;
    public ModelRenderer poker1;
    public ModelRenderer furnace_backplate;
    public ModelRenderer poker2;

    public ModelDraetonUpgradeFurnace() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.connection2 = new ModelRenderer(this, 22, 17);
        this.connection2.setRotationPoint(-5.0F, -2.0F, -2.0F);
        this.connection2.addBox(-1.0F, -1.0F, -1.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(connection2, 0.0F, 0.22759093446006054F, 0.0F);
        this.connection1 = new ModelRenderer(this, 22, 13);
        this.connection1.setRotationPoint(-5.0F, -2.0F, 2.0F);
        this.connection1.addBox(-1.0F, -1.0F, 0.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(connection1, 0.0F, -0.22759093446006054F, 0.0F);
        this.support2a = new ModelRenderer(this, 33, 10);
        this.support2a.setRotationPoint(5.0F, 3.0F, -2.99F);
        this.support2a.addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F);
        this.setRotateAngle(support2a, 0.0F, 0.0F, -0.091106186954104F);
        this.furnace_topside1 = new ModelRenderer(this, 0, 13);
        this.furnace_topside1.setRotationPoint(6.0F, 0.0F, 1.99F);
        this.furnace_topside1.addBox(-6.0F, -4.0F, 0.0F, 6, 4, 2, 0.0F);
        this.setRotateAngle(furnace_topside1, 0.0F, 0.0F, -0.091106186954104F);
        this.furnace_mainrotation = new ModelRenderer(this, 0, 0);
        this.furnace_mainrotation.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.furnace_mainrotation.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.support2b = new ModelRenderer(this, 33, 15);
        this.support2b.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.support2b.addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F);
        this.setRotateAngle(support2b, 0.0F, 0.0F, -0.40980330836826856F);
        this.poker2 = new ModelRenderer(this, 27, 21);
        this.poker2.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.poker2.addBox(-0.5F, 0.0F, -1.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(poker2, -0.31869712141416456F, 0.0F, 0.0F);
        this.cornerpiece1 = new ModelRenderer(this, 17, 13);
        this.cornerpiece1.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.cornerpiece1.addBox(-1.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F);
        this.furnace_backplate = new ModelRenderer(this, 0, 23);
        this.furnace_backplate.setRotationPoint(-5.8F, 0.0F, 0.0F);
        this.furnace_backplate.addBox(0.0F, 0.0F, -2.0F, 0, 4, 4, 0.0F);
        this.support1b = new ModelRenderer(this, 33, 5);
        this.support1b.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.support1b.addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F);
        this.setRotateAngle(support1b, 0.0F, 0.0F, -0.40980330836826856F);
        this.furnace_top1 = new ModelRenderer(this, 34, 23);
        this.furnace_top1.setRotationPoint(0.0F, -4.0F, 2.0F);
        this.furnace_top1.addBox(-6.0F, -2.0F, -3.0F, 6, 2, 6, 0.0F);
        this.burnhatch = new ModelRenderer(this, 11, 22);
        this.burnhatch.setRotationPoint(6.0F, 4.0F, 0.0F);
        this.burnhatch.addBox(-0.5F, -3.0F, -3.0F, 1, 3, 6, 0.0F);
        this.setRotateAngle(burnhatch, 0.0F, 0.0F, 0.091106186954104F);
        this.support1a = new ModelRenderer(this, 33, 0);
        this.support1a.setRotationPoint(5.0F, 3.0F, 2.99F);
        this.support1a.addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F);
        this.setRotateAngle(support1a, 0.0F, 0.0F, -0.091106186954104F);
        this.cornerpiece2 = new ModelRenderer(this, 17, 16);
        this.cornerpiece2.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.cornerpiece2.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.furnace_base = new ModelRenderer(this, 0, 0);
        this.furnace_base.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.furnace_base.addBox(-2.0F, 0.0F, -4.0F, 8, 4, 8, 0.0F);
        this.setRotateAngle(furnace_base, 0.0F, 0.0F, 0.045553093477052F);
        this.furnace_topside2 = new ModelRenderer(this, 0, 20);
        this.furnace_topside2.setRotationPoint(6.0F, 0.0F, -1.99F);
        this.furnace_topside2.addBox(-6.0F, -4.0F, -2.0F, 6, 4, 2, 0.0F);
        this.setRotateAngle(furnace_topside2, 0.0F, 0.0F, -0.091106186954104F);
        this.poker1 = new ModelRenderer(this, 27, 13);
        this.poker1.setRotationPoint(-3.0F, -1.5F, -2.0F);
        this.poker1.addBox(-0.5F, -2.0F, -1.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(poker1, 0.0F, 0.31869712141416456F, 0.0F);
        this.furnace_topside2.addChild(this.connection2);
        this.furnace_topside1.addChild(this.connection1);
        this.furnace_base.addChild(this.support2a);
        this.furnace_base.addChild(this.furnace_topside1);
        this.support2a.addChild(this.support2b);
        this.poker1.addChild(this.poker2);
        this.furnace_topside1.addChild(this.cornerpiece1);
        this.furnace_top1.addChild(this.furnace_backplate);
        this.support1a.addChild(this.support1b);
        this.furnace_topside2.addChild(this.furnace_top1);
        this.furnace_base.addChild(this.burnhatch);
        this.furnace_base.addChild(this.support1a);
        this.furnace_topside2.addChild(this.cornerpiece2);
        this.furnace_mainrotation.addChild(this.furnace_base);
        this.furnace_base.addChild(this.furnace_topside2);
        this.furnace_topside2.addChild(this.poker1);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) { 
    	if(entityIn instanceof EntityDraeton) {
    		float roll = (float)Math.toRadians(((EntityDraeton) entityIn).upgradeCounterRoll);
    		
    		this.poker1.rotateAngleZ = -roll;
    	} else {
    		this.poker1.rotateAngleZ = 0;
    	}
    	this.furnace_mainrotation.render(scale);
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
