package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityCryptCrawler;
@SideOnly(Side.CLIENT)
public class ModelCryptCrawler extends ModelBase {
    ModelRenderer[] body_main = new ModelRenderer[2];
    ModelRenderer[] neck = new ModelRenderer[2];
    ModelRenderer[] body_lower = new ModelRenderer[2];
    ModelRenderer[] leg_front_left1 = new ModelRenderer[2];
    ModelRenderer[] leg_front_right1 = new ModelRenderer[2];
    ModelRenderer[] head1 = new ModelRenderer[2];
    ModelRenderer[] snout = new ModelRenderer[2];
    ModelRenderer[] lowerjaw = new ModelRenderer[2];
    ModelRenderer[] ear_left = new ModelRenderer[2];
    ModelRenderer[] ear_right = new ModelRenderer[2];
    ModelRenderer[] cheekybreeky_left = new ModelRenderer[2];
    ModelRenderer[] cheeckybreeky_right = new ModelRenderer[2];
    ModelRenderer[] teeth_upper1 = new ModelRenderer[2];
    ModelRenderer[] teeth_upper2 = new ModelRenderer[2];
    ModelRenderer[] teethlower = new ModelRenderer[2];
    ModelRenderer[] leg_back_left1 = new ModelRenderer[2];
    ModelRenderer[] leg_back_right1 = new ModelRenderer[2];
    ModelRenderer[] tail1 = new ModelRenderer[2];
    ModelRenderer[] tinyurn1 = new ModelRenderer[2];
    ModelRenderer[] tinyurn2 = new ModelRenderer[2];
    ModelRenderer[] tinyurn3 = new ModelRenderer[2];
    ModelRenderer[] leg_back_left2 = new ModelRenderer[2];
    ModelRenderer[] leg_back_left3 = new ModelRenderer[2];
    ModelRenderer[] leg_back_right2 = new ModelRenderer[2];
    ModelRenderer[] leg_back_right3 = new ModelRenderer[2];
    ModelRenderer[] tail2 = new ModelRenderer[2];
    ModelRenderer[] tail3 = new ModelRenderer[2];
    ModelRenderer[] tail4 = new ModelRenderer[2];
    ModelRenderer[] leg_front_left2 = new ModelRenderer[2];
    public ModelRenderer[] leg_front_left3 = new ModelRenderer[2];
    ModelRenderer[] leg_front_right2 = new ModelRenderer[2];
    public ModelRenderer[] leg_front_right3 = new ModelRenderer[2];
    
