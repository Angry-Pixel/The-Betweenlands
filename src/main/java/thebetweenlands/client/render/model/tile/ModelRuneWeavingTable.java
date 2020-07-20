package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLRuneweavingTable - TripleHeadedSheep
 * Created using Tabula 7.0.1
 */
public class ModelRuneWeavingTable extends ModelBase {
	public ModelRenderer table_woodbase;
	public ModelRenderer table_stoneslate;
	public ModelRenderer leg_front_right1a;
	public ModelRenderer leg_front_left1a;
	public ModelRenderer leg_back_right1a;
	public ModelRenderer leg_back_left1a;
	public ModelRenderer lowershelf;
	public ModelRenderer stoneslate1;
	public ModelRenderer leg_front_right1b;
	public ModelRenderer leg_front_right_rope;
	public ModelRenderer leg_front_left1b;
	public ModelRenderer leg_front_left_rope;
	public ModelRenderer leg_back_right1b;
	public ModelRenderer leg_back_right_rope;
	public ModelRenderer leg_back_left1b;
	public ModelRenderer leg_back_left_rope;
	public ModelRenderer scroll1a;
	public ModelRenderer scroll2a;
	public ModelRenderer scroll3a;
	public ModelRenderer rope_front;
	public ModelRenderer rope_back;
	public ModelRenderer scroll1b;
	public ModelRenderer scroll2b;
	public ModelRenderer scroll3b;
	public ModelRenderer scroll3c;
	public ModelRenderer scroll3d;
	public ModelRenderer stoneslate2;
	public ModelRenderer stoneslate3;
	public ModelRenderer bobbin_base;
	public ModelRenderer bobbin_rope;
	public ModelRenderer bobbin_top;
	public ModelRenderer bobbin_string;

