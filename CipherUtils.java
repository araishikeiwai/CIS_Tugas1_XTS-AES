import java.io.*;

public class CipherUtils {

    private static final int GF_SIZE = 128,
                             BLOCK_SIZE = 128,
                             BYTE_SIZE = 8,
                             POLY_128 = 0x87;

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

    public static void writeToFile(int[] arr, File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);

        byte[] data = new byte[arr.length];
        for (int i = 0; i < arr.length; i++)
            data[i] = (byte)(arr[i] - 128);

        stream.write(data);
        stream.close();
    }

    //multiplication in gf(2^128) with irreducible polynomial x^128 + x^7 + x^2 + x + 1
    public static int[] multiplyGF2_128(int[] x, int[] y) {
        if (x.length != BLOCK_SIZE / BYTE_SIZE || y.length != BLOCK_SIZE / BYTE_SIZE) {
            return null;
        }

        int[][] arm = new int[GF_SIZE][y.length];
        System.arraycopy(y, 0, arm[0], 0, y.length);

        int[] temp = new int[y.length];
        System.arraycopy(y, 0, temp, 0, y.length);

        int n = 8;

        for (int i = 1; i < GF_SIZE; i++) {
            int m = (temp[0] & (1 << (n - 1))) >> (n - 1);
            temp = shiftLeft(temp);
            if (m == 1) {
                temp[temp.length - 1] ^= POLY_128;
            }
            arm[i] = temp;
        }

        int[] res = new int[y.length];
        for (int i = 0; i < GF_SIZE / BYTE_SIZE; i++) {
            for (int j = 0; j < BYTE_SIZE; j++) {
                if (((x[i] & (1 << j)) >> (j)) == 1) {
                    for (int k = 0; k < res.length; k++) {
                        res[k] = res[k] ^ arm[(GF_SIZE - (i + 1) * BYTE_SIZE) + j][k];
                    }
                }
            }
        }

        return res;
    }

    private static int[] shiftLeft(int[] x) {
        int msb = 0;
        int n = 8;
        int[] res = new int[x.length];

        for (int i = x.length - 1; i >= 0; i--) {
            int temp = (x[i] & (1 << (n - 1))) >> (n - 1);
            res[i] = x[i] << 1;
            res[i] &= ((1 << n) - 1);
            if (msb == 1) {
                res[i] |= 1;
            }
            msb = temp;
        }

        return res;
    }

    /*public static void main(String[] araishikeiwai) {
        int[] alpha = new int[16];
        alpha[15] = 0xFF;

        int[] beta = new int[16];
        beta[15] = 0x10;

        int[] res = multiplyGF2_128(alpha, beta);
        for (int i = 0; i < res.length; i++) {
            System.out.printf("%x ", res[i]);
        }
        System.out.println();
    }*/
}