package ru.owl.encryption.model;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOWorker {
    public static byte[] getFileBytes(File inputFile) throws IOException{
        byte[] result = null;
        try (BufferedInputStream fis =
                     new BufferedInputStream(new FileInputStream(inputFile)))
        {
            result = new byte[fis.available()];
            fis.read(result);
        }

        return result;
    }

    public static void writeBytesToFile(byte[] bytes, File outputFile){
        try (BufferedOutputStream fos =
                     new BufferedOutputStream(new FileOutputStream(outputFile)))
        {
            fos.write(bytes);
            fos.flush();
        } catch (FileNotFoundException e) {
            Logger.getLogger(Crypter.class.getName())
                    .log(Level.SEVERE, null, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
