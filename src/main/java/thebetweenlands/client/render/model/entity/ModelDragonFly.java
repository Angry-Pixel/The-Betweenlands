package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.AdvancedModelRenderer;
import thebetweenlands.client.render.model.MowzieModelBase;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.util.MathUtils;

@SideOnly(Side.CLIENT)
public class ModelDragonFly extends MowzieModelBase {
    private ModelRenderer antennaleft;
    private ModelRenderer antennaright;
    private ModelRenderer head1;
    private ModelRenderer head2;
    private ModelRenderer back;
    private AdvancedModelRenderer leftback1;
    private AdvancedModelRenderer leftback2;
    private AdvancedModelRenderer rightback1;
    private AdvancedModelRenderer rightback2;
    private AdvancedModelRenderer leftfront1;
    private AdvancedModelRenderer leftfront2;
    private AdvancedModelRenderer rightfront2;
    private AdvancedModelRenderer rightfront1;
    private AdvancedModelRenderer leftmid1;
    private AdvancedModelRenderer leftmid2;
    private AdvancedModelRenderer rightmid1;
    private AdvancedModelRenderer rightmid2;
    private ModelRenderer eyeleft;
    private ModelRenderer eyeright;
    private ModelRenderer jawleft;
    private ModelRenderer jawright;
    private ModelRenderer torso;
    private AdvancedModelRenderer tail1;
    private AdvancedModelRenderer tail2;
    private AdvancedModelRenderer tail3;
    private AdvancedModelRenderer tail4;
    private AdvancedModelRenderer stinger;
    private AdvancedModelRenderer wingleft1;
    private AdvancedModelRenderer wingleft2;
    private AdvancedModelRenderer wingright1;
    private AdvancedModelRenderer wingright2;

    public ModelDragonFly() {
        textureWidth = 128;
        textureHeight = 64;
        init();
    }

