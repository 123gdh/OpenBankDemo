package com.example.openbank;

import java.io.*;

public class FileTest {
    public static void main(String[] args) throws IOException {
        File file = new File("OpenBank\\src\\test\\resources\\a.txt");
        File fileout = new File("OpenBank\\src\\test\\resources\\b.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"GBK");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileout),"GBK");
        boolean mkdir = file.createNewFile();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(fileout,true);
            try {
                char[] bytes = new char[4];
                int len;
                while ((len = inputStreamReader.read(bytes)) != -1){
                    System.out.println("len="+len);
                    System.out.println("byte="+bytes.toString());
                    outputStreamWriter.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            inputStreamReader.close();
            outputStreamWriter.close();
        }
    }
}
