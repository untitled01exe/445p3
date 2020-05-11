package com.company;

import java.math.BigInteger;
import java.util.Arrays;

public class IntCon {

    public byte[] intToArr(BigInteger i) {
        //System.out.println("to " + Arrays.toString(i.toByteArray()));
        return i.toByteArray();
    }

    public BigInteger arrToBInt(byte[] input){
        BigInteger big = new BigInteger(input);
        return big;
    }

    public static void main(String[] args) {
        IntCon ic = new IntCon();
        byte[] b;
        //System.out.println(Arrays.toString(b));
        //System.out.println(b.length);
        BigInteger bi  = new BigInteger("13135573131760633232255419911278321046186302353961361867475107918972535543677176995641322422502867516817194617893984135047605583473440979155350253300634707");
        System.out.println(bi.toString());
        b = ic.intToArr(bi);
        System.out.println(Arrays.toString(b));
        System.out.println(b.length);
    }
}
