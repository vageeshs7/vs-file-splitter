package com.vags.file.splitter;

import javax.swing.*;
import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FileSplitter implements ActionListener{
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
            long totalLengthProcessed = 0;

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
                    //Progress
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    totalLengthProcessed += currentFileSize;
                    float percent =  ((float)totalLengthProcessed / (float)file.length()) * 100;
                    if(progressBar != null)
                        progressBar.setValue((int)percent);
                    else
                        System.out.println("Progress : " + (int)percent + "%");

                    //Reset
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
        try {
            long bytesPerSplit = calculateSizeInBytes(size);
            splitTextFile(filename, bytesPerSplit);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    JTextField filenameField = null;
    JTextField sizeField = null;
    JProgressBar progressBar = null;

    private void handleGUIRequest()
    {
        System.out.println("Starting File Splitter UI");
        JFrame frame = new JFrame("VS File Splitter");

        JLabel filenameLabel = new JLabel("File : ");
        filenameLabel.setBounds(10, 20, 150, 20);

        filenameField= new JTextField();
        filenameField.setBounds(50, 20, 150, 20);

        JLabel sizeLabel = new JLabel("Size : ");
        sizeLabel.setBounds(10, 50, 150, 20);

        sizeField = new JTextField();
        sizeField.setBounds(50, 50, 150, 20);

        JButton splitButton = new JButton(" Split ");
        splitButton.setBounds(30, 80, 100, 20);
        splitButton.addActionListener(this);

        progressBar = new JProgressBar(0,100);
        progressBar.setBounds(20, 110, 200, 30);
        progressBar.setValue(0);

        frame.add(filenameLabel);
        frame.add(filenameField);
        frame.add(sizeLabel);
        frame.add(sizeField);
        frame.add(splitButton);
        frame.add(progressBar);

        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String filename = filenameField.getText();
        String size = sizeField.getText();
        try {
            long bytesPerSplit = calculateSizeInBytes(size);
            this.splitTextFile(filename, bytesPerSplit);
            this.progressBar.setValue(100);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private long calculateSizeInBytes(String size) {
        long bytesPerSplit;
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

        return bytesPerSplit;
    }
}
