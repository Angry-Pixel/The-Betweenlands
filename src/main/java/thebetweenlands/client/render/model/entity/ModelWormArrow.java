package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWormArrow extends ModelBase {
    ModelRenderer head;
    ModelRenderer beak_right;
    ModelRenderer beak_left;
    ModelRenderer dat_detailed_hot_bod;
    ModelRenderer dat_detailed_hot_bod_2;
    ModelRenderer dat_detailed_hot_bod_3;
    ModelRenderer cute_lil_butt;
    ModelRenderer spoopy_stinger;

    public ModelWormArrow() {
        textureWidth = 32;
        textureHeight = 32;
        dat_detailed_hot_bod_2 = new ModelRenderer(this, 13, 0);
        dat_detailed_hot_bod_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        dat_detailed_hot_bod_2.addBox(-1.5F, -1.5F, -0.5F, 3, 3, 3, 0.0F);
        cute_lil_butt = new ModelRenderer(this, 13, 7);
        cute_lil_butt.setRotationPoint(0.0F, 0.0F, 0.0F);
        cute_lil_butt.addBox(-1.0F, -1.0F, 5.5F, 2, 2, 2, 0.0F);
        spoopy_stinger = new ModelRenderer(this, 13, 11);
        spoopy_stinger.setRotationPoint(0.0F, -1.5F, 1.0F);
        spoopy_stinger.addBox(-0.5F, -0.8F, 6.5F, 1, 2, 2, 0.0F);
        setRotateAngle(spoopy_stinger, -0.18203784098300857F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 0, 0);
        head.setRotationPoint(0.0F, 22.5F, 0.0F);
        head.addBox(-1.5F, -1.5F, -6.5F, 3, 3, 3, 0.0F);
        beak_left = new ModelRenderer(this, 0, 14);
        beak_left.setRotationPoint(1.5F, 0.5F, -1.5F);
        beak_left.addBox(-3.5F, -2.0F, -6.7F, 2, 3, 3, 0.0F);
        setRotateAngle(beak_left, 0.0F, -0.31869712141416456F, 0.0F);
        dat_detailed_hot_bod_3 = new ModelRenderer(this, 13, 0);
        dat_detailed_hot_bod_3.setRotationPoint(0.0F, 0.0F, 0.0F);
        dat_detailed_hot_bod_3.addBox(-1.5F, -1.5F, 2.5F, 3, 3, 3, 0.0F);
        dat_detailed_hot_bod = new ModelRenderer(this, 13, 0);
        dat_detailed_hot_bod.setRotationPoint(0.0F, 0.0F, 0.0F);
        dat_detailed_hot_bod.addBox(-1.5F, -1.5F, -3.5F, 3, 3, 3, 0.0F);
        beak_right = new ModelRenderer(this, 0, 7);
        beak_right.setRotationPoint(-1.5F, 0.0F, -1.5F);
        beak_right.addBox(1.5F, -1.5F, -6.7F, 2, 3, 3, 0.0F);
        setRotateAngle(beak_right, 0.0F, 0.31869712141416456F, 0.0F);
        head.addChild(dat_detailed_hot_bod_2);
        head.addChild(cute_lil_butt);
        cute_lil_butt.addChild(spoopy_stinger);
        head.addChild(beak_left);
        head.addChild(dat_detailed_hot_bod_3);
        head.addChild(dat_detailed_hot_bod);
        head.addChild(beak_right);
    }

	public void render() {
        head.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
