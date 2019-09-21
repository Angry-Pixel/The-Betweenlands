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
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;
import thebetweenlands.common.lib.ModInfo;

public class EntityPropertySludgeWormSquashed implements EntityProperty {
	private final boolean isSquashed;

	public EntityPropertySludgeWormSquashed(boolean isSquashed) {
		this.isSquashed = isSquashed;
	}

	@Override
	public boolean testProperty(Random random, Entity entity) {
		return entity instanceof EntityTinySludgeWorm && ((EntityTinySludgeWorm)entity).isSquashed() == this.isSquashed;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPropertySludgeWormSquashed> {
		public Serializer() {
			super(new ResourceLocation(ModInfo.ID, "sludge_worm_squashed"), EntityPropertySludgeWormSquashed.class);
		}

		@Override
		public JsonElement serialize(EntityPropertySludgeWormSquashed property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.isSquashed);
		}

		@Override
		public EntityPropertySludgeWormSquashed deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPropertySludgeWormSquashed(JsonUtils.getBoolean(element, this.getName().getPath()));
		}
	}
}