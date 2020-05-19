package thebetweenlands.client.render.model.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.client.render.model.MowzieModelRenderer;
import thebetweenlands.common.entity.mobs.EntityAnadia;

@SideOnly(Side.CLIENT)
public class ModelAnadia extends MowzieModelBase {
	
    MowzieModelRenderer set1_body_main;
    MowzieModelRenderer set1_head_main;
    MowzieModelRenderer set1_tail_main;
    MowzieModelRenderer set1_body_bass;
    MowzieModelRenderer set1_connectionfix;
    MowzieModelRenderer set1_body_back;
    MowzieModelRenderer set1_dorsalfin1;
    MowzieModelRenderer set1_pectoralfin_left1a;
    MowzieModelRenderer set1_pectoralfin_right1a;
    MowzieModelRenderer set1_pelvicfin_left1;
    MowzieModelRenderer set1_pelvicfin_right1;
    MowzieModelRenderer set1_pectoralfin_left1b;
    MowzieModelRenderer set1_pectoralfin_right1b;
    MowzieModelRenderer set1_head_bass;
    MowzieModelRenderer set1_head_2;
    MowzieModelRenderer set1_jaw;
    MowzieModelRenderer set1_sensorything_left1a;
    MowzieModelRenderer set1_sensorything_left2;
    MowzieModelRenderer set1_sensorything_right1a;
    MowzieModelRenderer set1_sensorything_right2;
    MowzieModelRenderer set1_sensorything_left1b;
    MowzieModelRenderer set1_sensorything_right1b;
    MowzieModelRenderer set1_tail_bass;
    MowzieModelRenderer set1_tail_back;
    MowzieModelRenderer set1_analfin1;
    MowzieModelRenderer set1_caudalfin1;
    
    MowzieModelRenderer set2_body_main;
    MowzieModelRenderer set2_head_main;
    MowzieModelRenderer set2_tail_main;
    MowzieModelRenderer set2_body_bass;
    MowzieModelRenderer set2_body_back;
    MowzieModelRenderer set2_connectionfix;
    MowzieModelRenderer set2_dorsalfin1;
    MowzieModelRenderer set2_pectoralfin_left1a;
    MowzieModelRenderer set2_pectoralfin_right1a;
    MowzieModelRenderer set2_pelvicfin_left1;
    MowzieModelRenderer set2_pelvicfin_right1;
    MowzieModelRenderer set2_pectoralfin_left1b;
    MowzieModelRenderer set2_pectoralfin_right1b;
    MowzieModelRenderer set2_head_bass;
    MowzieModelRenderer set2_head_2;
    MowzieModelRenderer set2_head_jaw;
    MowzieModelRenderer set2_head_crestleft;
    MowzieModelRenderer set2_head_crestright;
    MowzieModelRenderer set2_sensorything_left2a;
    MowzieModelRenderer set2_sensorything_right2a;
    MowzieModelRenderer set2_sensorything_left1a;
    MowzieModelRenderer set2_sensorything_right1a;
    MowzieModelRenderer set_sensorything_left1b;
    MowzieModelRenderer set2_sensorything_right1b;
    MowzieModelRenderer set2_tail_bass;
    MowzieModelRenderer set2_tail_back;
    MowzieModelRenderer set2_analfin1;
    MowzieModelRenderer set2_caudalfin1;

    MowzieModelRenderer set3_body_main;
    MowzieModelRenderer set3_head_main;
    MowzieModelRenderer set3_tail_main;
    MowzieModelRenderer set3_body_bass;
    MowzieModelRenderer set3_body_mid;
    MowzieModelRenderer set3_connectionfix;
    MowzieModelRenderer set3_dorsalfin1;
    MowzieModelRenderer set3_pectoralfin_left1a;
    MowzieModelRenderer set3_pectoralfin_right1a;
    MowzieModelRenderer set3_body_back;
    MowzieModelRenderer set3_pelvicfin_left1;
    MowzieModelRenderer set3_pelvicfin_right1;
    MowzieModelRenderer set3_pectoralfin_left1b;
    MowzieModelRenderer set3_pectoralfin_right1b;
    MowzieModelRenderer set3_head_bass;
    MowzieModelRenderer set3_head_2;
    MowzieModelRenderer set3_head_side_left;
    MowzieModelRenderer set3_head_side_right;
    MowzieModelRenderer set3_head_3;
    MowzieModelRenderer set3_head_jaw;
    MowzieModelRenderer set3_sensorything_left1a;
    MowzieModelRenderer set3_sensorything_right1a;
    MowzieModelRenderer set3_sensorything_left1b;
    MowzieModelRenderer set3_sensorything_right1b;
    MowzieModelRenderer set3_tail_bass;
    MowzieModelRenderer set3_tail_mid;
    MowzieModelRenderer set3_analfin1;
    MowzieModelRenderer set3_tail_back;
    MowzieModelRenderer set3_caudalfin1;

