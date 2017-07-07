package thebetweenlands.core.module;

import static org.objectweb.asm.Opcodes.FLOAD;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PreRenderShadersHookTransformer extends TransformerModule {
	public PreRenderShadersHookTransformer() {
		this.addAcceptedClass("bqe", "net.minecraft.client.renderer.EntityRenderer");
	}

	@Override
	public String getName() {
		return "PreRenderShadersHook";
	}

	@Override
	public boolean acceptsMethod(MethodNode method) {
		return method.name.equals(this.getMappedName("a", "updateCameraAndRender")) && method.desc.equals("(FJ)V");
	}

	@Override
	public int transformMethodInstruction(MethodNode method, AbstractInsnNode node, int index) {
		if(node instanceof MethodInsnNode) {
			MethodInsnNode methodCallNode = (MethodInsnNode) node;
			if(methodCallNode.name.equals(this.getMappedName("c", "renderEntityOutlineFramebuffer")) && methodCallNode.desc.equals("()V")
					&& methodCallNode.owner.equals(this.getMappedName("bqm", "net/minecraft/client/renderer/RenderGlobal"))) {
				List<AbstractInsnNode> insertions = new ArrayList<AbstractInsnNode>();

				//Call ClientHooks#onPreRenderShaders(float partialTicks)
				insertions.add(new VarInsnNode(FLOAD, 1));
				insertions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thebetweenlands/client/ClientHooks", "onPreRenderShaders", "(F)V", false));

				this.insertBefore(method, node, insertions);

				this.setSuccessful();

				return 2;
			}
		}
		return 0;
	}
}
