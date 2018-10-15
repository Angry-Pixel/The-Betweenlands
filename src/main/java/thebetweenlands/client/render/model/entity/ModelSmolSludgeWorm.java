package thebetweenlands.client.render.model.entity;



import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelSmolSludgeWorm extends ModelBase {


    
	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale, entity);
	
	}
	
	public void renderBody(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale);
		
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
	}
/*
    @Override
    public void render(Entity entity, float yaw, float partialTick, float f2, float f3, float f4, float f5) { 
    	EntitySludgeWorm worm = (EntitySludgeWorm) entity;
    	Part1.rotateAngleY = (float)Math.toRadians(yaw);
    	Part2.rotateAngleY = (float)Math.toRadians(worm.sludge_worm_2.rotationYaw);
    	Part3.rotateAngleY = (float)Math.toRadians(worm.sludge_worm_3.rotationYaw);
    	Part4.rotateAngleY = (float)Math.toRadians(worm.sludge_worm_4.rotationYaw);
    	Part5.rotateAngleY = (float)Math.toRadians(worm.sludge_worm_5.rotationYaw);
    	
    	Part2.rotationPointX = (float) (worm.sludge_worm_2.posX - worm.sludge_worm_1.posX) * 25;
    	Part2.rotationPointZ = (float) (worm.sludge_worm_2.posZ - worm.sludge_worm_1.posZ) * -25;
    	
    	Part3.rotationPointX = (float) (worm.sludge_worm_3.posX - worm.sludge_worm_1.posX) * 25;
    	Part3.rotationPointZ = (float) (worm.sludge_worm_3.posZ - worm.sludge_worm_1.posZ) * -25;

    	Part4.rotationPointX = (float) (worm.sludge_worm_4.posX - worm.sludge_worm_1.posX) * 22;
    	Part4.rotationPointZ = (float) (worm.sludge_worm_4.posZ - worm.sludge_worm_1.posZ) * -22;

    	
    	float p1actualX =  (float) ((worm.sludge_worm_1.lastTickPosX + (worm.sludge_worm_1.posX - worm.sludge_worm_1.lastTickPosX) * partialTick));
    	float p1actualZ =  (float) ((worm.sludge_worm_1.lastTickPosZ + (worm.sludge_worm_1.posZ - worm.sludge_worm_1.lastTickPosZ) * partialTick));
    	
    	float p5actualX =  (float) ((worm.sludge_worm_5.lastTickPosX + (worm.sludge_worm_5.posX - worm.sludge_worm_5.lastTickPosX) * partialTick));
    	float p5actualZ =  (float) ((worm.sludge_worm_5.lastTickPosZ + (worm.sludge_worm_5.posZ - worm.sludge_worm_5.lastTickPosZ) * partialTick));
    	
    	Part5.rotationPointX = (float) (p5actualX - worm.posX);
    	Part5.rotationPointZ = (float) (p5actualZ - worm.posZ);
    	
    	Part5.rotationPointX *= 22;
    	Part5.rotationPointZ *= -22;

    	setLivingAnimations((EntityLivingBase) entity,0,0,partialTick);
    	Part1.render(f5);
    	Part2.render(f5);
        Part3.render(f5);
        Part4.render(f5);
        Part5.render(f5);
        
    }
    
	@Override
	public void setLivingAnimations(EntityLivingBase entity, float par2, float par3, float partialTick) {
		setLivingAnimations((EntitySludgeWorm) entity, par2, par3, partialTick);
	}

    private void setLivingAnimations(EntitySludgeWorm worm, float par2, float par3, float PartialTick)  {
    	if (worm.hurtResistantTime > 0){
    		pupilR.rotateAngleZ = ((worm.ticksExisted * 50) + (PartialTick * 50)) * 0.0174F; 
    		pupilL.rotateAngleZ = ((worm.ticksExisted * 50) + (PartialTick * 50)) * 0.0174F;
    	} else {
    		pupilR.rotateAngleZ = 0; 
    		pupilL.rotateAngleZ = 0;
    	}
    }
*/
}
