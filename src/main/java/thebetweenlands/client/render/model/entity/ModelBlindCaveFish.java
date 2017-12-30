package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;

@SideOnly(Side.CLIENT)
public class ModelBlindCaveFish extends ModelBase {
    ModelRenderer lure1;
    ModelRenderer lure2;
    ModelRenderer lure3;
    ModelRenderer head;
    ModelRenderer jaw;
    ModelRenderer bottomTeeth;
    ModelRenderer topTeeth;
    ModelRenderer body;
    ModelRenderer tail;
    ModelRenderer midSection;
    ModelRenderer dorsalFin;
    ModelRenderer pectoralFinL;
    ModelRenderer pectoralFinR;
    ModelRenderer tailFin;

    public ModelBlindCaveFish() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.pectoralFinL = new ModelRenderer(this, 35, 0);
        this.pectoralFinL.setRotationPoint(2.0F, 11.0F, 2.0F);
        this.pectoralFinL.addBox(0.0F, 0.0F, -1.5F, 4, 0, 3, 0.0F);
        this.setRotation(pectoralFinL, -0.5585053563117981F, -0.6217309832572937F, -0.2617993950843811F);
        this.midSection = new ModelRenderer(this, 42, 21);
        this.midSection.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.midSection.addBox(-1.0F, -7.0F, 8.0F, 2, 6, 5, 0.0F);
        this.setRotation(midSection, 0.0F, -0.024346200749278072F, 0.0F);
        this.lure1 = new ModelRenderer(this, 2, 0);
        this.lure1.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.lure1.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
        this.setRotation(lure1, 0.8203047513961792F, -0.0F, 0.0F);
        this.lure3 = new ModelRenderer(this, 0, 9);
        this.lure3.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.lure3.addBox(-1.5F, -5.0F, -4.5F, 3, 3, 3, 0.0F);
        this.setRotation(lure3, 0.8203047513961792F, -0.0F, 0.0F);
        this.head = new ModelRenderer(this, 0, 19);
        this.head.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.head.addBox(-2.0F, -5.0F, 0.0F, 4, 6, 7, 0.0F);
        this.setRotation(head, 0.8203047513961792F, -0.0F, 0.0F);
        this.topTeeth = new ModelRenderer(this, 16, 0);
        this.topTeeth.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.topTeeth.addBox(-2.0F, -4.5F, -2.0F, 4, 5, 2, 0.0F);
        this.setRotation(topTeeth, 0.8203047513961792F, -0.0F, 0.0F);
        this.lure2 = new ModelRenderer(this, 0, 4);
        this.lure2.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.lure2.addBox(-0.5F, -4.0F, -3.5F, 1, 1, 4, 0.0F);
        this.setRotation(lure2, 0.8203047513961792F, -0.0F, 0.0F);
        this.dorsalFin = new ModelRenderer(this, 46, -6);
        this.dorsalFin.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.dorsalFin.addBox(0.0F, -11.0F, 5.0F, 0, 3, 6, 0.0F);
        this.setRotation(dorsalFin, -0.13962633907794952F, -0.024346200749278072F, 0.0F);
        this.jaw = new ModelRenderer(this, 14, 13);
        this.jaw.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.jaw.addBox(-1.5F, -4.0F, -1.0F, 3, 5, 1, 0.0F);
        this.setRotation(jaw, 1.3782689571380613F, -0.0F, 0.0F);
        this.body = new ModelRenderer(this, 22, 17);
        this.body.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.body.addBox(-1.5F, -8.0F, 1.0F, 3, 8, 7, 0.0F);
        this.tail = new ModelRenderer(this, 56, 26);
        this.tail.setRotationPoint(0.0F, 12.0F, 13.0F);
        this.tail.addBox(-0.5F, -17.5F, 0.0F, 1, 3, 3, 0.0F);
        this.setRotation(tail, 0.0F, 0.07557275661135447F, 0.0F);
        this.pectoralFinR = new ModelRenderer(this, 35, 0);
        this.pectoralFinR.setRotationPoint(-2.0F, 11.0F, 2.0F);
        this.pectoralFinR.addBox(-4.0F, 0.0F, -1.5F, 4, 0, 3, 0.0F);
        this.setRotation(pectoralFinR, -0.5585053563117981F, 0.6217309832572937F, 0.2617993950843811F);
        this.bottomTeeth = new ModelRenderer(this, 8, 0);
        this.bottomTeeth.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.bottomTeeth.addBox(-1.5F, -3.5F, 0.0F, 3, 5, 1, 0.0F);
        this.setRotation(bottomTeeth, 1.3782689571380613F, -0.0F, 0.0F);
        this.tailFin = new ModelRenderer(this, 58, -3);
        this.tailFin.setRotationPoint(0.0F, 12.0F, 13.0F);
        this.tailFin.addBox(0.0F, -18.0F, 3.0F, 0, 4, 3, 0.0F);
        this.setRotation(tailFin, 0.0F, 0.07557275661135447F, 0.0F);
        this.midSection.addChild(this.tail);
        this.midSection.addChild(this.tailFin);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        body.render(unitPixel);
        pectoralFinL.render(unitPixel);
        pectoralFinR.render(unitPixel);
        bottomTeeth.render(unitPixel);
        topTeeth.render(unitPixel);
        lure3.render(unitPixel);
        jaw.render(unitPixel);
        dorsalFin.render(unitPixel);
        midSection.render(unitPixel);
        lure1.render(unitPixel);
        lure2.render(unitPixel);
        head.render(unitPixel);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        EntityBlindCaveFish fish = (EntityBlindCaveFish) entity;
        jaw.rotateAngleX = 1.5F + fish.moveProgress;
        bottomTeeth.rotateAngleX = 1.5F + fish.moveProgress;
        pectoralFinL.rotateAngleY = -0.5F - fish.moveProgress;
        pectoralFinR.rotateAngleY = 0.5F + fish.moveProgress;
        dorsalFin.rotateAngleY = midSection.rotateAngleY = -0.05F + fish.moveProgress * 0.2F;
        tail.rotateAngleY = midSection.rotateAngleY * 1.2F;
        tailFin.rotateAngleY = midSection.rotateAngleY * 1.4F;
    }
}
