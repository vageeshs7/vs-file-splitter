package com.vags.file.splitter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class FileSplitter implements ActionListener, Runnable{
    private void splitTextFile(String filename, long bytesPerSplit)
    {
        logMessage("Splitting " + filename + " into splits of " + bytesPerSplit + " bytes per file");
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            File file = new File(filename);
            logMessage("Original File size: " + file.length());
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
                    logMessage("Created Split-" + splitCount + ".txt with " + currentFileSize + "bytes");
                    splitCount++;
                    //Progress

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
            try {
                fs.handleGUIRequest();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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
    JTextArea infoTextArea = null;

    private void handleGUIRequest() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        System.out.println("Starting File Splitter UI");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
            this.splitTextFile(filename, bytesPerSplit);
            this.progressBar.setValue(100);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
