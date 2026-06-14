package thebetweenlands.client.model.baked.whitepearcrop1;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WhitePearCrop1ModelLoader implements IGeometryLoader<UnbakedWhitePearCrop1Model> {
	public static final WhitePearCrop1ModelLoader INSTANCE = new WhitePearCrop1ModelLoader();

	@Override
	public UnbakedWhitePearCrop1Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWhitePearCrop1Model();
	}
}

