package thebetweenlands.common.loot;

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

public class EntityPropertyIsBossPeatMummy implements EntityProperty {
	private final boolean bossMummy;

	public EntityPropertyIsBossPeatMummy(boolean bossMummy) {
		this.bossMummy = bossMummy;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityPeatMummy && ((EntityPeatMummy)entity).isBossMummy() == this.bossMummy;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertyIsBossPeatMummy> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "is_boss_mummy"), EntityPropertyIsBossPeatMummy.class);
		}

		@Override
		public JsonElement serialize(EntityPropertyIsBossPeatMummy property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.bossMummy);
		}

		@Override
		public EntityPropertyIsBossPeatMummy deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertyIsBossPeatMummy(JsonUtils.getBoolean(element, this.getName().getResourcePath()));
		}
	}
}