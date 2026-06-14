package thebetweenlands.client.model.baked.whitepearcrop4;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop4ModelLoader implements IGeometryLoader<UnbakedWhitePearCrop4Model> {
	public static final WhitePearCrop4ModelLoader INSTANCE = new WhitePearCrop4ModelLoader();

	@Override
	public UnbakedWhitePearCrop4Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop4Model();
	}
}

