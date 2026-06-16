package thebetweenlands.client.model.baked.dungeonwallcandle;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class DungeonWallCandleModelLoader implements IGeometryLoader<UnbakedDungeonWallCandleModel> {
	public static final DungeonWallCandleModelLoader INSTANCE = new DungeonWallCandleModelLoader();

	@Override
	public UnbakedDungeonWallCandleModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedDungeonWallCandleModel();
	}
}

