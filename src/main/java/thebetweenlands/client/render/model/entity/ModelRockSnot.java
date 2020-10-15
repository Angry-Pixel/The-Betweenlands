package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityRockSnot;

@SideOnly(Side.CLIENT)
public class ModelRockSnot extends ModelBase {
    ModelRenderer shell_left_main1a;
    ModelRenderer shell_right_main1a;
    ModelRenderer connectingbit_front1a;
    ModelRenderer connectingbit_back1a;
    ModelRenderer shell_left_main1b;
    ModelRenderer shell_left_side_f1a;
    ModelRenderer shell_left_side_b1a;
    ModelRenderer shell_left_side_f1b;
    ModelRenderer shell_left_side_b1b;
    ModelRenderer thorn_left3a;
    ModelRenderer mantle_left_main1a;
    ModelRenderer thorn_left1a;
    ModelRenderer mantle_left_sidef1a;
    ModelRenderer thorn_left1b;
    ModelRenderer mantle_left_sidef1b;
    ModelRenderer mantle_left_sidef1c;
    ModelRenderer thorn_left2a;
    ModelRenderer mantle_left_sideb1a;
    ModelRenderer thorn_left2b;
    ModelRenderer mantle_left_sideb1b;
    ModelRenderer mantle_left_sideb1c;
    ModelRenderer thorn_left3b;
    ModelRenderer thorn_left3c;
    ModelRenderer mantle_left_main1b;
    ModelRenderer mantle_left_main1c;
    ModelRenderer shell_right_main1b;
    ModelRenderer shell_right_side_f1a;
    ModelRenderer shell_right_side_b1a;
    ModelRenderer shell_right_side_f1b;
    ModelRenderer shell_right_side_b1b;
    ModelRenderer thorn_right3a;
    ModelRenderer mantle_right_main1a;
    ModelRenderer thorn_right1a;
    ModelRenderer mantle_right_sidef1a;
    ModelRenderer thorn_right1b;
    ModelRenderer mantle_left_sidef1b_1;
    ModelRenderer mantle_right_sidef1c;
    ModelRenderer thorn_right2a;
    ModelRenderer mantle_right_sideb1a;
    ModelRenderer thorn_right2b;
    ModelRenderer mantle_right_sideb1b;
    ModelRenderer mantle_right_sideb1c;
    ModelRenderer thorn_right3b;
    ModelRenderer thorn_right3c;
    ModelRenderer mantle_right_main1b;
    ModelRenderer mantle_right_main1c;
    ModelRenderer connectingbit_front1b;
    ModelRenderer connectingbit_back1b;

