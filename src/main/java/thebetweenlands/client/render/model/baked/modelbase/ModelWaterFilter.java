package thebetweenlands.client.render.model.baked.modelbase;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWaterFilter extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer barrelcon1a;
	private final ModelRenderer barrelcon1b;
	private final ModelRenderer barrelcon2a;
	private final ModelRenderer barrelcon2b;
	private final ModelRenderer barrelcon3a;
	private final ModelRenderer barrelcon3b;
	private final ModelRenderer barrelcon4a;
	private final ModelRenderer barrelcon4b;
	private final ModelRenderer support1a;
	private final ModelRenderer support1b;
	private final ModelRenderer support1c;
	private final ModelRenderer support1d;
	private final ModelRenderer support1e;
	private final ModelRenderer support2a;
	private final ModelRenderer support2b;
	private final ModelRenderer support2c;
	private final ModelRenderer support2d;
	private final ModelRenderer support2e;
	private final ModelRenderer support3a;
	private final ModelRenderer support3b;
	private final ModelRenderer support3c;
	private final ModelRenderer support3d;
	private final ModelRenderer support3e;
	private final ModelRenderer support4a;
	private final ModelRenderer support4b;
	private final ModelRenderer support4c;
	private final ModelRenderer support4d;
	private final ModelRenderer support4e;
	private final ModelRenderer filterbase;
	private final ModelRenderer filterbase1;
	private final ModelRenderer filterbase2;
	private final ModelRenderer filterbase3;
	private final ModelRenderer filterbase4;
	private final ModelRenderer basin;
	private final ModelRenderer basinside1a;
	private final ModelRenderer basinside1b;
	private final ModelRenderer plank1;
	private final ModelRenderer basinside2a;
	private final ModelRenderer basinside2b;
	private final ModelRenderer plank2;
	private final ModelRenderer basinside3a;
	private final ModelRenderer basinside3b;
	private final ModelRenderer plank3;
	private final ModelRenderer basinside4a;
	private final ModelRenderer basinside4b;
	private final ModelRenderer plank4;
	private final ModelRenderer cornerpiece1;
	private final ModelRenderer cornerpiece2;
	private final ModelRenderer cornerpiece3;
	private final ModelRenderer cornerpiece4;
	private final ModelRenderer basin_bottom;
	private final ModelRenderer basin_opening;

	public ModelWaterFilter() {
		textureWidth = 128;
		textureHeight = 128;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 24.0F, 0.0F);
		

		barrelcon1a = new ModelRenderer(this);
		barrelcon1a.setRotationPoint(0.0F, 0.0F, -3.0F);
		base.addChild(barrelcon1a);
		barrelcon1a.cubeList.add(new ModelBox(barrelcon1a, 68, 65, -3.0F, -1.0F, -1.0F, 6, 2, 2, 0.0F, false));

		barrelcon1b = new ModelRenderer(this);
		barrelcon1b.setRotationPoint(0.0F, -1.0F, 1.0F);
		barrelcon1a.addChild(barrelcon1b);
		setRotationAngle(barrelcon1b, 0.1745F, 0.0F, 0.0F);
		barrelcon1b.cubeList.add(new ModelBox(barrelcon1b, 34, 65, -3.001F, -2.0F, -2.0F, 6, 2, 2, 0.0F, false));

		barrelcon2a = new ModelRenderer(this);
		barrelcon2a.setRotationPoint(0.0F, 0.0F, 3.0F);
		base.addChild(barrelcon2a);
		barrelcon2a.cubeList.add(new ModelBox(barrelcon2a, 51, 65, -3.0F, -1.0F, -1.0F, 6, 2, 2, 0.0F, false));

		barrelcon2b = new ModelRenderer(this);
		barrelcon2b.setRotationPoint(0.0F, -1.0F, -1.0F);
		barrelcon2a.addChild(barrelcon2b);
		setRotationAngle(barrelcon2b, -0.1745F, 0.0F, 0.0F);
		barrelcon2b.cubeList.add(new ModelBox(barrelcon2b, 17, 65, -3.001F, -2.0F, 0.0F, 6, 2, 2, 0.0F, false));

		barrelcon3a = new ModelRenderer(this);
		barrelcon3a.setRotationPoint(3.0F, 0.0F, 0.0F);
		base.addChild(barrelcon3a);
		barrelcon3a.cubeList.add(new ModelBox(barrelcon3a, 51, 56, -1.0F, -1.0F, -3.0F, 2, 2, 6, 0.0F, false));

		barrelcon3b = new ModelRenderer(this);
		barrelcon3b.setRotationPoint(-1.0F, -1.0F, 0.0F);
		barrelcon3a.addChild(barrelcon3b);
		setRotationAngle(barrelcon3b, 0.0F, 0.0F, 0.1745F);
		barrelcon3b.cubeList.add(new ModelBox(barrelcon3b, 17, 56, 0.0F, -2.0F, -3.0F, 2, 2, 6, 0.0F, false));

		barrelcon4a = new ModelRenderer(this);
		barrelcon4a.setRotationPoint(-3.0F, 0.0F, 0.0F);
		base.addChild(barrelcon4a);
		barrelcon4a.cubeList.add(new ModelBox(barrelcon4a, 34, 56, -1.0F, -1.0F, -3.0F, 2, 2, 6, 0.0F, false));

		barrelcon4b = new ModelRenderer(this);
		barrelcon4b.setRotationPoint(1.0F, -1.0F, 0.0F);
		barrelcon4a.addChild(barrelcon4b);
		setRotationAngle(barrelcon4b, 0.0F, 0.0F, -0.1745F);
		barrelcon4b.cubeList.add(new ModelBox(barrelcon4b, 0, 56, -2.0F, -2.0F, -3.0F, 2, 2, 6, 0.0F, false));

		support1a = new ModelRenderer(this);
		support1a.setRotationPoint(-3.0F, 0.0F, -3.0F);
		base.addChild(support1a);
		setRotationAngle(support1a, 0.0F, -0.7854F, 0.0F);
		support1a.cubeList.add(new ModelBox(support1a, 63, 70, -2.0F, -4.0F, -1.001F, 2, 4, 2, 0.0F, false));

		support1b = new ModelRenderer(this);
		support1b.setRotationPoint(0.0F, -4.0F, 0.0F);
		support1a.addChild(support1b);
		setRotationAngle(support1b, 0.0F, 0.0F, -0.3491F);
		support1b.cubeList.add(new ModelBox(support1b, 18, 84, -2.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support1c = new ModelRenderer(this);
		support1c.setRotationPoint(0.0F, -3.0F, 0.0F);
		support1b.addChild(support1c);
		setRotationAngle(support1c, 0.0F, 0.0F, -0.3491F);
		support1c.cubeList.add(new ModelBox(support1c, 9, 84, -2.0F, -3.0F, -1.001F, 2, 3, 2, 0.0F, false));

		support1d = new ModelRenderer(this);
		support1d.setRotationPoint(-2.0F, -3.0F, 0.0F);
		support1c.addChild(support1d);
		setRotationAngle(support1d, 0.0F, 0.0F, 0.3491F);
		support1d.cubeList.add(new ModelBox(support1d, 0, 84, 0.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support1e = new ModelRenderer(this);
		support1e.setRotationPoint(0.0F, -3.0F, 0.0F);
		support1d.addChild(support1e);
		setRotationAngle(support1e, 0.0F, 0.0F, 0.3491F);
		support1e.cubeList.add(new ModelBox(support1e, 27, 70, 0.0F, -5.0F, -1.001F, 2, 5, 2, 0.0F, false));

		support2a = new ModelRenderer(this);
		support2a.setRotationPoint(3.0F, 0.0F, -3.0F);
		base.addChild(support2a);
		setRotationAngle(support2a, 0.0F, 0.7854F, 0.0F);
		support2a.cubeList.add(new ModelBox(support2a, 54, 70, 0.0F, -4.0F, -1.001F, 2, 4, 2, 0.0F, false));

		support2b = new ModelRenderer(this);
		support2b.setRotationPoint(0.0F, -4.0F, 0.0F);
		support2a.addChild(support2b);
		setRotationAngle(support2b, 0.0F, 0.0F, 0.3491F);
		support2b.cubeList.add(new ModelBox(support2b, 72, 78, 0.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support2c = new ModelRenderer(this);
		support2c.setRotationPoint(0.0F, -3.0F, 0.0F);
		support2b.addChild(support2c);
		setRotationAngle(support2c, 0.0F, 0.0F, 0.3491F);
		support2c.cubeList.add(new ModelBox(support2c, 63, 78, 0.0F, -3.0F, -1.001F, 2, 3, 2, 0.0F, false));

		support2d = new ModelRenderer(this);
		support2d.setRotationPoint(2.0F, -3.0F, 0.0F);
		support2c.addChild(support2d);
		setRotationAngle(support2d, 0.0F, 0.0F, -0.3491F);
		support2d.cubeList.add(new ModelBox(support2d, 54, 78, -2.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support2e = new ModelRenderer(this);
		support2e.setRotationPoint(0.0F, -3.0F, 0.0F);
		support2d.addChild(support2e);
		setRotationAngle(support2e, 0.0F, 0.0F, -0.3491F);
		support2e.cubeList.add(new ModelBox(support2e, 18, 70, -2.0F, -5.0F, -1.001F, 2, 5, 2, 0.0F, false));

		support3a = new ModelRenderer(this);
		support3a.setRotationPoint(-3.0F, 0.0F, 3.0F);
		base.addChild(support3a);
		setRotationAngle(support3a, 0.0F, 0.7854F, 0.0F);
		support3a.cubeList.add(new ModelBox(support3a, 45, 70, -2.0F, -4.0F, -0.999F, 2, 4, 2, 0.0F, false));

		support3b = new ModelRenderer(this);
		support3b.setRotationPoint(0.0F, -4.0F, 0.0F);
		support3a.addChild(support3b);
		setRotationAngle(support3b, 0.0F, 0.0F, -0.3491F);
		support3b.cubeList.add(new ModelBox(support3b, 45, 78, -2.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support3c = new ModelRenderer(this);
		support3c.setRotationPoint(0.0F, -3.0F, 0.0F);
		support3b.addChild(support3c);
		setRotationAngle(support3c, 0.0F, 0.0F, -0.3491F);
		support3c.cubeList.add(new ModelBox(support3c, 36, 78, -2.0F, -3.0F, -0.999F, 2, 3, 2, 0.0F, false));

		support3d = new ModelRenderer(this);
		support3d.setRotationPoint(-2.0F, -3.0F, 0.0F);
		support3c.addChild(support3d);
		setRotationAngle(support3d, 0.0F, 0.0F, 0.3491F);
		support3d.cubeList.add(new ModelBox(support3d, 27, 78, 0.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support3e = new ModelRenderer(this);
		support3e.setRotationPoint(0.0F, -3.0F, 0.0F);
		support3d.addChild(support3e);
		setRotationAngle(support3e, 0.0F, 0.0F, 0.3491F);
		support3e.cubeList.add(new ModelBox(support3e, 9, 70, 0.0F, -5.0F, -0.999F, 2, 5, 2, 0.0F, false));

		support4a = new ModelRenderer(this);
		support4a.setRotationPoint(3.0F, 0.0F, 3.0F);
		base.addChild(support4a);
		setRotationAngle(support4a, 0.0F, -0.7854F, 0.0F);
		support4a.cubeList.add(new ModelBox(support4a, 36, 70, 0.0F, -4.0F, -0.999F, 2, 4, 2, 0.0F, false));

		support4b = new ModelRenderer(this);
		support4b.setRotationPoint(0.0F, -4.0F, 0.0F);
		support4a.addChild(support4b);
		setRotationAngle(support4b, 0.0F, 0.0F, 0.3491F);
		support4b.cubeList.add(new ModelBox(support4b, 18, 78, 0.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support4c = new ModelRenderer(this);
		support4c.setRotationPoint(0.0F, -3.0F, 0.0F);
		support4b.addChild(support4c);
		setRotationAngle(support4c, 0.0F, 0.0F, 0.3491F);
		support4c.cubeList.add(new ModelBox(support4c, 9, 78, 0.0F, -3.0F, -0.999F, 2, 3, 2, 0.0F, false));

		support4d = new ModelRenderer(this);
		support4d.setRotationPoint(2.0F, -3.0F, 0.0F);
		support4c.addChild(support4d);
		setRotationAngle(support4d, 0.0F, 0.0F, -0.3491F);
		support4d.cubeList.add(new ModelBox(support4d, 0, 78, -2.0F, -3.0F, -1.0F, 2, 3, 2, 0.0F, false));

		support4e = new ModelRenderer(this);
		support4e.setRotationPoint(0.0F, -3.0F, 0.0F);
		support4d.addChild(support4e);
		setRotationAngle(support4e, 0.0F, 0.0F, -0.3491F);
		support4e.cubeList.add(new ModelBox(support4e, 0, 70, -2.0F, -5.0F, -0.999F, 2, 5, 2, 0.0F, false));

		filterbase = new ModelRenderer(this);
		filterbase.setRotationPoint(0.0F, -4.0F, 0.0F);
		base.addChild(filterbase);
		

		filterbase1 = new ModelRenderer(this);
		filterbase1.setRotationPoint(0.0F, 0.0F, -4.0F);
		filterbase.addChild(filterbase1);
		filterbase1.cubeList.add(new ModelBox(filterbase1, 0, 65, -3.0F, 0.0F, 0.0F, 6, 2, 2, 0.0F, false));

		filterbase2 = new ModelRenderer(this);
		filterbase2.setRotationPoint(0.0F, 0.0F, 4.0F);
		filterbase.addChild(filterbase2);
		filterbase2.cubeList.add(new ModelBox(filterbase2, 68, 56, -3.0F, 0.0F, -2.0F, 6, 2, 2, 0.0F, false));

		filterbase3 = new ModelRenderer(this);
		filterbase3.setRotationPoint(4.0F, 0.0F, 0.0F);
		filterbase.addChild(filterbase3);
		filterbase3.cubeList.add(new ModelBox(filterbase3, 67, 47, -2.0F, 0.0F, -3.0F, 2, 2, 6, 0.0F, false));

		filterbase4 = new ModelRenderer(this);
		filterbase4.setRotationPoint(-4.0F, 0.0F, 0.0F);
		filterbase.addChild(filterbase4);
		filterbase4.cubeList.add(new ModelBox(filterbase4, 50, 47, 0.0F, 0.0F, -3.0F, 2, 2, 6, 0.0F, false));

		basin = new ModelRenderer(this);
		basin.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(basin);
		

		basinside1a = new ModelRenderer(this);
		basinside1a.setRotationPoint(0.0F, -16.0F, -6.0F);
		basin.addChild(basinside1a);
		basinside1a.cubeList.add(new ModelBox(basinside1a, 50, 35, -6.0F, 0.0F, -1.0F, 12, 5, 1, 0.0F, false));

		basinside1b = new ModelRenderer(this);
		basinside1b.setRotationPoint(0.0F, 5.0F, -1.0F);
		basinside1a.addChild(basinside1b);
		setRotationAngle(basinside1b, 0.2618F, 0.0F, 0.0F);
		basinside1b.cubeList.add(new ModelBox(basinside1b, 0, 47, -6.0F, 0.0F, 0.0F, 12, 4, 1, 0.0F, false));

		plank1 = new ModelRenderer(this);
		plank1.setRotationPoint(0.0F, 8.75F, 1.0F);
		basinside1a.addChild(plank1);
		setRotationAngle(plank1, 0.7854F, 0.0F, 0.0F);
		plank1.cubeList.add(new ModelBox(plank1, 27, 47, -5.0F, -1.0F, -1.0F, 10, 1, 1, 0.0F, false));

		basinside2a = new ModelRenderer(this);
		basinside2a.setRotationPoint(0.0F, -16.0F, 6.0F);
		basin.addChild(basinside2a);
		basinside2a.cubeList.add(new ModelBox(basinside2a, 23, 35, -6.0F, 0.0F, 0.0F, 12, 5, 1, 0.0F, false));

		basinside2b = new ModelRenderer(this);
		basinside2b.setRotationPoint(0.0F, 5.0F, 1.0F);
		basinside2a.addChild(basinside2b);
		setRotationAngle(basinside2b, -0.2618F, 0.0F, 0.0F);
		basinside2b.cubeList.add(new ModelBox(basinside2b, 77, 35, -6.0F, 0.0F, -1.0F, 12, 4, 1, 0.0F, false));

		plank2 = new ModelRenderer(this);
		plank2.setRotationPoint(0.0F, 8.75F, -1.0F);
		basinside2a.addChild(plank2);
		setRotationAngle(plank2, -0.7854F, 0.0F, 0.0F);
		plank2.cubeList.add(new ModelBox(plank2, 0, 53, -5.0F, -1.0F, 0.0F, 10, 1, 1, 0.0F, false));

		basinside3a = new ModelRenderer(this);
		basinside3a.setRotationPoint(-6.0F, -16.0F, 0.0F);
		basin.addChild(basinside3a);
		basinside3a.cubeList.add(new ModelBox(basinside3a, 72, 0, -1.0F, 0.0F, -6.0F, 1, 5, 12, 0.0F, false));

		basinside3b = new ModelRenderer(this);
		basinside3b.setRotationPoint(-1.0F, 5.0F, 0.0F);
		basinside3a.addChild(basinside3b);
		setRotationAngle(basinside3b, 0.0F, 0.0F, -0.2618F);
		basinside3b.cubeList.add(new ModelBox(basinside3b, 27, 18, 0.0F, 0.0F, -6.0F, 1, 4, 12, 0.0F, false));

		plank3 = new ModelRenderer(this);
		plank3.setRotationPoint(1.0F, 8.75F, 0.0F);
		basinside3a.addChild(plank3);
		setRotationAngle(plank3, 0.0F, 0.0F, -0.7854F);
		plank3.cubeList.add(new ModelBox(plank3, 0, 35, -1.0F, -1.0F, -5.0F, 1, 1, 10, 0.0F, false));

		basinside4a = new ModelRenderer(this);
		basinside4a.setRotationPoint(6.0F, -16.0F, 0.0F);
		basin.addChild(basinside4a);
		basinside4a.cubeList.add(new ModelBox(basinside4a, 45, 0, 0.0F, 0.0F, -6.0F, 1, 5, 12, 0.0F, false));

		basinside4b = new ModelRenderer(this);
		basinside4b.setRotationPoint(1.0F, 5.0F, 0.0F);
		basinside4a.addChild(basinside4b);
		setRotationAngle(basinside4b, 0.0F, 0.0F, 0.2618F);
		basinside4b.cubeList.add(new ModelBox(basinside4b, 0, 18, -1.0F, 0.0F, -6.0F, 1, 4, 12, 0.0F, false));

		plank4 = new ModelRenderer(this);
		plank4.setRotationPoint(-1.0F, 8.75F, 0.0F);
		basinside4a.addChild(plank4);
		setRotationAngle(plank4, 0.0F, 0.0F, 0.7854F);
		plank4.cubeList.add(new ModelBox(plank4, 54, 18, 0.0F, -1.0F, -5.0F, 1, 1, 10, 0.0F, false));

		cornerpiece1 = new ModelRenderer(this);
		cornerpiece1.setRotationPoint(-6.0F, -16.0F, -6.0F);
		basin.addChild(cornerpiece1);
		cornerpiece1.cubeList.add(new ModelBox(cornerpiece1, 32, 87, 0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cornerpiece2 = new ModelRenderer(this);
		cornerpiece2.setRotationPoint(6.0F, -16.0F, -6.0F);
		basin.addChild(cornerpiece2);
		cornerpiece2.cubeList.add(new ModelBox(cornerpiece2, 32, 84, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cornerpiece3 = new ModelRenderer(this);
		cornerpiece3.setRotationPoint(6.0F, -16.0F, 6.0F);
		basin.addChild(cornerpiece3);
		cornerpiece3.cubeList.add(new ModelBox(cornerpiece3, 27, 87, -1.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));

		cornerpiece4 = new ModelRenderer(this);
		cornerpiece4.setRotationPoint(-6.0F, -16.0F, 6.0F);
		basin.addChild(cornerpiece4);
		cornerpiece4.cubeList.add(new ModelBox(cornerpiece4, 27, 84, 0.0F, 0.0F, -1.0F, 1, 1, 1, 0.0F, false));

		basin_bottom = new ModelRenderer(this);
		basin_bottom.setRotationPoint(0.0F, -7.0F, 0.0F);
		basin.addChild(basin_bottom);
		basin_bottom.cubeList.add(new ModelBox(basin_bottom, 0, 0, -5.5F, -1.0F, -5.5F, 11, 1, 11, 0.0F, false));

		basin_opening = new ModelRenderer(this);
		basin_opening.setRotationPoint(0.0F, 0.0F, 0.0F);
		basin_bottom.addChild(basin_opening);
		basin_opening.cubeList.add(new ModelBox(basin_opening, 85, 65, -1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		base.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}