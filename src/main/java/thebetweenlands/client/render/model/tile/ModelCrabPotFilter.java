package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCrabPotFilter extends ModelBase {
    ModelRenderer base;
    ModelRenderer front_left_leg;
    ModelRenderer front_right_leg;
    ModelRenderer back_left_leg;
    ModelRenderer back_right_leg;
    ModelRenderer left_side_bottom;
    ModelRenderer right_side_bottom;
    ModelRenderer left_side_top;
    ModelRenderer right_side_top;
    ModelRenderer right_side_mesh_frame;
    ModelRenderer left_side_mesh_frame;
    ModelRenderer back_side_mesh_frame;
    ModelRenderer fromt_side_mesh_frame;
    ModelRenderer front_right_bottom;
    ModelRenderer front_left_bottom;
    ModelRenderer mesh;
    ModelRenderer front_top;
    ModelRenderer back_top;
    ModelRenderer back_bottom;
    ModelRenderer left_side_mesh;
    ModelRenderer right_side_mesh;
    ModelRenderer back_side_mesh;

    public ModelCrabPotFilter() {
        textureWidth = 128;
        textureHeight = 128;
        back_top = new ModelRenderer(this, 30, 13);
        back_top.setRotationPoint(0.0F, 10.0F, 7.5F);
        back_top.addBox(-8.0F, -2.0F, -0.5F, 16, 4, 1, 0.0F);
        left_side_bottom = new ModelRenderer(this, 0, 38);
        left_side_bottom.setRotationPoint(-7.5F, 23.0F, 0.0F);
        left_side_bottom.addBox(-0.5F, -5.0F, -7.0F, 1, 5, 14, 0.0F);
        front_right_leg = new ModelRenderer(this, 9, 20);
        front_right_leg.setRotationPoint(6.0F, 24.0F, -6.0F);
        front_right_leg.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2, 0.0F);
        back_right_leg = new ModelRenderer(this, 27, 20);
        back_right_leg.setRotationPoint(6.0F, 24.0F, 6.0F);
        back_right_leg.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2, 0.0F);
        back_side_mesh = new ModelRenderer(this, 38, 38);
        back_side_mesh.setRotationPoint(0.0F, 15.0F, 7.0F);
        back_side_mesh.addBox(-5.0F, -3.0F, 0.0F, 10, 6, 0, 0.0F);
        base = new ModelRenderer(this, 36, 20);
        base.setRotationPoint(0.0F, 24.0F, 0.0F);
        base.addBox(-8.0F, -1.0F, -8.0F, 16, 1, 16, 0.0F);
        left_side_top = new ModelRenderer(this, 0, 0);
        left_side_top.setRotationPoint(-7.5F, 10.0F, 0.0F);
        left_side_top.addBox(-0.5F, -2.0F, -7.0F, 1, 4, 14, 0.0F);
        back_side_mesh_frame = new ModelRenderer(this, 35, 0);
        back_side_mesh_frame.setRotationPoint(-0.0F, 19.05F, 6.65F);
        back_side_mesh_frame.addBox(-5.0F, -0.5F, -2.0F, 10, 1, 3, 0.0F);
        setRotateAngle(back_side_mesh_frame, 0.7853981633974483F, 0.0F, 0.0F);
        right_side_mesh_frame = new ModelRenderer(this, 56, 0);
        right_side_mesh_frame.setRotationPoint(6.65F, 19.05F, 0.0F);
        right_side_mesh_frame.addBox(-0.5F, -1.0F, -5.0F, 1, 3, 10, 0.0F);
        setRotateAngle(right_side_mesh_frame, 0.0F, 0.0F, 0.7853981633974483F);
        right_side_top = new ModelRenderer(this, 64, 0);
        right_side_top.setRotationPoint(7.5F, 10.0F, 0.0F);
        right_side_top.addBox(-0.5F, -2.0F, -7.0F, 1, 4, 14, 0.0F);
        front_right_bottom = new ModelRenderer(this, 106, 51);
        front_right_bottom.setRotationPoint(6.5F, 24.0F, -7.5F);
        front_right_bottom.addBox(-1.5F, -6.0F, -0.5F, 3, 5, 1, 0.0F);
        back_bottom = new ModelRenderer(this, 31, 51);
        back_bottom.setRotationPoint(0.0F, 20.5F, 7.5F);
        back_bottom.addBox(-8.0F, -2.5F, -0.5F, 16, 5, 1, 0.0F);
        back_left_leg = new ModelRenderer(this, 18, 20);
        back_left_leg.setRotationPoint(-6.0F, 24.0F, 6.0F);
        back_left_leg.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2, 0.0F);
        right_side_mesh = new ModelRenderer(this, 59, 28);
        right_side_mesh.setRotationPoint(7.0F, 15.0F, 0.0F);
        right_side_mesh.addBox(0.0F, -3.0F, -5.0F, 0, 6, 10, 0.0F);
        left_side_mesh_frame = new ModelRenderer(this, 17, 0);
        left_side_mesh_frame.setRotationPoint(-6.65F, 19.05F, 0.0F);
        left_side_mesh_frame.addBox(-0.5F, -1.0F, -5.0F, 1, 3, 10, 0.0F);
        setRotateAngle(left_side_mesh_frame, 0.0F, 0.0F, -0.7853981633974483F);
        fromt_side_mesh_frame = new ModelRenderer(this, 36, 5);
        fromt_side_mesh_frame.setRotationPoint(-0.0F, 19.05F, -6.65F);
        fromt_side_mesh_frame.addBox(-5.0F, -0.5F, 0.0F, 10, 1, 2, 0.0F);
        setRotateAngle(fromt_side_mesh_frame, -0.7853981633974483F, 0.0F, 0.0F);
        right_side_bottom = new ModelRenderer(this, 66, 38);
        right_side_bottom.setRotationPoint(7.5F, 23.0F, 0.0F);
        right_side_bottom.addBox(-0.5F, -5.0F, -7.0F, 1, 5, 14, 0.0F);
        front_top = new ModelRenderer(this, 94, 13);
        front_top.setRotationPoint(0.0F, 10.0F, -7.5F);
        front_top.addBox(-8.0F, -2.0F, -0.5F, 16, 4, 1, 0.0F);
        front_left_leg = new ModelRenderer(this, 0, 20);
        front_left_leg.setRotationPoint(-6.0F, 24.0F, -6.0F);
        front_left_leg.addBox(-1.0F, -16.0F, -1.0F, 2, 15, 2, 0.0F);
        front_left_bottom = new ModelRenderer(this, 97, 51);
        front_left_bottom.setRotationPoint(-6.5F, 24.0F, -7.5F);
        front_left_bottom.addBox(-1.5F, -6.0F, -0.5F, 3, 5, 1, 0.0F);
        mesh = new ModelRenderer(this, 71, 0);
        mesh.setRotationPoint(0.0F, 20.1F, 0.0F);
        mesh.addBox(-6.0F, 0.0F, -6.0F, 12, 0, 12, 0.0F);
        left_side_mesh = new ModelRenderer(this, 17, 28);
        left_side_mesh.setRotationPoint(-7.0F, 15.0F, 0.0F);
        left_side_mesh.addBox(0.0F, -3.0F, -5.0F, 0, 6, 10, 0.0F);
    }

    public void render() { 
        back_top.render(0.0625F);
        left_side_bottom.render(0.0625F);
        front_right_leg.render(0.0625F);
        back_right_leg.render(0.0625F);
        back_side_mesh.render(0.0625F);
        base.render(0.0625F);
        left_side_top.render(0.0625F);
        back_side_mesh_frame.render(0.0625F);
        right_side_mesh_frame.render(0.0625F);
        right_side_top.render(0.0625F);
        front_right_bottom.render(0.0625F);
        back_bottom.render(0.0625F);
        back_left_leg.render(0.0625F);
        right_side_mesh.render(0.0625F);
        left_side_mesh_frame.render(0.0625F);
        fromt_side_mesh_frame.render(0.0625F);
        right_side_bottom.render(0.0625F);
        front_top.render(0.0625F);
        front_left_leg.render(0.0625F);
        front_left_bottom.render(0.0625F);
        mesh.render(0.0625F);
        left_side_mesh.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
