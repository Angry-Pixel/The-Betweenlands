package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPestleAndMortar extends ModelBase {
    ModelRenderer mortarpiece_r;
    ModelRenderer mortarpiece_f;
    ModelRenderer mortarpiece_l;
    ModelRenderer mortarpiece_b;
    ModelRenderer mortarbottom_huehue;
    ModelRenderer pestle1;
    ModelRenderer standardbrace1;
    ModelRenderer groundwaste;
    ModelRenderer pestle2;
    ModelRenderer pestle3;
    ModelRenderer leg1;
    ModelRenderer leg2;
    ModelRenderer leg3;
    ModelRenderer leg4;
    ModelRenderer fancypiece1;
    ModelRenderer fancypiece2;
    ModelRenderer fancypiece3;
    ModelRenderer fancypiece4;
    /*  ModelRenderer driedherb1;
        ModelRenderer driedherb1a;
        ModelRenderer driedherb1b;
        ModelRenderer driedherb1c;
    */
    public ModelPestleAndMortar() {
        textureWidth = 128;
        textureHeight = 64;
/*		driedherb1 = new ModelRenderer(this, 60, 47);
        driedherb1.setRotationPoint(6.0F, 12.0F, 0.0F);
        driedherb1.addBox(0.0F, -1.0F, -3.0F, 0, 6, 6, 0.0F);
        setRotateAngle(driedherb1, -0.136659280431156F, 3.142F, -0.136659280431156F);
        driedherb1a = new ModelRenderer(this, 73, 47);
        driedherb1a.setRotationPoint(0.0F, 5.0F, 0.0F);
        driedherb1a.addBox(0.0F, 0.0F, -3.0F, 0, 4, 6, 0.0F);
        setRotateAngle(driedherb1a, -0.045553093477052F, 0.0F, 0.091106186954104F);
        driedherb1b = new ModelRenderer(this, 86, 48);
        driedherb1b.setRotationPoint(0.0F, 0.0F, 0.0F);
        driedherb1b.addBox(0.0F, 0.0F, -2.5F, 0, 6, 5, 0.0F);
        setRotateAngle(driedherb1b, -0.091106186954104F, 0.0F, -0.136659280431156F);
        driedherb1c = new ModelRenderer(this, 97, 48);
        driedherb1c.setRotationPoint(0.0F, 0.0F, 0.0F);
        driedherb1c.addBox(0.0F, -1.0F, -2.5F, 0, 7, 5, 0.0F);
        setRotateAngle(driedherb1c, -0.22759093446006054F, 0.22759093446006054F, -0.136659280431156F);
*/
        fancypiece3 = new ModelRenderer(this, 81, 30);
        fancypiece3.setRotationPoint(0.0F, 2.0F, 5.5F);
        fancypiece3.addBox(-5.0F, 0.0F, 0.0F, 10, 5, 0, 0.0F);
        setRotateAngle(fancypiece3, -0.091106186954104F, 3.142F, 0.0F);
        standardbrace1 = new ModelRenderer(this, 60, 0);
        standardbrace1.setRotationPoint(0.0F, 12.0F, 0.0F);
        standardbrace1.addBox(-6.0F, 0.0F, -6.0F, 12, 2, 12, 0.0F);
        mortarpiece_b = new ModelRenderer(this, 17, 17);
        mortarpiece_b.setRotationPoint(0.0F, 8.0F, 3.0F);
        mortarpiece_b.addBox(-5.0F, 0.0F, 0.0F, 10, 10, 2, 0.0F);
        fancypiece4 = new ModelRenderer(this, 81, 26);
        fancypiece4.setRotationPoint(5.5F, 2.0F, 0.0F);
        fancypiece4.addBox(0.0F, 0.0F, -5.0F, 0, 5, 10, 0.0F);
        setRotateAngle(fancypiece4, 0.0F, 3.142F, -0.091106186954104F);
        fancypiece2 = new ModelRenderer(this, 60, 26);
        fancypiece2.setRotationPoint(-5.5F, 2.0F, 0.0F);
        fancypiece2.addBox(0.0F, 0.0F, -5.0F, 0, 5, 10, 0.0F);
        setRotateAngle(fancypiece2, 0.0F, 0.0F, 0.091106186954104F);
        mortarbottom_huehue = new ModelRenderer(this, 0, 35);
        mortarbottom_huehue.setRotationPoint(0.0F, 14.0F, 0.0F);
        mortarbottom_huehue.addBox(-4.0F, 0.0F, -4.0F, 8, 6, 8, 0.0F);
        leg4 = new ModelRenderer(this, 87, 15);
        leg4.setRotationPoint(5.0F, 1.0F, 5.0F);
        leg4.addBox(-1.1F, 0.0F, -1.1F, 2, 12, 2, 0.0F);
        setRotateAngle(leg4, 0.091106186954104F, 0.0F, -0.091106186954104F);
        leg2 = new ModelRenderer(this, 69, 15);
        leg2.setRotationPoint(-5.0F, 1.0F, -5.0F);
        leg2.addBox(-0.9F, 0.0F, -0.9F, 2, 12, 2, 0.0F);
        setRotateAngle(leg2, -0.091106186954104F, 0.0F, 0.091106186954104F);
        leg3 = new ModelRenderer(this, 78, 15);
        leg3.setRotationPoint(-5.0F, 1.0F, 5.0F);
        leg3.addBox(-0.9F, 0.0F, -1.1F, 2, 12, 2, 0.0F);
        setRotateAngle(leg3, 0.091106186954104F, 0.0F, 0.091106186954104F);
        mortarpiece_l = new ModelRenderer(this, 0, 17);
        mortarpiece_l.setRotationPoint(3.0F, 8.0F, 0.0F);
        mortarpiece_l.addBox(0.0F, 0.0F, -3.0F, 2, 10, 6, 0.0F);
        groundwaste = new ModelRenderer(this, 51, 42);
        groundwaste.setRotationPoint(0.0F, 23.9F, 0.0F);
        groundwaste.addBox(-3.0F, 0.0F, -4.0F, 10, 0, 10, 0.0F);
        fancypiece1 = new ModelRenderer(this, 60, 30);
        fancypiece1.setRotationPoint(0.0F, 2.0F, -5.5F);
        fancypiece1.addBox(-5.0F, 0.0F, 0.0F, 10, 5, 0, 0.0F);
        setRotateAngle(fancypiece1, -0.091106186954104F, 0.0F, 0.0F);
        pestle1 = new ModelRenderer(this, 45, 0);
        pestle1.setRotationPoint(0.0F, 12.0F, 0.0F);
        pestle1.addBox(-1.5F, -4.0F, -1.5F, 3, 6, 3, 0.0F);
        setRotateAngle(pestle1, -0.40980330836826856F, -0.31869712141416456F, -0.18203784098300857F);
        mortarpiece_r = new ModelRenderer(this, 0, 0);
        mortarpiece_r.setRotationPoint(-3.0F, 8.0F, 0.0F);
        mortarpiece_r.addBox(-2.0F, 0.0F, -3.0F, 2, 10, 6, 0.0F);
        pestle3 = new ModelRenderer(this, 45, 19);
        pestle3.setRotationPoint(0.0F, -6.0F, 0.0F);
        pestle3.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3, 0.0F);
        leg1 = new ModelRenderer(this, 60, 15);
        leg1.setRotationPoint(5.0F, 1.0F, -5.0F);
        leg1.addBox(-1.1F, 0.0F, -0.9F, 2, 12, 2, 0.0F);
        setRotateAngle(leg1, -0.091106186954104F, 0.0F, -0.091106186954104F);
        mortarpiece_f = new ModelRenderer(this, 17, 4);
        mortarpiece_f.setRotationPoint(0.0F, 8.0F, -3.0F);
        mortarpiece_f.addBox(-5.0F, 0.0F, -2.0F, 10, 10, 2, 0.0F);
        pestle2 = new ModelRenderer(this, 45, 10);
        pestle2.setRotationPoint(0.0F, -4.0F, 0.0F);
        pestle2.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2, 0.0F);
        standardbrace1.addChild(fancypiece3);

/*		driedherb1.addChild(driedherb1a);
		driedherb1.addChild(driedherb1b);
		driedherb1.addChild(driedherb1c);
*/
        standardbrace1.addChild(fancypiece4);
        standardbrace1.addChild(fancypiece2);
        standardbrace1.addChild(leg4);
        standardbrace1.addChild(leg2);
        standardbrace1.addChild(leg3);
        standardbrace1.addChild(fancypiece1);
        pestle2.addChild(pestle3);
        standardbrace1.addChild(leg1);
        pestle1.addChild(pestle2);
    }

    public void render() {
//		driedherb1.render(0.0625F);
        standardbrace1.render(0.0625F);
        mortarpiece_b.render(0.0625F);
        mortarbottom_huehue.render(0.0625F);
        mortarpiece_l.render(0.0625F);
        groundwaste.render(0.0625F);
        mortarpiece_r.render(0.0625F);
        mortarpiece_f.render(0.0625F);
    }

    public void renderPestle() {
        pestle1.render(0.0625F);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
