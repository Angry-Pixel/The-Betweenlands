package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import thebetweenlands.client.render.entity.RenderPlayerRower;
import thebetweenlands.client.render.model.entity.rowboat.ModelBipedRower;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.Matrix;

import java.util.*;

public class LayerRowerArmor extends LayerBipedArmor {
	private final RenderPlayerRower rower;

	private final ModelBiped initialPose = new ModelBipedRower(0.0F);

	private final GlintDelegate glint = new GlintDelegate();

	private Map<ModelBiped, ArmorRenderer> cache = new HashMap<>();

	private Set<Item> naughtyList = new HashSet<>();

	public LayerRowerArmor(RenderPlayerRower renderer) {
		super(renderer);
		this.rower = renderer;
	}

	@Override
	protected void initArmor() {
		modelLeggings = new ModelBipedRower(0.5F);
		modelArmor = new ModelBipedRower(1);
	}

	public ModelBipedRower getLeggings() {
		return (ModelBipedRower) modelLeggings;
	}

	public ModelBipedRower getChest() {
		return (ModelBipedRower) modelArmor;
	}

	@Override
	public void doRenderLayer(EntityLivingBase living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		renderArmorLayer(living, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
		renderArmorLayer(living, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
		renderArmorLayer(living, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
		renderArmorLayer(living, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
	}

	private void renderArmorLayer(EntityLivingBase living, float limbSwing, float limbSwingAmount, float delta, float age, float headYaw, float headPitch, float scale, EntityEquipmentSlot slot) {
		ItemStack stack = living.getItemStackFromSlot(slot);
		Item item = stack.getItem();
		if (item instanceof ItemArmor) {
			ItemArmor itemarmor = (ItemArmor) item;
			if (itemarmor.getEquipmentSlot() == slot) {
				ArmorRenderer armor = getModel(living, stack, slot, getModelFromSlot(slot));
				armor.setup(living, limbSwing, limbSwingAmount, delta, slot);
				//noinspection ConstantConditions
				rower.bindTexture(getArmorResource(living, stack, slot, null));
				if (itemarmor.hasOverlay(stack)) {
					int rgb = itemarmor.getColor(stack);
					float r = (float) (rgb >> 16 & 255) / 255.0F;
					float g = (float) (rgb >> 8 & 255) / 255.0F;
					float b = (float) (rgb & 255) / 255.0F;
					GlStateManager.color(r, g, b, 1.0F);
					armor.render(living, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale, slot);
					rower.bindTexture(getArmorResource(living, stack, slot, "overlay"));
				}
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				armor.render(living, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale, slot);
				if (stack.hasEffect()) {
					glint.render(living, armor, limbSwing, limbSwingAmount, delta, age, headYaw, headPitch, scale, slot);
				}
			}
		}
	}

	private ArmorRenderer getModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped model) {
		ModelBiped result = getArmorModelHook(entity, stack, slot, model);
		if (result == model) {
			return cache.computeIfAbsent(model, BuiltInArmorRenderer::new);
		}
		if (result == getArmorModelHook(entity, stack, slot, model)) {
			// *pat* *pat* good boy
			return cache.computeIfAbsent(result, this::createCustomArmorModel);
		}
		if (this.naughtyList.add(stack.getItem())) {
			ResourceLocation reg = stack.getItem().getRegistryName();
			String name = "???";
			String owner = "???";
			if (reg != null) {
				name = reg.toString();
				ModContainer mod = Loader.instance().getIndexedModList().get(reg.getNamespace());
				if (mod != null) {
					owner = mod.getName();
				}
			}
			TheBetweenlands.logger.warn(
				"Expect decreased rendering performance of item \"{}\" registered as \"{}\" with class \"{}\" as it appears to " +
				"not cache its custom model, leaking video memory. Report to mod \"{}\"!",
				stack.getDisplayName(),
				name,
				stack.getItem().getClass().getName(),
				owner
			);
		}
		return createCustomArmorModel(result);
	}

	private CustomArmorRenderer createCustomArmorModel(ModelBiped model) {
		return new CustomArmorRenderer(model, getForearmModels(model.bipedLeftArm), getForearmModels(model.bipedRightArm));
	}

	private List<ModelRenderer> getForearmModels(ModelRenderer model) {
		if (model.childModels == null) {
			return Collections.emptyList();
		}
		List<ModelRenderer> lower = new ArrayList<>();
		double armHalfLen = 6.0D;
		Matrix m = new Matrix();
		for (ModelRenderer child : model.childModels) {
			m.setIdentity();
			m.translate(child.rotationPointX, child.rotationPointY, child.rotationPointZ);
			m.rotate(child.rotateAngleZ, 0.0D, 0.0D, 1.0D);
			m.rotate(child.rotateAngleY, 0.0D, 1.0D, 0.0D);
			m.rotate(child.rotateAngleX, 1.0D, 0.0D, 0.0D);
			double half = 0.0D;
			for (ModelBox box : child.cubeList) {
				float lenX = box.posX2 - box.posX1;
				float lenY = box.posY2 - box.posY1;
				float lenZ = box.posZ2 - box.posZ1;
				float px = 0.5F * lenX;
				float py = 0.5F * lenY;
				float pz = 0.5F * lenZ;
				if (lenX > lenY && lenX > lenZ) {
					px = 0.0F;
				} else if (lenY > lenX && lenY > lenZ) {
					py = 0.0F;
				} else {
					pz = 0.0F;
				}
				half += m.transform(new Vec3d(box.posX1 + px, box.posY1 + py, box.posZ1 + pz)).y - armHalfLen;
				half += m.transform(new Vec3d(box.posX2 - px, box.posY2 - py, box.posZ2 - pz)).y - armHalfLen;
			}
			if (half > 0.0D) {
				lower.add(child);
			}
		}
		return lower;
	}

	static abstract class ArmorRenderer {
		abstract void setup(EntityLivingBase living, float limbSwing, float limbSwingAmount, float delta, EntityEquipmentSlot slot);

		abstract void render(EntityLivingBase living, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale, EntityEquipmentSlot slot);
	}

	abstract class ModelArmorRenderer extends ArmorRenderer {
		final ModelBiped model;

		ModelArmorRenderer(ModelBiped model) {
			this.model = model;
		}

		@Override
		void setup(EntityLivingBase living, float limbSwing, float limbSwingAmount, float delta, EntityEquipmentSlot slot) {
			model.setModelAttributes(rower.getMainModel());
			model.setLivingAnimations(living, limbSwing, limbSwingAmount, delta);
			setModelSlotVisible(model, slot);
		}
	}

	class BuiltInArmorRenderer extends ModelArmorRenderer {
		BuiltInArmorRenderer(ModelBiped model) {
			super(model);
		}

		@Override
		void render(EntityLivingBase living, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale, EntityEquipmentSlot slot) {
			this.model.render(living, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
		}
	}

	class CustomArmorRenderer extends ModelArmorRenderer {
		final List<ModelRenderer> leftForearmParts;
		final List<ModelRenderer> rightForearmParts;
		boolean init = true;

		CustomArmorRenderer(ModelBiped model, List<ModelRenderer> leftForearmParts, List<ModelRenderer> rightForearmParts) {
			super(model);
			this.leftForearmParts = leftForearmParts;
			this.rightForearmParts = rightForearmParts;
		}

		@Override
		void render(EntityLivingBase living, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale, EntityEquipmentSlot slot) {
			// Construct's Armory does initialization of model in render
			if (init) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0D, -10000.0D, 0.0D);
				GlStateManager.scale(0.0D, 0.0D, 0.0D);
				model.render(living, limbSwing, limbSwing, age, headYaw, headPitch, scale);
				GlStateManager.popMatrix();
				init = false;
			}
			model.setRotationAngles(0.0F, 0.0F, age, 0.0F, 0.0F, scale, living);
			GlStateManager.pushMatrix();
			switch (slot) {
				case HEAD:
					transform(modelArmor.bipedBody, scale);
					renderHead(modelArmor.bipedBody, modelArmor.bipedHead, model.bipedHead, scale);
					break;
				case CHEST:
					render(modelArmor.bipedBody, model.bipedBody, scale);
					transform(modelArmor.bipedBody, scale);
					renderArm(modelArmor.bipedBody, modelArmor.bipedLeftArm, initialPose.bipedLeftArm, getChest().leftForearm, model.bipedLeftArm, scale);
					renderArm(modelArmor.bipedBody, modelArmor.bipedRightArm, initialPose.bipedRightArm, getChest().rightForearm, model.bipedRightArm, scale);
					break;
				case LEGS:
					render(modelLeggings.bipedBody, model.bipedBody, scale);
				case FEET:
					render(modelArmor.bipedLeftLeg, model.bipedLeftLeg, scale);
					render(modelArmor.bipedRightLeg, model.bipedRightLeg, scale);
					break;
			}
			GlStateManager.popMatrix();
		}

		private void renderArm(ModelRenderer body, ModelRenderer upperArm, ModelRenderer initialArm, ModelRenderer lowerArm, ModelRenderer model, float scale) {
			GlStateManager.pushMatrix();
			transform(upperArm, scale);
			GlStateManager.pushMatrix();
			GlStateManager.translate(
				(-initialArm.rotationPointX) * scale,
				(-initialArm.rotationPointY - body.rotationPointY) * scale,
				(-initialArm.rotationPointZ) * scale
			);
			List<ModelRenderer> forearmModels = getForearmModels(model);
			renderExcluding(model, forearmModels, scale);
			GlStateManager.popMatrix();
			transform(lowerArm, scale);
			GlStateManager.translate((model.rotationPointX - initialArm.rotationPointX) * scale, -lowerArm.rotationPointY * scale, 0.0F);
			for (ModelRenderer m : forearmModels) {
				m.render(scale);
			}
			GlStateManager.popMatrix();
		}

		private void renderExcluding(ModelRenderer model, List<ModelRenderer> exclusions, float scale) {
			if (model.childModels != null) {
				model.childModels.removeAll(exclusions);
			}
			render(model, scale);
			if (model.childModels != null) {
				model.childModels.addAll(exclusions);
			}
		}

		private void renderHead(ModelRenderer body, ModelRenderer pose, ModelRenderer model, float scale) {
			GlStateManager.pushMatrix();
			transform(pose, scale);
			GlStateManager.translate(
				(-pose.rotationPointX) * scale,
				(-pose.rotationPointY - body.rotationPointY) * scale,
				(-pose.rotationPointZ) * scale
			);
			render(model, scale);
			GlStateManager.popMatrix();
		}

		private void render(ModelRenderer pose, ModelRenderer model, float scale) {
			GlStateManager.pushMatrix();
			transform(pose, scale);
			GlStateManager.translate(
				(-pose.rotationPointX) * scale,
				(-pose.rotationPointY) * scale,
				(-pose.rotationPointZ) * scale
			);
			render(model, scale);
			GlStateManager.popMatrix();
		}

		private void render(ModelRenderer model, float scale) {
			float rx = model.rotateAngleX;
			float ry = model.rotateAngleY;
			float rz = model.rotateAngleZ;
			model.rotateAngleX = 0.0F;
			model.rotateAngleY = 0.0F;
			model.rotateAngleZ = 0.0F;
			model.render(scale);
			model.rotateAngleX = rx;
			model.rotateAngleY = ry;
			model.rotateAngleZ = rz;
		}

		private void transform(ModelRenderer model, float scale) {
			boolean show = model.showModel;
			model.showModel = true;
			model.postRender(scale);
			model.showModel = show;
		}
	}

	class GlintDelegate extends ModelBase {
		ArmorRenderer model;
		EntityEquipmentSlot slot;

		@Override
		public void render(Entity living, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale) {
			model.render((EntityLivingBase) living, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale, slot);
		}

		void render(EntityLivingBase living, ArmorRenderer model, float limbSwing, float limbSwingAmount, float delta, float age, float headYaw, float headPitch, float scale, EntityEquipmentSlot slot) {
			this.model = model;
			this.slot = slot;

			renderEnchantedGlint(rower, living, this, limbSwing, limbSwingAmount, delta, age, headYaw, headPitch, scale);
		}
	}
}
