package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class HasAdvancementTrigger extends BLTrigger<HasAdvancementTrigger.Instance, HasAdvancementTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "has_advancement");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public HasAdvancementTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new HasAdvancementTrigger.Listener(playerAdvancements);
    }

    @Override
    public HasAdvancementTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        ResourceLocation advancementId = new ResourceLocation(JsonUtils.getString(json, "advancement"));
        return new HasAdvancementTrigger.Instance(advancementId);
    }

    public void trigger(EntityPlayerMP player) {
        HasAdvancementTrigger.Listener listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(player);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final ResourceLocation advancementLocation;

        Instance(ResourceLocation advancementLocation) {
            super(HasAdvancementTrigger.ID);
            this.advancementLocation = advancementLocation;
        }

        boolean test(EntityPlayerMP player) {
            if (player != null) {
                Advancement adv = player.getServerWorld().getAdvancementManager().getAdvancement(advancementLocation);
                return adv != null && player.getAdvancements().getProgress(adv).isDone();
            }
            return false;
        }
    }

    static class Listener extends BLTrigger.Listener<HasAdvancementTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(EntityPlayerMP player) {
            List<ICriterionTrigger.Listener<HasAdvancementTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<HasAdvancementTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(player)) {
                    list.add(listener);
                }
            }

            for (ICriterionTrigger.Listener<HasAdvancementTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
