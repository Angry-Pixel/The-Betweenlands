package thebetweenlands.client.render.model.armor;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAncientArmour extends ModelBodyAttachment {
    public ModelRenderer helmet_mainrotation;
    public ModelRenderer leggings_mainrotation;
    public ModelRenderer chestplate_mainrotation;
    public ModelRenderer chestplate_armrotation_left;
    public ModelRenderer chestplate_armrotation_right;
    public ModelRenderer boots_left_main;
    public ModelRenderer boots_right_main;
    public ModelRenderer topplate1;
    public ModelRenderer maincover;
    public ModelRenderer foreheadpiece;
    public ModelRenderer topplate2;
    public ModelRenderer antler_left1a;
    public ModelRenderer antler_right1a;
    public ModelRenderer sideplate1;
    public ModelRenderer sideplate2;
    public ModelRenderer antler_left1b;
    public ModelRenderer antler_left1c;
    public ModelRenderer antlers_left1j;
    public ModelRenderer antler_left1d;
    public ModelRenderer antler_left1e;
    public ModelRenderer antlers_left1f;
    public ModelRenderer antlers_left1g;
    public ModelRenderer antlers_left1h;
    public ModelRenderer antlers_left1i;
    public ModelRenderer antler_right1b;
    public ModelRenderer antler_right1c;
    public ModelRenderer antlers_right1j;
    public ModelRenderer antler_right1d;
    public ModelRenderer antler_right1e;
    public ModelRenderer antlers_right1f;
    public ModelRenderer antlers_right1g;
    public ModelRenderer antlers_right1h;
    public ModelRenderer antlers_right1i;
    public ModelRenderer belt_main;
    public ModelRenderer chainmail_leg_left;
    public ModelRenderer chainmail_leg_right;
    public ModelRenderer belt_add1;
    public ModelRenderer bone1a;
    public ModelRenderer bone2a;
    public ModelRenderer bone3a;
    public ModelRenderer belt_add2;
    public ModelRenderer bone1b;
    public ModelRenderer bone2b;
    public ModelRenderer bone3b;
    public ModelRenderer chestpiece_left1;
    public ModelRenderer chestpiece_right1;
    public ModelRenderer lowerplate_main;
    public ModelRenderer chestpiece_left2;
    public ModelRenderer chestpiece_right2;
    public ModelRenderer lowerplate_left;
    public ModelRenderer lowerplate_right;
    public ModelRenderer chainmail;
    public ModelRenderer shoulderpad_left;
    public ModelRenderer shoulderpad_right;
    public ModelRenderer boots_left_toes;
    public ModelRenderer boots_left_bone;
    public ModelRenderer boots_right_toes;
    public ModelRenderer boots_right_bone;

    public ModelRenderer helmetParts[];
    public ModelRenderer chestParts[];
    public ModelRenderer legParts[];
    public ModelRenderer bootParts[];

    public ModelAncientArmour() {
        textureWidth = 128;
        textureHeight = 64;
        topplate1 = new ModelRenderer(this, 0, 0);
        topplate1.setRotationPoint(0.0F, -7.0F, -1.5F);
        topplate1.addBox(-4.5F, -1.0F, -3.0F, 9, 3, 3, 0.0F);
        boots_right_bone = new ModelRenderer(this, 109, 56);
        boots_right_bone.setRotationPoint(2.0F, 0.01F, -1.0F);
        boots_right_bone.addBox(-3.0F, 0.0F, -1.0F, 3, 2, 1, 0.0F);
        setRotateAngle(boots_right_bone, 0.0F, 0.136659280431156F, 0.0F);
        lowerplate_left = new ModelRenderer(this, 75, 25);
        lowerplate_left.setRotationPoint(1.5F, 1.0F, 0.0F);
        lowerplate_left.addBox(0.0F, 0.0F, -2.5F, 3, 1, 5, 0.0F);
        setRotateAngle(lowerplate_left, 0.0F, 0.0F, -0.091106186954104F);
        chestpiece_left1 = new ModelRenderer(this, 75, 0);
        chestpiece_left1.setRotationPoint(0.0F, 1.0F, -3.5F);
        chestpiece_left1.addBox(0.0F, 0.0F, 0.0F, 6, 3, 6, 0.0F);
        setRotateAngle(chestpiece_left1, 0.0F, -0.136659280431156F, 0.0F);
        leggings_mainrotation = new ModelRenderer(this, 0, 0);
        leggings_mainrotation.setRotationPoint(0.0F, 10.0F, 0.0F);
        leggings_mainrotation.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        belt_add1 = new ModelRenderer(this, 45, 8);
        belt_add1.setRotationPoint(-2.5F, 2.0F, 0.0F);
        belt_add1.addBox(0.0F, 0.0F, -2.5F, 2, 1, 5, 0.0F);
        antlers_right1g = new ModelRenderer(this, 33, 30);
        antlers_right1g.mirror = true;
        antlers_right1g.setRotationPoint(0.0F, 0.0F, 2.0F);
        antlers_right1g.addBox(-0.01F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_right1g, 0.40980330836826856F, 0.0F, 0.0F);
        antlers_left1f = new ModelRenderer(this, 33, 25);
        antlers_left1f.setRotationPoint(0.0F, 0.0F, 1.0F);
        antlers_left1f.addBox(-2.0F, -2.01F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_left1f, 0.0F, -0.22759093446006054F, 0.0F);
        antlers_right1h = new ModelRenderer(this, 33, 35);
        antlers_right1h.mirror = true;
        antlers_right1h.setRotationPoint(0.0F, 0.0F, 2.0F);
        antlers_right1h.addBox(-0.02F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_right1h, 0.7740535232594852F, 0.0F, 0.0F);
        chainmail_leg_right = new ModelRenderer(this, 45, 42);
        chainmail_leg_right.setRotationPoint(-1.9F, 2.0F, 0.0F);
        chainmail_leg_right.addBox(-2.0F, 0.0F, -1.985F, 4, 8, 4, 0.0F);
        bone3a = new ModelRenderer(this, 45, 23);
        bone3a.setRotationPoint(0.0F, 0.0F, 2.5F);
        bone3a.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 1, 0.0F);
        setRotateAngle(bone3a, 0.0F, 0.0F, 0.045553093477052F);
        shoulderpad_right = new ModelRenderer(this, 100, 41);
        shoulderpad_right.setRotationPoint(0.0F, 0.0F, 0.0F);
        shoulderpad_right.addBox(-3.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        setRotateAngle(shoulderpad_right, 0.0F, 0.0F, 0.045553093477052F);
        belt_add2 = new ModelRenderer(this, 45, 15);
        belt_add2.setRotationPoint(0.0F, 1.0F, 0.0F);
        belt_add2.addBox(-2.0F, -1.0F, -2.5F, 2, 2, 5, 0.0F);
        setRotateAngle(belt_add2, 0.0F, 0.0F, 0.045553093477052F);
        chestplate_mainrotation = new ModelRenderer(this, 0, 0);
        chestplate_mainrotation.setRotationPoint(0.0F, 0.0F, 0.0F);
        chestplate_mainrotation.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        boots_left_toes = new ModelRenderer(this, 81, 52);
        boots_left_toes.setRotationPoint(0.0F, 9.5F, -2.0F);
        boots_left_toes.addBox(-2.0F, 0.0F, -1.0F, 4, 2, 1, 0.0F);
        setRotateAngle(boots_left_toes, 0.136659280431156F, 0.0F, 0.0F);
        topplate2 = new ModelRenderer(this, 0, 10);
        topplate2.setRotationPoint(0.0F, -1.0F, 0.0F);
        topplate2.addBox(-4.5F, 0.0F, 0.0F, 9, 3, 6, 0.0F);
        setRotateAngle(topplate2, -0.091106186954104F, 0.0F, 0.0F);
        chainmail_leg_left = new ModelRenderer(this, 45, 30);
        chainmail_leg_left.setRotationPoint(1.9F, 2.0F, 0.0F);
        chainmail_leg_left.addBox(-3.0F, -0.3F, -2.0F, 5, 7, 4, 0.0F);
        lowerplate_right = new ModelRenderer(this, 92, 25);
        lowerplate_right.setRotationPoint(-1.5F, 1.0F, 0.0F);
        lowerplate_right.addBox(-3.0F, 0.0F, -2.5F, 3, 1, 5, 0.0F);
        setRotateAngle(lowerplate_right, 0.0F, 0.0F, 0.091106186954104F);
        chestpiece_left2 = new ModelRenderer(this, 75, 10);
        chestpiece_left2.setRotationPoint(2.0F, 0.0F, 0.0F);
        chestpiece_left2.addBox(0.0F, -1.0F, 0.0F, 4, 1, 6, 0.0F);
        bone2a = new ModelRenderer(this, 60, 14);
        bone2a.setRotationPoint(-3.0F, -0.05F, -2.5F);
        bone2a.addBox(-2.0F, 0.0F, -0.5F, 2, 2, 3, 0.0F);
        setRotateAngle(bone2a, 0.0F, 0.136659280431156F, 0.045553093477052F);
        boots_right_main = new ModelRenderer(this, 92, 52);
        boots_right_main.setRotationPoint(-1.9F, 12.0F, 0.0F);
        boots_right_main.addBox(-2.0F, 4.5F, -2.0F, 4, 7, 4, 0.0F);
        antlers_right1j = new ModelRenderer(this, 33, 45);
        antlers_right1j.mirror = true;
        antlers_right1j.setRotationPoint(-2.0F, -1.0F, 1.0F);
        antlers_right1j.addBox(0.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F);
        setRotateAngle(antlers_right1j, 0.0F, 0.0F, 0.27314402793711257F);
        maincover = new ModelRenderer(this, 0, 32);
        maincover.setRotationPoint(0.0F, -6.0F, 0.0F);
        maincover.addBox(-4.0F, 0.0F, -4.0F, 8, 8, 8, 0.0F);
        antler_left1b = new ModelRenderer(this, 33, 6);
        antler_left1b.setRotationPoint(0.0F, -1.0F, -1.0F);
        antler_left1b.addBox(-0.01F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(antler_left1b, 0.31869712141416456F, 0.0F, 0.0F);
        chestplate_armrotation_right = new ModelRenderer(this, 0, 0);
        chestplate_armrotation_right.setRotationPoint(-5.0F, 2.0F, 0.0F);
        chestplate_armrotation_right.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        antler_right1a = new ModelRenderer(this, 33, 0);
        antler_right1a.mirror = true;
        antler_right1a.setRotationPoint(-2.5F, -1.5F, -0.5F);
        antler_right1a.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 3, 0.0F);
        setRotateAngle(antler_right1a, 0.136659280431156F, -0.31869712141416456F, -0.27314402793711257F);
        bone2b = new ModelRenderer(this, 60, 20);
        bone2b.setRotationPoint(-2.0F, 2.0F, 0.0F);
        bone2b.addBox(0.0F, 0.0F, -0.5F, 1, 1, 3, 0.0F);
        boots_left_bone = new ModelRenderer(this, 81, 56);
        boots_left_bone.setRotationPoint(-2.0F, 0.01F, -1.0F);
        boots_left_bone.addBox(0.0F, 0.0F, -1.0F, 3, 2, 1, 0.0F);
        setRotateAngle(boots_left_bone, 0.0F, -0.136659280431156F, 0.0F);
        boots_right_toes = new ModelRenderer(this, 109, 52);
        boots_right_toes.setRotationPoint(0.0F, 9.5F, -2.0F);
        boots_right_toes.addBox(-2.0F, 0.0F, -1.0F, 4, 2, 1, 0.0F);
        setRotateAngle(boots_right_toes, 0.136659280431156F, 0.0F, 0.0F);
        antler_left1a = new ModelRenderer(this, 33, 0);
        antler_left1a.setRotationPoint(2.5F, -1.5F, -0.5F);
        antler_left1a.addBox(0.0F, -1.0F, -1.0F, 2, 2, 3, 0.0F);
        setRotateAngle(antler_left1a, 0.136659280431156F, 0.31869712141416456F, 0.27314402793711257F);
        antler_left1c = new ModelRenderer(this, 33, 12);
        antler_left1c.setRotationPoint(0.0F, -1.0F, 2.0F);
        antler_left1c.addBox(0.01F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
        setRotateAngle(antler_left1c, -0.40980330836826856F, 0.0F, 0.0F);
        boots_left_main = new ModelRenderer(this, 64, 52);
        boots_left_main.setRotationPoint(1.9F, 12.0F, 0.0F);
        boots_left_main.addBox(-2.0F, 4.5F, -2.0F, 4, 7, 4, 0.0F);
        sideplate1 = new ModelRenderer(this, 0, 20);
        sideplate1.setRotationPoint(0.0F, 3.0F, 0.0F);
        sideplate1.addBox(-4.5F, 0.0F, 0.0F, 9, 2, 6, 0.0F);
        setRotateAngle(sideplate1, 0.045553093477052F, 0.0F, 0.0F);
        foreheadpiece = new ModelRenderer(this, 0, 7);
        foreheadpiece.setRotationPoint(0.0F, 2.0F, -3.0F);
        foreheadpiece.addBox(-2.5F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        setRotateAngle(foreheadpiece, 0.091106186954104F, 0.0F, 0.0F);
        antlers_left1g = new ModelRenderer(this, 33, 30);
        antlers_left1g.setRotationPoint(0.0F, 0.0F, 2.0F);
        antlers_left1g.addBox(-1.99F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_left1g, 0.40980330836826856F, 0.0F, 0.0F);
        antlers_left1j = new ModelRenderer(this, 33, 45);
        antlers_left1j.setRotationPoint(2.0F, -1.0F, 1.0F);
        antlers_left1j.addBox(-2.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F);
        setRotateAngle(antlers_left1j, 0.0F, 0.0F, -0.27314402793711257F);
        sideplate2 = new ModelRenderer(this, 0, 29);
        sideplate2.setRotationPoint(0.0F, 2.0F, 5.0F);
        sideplate2.addBox(-3.5F, 0.0F, 0.0F, 7, 1, 1, 0.0F);
        setRotateAngle(sideplate2, 0.045553093477052F, 0.0F, 0.0F);
        antlers_left1i = new ModelRenderer(this, 33, 40);
        antlers_left1i.setRotationPoint(0.0F, 0.0F, 2.0F);
        antlers_left1i.addBox(-1.97F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_left1i, 0.7740535232594852F, 0.0F, 0.0F);
        bone1b = new ModelRenderer(this, 60, 11);
        bone1b.setRotationPoint(-0.5F, 1.0F, 0.0F);
        bone1b.addBox(0.0F, 0.0F, -0.99F, 1, 1, 1, 0.0F);
        setRotateAngle(bone1b, 0.0F, 0.0F, -0.136659280431156F);
        chestpiece_right1 = new ModelRenderer(this, 100, 0);
        chestpiece_right1.setRotationPoint(0.0F, 1.0F, -3.5F);
        chestpiece_right1.addBox(-6.0F, 0.0F, 0.0F, 6, 3, 6, 0.0F);
        setRotateAngle(chestpiece_right1, 0.0F, 0.136659280431156F, 0.0F);
        bone1a = new ModelRenderer(this, 60, 8);
        bone1a.setRotationPoint(1.0F, 0.0F, -2.5F);
        bone1a.addBox(-0.5F, 0.0F, -1.0F, 1, 1, 1, 0.0F);
        setRotateAngle(bone1a, 0.0F, 0.0F, 0.045553093477052F);
        antler_left1d = new ModelRenderer(this, 33, 16);
        antler_left1d.setRotationPoint(2.0F, 0.0F, 1.0F);
        antler_left1d.addBox(-2.0F, -0.01F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antler_left1d, 0.0F, -0.22759093446006054F, 0.0F);
        helmet_mainrotation = new ModelRenderer(this, 0, 0);
        helmet_mainrotation.setRotationPoint(0.0F, 0.0F, 0.0F);
        helmet_mainrotation.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        antler_right1e = new ModelRenderer(this, 33, 21);
        antler_right1e.mirror = true;
        antler_right1e.setRotationPoint(0.0F, 2.0F, 2.0F);
        antler_right1e.addBox(-0.01F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        setRotateAngle(antler_right1e, 0.40980330836826856F, 0.0F, 0.0F);
        antlers_left1h = new ModelRenderer(this, 33, 35);
        antlers_left1h.setRotationPoint(0.0F, 0.0F, 2.0F);
        antlers_left1h.addBox(-1.98F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_left1h, 0.7740535232594852F, 0.0F, 0.0F);
        antler_right1b = new ModelRenderer(this, 33, 6);
        antler_right1b.mirror = true;
        antler_right1b.setRotationPoint(0.0F, -1.0F, -1.0F);
        antler_right1b.addBox(-1.99F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
        setRotateAngle(antler_right1b, 0.31869712141416456F, 0.0F, 0.0F);
        chestpiece_right2 = new ModelRenderer(this, 100, 10);
        chestpiece_right2.setRotationPoint(-2.0F, 0.0F, 0.0F);
        chestpiece_right2.addBox(-4.0F, -1.0F, 0.0F, 4, 1, 6, 0.0F);
        belt_main = new ModelRenderer(this, 45, 0);
        belt_main.setRotationPoint(0.0F, 0.0F, 0.0F);
        belt_main.addBox(-4.5F, 0.0F, -2.5F, 9, 2, 5, 0.0F);
        setRotateAngle(belt_main, 0.0F, 0.0F, -0.045553093477052F);
        bone3b = new ModelRenderer(this, 45, 27);
        bone3b.setRotationPoint(0.0F, 2.0F, 0.0F);
        bone3b.addBox(-1.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
        setRotateAngle(bone3b, 0.18203784098300857F, 0.0F, 0.0F);
        shoulderpad_left = new ModelRenderer(this, 100, 32);
        shoulderpad_left.setRotationPoint(0.0F, 0.0F, 0.0F);
        shoulderpad_left.addBox(-1.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        setRotateAngle(shoulderpad_left, 0.0F, 0.0F, -0.045553093477052F);
        antler_right1c = new ModelRenderer(this, 33, 12);
        antler_right1c.mirror = true;
        antler_right1c.setRotationPoint(0.0F, -1.0F, 2.0F);
        antler_right1c.addBox(-2.01F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
        setRotateAngle(antler_right1c, -0.40980330836826856F, 0.0F, 0.0F);
        antler_left1e = new ModelRenderer(this, 33, 21);
        antler_left1e.setRotationPoint(0.0F, 2.0F, 2.0F);
        antler_left1e.addBox(-1.99F, -2.0F, 0.0F, 2, 2, 1, 0.0F);
        setRotateAngle(antler_left1e, 0.40980330836826856F, 0.0F, 0.0F);
        chestplate_armrotation_left = new ModelRenderer(this, 0, 0);
        chestplate_armrotation_left.setRotationPoint(5.0F, 2.0F, 0.0F);
        chestplate_armrotation_left.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        antler_right1d = new ModelRenderer(this, 33, 16);
        antler_right1d.mirror = true;
        antler_right1d.setRotationPoint(-2.0F, 0.0F, 1.0F);
        antler_right1d.addBox(0.0F, -0.01F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antler_right1d, 0.0F, 0.22759093446006054F, 0.0F);
        antlers_right1f = new ModelRenderer(this, 33, 25);
        antlers_right1f.mirror = true;
        antlers_right1f.setRotationPoint(0.0F, 0.0F, 1.0F);
        antlers_right1f.addBox(0.0F, -2.01F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_right1f, 0.0F, 0.22759093446006054F, 0.0F);
        antlers_right1i = new ModelRenderer(this, 33, 40);
        antlers_right1i.mirror = true;
        antlers_right1i.setRotationPoint(0.0F, 0.0F, 2.0F);
        antlers_right1i.addBox(-0.03F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        setRotateAngle(antlers_right1i, 0.7740535232594852F, 0.0F, 0.0F);
        chainmail = new ModelRenderer(this, 75, 32);
        chainmail.setRotationPoint(0.0F, 0.9F, 0.0F);
        chainmail.addBox(-4.0F, 0.0F, -2.0F, 8, 6, 4, 0.0F);
        lowerplate_main = new ModelRenderer(this, 75, 18);
        lowerplate_main.setRotationPoint(0.0F, 4.0F, 0.0F);
        lowerplate_main.addBox(-4.5F, 0.0F, -2.5F, 9, 1, 5, 0.0F);

        helmet_mainrotation.addChild(topplate1);
        topplate1.addChild(topplate2);
        topplate1.addChild(antler_right1a);
        topplate2.addChild(sideplate1);
        topplate1.addChild(foreheadpiece);
        antlers_left1f.addChild(antlers_left1g);
        antler_left1a.addChild(antlers_left1j);
        sideplate1.addChild(sideplate2);
        antlers_left1h.addChild(antlers_left1i);
        topplate1.addChild(antler_left1a);
        antler_left1a.addChild(antler_left1c);
        antler_right1a.addChild(antlers_right1j);
        helmet_mainrotation.addChild(maincover);
        antler_left1a.addChild(antler_left1b);
        antler_left1c.addChild(antler_left1d);
        antler_right1d.addChild(antler_right1e);
        antlers_left1g.addChild(antlers_left1h);
        antler_right1a.addChild(antler_right1b);
        antler_right1a.addChild(antler_right1c);
        antler_left1d.addChild(antler_left1e);
        antler_right1c.addChild(antler_right1d);
        antler_right1e.addChild(antlers_right1f);
        antlers_right1h.addChild(antlers_right1i);
        antlers_right1f.addChild(antlers_right1g);
        antler_left1e.addChild(antlers_left1f);
        antlers_right1g.addChild(antlers_right1h);
       
        boots_left_main.addChild(boots_left_toes);
        boots_left_toes.addChild(boots_left_bone);
        boots_right_main.addChild(boots_right_toes);
        boots_right_toes.addChild(boots_right_bone);

        chestplate_mainrotation.addChild(chestpiece_left1);
        chestpiece_left1.addChild(chestpiece_left2);
        chestplate_mainrotation.addChild(chestpiece_right1);
        chestpiece_right1.addChild(chestpiece_right2);
        chestplate_mainrotation.addChild(lowerplate_main);
        lowerplate_main.addChild(lowerplate_left);
        lowerplate_main.addChild(chainmail);
        chestplate_armrotation_left.addChild(shoulderpad_left);
        chestplate_armrotation_right.addChild(shoulderpad_right);
        lowerplate_main.addChild(lowerplate_right);

        //leggings_mainrotation.addChild(chainmail_leg_right); // not needed to be a child/parent
        //leggings_mainrotation.addChild(chainmail_leg_left);
        leggings_mainrotation.addChild(belt_main);
        belt_main.addChild(belt_add1);
        belt_add1.addChild(belt_add2);
        belt_main.addChild(bone1a);
        bone1a.addChild(bone1b);
        belt_main.addChild(bone2a);
        bone2a.addChild(bone2b);
        belt_main.addChild(bone3a);
        bone3a.addChild(bone3b);

        bipedHead.addChild(helmet_mainrotation);
        bipedBody.addChild(chestplate_mainrotation);
        bipedBody.addChild(leggings_mainrotation);
        bipedRightArm.addChild(chestplate_armrotation_right);
        bipedLeftArm.addChild(chestplate_armrotation_left);
        bipedRightLeg.addChild(boots_right_main);
        bipedLeftLeg.addChild(boots_left_main);
        bipedRightLeg.addChild(chainmail_leg_right);
        bipedLeftLeg.addChild(chainmail_leg_left);

        helmetParts = new ModelRenderer[] {
        	helmet_mainrotation,
			topplate1,
			topplate2,
			sideplate1,
			sideplate2,
			maincover,
			foreheadpiece,
			antler_left1a,
			antler_left1b,
			antler_left1c,
			antler_left1d,
			antler_left1e,
			antlers_left1f,
			antlers_left1g,
			antlers_left1h,
			antlers_left1i,
			antlers_left1j,
			antler_right1a,
			antler_right1b,
			antler_right1c,
			antler_right1d,
			antler_right1e,
			antlers_right1f,
			antlers_right1g,
			antlers_right1h,
			antlers_right1i,
			antlers_right1j	
        };

        chestParts = new ModelRenderer[] {
			chestplate_armrotation_right,
			chestplate_armrotation_left,
			shoulderpad_left,
			shoulderpad_right,
			chestplate_mainrotation,
			chestpiece_left1,
			chestpiece_left2,
			chestpiece_right1,
			chestpiece_right2,
			lowerplate_main,
			lowerplate_left,
			lowerplate_right,
			chainmail
        };

        legParts = new ModelRenderer[] {
        	chainmail_leg_right,
			chainmail_leg_left
        };

        bootParts= new ModelRenderer[] {
        	boots_right_main,
			boots_left_main
        };
    }

    public void renderPostScaledPartBoots() {
		boots_right_main.showModel = true;
		boots_left_main.showModel = true;
		GlStateManager.pushMatrix();
    	for(int x = 0; x < bootParts.length; x++)
    		bootParts[x].postRender(0.06625F);
    	GlStateManager.popMatrix();
    }

    public void renderPostScaledPartLegs() {
		chainmail_leg_right.showModel = true;
		chainmail_leg_left.showModel = true;
		leggings_mainrotation.showModel = true;
		GlStateManager.pushMatrix();
    	for(int x = 0; x < legParts.length; x++)
    		legParts[x].postRender(0.0675F);
    	GlStateManager.popMatrix();
    }

    public void renderPostScaledPartChest() {
		chestplate_mainrotation.showModel = true;
		chestplate_armrotation_right.showModel = true;
		chestplate_armrotation_left.showModel = true;
		GlStateManager.pushMatrix();
    	for(int x = 0; x < chestParts.length; x++)
    		chestParts[x].postRender(0.0675F);
    	GlStateManager.popMatrix();
    }

    public void renderPostScaledPartHelm() {
		helmet_mainrotation.showModel = true;
		GlStateManager.pushMatrix();
    	for(int x = 0; x < helmetParts.length; x++)
    		helmetParts[x].postRender(0.0675F);
    	GlStateManager.popMatrix();
    }

	public void setUpModel(Entity entity) {
		setVisible(false);
		helmet_mainrotation.showModel = false;
		chestplate_mainrotation.showModel = false;
		chestplate_armrotation_right.showModel = false;
		chestplate_armrotation_left.showModel = false;
		boots_right_main.showModel = false;
		boots_left_main.showModel = false;
		leggings_mainrotation.showModel = false;
		chainmail_leg_right.showModel = false;
		chainmail_leg_left.showModel = false;
		if(entity instanceof EntityArmorStand)
			helmet_mainrotation.rotateAngleY = entity.rotationYaw * ((float) Math.PI / 180F);
		
		helmet_mainrotation.setRotationPoint(0F, 0F, 0F);
		chestplate_mainrotation.setRotationPoint(0F, 0F, 0F);
		if(entity.isSneaking()) {
			chestplate_armrotation_right.setRotationPoint(0F, 0F, -0.2F);
			chestplate_armrotation_left.setRotationPoint(0F, 0F, -0.2F);
			leggings_mainrotation.setRotationPoint(0F, 8F, -0.4F);
		}
		else {
			chestplate_armrotation_right.setRotationPoint(0F, 1F, 0F);
			chestplate_armrotation_left.setRotationPoint(0F, 0F, 0F);
			leggings_mainrotation.setRotationPoint(0F, 10F, 0F);
		}
		boots_right_main.setRotationPoint(0F, 0F, 0F);
		boots_left_main.setRotationPoint(0F, 0F, 0F);
		chainmail_leg_right.setRotationPoint(0F, 0F, 0F);
		chainmail_leg_left.setRotationPoint(0F, 0F, 0F);
	}

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
