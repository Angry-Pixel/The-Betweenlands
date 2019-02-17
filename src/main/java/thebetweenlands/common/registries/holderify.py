with open('SoundRegistry.java', 'r') as file:
    data = file.readlines()

newData = []

blockList = []
regList = []

for i in range(0, len(data)):
	line = data[i]
	
	if "public static final" in line:
		spl = line.strip().split(" ")
		name = spl[4]
		
		blockList.append("\n")
		blockList.append("    @ObjectHolder(\"" + name.lower() + "\")\n")
		blockList.append("    " + " ".join(spl[0:5]) + " = null;\n")
	
with open('SoundRegList.java', 'w') as file:
    file.writelines(blockList)