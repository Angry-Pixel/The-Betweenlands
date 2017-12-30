package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityLeech;

@SideOnly(Side.CLIENT)
public class ModelLeech extends ModelBase {
    ModelRenderer s1;
    ModelRenderer s2;
    ModelRenderer s3;
    ModelRenderer s4;
    ModelRenderer s5;
    ModelRenderer s6;

    public ModelLeech() {
        textureWidth = 64;
        textureHeight = 32;

        s1 = new ModelRenderer(this, 0, 11);
        s1.addBox(0F, 0F, 0F, 2, 2, 1);
        s1.setRotationPoint(-1F, 22F, -7F);
        setRotation(s1, 0F, 0F, 0F);

        s2 = new ModelRenderer(this, 0, 0);
        s2.addBox(0F, 0F, 0F, 3, 3, 2);
        s2.setRotationPoint(-1.5F, 21F, -6F);
        setRotation(s2, 0F, 0F, 0F);

        s3 = new ModelRenderer(this, 0, 0);
        s3.addBox(0F, 0F, 0F, 4, 4, 7);
        s3.setRotationPoint(-2F, 20F, -4F);
        setRotation(s3, 0F, 0F, 0F);

        s4 = new ModelRenderer(this, 0, 0);
        s4.addBox(0F, 0F, 0F, 3, 3, 2);
        s4.setRotationPoint(-1.5F, 21F, 3F);
        setRotation(s4, 0F, 0F, 0F);

        s5 = new ModelRenderer(this, 0, 0);
        s5.addBox(0F, 0F, 0F, 2, 2, 2);
        s5.setRotationPoint(-1F, 22F, 5F);
        setRotation(s5, 0F, 0F, 0F);

        s6 = new ModelRenderer(this, 6, 11);
        s6.addBox(0F, 0F, 0F, 1, 1, 2);
        s6.setRotationPoint(-0.5F, 23F, 7F);
        setRotation(s6, 0F, 0F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);

        s1.render(unitPixel);
        s2.render(unitPixel);
        s3.render(unitPixel);
        s4.render(unitPixel);
        s5.render(unitPixel);
        s6.render(unitPixel);

    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        EntityLeech leech = (EntityLeech) entity;
        if (!leech.isRiding())
            leech.moveProgress = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
    }
}
