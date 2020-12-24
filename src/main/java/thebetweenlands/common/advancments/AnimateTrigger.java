package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class AnimateTrigger extends BLTrigger<AnimateTrigger.Instance, AnimateTrigger.Listener> {

	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "animate");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public AnimateTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
		return new AnimateTrigger.Listener(playerAdvancements);
	}

	@Override
	public AnimateTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		ItemPredicate[] inputs = ItemPredicate.deserializeArray(json.get("inputs"));
		ItemPredicate[] outputs = ItemPredicate.deserializeArray(json.get("outputs"));
		return new AnimateTrigger.Instance(inputs, outputs);
	}

	public void trigger(ItemStack input, ItemStack output, EntityPlayerMP player) {
		AnimateTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

		if (listeners != null) {
			listeners.trigger(input, output);
		}
	}

	public static class Instance extends AbstractCriterionInstance {
		private final ItemPredicate[] inputs;
		private final ItemPredicate[] outputs;

		public Instance(ItemPredicate[] inputs, ItemPredicate[] outputs) {
			super(AnimateTrigger.ID);
			this.inputs = inputs;
			this.outputs = outputs;
		}

		public boolean test(ItemStack input, ItemStack output) {
			List<ItemPredicate> itemList = Lists.newArrayList(this.inputs);
			int itemAmount = itemList.size();
			itemList.removeIf(predicate -> predicate.test(input));

			List<ItemPredicate> outputList = Lists.newArrayList(this.outputs);
			int outputAmount = outputList.size();
			outputList.removeIf(predicate -> predicate.test(output));

			return (itemAmount == 0 || itemAmount > itemList.size()) && (outputAmount == 0 || outputAmount > outputList.size());
		}
	}

	static class Listener extends BLTrigger.Listener<AnimateTrigger.Instance> {
		public Listener(PlayerAdvancements playerAdvancementsIn) {
			super(playerAdvancementsIn);
		}

		public void trigger(ItemStack inpout, ItemStack output) {
			List<ICriterionTrigger.Listener<AnimateTrigger.Instance>> list = new ArrayList<>();

			for (ICriterionTrigger.Listener<AnimateTrigger.Instance> listener : this.listeners) {
				if (listener.getCriterionInstance().test(inpout, output)) {
					list.add(listener);
					break;
				}
			}

			for (ICriterionTrigger.Listener<AnimateTrigger.Instance> listener : list) {
				listener.grantCriterion(this.playerAdvancements);
			}
		}
	}
}
