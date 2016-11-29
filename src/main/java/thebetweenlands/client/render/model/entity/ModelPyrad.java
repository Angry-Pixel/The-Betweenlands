package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityPyrad;

@SideOnly(Side.CLIENT)
public class ModelPyrad extends ModelBase {
	ModelRenderer bodybase;
	ModelRenderer chestpiece_right;
	ModelRenderer chestpiece_left;
	ModelRenderer bodypiece1;
	ModelRenderer headbase;
	ModelRenderer plate_back1;
	ModelRenderer staff1;
	ModelRenderer plate_right1;
	ModelRenderer plate_right2;
	ModelRenderer plate_rightedge;
	ModelRenderer plate_left1;
	ModelRenderer plate_left2;
	ModelRenderer plate_leftedge;
	ModelRenderer headconnectionpiece;
	ModelRenderer snoutpiece;
	ModelRenderer leaf_headright1;
	ModelRenderer leaf_headleft1;
	ModelRenderer lowerjaw_iguess;
	ModelRenderer leaf_headright2;
	ModelRenderer leaf_headleft2;
	ModelRenderer plate_back2;
	ModelRenderer plate_backedge;
	ModelRenderer staff2;
	ModelRenderer staff6;
	ModelRenderer staff3;
	ModelRenderer staff4;
	ModelRenderer staff5;
	ModelRenderer staffleaf1;
	ModelRenderer staffleaf2;

