package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntitySporeMinion;

@SideOnly(Side.CLIENT)
public class ModelSporeMinionFloat extends ModelBase {
	private final ModelRenderer body_base;
	private final ModelRenderer spore_base;
	private final ModelRenderer spore_sac_top;
	private final ModelRenderer spore_sac_f1;
	private final ModelRenderer spore_sac_f2;
	private final ModelRenderer mycosac_f1;
	private final ModelRenderer spore_sac_b1;
	private final ModelRenderer spore_sac_b2;
	private final ModelRenderer mycosac_b1;
	private final ModelRenderer mycosac_b2;
	private final ModelRenderer spore_sac_r1;
	private final ModelRenderer spore_sac_r2;
	private final ModelRenderer mycosac_r1;
	private final ModelRenderer mycosac_r2;
	private final ModelRenderer spore_sac_l1;
	private final ModelRenderer spore_sac_l2;
	private final ModelRenderer mycosac_l1;
	private final ModelRenderer mycosac_l2;
	private final ModelRenderer tentaleg_fr1_anchor;
	private final ModelRenderer tentaleg_fr1;
	private final ModelRenderer tentlet_fr1;
	private final ModelRenderer tentaleg_fr2;
	private final ModelRenderer tentaleg_fr3;
	private final ModelRenderer tentlet_fr2;
	private final ModelRenderer tentaleg_fr4;
	private final ModelRenderer tentaleg_fl1_anchor;
	private final ModelRenderer tentaleg_fl1;
	private final ModelRenderer tentlet_fl1;
	private final ModelRenderer tentaleg_fl2;
	private final ModelRenderer tentaleg_fl3;
	private final ModelRenderer tentlet_fl2;
	private final ModelRenderer tentaleg_fl4;
	private final ModelRenderer tentaleg_br1_anchor;
	private final ModelRenderer tentaleg_br1;
	private final ModelRenderer tentlet_br1;
	private final ModelRenderer tentaleg_br2;
	private final ModelRenderer tentaleg_br3;
	private final ModelRenderer tentlet_br2;
	private final ModelRenderer tentaleg_br4;
	private final ModelRenderer tentaleg_bl1_anchor;
	private final ModelRenderer tentaleg_bl1;
	private final ModelRenderer tentlet_bl1;
	private final ModelRenderer tentaleg_bl2;
	private final ModelRenderer tentaleg_bl3;
	private final ModelRenderer tentlet_bl2;
	private final ModelRenderer tentaleg_bl4;
	
    public ModelRenderer spore_1;
    public ModelRenderer spore_2;
    public ModelRenderer spore_3;
    public ModelRenderer spore_5;
    public ModelRenderer spore_4;
    public ModelRenderer spore_6;

