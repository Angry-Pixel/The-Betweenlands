package thebetweenlands.client.rendering;

import java.nio.IntBuffer;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ActiveProfiler;
import thebetweenlands.common.TheBetweenlands;

// Compiles a atlas of all sky textures
// TODO: add datapack and resorce pack inputs
public class  BetweenlandsSkyShaderHandler {
	// TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
    // AbstractTexture abstracttexture = texturemanager.getTexture(null);
	
	public boolean isLoaded = false;		// When an update to the texture atlas is made, set this tag to false
	public boolean doUpdate = false;		// When a change is made to any data in the buffer this is set to true
	public int dataBufferId = -1;			// The buffer id, set to -1 to setup
	
	public int skyTexCount;
	public int effectTexCount;
	public int riftTexCount;
	
	// Do not use this unless you know exactly what you are doing!
	// Raw data to be sent to shader
	public IntBuffer Buffer;
	
	// Texture atlas data structure
	// Alows for ease of use externaly
	class AtlasObject {
		ResourceLocation name;
		float u0;
		float v0;
		float u1;
		float v1;
		
		// Constructs the atlas object, however does not contain uv values yet
		// called beffor atlas
		public AtlasObject(ResourceLocation name) {
			this.name = name;
		}
		
		// call only after atlas is set
		public void set() {
			TextureAtlasSprite sprite = skyAtlas.getSprite(name);
			// some cropping to remove some lines
	    	u0 = sprite.getU0()+0.01f;
	    	v0 = sprite.getV0()+0.01f;
	    	u1 = sprite.getU1()-0.01f;
	    	v1 = sprite.getV1()-0.01f;
		}
	};
	
	// Rift atlas object
	class RiftAtlasObject {
		AtlasObject day;
		AtlasObject night;
		AtlasObject mask;
		
		// Constructs the rift object, however does not contain uv values yet
		// called beffor atlas
		public RiftAtlasObject(ResourceLocation dayname, ResourceLocation nightname, ResourceLocation maskname) {
			this.day = new AtlasObject(dayname);
			this.night = new AtlasObject(nightname);
			this.mask = new AtlasObject(maskname);
		}
		
		// call only after atlas is set
		public void set() {
			this.day.set();
			this.night.set();
			this.mask.set();
		}
	};
	
	// Used to convert lists to stream for atlas
	public List<ResourceLocation> inputUtilTextures;
	public List<ResourceLocation> inputSkyTextures;
	public List<ResourceLocation> inputEffectsTextures;
	public List<ResourceLocation> inputRiftsTextures;

	// Noise texture
	public AtlasObject NoiseUV;

	// Used to read back data from atlas
	public List<AtlasObject> SkyUV;
	public List<AtlasObject> EffectUV;
	public List<RiftAtlasObject> RiftUV;
	
	// Read and write data list
	public List<RiftObject> RiftObjects;
	
	// A single texture holding all rift textures
    public TextureAtlas skyAtlas = new TextureAtlas(new ResourceLocation(TheBetweenlands.ID, "textures/sky/sky_atlas"));	// Makes atlas
    // TODO: generate and cache a .png file containing
    
    // Load defalt atlas from json
    // Alows us to combine all sky assets into a single texture to send to the shader
    // Should provide a speed boost and alow for alot of textures
    
    // Base hendler
    public BetweenlandsSkyShaderHandler(boolean useStockTextures) {
    	// If true (do small brain atlas) (will make smarter later)
    	if (useStockTextures) {
    		// Format AtlasObject lists
			NoiseUV = new AtlasObject(new ResourceLocation(TheBetweenlands.ID, "shader/noise"));
    		SkyUV = List.of(new AtlasObject(new ResourceLocation(TheBetweenlands.ID, "sky/fog_texture")), new AtlasObject(new ResourceLocation(TheBetweenlands.ID, "sky/sky_texture")));
        	EffectUV = List.of(new AtlasObject(new ResourceLocation(TheBetweenlands.ID, "sky/spoopy")));
        	RiftUV = List.of(new RiftAtlasObject(new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_overlay_1"), new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_alt_overlay_1"), new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_mask_1")),
        			new RiftAtlasObject(new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_overlay_2"), new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_alt_overlay_2"), new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_mask_2")),
        			new RiftAtlasObject(new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_overlay_3"), new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_alt_overlay_3"), new ResourceLocation(TheBetweenlands.ID, "sky/rifts/sky_rift_mask_3")));
    		
        	//TODO: make auto list builder
			// Utility noise texture
			inputUtilTextures = List.of(NoiseUV.name);
    		// Must have fog texture, Must have sky texture if starfield is turned off
    		inputSkyTextures = List.of(SkyUV.get(0).name, SkyUV.get(1).name);
    		// Must have spook texture
    		inputEffectsTextures = List.of(EffectUV.get(0).name);
    		// Must have atleast one rift texture
    		inputRiftsTextures = List.of(RiftUV.get(0).day.name, RiftUV.get(0).night.name, RiftUV.get(0).mask.name,
    				RiftUV.get(1).day.name, RiftUV.get(1).night.name, RiftUV.get(1).mask.name,
    				RiftUV.get(2).day.name, RiftUV.get(2).night.name, RiftUV.get(2).mask.name);
    		
    		// basic rift
    		RiftObjects = List.of(new RiftObject(0, 0.5f, 0.5f, 0.6f, 0.6f, 1.0f, 0.0f));
    		
    		return;
    	}
    	
