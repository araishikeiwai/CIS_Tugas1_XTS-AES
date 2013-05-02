class XTS {

    private static final int KEY_SIZE = 256;
    private static final int SPLIT_KEY_SIZE = 128;
    private static final int BYTE_SIZE = 8;
    private static final int BLOCK_SIZE = 128;
    private static final int ALPHA = 0x02;
    private static int[] key = new int[KEY_SIZE / BYTE_SIZE];
    private static int[] tweak = {0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef, 0xfe, 0xdc, 0xba, 0x98, 0x76, 0x54, 0x32, 0x10};
    private static int[] plaintext = new int[16];
    private static int[] alpha = new int[16];

    public XTS() {
        
        for (int i = 0; i < alpha.length - 1; i++) {
            alpha[i] = 0x00;
        }
        alpha[alpha.length - 1] = ALPHA;

    }

    // enkripsi plaintext keseluruhan
    public int[] encrypt(int[] key, int[] plaintext, int[] tweak) {
        int[] key1 = new int[SPLIT_KEY_SIZE / BYTE_SIZE];
        System.arraycopy(key, 0, key1, 0, key1.length);
        int[] key2 = new int[SPLIT_KEY_SIZE / BYTE_SIZE];
        System.arraycopy(key, SPLIT_KEY_SIZE / BYTE_SIZE, key2, 0, key2.length);

        int[] ciphertext = new int[plaintext.length];

        int m = plaintext.length / (BLOCK_SIZE / BYTE_SIZE);

        //plaintext per block
        int[] plntxt = new int[16];
        int[] citext;

        //enkripsi blok yang utuh 128 bit
        for (int q = 0; q < m - 2; q++) {
            System.arraycopy(plaintext, q * 16, plntxt, 0, plntxt.length);
            citext = blockEncrypt(key1, key2, plntxt, tweak, q);
            System.arraycopy(citext, 0, ciphertext, q * 16, citext.length);
        }
        if (plaintext.length % (BLOCK_SIZE / BYTE_SIZE) == 0) {
            System.arraycopy(plaintext, (m - 1) * 16, plntxt, 0, plaintext.length);
            citext = blockEncrypt(key1, key2, plntxt, tweak, (m-1));
            System.arraycopy(citext, 0, ciphertext, (m-1) * 16, citext.length);

            // untuk kasus yang ngga utuh blok terakhirnya 
        } /*else {
            int lastLength = plaintext.length - (m * 16); // panjang block terakhir (<128)
            int[] lastPlntxt = new int[lastLength];
            for (int i = 0; i < lastLength; i++) {
                lastPlntxt[i] = plaintext[i + (m * 16)];  // block terakhir
            }

            //cari jumlah bit block terakhir
            int ii = lastLength - 1;
            int angka = lastPlntxt[ii];
            int temp = angka;
            int counter = 0;
            while ((temp & 1) == 0) {
                temp >>= 1;
                counter++;
            }
            int bitSize = (ii * 8) + (8 - counter);

            //enkripsi 2 blok terakhir, bila blok terakhir tidak utuh 128 bit
            System.arraycopy(plaintext, (m - 1) * 16, plntxt, 0, plaintext.length);
            int[] cece = blockEncrypt(key1, key2, plntxt, tweak, q);

            System.arraycopy(cece, 0, ciphertext, m * 16, ii);
        }*/



        return ciphertext;

    }

    // enkripsi per block
    public int[] blockEncrypt(int[] key1, int[] key2, int[] plaintext, int[] tweak, int blockKe) {
        AES aes = new AES();
        aes.setRoundKey(key2);
        int[] hasilEncryptSatu = aes.encrypt(tweak);

        int[] temp = alpha;
        for (int i = 0; i < blockKe - 1; i++) {
            temp = CisUtils.multiplyGF2_128(temp, alpha);
        }

        int[] alphaPangkat = temp;

        int[] te = CisUtils.multiplyGF2_128(hasilEncryptSatu, alphaPangkat);

        int[] pepe = new int[16];
        for (int i = 0; i < pepe.length; i++) {
            pepe[i] = plaintext[i] ^ te[i];
        }

        aes.setRoundKey(key1);
        int[] cece = aes.encrypt(pepe);

        int[] hasil;
        hasil = new int[16];
        for (int i = 0; i < hasil.length; i++) {
            hasil[i] = cece[i] ^ te[i];
        }

        return hasil;
    }
}