	public ModelSporeMinionFloat() {
		textureWidth = 128;
		textureHeight = 64;

		body_base = new ModelRenderer(this);
		body_base.setRotationPoint(0.0F, 20.25F, 0.0F);
		body_base.cubeList.add(new ModelBox(body_base, 36, 11, -2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F, false));

		spore_base = new ModelRenderer(this);
		spore_base.setRotationPoint(0.0F, -2.0F, 0.0F);
		body_base.addChild(spore_base);
		setRotationAngle(spore_base, -0.1745F, 0.0F, 0.0F);
		

		spore_sac_top = new ModelRenderer(this);
		spore_sac_top.setRotationPoint(0.0F, -5.0F, 0.0F);
		spore_base.addChild(spore_sac_top);
		

		spore_sac_f1 = new ModelRenderer(this);
		spore_sac_f1.setRotationPoint(0.0F, -5.0F, -2.0F);
		spore_base.addChild(spore_sac_f1);
		setRotationAngle(spore_sac_f1, -0.2618F, 0.0F, 0.0F);
		

		spore_sac_f2 = new ModelRenderer(this);
		spore_sac_f2.setRotationPoint(0.0F, 3.0F, -1.0F);
		spore_sac_f1.addChild(spore_sac_f2);
		setRotationAngle(spore_sac_f2, 0.5236F, 0.0F, 0.0F);
		

		mycosac_f1 = new ModelRenderer(this);
		mycosac_f1.setRotationPoint(0.0F, 4.0F, 0.0F);
		spore_sac_f2.addChild(mycosac_f1);
		setRotationAngle(mycosac_f1, 0.2182F, 0.0F, 0.0F);
		

		spore_sac_b1 = new ModelRenderer(this);
		spore_sac_b1.setRotationPoint(0.0F, -5.0F, 2.0F);
		spore_base.addChild(spore_sac_b1);
		setRotationAngle(spore_sac_b1, 0.2618F, 0.0F, 0.0F);
		

		spore_sac_b2 = new ModelRenderer(this);
		spore_sac_b2.setRotationPoint(0.0F, 3.0F, 1.0F);
		spore_sac_b1.addChild(spore_sac_b2);
		setRotationAngle(spore_sac_b2, -0.5236F, 0.0F, 0.0F);
		

		mycosac_b1 = new ModelRenderer(this);
		mycosac_b1.setRotationPoint(0.0F, 4.0F, 0.0F);
		spore_sac_b2.addChild(mycosac_b1);
		setRotationAngle(mycosac_b1, -0.2182F, 0.0F, 0.0F);
		mycosac_b1.cubeList.add(new ModelBox(mycosac_b1, 19, 11, -3.0F, -4.0F, -2.0F, 6, 4, 2, 0.0F, false));

		mycosac_b2 = new ModelRenderer(this);
		mycosac_b2.setRotationPoint(0.0F, -4.0F, 0.0F);
		mycosac_b1.addChild(mycosac_b2);
		setRotationAngle(mycosac_b2, 0.3054F, 0.0F, 0.0F);
		mycosac_b2.cubeList.add(new ModelBox(mycosac_b2, 0, 11, -3.0F, -4.0F, -3.0F, 6, 4, 3, 0.0F, false));

		spore_sac_r1 = new ModelRenderer(this);
		spore_sac_r1.setRotationPoint(-2.0F, -5.0F, 0.0F);
		spore_base.addChild(spore_sac_r1);
		setRotationAngle(spore_sac_r1, 0.0F, 0.0F, 0.2618F);
		

		spore_sac_r2 = new ModelRenderer(this);
		spore_sac_r2.setRotationPoint(-1.0F, 3.0F, 0.0F);
		spore_sac_r1.addChild(spore_sac_r2);
		setRotationAngle(spore_sac_r2, 0.0F, 0.0F, -0.5236F);
		

		mycosac_r1 = new ModelRenderer(this);
		mycosac_r1.setRotationPoint(0.0F, 4.0F, 0.0F);
		spore_sac_r2.addChild(mycosac_r1);
		setRotationAngle(mycosac_r1, 0.0F, 0.0F, -0.2182F);
		mycosac_r1.cubeList.add(new ModelBox(mycosac_r1, 17, 0, 0.0F, -4.0F, -3.0F, 2, 4, 6, 0.0F, false));

		mycosac_r2 = new ModelRenderer(this);
		mycosac_r2.setRotationPoint(0.0F, -4.0F, 0.0F);
		mycosac_r1.addChild(mycosac_r2);
		setRotationAngle(mycosac_r2, 0.0F, 0.0F, 0.3054F);
		mycosac_r2.cubeList.add(new ModelBox(mycosac_r2, 0, 41, 0.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F, false));

		spore_sac_l1 = new ModelRenderer(this);
		spore_sac_l1.setRotationPoint(2.0F, -5.0F, 0.0F);
		spore_base.addChild(spore_sac_l1);
		setRotationAngle(spore_sac_l1, 0.0F, 0.0F, -0.2618F);
		

		spore_sac_l2 = new ModelRenderer(this);
		spore_sac_l2.setRotationPoint(1.0F, 3.0F, 0.0F);
		spore_sac_l1.addChild(spore_sac_l2);
		setRotationAngle(spore_sac_l2, 0.0F, 0.0F, 0.5236F);
		

		mycosac_l1 = new ModelRenderer(this);
		mycosac_l1.setRotationPoint(0.0F, 4.0F, 0.0F);
		spore_sac_l2.addChild(mycosac_l1);
		setRotationAngle(mycosac_l1, 0.0F, 0.0F, 0.2182F);
		mycosac_l1.cubeList.add(new ModelBox(mycosac_l1, 0, 0, -2.0F, -4.0F, -3.0F, 2, 4, 6, 0.0F, false));

		mycosac_l2 = new ModelRenderer(this);
		mycosac_l2.setRotationPoint(0.0F, -4.0F, 0.0F);
		mycosac_l1.addChild(mycosac_l2);
		setRotationAngle(mycosac_l2, 0.0F, 0.0F, -0.3054F);
		mycosac_l2.cubeList.add(new ModelBox(mycosac_l2, 52, 35, -2.0F, -2.0F, 0.0F, 2, 2, 3, 0.0F, false));

		tentaleg_fr1_anchor = new ModelRenderer(this);
		tentaleg_fr1_anchor.setRotationPoint(-1.0F, 0.0F, -1.0F);
		body_base.addChild(tentaleg_fr1_anchor);
		setRotationAngle(tentaleg_fr1_anchor, 0.0F, -0.7854F, 0.0F);
		

		tentaleg_fr1 = new ModelRenderer(this);
		tentaleg_fr1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentaleg_fr1_anchor.addChild(tentaleg_fr1);
		setRotationAngle(tentaleg_fr1, 0.0F, 0.0F, -1.5708F);
		tentaleg_fr1.cubeList.add(new ModelBox(tentaleg_fr1, 0, 35, -3.0F, -1.0F, -1.5F, 3, 2, 3, 0.0F, false));

		tentlet_fr1 = new ModelRenderer(this);
		tentlet_fr1.setRotationPoint(-3.0F, 0.0F, 0.5F);
		tentaleg_fr1.addChild(tentlet_fr1);
		setRotationAngle(tentlet_fr1, 0.0F, 0.2618F, 0.0F);
		tentlet_fr1.cubeList.add(new ModelBox(tentlet_fr1, 0, 56, -1.0F, -1.0F, 0.0F, 1, 2, 1, 0.001F, false));

		tentaleg_fr2 = new ModelRenderer(this);
		tentaleg_fr2.setRotationPoint(-3.0F, -1.0F, -0.5F);
		tentaleg_fr1.addChild(tentaleg_fr2);
		tentaleg_fr2.cubeList.add(new ModelBox(tentaleg_fr2, 11, 47, -3.0F, 0.0F, -0.999F, 3, 2, 2, 0.0F, false));

		tentaleg_fr3 = new ModelRenderer(this);
		tentaleg_fr3.setRotationPoint(-3.0F, 0.0F, 0.0F);
		tentaleg_fr2.addChild(tentaleg_fr3);
		tentaleg_fr3.cubeList.add(new ModelBox(tentaleg_fr3, 0, 47, -3.0F, 0.0F, -0.999F, 3, 2, 2, 0.001F, false));

		tentlet_fr2 = new ModelRenderer(this);
		tentlet_fr2.setRotationPoint(-3.0F, 1.0F, 0.0F);
		tentaleg_fr3.addChild(tentlet_fr2);
		setRotationAngle(tentlet_fr2, 0.0F, -0.1745F, 0.0F);
		tentlet_fr2.cubeList.add(new ModelBox(tentlet_fr2, 52, 52, -1.0F, -1.0F, -1.0F, 1, 2, 1, 0.0F, false));

		tentaleg_fr4 = new ModelRenderer(this);
		tentaleg_fr4.setRotationPoint(-3.0F, 0.0F, 0.0F);
		tentaleg_fr3.addChild(tentaleg_fr4);
		tentaleg_fr4.cubeList.add(new ModelBox(tentaleg_fr4, 9, 52, -3.0F, 0.0F, 0.0F, 3, 2, 1, 0.0F, false));

		tentaleg_fl1_anchor = new ModelRenderer(this);
		tentaleg_fl1_anchor.setRotationPoint(1.0F, 0.0F, -1.0F);
		body_base.addChild(tentaleg_fl1_anchor);
		setRotationAngle(tentaleg_fl1_anchor, 0.0F, 0.7854F, 0.0F);
		

		tentaleg_fl1 = new ModelRenderer(this);
		tentaleg_fl1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentaleg_fl1_anchor.addChild(tentaleg_fl1);
		setRotationAngle(tentaleg_fl1, 0.0F, 0.0F, 1.5708F);
		tentaleg_fl1.cubeList.add(new ModelBox(tentaleg_fl1, 52, 28, 0.0F, -1.0F, -1.5F, 3, 2, 3, 0.0F, false));

		tentlet_fl1 = new ModelRenderer(this);
		tentlet_fl1.setRotationPoint(3.0F, 0.0F, 0.5F);
		tentaleg_fl1.addChild(tentlet_fl1);
		setRotationAngle(tentlet_fl1, 0.0F, -0.2618F, 0.0F);
		tentlet_fl1.cubeList.add(new ModelBox(tentlet_fl1, 47, 52, 0.0F, -1.0F, 0.0F, 1, 2, 1, 0.001F, false));

		tentaleg_fl2 = new ModelRenderer(this);
		tentaleg_fl2.setRotationPoint(3.0F, -1.0F, -0.5F);
		tentaleg_fl1.addChild(tentaleg_fl2);
		tentaleg_fl2.cubeList.add(new ModelBox(tentaleg_fl2, 44, 41, 0.0F, 0.0F, -0.999F, 3, 2, 2, 0.0F, false));

		tentaleg_fl3 = new ModelRenderer(this);
		tentaleg_fl3.setRotationPoint(3.0F, 0.0F, 0.0F);
		tentaleg_fl2.addChild(tentaleg_fl3);
		tentaleg_fl3.cubeList.add(new ModelBox(tentaleg_fl3, 33, 41, 0.0F, 0.0F, -0.999F, 3, 2, 2, 0.001F, false));

		tentlet_fl2 = new ModelRenderer(this);
		tentlet_fl2.setRotationPoint(3.0F, 1.0F, 0.0F);
		tentaleg_fl3.addChild(tentlet_fl2);
		setRotationAngle(tentlet_fl2, 0.0F, 0.1745F, 0.0F);
		tentlet_fl2.cubeList.add(new ModelBox(tentlet_fl2, 42, 52, 0.0F, -1.0F, -1.0F, 1, 2, 1, 0.0F, false));

		tentaleg_fl4 = new ModelRenderer(this);
		tentaleg_fl4.setRotationPoint(3.0F, 0.0F, 0.0F);
		tentaleg_fl3.addChild(tentaleg_fl4);
		tentaleg_fl4.cubeList.add(new ModelBox(tentaleg_fl4, 0, 52, 0.0F, 0.0F, 0.0F, 3, 2, 1, 0.0F, false));

		tentaleg_br1_anchor = new ModelRenderer(this);
		tentaleg_br1_anchor.setRotationPoint(-1.0F, 0.0F, 1.0F);
		body_base.addChild(tentaleg_br1_anchor);
		setRotationAngle(tentaleg_br1_anchor, 0.0F, 0.7854F, 0.0F);
		

		tentaleg_br1 = new ModelRenderer(this);
		tentaleg_br1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentaleg_br1_anchor.addChild(tentaleg_br1);
		setRotationAngle(tentaleg_br1, 0.0F, 0.0F, -1.5708F);
		tentaleg_br1.cubeList.add(new ModelBox(tentaleg_br1, 39, 28, -3.0F, -1.0F, -1.5F, 3, 2, 3, 0.0F, false));

		tentlet_br1 = new ModelRenderer(this);
		tentlet_br1.setRotationPoint(-3.0F, 0.0F, -0.5F);
		tentaleg_br1.addChild(tentlet_br1);
		setRotationAngle(tentlet_br1, 0.0F, -0.2618F, 0.0F);
		tentlet_br1.cubeList.add(new ModelBox(tentlet_br1, 37, 52, -1.0F, -1.0F, -1.0F, 1, 2, 1, 0.001F, false));

		tentaleg_br2 = new ModelRenderer(this);
		tentaleg_br2.setRotationPoint(-3.0F, -1.0F, 0.5F);
		tentaleg_br1.addChild(tentaleg_br2);
		tentaleg_br2.cubeList.add(new ModelBox(tentaleg_br2, 22, 41, -3.0F, 0.0F, -1.001F, 3, 2, 2, 0.0F, false));

		tentaleg_br3 = new ModelRenderer(this);
		tentaleg_br3.setRotationPoint(-3.0F, 0.0F, 0.0F);
		tentaleg_br2.addChild(tentaleg_br3);
		tentaleg_br3.cubeList.add(new ModelBox(tentaleg_br3, 31, 47, -2.0F, 0.0F, -1.001F, 2, 2, 2, 0.001F, false));

		tentlet_br2 = new ModelRenderer(this);
		tentlet_br2.setRotationPoint(-2.0F, 1.0F, 0.0F);
		tentaleg_br3.addChild(tentlet_br2);
		setRotationAngle(tentlet_br2, 0.0F, 0.2618F, 0.0F);
		tentlet_br2.cubeList.add(new ModelBox(tentlet_br2, 25, 52, -2.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F, false));

		tentaleg_br4 = new ModelRenderer(this);
		tentaleg_br4.setRotationPoint(-2.0F, 0.0F, 0.0F);
		tentaleg_br3.addChild(tentaleg_br4);
		tentaleg_br4.cubeList.add(new ModelBox(tentaleg_br4, 49, 47, -3.0F, 0.0F, -1.0F, 3, 2, 1, 0.0F, false));

		tentaleg_bl1_anchor = new ModelRenderer(this);
		tentaleg_bl1_anchor.setRotationPoint(1.0F, 0.0F, 1.0F);
		body_base.addChild(tentaleg_bl1_anchor);
		setRotationAngle(tentaleg_bl1_anchor, 0.0F, -0.7854F, 0.0F);
		

		tentaleg_bl1 = new ModelRenderer(this);
		tentaleg_bl1.setRotationPoint(0.0F, 0.0F, 0.0F);
		tentaleg_bl1_anchor.addChild(tentaleg_bl1);
		setRotationAngle(tentaleg_bl1, 0.0F, 0.0F, 1.5708F);
		tentaleg_bl1.cubeList.add(new ModelBox(tentaleg_bl1, 26, 28, 0.0F, -1.0F, -1.5F, 3, 2, 3, 0.0F, false));

		tentlet_bl1 = new ModelRenderer(this);
		tentlet_bl1.setRotationPoint(3.0F, 0.0F, -0.5F);
		tentaleg_bl1.addChild(tentlet_bl1);
		setRotationAngle(tentlet_bl1, 0.0F, 0.2618F, 0.0F);
		tentlet_bl1.cubeList.add(new ModelBox(tentlet_bl1, 32, 52, 0.0F, -1.0F, -1.0F, 1, 2, 1, 0.001F, false));

		tentaleg_bl2 = new ModelRenderer(this);
		tentaleg_bl2.setRotationPoint(3.0F, -1.0F, 0.5F);
		tentaleg_bl1.addChild(tentaleg_bl2);
		tentaleg_bl2.cubeList.add(new ModelBox(tentaleg_bl2, 11, 41, 0.0F, 0.0F, -1.001F, 3, 2, 2, 0.0F, false));

		tentaleg_bl3 = new ModelRenderer(this);
		tentaleg_bl3.setRotationPoint(3.0F, 0.0F, 0.0F);
		tentaleg_bl2.addChild(tentaleg_bl3);
		tentaleg_bl3.cubeList.add(new ModelBox(tentaleg_bl3, 22, 47, 0.0F, 0.0F, -1.001F, 2, 2, 2, 0.001F, false));

		tentlet_bl2 = new ModelRenderer(this);
		tentlet_bl2.setRotationPoint(2.0F, 1.0F, 0.0F);
		tentaleg_bl3.addChild(tentlet_bl2);
		setRotationAngle(tentlet_bl2, 0.0F, -0.2618F, 0.0F);
		tentlet_bl2.cubeList.add(new ModelBox(tentlet_bl2, 18, 52, 0.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F, false));

		tentaleg_bl4 = new ModelRenderer(this);
		tentaleg_bl4.setRotationPoint(2.0F, 0.0F, 0.0F);
		tentaleg_bl3.addChild(tentaleg_bl4);
		tentaleg_bl4.cubeList.add(new ModelBox(tentaleg_bl4, 40, 47, 0.0F, 0.0F, -1.0F, 3, 2, 1, 0.0F, false));

		// spore
		this.spore_1 = new ModelRenderer(this, 108, 0);
		this.spore_1.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.spore_1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);

