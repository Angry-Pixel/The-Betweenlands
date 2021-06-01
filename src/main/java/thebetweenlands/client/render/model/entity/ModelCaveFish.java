package thebetweenlands.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityCaveFish;
import thebetweenlands.common.entity.mobs.EntityCaveFishSmall;

@SideOnly(Side.CLIENT)
public class ModelCaveFish extends MowzieModelBase {
    MowzieModelRenderer body;
    MowzieModelRenderer head;
    MowzieModelRenderer body_2;
    MowzieModelRenderer dorsal_fin;
    MowzieModelRenderer ventral_fin_right;
    MowzieModelRenderer ventral_fin_left;
    MowzieModelRenderer pectoral_fin_right;
    MowzieModelRenderer pectoral_fin_left;
    MowzieModelRenderer body_3;
    MowzieModelRenderer body_4;
    MowzieModelRenderer anal_fin;
    MowzieModelRenderer tail;
    MowzieModelRenderer dorsal_fin_2;
    MowzieModelRenderer tail_fin;
    MowzieModelRenderer eye_left;
    MowzieModelRenderer eye_right;
    MowzieModelRenderer head_1;
    MowzieModelRenderer eye_left_2;
    MowzieModelRenderer eye_right_2;

    private MowzieModelRenderer[] parts;

