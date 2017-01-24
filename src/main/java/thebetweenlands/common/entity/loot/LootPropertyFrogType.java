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
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.lib.ModInfo;

public class LootPropertyFrogType implements EntityProperty {
	private final int type;

	public LootPropertyFrogType(int type) {
		this.type = type;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityFrog && ((EntityFrog)entity).getSkin() == this.type;
	}

	public static class Serializer extends EntityProperty.Serializer<LootPropertyFrogType> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "frog_type"), LootPropertyFrogType.class);
		}

		@Override
		public JsonElement serialize(LootPropertyFrogType property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.type);
		}

		@Override
		public LootPropertyFrogType deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new LootPropertyFrogType(JsonUtils.getInt(element, this.getName().getResourcePath()));
		}
	}
}