package thebetweenlands.client.stencil;

import java.nio.ByteBuffer;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexSorting;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT) // Inherit from MeshData
public class SimpleStencilledMeshData extends MeshData {
    private final MeshData baseMeshData;
    private final MeshData maskMeshData;
    private final SimpleStencilConfig maskConfig;

	public SimpleStencilledMeshData(MeshData baseMeshData, MeshData maskMeshData, SimpleStencilConfig maskConfig) {
		super(null, null);
		this.baseMeshData = baseMeshData;
		this.maskMeshData = maskMeshData;
		this.maskConfig = maskConfig;
	}

	public MeshData getBaseMeshData() {
		return this.baseMeshData;
	}
	
	public MeshData getMaskMeshData() {
		return this.maskMeshData;
	}
	
	public SimpleStencilConfig getMaskConfig() {
		return this.maskConfig;
	}
	
	@Override
    public ByteBuffer vertexBuffer() {
        return this.baseMeshData.vertexBuffer();
    }

    @Nullable
	@Override
    public ByteBuffer indexBuffer() {
        return this.baseMeshData.indexBuffer();
    }

	@Override
    public MeshData.DrawState drawState() {
        return this.baseMeshData.drawState();
    }

    @Nullable
	@Override
    public MeshData.SortState sortQuads(ByteBufferBuilder bufferBuilder, VertexSorting sorting) {
        return this.baseMeshData.sortQuads(bufferBuilder, sorting);
    }

    @Override
    public void close() {
    	this.baseMeshData.close();
    	this.maskMeshData.close();
    }
    
    public static enum MaskType {
    	
    }
}
