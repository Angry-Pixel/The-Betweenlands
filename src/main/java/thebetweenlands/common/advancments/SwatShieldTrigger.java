package thebetweenlands.common.advancments;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwatShieldTrigger implements ICriterionTrigger<SwatShieldTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "swat_shield");

    private final Map<PlayerAdvancements, SwatShieldTrigger.Listeners> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        SwatShieldTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancements, Listeners::new);

        listeners.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancements, Listener<Instance> listener) {
        SwatShieldTrigger.Listeners listeners = this.listeners.get(playerAdvancements);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancements);
            }
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        EntityPredicate entityPredicates = EntityPredicate.deserialize(json.get("entity"));
        return new SwatShieldTrigger.Instance(entityPredicates);
    }

    public void trigger(EntityPlayerMP player, EntityLivingBase entity) {
        SwatShieldTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(player, entity);
        }
    }

    public void revert(EntityPlayerMP player) {
        SwatShieldTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.revert();
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

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<SwatShieldTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(Listener<SwatShieldTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<SwatShieldTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(EntityPlayerMP player, EntityLivingBase entity) {
            List<Listener<SwatShieldTrigger.Instance>> list = new ArrayList<>();

            for (Listener<SwatShieldTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(player, entity)) {
                    list.add(listener);
                    break;
                }
            }

            for (Listener<SwatShieldTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }

        public void revert() {
            for (Listener<SwatShieldTrigger.Instance> listener : this.listeners) {
                AdvancementProgress progress = playerAdvancements.getProgress(listener.advancement);
                if (!progress.isDone() && progress.hasProgress()) {
                    for (Map.Entry<String, Criterion> entry: listener.advancement.getCriteria().entrySet()) {
                        if (entry.getValue().getCriterionInstance() instanceof SwatShieldTrigger.Instance) {
                            CriterionProgress criterionProgress = progress.getCriterionProgress(entry.getKey());
                            if (criterionProgress != null && criterionProgress.isObtained())
                                playerAdvancements.revokeCriterion(listener.advancement, entry.getKey());
                        }
                    }
                }
            }
        }
    }
}
