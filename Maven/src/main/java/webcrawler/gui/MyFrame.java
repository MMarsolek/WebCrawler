package webcrawler.gui;

import webcrawler.CompleteUrlSanitizer;
import webcrawler.UpToDomainSanitizer;
import webcrawler.UpToQuerySanitizer;
import webcrawler.WebCrawler;

import javax.print.Doc;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;


public class MyFrame extends JFrame {

    private  int myMaxSites;
    private int myDrThreads;
    private int myDpThreads;
    private  String myUrl;
    private String myWordSearch;
    private WebCrawler myWc;
    private final Color myBackground;
    private ButtonGroup myRadioButtons;
    private PropertyChangeListener myWebcrawlerListener;
    private final Object myResultLock;
    private JButton myStartButton;
    private JButton myStopButton;
    private JButton myRestartButton;
    private final DefaultListModel<String> resultDefaultList;
    private final Border border;
    private  final Label completeLabel;

    private final int width;
    private final int height;
    private final int myFrameWidth;
    private final int myFrameHeight;


    public MyFrame(){
        myDpThreads = 10;
        myDrThreads = 10;
        myMaxSites = 10;
        myUrl = "https://www.Google.com";
        myWordSearch = "Example";
        myBackground = new Color(0xFFFAFAFA);
        myResultLock = new Object();
        resultDefaultList = new DefaultListModel<>();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.width;
        height = screenSize.height;
        border = BorderFactory.createLineBorder(Color.black, 1);
        completeLabel = new Label("Complete!");
        myFrameHeight = height/2;
        myFrameWidth = width/3+10;



    }
    public void start() {
        this.setMinimumSize(new Dimension(myFrameWidth,myFrameHeight));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Web Crawler");
        JPanel inputSection = createInputSections();
        this.add(inputSection, BorderLayout.NORTH);

        inputSection.setBackground(myBackground);
        this.pack();
        this.setVisible(true);

        JPanel controlButtons = new JPanel();
        myStartButton = new JButton("Start");
        controlButtons.add(myStartButton, BorderLayout.NORTH);

        myStopButton = new JButton("Stop");
        myStopButton.setVisible(false);
        controlButtons.add(myStopButton, BorderLayout.NORTH);

        myRestartButton = new JButton("Restart");
        myRestartButton.setVisible(false);
        controlButtons.add(myRestartButton, BorderLayout.NORTH);
        completeLabel.setVisible(false);
        controlButtons.add(completeLabel, BorderLayout.SOUTH);


        this.add(controlButtons, BorderLayout.SOUTH);

        controlWebCrawler();
        JPanel myResultPanel = new JPanel();
        myResultPanel.add(setResultPanel());
        myResultPanel.setBorder(border);
        this.add(new JScrollPane(setResultPanel()));
        this.getRootPane().setBorder(new EmptyBorder(5,5,5,5));
    }