    public ModelRockSnot() {
        textureWidth = 64;
        textureHeight = 64;
        thorn_right1b = new ModelRenderer(this, 39, 25);
        thorn_right1b.setRotationPoint(1.0F, 1.0F, -1.0F);
        thorn_right1b.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_right1b, -0.091106186954104F, 0.0F, 0.0F);
        shell_left_side_b1b = new ModelRenderer(this, 28, 6);
        shell_left_side_b1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        shell_left_side_b1b.addBox(-2.0F, -4.0F, 0.0F, 2, 4, 3, 0.0F);
        setRotateAngle(shell_left_side_b1b, 0.0F, -0.27314402793711257F, 0.0F);
        thorn_left3c = new ModelRenderer(this, 49, 9);
        thorn_left3c.setRotationPoint(-1.0F, 1.0F, 1.0F);
        thorn_left3c.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_left3c, 0.091106186954104F, 0.0F, 0.0F);
        shell_right_side_f1b = new ModelRenderer(this, 17, 26);
        shell_right_side_f1b.setRotationPoint(0.0F, 0.0F, -3.0F);
        shell_right_side_f1b.addBox(0.0F, -4.0F, -3.0F, 2, 4, 3, 0.0F);
        setRotateAngle(shell_right_side_f1b, 0.0F, -0.27314402793711257F, 0.0F);
        mantle_right_main1c = new ModelRenderer(this, 0, 47);
        mantle_right_main1c.setRotationPoint(-1.0F, 0.0F, 0.0F);
        mantle_right_main1c.addBox(-2.0F, 0.0F, -3.0F, 2, 0, 6, 0.0F);
        setRotateAngle(mantle_right_main1c, 0.0F, 0.0F, -1.0471975511965976F);
        mantle_right_sideb1b = new ModelRenderer(this, 22, 47);
        mantle_right_sideb1b.setRotationPoint(-1.0F, 0.0F, 0.0F);
        mantle_right_sideb1b.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_right_sideb1b, 0.0F, 0.0F, -0.40980330836826856F);
        mantle_left_main1a = new ModelRenderer(this, -6, 40);
        mantle_left_main1a.setRotationPoint(-2.0F, -4.0F, 0.0F);
        mantle_left_main1a.addBox(0.0F, 0.0F, -3.0F, 1, 0, 6, 0.0F);
        setRotateAngle(mantle_left_main1a, 0.0F, 0.0F, -0.31869712141416456F);
        shell_left_side_f1a = new ModelRenderer(this, 17, 0);
        shell_left_side_f1a.setRotationPoint(2.0F, 0.0F, -3.0F);
        shell_left_side_f1a.addBox(-2.0F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(shell_left_side_f1a, 0.0F, 0.27314402793711257F, 0.0F);
        thorn_left3b = new ModelRenderer(this, 42, 9);
        thorn_left3b.setRotationPoint(-1.0F, 1.0F, -1.0F);
        thorn_left3b.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_left3b, -0.091106186954104F, 0.0F, 0.0F);
        connectingbit_back1a = new ModelRenderer(this, 0, 59);
        connectingbit_back1a.setRotationPoint(0.0F, 24.0F, 4.5F);
        connectingbit_back1a.addBox(-1.5F, -2.0F, 0.0F, 3, 2, 2, 0.0F);
        setRotateAngle(connectingbit_back1a, -0.22759093446006054F, 0.0F, 0.0F);
        connectingbit_front1b = new ModelRenderer(this, 11, 54);
        connectingbit_front1b.setRotationPoint(0.0F, -2.0F, -2.0F);
        connectingbit_front1b.addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(connectingbit_front1b, 0.31869712141416456F, 0.0F, 0.0F);
        thorn_right3a = new ModelRenderer(this, 33, 29);
        thorn_right3a.setRotationPoint(0.0F, -3.0F, 0.0F);
        thorn_right3a.addBox(0.0F, 0.0F, -3.0F, 1, 1, 6, 0.0F);
        setRotateAngle(thorn_right3a, 0.0F, 0.0F, 0.36425021489121656F);
        mantle_left_sidef1b_1 = new ModelRenderer(this, 11, 47);
        mantle_left_sidef1b_1.setRotationPoint(-1.0F, 0.0F, 0.0F);
        mantle_left_sidef1b_1.addBox(-1.0F, 0.0F, -3.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sidef1b_1, 0.0F, 0.0F, -0.40980330836826856F);
        thorn_left3a = new ModelRenderer(this, 33, 9);
        thorn_left3a.setRotationPoint(0.0F, -3.0F, 0.0F);
        thorn_left3a.addBox(-1.0F, 0.0F, -3.0F, 1, 1, 6, 0.0F);
        setRotateAngle(thorn_left3a, 0.0F, 0.0F, -0.36425021489121656F);
        thorn_right1a = new ModelRenderer(this, 39, 20);
        thorn_right1a.setRotationPoint(0.0F, -1.0F, 0.0F);
        thorn_right1a.addBox(0.0F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        setRotateAngle(thorn_right1a, 0.0F, 0.0F, 0.36425021489121656F);
        mantle_left_sideb1a = new ModelRenderer(this, 19, 40);
        mantle_left_sideb1a.setRotationPoint(-2.0F, -4.0F, 0.0F);
        mantle_left_sideb1a.addBox(0.0F, 0.0F, 0.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sideb1a, 0.0F, 0.0F, -0.31869712141416456F);
        shell_left_main1b = new ModelRenderer(this, 0, 9);
        shell_left_main1b.setRotationPoint(2.0F, -2.0F, 0.0F);
        shell_left_main1b.addBox(-2.0F, -4.0F, -3.0F, 2, 4, 6, 0.0F);
        setRotateAngle(shell_left_main1b, 0.0F, 0.0F, -0.27314402793711257F);
        mantle_left_sidef1c = new ModelRenderer(this, 14, 40);
        mantle_left_sidef1c.setRotationPoint(1.0F, 0.0F, 0.0F);
        mantle_left_sidef1c.addBox(0.0F, 0.0F, -3.0F, 2, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sidef1c, 0.0F, 0.0F, 1.0471975511965976F);
        shell_right_main1b = new ModelRenderer(this, 0, 29);
        shell_right_main1b.setRotationPoint(-2.0F, -2.0F, 0.0F);
        shell_right_main1b.addBox(0.0F, -4.0F, -3.0F, 2, 4, 6, 0.0F);
        setRotateAngle(shell_right_main1b, 0.0F, 0.0F, 0.27314402793711257F);
        mantle_right_sidef1a = new ModelRenderer(this, 8, 47);
        mantle_right_sidef1a.setRotationPoint(2.0F, -4.0F, 0.0F);
        mantle_right_sidef1a.addBox(-1.0F, 0.0F, -3.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_right_sidef1a, 0.0F, 0.0F, 0.31869712141416456F);
        connectingbit_front1a = new ModelRenderer(this, 0, 54);
        connectingbit_front1a.setRotationPoint(0.0F, 24.0F, -4.5F);
        connectingbit_front1a.addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2, 0.0F);
        setRotateAngle(connectingbit_front1a, 0.22759093446006054F, 0.0F, 0.0F);
        thorn_right3b = new ModelRenderer(this, 42, 29);
        thorn_right3b.setRotationPoint(1.0F, 1.0F, -1.0F);
        thorn_right3b.addBox(-1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_right3b, -0.091106186954104F, 0.0F, 0.0F);
        mantle_left_sidef1b = new ModelRenderer(this, 11, 40);
        mantle_left_sidef1b.setRotationPoint(1.0F, 0.0F, 0.0F);
        mantle_left_sidef1b.addBox(0.0F, 0.0F, -3.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sidef1b, 0.0F, 0.0F, 0.40980330836826856F);
        thorn_left2a = new ModelRenderer(this, 48, 0);
        thorn_left2a.setRotationPoint(0.0F, -1.0F, 0.0F);
        thorn_left2a.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
        setRotateAngle(thorn_left2a, 0.0F, 0.0F, -0.36425021489121656F);
        mantle_left_main1b = new ModelRenderer(this, -3, 40);
        mantle_left_main1b.setRotationPoint(1.0F, 0.0F, 0.0F);
        mantle_left_main1b.addBox(0.0F, 0.0F, -3.0F, 1, 0, 6, 0.0F);
        setRotateAngle(mantle_left_main1b, 0.0F, 0.0F, 0.40980330836826856F);
        mantle_right_sideb1c = new ModelRenderer(this, 25, 47);
        mantle_right_sideb1c.setRotationPoint(-1.0F, 0.0F, 0.0F);
        mantle_right_sideb1c.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 3, 0.0F);
        setRotateAngle(mantle_right_sideb1c, 0.0F, 0.0F, -1.0471975511965976F);
        mantle_left_main1c = new ModelRenderer(this, 0, 40);
        mantle_left_main1c.setRotationPoint(1.0F, 0.0F, 0.0F);
        mantle_left_main1c.addBox(0.0F, 0.0F, -3.0F, 2, 0, 6, 0.0F);
        setRotateAngle(mantle_left_main1c, 0.0F, 0.0F, 1.0471975511965976F);
        shell_left_main1a = new ModelRenderer(this, 0, 0);
        shell_left_main1a.setRotationPoint(0.5F, 24.0F, 0.0F);
        shell_left_main1a.addBox(0.0F, -2.0F, -3.0F, 2, 2, 6, 0.0F);
        setRotateAngle(shell_left_main1a, 0.0F, 0.0F, 0.136659280431156F);
        connectingbit_back1b = new ModelRenderer(this, 11, 59);
        connectingbit_back1b.setRotationPoint(0.0F, -2.0F, 2.0F);
        connectingbit_back1b.addBox(-1.5F, 0.0F, 0.0F, 3, 2, 2, 0.0F);
        setRotateAngle(connectingbit_back1b, -0.31869712141416456F, 0.0F, 0.0F);
        thorn_right2b = new ModelRenderer(this, 48, 25);
        thorn_right2b.setRotationPoint(1.0F, 1.0F, 1.0F);
        thorn_right2b.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_right2b, 0.091106186954104F, 0.0F, 0.0F);
        thorn_left2b = new ModelRenderer(this, 48, 5);
        thorn_left2b.setRotationPoint(-1.0F, 1.0F, 1.0F);
        thorn_left2b.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_left2b, 0.091106186954104F, 0.0F, 0.0F);
        mantle_left_sidef1a = new ModelRenderer(this, 8, 40);
        mantle_left_sidef1a.setRotationPoint(-2.0F, -4.0F, 0.0F);
        mantle_left_sidef1a.addBox(0.0F, 0.0F, -3.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sidef1a, 0.0F, 0.0F, -0.31869712141416456F);
        shell_left_side_f1b = new ModelRenderer(this, 17, 6);
        shell_left_side_f1b.setRotationPoint(0.0F, 0.0F, -3.0F);
        shell_left_side_f1b.addBox(-2.0F, -4.0F, -3.0F, 2, 4, 3, 0.0F);
        setRotateAngle(shell_left_side_f1b, 0.0F, 0.27314402793711257F, 0.0F);
        shell_right_side_b1a = new ModelRenderer(this, 28, 20);
        shell_right_side_b1a.setRotationPoint(-2.0F, 0.0F, 3.0F);
        shell_right_side_b1a.addBox(0.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(shell_right_side_b1a, 0.0F, 0.27314402793711257F, 0.0F);
        thorn_left1a = new ModelRenderer(this, 39, 0);
        thorn_left1a.setRotationPoint(0.0F, -1.0F, 0.0F);
        thorn_left1a.addBox(-1.0F, 0.0F, -3.0F, 1, 1, 3, 0.0F);
        setRotateAngle(thorn_left1a, 0.0F, 0.0F, -0.36425021489121656F);
        thorn_right2a = new ModelRenderer(this, 48, 20);
        thorn_right2a.setRotationPoint(0.0F, -1.0F, 0.0F);
        thorn_right2a.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
        setRotateAngle(thorn_right2a, 0.0F, 0.0F, 0.36425021489121656F);
        thorn_left1b = new ModelRenderer(this, 39, 5);
        thorn_left1b.setRotationPoint(-1.0F, 1.0F, -1.0F);
        thorn_left1b.addBox(0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_left1b, -0.091106186954104F, 0.0F, 0.0F);
        thorn_right3c = new ModelRenderer(this, 49, 29);
        thorn_right3c.setRotationPoint(1.0F, 1.0F, 1.0F);
        thorn_right3c.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
        setRotateAngle(thorn_right3c, 0.091106186954104F, 0.0F, 0.0F);
        mantle_right_sidef1c = new ModelRenderer(this, 14, 47);
        mantle_right_sidef1c.setRotationPoint(-1.0F, 0.0F, 0.0F);
        mantle_right_sidef1c.addBox(-2.0F, 0.0F, -3.0F, 2, 0, 3, 0.0F);
        setRotateAngle(mantle_right_sidef1c, 0.0F, 0.0F, -1.0471975511965976F);
        shell_right_side_b1b = new ModelRenderer(this, 28, 26);
        shell_right_side_b1b.setRotationPoint(0.0F, 0.0F, 3.0F);
        shell_right_side_b1b.addBox(0.0F, -4.0F, 0.0F, 2, 4, 3, 0.0F);
        setRotateAngle(shell_right_side_b1b, 0.0F, 0.27314402793711257F, 0.0F);
        mantle_right_main1a = new ModelRenderer(this, -6, 47);
        mantle_right_main1a.setRotationPoint(2.0F, -4.0F, 0.0F);
        mantle_right_main1a.addBox(-1.0F, 0.0F, -3.0F, 1, 0, 6, 0.0F);
        setRotateAngle(mantle_right_main1a, 0.0F, 0.0F, 0.31869712141416456F);
        mantle_left_sideb1c = new ModelRenderer(this, 25, 40);
        mantle_left_sideb1c.setRotationPoint(1.0F, 0.0F, 0.0F);
        mantle_left_sideb1c.addBox(0.0F, 0.0F, 0.0F, 2, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sideb1c, 0.0F, 0.0F, 1.0471975511965976F);
        mantle_right_sideb1a = new ModelRenderer(this, 19, 47);
        mantle_right_sideb1a.setRotationPoint(2.0F, -4.0F, 0.0F);
        mantle_right_sideb1a.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_right_sideb1a, 0.0F, 0.0F, 0.31869712141416456F);
        mantle_left_sideb1b = new ModelRenderer(this, 22, 40);
        mantle_left_sideb1b.setRotationPoint(1.0F, 0.0F, 0.0F);
        mantle_left_sideb1b.addBox(0.0F, 0.0F, 0.0F, 1, 0, 3, 0.0F);
        setRotateAngle(mantle_left_sideb1b, 0.0F, 0.0F, 0.40980330836826856F);
        shell_right_side_f1a = new ModelRenderer(this, 17, 20);
        shell_right_side_f1a.setRotationPoint(-2.0F, 0.0F, -3.0F);
        shell_right_side_f1a.addBox(0.0F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(shell_right_side_f1a, 0.0F, -0.27314402793711257F, 0.0F);
        shell_right_main1a = new ModelRenderer(this, 0, 20);
        shell_right_main1a.setRotationPoint(-0.5F, 24.0F, 0.0F);
        shell_right_main1a.addBox(-2.0F, -2.0F, -3.0F, 2, 2, 6, 0.0F);
        setRotateAngle(shell_right_main1a, 0.0F, 0.0F, -0.136659280431156F);
        shell_left_side_b1a = new ModelRenderer(this, 28, 0);
        shell_left_side_b1a.setRotationPoint(2.0F, 0.0F, 3.0F);
        shell_left_side_b1a.addBox(-2.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(shell_left_side_b1a, 0.0F, -0.27314402793711257F, 0.0F);
        mantle_right_main1b = new ModelRenderer(this, -3, 47);
        mantle_right_main1b.setRotationPoint(-1.0F, 0.0F, 0.0F);
        mantle_right_main1b.addBox(-1.0F, 0.0F, -3.0F, 1, 0, 6, 0.0F);
        setRotateAngle(mantle_right_main1b, 0.0F, 0.0F, -0.40980330836826856F);
        thorn_right1a.addChild(thorn_right1b);
        shell_left_main1b.addChild(shell_left_side_b1b);
        thorn_left3a.addChild(thorn_left3c);
        shell_right_main1b.addChild(shell_right_side_f1b);
        mantle_right_main1b.addChild(mantle_right_main1c);
        mantle_right_sideb1a.addChild(mantle_right_sideb1b);
        shell_left_main1b.addChild(mantle_left_main1a);
        shell_left_main1a.addChild(shell_left_side_f1a);
        thorn_left3a.addChild(thorn_left3b);
        connectingbit_front1a.addChild(connectingbit_front1b);
        shell_right_main1b.addChild(thorn_right3a);
        mantle_right_sidef1a.addChild(mantle_left_sidef1b_1);
        shell_left_main1b.addChild(thorn_left3a);
        shell_right_side_f1b.addChild(thorn_right1a);
        shell_left_side_b1b.addChild(mantle_left_sideb1a);
        shell_left_main1a.addChild(shell_left_main1b);
        mantle_left_sidef1b.addChild(mantle_left_sidef1c);
        shell_right_main1a.addChild(shell_right_main1b);
        shell_right_side_f1b.addChild(mantle_right_sidef1a);
        thorn_right3a.addChild(thorn_right3b);
        mantle_left_sidef1a.addChild(mantle_left_sidef1b);
        shell_left_side_b1b.addChild(thorn_left2a);
        mantle_left_main1a.addChild(mantle_left_main1b);
        mantle_right_sideb1b.addChild(mantle_right_sideb1c);
        mantle_left_main1b.addChild(mantle_left_main1c);
        connectingbit_back1a.addChild(connectingbit_back1b);
        thorn_right2a.addChild(thorn_right2b);
        thorn_left2a.addChild(thorn_left2b);
        shell_left_side_f1b.addChild(mantle_left_sidef1a);
        shell_left_main1b.addChild(shell_left_side_f1b);
        shell_right_main1a.addChild(shell_right_side_b1a);
        shell_left_side_f1b.addChild(thorn_left1a);
        shell_right_side_b1b.addChild(thorn_right2a);
        thorn_left1a.addChild(thorn_left1b);
        thorn_right3a.addChild(thorn_right3c);
        mantle_left_sidef1b_1.addChild(mantle_right_sidef1c);
        shell_right_main1b.addChild(shell_right_side_b1b);
        shell_right_main1b.addChild(mantle_right_main1a);
        mantle_left_sideb1b.addChild(mantle_left_sideb1c);
        shell_right_side_b1b.addChild(mantle_right_sideb1a);
        mantle_left_sideb1a.addChild(mantle_left_sideb1b);
        shell_right_main1a.addChild(shell_right_side_f1a);
        shell_left_main1a.addChild(shell_left_side_b1a);
        mantle_right_main1a.addChild(mantle_right_main1b);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
        connectingbit_back1a.render(scale);
        connectingbit_front1a.render(scale);
        shell_left_main1a.render(scale);
        shell_right_main1a.render(scale);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, scale, entity);
	}

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
		EntityRockSnot snot = (EntityRockSnot ) entity;
		float chomp = MathHelper.sin((snot.ticksExisted + partialRenderTicks) * 0.5F) * 0.8F;
		float chomp2 = MathHelper.sin((snot.ticksExisted + partialRenderTicks) * 0.25F) * 0.6F;
		shell_right_main1a.rotateAngleX = 0F;
		shell_left_main1a.rotateAngleX = 0F;
		shell_right_main1a.rotateAngleZ = -0.136659280431156F -convertDegtoRad(snot.getJawAngle());
		shell_left_main1a.rotateAngleZ = 0.136659280431156F + convertDegtoRad(snot.getJawAngle());
		shell_right_main1a.rotationPointY = 24.0F;
		shell_left_main1a.rotationPointY = 24.0F;
		shell_right_main1a.rotationPointX = -0.5F;
		shell_left_main1a.rotationPointX = 0.5F;
		shell_right_main1a.rotationPointZ = 0F;
		shell_left_main1a.rotationPointZ = 0F;

		if(snot.isBeingRidden() && snot.getJawAngle() == 16) {
			shell_right_main1a.rotateAngleX = 0F - chomp2 * 0.125F;
			shell_left_main1a.rotateAngleX = 0F + chomp2 * 0.125F;
			shell_right_main1a.rotationPointY = 24.0F + chomp2 * 1.5F;
			shell_left_main1a.rotationPointY = 24.0F + chomp2 * 1.5F;
			shell_right_main1a.rotateAngleZ = -0.136659280431156F -convertDegtoRad(snot.getJawAngle()) + chomp * 0.125F;
			shell_left_main1a.rotateAngleZ = 0.136659280431156F + convertDegtoRad(snot.getJawAngle()) - chomp * 0.125F;
		}

		if(snot.getPearlTimer() > 30 && snot.getEntityWorld().getTotalWorldTime()%2 == 0) {
			shell_right_main1a.rotationPointX = -0.5F + (snot.getEntityWorld().rand.nextFloat())* 0.5F;
			shell_left_main1a.rotationPointX = 0.5F + (snot.getEntityWorld().rand.nextFloat())* 0.5F;
			shell_right_main1a.rotationPointZ = 0F + (snot.getEntityWorld().rand.nextFloat())* 0.5F;
			shell_left_main1a.rotationPointZ = 0F + (snot.getEntityWorld().rand.nextFloat())* 0.5F;
			shell_right_main1a.rotationPointY = 24F + (snot.getEntityWorld().rand.nextFloat())* 0.75F;
			shell_left_main1a.rotationPointY = 24F + (snot.getEntityWorld().rand.nextFloat())* 0.75F;
		}

		if(snot.getPearlTimer() <= 30 && snot.getPearlTimer() > 10) {
			shell_right_main1a.rotateAngleZ = -0.136659280431156F - convertDegtoRad(30 -snot.getPearlTimer());
			shell_left_main1a.rotateAngleZ = 0.136659280431156F + convertDegtoRad(30 -snot.getPearlTimer());
			shell_right_main1a.rotationPointY = 24.0F + (30 - snot.getPearlTimer())* 0.1F;
			shell_left_main1a.rotationPointY = 24.0F + (30 - snot.getPearlTimer()) * 0.1F;
		}

		if(snot.getPearlTimer() <= 10 && snot.getPearlTimer() >= 1) {
			shell_right_main1a.rotateAngleZ = -0.136659280431156F - convertDegtoRad(10F) - convertDegtoRad(snot.getPearlTimer());
			shell_left_main1a.rotateAngleZ = 0.136659280431156F + convertDegtoRad(10F) + convertDegtoRad(snot.getPearlTimer());
			shell_right_main1a.rotationPointY = 25.0F + snot.getPearlTimer() * 0.1F;
			shell_left_main1a.rotationPointY = 25.0F + snot.getPearlTimer() * 0.1F;
		}
    }

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
