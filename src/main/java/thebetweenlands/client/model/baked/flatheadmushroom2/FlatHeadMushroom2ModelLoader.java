package thebetweenlands.client.model.baked.flatheadmushroom2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FlatHeadMushroom2ModelLoader implements IGeometryLoader<UnbakedFlatHeadMushroom2Model> {
	public static final FlatHeadMushroom2ModelLoader INSTANCE = new FlatHeadMushroom2ModelLoader();

	@Override
	public UnbakedFlatHeadMushroom2Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFlatHeadMushroom2Model();
	}
}