    public ModelCryptCrawler() {
        textureWidth = 128;
        textureHeight = 64;
        // Multipose model
        leg_front_right1[0] = new ModelRenderer(this, 48, 27);
        leg_front_right1[0].setRotationPoint(-4.0F, 2.0F, -6.0F);
        leg_front_right1[0].addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        setRotateAngle(leg_front_right1[0], 0.27314402793711257F, 0.0F, 0.136659280431156F);
        tinyurn1[0] = new ModelRenderer(this, 80, 0);
        tinyurn1[0].setRotationPoint(2.0F, 6.0F, 3.0F);
        tinyurn1[0].addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tinyurn1[0], 0.136659280431156F, 0.0F, 0.0F);
        leg_front_left2[0] = new ModelRenderer(this, 33, 37);
        leg_front_left2[0].setRotationPoint(0.0F, 5.0F, 2.0F);
        leg_front_left2[0].addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_front_left2[0], -0.5918411493512771F, 0.0F, 0.0F);
        leg_back_right1[0] = new ModelRenderer(this, 48, 0);
        leg_back_right1[0].setRotationPoint(-4.0F, 0.0F, 7.0F);
        leg_back_right1[0].addBox(-1.0F, 0.0F, -3.0F, 3, 7, 4, 0.0F);
        setRotateAngle(leg_back_right1[0], 0.27314402793711257F, 0.18203784098300857F, 0.0F);
        body_main[0] = new ModelRenderer(this, 0, 0);
        body_main[0].setRotationPoint(0.0F, 12.0F, 0.0F);
        body_main[0].addBox(-4.0F, 0.0F, -8.0F, 8, 6, 8, 0.0F);
        setRotateAngle(body_main[0], 0.091106186954104F, 0.0F, 0.0F);
        lowerjaw[0] = new ModelRenderer(this, 22, 55);
        lowerjaw[0].setRotationPoint(0.0F, 4.0F, -3.8F);
        lowerjaw[0].addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
        setRotateAngle(lowerjaw[0], -0.091106186954104F, 0.0F, 0.0F);
        leg_back_left3[0] = new ModelRenderer(this, 33, 21);
        leg_back_left3[0].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_back_left3[0].addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_back_left3[0], 0.8651597102135892F, 0.0F, 0.0F);
        tinyurn2[0] = new ModelRenderer(this, 80, 6);
        tinyurn2[0].setRotationPoint(-1.0F, 6.0F, 3.0F);
        tinyurn2[0].addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F);
        setRotateAngle(tinyurn2[0], 0.136659280431156F, 0.0F, 0.0F);
        leg_back_right3[0] = new ModelRenderer(this, 48, 21);
        leg_back_right3[0].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_back_right3[0].addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_back_right3[0], 0.8651597102135892F, 0.0F, 0.0F);
        ear_right[0] = new ModelRenderer(this, 26, 43);
        ear_right[0].setRotationPoint(-3.0F, 0.0F, 0.0F);
        ear_right[0].addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(ear_right[0], -0.091106186954104F, 0.5462880558742251F, 0.136659280431156F);
        leg_back_right2[0] = new ModelRenderer(this, 48, 12);
        leg_back_right2[0].setRotationPoint(0.0F, 7.0F, 1.0F);
        leg_back_right2[0].addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_back_right2[0], -0.7740535232594852F, 0.0F, 0.0F);
        body_lower[0] = new ModelRenderer(this, 0, 15);
        body_lower[0].setRotationPoint(0.0F, 0.0F, 0.0F);
        body_lower[0].addBox(-4.01F, 0.0F, 0.0F, 8, 6, 8, 0.0F);
        setRotateAngle(body_lower[0], -0.22759093446006054F, 0.0F, 0.0F);
        teeth_upper1[0] = new ModelRenderer(this, 27, 60);
        teeth_upper1[0].setRotationPoint(0.0F, 1.0F, -3.0F);
        teeth_upper1[0].addBox(-1.0F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        setRotateAngle(teeth_upper1[0], -0.136659280431156F, 0.045553093477052F, 0.0F);
        leg_front_right3[0] = new ModelRenderer(this, 48, 46);
        leg_front_right3[0].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_front_right3[0].addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_front_right3[0], 0.5918411493512771F, 0.0F, 0.0F);
        tinyurn3[0] = new ModelRenderer(this, 80, 13);
        tinyurn3[0].setRotationPoint(-4.0F, 4.0F, 3.0F);
        tinyurn3[0].addBox(-1.3F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tinyurn3[0], 0.136659280431156F, 0.0F, 0.045553093477052F);
        head1[0] = new ModelRenderer(this, 0, 39);
        head1[0].setRotationPoint(0.0F, 0.0F, -3.0F);
        head1[0].addBox(-3.0F, 0.0F, -4.0F, 6, 5, 4, 0.0F);
        setRotateAngle(head1[0], 0.22759093446006054F, 0.0F, 0.0F);
        tail3[0] = new ModelRenderer(this, 64, 12);
        tail3[0].setRotationPoint(0.0F, 0.0F, 3.0F);
        tail3[0].addBox(-1.01F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail3[0], -0.22759093446006054F, 0.0F, 0.0F);
        teethlower[0] = new ModelRenderer(this, 22, 60);
        teethlower[0].setRotationPoint(0.0F, 0.0F, -3.0F);
        teethlower[0].addBox(-1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(teethlower[0], -0.36425021489121656F, 0.0F, 0.0F);
        tail1[0] = new ModelRenderer(this, 64, 0);
        tail1[0].setRotationPoint(0.0F, 0.0F, 8.0F);
        tail1[0].addBox(-1.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail1[0], -0.40980330836826856F, 0.0F, 0.0F);
        tail4[0] = new ModelRenderer(this, 64, 18);
        tail4[0].setRotationPoint(0.0F, 2.0F, 3.0F);
        tail4[0].addBox(-1.01F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail4[0], 0.22759093446006054F, 0.0F, 0.0F);
        leg_front_left3[0] = new ModelRenderer(this, 33, 46);
        leg_front_left3[0].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_front_left3[0].addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_front_left3[0], 0.5918411493512771F, 0.0F, 0.0F);
        snout[0] = new ModelRenderer(this, 0, 49);
        snout[0].setRotationPoint(0.0F, 0.0F, -4.0F);
        snout[0].addBox(-2.0F, 0.0F, -3.0F, 4, 2, 3, 0.0F);
        setRotateAngle(snout[0], 0.045553093477052F, 0.0F, 0.0F);
        leg_front_right2[0] = new ModelRenderer(this, 48, 37);
        leg_front_right2[0].setRotationPoint(0.0F, 5.0F, 2.0F);
        leg_front_right2[0].addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_front_right2[0], -0.5918411493512771F, 0.0F, 0.0F);
        neck[0] = new ModelRenderer(this, 0, 30);
        neck[0].setRotationPoint(0.0F, 0.0F, -8.0F);
        neck[0].addBox(-3.01F, 0.0F, -3.0F, 6, 5, 3, 0.0F);
        setRotateAngle(neck[0], 0.27314402793711257F, 0.0F, 0.0F);
        leg_front_left1[0] = new ModelRenderer(this, 33, 27);
        leg_front_left1[0].setRotationPoint(4.0F, 2.0F, -6.0F);
        leg_front_left1[0].addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        setRotateAngle(leg_front_left1[0], 0.27314402793711257F, 0.0F, -0.136659280431156F);
        tail2[0] = new ModelRenderer(this, 64, 6);
        tail2[0].setRotationPoint(0.0F, 0.0F, 3.0F);
        tail2[0].addBox(-1.01F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail2[0], -0.22759093446006054F, 0.0F, 0.0F);
        leg_back_left2[0] = new ModelRenderer(this, 33, 12);
        leg_back_left2[0].setRotationPoint(0.0F, 7.0F, 1.0F);
        leg_back_left2[0].addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_back_left2[0], -0.7740535232594852F, 0.0F, 0.0F);
        leg_back_left1[0] = new ModelRenderer(this, 33, 0);
        leg_back_left1[0].setRotationPoint(4.0F, 0.0F, 7.0F);
        leg_back_left1[0].addBox(-2.0F, 0.0F, -3.0F, 3, 7, 4, 0.0F);
        setRotateAngle(leg_back_left1[0], 0.27314402793711257F, -0.18203784098300857F, 0.0F);
        cheeckybreeky_right[0] = new ModelRenderer(this, 11, 55);
        cheeckybreeky_right[0].setRotationPoint(-2.0F, 0.0F, -3.0F);
        cheeckybreeky_right[0].addBox(0.0F, 0.0F, 0.01F, 2, 4, 3, 0.0F);
        setRotateAngle(cheeckybreeky_right[0], 0.0F, 0.0F, 0.091106186954104F);
        ear_left[0] = new ModelRenderer(this, 21, 43);
        ear_left[0].setRotationPoint(3.0F, 0.0F, 0.0F);
        ear_left[0].addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(ear_left[0], -0.091106186954104F, -0.5462880558742251F, -0.136659280431156F);
        cheekybreeky_left[0] = new ModelRenderer(this, 0, 55);
        cheekybreeky_left[0].setRotationPoint(2.0F, 0.0F, -3.0F);
        cheekybreeky_left[0].addBox(-2.0F, 0.0F, 0.01F, 2, 4, 3, 0.0F);
        setRotateAngle(cheekybreeky_left[0], 0.0F, 0.0F, -0.091106186954104F);
        teeth_upper2[0] = new ModelRenderer(this, 30, 60);
        teeth_upper2[0].setRotationPoint(0.0F, 1.0F, -3.0F);
        teeth_upper2[0].addBox(0.0F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        setRotateAngle(teeth_upper2[0], -0.136659280431156F, -0.045553093477052F, 0.0F);
        body_main[0].addChild(leg_front_right1[0]);
        body_lower[0].addChild(tinyurn1[0]);
        leg_front_left1[0].addChild(leg_front_left2[0]);
        body_lower[0].addChild(leg_back_right1[0]);
        head1[0].addChild(lowerjaw[0]);
        leg_back_left2[0].addChild(leg_back_left3[0]);
        body_lower[0].addChild(tinyurn2[0]);
        leg_back_right2[0].addChild(leg_back_right3[0]);
        head1[0].addChild(ear_right[0]);
        leg_back_right1[0].addChild(leg_back_right2[0]);
        body_main[0].addChild(body_lower[0]);
        snout[0].addChild(teeth_upper1[0]);
        leg_front_right2[0].addChild(leg_front_right3[0]);
        body_lower[0].addChild(tinyurn3[0]);
        neck[0].addChild(head1[0]);
        tail2[0].addChild(tail3[0]);
        lowerjaw[0].addChild(teethlower[0]);
        body_lower[0].addChild(tail1[0]);
        tail3[0].addChild(tail4[0]);
        leg_front_left2[0].addChild(leg_front_left3[0]);
        head1[0].addChild(snout[0]);
        leg_front_right1[0].addChild(leg_front_right2[0]);
        body_main[0].addChild(neck[0]);
        body_main[0].addChild(leg_front_left1[0]);
        tail1[0].addChild(tail2[0]);
        leg_back_left1[0].addChild(leg_back_left2[0]);
        body_lower[0].addChild(leg_back_left1[0]);
        snout[0].addChild(cheeckybreeky_right[0]);
        head1[0].addChild(ear_left[0]);
        snout[0].addChild(cheekybreeky_left[0]);
        snout[0].addChild(teeth_upper2[0]);
        
     // Standing model
        leg_back_right1[1] = new ModelRenderer(this, 48, 0);
        leg_back_right1[1].setRotationPoint(-4.0F, 4.0F, 6.0F);
        leg_back_right1[1].addBox(-1.0F, -1.0F, -3.0F, 3, 7, 4, 0.0F);
        setRotateAngle(leg_back_right1[1], 1.6390387005478748F, -0.045553093477052F, 0.0F);
        tinyurn2[1] = new ModelRenderer(this, 80, 6);
        tinyurn2[1].setRotationPoint(-1.0F, 6.0F, 3.0F);
        tinyurn2[1].addBox(-1.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F);
        setRotateAngle(tinyurn2[1], 1.1383037381507017F, 0.0F, 0.0F);
        leg_front_right2[1] = new ModelRenderer(this, 48, 37);
        leg_front_right2[1].setRotationPoint(0.0F, 5.0F, 2.0F);
        leg_front_right2[1].addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_front_right2[1], -0.27314402793711257F, 0.0F, 0.0F);
        body_main[1] = new ModelRenderer(this, 0, 0);
        body_main[1].setRotationPoint(0.0F, 8.0F, 4.0F);
        body_main[1].addBox(-4.0F, 0.0F, -8.0F, 8, 6, 8, 0.0F);
        setRotateAngle(body_main[1], -0.7740535232594852F, 0.0F, 0.0F);
        leg_front_right1[1] = new ModelRenderer(this, 48, 27);
        leg_front_right1[1].setRotationPoint(-4.0F, 2.0F, -6.0F);
        leg_front_right1[1].addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        setRotateAngle(leg_front_right1[1], 0.9105382707654417F, -0.31869712141416456F, -0.091106186954104F);
        neck[1] = new ModelRenderer(this, 0, 30);
        neck[1].setRotationPoint(0.0F, 0.0F, -8.0F);
        neck[1].addBox(-3.01F, 0.0F, -3.0F, 6, 5, 3, 0.0F);
        setRotateAngle(neck[1], 0.36425021489121656F, 0.0F, 0.0F);
        cheeckybreeky_right[1] = new ModelRenderer(this, 11, 55);
        cheeckybreeky_right[1].setRotationPoint(-2.0F, 0.0F, -3.0F);
        cheeckybreeky_right[1].addBox(0.0F, 0.0F, 0.01F, 2, 4, 3, 0.0F);
        setRotateAngle(cheeckybreeky_right[1], 0.0F, 0.0F, 0.091106186954104F);
        body_lower[1] = new ModelRenderer(this, 0, 15);
        body_lower[1].setRotationPoint(0.0F, 0.0F, 0.0F);
        body_lower[1].addBox(-4.01F, 0.0F, 0.0F, 8, 6, 8, 0.0F);
        setRotateAngle(body_lower[1], -0.40980330836826856F, 0.0F, 0.0F);
        head1[1] = new ModelRenderer(this, 0, 39);
        head1[1].setRotationPoint(0.0F, 0.0F, -3.0F);
        head1[1].addBox(-3.0F, 0.0F, -4.0F, 6, 5, 4, 0.0F);
        setRotateAngle(head1[1], 0.5462880558742251F, 0.0F, 0.0F);
        tail1[1] = new ModelRenderer(this, 64, 0);
        tail1[1].setRotationPoint(0.0F, 2.0F, 8.0F);
        tail1[1].addBox(-1.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail1[1], 0.36425021489121656F, 0.0F, 0.0F);
        leg_front_left2[1] = new ModelRenderer(this, 33, 37);
        leg_front_left2[1].setRotationPoint(0.0F, 5.0F, 2.0F);
        leg_front_left2[1].addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_front_left2[1], -0.27314402793711257F, 0.0F, 0.0F);
        snout[1] = new ModelRenderer(this, 0, 49);
        snout[1].setRotationPoint(0.0F, 0.0F, -4.0F);
        snout[1].addBox(-2.0F, 0.0F, -3.0F, 4, 2, 3, 0.0F);
        setRotateAngle(snout[1], 0.045553093477052F, 0.0F, 0.0F);
        teeth_upper2[1] = new ModelRenderer(this, 30, 60);
        teeth_upper2[1].setRotationPoint(0.0F, 1.0F, -3.0F);
        teeth_upper2[1].addBox(0.0F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        setRotateAngle(teeth_upper2[1], -0.136659280431156F, -0.045553093477052F, 0.0F);
        tail4[1] = new ModelRenderer(this, 64, 18);
        tail4[1].setRotationPoint(0.0F, 0.0F, 3.0F);
        tail4[1].addBox(-1.01F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail4[1], 0.22759093446006054F, 0.0F, 0.0F);
        leg_front_left1[1] = new ModelRenderer(this, 33, 27);
        leg_front_left1[1].setRotationPoint(4.0F, 2.0F, -6.0F);
        leg_front_left1[1].addBox(-1.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        setRotateAngle(leg_front_left1[1], 0.9105382707654417F, 0.31869712141416456F, 0.091106186954104F);
        teeth_upper1[1] = new ModelRenderer(this, 27, 60);
        teeth_upper1[1].setRotationPoint(0.0F, 1.0F, -3.0F);
        teeth_upper1[1].addBox(-1.0F, 0.0F, 0.0F, 1, 4, 0, 0.0F);
        setRotateAngle(teeth_upper1[1], -0.136659280431156F, 0.045553093477052F, 0.0F);
        tinyurn3[1] = new ModelRenderer(this, 80, 13);
        tinyurn3[1].setRotationPoint(-4.0F, 4.0F, 3.0F);
        tinyurn3[1].addBox(-1.3F, 0.0F, -1.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tinyurn3[1], 1.1838568316277536F, -0.22759093446006054F, 0.045553093477052F);
        leg_back_left2[1] = new ModelRenderer(this, 33, 12);
        leg_back_left2[1].setRotationPoint(0.0F, 6.0F, 1.0F);
        leg_back_left2[1].addBox(-1.01F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_back_left2[1], -1.0927506446736497F, 0.0F, 0.0F);
        lowerjaw[1] = new ModelRenderer(this, 22, 55);
        lowerjaw[1].setRotationPoint(0.0F, 4.0F, -3.8F);
        lowerjaw[1].addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3, 0.0F);
        setRotateAngle(lowerjaw[1], 0.6373942428283291F, 0.0F, 0.0F);
        leg_front_left3[1] = new ModelRenderer(this, 33, 46);
        leg_front_left3[1].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_front_left3[1].addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_front_left3[1], 1.1838568316277536F, 0.0F, 0.0F);
        tail3[1] = new ModelRenderer(this, 64, 12);
        tail3[1].setRotationPoint(0.0F, 0.0F, 3.0F);
        tail3[1].addBox(-1.01F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail3[1], 0.22759093446006054F, 0.0F, 0.0F);
        tinyurn1[1] = new ModelRenderer(this, 80, 0);
        tinyurn1[1].setRotationPoint(2.0F, 6.0F, 3.0F);
        tinyurn1[1].addBox(-1.0F, 0.0F, -2.0F, 2, 3, 2, 0.0F);
        setRotateAngle(tinyurn1[1], 1.1383037381507017F, 0.0F, 0.0F);
        leg_back_right2[1] = new ModelRenderer(this, 48, 12);
        leg_back_right2[1].setRotationPoint(0.0F, 6.0F, 1.0F);
        leg_back_right2[1].addBox(-0.99F, 0.0F, -2.0F, 2, 6, 2, 0.0F);
        setRotateAngle(leg_back_right2[1], -1.0927506446736497F, 0.0F, 0.0F);
        ear_right[1] = new ModelRenderer(this, 26, 43);
        ear_right[1].setRotationPoint(-3.0F, 0.0F, 0.0F);
        ear_right[1].addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(ear_right[1], -0.091106186954104F, 0.5462880558742251F, 0.136659280431156F);
        teethlower[1] = new ModelRenderer(this, 22, 60);
        teethlower[1].setRotationPoint(0.0F, 0.0F, -3.0F);
        teethlower[1].addBox(-1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(teethlower[1], -0.36425021489121656F, 0.0F, 0.0F);
        tail2[1] = new ModelRenderer(this, 64, 6);
        tail2[1].setRotationPoint(0.0F, 0.0F, 3.0F);
        tail2[1].addBox(-1.01F, -2.0F, 0.0F, 2, 2, 3, 0.0F);
        setRotateAngle(tail2[1], 0.22759093446006054F, 0.0F, 0.0F);
        leg_back_left1[1] = new ModelRenderer(this, 33, 0);
        leg_back_left1[1].setRotationPoint(4.0F, 4.0F, 6.0F);
        leg_back_left1[1].addBox(-2.0F, -1.0F, -3.0F, 3, 7, 4, 0.0F);
        setRotateAngle(leg_back_left1[1], 1.6390387005478748F, 0.045553093477052F, 0.0F);
        leg_back_left3[1] = new ModelRenderer(this, 33, 21);
        leg_back_left3[1].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_back_left3[1].addBox(-1.02F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_back_left3[1], 0.7740535232594852F, 0.0F, 0.0F);
        ear_left[1] = new ModelRenderer(this, 21, 43);
        ear_left[1].setRotationPoint(3.0F, 0.0F, 0.0F);
        ear_left[1].addBox(-1.0F, 0.0F, 0.0F, 2, 2, 0, 0.0F);
        setRotateAngle(ear_left[1], -0.091106186954104F, -0.5462880558742251F, -0.136659280431156F);
        leg_back_right3[1] = new ModelRenderer(this, 48, 21);
        leg_back_right3[1].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_back_right3[1].addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_back_right3[1], 0.7740535232594852F, 0.0F, 0.0F);
        cheekybreeky_left[1] = new ModelRenderer(this, 0, 55);
        cheekybreeky_left[1].setRotationPoint(2.0F, 0.0F, -3.0F);
        cheekybreeky_left[1].addBox(-2.0F, 0.0F, 0.01F, 2, 4, 3, 0.0F);
        setRotateAngle(cheekybreeky_left[1], 0.0F, 0.0F, -0.091106186954104F);
        leg_front_right3[1] = new ModelRenderer(this, 48, 46);
        leg_front_right3[1].setRotationPoint(0.0F, 6.0F, 0.0F);
        leg_front_right3[1].addBox(-0.98F, -2.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(leg_front_right3[1], 1.1838568316277536F, 0.0F, 0.0F);
        body_lower[1].addChild(leg_back_right1[1]);
        body_lower[1].addChild(tinyurn2[1]);
        leg_front_right1[1].addChild(leg_front_right2[1]);
        body_main[1].addChild(leg_front_right1[1]);
        body_main[1].addChild(neck[1]);
        snout[1].addChild(cheeckybreeky_right[1]);
        body_main[1].addChild(body_lower[1]);
        neck[1].addChild(head1[1]);
        body_lower[1].addChild(tail1[1]);
        leg_front_left1[1].addChild(leg_front_left2[1]);
        head1[1].addChild(snout[1]);
        snout[1].addChild(teeth_upper2[1]);
        tail3[1].addChild(tail4[1]);
        body_main[1].addChild(leg_front_left1[1]);
        snout[1].addChild(teeth_upper1[1]);
        body_lower[1].addChild(tinyurn3[1]);
        leg_back_left1[1].addChild(leg_back_left2[1]);
        head1[1].addChild(lowerjaw[1]);
        leg_front_left2[1].addChild(leg_front_left3[1]);
        tail2[1].addChild(tail3[1]);
        body_lower[1].addChild(tinyurn1[1]);
        leg_back_right1[1].addChild(leg_back_right2[1]);
        head1[1].addChild(ear_right[1]);
        lowerjaw[1].addChild(teethlower[1]);
        tail1[1].addChild(tail2[1]);
        body_lower[1].addChild(leg_back_left1[1]);
        leg_back_left2[1].addChild(leg_back_left3[1]);
        head1[1].addChild(ear_left[1]);
        leg_back_right2[1].addChild(leg_back_right3[1]);
        snout[1].addChild(cheekybreeky_left[1]);
        leg_front_right2[1].addChild(leg_front_right3[1]);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
    	EntityCryptCrawler cryptCrawler = (EntityCryptCrawler) entity;
    	
    	if(!cryptCrawler.isBiped())
    		body_main[0].render(scale);
    	else
    		body_main[1].render(scale);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		EntityCryptCrawler crypt_crawler = (EntityCryptCrawler) entity;
		float heady = MathHelper.sin((rotationYaw / (180F / (float) Math.PI)) * 0.5F);
		float headx = MathHelper.sin((rotationPitch / (180F / (float) Math.PI)) * 0.5F);
		if(!crypt_crawler.isBiped())
			neck[0].rotateAngleY = heady;
		else {
			neck[1].rotateAngleY = heady;
			neck[1].rotateAngleX = 0.36425021489121656F + headx;
			}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialRenderTicks) {

		EntityCryptCrawler crypt_crawler = (EntityCryptCrawler) entity;

		if(!crypt_crawler.isBiped()) {
			float animation = MathHelper.sin((limbSwing * 0.6F + 2) * 0.5F) * 0.3F * limbSwingAngle * 0.3F;
			float animation2 = MathHelper.sin((limbSwing * 0.6F) * 0.5F) * 0.3F * limbSwingAngle * 0.3F;
			float animation3 = MathHelper.sin((limbSwing * 0.6F + 4) * 0.5F) * 0.3F * limbSwingAngle * 0.3F;
			float flap = MathHelper.sin((crypt_crawler.ticksExisted) * 0.3F) * 0.8F;
			float standingAngle = crypt_crawler.smoothedAngle(partialRenderTicks); 
			tail1[0].rotateAngleX = -0.40980330836826856F + (standingAngle * 0.75F)- animation * 1F;
			tail2[0].rotateAngleX = -0.22759093446006054F + (standingAngle * 0.75F)- animation * 3F;
			tail3[0].rotateAngleX = -0.22759093446006054F + (standingAngle * 0.75F)- animation * 4F;
			tail4[0].rotateAngleX = 0.22759093446006054F + (standingAngle * 0.75F)- animation * 5F;

		    tinyurn1[0].rotateAngleX = 0.136659280431156F + (standingAngle);
		    tinyurn2[0].rotateAngleX = 0.136659280431156F + (standingAngle);
		    tinyurn3[0].rotateAngleX = 0.136659280431156F + (standingAngle);
	
			if (!(crypt_crawler.standingAngle > 0)) {

				leg_front_right1[0].rotateAngleX = 0.27314402793711257F + (animation2 * 8F) + flap * 0.05F;
				leg_front_right2[0].rotateAngleX = -0.5918411493512771F + (animation2 * 6F) - flap * 0.025F;
				leg_front_right3[0].rotateAngleX = 0.5918411493512771F -(standingAngle*1.25F) -0.17453292519943295F - animation2 * 18F + flap * 0.05F;

				leg_front_left1[0].rotateAngleX = 0.27314402793711257F + (animation * 8F) + flap * 0.05F;
				leg_front_left2[0].rotateAngleX = -0.5918411493512771F + (animation * 6F) - flap * 0.025F;
				leg_front_left3[0].rotateAngleX = 0.5918411493512771F -(standingAngle * 1.25F) -0.17453292519943295F - (animation * 18F) + flap * 0.05F;

				leg_back_right1[0].rotateAngleX = 0.27314402793711257F -(standingAngle * 0.75F) -0.1F - (animation2 * 6F) - flap * 0.05F;
				leg_back_left1[0].rotateAngleX = 0.27314402793711257F -(standingAngle * 0.75F) -0.1F - (animation3 * 6F) - flap * 0.05F;
	
				leg_back_right1[0].rotateAngleY = 0.18203784098300857F - (standingAngle * 0.0625F);
				leg_back_left1[0].rotateAngleY = -0.18203784098300857F + (standingAngle * 0.0625F);

				leg_back_right2[0].rotateAngleX = standingAngle -0.7740535232594852F + (leg_back_right1[0].rotateAngleX + animation2) + flap * 0.1F;
				leg_back_left2[0].rotateAngleX = standingAngle -0.7740535232594852F + (leg_back_left1[0].rotateAngleX + animation3) + flap * 0.1F;

				leg_back_right3[0].rotateAngleX = 0.8651597102135892F -(standingAngle * 0.5F) - (leg_back_right1[0].rotateAngleX * 1.25F) - flap * 0.05F;
				leg_back_left3[0].rotateAngleX = 0.8651597102135892F -(standingAngle * 0.5F) - (leg_back_left1[0].rotateAngleX * 1.25F) - flap * 0.05F;

				body_main[0].rotateAngleX = 0.091106186954104F - standingAngle - (animation2 * 3F) - flap * 0.025F;
				body_main[0].rotateAngleZ = 0F - animation2 * 1.5F;

				body_lower[0].rotateAngleX = -0.22759093446006054F + standingAngle + (animation2 * 2F) + flap * 0.05F;

				neck[0].rotateAngleX = 0.27314402793711257F -(standingAngle * 0.5F) -0.17453292519943295F + (animation2 * 2.9F) + flap * 0.025F;
				head1[0].rotateAngleX = -(standingAngle * 0.5F) + 0.17453292519943295F;
				head1[0].rotateAngleZ = -(standingAngle * 0.1F * flap * 6F);
	
			} else {
				leg_front_right1[0].rotateAngleX = 0.27314402793711257F + (standingAngle * 0.5F * flap) + animation2 * 6F;
				leg_front_left1[0].rotateAngleX = 0.27314402793711257F + (standingAngle * 0.5F * flap) + animation * 6F;

				leg_front_right2[0].rotateAngleX = -0.5918411493512771F + animation2 * 5F;
				leg_front_left2[0].rotateAngleX = -0.5918411493512771F + animation * 5F;

				leg_front_right3[0].rotateAngleX = 0.5918411493512771F + (standingAngle * 1.25F) -0.17453292519943295F - animation2 * 12F;
				leg_front_left3[0].rotateAngleX = 0.5918411493512771F + (standingAngle * 1.25F) -0.17453292519943295F - animation * 12F;

				leg_back_right1[0].rotateAngleX = 0.27314402793711257F + (standingAngle * 0.5F) - animation2 * 5F;
				leg_back_left1[0].rotateAngleX =  0.27314402793711257F + (standingAngle * 0.5F) - animation3 * 5F;

				leg_back_right1[0].rotateAngleY = 0.18203784098300857F - (standingAngle * 0.1625F);
				leg_back_left1[0].rotateAngleY = -0.18203784098300857F + (standingAngle * 0.1625F);

				leg_back_right2[0].rotateAngleX = -0.7740535232594852F + (standingAngle * 1.25F) + (leg_back_right1[0].rotateAngleX + animation2);
				leg_back_left2[0].rotateAngleX = -0.7740535232594852F + (standingAngle * 1.25F) + (leg_back_left1[0].rotateAngleX + animation3);

				leg_back_right3[0].rotateAngleX = 0.8651597102135892F +(standingAngle * 0.125F) - leg_back_right1[0].rotateAngleX * 1.25F;
				leg_back_left3[0].rotateAngleX = 0.8651597102135892F +(standingAngle * 0.125F) - leg_back_left1[0].rotateAngleX * 1.25F;

				body_main[0].rotateAngleX = 0.091106186954104F - (standingAngle) - animation2 * 2F;
				body_lower[0].rotateAngleX = -0.22759093446006054F - (standingAngle * 0.5F) + animation2 * 2F;

				neck[0].rotateAngleX = 0.27314402793711257F + (standingAngle * 0.25F) -0.17453292519943295F + animation2 * 2.9F;
				head1[0].rotateAngleX = 0.17453292519943295F + (standingAngle * 0.5F);
			}
			if (!crypt_crawler.onGround)
				lowerjaw[0].rotateAngleX = -0.091106186954104F;
			else {
				if (standingAngle > 0)
					lowerjaw[0].rotateAngleX = 0.091106186954104F + flap * 0.5F;
				else
					lowerjaw[0].rotateAngleX = -0.091106186954104F + flap * 0.3F;
			}
		}
		else {
			float animation = MathHelper.sin(limbSwing * 0.4F) * limbSwingAngle * 0.2F;
			float flap = MathHelper.sin((crypt_crawler.ticksExisted) * 0.3F) * 0.8F;
			tail1[1].rotateAngleX = 0.36425021489121656F - animation * 0.25F;
			tail2[1].rotateAngleX = 0.22759093446006054F - animation * 0.5F;
			tail3[1].rotateAngleX = 0.22759093446006054F - animation * 0.75F;
			tail4[1].rotateAngleX = 0.22759093446006054F - animation * 1F;
			if (!crypt_crawler.isBlocking()) {
				leg_front_right1[1].rotateAngleX = 0.9105382707654417F - 1F + animation * 2F;
				leg_front_left1[1].rotateAngleX = 0.9105382707654417F - 1F - animation * 2F;

				leg_front_right1[1].rotateAngleZ = -0.091106186954104F + 0.25F;
				leg_front_left1[1].rotateAngleZ = 0.091106186954104F - 0.25F;

				leg_front_right1[1].rotateAngleY = -0.31869712141416456F;
				leg_front_left1[1].rotateAngleY = 0.31869712141416456F;

				leg_front_right2[1].rotateAngleX = -0.27314402793711257F;
				leg_front_left2[1].rotateAngleX = -0.27314402793711257F;

				leg_front_right3[1].rotateAngleX = 1.1838568316277536F;
				leg_front_left3[1].rotateAngleX = 1.1838568316277536F;
			}
			else{
				leg_front_right1[1].rotateAngleX = 0.9105382707654417F - 1F;
				leg_front_left1[1].rotateAngleX = 0.9105382707654417F - 1F;

				leg_front_right1[1].rotateAngleZ = -0.091106186954104F + 0.25F;
				leg_front_left1[1].rotateAngleZ = 0.091106186954104F - 0.25F;

				leg_front_right1[1].rotateAngleY = -0.31869712141416456F;
				leg_front_left1[1].rotateAngleY = 0.31869712141416456F;

				leg_front_right2[1].rotateAngleX = -0.27314402793711257F;
				leg_front_left2[1].rotateAngleX = -0.27314402793711257F;

				leg_front_right3[1].rotateAngleX = 1.1838568316277536F;
				leg_front_left3[1].rotateAngleX = 1.1838568316277536F;
			}

			leg_back_right1[1].rotateAngleX = 1.6390387005478748F + animation * 5F - flap * 0.05F;
			leg_back_left1[1].rotateAngleX =  1.6390387005478748F  - animation * 5F - flap * 0.05F;

			leg_back_right1[1].rotateAngleY = -0.045553093477052F;
			leg_back_left1[1].rotateAngleY = -0.045553093477052F;

			leg_back_right2[1].rotateAngleX = -1.0927506446736497F - animation * 5F + flap * 0.05F;
			leg_back_left2[1].rotateAngleX = -1.0927506446736497F + animation * 5F + flap * 0.05F;

			leg_back_right3[1].rotateAngleX = 0.7740535232594852F + animation * 5F - flap * 0.05F;
			leg_back_left3[1].rotateAngleX = 0.7740535232594852F - animation * 5F - flap * 0.05F;
			

			body_main[1].rotateAngleX = -0.7740535232594852F - animation - flap * 0.025F;

			body_lower[1].rotateAngleX = -0.40980330836826856F + animation + flap * 0.05F;

			head1[1].rotateAngleX = 0.5462880558742251F;

			if (!crypt_crawler.onGround)
				lowerjaw[1].rotateAngleX = -0.091106186954104F;
			else
				lowerjaw[1].rotateAngleX = 0.091106186954104F + flap * 0.5F;

			/// EEEHHH fricking annoying angles
			EnumHandSide enumhandside = this.getMainHand(crypt_crawler);
			ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
			if (swingProgress > 0.0F) {
				float f1 = swingProgress;
				body_main[1].rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.5F;

				if (enumhandside == EnumHandSide.LEFT)
					body_main[1].rotateAngleY *= -1.0F;

				f1 = 1.0F - swingProgress;
				f1 = f1 * f1;
				f1 = f1 * f1;
				f1 = 1.0F - f1;
				float f2 = MathHelper.sin(f1 * (float) Math.PI);
				float f3 = MathHelper.sin(swingProgress * (float) Math.PI) * -(head1[1].rotateAngleX + 0.7F) * 0.75F;
				modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 0.8D + (double) f3));
				modelrenderer.rotateAngleY += body_main[1].rotateAngleY;
				modelrenderer.rotateAngleZ += MathHelper.sin(swingProgress * (float) Math.PI) * -0.4F;
			}
		}
	}

	public void postRenderArm(float scale, EnumHandSide side) {
		getArmForSide(side).postRender(scale);
	}

	protected ModelRenderer getArmForSide(EnumHandSide side) {
		return side == EnumHandSide.LEFT ? leg_front_left1[1] : leg_front_right1[1];
	}

	protected EnumHandSide getMainHand(Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
			EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
			return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
		} else
			return EnumHandSide.RIGHT;
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

	public boolean getIsBlocking() {
		// TODO Auto-generated method stub
		return false;
	}
}