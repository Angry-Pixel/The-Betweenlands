package thebetweenlands.client.render.entity.layer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.client.render.model.armor.ModelRendererItemAttachment;

@SideOnly(Side.CLIENT)
public class LayerAttachedItems<T extends EntityLivingBase> implements LayerRenderer<T> {
	protected final ModelBase model;

	private final Multimap<ModelRenderer, ModelRendererItemAttachment<T>> attachments = ArrayListMultimap.create();

	public LayerAttachedItems(ModelBase model) {
		this.model = model;
	}

	public LayerAttachedItems<T> attach(ModelRenderer part, Function<T, ItemStack> stack, EnumHandSide side, float scale, Consumer<ModelRenderer> transformSetter) {
		ModelRendererItemAttachment<T> attachment = new ModelRendererItemAttachment<T>(this.model, stack, side, scale);
		transformSetter.accept(attachment);
		this.attachments.put(this.findBasePart(part, new HashSet<>()), attachment);
		part.addChild(attachment);
		return this;
	}

	public LayerAttachedItems<T> attach(ModelRenderer part, Function<T, ItemStack> stack, EnumHandSide side, float scale) {
		return this.attach(part, stack, side, scale, renderer -> {});
	}

	private ModelRenderer findBasePart(ModelRenderer part, Set<ModelRenderer> checked) {
		checked.add(part);
		for(ModelRenderer modelPart : this.model.boxList) {
			if(!checked.contains(modelPart) && modelPart.childModels != null) {
				for(ModelRenderer childPart : modelPart.childModels) {
					if(childPart == part) {
						return this.findBasePart(modelPart, checked);
					}
				}
			}
		}
		return part;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		@SuppressWarnings("unchecked")
		T entity = (T) entityIn;

		this.model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		this.model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		for(ModelRenderer basePart : this.attachments.keys()) {
			Collection<ModelRendererItemAttachment<T>> itemAttachments = this.attachments.get(basePart);

			for(ModelRendererItemAttachment<T> attachment : itemAttachments) {
				attachment.setEntity(entity);
				attachment.isHidden = false;
			}

			Set<ModelRenderer> toRender = new HashSet<>();
			toRender.addAll(itemAttachments);

			ModelBodyAttachment.renderSpecific(basePart, scale, toRender);

			for(ModelRendererItemAttachment<T> attachment : itemAttachments) {
				attachment.isHidden = true;
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
