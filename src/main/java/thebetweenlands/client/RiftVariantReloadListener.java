package thebetweenlands.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import thebetweenlands.client.sky.RiftVariant;
import thebetweenlands.common.TheBetweenlands;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RiftVariantReloadListener implements ResourceManagerReloadListener {

	private final List<RiftVariant> riftVariants = new ArrayList<>();

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		this.riftVariants.clear();

		try {
			Optional<Resource> riftsFile = Minecraft.getInstance().getResourceManager().getResource(TheBetweenlands.prefix("textures/sky/rifts/rifts.json"));
			if (riftsFile.isPresent()) {
				try (InputStreamReader reader = new InputStreamReader(riftsFile.get().open())) {
					JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
					for (int i = 0; i < array.size(); i++) {
						JsonObject jsonObj = array.get(i).getAsJsonObject();

						ResourceLocation textureMask = ResourceLocation.tryParse(jsonObj.get("texture_mask").getAsString());
						textureMask = ResourceLocation.fromNamespaceAndPath(textureMask.getNamespace(), "textures/" + textureMask.getPath());

						ResourceLocation textureOverlay = ResourceLocation.tryParse(jsonObj.get("texture_overlay").getAsString());
						textureOverlay = ResourceLocation.fromNamespaceAndPath(textureMask.getNamespace(), "textures/" + textureOverlay.getPath());

						ResourceLocation textureAltOverlay = null;
						if (jsonObj.has("texture_alt_overlay")) {
							textureAltOverlay = ResourceLocation.tryParse(jsonObj.get("texture_alt_overlay").getAsString());
							textureAltOverlay = ResourceLocation.fromNamespaceAndPath(textureAltOverlay.getNamespace(), "textures/" + textureAltOverlay.getPath());
						}

						JsonObject yawJson = jsonObj.getAsJsonObject("yaw");
						JsonObject pitchJson = jsonObj.getAsJsonObject("pitch");
						JsonObject rollJson = jsonObj.getAsJsonObject("roll");
						JsonObject scaleJson = jsonObj.getAsJsonObject("scale");
						JsonObject mirrorJson = jsonObj.getAsJsonObject("mirror");

						this.riftVariants.add(new RiftVariant(textureMask, textureOverlay, textureAltOverlay,
							yawJson.get("min").getAsFloat(), yawJson.get("max").getAsFloat(),
							pitchJson.get("min").getAsFloat(), pitchJson.get("max").getAsFloat(),
							rollJson.get("min").getAsFloat(), rollJson.get("max").getAsFloat(),
							scaleJson.get("min").getAsFloat(), scaleJson.get("max").getAsFloat(),
							mirrorJson.get("u").getAsBoolean(), mirrorJson.get("v").getAsBoolean()));
					}
				}
			}
		} catch (Exception ex) {
			TheBetweenlands.LOGGER.error("Failed loading sky rift variants", ex);
		}
	}

	public List<RiftVariant> getRiftVariants() {
		return Collections.unmodifiableList(this.riftVariants);
	}
}
