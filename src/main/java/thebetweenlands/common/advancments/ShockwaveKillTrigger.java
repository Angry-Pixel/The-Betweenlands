package thebetweenlands.common.advancments;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class ShockwaveKillTrigger extends BLTrigger<ShockwaveKillTrigger.Instance, ShockwaveKillTrigger.Listener> {

    public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "shockwave_kill");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Listener createListener(PlayerAdvancements playerAdvancements) {
        return new ShockwaveKillTrigger.Listener(playerAdvancements);
    }

    @Override
    public ShockwaveKillTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        EntityPredicate entityPredicates = EntityPredicate.deserialize(json.get("entity"));
        return new ShockwaveKillTrigger.Instance(entityPredicates);
    }

    public void trigger(EntityPlayerMP player, EntityLivingBase entity) {
        Listener listener = this.listeners.get(player.getAdvancements());

        if (listener != null) {
            listener.trigger(player, entity);
        }
    }

    public static class Instance extends AbstractCriterionInstance {

        private final EntityPredicate entity;

        public Instance(EntityPredicate entity) {
            super(ShockwaveKillTrigger.ID);
            this.entity = entity;
        }

        public boolean test(EntityPlayerMP player, EntityLivingBase entity) {
            return this.entity.test(player, entity);
        }
    }

    static class Listener extends BLTrigger.Listener<ShockwaveKillTrigger.Instance> {

        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(EntityPlayerMP player, EntityLivingBase entity) {
            List<ICriterionTrigger.Listener<ShockwaveKillTrigger.Instance>> list = new ArrayList<>();

            for (ICriterionTrigger.Listener<ShockwaveKillTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(player, entity)) {
                    list.add(listener);
                    break;
                }
            }

            for (ICriterionTrigger.Listener<ShockwaveKillTrigger.Instance> listener : list) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}
