import java.nio.IntBuffer;

class LwglTemps {
	static private IntBuffer[] intTemps = new IntBuffer[]{IntBuffer.allocate(1), IntBuffer.allocate(1), IntBuffer.allocate(1), IntBuffer.allocate(1)};
	static IntBuffer intBuffer(int index) {
		intTemps[index].clear();
		return intTemps[index];
	}

	static int intValue(int index) {
		return intBuffer(index).get(0);
	}
}
