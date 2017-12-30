package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMireSnail extends ModelBase {
    ModelRenderer torso1;
    ModelRenderer head;
    ModelRenderer torso2;
    ModelRenderer shell1;
    ModelRenderer shell2;
    ModelRenderer sensor1;
    ModelRenderer sensor2;
    ModelRenderer sensor3;
    ModelRenderer sensor4;
    ModelRenderer sensor5;
    ModelRenderer sensor6;
    ModelRenderer tree1;
    ModelRenderer tree2;
    ModelRenderer tree3;
    ModelRenderer tree4;
    ModelRenderer tree5;
    ModelRenderer leaf1;
    ModelRenderer leaf2;
    ModelRenderer leaf3;

    public ModelMireSnail() {
        textureWidth = 64;
        textureHeight = 32;

        torso1 = new ModelRenderer(this, 0, 0);
        torso1.addBox(-1.5F, 0F, -7F, 3, 2, 14);
        torso1.setRotationPoint(0F, 22F, 0F);
        setRotation(torso1, 0F, 0F, 0F);
        head = new ModelRenderer(this, 0, 17);
        head.addBox(-1F, -4.8F, -7F, 2, 2, 2);
        head.setRotationPoint(0F, 22F, 0F);
        setRotation(head, 0.7807508F, 0F, 0F);
        torso2 = new ModelRenderer(this, 0, 22);
        torso2.addBox(-2F, -1F, -5.5F, 4, 3, 6);
        torso2.setRotationPoint(0F, 22F, 0F);
        setRotation(torso2, 0F, 0F, 0F);
        shell1 = new ModelRenderer(this, 22, 0);
        shell1.addBox(-2.5F, -5F, -3F, 5, 6, 6);
        shell1.setRotationPoint(0F, 22F, 0F);
        setRotation(shell1, -0.3717861F, 0F, 0F);
        shell2 = new ModelRenderer(this, 35, 13);
        shell2.addBox(-3F, -4F, -2F, 6, 4, 4);
        shell2.setRotationPoint(0F, 22F, 0F);
        setRotation(shell2, -0.37179F, 0F, 0F);
        sensor1 = new ModelRenderer(this, 9, 16);
        sensor1.addBox(-0.5F, -3F, -0.5F, 1, 4, 1);
        sensor1.setRotationPoint(1F, 22F, -7F);
        setRotation(sensor1, 0.669215F, -0.5576792F, 0F);
        sensor2 = new ModelRenderer(this, 14, 16);
        sensor2.addBox(-0.5F, -3F, -0.5F, 1, 4, 1);
        sensor2.setRotationPoint(-1F, 22F, -7F);
        setRotation(sensor2, 0.669215F, 0.5576851F, 0F);
        sensor3 = new ModelRenderer(this, 17, 16);
        sensor3.addBox(-0.5F, 1.5F, -2.5F, 1, 0, 2);
        sensor3.setRotationPoint(0F, 22F, -7F);
        setRotation(sensor3, -0.1115358F, -0.8179294F, 0F);
        sensor4 = new ModelRenderer(this, 17, 19);
        sensor4.addBox(-0.5F, 1.5F, -2.5F, 1, 0, 2);
        sensor4.setRotationPoint(0F, 22F, -7F);
        setRotation(sensor4, -0.1115358F, 0.8179311F, 0F);
        sensor5 = new ModelRenderer(this, 22, 16);
        sensor5.addBox(-0.5F, -1F, -0.5F, 1, 3, 1);
        sensor5.setRotationPoint(1.5F, 21F, -5.5F);
        setRotation(sensor5, 0.7063936F, -0.9666439F, 0F);
        sensor6 = new ModelRenderer(this, 27, 16);
        sensor6.addBox(-0.5F, -1F, -0.5F, 1, 3, 1);
        sensor6.setRotationPoint(-1.5F, 21F, -5.5F);
        setRotation(sensor6, 0.7063936F, 0.9666506F, 0F);
        tree1 = new ModelRenderer(this, 21, 22);
        tree1.addBox(-2.5F, -2F, 0F, 1, 3, 1);
        tree1.setRotationPoint(0F, 16F, 0F);
        setRotation(tree1, 0F, -0.3346075F, -0.1858931F);
        tree2 = new ModelRenderer(this, 21, 27);
        tree2.addBox(-2.3F, 1F, 0F, 1, 2, 1);
        tree2.setRotationPoint(0F, 16F, 0F);
        setRotation(tree2, 0F, -0.3346145F, 0.1858911F);
        tree3 = new ModelRenderer(this, 21, 30);
        tree3.addBox(-2F, 0.5F, 0F, 3, 1, 1);
        tree3.setRotationPoint(0F, 16F, 0F);
        setRotation(tree3, 0F, -0.3346145F, 0.3346055F);
        tree4 = new ModelRenderer(this, 29, 29);
        tree4.addBox(0F, 0F, 1F, 1, 1, 2);
        tree4.setRotationPoint(0F, 16F, 0F);
        setRotation(tree4, -0.5948578F, 0.2230717F, 0.1115358F);
        tree5 = new ModelRenderer(this, 35, 29);
        tree5.addBox(-2.5F, 0F, 0.5F, 1, 1, 2);
        tree5.setRotationPoint(0F, 16F, 0F);
        setRotation(tree5, -0.4833219F, -0.1115358F, 0F);
        leaf1 = new ModelRenderer(this, 26, 22);
        leaf1.addBox(-4F, -3F, 0F, 3, 2, 3);
        leaf1.setRotationPoint(0F, 16F, 0F);
        setRotation(leaf1, 0F, -0.4461433F, 0F);
        leaf2 = new ModelRenderer(this, 39, 21);
        leaf2.addBox(-2F, -4F, -3F, 4, 2, 4);
        leaf2.setRotationPoint(0F, 16F, 0F);
        setRotation(leaf2, 0F, 0.4089647F, 0.0743572F);
        leaf3 = new ModelRenderer(this, 45, 0);
        leaf3.addBox(-1.5F, 2F, -4F, 4, 2, 4);
        leaf3.setRotationPoint(0F, 16F, 0F);
        setRotation(leaf3, 0.0743572F, -0.6320364F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel) {
        super.render(entity, limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel);
        setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        torso1.render(unitPixel);
        head.render(unitPixel);
        torso2.render(unitPixel);
        shell1.render(unitPixel);
        shell2.render(unitPixel);
        sensor1.render(unitPixel);
        sensor2.render(unitPixel);
        sensor3.render(unitPixel);
        sensor4.render(unitPixel);
        sensor5.render(unitPixel);
        sensor6.render(unitPixel);
        tree1.render(unitPixel);
        tree2.render(unitPixel);
        tree3.render(unitPixel);
        tree4.render(unitPixel);
        tree5.render(unitPixel);
        leaf1.render(unitPixel);
        leaf2.render(unitPixel);
        leaf3.render(unitPixel);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAngle, entityTickTime, rotationYaw, rotationPitch, unitPixel, entity);
        sensor1.rotateAngleX = MathHelper.cos(limbSwing * 1F + (float) Math.PI) * 1.5F * limbSwingAngle + 0.5F;
        sensor2.rotateAngleX = MathHelper.cos(limbSwing * 1F) * 1.5F * limbSwingAngle + 0.5F;
        sensor1.rotateAngleY = MathHelper.cos(limbSwing * 1F + (float) Math.PI) * 1.5F * limbSwingAngle - 0.2F;
        sensor2.rotateAngleY = MathHelper.cos(limbSwing * 1F) * 1.5F * limbSwingAngle + 0.2F;
    }

}