    public ModelCaveFish() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.head = new MowzieModelRenderer(this, 12, 0);
        this.head.mirror = true;
        this.head.setRotationPoint(0.0F, 14.1F, -3.8F);
        this.head.addBox(-1.0F, -1.5F, -0.5F, 2, 2, 2, 0.0F);
        this.setRotateAngle(head, 0.8726646259971648F, 0.0F, 0.0F);
        this.ventral_fin_right = new MowzieModelRenderer(this, 0, 13);
        this.ventral_fin_right.mirror = true;
        this.ventral_fin_right.setRotationPoint(1.0F, 1.0F, 2.0F);
        this.ventral_fin_right.addBox(0.0F, -0.5F, -0.5F, 0, 2, 1, 0.0F);
        this.setRotateAngle(ventral_fin_right, 0.6981317007977318F, 0.0F, -0.3490658503988659F);
        this.body_4 = new MowzieModelRenderer(this, 13, 22);
        this.body_4.mirror = true;
        this.body_4.setRotationPoint(0.0F, 0.3F, 1.0F);
        this.body_4.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(body_4, 0.6981317007977318F, 0.0F, 0.0F);
        this.tail_fin = new MowzieModelRenderer(this, 0, 14);
        this.tail_fin.mirror = true;
        this.tail_fin.setRotationPoint(0.0F, 0.5F, 0.0F);
        this.tail_fin.addBox(0.0F, -0.5F, -3.0F, 0, 5, 5, 0.0F);
        this.ventral_fin_left = new MowzieModelRenderer(this, 8, 13);
        this.ventral_fin_left.mirror = true;
        this.ventral_fin_left.setRotationPoint(-1.0F, 1.0F, 2.0F);
        this.ventral_fin_left.addBox(0.0F, -0.5F, -0.5F, 0, 2, 1, 0.0F);
        this.setRotateAngle(ventral_fin_left, 0.6981317007977318F, 0.0F, 0.3490658503988659F);
        this.head_1 = new MowzieModelRenderer(this, 21, 0);
        this.head_1.mirror = true;
        this.head_1.setRotationPoint(0.0F, 0.5F, -0.5F);
        this.head_1.addBox(-1.0F, -0.8F, -1.0F, 2, 1, 1, 0.0F);
        this.setRotateAngle(head_1, -0.4553564018453205F, 0.0F, 0.0F);
        this.body_3 = new MowzieModelRenderer(this, 11, 12);
        this.body_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body_3.addBox(-1.5F, -1.0F, -1.0F, 3, 2, 2, 0.0F);
        this.setRotateAngle(body_3, 0.7853981633974483F, 0.0F, 0.0F);
        this.eye_left = new MowzieModelRenderer(this, 0, 0);
        this.eye_left.setRotationPoint(1.0F, -0.5F, 2.0F);
        this.eye_left.addBox(-0.5F, -1.5F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(eye_left, 0.0F, 0.17453292519943295F, -0.17453292519943295F);
        this.body_2 = new MowzieModelRenderer(this, 13, 17);
        this.body_2.mirror = true;
        this.body_2.setRotationPoint(0.0F, -0.53F, 2.7F);
        this.body_2.addBox(-1.0F, -1.5F, 0.0F, 2, 3, 1, 0.0F);
        this.setRotateAngle(body_2, -0.14433872913993104F, 0.0F, 0.0F);
        this.pectoral_fin_right = new MowzieModelRenderer(this, 0, 10);
        this.pectoral_fin_right.mirror = true;
        this.pectoral_fin_right.setRotationPoint(1.5F, -0.5F, 2.3F);
        this.pectoral_fin_right.addBox(0.0F, -0.5F, 0.0F, 0, 1, 2, 0.0F);
        this.setRotateAngle(pectoral_fin_right, 0.0F, 0.3490658503988659F, 0.0F);
        this.body = new MowzieModelRenderer(this, 11, 5);
        this.body.setRotationPoint(0.0F, 14.0F, -4.0F);
        this.body.addBox(-1.0F, -2.0F, 0.0F, 2, 3, 3, 0.0F);
        this.dorsal_fin_2 = new MowzieModelRenderer(this, 7, 16);
        this.dorsal_fin_2.mirror = true;
        this.dorsal_fin_2.setRotationPoint(0.0F, 1.0F, 1.0F);
        this.dorsal_fin_2.addBox(0.0F, 0.0F, 0.0F, 0, 1, 1, 0.0F);
        this.eye_right_2 = new MowzieModelRenderer(this, 24, 10);
        this.eye_right_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.eye_right_2.addBox(-2.5F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.eye_left_2 = new MowzieModelRenderer(this, 0, 7);
        this.eye_left_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.eye_left_2.addBox(1.5F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        this.tail = new MowzieModelRenderer(this, 13, 27);
        this.tail.mirror = true;
        this.tail.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.tail.addBox(-0.5F, 0.0F, -1.0F, 1, 1, 2, 0.0F);
        this.anal_fin = new MowzieModelRenderer(this, 4, 16);
        this.anal_fin.mirror = true;
        this.anal_fin.setRotationPoint(0.0F, 0.7F, -1.0F);
        this.anal_fin.addBox(0.0F, -0.5F, -1.0F, 0, 1, 1, 0.0F);
        this.dorsal_fin = new MowzieModelRenderer(this, 3, 12);
        this.dorsal_fin.mirror = true;
        this.dorsal_fin.setRotationPoint(0.0F, -2.0F, 1.8F);
        this.dorsal_fin.addBox(0.0F, -2.0F, 0.0F, 0, 2, 2, 0.0F);
        this.setRotateAngle(dorsal_fin, -0.4363323129985824F, 0.0F, 0.0F);
        this.pectoral_fin_left = new MowzieModelRenderer(this, 6, 10);
        this.pectoral_fin_left.mirror = true;
        this.pectoral_fin_left.setRotationPoint(-1.5F, -0.5F, 2.3F);
        this.pectoral_fin_left.addBox(0.0F, -0.5F, 0.0F, 0, 1, 2, 0.0F);
        this.setRotateAngle(pectoral_fin_left, 0.0F, -0.3490658503988659F, 0.0F);
        this.eye_right = new MowzieModelRenderer(this, 22, 3);
        this.eye_right.setRotationPoint(-1.0F, -0.5F, 2.0F);
        this.eye_right.addBox(-1.5F, -1.5F, -1.5F, 2, 3, 3, 0.0F);
        this.setRotateAngle(eye_right, 0.0F, -0.17453292519943295F, 0.17453292519943295F);
        this.body.addChild(this.ventral_fin_right);
        this.body_3.addChild(this.body_4);
        this.tail.addChild(this.tail_fin);
        this.body.addChild(this.ventral_fin_left);
        this.head.addChild(this.head_1);
        this.body_2.addChild(this.body_3);
        this.head.addChild(this.eye_left);
        this.body.addChild(this.body_2);
        this.body.addChild(this.pectoral_fin_right);
        this.body_4.addChild(this.dorsal_fin_2);
        this.eye_right.addChild(this.eye_right_2);
        this.eye_left.addChild(this.eye_left_2);
        this.body_4.addChild(this.tail);
        this.body_4.addChild(this.anal_fin);
        this.body.addChild(this.dorsal_fin);
        this.body.addChild(this.pectoral_fin_left);
        this.head.addChild(this.eye_right);
        
		parts = new MowzieModelRenderer[] {
				body,
				head,
				body_2,
				dorsal_fin,
				ventral_fin_right,
				ventral_fin_left,
				pectoral_fin_right,
				pectoral_fin_left,
				body_3,
				body_4,
				anal_fin,
				tail,
				dorsal_fin_2,
				tail_fin,
				eye_left,
				eye_right,
				head_1,
				eye_left_2,
				eye_right_2
		};

		setInitPose();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    }

    public void render(float scale) {
        this.head.render(scale);
        this.body.render(scale);
    }

	@Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityLivingBase fish;
		if(entity instanceof EntityCaveFishSmall)
			fish = (EntityCaveFishSmall) entity;
		else
			fish = (EntityCaveFish) entity;
		
		setToInitPose();

    	float frame = fish.ticksExisted + partialRenderTicks;

    	walk(head_1, 0.25F, 0.35F, false, 0.0F, 0F, frame, 1F - speed);

    	flap(tail, 0.75F, 0.5F, false, 0.0F, 0F, frame, 0.0625F + speed);
		flap(tail_fin, 0.75F, 0.5F, false, 1.0F, 0F, frame, 0.0625F + speed);

		swing(ventral_fin_left, 0.75F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
		swing(ventral_fin_right, 0.75F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);

		swing(pectoral_fin_left, 0.5F, 0.75F, true, 1.0F, 0F, frame, 0.125F + speed);
		swing(pectoral_fin_right, 0.5F, 0.75F, false, 1.0F, 0F, frame, 0.125F + speed);

		walk(pectoral_fin_left, 0.5F, 0.75F, false, 0.0F, 0F, frame, 0.125F + speed);
		walk(pectoral_fin_right, 0.5F, 0.75F, false, 0.0F, 0F, frame, 0.125F + speed);

		flap(pectoral_fin_left, 0.5F, 0.75F, false, 0.0F, 0F, frame, 0.125F + speed);
		flap(pectoral_fin_right, 0.5F, 0.75F, true, 0.0F, 0F, frame, 0.125F + speed);
	}

    public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
