package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAnadia extends ModelBase {
	
    public ModelRenderer set1_body_main;
    public ModelRenderer set1_head_main;
    public ModelRenderer set1_tail_main;
    public ModelRenderer set1_body_bass;
    public ModelRenderer set1_connectionfix;
    public ModelRenderer set1_body_back;
    public ModelRenderer set1_dorsalfin1;
    public ModelRenderer set1_pectoralfin_left1a;
    public ModelRenderer set1_pectoralfin_right1a;
    public ModelRenderer set1_pelvicfin_left1;
    public ModelRenderer set1_pelvicfin_right1;
    public ModelRenderer set1_pectoralfin_left1b;
    public ModelRenderer set1_pectoralfin_right1b;
    public ModelRenderer set1_head_bass;
    public ModelRenderer set1_head_2;
    public ModelRenderer set1_jaw;
    public ModelRenderer set1_sensorything_left1a;
    public ModelRenderer set1_sensorything_left2;
    public ModelRenderer set1_sensorything_right1a;
    public ModelRenderer set1_sensorything_right2;
    public ModelRenderer set1_sensorything_left1b;
    public ModelRenderer set1_sensorything_right1b;
    public ModelRenderer set1_tail_bass;
    public ModelRenderer set1_tail_back;
    public ModelRenderer set1_analfin1;
    public ModelRenderer set1_caudalfin1;
    
    public ModelRenderer set2_body_main;
    public ModelRenderer set2_head_main;
    public ModelRenderer set2_tail_main;
    public ModelRenderer set2_body_bass;
    public ModelRenderer set2_body_back;
    public ModelRenderer set2_connectionfix;
    public ModelRenderer set2_dorsalfin1;
    public ModelRenderer set2_pectoralfin_left1a;
    public ModelRenderer set2_pectoralfin_right1a;
    public ModelRenderer set2_pelvicfin1;
    public ModelRenderer set2_pelvicfin_right1;
    public ModelRenderer set2_pectoralfin_left1b;
    public ModelRenderer set2_pectoralfin_right1b;
    public ModelRenderer set2_head_bass;
    public ModelRenderer set2_head_2;
    public ModelRenderer set2_head_jaw;
    public ModelRenderer set2_head_crestleft;
    public ModelRenderer set2_head_crestright;
    public ModelRenderer set2_sensorything_left2a;
    public ModelRenderer set2_sensorything_right2a;
    public ModelRenderer set2_sensorything_left1a;
    public ModelRenderer set2_sensorything_right1a;
    public ModelRenderer set_sensorything_left1b;
    public ModelRenderer set2_sensorything_right1b;
    public ModelRenderer set2_tail_bass;
    public ModelRenderer set2_tail_back;
    public ModelRenderer set2_analfin1;
    public ModelRenderer set2_caudalfin1;

    public ModelAnadia() {
        textureWidth = 64;
        textureHeight = 64;
        set1_body_main = new ModelRenderer(this, 0, 0);
        set1_body_main.setRotationPoint(0.0F, 15.0F, -3.0F);
        set1_body_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        set1_tail_main = new ModelRenderer(this, 0, 0);
        set1_tail_main.setRotationPoint(0.0F, 15.0F, 7.0F);
        set1_tail_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        set1_pectoralfin_right1a = new ModelRenderer(this, 7, 33);
        set1_pectoralfin_right1a.setRotationPoint(-2.0F, 4.0F, 1.0F);
        set1_pectoralfin_right1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotateAngle(set1_pectoralfin_right1a, -0.5009094953223726F, -0.6373942428283291F, 0.0F);
        set1_connectionfix = new ModelRenderer(this, 0, 13);
        set1_connectionfix.setRotationPoint(0.0F, 5.0F, 0.0F);
        set1_connectionfix.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2, 0.0F);
        set1_sensorything_right1a = new ModelRenderer(this, 27, 21);
        set1_sensorything_right1a.setRotationPoint(-2.5F, 0.0F, -2.0F);
        set1_sensorything_right1a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_sensorything_right1a, 0.0F, 0.22759093446006054F, 0.0F);
        set1_sensorything_left1b = new ModelRenderer(this, 22, 24);
        set1_sensorything_left1b.setRotationPoint(0.0F, 0.0F, -2.0F);
        set1_sensorything_left1b.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_sensorything_left1b, 0.0F, -0.22759093446006054F, 0.0F);
        set1_body_bass = new ModelRenderer(this, 0, 0);
        set1_body_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
        set1_body_bass.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 6, 0.0F);
        set1_sensorything_left1a = new ModelRenderer(this, 22, 21);
        set1_sensorything_left1a.setRotationPoint(2.5F, 0.0F, -2.0F);
        set1_sensorything_left1a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_sensorything_left1a, 0.0F, -0.22759093446006054F, 0.0F);
        set1_analfin1 = new ModelRenderer(this, 53, 13);
        set1_analfin1.setRotationPoint(0.0F, 4.0F, 2.0F);
        set1_analfin1.addBox(0.0F, -1.0F, 0.0F, 0, 3, 4, 0.0F);
        setRotateAngle(set1_analfin1, -0.22759093446006054F, 0.0F, 0.0F);
        set1_body_back = new ModelRenderer(this, 0, 17);
        set1_body_back.setRotationPoint(0.0F, 0.0F, 6.0F);
        set1_body_back.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 4, 0.0F);
        set1_pectoralfin_right1b = new ModelRenderer(this, 7, 37);
        set1_pectoralfin_right1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        set1_pectoralfin_right1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_pectoralfin_right1b, 0.0F, -0.4553564018453205F, 0.0F);
        set1_pelvicfin_right1 = new ModelRenderer(this, 5, 27);
        set1_pelvicfin_right1.setRotationPoint(-2.0F, 5.0F, 1.0F);
        set1_pelvicfin_right1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_pelvicfin_right1, 0.31869712141416456F, 0.0F, 0.27314402793711257F);
        set1_pectoralfin_left1b = new ModelRenderer(this, 0, 37);
        set1_pectoralfin_left1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        set1_pectoralfin_left1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_pectoralfin_left1b, 0.0F, 0.4553564018453205F, 0.0F);
        set1_head_2 = new ModelRenderer(this, 22, 10);
        set1_head_2.setRotationPoint(0.0F, 0.0F, -4.0F);
        set1_head_2.addBox(-2.51F, 0.0F, -2.0F, 5, 4, 2, 0.0F);
        setRotateAngle(set1_head_2, 0.5009094953223726F, 0.0F, 0.0F);
        set1_sensorything_left2 = new ModelRenderer(this, 22, 27);
        set1_sensorything_left2.setRotationPoint(2.5F, 2.0F, -2.0F);
        set1_sensorything_left2.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_sensorything_left2, 0.0F, -0.40980330836826856F, 0.0F);
        set1_tail_bass = new ModelRenderer(this, 42, 0);
        set1_tail_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
        set1_tail_bass.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 4, 0.0F);
        setRotateAngle(set1_tail_bass, -0.136659280431156F, 0.0F, 0.0F);
        set1_pelvicfin_left1 = new ModelRenderer(this, 0, 27);
        set1_pelvicfin_left1.setRotationPoint(2.0F, 5.0F, 1.0F);
        set1_pelvicfin_left1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_pelvicfin_left1, 0.31869712141416456F, 0.0F, -0.27314402793711257F);
        set1_caudalfin1 = new ModelRenderer(this, 42, 12);
        set1_caudalfin1.setRotationPoint(0.0F, 0.0F, 3.0F);
        set1_caudalfin1.addBox(0.0F, -1.0F, 0.0F, 0, 5, 5, 0.0F);
        set1_dorsalfin1 = new ModelRenderer(this, 0, 24);
        set1_dorsalfin1.setRotationPoint(0.0F, 0.0F, 2.0F);
        set1_dorsalfin1.addBox(0.0F, -3.0F, 0.0F, 0, 3, 8, 0.0F);
        setRotateAngle(set1_dorsalfin1, -0.136659280431156F, 0.0F, 0.0F);
        set1_sensorything_right1b = new ModelRenderer(this, 27, 24);
        set1_sensorything_right1b.setRotationPoint(0.0F, 0.0F, -2.0F);
        set1_sensorything_right1b.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_sensorything_right1b, 0.0F, 0.22759093446006054F, 0.0F);
        set1_pectoralfin_left1a = new ModelRenderer(this, 0, 33);
        set1_pectoralfin_left1a.setRotationPoint(2.0F, 4.0F, 1.0F);
        set1_pectoralfin_left1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotateAngle(set1_pectoralfin_left1a, -0.5009094953223726F, 0.6373942428283291F, 0.0F);
        set1_head_bass = new ModelRenderer(this, 22, 0);
        set1_head_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
        set1_head_bass.addBox(-2.5F, 0.0F, -4.0F, 5, 5, 4, 0.0F);
        setRotateAngle(set1_head_bass, 0.36425021489121656F, 0.0F, 0.0F);
        set1_jaw = new ModelRenderer(this, 22, 17);
        set1_jaw.setRotationPoint(0.0F, 5.0F, -4.0F);
        set1_jaw.addBox(-2.0F, -1.0F, -4.0F, 4, 1, 4, 0.0F);
        setRotateAngle(set1_jaw, -0.6373942428283291F, 0.0F, 0.0F);
        set1_sensorything_right2 = new ModelRenderer(this, 27, 27);
        set1_sensorything_right2.setRotationPoint(-2.5F, 2.0F, -2.0F);
        set1_sensorything_right2.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set1_sensorything_right2, 0.0F, 0.40980330836826856F, 0.0F);
        set1_tail_back = new ModelRenderer(this, 42, 9);
        set1_tail_back.setRotationPoint(0.0F, 0.0F, 4.0F);
        set1_tail_back.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 4, 0.0F);
        setRotateAngle(set1_tail_back, 0.091106186954104F, 0.0F, 0.0F);
        set1_head_main = new ModelRenderer(this, 0, 0);
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
        
        set2_tail_back = new ModelRenderer(this, 48, 8);
        set2_tail_back.setRotationPoint(0.0F, 0.0F, 3.0F);
        set2_tail_back.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 3, 0.0F);
        setRotateAngle(set2_tail_back, 0.136659280431156F, 0.0F, 0.0F);
        set2_pelvicfin1 = new ModelRenderer(this, 0, 21);
        set2_pelvicfin1.setRotationPoint(2.0F, 5.0F, 0.0F);
        set2_pelvicfin1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set2_pelvicfin1, 0.36425021489121656F, 0.0F, -0.40980330836826856F);
        set2_sensorything_right2a = new ModelRenderer(this, 31, 16);
        set2_sensorything_right2a.setRotationPoint(-2.5F, 0.0F, -2.0F);
        set2_sensorything_right2a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set2_sensorything_right2a, 0.0F, 0.18203784098300857F, 0.0F);
        set2_analfin1 = new ModelRenderer(this, 48, 19);
        set2_analfin1.setRotationPoint(0.0F, 4.0F, 1.0F);
        set2_analfin1.addBox(0.0F, -1.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotateAngle(set2_analfin1, -0.27314402793711257F, 0.0F, 0.0F);
        set2_pectoralfin_left1b = new ModelRenderer(this, 0, 29);
        set2_pectoralfin_left1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        set2_pectoralfin_left1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotateAngle(set2_pectoralfin_left1b, 0.0F, 0.0F, -0.40980330836826856F);
        set2_body_back = new ModelRenderer(this, 0, 15);
        set2_body_back.setRotationPoint(0.0F, 0.0F, 8.0F);
        set2_body_back.addBox(-2.0F, 0.0F, 0.0F, 4, 5, 2, 0.0F);
        set2_pelvicfin_right1 = new ModelRenderer(this, 5, 21);
        set2_pelvicfin_right1.setRotationPoint(-2.0F, 5.0F, 0.0F);
        set2_pelvicfin_right1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set2_pelvicfin_right1, 0.36425021489121656F, 0.0F, 0.40980330836826856F);
        set2_head_crestright = new ModelRenderer(this, 33, 33);
        set2_head_crestright.setRotationPoint(-2.5F, 0.0F, -1.0F);
        set2_head_crestright.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(set2_head_crestright, 0.0F, 0.0F, -0.18203784098300857F);
        set2_pectoralfin_right1b = new ModelRenderer(this, 7, 29);
        set2_pectoralfin_right1b.setRotationPoint(0.0F, 2.0F, 0.0F);
        set2_pectoralfin_right1b.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotateAngle(set2_pectoralfin_right1b, 0.0F, 0.0F, 0.40980330836826856F);
        set2_tail_main = new ModelRenderer(this, 0, 0);
        set2_tail_main.setRotationPoint(0.0F, 15.0F, 7.0F);
        set2_tail_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        set2_head_bass = new ModelRenderer(this, 26, 0);
        set2_head_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
        set2_head_bass.addBox(-2.5F, 0.0F, -5.0F, 5, 5, 5, 0.0F);
        setRotateAngle(set2_head_bass, 0.36425021489121656F, 0.0F, 0.0F);
        set2_head_jaw = new ModelRenderer(this, 26, 21);
        set2_head_jaw.setRotationPoint(0.0F, 5.0F, -5.0F);
        set2_head_jaw.addBox(-2.0F, -4.0F, 0.0F, 4, 4, 1, 0.0F);
        setRotateAngle(set2_head_jaw, 0.8196066167365371F, 0.0F, 0.0F);
        set2_pectoralfin_right1a = new ModelRenderer(this, 7, 27);
        set2_pectoralfin_right1a.setRotationPoint(-2.0F, 5.0F, 1.0F);
        set2_pectoralfin_right1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set2_pectoralfin_right1a, 0.22759093446006054F, 0.136659280431156F, 0.9105382707654417F);
        set2_connectionfix = new ModelRenderer(this, 13, 15);
        set2_connectionfix.setRotationPoint(0.0F, 5.0F, 0.0F);
        set2_connectionfix.addBox(-2.0F, 0.0F, -2.0F, 4, 1, 2, 0.0F);
        set2_body_bass = new ModelRenderer(this, 0, 0);
        set2_body_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
        set2_body_bass.addBox(-2.0F, 0.0F, 0.0F, 4, 6, 8, 0.0F);
        set2_tail_bass = new ModelRenderer(this, 48, 0);
        set2_tail_bass.setRotationPoint(0.0F, 0.0F, 0.0F);
        set2_tail_bass.addBox(-1.5F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
        setRotateAngle(set2_tail_bass, -0.136659280431156F, 0.0F, 0.0F);
        set2_sensorything_right1b = new ModelRenderer(this, 30, 30);
        set2_sensorything_right1b.setRotationPoint(0.0F, 0.0F, -2.0F);
        set2_sensorything_right1b.addBox(-3.0F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
        setRotateAngle(set2_sensorything_right1b, -0.22759093446006054F, 0.0F, 0.0F);
        set2_body_main = new ModelRenderer(this, 0, 0);
        set2_body_main.setRotationPoint(0.0F, 15.0F, -3.0F);
        set2_body_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        set2_head_2 = new ModelRenderer(this, 26, 11);
        set2_head_2.setRotationPoint(0.0F, 0.0F, -5.0F);
        set2_head_2.addBox(-2.51F, 0.0F, -2.0F, 5, 4, 2, 0.0F);
        setRotateAngle(set2_head_2, 0.5009094953223726F, 0.0F, 0.0F);
        set2_sensorything_left1a = new ModelRenderer(this, 25, 27);
        set2_sensorything_left1a.setRotationPoint(0.0F, -4.0F, 0.0F);
        set2_sensorything_left1a.addBox(0.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
        setRotateAngle(set2_sensorything_left1a, 0.6373942428283291F, 0.0F, 0.0F);
        set2_pectoralfin_left1a = new ModelRenderer(this, 0, 27);
        set2_pectoralfin_left1a.setRotationPoint(2.0F, 5.0F, 1.0F);
        set2_pectoralfin_left1a.addBox(0.0F, 0.0F, 0.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set2_pectoralfin_left1a, 0.22759093446006054F, -0.136659280431156F, -0.9105382707654417F);
        set2_head_crestleft = new ModelRenderer(this, 26, 33);
        set2_head_crestleft.setRotationPoint(2.5F, 0.0F, -1.0F);
        set2_head_crestleft.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(set2_head_crestleft, 0.0F, 0.0F, 0.18203784098300857F);
        set_sensorything_left1b = new ModelRenderer(this, 30, 27);
        set_sensorything_left1b.setRotationPoint(0.0F, 0.0F, -2.0F);
        set_sensorything_left1b.addBox(0.0F, 0.0F, -2.0F, 3, 0, 2, 0.0F);
        setRotateAngle(set_sensorything_left1b, -0.31869712141416456F, 0.0F, 0.0F);
        set2_head_main = new ModelRenderer(this, 0, 0);
        set2_head_main.setRotationPoint(0.0F, 15.0F, -3.0F);
        set2_head_main.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        set2_sensorything_right1a = new ModelRenderer(this, 25, 30);
        set2_sensorything_right1a.setRotationPoint(0.0F, -4.0F, 0.0F);
        set2_sensorything_right1a.addBox(-2.0F, 0.0F, -2.0F, 2, 0, 2, 0.0F);
        setRotateAngle(set2_sensorything_right1a, 0.5462880558742251F, 0.0F, 0.0F);
        set2_sensorything_left2a = new ModelRenderer(this, 26, 16);
        set2_sensorything_left2a.setRotationPoint(2.5F, 0.0F, -2.0F);
        set2_sensorything_left2a.addBox(0.0F, 0.0F, -2.0F, 0, 2, 2, 0.0F);
        setRotateAngle(set2_sensorything_left2a, 0.0F, -0.18203784098300857F, 0.0F);
        set2_caudalfin1 = new ModelRenderer(this, 48, 9);
        set2_caudalfin1.setRotationPoint(0.0F, 0.0F, 2.0F);
        set2_caudalfin1.addBox(0.0F, -1.0F, 0.0F, 0, 6, 6, 0.0F);
        setRotateAngle(set2_caudalfin1, -0.091106186954104F, 0.0F, 0.0F);
        set2_dorsalfin1 = new ModelRenderer(this, 0, 21);
        set2_dorsalfin1.setRotationPoint(0.0F, 0.0F, 5.0F);
        set2_dorsalfin1.addBox(0.0F, -2.0F, 0.0F, 0, 2, 5, 0.0F);
        setRotateAngle(set2_dorsalfin1, -0.136659280431156F, 0.0F, 0.0F);
        set2_tail_bass.addChild(set2_tail_back);
        set2_body_back.addChild(set2_pelvicfin1);
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
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    	super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

	public void renderHead(byte headType, float scale) {
		switch (headType) {
		case 0:
			set1_head_main.render(scale);
			break;
		case 1:
			set2_head_main.render(scale);
			break;
		case 2: // temp until next model
			set2_head_main.render(scale);
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
		case 2:// temp until next model
			set2_body_main.render(scale);
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
		case 2: // temp until next model
			set2_tail_main.render(scale);
			break;
		}
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}
