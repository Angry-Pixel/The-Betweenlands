package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBLFishHook extends ModelBase {
    public ModelRenderer main_floater;
    public ModelRenderer baiter_left1a;
    public ModelRenderer baiter_left1b;
    public ModelRenderer baiter_right1a;
    public ModelRenderer baiter_right1b;
    public ModelRenderer floater_tip;
    public ModelRenderer floater_connection;
    public ModelRenderer baiter_left1c;
    public ModelRenderer baiter_right1c;
    public ModelRenderer hook;

    public ModelBLFishHook() {
        textureWidth = 32;
        textureHeight = 32;
        main_floater = new ModelRenderer(this, 0, 0);
        main_floater.setRotationPoint(0.0F, 21.0F, 0.0F);
        main_floater.addBox(-1.5F, -2.0F, -1.5F, 3, 4, 3, 0.0F);
        baiter_right1b = new ModelRenderer(this, 7, 8);
        baiter_right1b.setRotationPoint(-1.5F, -1.0F, 0.0F);
        baiter_right1b.addBox(0.0F, -2.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(baiter_right1b, 0.0F, 0.0F, -0.36425021489121656F);
        baiter_right1a = new ModelRenderer(this, 7, 5);
        baiter_right1a.setRotationPoint(-1.5F, 1.0F, 0.0F);
        baiter_right1a.addBox(0.0F, 0.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(baiter_right1a, 0.0F, 0.0F, 0.18203784098300857F);
        baiter_left1c = new ModelRenderer(this, 0, 11);
        baiter_left1c.setRotationPoint(0.0F, -2.0F, 0.0F);
        baiter_left1c.addBox(0.0F, -2.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(baiter_left1c, 0.0F, 0.0F, 0.36425021489121656F);
        floater_tip = new ModelRenderer(this, 0, 17);
        floater_tip.setRotationPoint(0.0F, -2.0F, 0.0F);
        floater_tip.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1, 0.0F);
        baiter_right1c = new ModelRenderer(this, 7, 11);
        baiter_right1c.setRotationPoint(0.0F, -2.0F, 0.0F);
        baiter_right1c.addBox(0.0F, -2.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(baiter_right1c, 0.0F, 0.0F, -0.36425021489121656F);
        hook = new ModelRenderer(this, 5, 16);
        hook.setRotationPoint(0.0F, 1.0F, 0.0F);
        hook.addBox(0.0F, 0.0F, -2.0F, 0, 4, 4, 0.0F);
        baiter_left1b = new ModelRenderer(this, 0, 8);
        baiter_left1b.setRotationPoint(1.5F, -1.0F, 0.0F);
        baiter_left1b.addBox(0.0F, -2.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(baiter_left1b, 0.0F, 0.0F, 0.36425021489121656F);
        floater_connection = new ModelRenderer(this, 5, 17);
        floater_connection.setRotationPoint(0.0F, 2.0F, 0.0F);
        floater_connection.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
        baiter_left1a = new ModelRenderer(this, 0, 5);
        baiter_left1a.setRotationPoint(1.5F, 1.0F, 0.0F);
        baiter_left1a.addBox(0.0F, 0.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(baiter_left1a, 0.0F, 0.0F, -0.18203784098300857F);
        main_floater.addChild(baiter_right1b);
        main_floater.addChild(baiter_right1a);
        baiter_left1b.addChild(baiter_left1c);
        main_floater.addChild(floater_tip);
        baiter_right1b.addChild(baiter_right1c);
        floater_connection.addChild(hook);
        main_floater.addChild(baiter_left1b);
        main_floater.addChild(floater_connection);
        main_floater.addChild(baiter_left1a);
    }

    public void render() { 
        main_floater.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
