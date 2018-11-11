package thebetweenlands.client.render.model.tile;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * BLWaystone - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelWaystone extends ModelBase {
	public ModelRenderer slate1;
	public ModelRenderer rope1;
	public ModelRenderer rope5;
	public ModelRenderer slate2;
	public ModelRenderer slate2b;
	public ModelRenderer slate3a;
	public ModelRenderer slate3b;
	public ModelRenderer slate4;
	public ModelRenderer slate4b;
	public ModelRenderer slate3a1;
	public ModelRenderer slate3b1;
	public ModelRenderer slate5a;
	public ModelRenderer slate5b;
	public ModelRenderer slate6;
	public ModelRenderer slate5a1;
	public ModelRenderer slate5b1;
	public ModelRenderer slate7;
	public ModelRenderer slate7b;
	public ModelRenderer slate8;
	public ModelRenderer rope2;
	public ModelRenderer objects4;
	public ModelRenderer rope3;
	public ModelRenderer objects3;
	public ModelRenderer rope4;
	public ModelRenderer objects1;
	public ModelRenderer objects2;
	public ModelRenderer rope6;
	public ModelRenderer rope7;
	public ModelRenderer objects5;
	public ModelRenderer rope8;
	public ModelRenderer object6a;
	public ModelRenderer object6b;
	public ModelRenderer object6b_1;

	public ModelWaystone() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.objects1 = new ModelRenderer(this, 65, 88);
		this.objects1.setRotationPoint(0.0F, 0.0F, -4.4F);
		this.objects1.addBox(0.0F, 0.0F, 0.0F, 10, 8, 0, 0.0F);
		this.setRotateAngle(objects1, 0.0F, 0.0F, 1.0471975511965976F);
		this.slate3b1 = new ModelRenderer(this, 27, 55);
		this.slate3b1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.slate3b1.addBox(1.0F, -1.0F, -4.0F, 1, 1, 8, 0.0F);
		this.slate7b = new ModelRenderer(this, 45, 11);
		this.slate7b.setRotationPoint(0.0F, 0.0F, 3.0F);
		this.slate7b.addBox(-6.0F, -2.0F, 0.0F, 8, 2, 1, 0.0F);
		this.objects5 = new ModelRenderer(this, 82, 97);
		this.objects5.setRotationPoint(0.0F, 0.0F, -4.4F);
		this.objects5.addBox(-0.2F, 0.0F, 0.0F, 9, 8, 0, 0.0F);
		this.setRotateAngle(objects5, 0.0F, 0.0F, 1.0016444577195458F);
		this.slate4b = new ModelRenderer(this, 41, 65);
		this.slate4b.setRotationPoint(-6.0F, -9.0F, 0.0F);
		this.slate4b.addBox(-1.0F, -2.0F, -4.0F, 1, 2, 6, 0.0F);
		this.slate3b = new ModelRenderer(this, 27, 41);
		this.slate3b.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.slate3b.addBox(2.0F, -5.0F, -4.0F, 4, 5, 8, 0.0F);
		this.rope2 = new ModelRenderer(this, 86, 40);
		this.rope2.setRotationPoint(-1.0F, 1.0F, 0.0F);
		this.rope2.addBox(0.0F, 0.0F, -4.5F, 1, 6, 9, 0.0F);
		this.setRotateAngle(rope2, 0.0F, 0.0F, -0.18203784098300857F);
		this.rope3 = new ModelRenderer(this, 107, 40);
		this.rope3.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.rope3.addBox(0.0F, 0.0F, -4.5F, 1, 6, 9, 0.0F);
		this.setRotateAngle(rope3, 0.0F, 0.0F, -0.18203784098300857F);
		this.rope4 = new ModelRenderer(this, 65, 56);
		this.rope4.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.rope4.addBox(0.0F, 0.0F, -4.5F, 1, 5, 9, 0.0F);
		this.setRotateAngle(rope4, 0.0F, 0.0F, -0.18203784098300857F);
		this.slate5a = new ModelRenderer(this, 0, 77);
		this.slate5a.setRotationPoint(0.0F, -3.0F, 0.0F);
		this.slate5a.addBox(-6.0F, -10.0F, -4.0F, 4, 10, 8, 0.0F);
		this.object6a = new ModelRenderer(this, 65, 101);
		this.object6a.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.object6a.addBox(0.0F, 0.0F, -2.0F, 0, 3, 4, 0.0F);
		this.setRotateAngle(object6a, 0.0F, 0.0F, 1.4570008595648662F);
		this.slate5a1 = new ModelRenderer(this, 0, 96);
		this.slate5a1.setRotationPoint(0.0F, -10.0F, 0.0F);
		this.slate5a1.addBox(-2.0F, 0.0F, -4.0F, 1, 1, 8, 0.0F);
		this.object6b = new ModelRenderer(this, 65, 105);
		this.object6b.setRotationPoint(0.0F, 3.0F, 0.0F);
		this.object6b.addBox(0.0F, 0.0F, -2.0F, 0, 2, 4, 0.0F);
		this.setRotateAngle(object6b, 0.0F, 0.0F, -0.31869712141416456F);
		this.slate5b = new ModelRenderer(this, 25, 77);
		this.slate5b.setRotationPoint(0.0F, -3.0F, 0.0F);
		this.slate5b.addBox(2.0F, -10.0F, -4.0F, 4, 10, 8, 0.0F);
		this.slate4 = new ModelRenderer(this, 0, 65);
		this.slate4.setRotationPoint(0.0F, -9.0F, 0.0F);
		this.slate4.addBox(-6.0F, -3.0F, -4.0F, 12, 3, 8, 0.0F);
		this.rope6 = new ModelRenderer(this, 107, 56);
		this.rope6.setRotationPoint(-1.0F, 1.0F, 0.0F);
		this.rope6.addBox(0.0F, 0.0F, -4.5F, 1, 6, 9, 0.0F);
		this.setRotateAngle(rope6, 0.0F, 0.0F, -0.18203784098300857F);
		this.object6b_1 = new ModelRenderer(this, 65, 108);
		this.object6b_1.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.object6b_1.addBox(0.0F, 0.0F, -2.0F, 0, 2, 4, 0.0F);
		this.setRotateAngle(object6b_1, 0.0F, 0.0F, -0.5462880558742251F);
		this.slate2b = new ModelRenderer(this, 43, 28);
		this.slate2b.setRotationPoint(6.0F, -19.0F, 0.0F);
		this.slate2b.addBox(0.0F, -2.0F, -2.0F, 1, 2, 6, 0.0F);
		this.objects2 = new ModelRenderer(this, 86, 80);
		this.objects2.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.objects2.addBox(0.0F, 0.0F, -4.0F, 0, 5, 8, 0.0F);
		this.setRotateAngle(objects2, 0.0F, 0.0F, 1.2292353921796064F);
		this.slate1 = new ModelRenderer(this, 0, 0);
		this.slate1.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.slate1.addBox(-7.0F, -19.0F, -4.0F, 14, 19, 8, 0.0F);
		this.slate2 = new ModelRenderer(this, 0, 28);
		this.slate2.setRotationPoint(0.0F, -19.0F, 0.0F);
		this.slate2.addBox(-7.0F, -4.0F, -4.0F, 13, 4, 8, 0.0F);
		this.rope7 = new ModelRenderer(this, 65, 72);
		this.rope7.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.rope7.addBox(0.0F, 0.0F, -4.5F, 1, 5, 9, 0.0F);
		this.setRotateAngle(rope7, 0.0F, 0.0F, -0.18203784098300857F);
		this.rope1 = new ModelRenderer(this, 65, 40);
		this.rope1.setRotationPoint(-7.0F, 7.0F, 0.0F);
		this.rope1.addBox(-1.0F, 0.0F, -4.5F, 1, 1, 9, 0.0F);
		this.setRotateAngle(rope1, 0.0F, 0.0F, -0.6829473363053812F);
		this.slate8 = new ModelRenderer(this, 45, 15);
		this.slate8.setRotationPoint(0.0F, -3.0F, 0.0F);
		this.slate8.addBox(-5.0F, -1.0F, -4.0F, 8, 1, 7, 0.0F);
		this.objects4 = new ModelRenderer(this, 65, 89);
		this.objects4.setRotationPoint(-1.0F, 0.0F, 0.0F);
		this.objects4.addBox(0.0F, 0.0F, -4.0F, 0, 7, 8, 0.0F);
		this.setRotateAngle(objects4, 0.0F, 0.0F, 0.6829473363053812F);
		this.rope5 = new ModelRenderer(this, 86, 56);
		this.rope5.setRotationPoint(-7.0F, 0.9F, 0.0F);
		this.rope5.addBox(-1.0F, 0.0F, -4.5F, 1, 1, 9, 0.0F);
		this.setRotateAngle(rope5, 0.0F, 0.0F, -0.8196066167365371F);
		this.slate3a1 = new ModelRenderer(this, 0, 55);
		this.slate3a1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.slate3a1.addBox(-2.0F, -1.0F, -4.0F, 1, 1, 8, 0.0F);
		this.slate5b1 = new ModelRenderer(this, 25, 96);
		this.slate5b1.setRotationPoint(0.0F, -10.0F, 0.0F);
		this.slate5b1.addBox(1.0F, 0.0F, -4.0F, 1, 1, 8, 0.0F);
		this.rope8 = new ModelRenderer(this, 86, 72);
		this.rope8.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.rope8.addBox(0.0F, 0.0F, -4.5F, 1, 5, 9, 0.0F);
		this.setRotateAngle(rope8, 0.0F, 0.0F, -0.18203784098300857F);
		this.slate7 = new ModelRenderer(this, 45, 0);
		this.slate7.setRotationPoint(0.0F, -3.0F, 0.0F);
		this.slate7.addBox(-6.0F, -3.0F, -4.0F, 11, 3, 7, 0.0F);
		this.objects3 = new ModelRenderer(this, 104, 88);
		this.objects3.setRotationPoint(0.0F, 0.0F, 4.4F);
		this.objects3.addBox(-1.0F, 0.0F, 0.0F, 8, 10, 0, 0.0F);
		this.setRotateAngle(objects3, 0.0F, 0.0F, 0.8651597102135892F);
		this.slate3a = new ModelRenderer(this, 0, 41);
		this.slate3a.setRotationPoint(0.0F, -4.0F, 0.0F);
		this.slate3a.addBox(-7.0F, -5.0F, -4.0F, 5, 5, 8, 0.0F);
		this.slate6 = new ModelRenderer(this, 0, 106);
		this.slate6.setRotationPoint(0.0F, -13.0F, 0.0F);
		this.slate6.addBox(-6.0F, -3.0F, -4.0F, 12, 3, 8, 0.0F);
		this.rope3.addChild(this.objects1);
		this.slate3b.addChild(this.slate3b1);
		this.slate7.addChild(this.slate7b);
		this.rope6.addChild(this.objects5);
		this.slate2.addChild(this.slate4b);
		this.slate2.addChild(this.slate3b);
		this.rope1.addChild(this.rope2);
		this.rope2.addChild(this.rope3);
		this.rope3.addChild(this.rope4);
		this.slate4.addChild(this.slate5a);
		this.rope8.addChild(this.object6a);
		this.slate5a.addChild(this.slate5a1);
		this.object6a.addChild(this.object6b);
		this.slate4.addChild(this.slate5b);
		this.slate2.addChild(this.slate4);
		this.rope5.addChild(this.rope6);
		this.object6b.addChild(this.object6b_1);
		this.slate1.addChild(this.slate2b);
		this.rope4.addChild(this.objects2);
		this.slate1.addChild(this.slate2);
		this.rope6.addChild(this.rope7);
		this.slate7.addChild(this.slate8);
		this.rope1.addChild(this.objects4);
		this.slate3a.addChild(this.slate3a1);
		this.slate5b.addChild(this.slate5b1);
		this.rope7.addChild(this.rope8);
		this.slate6.addChild(this.slate7);
		this.rope2.addChild(this.objects3);
		this.slate2.addChild(this.slate3a);
		this.slate4.addChild(this.slate6);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.slate1.render(f5);
		this.rope1.render(f5);
		this.rope5.render(f5);
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
