package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

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

public class EquipTrigger extends BLTrigger<EquipTrigger.Instance, EquipTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "equip_item");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public EquipTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new EquipTrigger.Listener(playerAdvancements);
    }

    @Override
    public EquipTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        ItemPredicate itemPredicates = ItemPredicate.deserialize(json.get("item"));
        return new EquipTrigger.Instance(itemPredicates);
    }

    public void trigger(EntityPlayerMP player, ItemStack stack) {
        EquipTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger(stack);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final ItemPredicate item;

        public Instance(ItemPredicate item) {
            super(EquipTrigger.ID);
            this.item = item;
        }

        public boolean test(ItemStack stack) {
            return this.item.test(stack);
        }
    }

    static class Listener extends BLTrigger.Listener<EquipTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(ItemStack stack) {
            List<ICriterionTrigger.Listener<EquipTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<EquipTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(stack)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<EquipTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