		this.spore_2 = new ModelRenderer(this, 108, 0);
		this.spore_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spore_2.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		this.setRotationAngle(spore_2, 0.0F, 0.7853981633974483F, 0.0F);

		this.spore_3 = new ModelRenderer(this, 108, 0);
		this.spore_3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spore_3.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		this.setRotationAngle(spore_3, 0.7853981633974483F, 0.0F, 0.0F);

		this.spore_4 = new ModelRenderer(this, 108, 0);
		this.spore_4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spore_4.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		this.setRotationAngle(spore_4, 0.0F, 0.7853981633974483F, 0.0F);

		this.spore_5 = new ModelRenderer(this, 108, 0);
		this.spore_5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spore_5.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		this.setRotationAngle(spore_5, -0.7853981633974483F, 0.0F, 0.0F);

		this.spore_6 = new ModelRenderer(this, 108, 0);
		this.spore_6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spore_6.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5, 0.0F);
		this.setRotationAngle(spore_6, 0.0F, 0.7853981633974483F, 0.0F);

		this.spore_1.addChild(this.spore_2);
		this.spore_1.addChild(this.spore_3);
		this.spore_1.addChild(this.spore_5);
		this.spore_3.addChild(this.spore_4);
		this.spore_5.addChild(this.spore_6);
	}

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float scale) {
		//body_base.render(scale);
	}

    public void renderSpore(float scale) {
    	this.spore_1.render(scale);
    }

    public void renderTendrils(float scale) {
    	 this.body_base.render(scale);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float swing, float speed, float partialRenderTicks) {
    	EntitySporeMinion spore = (EntitySporeMinion) entity;
		float smoothed_1 = spore.animation_1 + (spore.animation_1 - spore.prev_animation_1) * partialRenderTicks;
		float smoothed_2 = spore.animation_2 + (spore.animation_2 - spore.prev_animation_2) * partialRenderTicks;
		float movementCos = MathHelper.cos(swing * 1.5F + (float) Math.PI) * 1.5F * speed *0.5F;
		float movementSin = MathHelper.sin(swing * 1.5F + (float) Math.PI) * 1.5F * speed *0.5F;

		body_base.rotateAngleX = convertDegtoRad(0F) + smoothed_2 * convertDegtoRad(0.5F);

		tentaleg_fl1.rotateAngleZ = convertDegtoRad(90F) - smoothed_1 * convertDegtoRad(10.75F) + (!spore.getIsFalling() ? movementSin : 0F);
		tentaleg_fr1.rotateAngleZ = convertDegtoRad(-90F) + smoothed_1 * convertDegtoRad(10.75F) - (!spore.getIsFalling() ? movementCos : 0F);
		tentaleg_bl1.rotateAngleZ = convertDegtoRad(90F) - smoothed_1 * convertDegtoRad(10.25F) + (!spore.getIsFalling() ? movementSin : 0F);
		tentaleg_br1.rotateAngleZ = convertDegtoRad(-90F) + smoothed_1 * convertDegtoRad(10.25F) - (!spore.getIsFalling() ? movementCos : 0F);

		tentaleg_fl2.rotateAngleZ = convertDegtoRad(0F) + smoothed_2 * convertDegtoRad(3.25F);
		tentaleg_fr2.rotateAngleZ  = convertDegtoRad(0F) - smoothed_2 * convertDegtoRad(3.25F);
		tentaleg_bl2.rotateAngleZ = convertDegtoRad(0F) + smoothed_2 * convertDegtoRad(2.5F);
		tentaleg_br2.rotateAngleZ = convertDegtoRad(0F) - smoothed_2 * convertDegtoRad(2.5F);

		tentaleg_fl3.rotateAngleZ = convertDegtoRad(0F) + smoothed_2 * convertDegtoRad(3.75F);
		tentaleg_fr3.rotateAngleZ = convertDegtoRad(0F) - smoothed_2 * convertDegtoRad(3.75F);
		tentaleg_bl3.rotateAngleZ = convertDegtoRad(0F) + smoothed_2 * convertDegtoRad(3.25F);
		tentaleg_br3.rotateAngleZ = convertDegtoRad(0F) - smoothed_2 * convertDegtoRad(3.25F);

		tentaleg_fl4.rotateAngleZ = convertDegtoRad(0F) + smoothed_1 * convertDegtoRad(3.5F);
		tentaleg_fr4.rotateAngleZ = convertDegtoRad(0F) - smoothed_1 * convertDegtoRad(3.5F);
		tentaleg_bl4.rotateAngleZ = convertDegtoRad(0F) + smoothed_1 * convertDegtoRad(3F);
		tentaleg_br4.rotateAngleZ = convertDegtoRad(0F) - smoothed_1 * convertDegtoRad(3F);

		tentaleg_fl1.rotateAngleX = -movementCos;
		tentaleg_fr1.rotateAngleX = movementSin;
		tentaleg_bl1.rotateAngleX = movementCos;
		tentaleg_br1.rotateAngleX = movementSin;
    }

	public float convertDegtoRad(float angleIn) {
		return angleIn * ((float) Math.PI / 180F);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}