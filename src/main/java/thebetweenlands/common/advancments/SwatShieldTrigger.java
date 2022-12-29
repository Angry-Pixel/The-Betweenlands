package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import thebetweenlands.common.lib.ModInfo;

public class SwatShieldTrigger extends BLTrigger<SwatShieldTrigger.Instance, SwatShieldTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "swat_shield");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public SwatShieldTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new SwatShieldTrigger.Listener(playerAdvancements);
    }

    @Override
    public SwatShieldTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        EntityPredicate entityPredicates = EntityPredicate.deserialize(json.get("entity"));
        return new SwatShieldTrigger.Instance(entityPredicates);
    }

    public void trigger(EntityPlayerMP player, EntityLivingBase entity) {
        SwatShieldTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger(player, entity);
        }
    }

    public void revert(EntityPlayerMP player) {
        SwatShieldTrigger.Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.revert();
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final EntityPredicate entity;

        public Instance(EntityPredicate entity) {
            super(SwatShieldTrigger.ID);
            this.entity = entity;
        }

        public boolean test(EntityPlayerMP player, EntityLivingBase entity) {
            return this.entity.test(player, entity);
        }
    }

    static class Listener extends BLTrigger.Listener<SwatShieldTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(EntityPlayerMP player, EntityLivingBase entity) {
            List<ICriterionTrigger.Listener<SwatShieldTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<SwatShieldTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(player, entity)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<SwatShieldTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }

        public void revert() {
        	List<Tuple<Advancement, String>> criterions = new ArrayList<>();
            for (ICriterionTrigger.Listener<SwatShieldTrigger.Instance> listener : this.listeners) {
                AdvancementProgress progress = playerAdvancements.getProgress(listener.advancement);
                if (!progress.isDone() && progress.hasProgress()) {
                    for (Map.Entry<String, Criterion> entry: listener.advancement.getCriteria().entrySet()) {
                        if (entry.getValue().getCriterionInstance() instanceof SwatShieldTrigger.Instance) {
                            CriterionProgress criterionProgress = progress.getCriterionProgress(entry.getKey());
                            if (criterionProgress != null && criterionProgress.isObtained()) {
                            	criterions.add(new Tuple<>(listener.advancement, entry.getKey()));
                            }
                        }
                    }
                }
            }
            for(Tuple<Advancement, String> criterion : criterions) {
            	this.playerAdvancements.revokeCriterion(criterion.getFirst(), criterion.getSecond());
            }
        }
    }
}
