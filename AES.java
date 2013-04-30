/**
 * AES.java
 * 
 * This class represents the AES cipher with 128 bit key
 *
 * @author Rick Daniel (rick@araishikeiwai.com)
 * @version 1.0
 *
 */

import java.util.*;

public class AES {

    private static final int KEY_SIZE = 128, // bit
                             BLOCK_SIZE = 128, // bit
                             ROUND_NUM = 10,
                             ROUND_KEY_SIZE = 128, // bit
                             EXP_KEY_SIZE = 1408, // bit
                             GF_SIZE = 8,
                             WORD_SIZE = 4, // 1 word = 4 byte
                             BYTE_SIZE = 8, // 1 byte = 8 bit
                             POLY_MX = 0x1B,
                             GF_DIMENSION = 1 << GF_SIZE;

    //multiplicationTable di-precompute, roundKey buat cari 15 word keynya
    private int[][] multiplicationTable,
                    roundKey;

    private boolean keyAvailable = false;

    //ini s-box encrypt
    private static final int[] S_BOX = {
        0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76,
        0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0,
        0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15,
        0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75,
        0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84,
        0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF,
        0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8,
        0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
        0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73,
        0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB,
        0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79,
        0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08,
        0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A,
        0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E,
        0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF,
        0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16
    };

    //ini s-box decrypt
    private static final int[] S_BOX_I = {
        0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB,
        0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE, 0xE9, 0xCB,
        0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E,
        0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25,
        0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92,
        0x6C, 0x70, 0x48, 0x50, 0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84,
        0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06,
        0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B,
        0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73,
        0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E,
        0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18, 0xBE, 0x1B,
        0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4,
        0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F,
        0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF,
        0xA0, 0xE0, 0x3B, 0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61,
        0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D
    };

    //ini constructor
    public AES() {
        multiplicationTable = new int[GF_DIMENSION][GF_DIMENSION];
        fillMultiplicationTable();

        // the roundKey is put into array with row n corresponding to the n-th word
        roundKey = new int[EXP_KEY_SIZE / WORD_SIZE / BYTE_SIZE][WORD_SIZE];
    }

    //sebenernya ga terlalu perlu sih kalo setRoundKey baru kan tetep aja yang lama ilang
    public void reset() {
        roundKey = new int[EXP_KEY_SIZE / WORD_SIZE / BYTE_SIZE][WORD_SIZE];
        keyAvailable = false;
    }

    /**
     * @param key the key used for the cipher, 128 bit long in array with size 16 (each value of the array stores one byte of key)
     */
    public int[][] setRoundKey(int[] key) {
        //ini ngisi roundKey terus ngereturn hasilnya (buat dipake di XTS ntar)
        for (int i = 0; i < KEY_SIZE / BYTE_SIZE / WORD_SIZE; i++) {
            for (int j = 0; j < ROUND_KEY_SIZE / BYTE_SIZE / WORD_SIZE; j++) {
                roundKey[i][j] = key[(KEY_SIZE / BYTE_SIZE / WORD_SIZE * i) + j];
            }
        }
        expandKey();

        keyAvailable = true;
        return roundKey;
    }

    private void expandKey() {
        int[] rcon = {0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1B, 0x36};
        for (int i = KEY_SIZE / BYTE_SIZE / WORD_SIZE; i < EXP_KEY_SIZE / BYTE_SIZE / WORD_SIZE; i++) {
            int[] temp = new int[WORD_SIZE];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = roundKey[i - 1][j];
            }

            if (i % 4 == 0) {
                int temp2 = temp[0];
                for (int j = 0; j < 3; j++) {
                    temp[j] = temp[j + 1];
                }
                temp[3] = temp2;

                for (int j = 0; j < temp.length; j++) {
                    temp[j] = S_BOX[temp[j]];
                }
                temp[0] = add(temp[0], rcon[i / 4]);
            }

            for (int j = 0; j < WORD_SIZE; j++) {
                roundKey[i][j] = add(temp[j], roundKey[i - WORD_SIZE][j]);
            }
        }
    }

    public int[] encrypt(int[] plaintext) {
        //ini nge-encrypt
        if (!keyAvailable) return null;
        return null;
    }

    public int[] decrypt(int[] ciphertext) {
        //ini nge-decrypt
        if (!keyAvailable) return null;
        return null;
    }

    //ini ngisi tabel perkalian gf(2^8)
    private void fillMultiplicationTable() {
        for (int i = 0; i < GF_DIMENSION; i++) {
            for (int j = 0; j < GF_DIMENSION; j++) {
                multiplicationTable[i][j] = multiply(i, j, GF_SIZE, POLY_MX);
            }
        }
    }

    //ini perkalian dalam gf(2^n) dengan polinomial mx
    private int multiply(int x, int y, int n, int mx) {
        int[] arm = new int[n];
        arm[0] = y;
        
        int temp = y;
        for (int i = 1; i < n; i++) {
            int m = (temp & (1 << (n - 1))) >> (n - 1);
            temp <<= 1;
            temp &= (GF_DIMENSION - 1);
            if (m == 1) {
                temp = add(temp, mx);
            }
            arm[i] = temp;
        }
        
        int res = 0;
        for (int i = 0; i < n; i++) {
            if (((x & (1 << i)) >> (i)) == 1) {
                res = add(res, arm[i]);
            }
        }
        return res;
    }

    //ini penjumlahan dalam gf
    private int add(int x, int y) {
        return x ^ y;
    }

    //buat ngetes exp key-nya, udah sesuai sama jawaban uts
    /*public static void main(String[] araishikeiwai) {
        int[] key = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
        int[][] rk = new AES().setRoundKey(key);
        for (int i = 0; i < rk.length; i++) {
            for (int j = 0; j < rk[i].length; j++) {
                System.out.printf("%x ", rk[i][j]);
            }
            System.out.println();
        }
    }*/

}