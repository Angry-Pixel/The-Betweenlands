package thebetweenlands.common.entity.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;
import thebetweenlands.common.lib.ModInfo;

public class LootPropertyIsBossPeatMummy implements EntityProperty {
	private final boolean bossMummy;

	public LootPropertyIsBossPeatMummy(boolean bossMummy) {
		this.bossMummy = bossMummy;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityPeatMummy && ((EntityPeatMummy)entity).isBossMummy() == this.bossMummy;
	}

	public static class Serializer extends EntityProperty.Serializer<LootPropertyIsBossPeatMummy> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "is_boss_mummy"), LootPropertyIsBossPeatMummy.class);
		}

		@Override
		public JsonElement serialize(LootPropertyIsBossPeatMummy property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.bossMummy);
		}

		@Override
		public LootPropertyIsBossPeatMummy deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new LootPropertyIsBossPeatMummy(JsonUtils.getBoolean(element, this.getName().getResourcePath()));
		}
	}
}