	public ModelRuneWeavingTable() {
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.leg_back_right_rope = new ModelRenderer(this, 34, 52);
		this.leg_back_right_rope.setRotationPoint(-1.5F, 0.0F, 1.5F);
		this.leg_back_right_rope.addBox(-2.0F, -1.0F, -2.0F, 4, 2, 4, 0.0F);
		this.setRotateAngle(leg_back_right_rope, -0.045553093477052F, 0.0F, -0.045553093477052F);
		this.leg_back_left_rope = new ModelRenderer(this, 51, 52);
		this.leg_back_left_rope.setRotationPoint(1.5F, 0.0F, 1.5F);
		this.leg_back_left_rope.addBox(-2.0F, -1.0F, -2.0F, 4, 2, 4, 0.0F);
		this.setRotateAngle(leg_back_left_rope, -0.045553093477052F, 0.0F, 0.045553093477052F);
		this.leg_front_left1b = new ModelRenderer(this, 13, 41);
		this.leg_front_left1b.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.leg_front_left1b.addBox(0.0F, 0.0F, -3.0F, 3, 7, 3, 0.0F);
		this.setRotateAngle(leg_front_left1b, -0.091106186954104F, -0.009075712110370514F, -0.091106186954104F);
		this.scroll1b = new ModelRenderer(this, 73, 25);
		this.scroll1b.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.scroll1b.addBox(-0.5F, -0.5F, -5.0F, 1, 1, 10, 0.0F);
		this.setRotateAngle(scroll1b, 0.0F, 0.0F, 0.22759093446006054F);
		this.bobbin_rope = new ModelRenderer(this, 58, 23);
		this.bobbin_rope.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.bobbin_rope.addBox(-1.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(bobbin_rope, 0.0F, 0.136659280431156F, 0.0F);
		this.scroll1a = new ModelRenderer(this, 73, 14);
		this.scroll1a.setRotationPoint(-5.5F, 0.0F, 0.0F);
		this.scroll1a.addBox(-1.0F, -2.0F, -4.0F, 2, 2, 8, 0.0F);
		this.setRotateAngle(scroll1a, 0.0F, 0.136659280431156F, -0.045553093477052F);
		this.leg_front_right1a = new ModelRenderer(this, 0, 31);
		this.leg_front_right1a.setRotationPoint(-7.0F, 0.0F, -3.0F);
		this.leg_front_right1a.addBox(-3.0F, 0.0F, -3.0F, 3, 6, 3, 0.0F);
		this.setRotateAngle(leg_front_right1a, -0.091106186954104F, 0.009075712110370514F, 0.091106186954104F);
		this.leg_front_left1a = new ModelRenderer(this, 13, 31);
		this.leg_front_left1a.setRotationPoint(7.0F, 0.0F, -3.0F);
		this.leg_front_left1a.addBox(0.0F, 0.0F, -3.0F, 3, 6, 3, 0.0F);
		this.setRotateAngle(leg_front_left1a, -0.091106186954104F, -0.009075712110370514F, -0.091106186954104F);
		this.scroll2a = new ModelRenderer(this, 73, 14);
		this.scroll2a.mirror = true;
		this.scroll2a.setRotationPoint(-3.0F, 0.0F, -1.0F);
		this.scroll2a.addBox(-1.0F, -2.0F, -4.0F, 2, 2, 8, 0.0F);
		this.setRotateAngle(scroll2a, 0.0F, 0.045553093477052F, 0.045553093477052F);
		this.leg_back_right1a = new ModelRenderer(this, 26, 31);
		this.leg_back_right1a.setRotationPoint(-7.0F, 0.0F, 3.0F);
		this.leg_back_right1a.addBox(-3.0F, 0.0F, 0.0F, 3, 6, 3, 0.0F);
		this.setRotateAngle(leg_back_right1a, 0.091106186954104F, -0.009075712110370514F, 0.091106186954104F);
		this.table_woodbase = new ModelRenderer(this, 0, 0);
		this.table_woodbase.setRotationPoint(4.0F, 12.0F, 0.0F);
		this.table_woodbase.addBox(-11.0F, -3.0F, -7.0F, 22, 3, 14, 0.0F);
		this.stoneslate3 = new ModelRenderer(this, 88, 46);
		this.stoneslate3.setRotationPoint(-1.0F, -1.0F, 0.0F);
		this.stoneslate3.addBox(0.0F, -10.0F, -4.0F, 1, 10, 8, 0.0F);
		this.setRotateAngle(stoneslate3, 0.091106186954104F, -0.136659280431156F, -0.5462880558742251F);
		this.scroll2b = new ModelRenderer(this, 73, 25);
		this.scroll2b.mirror = true;
		this.scroll2b.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.scroll2b.addBox(-0.5F, -0.5F, -5.0F, 1, 1, 10, 0.0F);
		this.setRotateAngle(scroll2b, 0.0F, 0.0F, -0.22759093446006054F);
		this.bobbin_top = new ModelRenderer(this, 58, 28);
		this.bobbin_top.setRotationPoint(0.0F, -2.0F, 0.0F);
		this.bobbin_top.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
		this.setRotateAngle(bobbin_top, 0.0F, -0.136659280431156F, 0.0F);
		this.scroll3b = new ModelRenderer(this, 73, 25);
		this.scroll3b.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.scroll3b.addBox(-0.5F, -0.5F, -5.0F, 1, 1, 10, 0.0F);
		this.setRotateAngle(scroll3b, 0.0F, 0.0F, 0.18203784098300857F);
		this.bobbin_string = new ModelRenderer(this, 58, 31);
		this.bobbin_string.setRotationPoint(1.0F, 0.0F, -1.0F);
		this.bobbin_string.addBox(0.0F, -1.0F, -2.0F, 0, 1, 2, 0.0F);
		this.setRotateAngle(bobbin_string, 0.0F, 0.7740535232594852F, 0.0F);
		this.leg_front_right_rope = new ModelRenderer(this, 0, 52);
		this.leg_front_right_rope.setRotationPoint(-1.5F, 0.0F, -1.5F);
		this.leg_front_right_rope.addBox(-2.0F, -1.0F, -2.0F, 4, 2, 4, 0.0F);
		this.setRotateAngle(leg_front_right_rope, 0.045553093477052F, 0.0F, -0.045553093477052F);
		this.stoneslate2 = new ModelRenderer(this, 88, 34);
		this.stoneslate2.setRotationPoint(0.0F, -1.0F, 0.8F);
		this.stoneslate2.addBox(-4.0F, -1.0F, -5.0F, 8, 1, 10, 0.0F);
		this.setRotateAngle(stoneslate2, 0.0F, 0.136659280431156F, 0.0F);
		this.rope_front = new ModelRenderer(this, 52, 37);
		this.rope_front.setRotationPoint(0.0F, 0.5F, -6.0F);
		this.rope_front.addBox(-7.5F, 0.0F, 0.0F, 15, 6, 0, 0.0F);
		this.stoneslate1 = new ModelRenderer(this, 88, 34);
		this.stoneslate1.mirror = true;
		this.stoneslate1.setRotationPoint(16.0F, 12.0F, 1.0F);
		this.stoneslate1.addBox(-4.0F, -1.0F, -5.0F, 8, 1, 10, 0.0F);
		this.setRotateAngle(stoneslate1, 0.0F, 0.091106186954104F, 0.0F);
		this.leg_front_left_rope = new ModelRenderer(this, 17, 52);
		this.leg_front_left_rope.setRotationPoint(1.5F, 0.0F, -1.5F);
		this.leg_front_left_rope.addBox(-2.0F, -1.0F, -2.0F, 4, 2, 4, 0.0F);
		this.setRotateAngle(leg_front_left_rope, 0.045553093477052F, 0.0F, 0.045553093477052F);
		this.leg_back_right1b = new ModelRenderer(this, 26, 41);
		this.leg_back_right1b.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.leg_back_right1b.addBox(-3.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		this.setRotateAngle(leg_back_right1b, 0.091106186954104F, -0.009075712110370514F, 0.091106186954104F);
		this.leg_back_left1a = new ModelRenderer(this, 39, 31);
		this.leg_back_left1a.setRotationPoint(7.0F, 0.0F, 3.0F);
		this.leg_back_left1a.addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, 0.0F);
		this.setRotateAngle(leg_back_left1a, 0.091106186954104F, 0.009075712110370514F, -0.091106186954104F);
		this.scroll3a = new ModelRenderer(this, 94, 14);
		this.scroll3a.setRotationPoint(2.0F, 0.0F, 0.0F);
		this.scroll3a.addBox(-1.0F, -2.0F, -4.0F, 2, 2, 8, 0.0F);
		this.setRotateAngle(scroll3a, -0.091106186954104F, 0.6373942428283291F, -0.136659280431156F);
		this.lowershelf = new ModelRenderer(this, 60, 0);
		this.lowershelf.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.lowershelf.addBox(-9.0F, 0.0F, -5.0F, 18, 2, 10, 0.0F);
		this.scroll3d = new ModelRenderer(this, 99, 25);
		this.scroll3d.setRotationPoint(2.0F, 0.0F, 0.0F);
		this.scroll3d.addBox(0.0F, 0.0F, -4.0F, 3, 0, 8, 0.0F);
		this.setRotateAngle(scroll3d, 0.0F, 0.0F, 0.36425021489121656F);
		this.table_stoneslate = new ModelRenderer(this, 0, 18);
		this.table_stoneslate.setRotationPoint(-9.0F, -3.0F, 5.0F);
		this.table_stoneslate.addBox(0.0F, -1.0F, -10.0F, 18, 1, 10, 0.0F);
		this.leg_front_right1b = new ModelRenderer(this, 0, 41);
		this.leg_front_right1b.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.leg_front_right1b.addBox(-3.0F, 0.0F, -3.0F, 3, 7, 3, 0.0F);
		this.setRotateAngle(leg_front_right1b, -0.091106186954104F, 0.009075712110370514F, 0.091106186954104F);
		this.leg_back_left1b = new ModelRenderer(this, 39, 41);
		this.leg_back_left1b.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.leg_back_left1b.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3, 0.0F);
		this.setRotateAngle(leg_back_left1b, 0.091106186954104F, 0.009075712110370514F, -0.091106186954104F);
		this.rope_back = new ModelRenderer(this, 52, 44);
		this.rope_back.setRotationPoint(0.0F, 0.5F, 6.0F);
		this.rope_back.addBox(-7.5F, 0.0F, 0.0F, 15, 6, 0, 0.0F);
		this.scroll3c = new ModelRenderer(this, 90, 25);
		this.scroll3c.setRotationPoint(1.0F, 0.0F, 0.0F);
		this.scroll3c.addBox(0.0F, 0.0F, -4.0F, 2, 0, 8, 0.0F);
		this.setRotateAngle(scroll3c, 0.0F, 0.0F, -0.091106186954104F);
		this.bobbin_base = new ModelRenderer(this, 58, 18);
		this.bobbin_base.setRotationPoint(2.0F, -1.0F, 2.0F);
		this.bobbin_base.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F);
		this.setRotateAngle(bobbin_base, 0.0F, 0.31869712141416456F, 0.0F);
		this.leg_back_right1b.addChild(this.leg_back_right_rope);
		this.leg_back_left1b.addChild(this.leg_back_left_rope);
		this.leg_front_left1a.addChild(this.leg_front_left1b);
		this.scroll1a.addChild(this.scroll1b);
		this.bobbin_base.addChild(this.bobbin_rope);
		this.lowershelf.addChild(this.scroll1a);
		this.table_woodbase.addChild(this.leg_front_right1a);
		this.table_woodbase.addChild(this.leg_front_left1a);
		this.lowershelf.addChild(this.scroll2a);
		this.table_woodbase.addChild(this.leg_back_right1a);
		this.stoneslate2.addChild(this.stoneslate3);
		this.scroll2a.addChild(this.scroll2b);
		this.bobbin_rope.addChild(this.bobbin_top);
		this.scroll3a.addChild(this.scroll3b);
		this.bobbin_rope.addChild(this.bobbin_string);
		this.leg_front_right1b.addChild(this.leg_front_right_rope);
		this.stoneslate1.addChild(this.stoneslate2);
		this.lowershelf.addChild(this.rope_front);
		this.table_woodbase.addChild(this.stoneslate1);
		this.leg_front_left1b.addChild(this.leg_front_left_rope);
		this.leg_back_right1a.addChild(this.leg_back_right1b);
		this.table_woodbase.addChild(this.leg_back_left1a);
		this.lowershelf.addChild(this.scroll3a);
		this.table_woodbase.addChild(this.lowershelf);
		this.scroll3c.addChild(this.scroll3d);
		this.table_woodbase.addChild(this.table_stoneslate);
		this.leg_front_right1a.addChild(this.leg_front_right1b);
		this.leg_back_left1a.addChild(this.leg_back_left1b);
		this.lowershelf.addChild(this.rope_back);
		this.scroll3a.addChild(this.scroll3c);
		this.stoneslate2.addChild(this.bobbin_base);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.table_woodbase.render(f5);
	}
	
	public void render() {
		this.table_woodbase.render(0.0625f);
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
