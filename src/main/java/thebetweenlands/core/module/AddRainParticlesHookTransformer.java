package thebetweenlands.core.module;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AddRainParticlesHookTransformer extends TransformerModule {
	public AddRainParticlesHookTransformer() {
		this.addAcceptedClass("buq", "net.minecraft.client.renderer.EntityRenderer");
	}

	@Override
	public String getName() {
		return "AddRainParticlesHook";
	}

	@Override
	public boolean acceptsMethod(MethodNode method) {
		return method.name.equals(this.getMappedName("q", "addRainParticles")) && method.desc.equals("()V");
	}

	@Override
	public void transformMethodInstruction(MethodNode method, AbstractInsnNode node, int index) {
		if(index == 0) {
			List<AbstractInsnNode> insertions = new ArrayList<>();

			//Call ClientHooks#onAddRainParticles() and return if true
			insertions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "thebetweenlands/client/ClientHooks", "onAddRainParticles", "()Z", false));
			LabelNode jumpIfFalseTarget = new LabelNode();
			insertions.add(new JumpInsnNode(Opcodes.IFNE, jumpIfFalseTarget));
			insertions.add(new InsnNode(Opcodes.RETURN));
			insertions.add(jumpIfFalseTarget);

			this.insertBefore(method, node, insertions);
			
			this.setSuccessful();
		}
	}
}