    	// Else leave clear
		inputUtilTextures = List.of();
    	inputSkyTextures = List.of();
    	inputEffectsTextures = List.of();
    	inputRiftsTextures = List.of();
    	
    	SkyUV = List.of();
    	EffectUV = List.of();
    	RiftUV = List.of();
    	
    	RiftObjects = List.of();
    }
    
    // Warning: Must be in render thread
    // Call upon atlas setup (startup and resorcepack loading)
    public void setup(ResourceManager resourcemanager) {
    	// Uhese values are unused curently: set to 0
    	LongSupplier starttimenano = new LongSupplier() { public long getAsLong() { return 0; } };
    	IntSupplier starttime = new IntSupplier() { public int getAsInt() { return 0; } };
    	
    	//Stream<ResourceLocation> textureStream = Stream.of(new ResourceLocation(Betweenlands.ID, "sky/rifts/sky_rift_mask_1"),
    	//		new ResourceLocation(Betweenlands.ID, "sky/sky_texture"),
        //		new ResourceLocation(Betweenlands.ID, "sky/fog_texture"),
        //		new ResourceLocation(Betweenlands.ID, "sky/rifts/sky_rift_mask_2"));
    	
    	// Makes a list for texture atlas to use when stitching
		Stream<ResourceLocation> textureStream = Stream.of(inputUtilTextures.stream(), inputSkyTextures.stream(), inputEffectsTextures.stream(), inputRiftsTextures.stream())
				.flatMap(s -> s);

    	// Stitch atlas
    	skyAtlas.reload(skyAtlas.prepareToStitch(resourcemanager, textureStream, new ActiveProfiler(starttimenano, starttime, true), 0));
    	
    	skyAtlas.setFilter(true, false);
    	
    	// Setup atlas objects
		NoiseUV.set();
    	SkyUV.forEach((AtlasObject object) -> {object.set();});
    	EffectUV.forEach((AtlasObject object) -> {object.set();});
    	RiftUV.forEach((RiftAtlasObject object) -> {object.set();});
    	
    	
    	// Makes a buffer object ready to send to shader
    	// contains atlas texture cords to use when sampling
    	// beware undefined waning here (storing float as int here)
    	dataBufferId = GL40.glGenBuffers();
    	GL40.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, dataBufferId);
    	
    	// Stock buffer settings
		IntBuffer Input = BufferUtils.createIntBuffer(1000);

		// TODO: add animation textures (and figure out how to fit a entire animation in a texture)
		// calculate data location using stride (purely here to make it easy to follow)
		int indexPoint = 4;								// shift by 4 to add noise util
		Input.put(indexPoint);
		indexPoint += SkyUV.size() * 4;
    	Input.put(indexPoint);							//	Effect textures (unused untill I add events)
		indexPoint += EffectUV.size() * 4;
    	Input.put(indexPoint);							//	Rift textures (day overlay, night overlay, mask)
		indexPoint += RiftUV.size() * 12;
    	Input.put(indexPoint-5);							//	Rift objects (current open rifts)
    	Input.put(RiftObjects.size());					//	Rift objects count

    	// Cord writing
		// Noise texture
		Input.put(Float.floatToRawIntBits(NoiseUV.u0));
		Input.put(Float.floatToRawIntBits(NoiseUV.v0));
		Input.put(Float.floatToRawIntBits(NoiseUV.u1));
		Input.put(Float.floatToRawIntBits(NoiseUV.v1));

    	// Sky textures
    	SkyUV.forEach((AtlasObject object) -> {Input.put(Float.floatToRawIntBits(object.u0));
    		Input.put(Float.floatToRawIntBits(object.v0));
    		Input.put(Float.floatToRawIntBits(object.u1));
    		Input.put(Float.floatToRawIntBits(object.v1));});

    	// Effect textures
    	EffectUV.forEach((AtlasObject object) -> {Input.put(Float.floatToRawIntBits(object.u0));
			Input.put(Float.floatToRawIntBits(object.v0));
			Input.put(Float.floatToRawIntBits(object.u1));
			Input.put(Float.floatToRawIntBits(object.v1));});
    	
    	// Rift textures
    	RiftUV.forEach((RiftAtlasObject object) -> {
    		// Day (I might need to get a day rift texture with just the god rays for better visuals)
    		Input.put(Float.floatToRawIntBits(object.day.u0));
			Input.put(Float.floatToRawIntBits(object.day.v0));
			Input.put(Float.floatToRawIntBits(object.day.u1));
			Input.put(Float.floatToRawIntBits(object.day.v1));
			// Night
			Input.put(Float.floatToRawIntBits(object.night.u0));
			Input.put(Float.floatToRawIntBits(object.night.v0));
			Input.put(Float.floatToRawIntBits(object.night.u1));
			Input.put(Float.floatToRawIntBits(object.night.v1));
			// Mask
			Input.put(Float.floatToRawIntBits(object.mask.u0));
			Input.put(Float.floatToRawIntBits(object.mask.v0));
			Input.put(Float.floatToRawIntBits(object.mask.u1));
			Input.put(Float.floatToRawIntBits(object.mask.v1));});
    	
    	// Rift objects
    	RiftObjects.forEach((RiftObject object) -> {
			// TODO: texureindex is not sending to shader, must be addressed
    		Input.put(object.texureindex);
    		Input.put(Float.floatToRawIntBits(object.u));
    		Input.put(Float.floatToRawIntBits(object.v));
    		Input.put(Float.floatToRawIntBits(object.width));
    		Input.put(Float.floatToRawIntBits(object.height));
    		Input.put(Float.floatToRawIntBits(object.opacity));
    		Input.put(Float.floatToRawIntBits(object.ang));});
    	
    	// Index list
    	// Send data to buffer
    	//Betweenlands.LOGGER.info("Buffer id bound to: " + Betweenlands.skyTextureHandler.bufferId);
    	//ByteBuffer test = GL40.glMapBuffer(GL40.GL_UNIFORM_BUFFER, GL40.GL_READ_WRITE, Input.capacity(), Input);
    	GL40.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, BufferUtils.createIntBuffer(1000), GL40.GL_STATIC_DRAW);
    	GL40.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, Input.rewind());
    	GL40.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, dataBufferId);
    	
    	if (GL40.glIsBuffer(dataBufferId)) {
    		TheBetweenlands.LOGGER.info("Buffer id bound to: " + dataBufferId);
    	}
    	else {
    		TheBetweenlands.LOGGER.info("Failed to bind buffer id to: " + dataBufferId);
    	}
    	// Reset for safety
    	RenderSystem.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, () -> 0);
    	
    	// Mark as loaded
    	isLoaded = true;
    }
    
    // Prepares the handler to be cleared
    public void release() {
    	
    }
    
    public void simpleUpload(RiftObject upload) {
    	
    	GL40.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, dataBufferId);
    	
    	int index = (SkyUV.size() * 4) + (EffectUV.size() * 4) + (RiftUV.size() * 12) + 4;
    	
    	IntBuffer Input = BufferUtils.createIntBuffer(256);
    	GL40.glGetBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, Input);
    	
    	Input.put(index, upload.texureindex);
    	Input.put(index+1, Float.floatToRawIntBits(upload.u));
    	Input.put(index+2, Float.floatToRawIntBits(upload.v));
    	Input.put(index+3, Float.floatToRawIntBits(upload.width));
    	Input.put(index+4, Float.floatToRawIntBits(upload.height));
    	Input.put(index+5, Float.floatToRawIntBits(upload.opacity));
    	Input.put(index+6, Float.floatToRawIntBits(upload.ang));
    	
    	// Set
    	GL40.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, Input.rewind());
    	
    	// Reset for safety
    	RenderSystem.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, () -> 0);
    }
}
