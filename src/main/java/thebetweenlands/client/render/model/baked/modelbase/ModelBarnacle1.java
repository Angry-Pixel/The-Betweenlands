package thebetweenlands.client.render.model.baked.modelbase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBarnacle1 extends ModelBase {
	public ModelRenderer bone;
	public ModelRenderer Bornacle1;
	public final ModelRenderer Bornacle2;

	public ModelBarnacle1() {
		textureWidth = 16;
		textureHeight = 16;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		Bornacle1 = new ModelRenderer(this, 0, 0);
		Bornacle1.setRotationPoint(-1.0F, 0.0F, -1.0F);
		bone.addChild(Bornacle1);
		setRotationAngle(Bornacle1, 0.2182F, 0.2618F, 0.0F);
		Bornacle1.addBox(-2.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F);

		Bornacle2 = new ModelRenderer(this, 0, 4);
		Bornacle2.setRotationPoint(1.0F, 0.0F, 1.0F);
		bone.addChild(Bornacle2);
		setRotationAngle(Bornacle2, -0.0873F, 0.1309F, 0.1309F);
		Bornacle2.addBox(0.0F, -1.0F, 0.0F, 2, 1, 2, 0.0F);
	}

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		bone.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}