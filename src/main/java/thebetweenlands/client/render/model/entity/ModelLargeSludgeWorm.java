package thebetweenlands.client.render.model.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;

/**
 * BLWormFront - TripleHeadedSheep
 * Created using Tabula 7.0.0
 */
public class ModelLargeSludgeWorm extends ModelBase {
	public ModelRenderer body_base;
	public ModelRenderer midpiece_spine4;
	public ModelRenderer endpiece_spine10;
	public ModelRenderer sludge_front1;
	public ModelRenderer sludge_mid1;
	public ModelRenderer sludge_back1;
	public ModelRenderer spine1;
	public ModelRenderer ribs1;
	public ModelRenderer spine2;
	public ModelRenderer spine3;
	public ModelRenderer artery1;
	public ModelRenderer head1;
	public ModelRenderer head2;
	public ModelRenderer upperjaw;
	public ModelRenderer lowerjaw_left;
	public ModelRenderer lowerjaw_right;
	public ModelRenderer artery2;
	public ModelRenderer artery3;
	public ModelRenderer heart1;
	public ModelRenderer artery5;
	public ModelRenderer heart2;
	public ModelRenderer artery4;
	public ModelRenderer artery6;
	public ModelRenderer artery7;
	public ModelRenderer artery8;
	public ModelRenderer spine5;
	public ModelRenderer spine6;
	public ModelRenderer spine7;
	public ModelRenderer spine8;
	public ModelRenderer spine9;
	public ModelRenderer tailbone1;
	public ModelRenderer sludge_front2;
	public ModelRenderer sludge_front3;
	public ModelRenderer sludgemid_2;
	public ModelRenderer sludge_mid3;
	public ModelRenderer sludge_back2;
	public ModelRenderer sludge_back3;

