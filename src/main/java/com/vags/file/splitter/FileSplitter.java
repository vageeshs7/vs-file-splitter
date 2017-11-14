package com.vags.file.splitter;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.*;

public class FileSplitter {
    private void splitTextFile(String filename, long bytesPerSplit)
    {
        System.out.println("Splitting " + filename + " into splits of " + bytesPerSplit + " bytes per file");
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            File file = new File(filename);
            System.out.println("Original File size: " + file.length());
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;

            int splitCount = 0;
            bufferedWriter = new BufferedWriter(new FileWriter("Split-" + splitCount + ".txt"));
            long currentFileSize = 0;
            String lineSeperator = System.getProperty("line.separator");
            while((line = bufferedReader.readLine()) != null)
            {
                currentFileSize += line.length();
                bufferedWriter.write(line + lineSeperator);

                if(currentFileSize >= bytesPerSplit)
                {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    splitCount++;
                    currentFileSize = 0;
                    bufferedWriter = new BufferedWriter(new FileWriter("Split-" + splitCount + ".txt"));
                }

            }

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args)
    {
        FileSplitter fs = new FileSplitter();
        long bytesPerSplit = 0;

        if(args[1].endsWith("KB") || args[1].endsWith("kb"))
        {
            String lengthStr = args[1].substring(0, args[1].length()-2);
            bytesPerSplit = Long.parseLong(lengthStr.trim());
            bytesPerSplit = bytesPerSplit * 1024; //kilo bytes
        } else if(args[1].endsWith("MB") || args[1].endsWith("mb"))
        {
            String lengthStr = args[1].substring(0, args[1].length()-2);
            bytesPerSplit = Long.parseLong(lengthStr.trim());
            bytesPerSplit = bytesPerSplit * 1024 * 1024; //kilo bytes
        }else{
            bytesPerSplit = Long.parseLong(args[1].trim());
        }


        fs.splitTextFile(args[0], bytesPerSplit);
    }
}
