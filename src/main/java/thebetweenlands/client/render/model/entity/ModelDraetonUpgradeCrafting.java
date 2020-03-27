package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.draeton.EntityDraeton;

/**
 * BLDraetonAddonCrafting - Undefined
 * Created using Tabula 7.0.1
 */
public class ModelDraetonUpgradeCrafting extends ModelBase {
    public ModelRenderer crafting_mainrotation;
    public ModelRenderer craftingbench1;
    public ModelRenderer support1a;
    public ModelRenderer support2a;
    public ModelRenderer sawhandle;
    public ModelRenderer hammerhandle;
    public ModelRenderer thonghandle1;
    public ModelRenderer support1b;
    public ModelRenderer support1c;
    public ModelRenderer support2b;
    public ModelRenderer support2c;
    public ModelRenderer sawblade;
    public ModelRenderer hammerhead;
    public ModelRenderer thonghandle2;
    public ModelRenderer thonghead1;
    public ModelRenderer thonghead2;

    public ModelDraetonUpgradeCrafting() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.support1b = new ModelRenderer(this, 0, 18);
        this.support1b.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.support1b.addBox(-4.0F, 0.0F, -0.99F, 4, 2, 2, 0.0F);
        this.setRotateAngle(support1b, 0.0F, 0.0F, -0.40980330836826856F);
        this.sawblade = new ModelRenderer(this, 41, 4);
        this.sawblade.setRotationPoint(0.0F, 2.0F, 0.5F);
        this.sawblade.addBox(-1.5F, 0.0F, 0.0F, 3, 6, 0, 0.0F);
        this.craftingbench1 = new ModelRenderer(this, 0, 0);
        this.craftingbench1.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.craftingbench1.addBox(0.0F, 0.0F, -5.0F, 10, 2, 10, 0.0F);
        this.setRotateAngle(craftingbench1, 0.0F, 0.0F, 0.136659280431156F);
        this.thonghead1 = new ModelRenderer(this, 41, 22);
        this.thonghead1.setRotationPoint(-0.5F, 2.5F, -0.5F);
        this.thonghead1.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 0, 0.0F);
        this.thonghead2 = new ModelRenderer(this, 46, 22);
        this.thonghead2.setRotationPoint(0.0F, 0.0F, -0.5F);
        this.thonghead2.addBox(-1.0F, 0.0F, -0.1F, 2, 3, 0, 0.0F);
        this.support1a = new ModelRenderer(this, 0, 13);
        this.support1a.mirror = true;
        this.support1a.setRotationPoint(8.0F, 1.0F, 3.0F);
        this.support1a.addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F);
        this.setRotateAngle(support1a, 0.0F, 0.0F, -0.091106186954104F);
        this.support2c = new ModelRenderer(this, 15, 23);
        this.support2c.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.support2c.addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F);
        this.setRotateAngle(support2c, 0.0F, 0.0F, -0.40980330836826856F);
        this.thonghandle2 = new ModelRenderer(this, 46, 16);
        this.thonghandle2.setRotationPoint(-0.5F, 2.5F, 0.0F);
        this.thonghandle2.addBox(-1.0F, -4.0F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(thonghandle2, 0.0F, 0.0F, -0.091106186954104F);
        this.hammerhead = new ModelRenderer(this, 50, 8);
        this.hammerhead.setRotationPoint(0.5F, 5.0F, -0.5F);
        this.hammerhead.addBox(-1.5F, 0.0F, -1.0F, 3, 2, 2, 0.0F);
        this.sawhandle = new ModelRenderer(this, 41, 0);
        this.sawhandle.setRotationPoint(3.0F, 0.5F, 5.0F);
        this.sawhandle.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 1, 0.0F);
        this.setRotateAngle(sawhandle, 0.0F, 0.045553093477052F, -0.136659280431156F);
        this.thonghandle1 = new ModelRenderer(this, 41, 16);
        this.thonghandle1.setRotationPoint(7.5F, 1.0F, -5.0F);
        this.thonghandle1.addBox(-0.5F, -1.5F, -1.0F, 1, 4, 1, 0.0F);
        this.setRotateAngle(thonghandle1, 0.0F, 0.0F, -0.27314402793711257F);
        this.support2a = new ModelRenderer(this, 15, 13);
        this.support2a.mirror = true;
        this.support2a.setRotationPoint(8.0F, 1.0F, -3.0F);
        this.support2a.addBox(-4.0F, 0.0F, -1.0F, 4, 2, 2, 0.0F);
        this.setRotateAngle(support2a, 0.0F, 0.0F, -0.091106186954104F);
        this.support2b = new ModelRenderer(this, 15, 18);
        this.support2b.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.support2b.addBox(-4.0F, 0.0F, -1.01F, 4, 2, 2, 0.0F);
        this.setRotateAngle(support2b, 0.0F, 0.0F, -0.40980330836826856F);
        this.support1c = new ModelRenderer(this, 0, 23);
        this.support1c.setRotationPoint(-4.0F, 0.0F, 0.0F);
        this.support1c.addBox(-5.0F, 0.0F, -1.0F, 5, 2, 2, 0.0F);
        this.setRotateAngle(support1c, 0.0F, 0.0F, -0.40980330836826856F);
        this.hammerhandle = new ModelRenderer(this, 50, 0);
        this.hammerhandle.setRotationPoint(3.0F, 1.0F, -5.0F);
        this.hammerhandle.addBox(0.0F, -1.0F, -1.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(hammerhandle, 0.0F, -0.18203784098300857F, -0.136659280431156F);
        this.crafting_mainrotation = new ModelRenderer(this, 0, 0);
        this.crafting_mainrotation.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.crafting_mainrotation.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.support1a.addChild(this.support1b);
        this.sawhandle.addChild(this.sawblade);
        this.crafting_mainrotation.addChild(this.craftingbench1);
        this.thonghandle1.addChild(this.thonghead1);
        this.thonghandle2.addChild(this.thonghead2);
        this.craftingbench1.addChild(this.support1a);
        this.support2b.addChild(this.support2c);
        this.thonghandle1.addChild(this.thonghandle2);
        this.hammerhandle.addChild(this.hammerhead);
        this.craftingbench1.addChild(this.sawhandle);
        this.craftingbench1.addChild(this.thonghandle1);
        this.craftingbench1.addChild(this.support2a);
        this.support2a.addChild(this.support2b);
        this.support1b.addChild(this.support1c);
        this.craftingbench1.addChild(this.hammerhandle);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    	float partialTicks = ageInTicks - entityIn.ticksExisted;
    	
    	if(entityIn instanceof EntityDraeton) {
    		EntityDraeton draeton = (EntityDraeton) entityIn;
    		
    		float roll = (float)Math.toRadians(draeton.prevRotationRoll + (draeton.rotationRoll - draeton.prevRotationRoll) * partialTicks);
    		
    		this.hammerhandle.rotateAngleZ = -0.136659280431156F + roll;
    		this.thonghandle1.rotateAngleZ = -0.27314402793711257F + roll;
    		this.sawhandle.rotateAngleZ = -0.136659280431156F + roll;
    	} else {
    		this.hammerhandle.rotateAngleZ = -0.136659280431156F;
    		this.thonghandle1.rotateAngleZ = -0.27314402793711257F;
    		this.sawhandle.rotateAngleZ = -0.136659280431156F;
    	}
        this.crafting_mainrotation.render(scale);
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