	public ModelLargeSludgeWorm() {
		this.textureWidth = 256;
		this.textureHeight = 128;
		this.spine5 = new ModelRenderer(this, 83, 97);
		this.spine5.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.spine5.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
		this.setRotateAngle(spine5, -0.091106186954104F, -0.091106186954104F, -0.045553093477052F);
		this.artery5 = new ModelRenderer(this, 69, 110);
		this.artery5.setRotationPoint(-0.5F, -1.0F, 2.0F);
		this.artery5.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(artery5, 1.2747884856566583F, 0.0F, 0.136659280431156F);
		this.lowerjaw_left = new ModelRenderer(this, 151, 110);
		this.lowerjaw_left.setRotationPoint(1.0F, 1.5F, -1.0F);
		this.lowerjaw_left.addBox(-1.0F, -1.5F, -11.0F, 4, 3, 12, 0.0F);
		this.setRotateAngle(lowerjaw_left, 0.5918411493512771F, -0.18203784098300857F, 0.091106186954104F);
		this.spine3 = new ModelRenderer(this, 63, 97);
		this.spine3.setRotationPoint(0.0F, -1.0F, 0.0F);
		this.spine3.addBox(-1.0F, 0.0F, -3.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(spine3, 0.091106186954104F, 0.0F, 0.0F);
		this.artery3 = new ModelRenderer(this, 37, 110);
		this.artery3.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.artery3.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(artery3, 0.36425021489121656F, 0.0F, 0.0F);
		this.sludge_front2 = new ModelRenderer(this, 77, 0);
		this.sludge_front2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.sludge_front2.addBox(-7.0F, -9.0F, -9.0F, 14, 2, 18, 0.0F);
		this.spine2 = new ModelRenderer(this, 46, 97);
		this.spine2.setRotationPoint(0.0F, 1.0F, 6.0F);
		this.spine2.addBox(-1.0F, -2.0F, 0.0F, 2, 2, 6, 0.0F);
		this.setRotateAngle(spine2, 0.091106186954104F, 0.0F, 0.0F);
		this.spine7 = new ModelRenderer(this, 97, 97);
		this.spine7.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.spine7.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
		this.setRotateAngle(spine7, 0.0F, -0.136659280431156F, -0.091106186954104F);
		this.endpiece_spine10 = new ModelRenderer(this, 118, 97);
		this.endpiece_spine10.setRotationPoint(0.0F, 16.0F, 30.0F);
		this.endpiece_spine10.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(endpiece_spine10, 0.0F, 0.0F, 0.045553093477052F);
		this.sludge_front1 = new ModelRenderer(this, 0, 0);
		this.sludge_front1.setRotationPoint(0.0F, 15.0F, -1.0F);
		this.sludge_front1.addBox(-9.0F, -7.0F, -11.0F, 18, 14, 20, 0.0F);
		this.sludge_back3 = new ModelRenderer(this, 126, 66);
		this.sludge_back3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.sludge_back3.addBox(-7.0F, 7.0F, 0.0F, 14, 2, 14, 0.0F);
		this.artery6 = new ModelRenderer(this, 76, 110);
		this.artery6.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.artery6.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(artery6, -0.4553564018453205F, 0.0F, 0.0F);
		this.spine6 = new ModelRenderer(this, 90, 97);
		this.spine6.setRotationPoint(0.0F, 0.0F, 4.0F);
		this.spine6.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
		this.setRotateAngle(spine6, 0.0F, 0.27314402793711257F, -0.31869712141416456F);
		this.sludgemid_2 = new ModelRenderer(this, 69, 35);
		this.sludgemid_2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.sludgemid_2.addBox(-7.0F, -9.0F, 0.0F, 14, 2, 16, 0.0F);
		this.head1 = new ModelRenderer(this, 99, 110);
		this.head1.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.head1.addBox(-4.0F, -5.0F, -8.0F, 8, 5, 8, 0.0F);
		this.setRotateAngle(head1, -0.045553093477052F, 0.0F, 0.0F);
		this.heart1 = new ModelRenderer(this, 44, 110);
		this.heart1.setRotationPoint(0.0F, -1.0F, 1.6F);
		this.heart1.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(heart1, -0.7285004297824331F, 0.0F, 0.36425021489121656F);
		this.artery7 = new ModelRenderer(this, 83, 110);
		this.artery7.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.artery7.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(artery7, -0.18203784098300857F, 0.0F, 0.0F);
		this.upperjaw = new ModelRenderer(this, 217, 110);
		this.upperjaw.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.upperjaw.addBox(-4.0F, 0.0F, -6.0F, 8, 1, 6, 0.0F);
		this.spine9 = new ModelRenderer(this, 111, 97);
		this.spine9.setRotationPoint(0.0F, 0.0F, 2.0F);
		this.spine9.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
		this.setRotateAngle(spine9, 0.045553093477052F, 0.0F, 0.045553093477052F);
		this.sludge_back2 = new ModelRenderer(this, 69, 66);
		this.sludge_back2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.sludge_back2.addBox(-7.0F, -9.0F, 0.0F, 14, 2, 14, 0.0F);
		this.artery2 = new ModelRenderer(this, 30, 110);
		this.artery2.setRotationPoint(0.0F, 1.0F, 2.0F);
		this.artery2.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(artery2, 0.18203784098300857F, 0.0F, 0.0F);
		this.sludge_front3 = new ModelRenderer(this, 142, 0);
		this.sludge_front3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.sludge_front3.addBox(-7.0F, 7.0F, -9.0F, 14, 2, 18, 0.0F);
		this.spine1 = new ModelRenderer(this, 29, 97);
		this.spine1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spine1.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 6, 0.0F);
		this.body_base = new ModelRenderer(this, 0, 97);
		this.body_base.setRotationPoint(0.0F, 14.0F, -3.5F);
		this.body_base.addBox(-4.0F, 0.0F, 0.0F, 8, 6, 6, 0.0F);
		this.setRotateAngle(body_base, -0.18203784098300857F, 0.0F, 0.0F);
		this.tailbone1 = new ModelRenderer(this, 129, 97);
		this.tailbone1.setRotationPoint(0.0F, -1.0F, 3.0F);
		this.tailbone1.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 3, 0.0F);
		this.setRotateAngle(tailbone1, -0.5918411493512771F, 0.0F, 0.0F);
		this.sludge_back1 = new ModelRenderer(this, 0, 66);
		this.sludge_back1.setRotationPoint(0.0F, 15.0F, 24.0F);
		this.sludge_back1.addBox(-9.0F, -7.0F, 0.0F, 18, 14, 16, 0.0F);
		this.artery8 = new ModelRenderer(this, 90, 110);
		this.artery8.setRotationPoint(0.0F, 1.0F, 2.0F);
		this.artery8.addBox(-0.5F, -1.0F, 0.0F, 1, 1, 3, 0.0F);
		this.setRotateAngle(artery8, 0.40980330836826856F, 0.0F, 0.0F);
		this.artery1 = new ModelRenderer(this, 23, 110);
		this.artery1.setRotationPoint(0.0F, 0.0F, -2.0F);
		this.artery1.addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.setRotateAngle(artery1, -1.0016444577195458F, 0.0F, 0.0F);
		this.spine8 = new ModelRenderer(this, 104, 97);
		this.spine8.setRotationPoint(0.0F, 0.0F, 2.1F);
		this.spine8.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1, 0.0F);
		this.setRotateAngle(spine8, 0.091106186954104F, -0.136659280431156F, 0.18203784098300857F);
		this.midpiece_spine4 = new ModelRenderer(this, 74, 97);
		this.midpiece_spine4.setRotationPoint(0.0F, 16.0F, 11.0F);
		this.midpiece_spine4.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F);
		this.setRotateAngle(midpiece_spine4, 0.0F, 0.0F, 0.27314402793711257F);
		this.heart2 = new ModelRenderer(this, 55, 110);
		this.heart2.setRotationPoint(-1.0F, 0.0F, 0.0F);
		this.heart2.addBox(-1.0F, -1.0F, 0.0F, 1, 2, 2, 0.0F);
		this.setRotateAngle(heart2, 0.0F, 0.136659280431156F, 0.0F);
		this.head2 = new ModelRenderer(this, 132, 110);
		this.head2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.head2.addBox(-3.0F, 0.0F, -2.0F, 6, 3, 2, 0.0F);
		this.lowerjaw_right = new ModelRenderer(this, 184, 110);
		this.lowerjaw_right.setRotationPoint(-1.0F, 1.5F, -1.0F);
		this.lowerjaw_right.addBox(-3.0F, -1.5F, -11.0F, 4, 3, 12, 0.0F);
		this.setRotateAngle(lowerjaw_right, 0.5918411493512771F, 0.18203784098300857F, -0.091106186954104F);
		this.sludge_mid1 = new ModelRenderer(this, 0, 35);
		this.sludge_mid1.setRotationPoint(0.0F, 15.0F, 8.0F);
		this.sludge_mid1.addBox(-9.0F, -7.0F, 0.0F, 18, 14, 16, 0.0F);
		this.ribs1 = new ModelRenderer(this, 0, 110);
		this.ribs1.setRotationPoint(0.0F, 0.0F, 6.0F);
		this.ribs1.addBox(-3.0F, 0.0F, 0.0F, 6, 5, 5, 0.0F);
		this.setRotateAngle(ribs1, 0.091106186954104F, 0.0F, 0.0F);
		this.artery4 = new ModelRenderer(this, 62, 110);
		this.artery4.setRotationPoint(1.0F, 0.0F, 0.5F);
		this.artery4.addBox(-0.5F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		this.setRotateAngle(artery4, 0.0F, 0.31869712141416456F, 0.40980330836826856F);
		this.sludge_mid3 = new ModelRenderer(this, 130, 35);
		this.sludge_mid3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.sludge_mid3.addBox(-7.0F, 7.0F, 0.0F, 14, 2, 16, 0.0F);
		//this.midpiece_spine4.addChild(this.spine5);
		this.artery3.addChild(this.artery5);
		this.head2.addChild(this.lowerjaw_left);
		this.spine1.addChild(this.spine3);
		this.artery2.addChild(this.artery3);
		this.sludge_front1.addChild(this.sludge_front2);
		this.spine1.addChild(this.spine2);
		//this.spine6.addChild(this.spine7);
		this.sludge_back1.addChild(this.sludge_back3);
		this.artery5.addChild(this.artery6);
		//this.spine5.addChild(this.spine6);
		this.sludge_mid1.addChild(this.sludgemid_2);
		this.spine3.addChild(this.head1);
		this.artery3.addChild(this.heart1);
		this.artery6.addChild(this.artery7);
		this.head1.addChild(this.upperjaw);
		//this.spine8.addChild(this.spine9);
		this.sludge_back1.addChild(this.sludge_back2);
		this.artery1.addChild(this.artery2);
		this.sludge_front1.addChild(this.sludge_front3);
		this.body_base.addChild(this.spine1);
		this.endpiece_spine10.addChild(this.tailbone1);
		this.artery7.addChild(this.artery8);
		this.spine1.addChild(this.artery1);
		//this.spine7.addChild(this.spine8);
		this.heart1.addChild(this.heart2);
		this.head1.addChild(this.head2);
		this.head2.addChild(this.lowerjaw_right);
		this.body_base.addChild(this.ribs1);
		this.heart1.addChild(this.artery4);
		this.sludge_mid1.addChild(this.sludge_mid3);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.endpiece_spine10.render(f5);
		this.sludge_front1.render(f5);
		this.body_base.render(f5);
		this.sludge_back1.render(f5);
		this.midpiece_spine4.render(f5);
		this.sludge_mid1.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void renderHead(EntityLargeSludgeWorm worm, int frame, float wibbleStrength, float partialTicks, boolean renderSolids) {
		float smoothedTicks = worm.ticksExisted + frame + (worm.ticksExisted + frame - (worm.ticksExisted + frame - 1)) * partialTicks;

		float jawWibbleLeft = MathHelper.sin(1F + (smoothedTicks) * 0.5F) * 0.5F;
		float jawWibbleRight = MathHelper.sin(1F + (smoothedTicks) * 0.5F + 0.1F) * 0.5F;

		this.lowerjaw_left.rotateAngleX = 0.5918411493512771F - 0.2F + jawWibbleLeft * 0.8F;
		this.lowerjaw_left.rotateAngleY = -0.18203784098300857F - jawWibbleRight * 0.2F;

		this.lowerjaw_right.rotateAngleX = 0.5918411493512771F - 0.2F + jawWibbleRight * 0.8F;
		this.lowerjaw_right.rotateAngleY = 0.18203784098300857F + jawWibbleRight * 0.2F;

		this.head1.rotateAngleX = -0.045553093477052F - jawWibbleLeft * 0.1F;

		if(renderSolids) {
			this.body_base.render(0.0625F);
		} else {
			this.sludge_front1.render(0.0625F);
		}
	}

	public void renderSpinePiece(int piece, float boneYaw) {
		GlStateManager.pushMatrix();

		GlStateManager.rotate(boneYaw, 0, 1, 0);

		switch(piece) {
		default:
		case 0:
			//GlStateManager.translate(0.35D, -1D, 0.48D);
			//this.midpiece_spine4.render(0.0625F);
			break;
		case 1:
			this.spine5.render(0.0625F);
			break;
		case 2:
			this.spine6.render(0.0625F);
			break;
		case 3:
			this.spine7.render(0.0625F);
			break;
		case 4:
			this.spine8.render(0.0625F);
			break;
		case 5:
			this.spine9.render(0.0625F);
			break;
		}
		GlStateManager.popMatrix();
	}

	public void renderTail(EntityLargeSludgeWorm worm, int frame, float wibbleStrength, float partialTicks, boolean renderSolids) {
		if(renderSolids) {
			this.endpiece_spine10.render(0.0625F);
		} else {
			this.sludge_back1.render(0.0625F);
		}
	}
}
