package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFragSpore extends ModelBase {
	private final ModelRenderer base;
	private final ModelRenderer core;
	private final ModelRenderer mycelium_ur;
	private final ModelRenderer mycelium_uf;
	private final ModelRenderer mycelium_ub;
	private final ModelRenderer mycelium_ul;
	private final ModelRenderer mycelium_dr;
	private final ModelRenderer mycelium_df;
	private final ModelRenderer mycelium_db;
	private final ModelRenderer mycelium_dl;
	private final ModelRenderer mycelium_fr;
	private final ModelRenderer mycelium_fl;
	private final ModelRenderer mycelium_br;
	private final ModelRenderer mycelium_bl;

	public ModelFragSpore() {
		textureWidth = 64;
		textureHeight = 64;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 20.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		core = new ModelRenderer(this);
		core.setRotationPoint(0.0F, 0.0F, 0.0F);
		base.addChild(core);
		core.cubeList.add(new ModelBox(core, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));

		mycelium_ur = new ModelRenderer(this);
		mycelium_ur.setRotationPoint(-2.0F, -2.0F, 0.0F);
		base.addChild(mycelium_ur);
		setRotationAngle(mycelium_ur, 0.0F, 0.0F, -0.7854F);
		mycelium_ur.cubeList.add(new ModelBox(mycelium_ur, 0, 0, 0.0F, -2.0F, -3.0F, 0, 2, 6, 0.0F, false));

		mycelium_uf = new ModelRenderer(this);
		mycelium_uf.setRotationPoint(0.0F, -2.0F, 2.0F);
		base.addChild(mycelium_uf);
		setRotationAngle(mycelium_uf, -0.7854F, 0.0F, 0.0F);
		mycelium_uf.cubeList.add(new ModelBox(mycelium_uf, 0, 0, -3.0F, -2.0F, 0.0F, 6, 2, 0, 0.0F, false));

		mycelium_ub = new ModelRenderer(this);
		mycelium_ub.setRotationPoint(0.0F, -2.0F, -2.0F);
		base.addChild(mycelium_ub);
		setRotationAngle(mycelium_ub, 0.7854F, 0.0F, 0.0F);
		mycelium_ub.cubeList.add(new ModelBox(mycelium_ub, 0, 0, -3.0F, -2.0F, 0.0F, 6, 2, 0, 0.0F, false));

		mycelium_ul = new ModelRenderer(this);
		mycelium_ul.setRotationPoint(2.0F, -2.0F, 0.0F);
		base.addChild(mycelium_ul);
		setRotationAngle(mycelium_ul, 0.0F, 0.0F, 0.7854F);
		mycelium_ul.cubeList.add(new ModelBox(mycelium_ul, 0, 0, 0.0F, -2.0F, -3.0F, 0, 2, 6, 0.0F, true));

		mycelium_dr = new ModelRenderer(this);
		mycelium_dr.setRotationPoint(-2.0F, 2.0F, 0.0F);
		base.addChild(mycelium_dr);
		setRotationAngle(mycelium_dr, 0.0F, 0.0F, 0.7854F);
		mycelium_dr.cubeList.add(new ModelBox(mycelium_dr, 0, 0, 0.0F, 0.0F, -3.0F, 0, 2, 6, 0.0F, false));

		mycelium_df = new ModelRenderer(this);
		mycelium_df.setRotationPoint(0.0F, 2.0F, 2.0F);
		base.addChild(mycelium_df);
		setRotationAngle(mycelium_df, 0.7854F, 0.0F, 0.0F);
		mycelium_df.cubeList.add(new ModelBox(mycelium_df, 0, 0, -3.0F, 0.0F, 0.0F, 6, 2, 0, 0.0F, false));

		mycelium_db = new ModelRenderer(this);
		mycelium_db.setRotationPoint(0.0F, 2.0F, -2.0F);
		base.addChild(mycelium_db);
		setRotationAngle(mycelium_db, -0.7854F, 0.0F, 0.0F);
		mycelium_db.cubeList.add(new ModelBox(mycelium_db, 0, 0, -3.0F, 0.0F, 0.0F, 6, 2, 0, 0.0F, false));

		mycelium_dl = new ModelRenderer(this);
		mycelium_dl.setRotationPoint(2.0F, 2.0F, 0.0F);
		base.addChild(mycelium_dl);
		setRotationAngle(mycelium_dl, 0.0F, 0.0F, -0.7854F);
		mycelium_dl.cubeList.add(new ModelBox(mycelium_dl, 0, 0, 0.0F, 0.0F, -3.0F, 0, 2, 6, 0.0F, true));

		mycelium_fr = new ModelRenderer(this);
		mycelium_fr.setRotationPoint(-2.0F, 0.0F, -2.0F);
		base.addChild(mycelium_fr);
		setRotationAngle(mycelium_fr, 0.0F, -0.7854F, 0.0F);
		mycelium_fr.cubeList.add(new ModelBox(mycelium_fr, 0, 0, -2.0F, -3.0F, 0.0F, 2, 6, 0, 0.0F, false));

		mycelium_fl = new ModelRenderer(this);
		mycelium_fl.setRotationPoint(2.0F, 0.0F, -2.0F);
		base.addChild(mycelium_fl);
		setRotationAngle(mycelium_fl, 0.0F, 0.7854F, 0.0F);
		mycelium_fl.cubeList.add(new ModelBox(mycelium_fl, 0, 0, 0.0F, -3.0F, 0.0F, 2, 6, 0, 0.0F, true));

		mycelium_br = new ModelRenderer(this);
		mycelium_br.setRotationPoint(-2.0F, 0.0F, 2.0F);
		base.addChild(mycelium_br);
		setRotationAngle(mycelium_br, 0.0F, 0.7854F, 0.0F);
		mycelium_br.cubeList.add(new ModelBox(mycelium_br, 0, 0, -2.0F, -3.0F, 0.0F, 2, 6, 0, 0.0F, false));

		mycelium_bl = new ModelRenderer(this);
		mycelium_bl.setRotationPoint(2.0F, 0.0F, 2.0F);
		base.addChild(mycelium_bl);
		setRotationAngle(mycelium_bl, 0.0F, -0.7854F, 0.0F);
		mycelium_bl.cubeList.add(new ModelBox(mycelium_bl, 0, 0, 0.0F, -3.0F, 0.0F, 2, 6, 0, 0.0F, true));
	}

	public void render(float scale) {
		base.render(scale);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}