	public ModelAnadia() {
		textureWidth = 64;
		textureHeight = 64;
		set1_body_main = new MowzieModelRenderer(this, 0, 0);
		set1_body_main.setRotationPoint(0.0F, 15.0F, -3.0F);
		set1_body_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set1_tail_main = new MowzieModelRenderer(this, 0, 0);
		set1_tail_main.setRotationPoint(0.0F, 15.0F, 7.0F);
		set1_tail_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set1_pectoralfin_right1a = new MowzieModelRenderer(this, 7, 33);
		set1_pectoralfin_right1a.setRotationPoint(-2.0F, 4.0F, 1.0F);
		set1_pectoralfin_right1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
		setRotateAngle(set1_pectoralfin_right1a, -0.5009094953223726F, -0.6373942428283291F, 0.0F);
		set1_connectionfix = new MowzieModelRenderer(this, 0, 13);
		set1_connectionfix.setRotationPoint(0.0F, 5.0F, 0.0F);
		set1_connectionfix.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2, 0.0F);
		set1_sensorything_right1a = new MowzieModelRenderer(this, 27, 21);
		set1_sensorything_right1a.setRotationPoint(-2.5F, 0.0F, -2.0F);
		set1_sensorything_right1a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_sensorything_right1a, 0.0F, 0.22759093446006054F, 0.0F);
		set1_sensorything_left1b = new MowzieModelRenderer(this, 22, 24);
		set1_sensorything_left1b.setRotationPoint(0.0F, 0.0F, -2.0F);
		set1_sensorything_left1b.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_sensorything_left1b, 0.0F, -0.22759093446006054F, 0.0F);
		set1_body_bass = new MowzieModelRenderer(this, 0, 0);
		set1_body_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set1_body_bass.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 6, 0.0F);
		set1_sensorything_left1a = new MowzieModelRenderer(this, 22, 21);
		set1_sensorything_left1a.setRotationPoint(2.5F, 0.0F, -2.0F);
		set1_sensorything_left1a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_sensorything_left1a, 0.0F, -0.22759093446006054F, 0.0F);
		set1_analfin1 = new MowzieModelRenderer(this, 53, 13);
		set1_analfin1.setRotationPoint(0.0F, 4.0F, 2.0F);
		set1_analfin1.addBox(0.0F, -1.0F, 0.0F, 0, 3, 4, 0.0F);
		setRotateAngle(set1_analfin1, -0.22759093446006054F, 0.0F, 0.0F);
		set1_body_back = new MowzieModelRenderer(this, 0, 17);
		set1_body_back.setRotationPoint(0.0F, 0.0F, 6.0F);
		set1_body_back.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		set1_pectoralfin_right1b = new MowzieModelRenderer(this, 7, 37);
		set1_pectoralfin_right1b.setRotationPoint(0.0F, 0.0F, 3.0F);
		set1_pectoralfin_right1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_pectoralfin_right1b, 0.0F, -0.4553564018453205F, 0.0F);
		set1_pelvicfin_right1 = new MowzieModelRenderer(this, 5, 27);
		set1_pelvicfin_right1.setRotationPoint(-2.0F, 5.0F, 1.0F);
		set1_pelvicfin_right1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_pelvicfin_right1, 0.31869712141416456F, 0.0F, 0.27314402793711257F);
		set1_pectoralfin_left1b = new MowzieModelRenderer(this, 0, 37);
		set1_pectoralfin_left1b.setRotationPoint(0.0F, 0.0F, 3.0F);
		set1_pectoralfin_left1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_pectoralfin_left1b, 0.0F, 0.4553564018453205F, 0.0F);
		set1_head_2 = new MowzieModelRenderer(this, 22, 10);
		set1_head_2.setRotationPoint(0.0F, 0.0F, -4.0F);
		set1_head_2.addBox(-2.51F, 0.0F, -2.0F, 5, 4, 2, 0.0F);
		setRotateAngle(set1_head_2, 0.5009094953223726F, 0.0F, 0.0F);
		set1_sensorything_left2 = new MowzieModelRenderer(this, 22, 27);
		set1_sensorything_left2.setRotationPoint(2.5F, 2.0F, -2.0F);
		set1_sensorything_left2.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_sensorything_left2, 0.0F, -0.40980330836826856F, 0.0F);
		set1_tail_bass = new MowzieModelRenderer(this, 42, 0);
		set1_tail_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set1_tail_bass.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 4, 0.0F);
		setRotateAngle(set1_tail_bass, -0.136659280431156F, 0.0F, 0.0F);
		set1_pelvicfin_left1 = new MowzieModelRenderer(this, 0, 27);
		set1_pelvicfin_left1.setRotationPoint(2.0F, 5.0F, 1.0F);
		set1_pelvicfin_left1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_pelvicfin_left1, 0.31869712141416456F, 0.0F, -0.27314402793711257F);
		set1_caudalfin1 = new MowzieModelRenderer(this, 42, 12);
		set1_caudalfin1.setRotationPoint(0.0F, 0.0F, 3.0F);
		set1_caudalfin1.addBox(0.0F, -1.0F, 0.0F, 0, 5, 5, 0.0F);
		set1_dorsalfin1 = new MowzieModelRenderer(this, 0, 24);
		set1_dorsalfin1.setRotationPoint(0.0F, 0.0F, 2.0F);
		set1_dorsalfin1.addBox(0.0F, -3.0F, 0.0F, 0, 3, 8, 0.0F);
		setRotateAngle(set1_dorsalfin1, -0.136659280431156F, 0.0F, 0.0F);
		set1_sensorything_right1b = new MowzieModelRenderer(this, 27, 24);
		set1_sensorything_right1b.setRotationPoint(0.0F, 0.0F, -2.0F);
		set1_sensorything_right1b.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_sensorything_right1b, 0.0F, 0.22759093446006054F, 0.0F);
		set1_pectoralfin_left1a = new MowzieModelRenderer(this, 0, 33);
		set1_pectoralfin_left1a.setRotationPoint(2.0F, 4.0F, 1.0F);
		set1_pectoralfin_left1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
		setRotateAngle(set1_pectoralfin_left1a, -0.5009094953223726F, 0.6373942428283291F, 0.0F);
		set1_head_bass = new MowzieModelRenderer(this, 22, 0);
		set1_head_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set1_head_bass.addBox(-2.5F, 0.0F, -4.0F, 5, 5, 4, 0.0F);
		setRotateAngle(set1_head_bass, 0.36425021489121656F, 0.0F, 0.0F);
		set1_jaw = new MowzieModelRenderer(this, 22, 17);
		set1_jaw.setRotationPoint(0.0F, 5.0F, -4.0F);
		set1_jaw.addBox(-2.0F, -1.0F, -4.0F, 4, 1, 4, 0.0F);
		setRotateAngle(set1_jaw, -0.6373942428283291F, 0.0F, 0.0F);
		set1_sensorything_right2 = new MowzieModelRenderer(this, 27, 27);
		set1_sensorything_right2.setRotationPoint(-2.5F, 2.0F, -2.0F);
		set1_sensorything_right2.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set1_sensorything_right2, 0.0F, 0.40980330836826856F, 0.0F);
		set1_tail_back = new MowzieModelRenderer(this, 42, 9);
		set1_tail_back.setRotationPoint(0.0F, 0.0F, 4.0F);
		set1_tail_back.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 4, 0.0F);
		setRotateAngle(set1_tail_back, 0.091106186954104F, 0.0F, 0.0F);
		set1_head_main = new MowzieModelRenderer(this, 0, 0);
		set1_head_main.setRotationPoint(0.0F, 15.0F, -3.0F);
		set1_head_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set1_body_bass.addChild(set1_pectoralfin_right1a);
		set1_body_bass.addChild(set1_connectionfix);
		set1_head_2.addChild(set1_sensorything_right1a);
		set1_sensorything_left1a.addChild(set1_sensorything_left1b);
		set1_body_main.addChild(set1_body_bass);
		set1_head_2.addChild(set1_sensorything_left1a);
		set1_tail_bass.addChild(set1_analfin1);
		set1_body_bass.addChild(set1_body_back);
		set1_pectoralfin_right1a.addChild(set1_pectoralfin_right1b);
		set1_body_back.addChild(set1_pelvicfin_right1);
		set1_pectoralfin_left1a.addChild(set1_pectoralfin_left1b);
		set1_head_bass.addChild(set1_head_2);
		set1_head_2.addChild(set1_sensorything_left2);
		set1_tail_main.addChild(set1_tail_bass);
		set1_body_back.addChild(set1_pelvicfin_left1);
		set1_tail_back.addChild(set1_caudalfin1);
		set1_body_bass.addChild(set1_dorsalfin1);
		set1_sensorything_right1a.addChild(set1_sensorything_right1b);
		set1_body_bass.addChild(set1_pectoralfin_left1a);
		set1_head_main.addChild(set1_head_bass);
		set1_head_bass.addChild(set1_jaw);
		set1_head_2.addChild(set1_sensorything_right2);
		set1_tail_bass.addChild(set1_tail_back);

		set2_tail_back = new MowzieModelRenderer(this, 48, 8);
		set2_tail_back.setRotationPoint(0.0F, 0.0F, 3.0F);
		set2_tail_back.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 3, 0.0F);
		setRotateAngle(set2_tail_back, 0.136659280431156F, 0.0F, 0.0F);
		set2_pelvicfin_left1 = new MowzieModelRenderer(this, 0, 21);
		set2_pelvicfin_left1.setRotationPoint(2.0F, 5.0F, 0.0F);
		set2_pelvicfin_left1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set2_pelvicfin_left1, 0.36425021489121656F, 0.0F, -0.40980330836826856F);
		set2_sensorything_right2a = new MowzieModelRenderer(this, 31, 16);
		set2_sensorything_right2a.setRotationPoint(-2.5F, 0.0F, -2.0F);
		set2_sensorything_right2a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set2_sensorything_right2a, 0.0F, 0.18203784098300857F, 0.0F);
		set2_analfin1 = new MowzieModelRenderer(this, 48, 19);
		set2_analfin1.setRotationPoint(0.0F, 4.0F, 1.0F);
		set2_analfin1.addBox(0.0F, -1.0F, 0.0F, 0, 2, 3, 0.0F);
		setRotateAngle(set2_analfin1, -0.27314402793711257F, 0.0F, 0.0F);
		set2_pectoralfin_left1b = new MowzieModelRenderer(this, 0, 29);
		set2_pectoralfin_left1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		set2_pectoralfin_left1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
		setRotateAngle(set2_pectoralfin_left1b, 0.0F, 0.0F, -0.40980330836826856F);
		set2_body_back = new MowzieModelRenderer(this, 0, 15);
		set2_body_back.setRotationPoint(0.0F, 0.0F, 8.0F);
		set2_body_back.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 2, 0.0F);
		set2_pelvicfin_right1 = new MowzieModelRenderer(this, 5, 21);
		set2_pelvicfin_right1.setRotationPoint(-2.0F, 5.0F, 0.0F);
		set2_pelvicfin_right1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set2_pelvicfin_right1, 0.36425021489121656F, 0.0F, 0.40980330836826856F);
		set2_head_crestright = new MowzieModelRenderer(this, 33, 33);
		set2_head_crestright.setRotationPoint(-2.5F, 0.0F, -1.0F);
		set2_head_crestright.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
		setRotateAngle(set2_head_crestright, 0.0F, 0.0F, -0.18203784098300857F);
		set2_pectoralfin_right1b = new MowzieModelRenderer(this, 7, 29);
		set2_pectoralfin_right1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		set2_pectoralfin_right1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
		setRotateAngle(set2_pectoralfin_right1b, 0.0F, 0.0F, 0.40980330836826856F);
		set2_tail_main = new MowzieModelRenderer(this, 0, 0);
		set2_tail_main.setRotationPoint(0.0F, 15.0F, 7.0F);
		set2_tail_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set2_head_bass = new MowzieModelRenderer(this, 26, 0);
		set2_head_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set2_head_bass.addBox(-2.5F, 0.0F, -5.0F, 5, 5, 5, 0.0F);
		setRotateAngle(set2_head_bass, 0.36425021489121656F, 0.0F, 0.0F);
		set2_head_jaw = new MowzieModelRenderer(this, 26, 21);
		set2_head_jaw.setRotationPoint(0.0F, 5.0F, -5.0F);
		set2_head_jaw.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 1, 0.0F);
		setRotateAngle(set2_head_jaw, 0.8196066167365371F, 0.0F, 0.0F);
		set2_pectoralfin_right1a = new MowzieModelRenderer(this, 7, 27);
		set2_pectoralfin_right1a.setRotationPoint(-2.0F, 5.0F, 1.0F);
		set2_pectoralfin_right1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set2_pectoralfin_right1a, 0.22759093446006054F, 0.136659280431156F, 0.9105382707654417F);
		set2_connectionfix = new MowzieModelRenderer(this, 13, 15);
		set2_connectionfix.setRotationPoint(0.0F, 5.0F, 0.0F);
		set2_connectionfix.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2, 0.0F);
		set2_body_bass = new MowzieModelRenderer(this, 0, 0);
		set2_body_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set2_body_bass.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 8, 0.0F);
		set2_tail_bass = new MowzieModelRenderer(this, 48, 0);
		set2_tail_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set2_tail_bass.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
		setRotateAngle(set2_tail_bass, -0.136659280431156F, 0.0F, 0.0F);
		set2_sensorything_right1b = new MowzieModelRenderer(this, 30, 30);
		set2_sensorything_right1b.setRotationPoint(0.0F, 0.0F, -2.0F);
		set2_sensorything_right1b.addBox(-3.0F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
		setRotateAngle(set2_sensorything_right1b, -0.22759093446006054F, 0.0F, 0.0F);
		set2_body_main = new MowzieModelRenderer(this, 0, 0);
		set2_body_main.setRotationPoint(0.0F, 15.0F, -3.0F);
		set2_body_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set2_head_2 = new MowzieModelRenderer(this, 26, 11);
		set2_head_2.setRotationPoint(0.0F, 0.0F, -5.0F);
		set2_head_2.addBox(-2.51F, 0.0F, -2.0F, 5, 4, 2, 0.0F);
		setRotateAngle(set2_head_2, 0.5009094953223726F, 0.0F, 0.0F);
		set2_sensorything_left1a = new MowzieModelRenderer(this, 25, 27);
		set2_sensorything_left1a.setRotationPoint(0.0F, -4.0F, 0.0F);
		set2_sensorything_left1a.addBox(0.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
		setRotateAngle(set2_sensorything_left1a, 0.6373942428283291F, 0.0F, 0.0F);
		set2_pectoralfin_left1a = new MowzieModelRenderer(this, 0, 27);
		set2_pectoralfin_left1a.setRotationPoint(2.0F, 5.0F, 1.0F);
		set2_pectoralfin_left1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set2_pectoralfin_left1a, 0.22759093446006054F, -0.136659280431156F, -0.9105382707654417F);
		set2_head_crestleft = new MowzieModelRenderer(this, 26, 33);
		set2_head_crestleft.setRotationPoint(2.5F, 0.0F, -1.0F);
		set2_head_crestleft.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
		setRotateAngle(set2_head_crestleft, 0.0F, 0.0F, 0.18203784098300857F);
		set_sensorything_left1b = new MowzieModelRenderer(this, 30, 27);
		set_sensorything_left1b.setRotationPoint(0.0F, 0.0F, -2.0F);
		set_sensorything_left1b.addBox(0.0F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
		setRotateAngle(set_sensorything_left1b, -0.31869712141416456F, 0.0F, 0.0F);
		set2_head_main = new MowzieModelRenderer(this, 0, 0);
		set2_head_main.setRotationPoint(0.0F, 15.0F, -3.0F);
		set2_head_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set2_sensorything_right1a = new MowzieModelRenderer(this, 25, 30);
		set2_sensorything_right1a.setRotationPoint(0.0F, -4.0F, 0.0F);
		set2_sensorything_right1a.addBox(-2.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
		setRotateAngle(set2_sensorything_right1a, 0.5462880558742251F, 0.0F, 0.0F);
		set2_sensorything_left2a = new MowzieModelRenderer(this, 26, 16);
		set2_sensorything_left2a.setRotationPoint(2.5F, 0.0F, -2.0F);
		set2_sensorything_left2a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set2_sensorything_left2a, 0.0F, -0.18203784098300857F, 0.0F);
		set2_caudalfin1 = new MowzieModelRenderer(this, 48, 9);
		set2_caudalfin1.setRotationPoint(0.0F, 0.0F, 2.0F);
		set2_caudalfin1.addBox(0.0F, -1.0F, 0.0F, 0, 6, 6, 0.0F);
		setRotateAngle(set2_caudalfin1, -0.091106186954104F, 0.0F, 0.0F);
		set2_dorsalfin1 = new MowzieModelRenderer(this, 0, 21);
		set2_dorsalfin1.setRotationPoint(0.0F, 0.0F, 5.0F);
		set2_dorsalfin1.addBox(0.0F, -2.0F, 0.0F, 0, 2, 5, 0.0F);
		setRotateAngle(set2_dorsalfin1, -0.136659280431156F, 0.0F, 0.0F);
		set2_tail_bass.addChild(set2_tail_back);
		set2_body_back.addChild(set2_pelvicfin_left1);
		set2_head_2.addChild(set2_sensorything_right2a);
		set2_tail_bass.addChild(set2_analfin1);
		set2_pectoralfin_left1a.addChild(set2_pectoralfin_left1b);
		set2_body_bass.addChild(set2_body_back);
		set2_body_back.addChild(set2_pelvicfin_right1);
		set2_head_bass.addChild(set2_head_crestright);
		set2_pectoralfin_right1a.addChild(set2_pectoralfin_right1b);
		set2_head_main.addChild(set2_head_bass);
		set2_head_bass.addChild(set2_head_jaw);
		set2_body_bass.addChild(set2_pectoralfin_right1a);
		set2_body_bass.addChild(set2_connectionfix);
		set2_body_main.addChild(set2_body_bass);
		set2_tail_main.addChild(set2_tail_bass);
		set2_sensorything_right1a.addChild(set2_sensorything_right1b);
		set2_head_bass.addChild(set2_head_2);
		set2_head_jaw.addChild(set2_sensorything_left1a);
		set2_body_bass.addChild(set2_pectoralfin_left1a);
		set2_head_bass.addChild(set2_head_crestleft);
		set2_sensorything_left1a.addChild(set_sensorything_left1b);
		set2_head_jaw.addChild(set2_sensorything_right1a);
		set2_head_2.addChild(set2_sensorything_left2a);
		set2_tail_back.addChild(set2_caudalfin1);
		set2_body_bass.addChild(set2_dorsalfin1);

		set3_head_3 = new MowzieModelRenderer(this, 22, 20);
		set3_head_3.setRotationPoint(0.0F, -5.0F, -3.0F);
		set3_head_3.addBox(-2.01F, 0.0F, -2.0F, 4, 4, 2, 0.0F);
		setRotateAngle(set3_head_3, 0.5009094953223726F, 0.0F, 0.0F);
		set3_sensorything_left1b = new MowzieModelRenderer(this, 22, 27);
		set3_sensorything_left1b.setRotationPoint(0.0F, 1.0F, 0.0F);
		set3_sensorything_left1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set3_sensorything_left1b, 0.0F, 0.0F, 0.5462880558742251F);
		set3_body_back = new MowzieModelRenderer(this, 0, 19);
		set3_body_back.setRotationPoint(0.0F, 0.0F, 4.0F);
		set3_body_back.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		set3_sensorything_left1a = new MowzieModelRenderer(this, 22, 26);
		set3_sensorything_left1a.setRotationPoint(2.0F, 0.0F, -2.0F);
		set3_sensorything_left1a.addBox(0.0F, 0.0F, 0.0F, 0, 1, 1, 0.0F);
		setRotateAngle(set3_sensorything_left1a, 0.0F, 0.0F, -0.8651597102135892F);
		set3_body_mid = new MowzieModelRenderer(this, 0, 9);
		set3_body_mid.setRotationPoint(0.0F, 0.0F, 2.0F);
		set3_body_mid.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
		set3_pectoralfin_left1a = new MowzieModelRenderer(this, 0, 40);
		set3_pectoralfin_left1a.setRotationPoint(2.0F, 5.0F, 1.0F);
		set3_pectoralfin_left1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set3_pectoralfin_left1a, 0.4553564018453205F, 0.0F, -0.40980330836826856F);
		set3_connectionfix = new MowzieModelRenderer(this, 0, 28);
		set3_connectionfix.setRotationPoint(0.0F, 5.0F, 0.0F);
		set3_connectionfix.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2, 0.0F);
		set3_pectoralfin_right1b = new MowzieModelRenderer(this, 5, 44);
		set3_pectoralfin_right1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		set3_pectoralfin_right1b.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
		setRotateAngle(set3_pectoralfin_right1b, 0.0F, 0.0F, 0.36425021489121656F);
		set3_analfin1 = new MowzieModelRenderer(this, 0, 30);
		set3_analfin1.setRotationPoint(0.0F, 3.0F, 1.0F);
		set3_analfin1.addBox(0.0F, 0.0F, 0.0F, 0, 3, 5, 0.0F);
		setRotateAngle(set3_analfin1, -0.136659280431156F, 0.0F, 0.0F);
		set3_pelvicfin_right1 = new MowzieModelRenderer(this, 5, 30);
		set3_pelvicfin_right1.setRotationPoint(-2.0F, 4.0F, 0.0F);
		set3_pelvicfin_right1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set3_pelvicfin_right1, 0.5009094953223726F, 0.0F, 0.36425021489121656F);
		set3_pectoralfin_left1b = new MowzieModelRenderer(this, 5, 40);
		set3_pectoralfin_left1b.setRotationPoint(0.0F, 2.0F, 0.0F);
		set3_pectoralfin_left1b.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2, 0.0F);
		setRotateAngle(set3_pectoralfin_left1b, 0.0F, 0.0F, -0.36425021489121656F);
		set3_tail_main = new MowzieModelRenderer(this, 0, 0);
		set3_tail_main.setRotationPoint(0.0F, 15.0F, 7.0F);
		set3_tail_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set3_sensorything_right1a = new MowzieModelRenderer(this, 27, 26);
		set3_sensorything_right1a.setRotationPoint(-2.0F, 0.0F, -2.0F);
		set3_sensorything_right1a.addBox(0.0F, 0.0F, 0.0F, 0, 1, 1, 0.0F);
		setRotateAngle(set3_sensorything_right1a, 0.0F, 0.0F, 0.8651597102135892F);
		set3_dorsalfin1 = new MowzieModelRenderer(this, 0, 29);
		set3_dorsalfin1.setRotationPoint(0.0F, 0.0F, 10.0F);
		set3_dorsalfin1.addBox(0.0F, -2.0F, -10.0F, 0, 2, 10, 0.0F);
		setRotateAngle(set3_dorsalfin1, 0.091106186954104F, 0.0F, 0.0F);
		set3_tail_mid = new MowzieModelRenderer(this, 42, 9);
		set3_tail_mid.setRotationPoint(0.0F, 0.0F, 4.0F);
		set3_tail_mid.addBox(-1.0F, 0.0F, 0.0F, 2, 3, 4, 0.0F);
		setRotateAngle(set3_tail_mid, -0.136659280431156F, 0.0F, 0.0F);
		set3_head_main = new MowzieModelRenderer(this, 0, 0);
		set3_head_main.setRotationPoint(0.0F, 15.0F, -3.0F);
		set3_head_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set3_head_jaw = new MowzieModelRenderer(this, 22, 32);
		set3_head_jaw.setRotationPoint(0.0F, 0.0F, -3.0F);
		set3_head_jaw.addBox(-2.0F, -1.0F, -4.0F, 4, 1, 4, 0.0F);
		setRotateAngle(set3_head_jaw, -0.6829473363053812F, 0.0F, 0.0F);
		set3_head_side_right = new MowzieModelRenderer(this, 29, 38);
		set3_head_side_right.setRotationPoint(-2.5F, 5.0F, -4.0F);
		set3_head_side_right.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		setRotateAngle(set3_head_side_right, 0.0F, 0.0F, -0.18203784098300857F);
		set3_head_2 = new MowzieModelRenderer(this, 22, 10);
		set3_head_2.setRotationPoint(0.0F, 5.0F, -4.0F);
		set3_head_2.addBox(-2.0F, -5.0F, -3.0F, 4, 5, 4, 0.0F);
		setRotateAngle(set3_head_2, -0.18203784098300857F, 0.0F, 0.0F);
		set3_tail_back = new MowzieModelRenderer(this, 42, 17);
		set3_tail_back.setRotationPoint(0.0F, 3.0F, 4.0F);
		set3_tail_back.addBox(-0.5F, -3.0F, 0.0F, 1, 3, 3, 0.0F);
		setRotateAngle(set3_tail_back, 0.136659280431156F, 0.0F, 0.0F);
		set3_head_bass = new MowzieModelRenderer(this, 22, 0);
		set3_head_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set3_head_bass.addBox(-2.5F, 0.0F, -4.0F, 5, 5, 4, 0.0F);
		setRotateAngle(set3_head_bass, 0.36425021489121656F, 0.0F, 0.0F);
		set3_sensorything_right1b = new MowzieModelRenderer(this, 27, 27);
		set3_sensorything_right1b.setRotationPoint(0.0F, 1.0F, 0.0F);
		set3_sensorything_right1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set3_sensorything_right1b, 0.0F, 0.0F, -0.5462880558742251F);
		set3_pelvicfin_left1 = new MowzieModelRenderer(this, 0, 30);
		set3_pelvicfin_left1.setRotationPoint(2.0F, 4.0F, 0.0F);
		set3_pelvicfin_left1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set3_pelvicfin_left1, 0.5009094953223726F, 0.0F, -0.36425021489121656F);
		set3_pectoralfin_right1a = new MowzieModelRenderer(this, 0, 44);
		set3_pectoralfin_right1a.setRotationPoint(-2.0F, 5.0F, 1.0F);
		set3_pectoralfin_right1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
		setRotateAngle(set3_pectoralfin_right1a, 0.4553564018453205F, 0.0F, 0.40980330836826856F);
		set3_tail_bass = new MowzieModelRenderer(this, 42, 0);
		set3_tail_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set3_tail_bass.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 4, 0.0F);
		setRotateAngle(set3_tail_bass, -0.136659280431156F, 0.0F, 0.0F);
		set3_body_bass = new MowzieModelRenderer(this, 0, 0);
		set3_body_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
		set3_body_bass.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 2, 0.0F);
		set3_body_main = new MowzieModelRenderer(this, 0, 0);
		set3_body_main.setRotationPoint(0.0F, 15.0F, -3.0F);
		set3_body_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		set3_head_side_left = new MowzieModelRenderer(this, 22, 38);
		set3_head_side_left.setRotationPoint(2.5F, 5.0F, -4.0F);
		set3_head_side_left.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		setRotateAngle(set3_head_side_left, 0.0F, 0.0F, 0.18203784098300857F);
		set3_caudalfin1 = new MowzieModelRenderer(this, 42, 17);
		set3_caudalfin1.setRotationPoint(0.0F, 0.0F, 3.0F);
		set3_caudalfin1.addBox(0.0F, -4.0F, 0.0F, 0, 5, 7, 0.0F);
		setRotateAngle(set3_caudalfin1, 0.136659280431156F, 0.0F, 0.0F);
		set3_head_2.addChild(set3_head_3);
		set3_sensorything_left1a.addChild(set3_sensorything_left1b);
		set3_body_mid.addChild(set3_body_back);
		set3_head_3.addChild(set3_sensorything_left1a);
		set3_body_bass.addChild(set3_body_mid);
		set3_body_bass.addChild(set3_pectoralfin_left1a);
		set3_body_bass.addChild(set3_connectionfix);
		set3_pectoralfin_right1a.addChild(set3_pectoralfin_right1b);
		set3_tail_bass.addChild(set3_analfin1);
		set3_body_back.addChild(set3_pelvicfin_right1);
		set3_pectoralfin_left1a.addChild(set3_pectoralfin_left1b);
		set3_head_3.addChild(set3_sensorything_right1a);
		set3_body_bass.addChild(set3_dorsalfin1);
		set3_tail_bass.addChild(set3_tail_mid);
		set3_head_2.addChild(set3_head_jaw);
		set3_head_bass.addChild(set3_head_side_right);
		set3_head_bass.addChild(set3_head_2);
		set3_tail_mid.addChild(set3_tail_back);
		set3_head_main.addChild(set3_head_bass);
		set3_sensorything_right1a.addChild(set3_sensorything_right1b);
		set3_body_back.addChild(set3_pelvicfin_left1);
		set3_body_bass.addChild(set3_pectoralfin_right1a);
		set3_tail_main.addChild(set3_tail_bass);
		set3_body_main.addChild(set3_body_bass);
		set3_head_bass.addChild(set3_head_side_left);
		set3_tail_back.addChild(set3_caudalfin1);
		
		parts = new MowzieModelRenderer[] {
				set1_body_main,
				set1_head_main,
				set1_tail_main,
				set1_body_bass,
				set1_connectionfix,
				set1_body_back,
				set1_dorsalfin1,
				set1_pectoralfin_left1a,
				set1_pectoralfin_right1a,
				set1_pelvicfin_left1,
				set1_pelvicfin_right1,
				set1_pectoralfin_left1b,
				set1_pectoralfin_right1b,
				set1_head_bass,
				set1_head_2,
				set1_jaw,
				set1_sensorything_left1a,
				set1_sensorything_left2,
				set1_sensorything_right1a,
				set1_sensorything_right2,
				set1_sensorything_left1b,
				set1_sensorything_right1b,
				set1_tail_bass,
				set1_tail_back,
				set1_analfin1,
				set1_caudalfin1,
			    
				set2_body_main,
				set2_head_main,
				set2_tail_main,
				set2_body_bass,
				set2_body_back,
				set2_connectionfix,
				set2_dorsalfin1,
				set2_pectoralfin_left1a,
				set2_pectoralfin_right1a,
				set2_pelvicfin_left1,
				set2_pelvicfin_right1,
				set2_pectoralfin_left1b,
				set2_pectoralfin_right1b,
				set2_head_bass,
				set2_head_2,
				set2_head_jaw,
				set2_head_crestleft,
				set2_head_crestright,
				set2_sensorything_left2a,
				set2_sensorything_right2a,
				set2_sensorything_left1a,
				set2_sensorything_right1a,
				set_sensorything_left1b,
				set2_sensorything_right1b,
				set2_tail_bass,
				set2_tail_back,
				set2_analfin1,
				set2_caudalfin1,

				set3_body_main,
				set3_head_main,
				set3_tail_main,
				set3_body_bass,
				set3_body_mid,
				set3_connectionfix,
				set3_dorsalfin1,
				set3_pectoralfin_left1a,
				set3_pectoralfin_right1a,
				set3_body_back,
				set3_pelvicfin_left1,
				set3_pelvicfin_right1,
				set3_pectoralfin_left1b,
				set3_pectoralfin_right1b,
				set3_head_bass,
				set3_head_2,
				set3_head_side_left,
				set3_head_side_right,
				set3_head_3,
				set3_head_jaw,
				set3_sensorything_left1a,
				set3_sensorything_right1a,
				set3_sensorything_left1b,
				set3_sensorything_right1b,
				set3_tail_bass,
				set3_tail_mid,
				set3_analfin1,
				set3_tail_back,
				set3_caudalfin1
		};

		setInitPose();
    }

	@Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
    	EntityAnadia anadia = (EntityAnadia) entity;
    	setToInitPose();
    	
    	float frame = anadia.ticksExisted + partialRenderTicks;
    	
    	switch (anadia.getHeadType()) {
		case 0:
			walk(set1_jaw, (1.5F - anadia.getFishSize()) * 0.25F, 0.35F, false, 0.0F, 0F, frame, 1F - speed);
			break;
		case 1:
			walk(set2_head_jaw, (1.5F - anadia.getFishSize()) * 0.25F, 0.35F, false, 0.0F, 0F, frame, 1F - speed);
			break;
		case 2:
			walk(set3_head_jaw, (1.5F - anadia.getFishSize()) * 0.25F, 0.35F, false, 0.0F, 0F, frame, 1F - speed);
			break;
		}
    	
    	switch (anadia.getTailType()) {
		case 0:
			swing(set1_tail_bass, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.0625F + speed);
			swing(set1_tail_back, 0.5F, 0.5F, false, 1.0F, 0F, frame, 0.0625F + speed);
			swing(set1_caudalfin1, 0.5F, 0.5F, false, 2.0F, 0F, frame, 0.0625F + speed);
			break;
		case 1:
			swing(set2_tail_bass, 0.75F, 0.35F, false, 0.0F, 0F, frame, 0.0625F + speed);
			swing(set2_tail_back, 0.75F, 0.35F, false, 1.0F, 0F, frame, 0.0625F + speed);
			swing(set2_caudalfin1, 0.75F, 0.35F, false, 2.0F, 0F, frame, 0.0625F + speed);
			break;
		case 2:
			swing(set3_tail_bass, 1F, 0.25F, false, 0.0F, 0F, frame, 0.0625F + speed);
			swing(set3_tail_back, 1F, 0.25F, false, 1.0F, 0F, frame, 0.0625F + speed);
			swing(set3_caudalfin1, 1F, 0.25F, false, 2.0F, 0F, frame, 0.0625F + speed);
			break;
		}
    	
    	switch (anadia.getBodyType()) {
		case 0:
			swing(set1_pelvicfin_left1, 0.25F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
			swing(set1_pelvicfin_right1, 0.25F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);
			
			swing(set1_pectoralfin_left1a, 0.25F, 0.5F, true, 1.0F, 0F, frame, 0.125F + speed);
			swing(set1_pectoralfin_left1b, 0.25F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);
			
			swing(set1_pectoralfin_right1a, 0.25F, 0.5F, false, 1.0F, 0F, frame, 0.125F + speed);
			swing(set1_pectoralfin_right1b, 0.25F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
			
			walk(set1_pectoralfin_left1a, 0.25F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			walk(set1_pectoralfin_right1a, 0.25F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			
			flap(set1_pectoralfin_left1a, 0.25F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			flap(set1_pectoralfin_right1a, 0.25F, 0.5F, true, 0.0F, 0F, frame, 0.125F + speed);

			break;
		case 1:
			swing(set2_pelvicfin_left1, 0.5F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
			swing(set2_pelvicfin_right1, 0.5F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);
			
			swing(set2_pectoralfin_left1a, 0.5F, 0.5F, true, 1.0F, 0F, frame, 0.125F + speed);
			swing(set2_pectoralfin_left1b, 0.5F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);
			
			swing(set2_pectoralfin_right1a, 0.5F, 0.5F, false, 1.0F, 0F, frame, 0.125F + speed);
			swing(set2_pectoralfin_right1b, 0.5F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
			
			walk(set2_pectoralfin_left1a, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			walk(set2_pectoralfin_right1a, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			
			flap(set2_pectoralfin_left1a, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			flap(set2_pectoralfin_right1a, 0.5F, 0.5F, true, 0.0F, 0F, frame, 0.125F + speed);

			break;
		case 2:
			swing(set3_pelvicfin_left1, 0.5F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
			swing(set3_pelvicfin_right1, 0.5F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);
			
			swing(set3_pectoralfin_left1a, 0.5F, 0.5F, true, 1.0F, 0F, frame, 0.125F + speed);
			swing(set3_pectoralfin_left1b, 0.5F, 0.5F, true, 2.0F, 0F, frame, 0.125F + speed);
			
			swing(set3_pectoralfin_right1a, 0.5F, 0.5F, false, 1.0F, 0F, frame, 0.125F + speed);
			swing(set3_pectoralfin_right1b, 0.5F, 0.5F, false, 2.0F, 0F, frame, 0.125F + speed);
			
			walk(set3_pectoralfin_left1a, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			walk(set3_pectoralfin_right1a, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			
			flap(set3_pectoralfin_left1a, 0.5F, 0.5F, false, 0.0F, 0F, frame, 0.125F + speed);
			flap(set3_pectoralfin_right1a, 0.5F, 0.5F, true, 0.0F, 0F, frame, 0.125F + speed);
			break;
		}
	}

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    	//super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

	public void renderHead(byte headType, float scale) {
		switch (headType) {
		case 0:
			set1_head_main.render(scale);
			break;
		case 1:
			set2_head_main.render(scale);
			break;
		case 2:
			set3_head_main.render(scale);
			break;
		}
	}

	public void renderBody(byte bodyType, float scale) {
		switch (bodyType) {
		case 0:
			set1_body_main.render(scale);
			break;
		case 1:
			set2_body_main.render(scale);
			break;
		case 2:
			set3_body_main.render(scale);
			break;
		}
	}

	public void renderTail(byte tailType, float scale) {
		switch (tailType) {
		case 0:
			set1_tail_main.render(scale);
			break;
		case 1:
			set2_tail_main.render(scale);
			break;
		case 2:
			set3_tail_main.render(scale);
			break;
		}
	}

	public void setRotateAngle(MowzieModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}
