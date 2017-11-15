package com.vags.file.splitter;

import javax.swing.*;
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
                    System.out.println("Created Split-" + splitCount + ".txt with " + currentFileSize + "bytes");
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args)
    {
        FileSplitter fs = new FileSplitter();
        if(args.length > 0 && "-n".equals(args[0])) {
            fs.handleCommandLineRequest(args[1], args[2]);
        }else {
            fs.handleGUIRequest();
        }
    }

    private void handleCommandLineRequest(String filename, String size) {
        long bytesPerSplit = 0;

        try {
            if(size.endsWith("KB") || size.endsWith("kb"))
            {
                String lengthStr = size.substring(0, size.length()-2);
                bytesPerSplit = Long.parseLong(lengthStr.trim());
                bytesPerSplit = bytesPerSplit * 1024; //kilo bytes
            } else if(size.endsWith("MB") || size.endsWith("mb"))
            {
                String lengthStr = size.substring(0, size.length()-2);
                bytesPerSplit = Long.parseLong(lengthStr.trim());
                bytesPerSplit = bytesPerSplit * 1024 * 1024; //kilo bytes
            }else{
                bytesPerSplit = Long.parseLong(size.trim());
            }

            splitTextFile(filename, bytesPerSplit);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleGUIRequest()
    {
        System.out.println("Starting File Splitter UI");
        JFrame frame = new JFrame("VS File Splitter");

        JTextField filenameField = new JTextField();
        filenameField.setBounds(20, 20, 150, 20);

        JTextField sizeField = new JTextField();
        sizeField.setBounds(20, 50, 150, 20);

        JButton splitButton = new JButton(" Split ");
        splitButton.setBounds(20, 80, 150, 20);

        frame.add(filenameField);
        frame.add(sizeField);
        frame.add(splitButton);

        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
