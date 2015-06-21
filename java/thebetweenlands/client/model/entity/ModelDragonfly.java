package thebetweenlands.client.model.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import thebetweenlands.entities.mobs.EntityDragonFly;

@SideOnly(Side.CLIENT)
public class ModelDragonfly extends ModelBase {
    ModelRenderer torso;
    ModelRenderer leftback1;
    ModelRenderer leftback2;
    ModelRenderer rightfront2;
    ModelRenderer wingleft1;
    ModelRenderer rightfront1;
    ModelRenderer eyeleft;
    ModelRenderer rightmid2;
    ModelRenderer rightmid1;
    ModelRenderer stinger;
    ModelRenderer jawright;
    ModelRenderer jawleft;
    ModelRenderer head1;
    ModelRenderer back;
    ModelRenderer head2;
    ModelRenderer rightback2;
    ModelRenderer wingright2;
    ModelRenderer leftfront2;
    ModelRenderer rightback1;
    ModelRenderer wingright1;
    ModelRenderer antennaright;
    ModelRenderer leftfront1;
    ModelRenderer leftmid2;
    ModelRenderer leftmid1;
    ModelRenderer tail4;
    ModelRenderer antennaleft;
    ModelRenderer eyeright;
    ModelRenderer wingleft2;
    ModelRenderer tail1;
    ModelRenderer tail3;
    ModelRenderer tail2;

