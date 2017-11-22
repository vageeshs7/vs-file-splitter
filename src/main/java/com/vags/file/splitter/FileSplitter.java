package com.vags.file.splitter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FileSplitter implements ActionListener, Runnable{
    private boolean splitTextFile(String filename, long bytesPerSplit)
    {
        logMessage("Splitting " + filename + " into splits of " + bytesPerSplit + " bytes per file");
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        boolean status;

        try {
            File file = new File(filename);
            logMessage("Original File size: " + file.length());

            int extIndex = filename.lastIndexOf(".");
            String splitFilename;
            String splitFileExt;
            if(extIndex < 0)
            {
                splitFilename = filename;
                splitFileExt = "";
            }else{
                splitFilename = filename.substring(0, extIndex);
                splitFileExt = filename.substring(extIndex, filename.length());
            }

            bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            long totalLengthProcessed = 0;

            int splitCount = 0;
            String splitFilenameFinalName = splitFilename + "-" + splitCount + splitFileExt;
            bufferedWriter = new BufferedWriter(new FileWriter(splitFilenameFinalName));
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
                    logMessage("Created " + splitFilenameFinalName + " with " + currentFileSize + "bytes");
                    splitCount++;
                    splitFilenameFinalName = splitFilename + "-" + splitCount + splitFileExt;

                    totalLengthProcessed += currentFileSize;
                    float percent =  ((float)totalLengthProcessed / (float)file.length()) * 100;
                    if(progressBar != null)
                        progressBar.setValue((int)percent);
                    else
                        System.out.println("Progress : " + (int)percent + "%");

                    //Reset
                    currentFileSize = 0;
                    bufferedWriter = new BufferedWriter(new FileWriter(splitFilenameFinalName));
                }

            }

            bufferedReader.close();
            status = true;
        } catch (Exception e) {
            this.logMessage("Error :" + e.getMessage());
            e.printStackTrace();
            status = false;
        } finally{
            try {
                if(bufferedReader != null)
                    bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if(bufferedWriter != null)
                    bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
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

    private JTextField filenameField = null;
    private JTextField sizeField = null;
    private JProgressBar progressBar = null;
    private JTextArea infoTextArea = null;

    private void handleGUIRequest()  {
        System.out.println("Starting File Splitter UI");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = (int) (dimension.getWidth() * 0.5);
        int frameHeight = (int) (dimension.getHeight() * 0.7);
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
        progressBar.setBounds(20, 110, frameWidth - 50, 30);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(false);

        infoTextArea = new JTextArea("");

        infoTextArea.setColumns(50);
        infoTextArea.setRows(5);
        JScrollPane textScroller = new JScrollPane(infoTextArea);
        textScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textScroller.setBounds(20, 160, frameWidth - 50, 300);


        frame.add(filenameLabel);
        frame.add(filenameField);
        frame.add(sizeLabel);
        frame.add(sizeField);
        frame.add(splitButton);
        frame.add(progressBar);
        frame.add(textScroller);


        frame.setSize(frameWidth, frameHeight);
        frame.setLocation((int) (dimension.getWidth() * 0.5 - frameWidth * 0.5),
                (int) (dimension.getHeight() * 0.5 - frameHeight * 0.5));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Thread t = new Thread(this);
        t.start();
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

    private void logMessage(String message)
    {
        System.out.println(message);
        if(infoTextArea != null) {
            this.infoTextArea.append("\n" + message);
        }
    }

    @Override
    public void run() {
        String filename = filenameField.getText();
        String size = sizeField.getText();
        try {
            long bytesPerSplit = calculateSizeInBytes(size);
            boolean status = this.splitTextFile(filename, bytesPerSplit);
            if(status)
                this.progressBar.setValue(100);
            else
                this.progressBar.setValue(-1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