    private JPanel createInputSections(){
        JPanel textFieldPanel = new JPanel();
        myRadioButtons = new ButtonGroup();
        ArrayList<JRadioButton> listOfButtons = new ArrayList<JRadioButton>();
        JPanel buttonPanel = new JPanel();



        textFieldPanel.add(createPanelForFormattedTextField("Number of Sites to Visit", 5, myMaxSites, tf -> myMaxSites = getIntFromTextField(tf)));
        textFieldPanel.add(createPanelForFormattedTextField("Word to Search For", 10, myWordSearch, tf -> myWordSearch = tf.getText()));
        textFieldPanel.add(createPanelForFormattedTextField("Starting URL Input", 15, myUrl,tf -> myUrl = tf.getText()));
        textFieldPanel.add(createPanelForFormattedTextField("Parsing Data Threads Input", 5, myDpThreads, tf -> myDpThreads = getIntFromTextField(tf)));
        textFieldPanel.add(createPanelForFormattedTextField("Requesting Data Threads Input", 5, myDrThreads, tf -> myDrThreads = getIntFromTextField(tf)));

        textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS));


        listOfButtons.add(createNewRadioButton("Filter to Query", "Query"));
        listOfButtons.add(createNewRadioButton("Filter Using Domain","Domain"));
        listOfButtons.add(createNewRadioButton("Complete URLs","Complete"));

        for(JRadioButton jrb : listOfButtons){
            jrb.setBackground(myBackground);
            jrb.setVerticalAlignment(SwingConstants.EAST);
            buttonPanel.add(jrb);
            myRadioButtons.add(jrb);
        }
        listOfButtons.get(0).setSelected(true);

        buttonPanel.setBackground(myBackground);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JPanel mainInputPanel = new JPanel();
        mainInputPanel.setLayout(new BorderLayout());
        mainInputPanel.add(buttonPanel, BorderLayout.EAST);
        mainInputPanel.add(textFieldPanel, BorderLayout.WEST);
        mainInputPanel.setBorder(border);
        mainInputPanel.setBackground(myBackground);
        return mainInputPanel;
    }

    private JRadioButton createNewRadioButton(final String name, final String actionCommand){
        JRadioButton newRadioButton = new JRadioButton(name);
        newRadioButton.setActionCommand(actionCommand);
        return newRadioButton;
    }

    private JPanel createPanelForFormattedTextField(final String label, final int columnSize, final int initialValue, final OnChangeEventListener eventListener){
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        JFormattedTextField textField = new JFormattedTextField(format);
        textField.setValue(initialValue);
        return createPanelForFormattedTextField(label, columnSize, textField, eventListener);
    }

    private JPanel createPanelForFormattedTextField(final String label, final int columnSize, final String initialValue, final OnChangeEventListener eventListener){
        JFormattedTextField textField = new JFormattedTextField(initialValue);
        textField.setValue(initialValue);
        return createPanelForFormattedTextField(label, columnSize, textField, eventListener);
    }

    private JPanel createPanelForFormattedTextField(final String label, final int columnSize, final JFormattedTextField textField,  final OnChangeEventListener eventListener) {
        JPanel panelWithFormattedTextField = new JPanel();
        DocumentListener docListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                eventListener.formattedInputChange(textField);

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                eventListener.formattedInputChange(textField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                eventListener.formattedInputChange(textField);
                System.out.println("Event Listener called");
            }
        };
        textField.getDocument().addDocumentListener(docListener);
        panelWithFormattedTextField.add(new Label(label));
        textField.setColumns(columnSize);
        panelWithFormattedTextField.add(textField);
        panelWithFormattedTextField.setBackground(myBackground);
        panelWithFormattedTextField.setLayout(new FlowLayout(FlowLayout.LEFT));
        return panelWithFormattedTextField;
    }



        private JList<String> setResultPanel(){
        myWebcrawlerListener = event -> {
            synchronized (myResultLock) {
                    resultDefaultList.addElement(event.getNewValue() + "\t" + event.getPropertyName());
            }
        };
        return new JList<>(resultDefaultList);
    }

    private void setUpWebCrawler(){
        myWc =  new WebCrawler(myMaxSites, myUrl, myDpThreads, myDrThreads, myWordSearch);
        myWc.addPropertyChangeListener(myWebcrawlerListener);
        setMySanitizer(myRadioButtons.getSelection().getActionCommand());
    }

    private int getIntFromTextField(JFormattedTextField siteTextField){
        if (siteTextField.getText().isEmpty()){
            return 0;
        }
        return Math.max(Integer.parseInt(siteTextField.getText()), 0);
    }

    private void controlWebCrawler(){
        startingWebcrawler();
        stopRunningWebCrawler();
        restartingWebcrawler();
    }

    private void startingWebcrawler(){
        myStartButton.addActionListener((e) -> {
            completeLabel.setVisible(false);
            try {
                if (myWc == null) {
                    setUpWebCrawler();
                }
                myWc.start();
                if(myWc.isRunning()) {
                    myStartButton.setVisible(false);
                    myStopButton.setVisible(true);
                    myRestartButton.setVisible(true);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void restartingWebcrawler(){
        myRestartButton.addActionListener((e) ->{
            if(myWc.isRunning()) {
                stopRunningWebCrawler();
            }
            resultDefaultList.clear();
            myWc.resetMyCounter();
            myStopButton.setVisible(false);
            myStartButton.doClick();
        });
    }


    private void stopRunningWebCrawler(){
        myStopButton.addActionListener((e) -> {
            try {
                myWc.stop();
                completeLabel.setVisible(true);
                    myStopButton.setVisible(false);
                    myStartButton.setVisible(true);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void setMySanitizer(String sanitizerType){
        if(sanitizerType.equals("Query")){
            myWc.setUrlSanitizer(new UpToQuerySanitizer());
        }else if(sanitizerType.equals("Domain")){
            myWc.setUrlSanitizer(new UpToDomainSanitizer());
        }else{
            myWc.setUrlSanitizer(new CompleteUrlSanitizer());
        }
    }

    @FunctionalInterface
    private interface OnChangeEventListener{
        void formattedInputChange(JFormattedTextField textField);
    }
}