    public ModelDragonfly() {
        textureWidth = 128;
        textureHeight = 64;
        tail3 = new ModelRenderer(this, 0, 32);
        tail3.setRotationPoint(0.0F, 18.0F, 8.0F);
        tail3.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        jawleft = new ModelRenderer(this, 30, 19);
        jawleft.setRotationPoint(0.0F, 18.0F, -9.0F);
        jawleft.addBox(0.0F, 0.0F, -4.5F, 1, 2, 1, 0.0F);
        setRotation(jawleft, 0.22307169437408447F, 0.0F, -0.22307169437408447F);
        leftback1 = new ModelRenderer(this, 50, 44);
        leftback1.setRotationPoint(1.0F, 20.0F, -5.0F);
        leftback1.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
        setRotation(leftback1, -0.17453292519943295F, -0.8922123136195012F, 0.17453292519943295F);
        rightfront1 = new ModelRenderer(this, 63, 38);
        rightfront1.setRotationPoint(-1.0F, 20.0F, -7.0F);
        rightfront1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(rightfront1, 0.0F, 2.6954864967800423F, -0.14870205226991687F);
        eyeleft = new ModelRenderer(this, 30, 14);
        eyeleft.setRotationPoint(0.0F, 18.0F, -9.0F);
        eyeleft.addBox(2.5F, -0.5F, -2.5F, 1, 2, 2, 0.0F);
        setRotation(eyeleft, 0.07436110079288481F, 0.3892394006252289F, -0.18589310348033902F);
        leftfront2 = new ModelRenderer(this, 50, 47);
        leftfront2.setRotationPoint(1.0F, 20.0F, -7.0F);
        leftfront2.addBox(4.0F, 1.2F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(leftfront2, 0.0F, 0.4461061568097506F, -0.18587756533739608F);
        leftmid2 = new ModelRenderer(this, 50, 53);
        leftmid2.setRotationPoint(1.0F, 20.0F, -6.0F);
        leftmid2.addBox(3.9F, 1.6F, -0.4F, 1, 4, 1, 0.0F);
        setRotation(leftmid2, 0.0F, -0.2230530784048753F, -0.33457961760731303F);
        rightmid1 = new ModelRenderer(this, 63, 41);
        rightmid1.setRotationPoint(-1.0F, 20.0F, -6.0F);
        rightmid1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(rightmid1, 0.08726646259971647F, -2.9169687788581227F, -0.08726646259971647F);
        back = new ModelRenderer(this, 0, 11);
        back.setRotationPoint(0.0F, 18.200000762939453F, -3.0F);
        back.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2, 0.0F);
        setRotation(back, -0.03717859834432601F, 0.0F, 0.0F);
        rightback1 = new ModelRenderer(this, 65, 44);
        rightback1.setRotationPoint(-1.0F, 20.0F, -5.0F);
        rightback1.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
        setRotation(rightback1, 0.17453292519943295F, -2.249380339970292F, -0.17453292519943295F);
        rightback2 = new ModelRenderer(this, 55, 59);
        rightback2.setRotationPoint(-1.0F, 20.0F, -5.0F);
        rightback2.addBox(4.8F, 1.8F, 0.2F, 1, 4, 1, 0.0F);
        setRotation(rightback2, -0.38083084278516266F, -2.1769491760125272F, 0.48939032225921F);
        head2 = new ModelRenderer(this, 30, 8);
        head2.setRotationPoint(0.0F, 18.0F, -9.0F);
        head2.addBox(-1.5F, -2.9000000953674316F, -4.0F, 3, 3, 2, 0.0F);
        setRotation(head2, 0.7435721755027772F, 0.0F, 0.0F);
        leftfront1 = new ModelRenderer(this, 50, 38);
        leftfront1.setRotationPoint(1.0F, 20.0F, -7.0F);
        leftfront1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(leftfront1, 0.0F, 0.44614329934120184F, 0.14871439337730405F);
        rightmid2 = new ModelRenderer(this, 55, 53);
        rightmid2.setRotationPoint(-1.0F, 20.0F, -6.0F);
        rightmid2.addBox(3.9F, 1.6F, -0.6F, 1, 4, 1, 0.0F);
        setRotation(rightmid2, 0.0F, -2.918539575184918F, 0.33457961760731303F);
        head1 = new ModelRenderer(this, 30, 0);
        head1.setRotationPoint(0.0F, 18.0F, -9.0F);
        head1.addBox(-2.0F, -2.0F, -3.0F, 4, 4, 3, 0.0F);
        setRotation(head1, 0.3717860877513886F, 0.0F, 0.0F);
        wingright2 = new ModelRenderer(this, 50, 21);
        wingright2.setRotationPoint(-1.0F, 16.4F, -6.0F);
        wingright2.addBox(0.0F, -12.0F, 0.0F, 0, 12, 4, 0.0F);
        setRotation(wingright2, -0.17453292519943295F, -0.17453292519943295F, -1.5707963267948966F);
        tail2 = new ModelRenderer(this, 0, 25);
        tail2.setRotationPoint(0.0F, 18.0F, 4.0F);
        tail2.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        tail1 = new ModelRenderer(this, 0, 17);
        tail1.setRotationPoint(0.0F, 18.0F, -1.0F);
        tail1.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 5, 0.0F);
        wingleft1 = new ModelRenderer(this, 59, 0);
        wingleft1.setRotationPoint(1.0F, 16.200000762939453F, -8.0F);
        wingleft1.addBox(0.0F, -16.0F, 0.0F, 0, 16, 4, 0.0F);
        setRotation(wingleft1, 0.1745329052209854F, 0.1745329052209854F, 1.570796012878418F);
        wingleft2 = new ModelRenderer(this, 59, 21);
        wingleft2.setRotationPoint(1.0F, 16.4F, -6.0F);
        wingleft2.addBox(0.0F, -12.0F, 0.0F, 0, 12, 4, 0.0F);
        setRotation(wingleft2, -0.17453292519943295F, 0.17453292519943295F, 1.5707963267948966F);
        antennaleft = new ModelRenderer(this, 30, 23);
        antennaleft.setRotationPoint(0.0F, 18.0F, -9.0F);
        antennaleft.addBox(0.0F, -4.0F, -2.0F, 1, 2, 0, 0.0F);
        setRotation(antennaleft, 0.40896469354629517F, 0.0F, 0.5205006003379822F);
        tail4 = new ModelRenderer(this, 0, 39);
        tail4.setRotationPoint(0.0F, 18.0F, 12.0F);
        tail4.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        antennaright = new ModelRenderer(this, 33, 23);
        antennaright.setRotationPoint(0.0F, 18.0F, -9.0F);
        antennaright.addBox(-1.0F, -4.0F, -2.0F, 1, 2, 0, 0.0F);
        setRotation(antennaright, 0.40896469354629517F, 0.0F, -0.5204920768737793F);
        torso = new ModelRenderer(this, 0, 0);
        torso.setRotationPoint(0.0F, 18.0F, -9.0F);
        torso.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6, 0.0F);
        setRotation(torso, -0.07435719668865202F, 0.0F, 0.0F);
        rightfront2 = new ModelRenderer(this, 55, 47);
        rightfront2.setRotationPoint(-1.0F, 20.0F, -7.0F);
        rightfront2.addBox(4.0F, 1.2F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(rightfront2, 0.0F, 2.6954864967800423F, 0.18587756533739608F);
        leftback2 = new ModelRenderer(this, 50, 59);
        leftback2.setRotationPoint(1.0F, 20.0F, -5.0F);
        leftback2.addBox(4.8F, 1.8F, -1.1F, 1, 4, 1, 0.0F);
        setRotation(leftback2, 0.38083084278516266F, -0.964643477577266F, -0.48939032225921F);
        stinger = new ModelRenderer(this, 0, 46);
        stinger.setRotationPoint(0.0F, 17.0F, 16.0F);
        stinger.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotation(stinger, -0.4363322854042054F, 0.0F, 0.0F);
        eyeright = new ModelRenderer(this, 37, 14);
        eyeright.setRotationPoint(0.0F, 18.0F, -9.0F);
        eyeright.addBox(2.5F, -0.5F, 0.5F, 1, 2, 2, 0.0F);
        setRotation(eyeright, -0.07435102613495843F, 2.7698375229150005F, 0.18587756533739608F);
        jawright = new ModelRenderer(this, 35, 19);
        jawright.setRotationPoint(0.0F, 18.0F, -9.0F);
        jawright.addBox(-1.0F, 0.0F, -4.5F, 1, 2, 1, 0.0F);
        setRotation(jawright, 0.22307050228118896F, 0.0F, 0.22307050228118896F);
        leftmid1 = new ModelRenderer(this, 50, 41);
        leftmid1.setRotationPoint(1.0F, 20.0F, -6.0F);
        leftmid1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(leftmid1, -0.08726646259971647F, -0.2230530784048753F, 0.08726646259971647F);
        wingright1 = new ModelRenderer(this, 50, 0);
        wingright1.setRotationPoint(-1.0F, 16.200000762939453F, -8.0F);
        wingright1.addBox(0.0F, -16.0F, 0.0F, 0, 16, 4, 0.0F);
        setRotation(wingright1, 0.1745329052209854F, -0.1745329052209854F, -1.570796012878418F);
    }

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
		super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
		setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
		torso.render(unitPixel);
		back.render(unitPixel);
		tail1.render(unitPixel);
		tail2.render(unitPixel);
		tail3.render(unitPixel);
		tail4.render(unitPixel);
		stinger.render(unitPixel);
		head1.render(unitPixel);
		head2.render(unitPixel);
		eyeleft.render(unitPixel);
		eyeright.render(unitPixel);
		jawleft.render(unitPixel);
		jawright.render(unitPixel);
		antennaleft.render(unitPixel);
		antennaright.render(unitPixel);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, 1);
		wingright1.render(unitPixel);
		wingleft1.render(unitPixel);
		wingright2.render(unitPixel);
		wingleft2.render(unitPixel);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, 0);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		leftfront1.render(unitPixel);
		rightfront1.render(unitPixel);
		leftmid1.render(unitPixel);
		rightmid1.render(unitPixel);
		leftback1.render(unitPixel);
		rightback1.render(unitPixel);
		leftfront2.render(unitPixel);
		rightfront2.render(unitPixel);
		leftmid2.render(unitPixel);
		rightmid2.render(unitPixel);
		leftback2.render(unitPixel);
		rightback2.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
		EntityDragonFly dragonFly = (EntityDragonFly) entity;
		wingright1.rotateAngleX = 0.1745329F;
		wingleft1.rotateAngleX = 0.1745329F;
		wingright2.rotateAngleX = -0.1745329F;
		wingleft2.rotateAngleX = -0.1745329F;

		if (dragonFly.onGround) {
			wingright1.rotateAngleZ = -1.570796F;
			wingleft1.rotateAngleZ = 1.570796F;
			wingright2.rotateAngleZ = -1.570796F;
			wingleft2.rotateAngleZ = 1.570796F;
		}
		if (dragonFly.isFlying()) {
			wingright1.rotateAngleZ = -1.570796F - dragonFly.wingFloat;
			wingleft1.rotateAngleZ = 1.570796F + dragonFly.wingFloat;
			wingright2.rotateAngleZ = -1.570796F - dragonFly.wingFloat;
			wingleft2.rotateAngleZ = 1.570796F + dragonFly.wingFloat;
		}
	}
}
