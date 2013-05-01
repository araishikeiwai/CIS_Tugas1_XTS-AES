import java.io.*;

public class CisUtils {
	public static int[] toIntArray(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int numData;
		byte[] data = new byte[16384];

		while ((numData = stream.read(data)) != -1)
			buffer.write(data, 0, numData);
		buffer.flush();

		byte[] byteResult = buffer.toByteArray();
		int[] intResult = new int[byteResult.length];
		for (int i = 0; i < byteResult.length; i++)
			intResult[i] = ((int)byteResult[i]) + 128;

		return intResult;
	}
}