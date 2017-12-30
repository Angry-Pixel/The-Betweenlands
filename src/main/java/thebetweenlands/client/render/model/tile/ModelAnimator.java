package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelAnimator extends ModelBase {
    public ModelRenderer stonebase;
    public ModelRenderer stonecorner1;
    public ModelRenderer stonecorner2;
    public ModelRenderer stonecorner3;
    public ModelRenderer stonecorner4;
    public ModelRenderer woodleg1;
    public ModelRenderer woodleg2;
    public ModelRenderer woodleg3;
    public ModelRenderer woodleg4;
    public ModelRenderer woodplate1;
    public ModelRenderer woodplate2;
    public ModelRenderer woodplate3;
    public ModelRenderer woodplate4;
    public ModelRenderer woodplate5;
    public ModelRenderer woodplate6;
    public ModelRenderer beam1;
    public ModelRenderer beam2;
    public ModelRenderer beam3;
    public ModelRenderer board4;
    public ModelRenderer scroll1;
    public ModelRenderer board1;
    public ModelRenderer board2;
    public ModelRenderer board3;
    public ModelRenderer scroll2;
    public ModelRenderer scrollpiece1;
    public ModelRenderer scrollpiece2;
    public ModelRenderer scrollpiece3;

    public ModelAnimator() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.stonecorner2 = new ModelRenderer(this, 17, 21);
        this.stonecorner2.setRotationPoint(-6.0F, 20.0F, 6.0F);
        this.stonecorner2.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.scrollpiece1 = new ModelRenderer(this, 80, 23);
        this.scrollpiece1.setRotationPoint(0.0F, 1.0F, -1.0F);
        this.scrollpiece1.addBox(-3.0F, 0.0F, -3.0F, 6, 0, 4, 0.0F);
        this.setRotateAngle(scrollpiece1, 0.136659280431156F, -0.27314402793711257F, 0.0F);
        this.woodplate1 = new ModelRenderer(this, 0, 41);
        this.woodplate1.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.woodplate1.addBox(-8.0F, -2.0F, -7.0F, 16, 2, 5, 0.0F);
        this.woodplate5 = new ModelRenderer(this, 43, 41);
        this.woodplate5.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.woodplate5.addBox(-7.0F, -2.0F, -8.0F, 14, 2, 1, 0.0F);
        this.stonecorner1 = new ModelRenderer(this, 0, 21);
        this.stonecorner1.setRotationPoint(-6.0F, 20.0F, -6.0F);
        this.stonecorner1.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.woodleg4 = new ModelRenderer(this, 39, 28);
        this.woodleg4.setRotationPoint(6.0F, 18.1F, -6.0F);
        this.woodleg4.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3, 0.0F);
        this.setRotateAngle(woodleg4, -0.18203784098300857F, -0.01815142422074103F, -0.18203784098300857F);
        this.woodplate6 = new ModelRenderer(this, 43, 45);
        this.woodplate6.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.woodplate6.addBox(-7.0F, -2.0F, 7.0F, 14, 2, 1, 0.0F);
        this.beam2 = new ModelRenderer(this, 68, 49);
        this.beam2.setRotationPoint(-5.5F, 16.0F, 0.0F);
        this.beam2.addBox(-1.0F, -2.0F, -5.0F, 2, 2, 10, 0.0F);
        this.beam1 = new ModelRenderer(this, 43, 49);
        this.beam1.setRotationPoint(5.5F, 16.0F, 0.0F);
        this.beam1.addBox(-1.0F, -2.0F, -5.0F, 2, 2, 10, 0.0F);
        this.scrollpiece3 = new ModelRenderer(this, 79, 31);
        this.scrollpiece3.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.scrollpiece3.addBox(-3.0F, 0.0F, -5.0F, 6, 0, 5, 0.0F);
        this.setRotateAngle(scrollpiece3, 0.40980330836826856F, 0.0F, 0.0F);
        this.beam3 = new ModelRenderer(this, 93, 49);
        this.beam3.setRotationPoint(0.0F, 16.0F, 5.5F);
        this.beam3.addBox(-5.0F, -2.0F, -1.0F, 10, 2, 2, 0.0F);
        this.board1 = new ModelRenderer(this, 50, 0);
        this.board1.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.board1.addBox(-1.0F, -2.0F, -4.0F, 1, 2, 8, 0.0F);
        this.woodleg2 = new ModelRenderer(this, 13, 28);
        this.woodleg2.setRotationPoint(-6.0F, 18.1F, 6.0F);
        this.woodleg2.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3, 0.0F);
        this.setRotateAngle(woodleg2, 0.18203784098300857F, -0.01815142422074103F, 0.18203784098300857F);
        this.woodplate2 = new ModelRenderer(this, 0, 49);
        this.woodplate2.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.woodplate2.addBox(-8.0F, -2.0F, -2.0F, 6, 2, 4, 0.0F);
        this.woodleg3 = new ModelRenderer(this, 26, 28);
        this.woodleg3.setRotationPoint(6.0F, 18.1F, 6.0F);
        this.woodleg3.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3, 0.0F);
        this.setRotateAngle(woodleg3, 0.18203784098300857F, 0.01815142422074103F, -0.18203784098300857F);
        this.scrollpiece2 = new ModelRenderer(this, 82, 28);
        this.scrollpiece2.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.scrollpiece2.addBox(-3.0F, 0.0F, -2.0F, 6, 0, 2, 0.0F);
        this.setRotateAngle(scrollpiece2, 1.1838568316277536F, 0.0F, 0.0F);
        this.stonecorner3 = new ModelRenderer(this, 34, 21);
        this.stonecorner3.setRotationPoint(6.0F, 20.0F, 6.0F);
        this.stonecorner3.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.board2 = new ModelRenderer(this, 69, 0);
        this.board2.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.board2.addBox(0.0F, -2.0F, -4.0F, 1, 2, 8, 0.0F);
        this.scroll2 = new ModelRenderer(this, 80, 18);
        this.scroll2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.scroll2.addBox(-3.0F, -1.0F, -1.0F, 6, 2, 2, 0.0F);
        this.board3 = new ModelRenderer(this, 88, 0);
        this.board3.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.board3.addBox(-4.0F, -2.0F, -1.0F, 8, 2, 1, 0.0F);
        this.woodleg1 = new ModelRenderer(this, 0, 28);
        this.woodleg1.setRotationPoint(-6.0F, 18.1F, -6.0F);
        this.woodleg1.addBox(-1.5F, -8.0F, -1.5F, 3, 9, 3, 0.0F);
        this.setRotateAngle(woodleg1, -0.18203784098300857F, 0.01815142422074103F, 0.18203784098300857F);
        this.board4 = new ModelRenderer(this, 88, 4);
        this.board4.setRotationPoint(0.0F, 11.0F, -4.0F);
        this.board4.addBox(-4.0F, 0.0F, -1.0F, 8, 2, 1, 0.0F);
        this.woodplate3 = new ModelRenderer(this, 0, 56);
        this.woodplate3.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.woodplate3.addBox(-8.0F, -2.0F, 2.0F, 16, 2, 5, 0.0F);
        this.stonecorner4 = new ModelRenderer(this, 51, 21);
        this.stonecorner4.setRotationPoint(6.0F, 20.0F, -6.0F);
        this.stonecorner4.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        this.stonebase = new ModelRenderer(this, 0, 0);
        this.stonebase.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.stonebase.addBox(-8.0F, -4.0F, -8.0F, 16, 4, 16, 0.0F);
        this.woodplate4 = new ModelRenderer(this, 21, 49);
        this.woodplate4.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.woodplate4.addBox(2.0F, -2.0F, -2.0F, 6, 2, 4, 0.0F);
        this.scroll1 = new ModelRenderer(this, 80, 15);
        this.scroll1.setRotationPoint(5.0F, 8.0F, 3.0F);
        this.scroll1.addBox(-3.5F, -0.5F, -0.5F, 7, 1, 1, 0.0F);
        this.setRotateAngle(scroll1, -0.136659280431156F, -1.0016444577195458F, 0.0F);
        this.scroll2.addChild(this.scrollpiece1);
        this.scrollpiece2.addChild(this.scrollpiece3);
        this.beam1.addChild(this.board1);
        this.scrollpiece1.addChild(this.scrollpiece2);
        this.beam2.addChild(this.board2);
        this.scroll1.addChild(this.scroll2);
        this.beam3.addChild(this.board3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.stonecorner2.render(f5);
        this.woodplate1.render(f5);
        this.woodplate5.render(f5);
        this.stonecorner1.render(f5);
        this.woodleg4.render(f5);
        this.woodplate6.render(f5);
        this.beam2.render(f5);
        this.beam1.render(f5);
        this.beam3.render(f5);
        this.woodleg2.render(f5);
        this.woodplate2.render(f5);
        this.woodleg3.render(f5);
        this.stonecorner3.render(f5);
        this.woodleg1.render(f5);
        this.board4.render(f5);
        this.woodplate3.render(f5);
        this.stonecorner4.render(f5);
        this.stonebase.render(f5);
        this.woodplate4.render(f5);
        this.scroll1.render(f5);
    }

    public void renderAll(float f5) {
        this.stonecorner2.render(f5);
        this.woodplate1.render(f5);
        this.woodplate5.render(f5);
        this.stonecorner1.render(f5);
        this.woodleg4.render(f5);
        this.woodplate6.render(f5);
        this.beam2.render(f5);
        this.beam1.render(f5);
        this.beam3.render(f5);
        this.woodleg2.render(f5);
        this.woodplate2.render(f5);
        this.woodleg3.render(f5);
        this.stonecorner3.render(f5);
        this.woodleg1.render(f5);
        this.board4.render(f5);
        this.woodplate3.render(f5);
        this.stonecorner4.render(f5);
        this.stonebase.render(f5);
        this.woodplate4.render(f5);
        this.scroll1.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