    private void init() {
        boxList.clear();
        
        jawleft = new ModelRenderer(this, 30, 19);
        jawleft.setRotationPoint(0.0F, 18.0F, -9.0F);
        jawleft.addBox(0.0F, 0.0F, -4.5F, 1, 2, 1, 0.0F);
        setRotation(jawleft, 0.22307169437408447F, 0.0F, -0.22307169437408447F);
        eyeleft = new ModelRenderer(this, 30, 14);
        eyeleft.setRotationPoint(0.0F, 18.0F, -9.0F);
        eyeleft.addBox(2.5F, -0.5F, -2.5F, 1, 2, 2, 0.0F);
        setRotation(eyeleft, 0.07436110079288481F, 0.3892394006252289F, -0.18589310348033902F);
        back = new ModelRenderer(this, 0, 11);
        back.setRotationPoint(0.0F, 18.200000762939453F, -3.0F);
        back.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2, 0.0F);
        setRotation(back, -0.03717859834432601F, 0.0F, 0.0F);
        head2 = new ModelRenderer(this, 30, 8);
        head2.setRotationPoint(0.0F, 18.0F, -9.0F);
        head2.addBox(-1.5F, -2.9000000953674316F, -4.0F, 3, 3, 2, 0.0F);
        setRotation(head2, 0.7435721755027772F, 0.0F, 0.0F);
        head1 = new ModelRenderer(this, 30, 0);
        head1.setRotationPoint(0.0F, 18.0F, -9.0F);
        head1.addBox(-2.0F, -2.0F, -3.0F, 4, 4, 3, 0.0F);
        setRotation(head1, 0.3717860877513886F, 0.0F, 0.0F);
        antennaleft = new ModelRenderer(this, 30, 23);
        antennaleft.setRotationPoint(0.0F, 18.0F, -9.0F);
        antennaleft.addBox(0.0F, -4.0F, -2.0F, 1, 2, 0, 0.0F);
        setRotation(antennaleft, 0.40896469354629517F, 0.0F, 0.5205006003379822F);
        antennaright = new ModelRenderer(this, 33, 23);
        antennaright.setRotationPoint(0.0F, 18.0F, -9.0F);
        antennaright.addBox(-1.0F, -4.0F, -2.0F, 1, 2, 0, 0.0F);
        setRotation(antennaright, 0.40896469354629517F, 0.0F, -0.5204920768737793F);
        torso = new ModelRenderer(this, 0, 0);
        torso.setRotationPoint(0.0F, 18.0F, -9.0F);
        torso.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6, 0.0F);
        setRotation(torso, -0.07435719668865202F, 0.0F, 0.0F);
        eyeright = new ModelRenderer(this, 37, 14);
        eyeright.setRotationPoint(0.0F, 18.0F, -9.0F);
        eyeright.addBox(2.5F, -0.5F, 0.5F, 1, 2, 2, 0.0F);
        setRotation(eyeright, -0.07435102613495843F, 2.7698375229150005F, 0.18587756533739608F);
        jawright = new ModelRenderer(this, 35, 19);
        jawright.setRotationPoint(0.0F, 18.0F, -9.0F);
        jawright.addBox(-1.0F, 0.0F, -4.5F, 1, 2, 1, 0.0F);
        setRotation(jawright, 0.22307050228118896F, 0.0F, 0.22307050228118896F);
        wingleft1 = new AdvancedModelRenderer(this, 63, 4);
        wingleft1.setRotationPoint(-0.5F, 16, -8);
        wingleft1.add3DTexture(0, 0, 1F / 32, 4, 16);
        setRotation(wingleft1, 0, -90 * MathUtils.DEG_TO_RAD, 0);
        wingleft2 = new AdvancedModelRenderer(this, 63, 25);
        wingleft2.setRotationPoint(-0.5F, 16.5F, -4);
        wingleft2.add3DTexture(0, 0, 1F / 32, 4, 12);
        setRotation(wingleft2, 0, -90 * MathUtils.DEG_TO_RAD, 0);
        wingright1 = new AdvancedModelRenderer(this, 50, 4);
        wingright1.setRotationPoint(0.5F, 16, -4);
        wingright1.add3DTexture(0, 0, 1F / 32, 4, 16);
        setRotation(wingright1, 0, 90 * MathUtils.DEG_TO_RAD, 0);
        wingright2 = new AdvancedModelRenderer(this, 50, 25);
        wingright2.setRotationPoint(0.5F, 16.5F, 0);
        wingright2.add3DTexture(0, 0, 1F / 32, 4, 12);
        setRotation(wingright2, 0, 90 * MathUtils.DEG_TO_RAD, 0);
        
        rightback1 = new AdvancedModelRenderer(this, 65, 44);
        rightback1.setRotationPoint(-1.0F, 20.0F, -5.0F);
        rightback1.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
        setRotation(rightback1, 0.17453292519943295F, -2.249380339970292F, -0.17453292519943295F);
        
        rightback2 = new AdvancedModelRenderer(this, 55, 59);
        rightback2.setRotationPoint(-1.0F, 20.0F, -5.0F);
        rightback2.addBox(4.8F, 1.8F, 0.2F, 1, 4, 1, 0.0F);
        setRotation(rightback2, -0.38083084278516266F, -2.1769491760125272F, 0.48939032225921F);
        
        leftback1 = new AdvancedModelRenderer(this, 50, 44);
        leftback1.setRotationPoint(1.0F, 20.0F, -5.0F);
        leftback1.addBox(0.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
        setRotation(leftback1, -0.17453292519943295F, -0.8922123136195012F, 0.17453292519943295F);
        
        leftback2 = new AdvancedModelRenderer(this, 50, 59);
        leftback2.setRotationPoint(1.0F, 20.0F, -5.0F);
        leftback2.addBox(4.8F, 1.8F, -1.1F, 1, 4, 1, 0.0F);
        setRotation(leftback2, 0.38083084278516266F, -0.964643477577266F, -0.48939032225921F);
        
        rightmid1 = new AdvancedModelRenderer(this, 63, 41);
        rightmid1.setRotationPoint(-1.0F, 20.0F, -6.0F);
        rightmid1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(rightmid1, 0.08726646259971647F, -2.9169687788581227F, -0.08726646259971647F);
        
        rightmid2 = new AdvancedModelRenderer(this, 55, 53);
        rightmid2.setRotationPoint(-1.0F, 20.0F, -6.0F);
        rightmid2.addBox(3.9F, 1.6F, -0.6F, 1, 4, 1, 0.0F);
        setRotation(rightmid2, 0.0F, -2.918539575184918F, 0.33457961760731303F);
        
        leftmid1 = new AdvancedModelRenderer(this, 50, 41);
        leftmid1.setRotationPoint(1.0F, 20.0F, -6.0F);
        leftmid1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(leftmid1, -0.08726646259971647F, -0.2230530784048753F, 0.08726646259971647F);
        
        leftmid2 = new AdvancedModelRenderer(this, 50, 53);
        leftmid2.setRotationPoint(1.0F, 20.0F, -6.0F);
        leftmid2.addBox(3.9F, 1.6F, -0.4F, 1, 4, 1, 0.0F);
        setRotation(leftmid2, 0.0F, -0.2230530784048753F, -0.33457961760731303F);
        
        rightfront1 = new AdvancedModelRenderer(this, 63, 38);
        rightfront1.setRotationPoint(-1.0F, 20.0F, -7.0F);
        rightfront1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(rightfront1, 0.0F, 2.6954864967800423F, -0.14870205226991687F);
        
        rightfront2 = new AdvancedModelRenderer(this, 55, 47);
        rightfront2.setRotationPoint(-1.0F, 20.0F, -7.0F);
        rightfront2.addBox(4.0F, 1.2F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(rightfront2, 0.0F, 2.6954864967800423F, 0.18587756533739608F);
        
        leftfront1 = new AdvancedModelRenderer(this, 50, 38);
        leftfront1.setRotationPoint(1.0F, 20.0F, -7.0F);
        leftfront1.addBox(0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F);
        setRotation(leftfront1, 0.0F, 0.44614329934120184F, 0.14871439337730405F);
        
        leftfront2 = new AdvancedModelRenderer(this, 50, 47);
        leftfront2.setRotationPoint(1.0F, 20.0F, -7.0F);
        leftfront2.addBox(4.0F, 1.2F, -0.5F, 1, 3, 1, 0.0F);
        setRotation(leftfront2, 0.0F, 0.4461061568097506F, -0.18587756533739608F);
        
        tail1 = new AdvancedModelRenderer(this, 0, 17);
        tail1.setRotationPoint(0.0F, 18.0F, -1.0F);
        tail1.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 5, 0.0F);
        
        tail2 = new AdvancedModelRenderer(this, 0, 25);
        tail2.setRotationPoint(0.0F, 0.0F, 5.0F);
        tail2.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        
        tail3 = new AdvancedModelRenderer(this, 0, 32);
        tail3.setRotationPoint(0.0F, 0.0F, 4.0F);
        tail3.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        
        tail4 = new AdvancedModelRenderer(this, 0, 39);
        tail4.setRotationPoint(0.0F, 0.0F, 4.0F);
        tail4.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        
        stinger = new AdvancedModelRenderer(this, 0, 46);
        stinger.setRotationPoint(0.0F, 0.0F, 4.0F);
        stinger.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3, 0.0F);
        setRotation(stinger, -0.4363322854042054F, 0.0F, 0.0F);
        
        this.tail1.addChild(this.tail2);
        this.tail2.addChild(this.tail3);
        this.tail3.addChild(this.tail4);
        this.tail4.addChild(this.stinger);
        
        this.setInitPose();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(Entity entity, float swing, float speed, float ticksExisted, float yaw, float pitch, float scale) {
        torso.render(scale);
        back.render(scale);
        tail1.render(scale);
        head1.render(scale);
        head2.render(scale);
        eyeleft.render(scale);
        eyeright.render(scale);
        jawleft.render(scale);
        jawright.render(scale);
        antennaleft.render(scale);
        antennaright.render(scale);
        wingright1.render(scale);
        wingleft1.render(scale);
        wingright2.render(scale);
        wingleft2.render(scale);
        leftfront1.render(scale);
        rightfront1.render(scale);
        leftmid1.render(scale);
        rightmid1.render(scale);
        leftback1.render(scale);
        rightback1.render(scale);
        leftfront2.render(scale);
        rightfront2.render(scale);
        leftmid2.render(scale);
        rightmid2.render(scale);
        leftback2.render(scale);
        rightback2.render(scale);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
        EntityDragonFly dragonFly = (EntityDragonFly) entity;
        
        float frame = entity.ticksExisted + partialRenderTicks;
        
        this.setToInitPose();
        
        wingright1.rotateAngleX = 0.1745329F;
        wingleft1.rotateAngleX = 0.1745329F;
        wingright2.rotateAngleX = -0.1745329F;
        wingleft2.rotateAngleX = -0.1745329F;

        if (dragonFly.onGround) {
        	float flap = MathHelper.sin((dragonFly.ticksExisted + partialRenderTicks) * 0.08F) * 0.05F;
        	 wingright1.rotateAngleZ = -1.570796F - flap;
             wingleft1.rotateAngleZ = 1.570796F + flap;
             wingright2.rotateAngleZ = -1.570796F - flap;
             wingleft2.rotateAngleZ = 1.570796F + flap;
        }
        if (dragonFly.isFlying()) {
        	float flap = MathHelper.sin((dragonFly.ticksExisted + partialRenderTicks) * 1.6F) * 0.3F;
            wingright1.rotateAngleZ = -1.570796F - flap;
            wingleft1.rotateAngleZ = 1.570796F + flap;
            wingright2.rotateAngleZ = -1.570796F - flap;
            wingleft2.rotateAngleZ = 1.570796F + flap;
        }
        
        walk(tail1, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
        walk(tail2, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
        walk(tail3, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
        walk(tail4, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
        walk(stinger, 0.1f, -0.05f, false, 1.8f, 0, frame, 1);
        
        swing(tail1, 0.03f, -0.08f, false, 1.8f, 0, frame, 1);
        swing(tail2, 0.04f, -0.08f, false, 1.8f, 0, frame, 1);
        swing(tail3, 0.05f, -0.08f, false, 1.8f, 0, frame, 1);
        swing(tail4, 0.06f, -0.08f, false, 1.8f, 0, frame, 1);
        
    	float legFlapSpeed = dragonFly.onGround ? 0.08F : 0.4F;
    	float legFlatDegree = dragonFly.onGround ? -0.05F : -0.1F;
        flap(rightback2, legFlapSpeed, legFlatDegree, true, 0.0f, 0, frame, 1);
        flap(leftback2, legFlapSpeed, legFlatDegree, false, 0.4f, 0, frame, 1);
        flap(rightmid2, legFlapSpeed, legFlatDegree, true, 0.8f, 0, frame, 1);
        flap(leftmid2, legFlapSpeed, legFlatDegree, false, 1.2f, 0, frame, 1);
        flap(rightfront2, legFlapSpeed, legFlatDegree, true, 1.6f, 0, frame, 1);
        flap(leftfront2, legFlapSpeed, legFlatDegree, false, 2.0f, 0, frame, 1);
    }
}