	public ModelPyrad() {
		textureWidth = 128;
		textureHeight = 64;
		bodybase = new ModelRenderer(this, 0, 0);
		bodybase.setRotationPoint(0.0F, 4.0F, -3.0F);
		bodybase.addBox(-5.0F, 0.0F, 0.0F, 10, 6, 7, 0.0F);
		setRotation(bodybase, 0.091106186954104F, 0.0F, 0.0F);
		chestpiece_right = new ModelRenderer(this, 0, 14);
		chestpiece_right.setRotationPoint(0.0F, -6.0F, 0.0F);
		chestpiece_right.addBox(-6.0F, 0.0F, 0.0F, 6, 6, 8, 0.0F);
		setRotation(chestpiece_right, -0.09145525280450287F, 0.09075712110370514F, -0.008377580409572781F);
		staffleaf2 = new ModelRenderer(this, 99, 35);
		staffleaf2.setRotationPoint(-3.0F, 0.0F, 0.0F);
		staffleaf2.addBox(-3.0F, -2.0F, 0.0F, 3, 4, 0, 0.0F);
		setRotation(staffleaf2, 0.0F, 0.9560913642424937F, 0.0F);
		plate_left1 = new ModelRenderer(this, 59, 42);
		plate_left1.setRotationPoint(7.0F, 0.0F, 3.0F);
		plate_left1.addBox(0.0F, -2.0F, -4.0F, 2, 8, 8, 0.0F);
		setRotation(plate_left1, 0.0F, 0.0F, -0.36425021489121656F);
		staff3 = new ModelRenderer(this, 99, 9);
		staff3.setRotationPoint(-2.0F, -6.0F, 0.0F);
		staff3.addBox(0.0F, -4.0F, -1.02F, 2, 4, 2, 0.0F);
		setRotation(staff3, 0.0F, 0.0F, 0.8651597102135892F);
		staff4 = new ModelRenderer(this, 99, 16);
		staff4.setRotationPoint(0.0F, -4.0F, 0.0F);
		staff4.addBox(0.0F, -4.0F, -1.03F, 2, 4, 2, 0.0F);
		setRotation(staff4, 0.0F, 0.0F, 0.8651597102135892F);
		staff6 = new ModelRenderer(this, 108, 0);
		staff6.setRotationPoint(1.0F, 12.0F, 1.0F);
		staff6.addBox(-2.0F, 0.0F, -0.99F, 2, 4, 2, 0.0F);
		setRotation(staff6, 0.0F, 0.0F, 0.4553564018453205F);
		plate_leftedge = new ModelRenderer(this, 97, 48);
		plate_leftedge.setRotationPoint(2.0F, 0.0F, 0.0F);
		plate_leftedge.addBox(0.0F, -2.0F, -4.01F, 2, 2, 8, 0.0F);
		setRotation(plate_leftedge, 0.0F, 0.0F, -0.22759093446006054F);
		chestpiece_left = new ModelRenderer(this, 29, 14);
		chestpiece_left.setRotationPoint(0.0F, -6.0F, 0.0F);
		chestpiece_left.addBox(0.0F, 0.01F, 0.0F, 6, 6, 8, 0.0F);
		setRotation(chestpiece_left, -0.09145525280450287F, -0.09058258817850572F, 0.008377580409572781F);
		lowerjaw_iguess = new ModelRenderer(this, 60, 19);
		lowerjaw_iguess.setRotationPoint(0.0F, 2.0F, -3.0F);
		lowerjaw_iguess.addBox(-3.015F, -3.0F, -7.0F, 6, 3, 7, 0.0F);
		plate_right1 = new ModelRenderer(this, 0, 41);
		plate_right1.setRotationPoint(-7.0F, 0.0F, 3.0F);
		plate_right1.addBox(-2.0F, -2.0F, -4.0F, 2, 8, 8, 0.0F);
		setRotation(plate_right1, 0.0F, 0.0F, 0.36425021489121656F);
		bodypiece1 = new ModelRenderer(this, 0, 29);
		bodypiece1.setRotationPoint(0.0F, 6.0F, 0.0F);
		bodypiece1.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 7, 0.0F);
		setRotation(bodypiece1, 0.136659280431156F, 0.0F, 0.0F);
		leaf_headright2 = new ModelRenderer(this, 42, -3);
		leaf_headright2.setRotationPoint(0.0F, 0.0F, 3.0F);
		leaf_headright2.addBox(0.0F, -2.0F, 0.0F, 0, 4, 3, 0.0F);
		setRotation(leaf_headright2, 0.0F, -0.5009094953223726F, 0.0F);
		staff2 = new ModelRenderer(this, 99, 0);
		staff2.setRotationPoint(1.0F, -12.0F, 1.0F);
		staff2.addBox(-2.0F, -6.0F, -1.01F, 2, 6, 2, 0.0F);
		setRotation(staff2, 0.0F, 0.0F, -0.8651597102135892F);
		leaf_headleft1 = new ModelRenderer(this, 35, 2);
		leaf_headleft1.setRotationPoint(4.0F, -3.0F, -2.0F);
		leaf_headleft1.addBox(0.0F, -2.0F, 0.0F, 0, 4, 3, 0.0F);
		setRotation(leaf_headleft1, 0.22759093446006054F, 0.4553564018453205F, 0.091106186954104F);
		staffleaf1 = new ModelRenderer(this, 99, 30);
		staffleaf1.setRotationPoint(0.0F, -3.0F, 1.0F);
		staffleaf1.addBox(-3.0F, -2.0F, 0.0F, 3, 4, 0, 0.0F);
		setRotation(staffleaf1, 0.0F, 0.4553564018453205F, 0.0F);
		plate_back2 = new ModelRenderer(this, 108, 18);
		plate_back2.setRotationPoint(0.0F, 6.0F, 0.0F);
		plate_back2.addBox(-3.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F);
		plate_right2 = new ModelRenderer(this, 21, 49);
		plate_right2.setRotationPoint(0.0F, 6.0F, 0.0F);
		plate_right2.addBox(-2.0F, 0.0F, -3.0F, 2, 2, 6, 0.0F);
		headbase = new ModelRenderer(this, 60, 0);
		headbase.setRotationPoint(0.0F, -9.0F, 6.0F);
		headbase.addBox(-4.0F, -6.0F, -4.0F, 8, 6, 6, 0.0F);
		setRotation(headbase, -0.091106186954104F, 0.0F, 0.0F);
		snoutpiece = new ModelRenderer(this, 60, 30);
		snoutpiece.setRotationPoint(0.0F, -6.0F, -4.0F);
		snoutpiece.addBox(-3.0F, 0.0F, -6.0F, 6, 5, 6, 0.0F);
		setRotation(snoutpiece, 0.40980330836826856F, 0.0F, 0.0F);
		staff5 = new ModelRenderer(this, 99, 23);
		staff5.setRotationPoint(0.0F, -4.0F, 0.0F);
		staff5.addBox(0.0F, -4.0F, -1.04F, 2, 4, 2, 0.0F);
		setRotation(staff5, 0.0F, 0.0F, 0.8651597102135892F);
		plate_rightedge = new ModelRenderer(this, 38, 47);
		plate_rightedge.setRotationPoint(-2.0F, 0.0F, 0.0F);
		plate_rightedge.addBox(-2.0F, -2.0F, -4.01F, 2, 2, 8, 0.0F);
		setRotation(plate_rightedge, 0.0F, 0.0F, 0.22759093446006054F);
		plate_back1 = new ModelRenderer(this, 108, 7);
		plate_back1.setRotationPoint(0.0F, -5.0F, 10.0F);
		plate_back1.addBox(-4.0F, -2.0F, 0.0F, 8, 8, 2, 0.0F);
		setRotation(plate_back1, 0.22759093446006054F, 0.0F, 0.0F);
		leaf_headright1 = new ModelRenderer(this, 35, -3);
		leaf_headright1.setRotationPoint(-4.0F, -3.0F, -2.0F);
		leaf_headright1.addBox(0.0F, -2.0F, 0.0F, 0, 4, 3, 0.0F);
		setRotation(leaf_headright1, 0.22759093446006054F, -0.4553564018453205F, -0.091106186954104F);
		staff1 = new ModelRenderer(this, 90, 0);
		staff1.setRotationPoint(0.0F, -2.0F, 8.0F);
		staff1.addBox(-1.0F, -12.0F, 0.0F, 2, 24, 2, 0.0F);
		setRotation(staff1, -0.045553093477052F, 0.0F, -0.27314402793711257F);
		leaf_headleft2 = new ModelRenderer(this, 42, 2);
		leaf_headleft2.setRotationPoint(0.0F, 0.0F, 3.0F);
		leaf_headleft2.addBox(0.0F, -2.0F, 0.0F, 0, 4, 3, 0.0F);
		setRotation(leaf_headleft2, 0.0F, 0.5462880558742251F, 0.0F);
		plate_left2 = new ModelRenderer(this, 80, 50);
		plate_left2.setRotationPoint(0.0F, 6.0F, 0.0F);
		plate_left2.addBox(0.0F, 0.0F, -3.0F, 2, 2, 6, 0.0F);
		headconnectionpiece = new ModelRenderer(this, 60, 13);
		headconnectionpiece.setRotationPoint(0.0F, 0.0F, 2.0F);
		headconnectionpiece.addBox(-4.01F, 0.0F, -3.0F, 8, 2, 3, 0.0F);
		plate_backedge = new ModelRenderer(this, 108, 23);
		plate_backedge.setRotationPoint(0.0F, 0.0F, 2.0F);
		plate_backedge.addBox(-4.01F, -2.0F, 0.0F, 8, 2, 2, 0.0F);
		setRotation(plate_backedge, 0.22759093446006054F, 0.0F, 0.0F);
		bodybase.addChild(chestpiece_right);
		staffleaf1.addChild(staffleaf2);
		chestpiece_left.addChild(plate_left1);
		staff2.addChild(staff3);
		staff3.addChild(staff4);
		staff1.addChild(staff6);
		plate_left1.addChild(plate_leftedge);
		bodybase.addChild(chestpiece_left);
		headconnectionpiece.addChild(lowerjaw_iguess);
		chestpiece_right.addChild(plate_right1);
		bodybase.addChild(bodypiece1);
		leaf_headright1.addChild(leaf_headright2);
		staff1.addChild(staff2);
		headbase.addChild(leaf_headleft1);
		staff4.addChild(staffleaf1);
		plate_back1.addChild(plate_back2);
		plate_right1.addChild(plate_right2);
		bodybase.addChild(headbase);
		headbase.addChild(snoutpiece);
		staff4.addChild(staff5);
		plate_right1.addChild(plate_rightedge);
		bodybase.addChild(plate_back1);
		headbase.addChild(leaf_headright1);
		bodybase.addChild(staff1);
		leaf_headleft1.addChild(leaf_headleft2);
		plate_left1.addChild(plate_left2);
		headbase.addChild(headconnectionpiece);
		plate_back1.addChild(plate_backedge);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		bodybase.render(0.0625F);
	}

	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		headbase.rotateAngleY = rotationYaw / (180F / (float) Math.PI);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityPyrad pyrad = (EntityPyrad) entity;
		float flap = MathHelper.sin((pyrad.ticksExisted + partialRenderTicks) * 0.05F) * 0.5F * pyrad.getActiveTicks(partialRenderTicks) / 60.0F - 0.4F * (1F - pyrad.getActiveTicks(partialRenderTicks) / 60.0F);
		plate_back1.rotateAngleX = 0.22759093446006054F + flap;
		plate_right1.rotateAngleZ = 0.36425021489121656F + flap;
		plate_left1.rotateAngleZ = -0.36425021489121656F - flap;
		
		plate_left1.offsetX = -0.1F * (1F - pyrad.getActiveTicks(partialRenderTicks) / 60.0F);
		plate_right1.offsetX = 0.1F * (1F - pyrad.getActiveTicks(partialRenderTicks) / 60.0F);
		plate_back1.offsetZ = -0.05F * (1F - pyrad.getActiveTicks(partialRenderTicks) / 60.0F);
		
		headbase.offsetY = 0.125F * (1F - pyrad.getActiveTicks(partialRenderTicks) / 60.0F);
		
		leaf_headleft1.isHidden = !pyrad.isActive();
		leaf_headright1.isHidden = !pyrad.isActive();
		staffleaf1.isHidden = !pyrad.isActive();
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
