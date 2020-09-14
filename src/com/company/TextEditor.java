package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    // the current opened file
    private File currentFile = null;
    // the indexes of the start of each occurrence found in the search process
    private ArrayList<Integer> searchStartIndex = new ArrayList<>();
    // the indexes of the end of each occurrence found in the search process
    private ArrayList<Integer> searchEndIndex = new ArrayList<>();
    // the number of the current occurrence in the search process
    private int currentIndex = -1;

    public TextEditor() {
        super("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 550);
        setVisible(true);
        setLocationRelativeTo(null);
        init();
        revalidate();
        repaint();
    }

    public void init() {
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setName("FileChooser");
        add(fileChooser);
        fileChooser.setVisible(false);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem loadMenuItem = new JMenuItem("Open");
        loadMenuItem.setName("MenuOpen");
        loadMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        fileMenu.add(loadMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        fileMenu.add(saveMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        fileMenu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(searchMenu);

        JMenuItem searchMenuItem = new JMenuItem("Start search");
        searchMenuItem.setName("MenuStartSearch");
        searchMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        searchMenu.add(searchMenuItem);

        JMenuItem prevMatchMenuItem = new JMenuItem("Previous match");
        prevMatchMenuItem.setName("MenuPreviousMatch");
        prevMatchMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        searchMenu.add(prevMatchMenuItem);

        JMenuItem nextMatchMenuItem = new JMenuItem("Next match");
        nextMatchMenuItem.setName("MenuNextMatch");
        nextMatchMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        searchMenu.add(nextMatchMenuItem);

        JMenuItem regexMenuItem = new JMenuItem("Use regular expressions");
        regexMenuItem.setName("MenuUseRegExp");
        regexMenuItem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        searchMenu.add(regexMenuItem);

        //========================================================================================

        // Top Panel
        JPanel topPanel = new JPanel();

        ImageIcon loadIcon = new ImageIcon("Icons/browse.png", "Open");
        Image image = loadIcon.getImage();
        loadIcon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton loadButton = new JButton(loadIcon);
        loadButton.setName("OpenButton");

        ImageIcon saveIcon = new ImageIcon("Icons/save.png", "Save");
        image = saveIcon.getImage();
        saveIcon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton saveButton = new JButton(saveIcon);
        saveButton.setName("SaveButton");

        topPanel.add(loadButton);
        topPanel.add(saveButton);

        add(topPanel, BorderLayout.NORTH);

        //=========================================================================================

        // Search Panel

        JPanel searchPanel = new JPanel();

        JTextField searchTextField = new JTextField();
        searchTextField.setName("SearchField");
        searchTextField.setPreferredSize(new Dimension(250, 40));
        searchTextField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        ImageIcon searchIcon = new ImageIcon("Icons/search.png", "Search");
        image = searchIcon.getImage();
        searchIcon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton searchButton = new JButton(searchIcon);
        searchButton.setName("StartSearchButton");

        ImageIcon prevMatchIcon = new ImageIcon("Icons/back.png", "Previous Match");
        image = prevMatchIcon.getImage();
        prevMatchIcon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton prevMatchButton = new JButton(prevMatchIcon);
        prevMatchButton.setName("PreviousMatchButton");

        ImageIcon nextMatchIcon = new ImageIcon("Icons/next.png", "Next Match");
        image = nextMatchIcon.getImage();
        nextMatchIcon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        JButton nextMatchButton = new JButton(nextMatchIcon);
        nextMatchButton.setName("NextMatchButton");

        JCheckBox regexCheckBox = new JCheckBox("use regex");
        regexCheckBox.setName("UseRegExCheckbox");
        regexCheckBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);
        searchPanel.add(prevMatchButton);
        searchPanel.add(nextMatchButton);
        searchPanel.add(regexCheckBox);

        topPanel.add(searchPanel);

        //====================================================================================

        // Text Area Panel
        JPanel textAreaPanel = new JPanel();
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setName("TextArea");
        textArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        scrollPane.setName("ScrollPane");
        scrollPane.setPreferredSize(new Dimension(700, 400));
        textAreaPanel.add(scrollPane);
        add(textAreaPanel, BorderLayout.CENTER);

        //================================================================================

        // Action Listeners

        loadButton.addActionListener(e -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
                currentFile = inputFile;
                Path filePath = Path.of(inputFile.getAbsolutePath());

                try {
                    String inputFileText = new String(Files.readAllBytes(filePath));
                    textArea.setText(inputFileText);
                    JOptionPane.showMessageDialog(this, "File Opened Successfully", "Success",
                            JOptionPane.PLAIN_MESSAGE);
                } catch (IOException ioException) {
                    textArea.setText("");
                    JOptionPane.showMessageDialog(this, "File Doesn't Exist", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(e -> {
            try {
                PrintWriter printWriter = new PrintWriter(currentFile);
                String text = textArea.getText();
                printWriter.print(text);
                printWriter.close();
                JOptionPane.showMessageDialog(this, "File Saved Successfully", "Success",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (FileNotFoundException | NullPointerException exception) {
                textArea.setText("");
                JOptionPane.showMessageDialog(this, "No File Selected", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        searchButton.addActionListener(e -> {
            try {
                // clear the previous search results
                searchStartIndex.clear();
                searchEndIndex.clear();
                currentIndex = -1;

                String text = searchTextField.getText();
                Pattern pattern;
                if (regexCheckBox.isSelected())
                    pattern = Pattern.compile(text);
                else
                    pattern = Pattern.compile(text, Pattern.LITERAL);

                Matcher matcher = pattern.matcher(textArea.getText());
                boolean match = matcher.find();

                if (match) {
                    int start = matcher.start();
                    int end = matcher.end();
                    // select the first occurrence in the text area
                    textArea.setCaretPosition(end);
                    textArea.select(start, end);
                    textArea.grabFocus();

                    searchStartIndex.add(start);
                    searchEndIndex.add(end);
                    currentIndex = 0;
                }
                while (matcher.find()) {
                    searchStartIndex.add(matcher.start());
                    searchEndIndex.add(matcher.end());
                }
            } catch (Exception exception) {
                textArea.setText("");
                JOptionPane.showMessageDialog(this, "No File Selected", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        prevMatchButton.addActionListener(e -> {
            if (currentIndex == -1) return; // then the search failed
            if (currentIndex > 0) {
                --currentIndex;
            } else {
                currentIndex = searchStartIndex.size() - 1;
            }
            int start = searchStartIndex.get(currentIndex);
            int end = searchEndIndex.get(currentIndex);

            textArea.setCaretPosition(end);
            textArea.select(start, end);
            textArea.grabFocus();
        });

        nextMatchButton.addActionListener(e -> {
            if (currentIndex == -1) return; // then the search failed
            if (currentIndex < searchStartIndex.size() - 1) {
                ++currentIndex;
            } else {
                currentIndex = 0;
            }
            int start = searchStartIndex.get(currentIndex);
            int end = searchEndIndex.get(currentIndex);

            textArea.setCaretPosition(end);
            textArea.select(start, end);
            textArea.grabFocus();
        });

        //action listeners for the menu items
        loadMenuItem.addActionListener(loadButton.getActionListeners()[0]);
        saveMenuItem.addActionListener(saveButton.getActionListeners()[0]);
        exitMenuItem.addActionListener(e -> dispose());
        searchMenuItem.addActionListener(searchButton.getActionListeners()[0]);
        prevMatchMenuItem.addActionListener(prevMatchButton.getActionListeners()[0]);
        nextMatchMenuItem.addActionListener(nextMatchButton.getActionListeners()[0]);
        regexMenuItem.addActionListener(e -> {
            regexCheckBox.setSelected(true);
        });
    }
}