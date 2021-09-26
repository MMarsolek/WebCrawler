package webcrawler.gui;

import webcrawler.CompleteUrlSanitizer;
import webcrawler.UpToDomainSanitizer;
import webcrawler.UpToQuerySanitizer;
import webcrawler.WebCrawler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;



public class MyFrame extends JFrame {

    private  int myMaxThreads;
    private  int myMaxSites;
    private  String myUrl;
    private WebCrawler myWc;
    private final Color myBackground;
    private ButtonGroup myRadioButtons;
    private JFormattedTextField threadTextField;
    private JFormattedTextField urlTextField;
    private JFormattedTextField maxSiteTextField;
    private PropertyChangeListener myWebcrawlerListener;
    private final Object myResultLock;
    private JButton myStartButton;
    private JButton myStopButton;
    private JButton myRestartButton;
    private JPanel myResultPanel;
    private DefaultListModel<String> resultDefaultList;
    private final int width;
    private final int height;


    public MyFrame(){
        myMaxThreads = 10;
        myMaxSites = 10;
        myUrl = "https://www.Google.com";
        myBackground = new Color(0xFFFAFAFA);
        myResultLock = new Object();
        resultDefaultList = new DefaultListModel<>();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = screenSize.width;
        height = screenSize.height;

    }
    public void start() {

        this.setPreferredSize(new Dimension(width/3,height/2));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Web Crawler");
        JPanel inputSection = createInputSections();
        inputSection.setPreferredSize(new Dimension((height/10),(width/12)));
        this.add(inputSection, BorderLayout.NORTH);

        inputSection.setBackground(myBackground);
        this.pack();
        this.setVisible(true);

        JPanel controlButtons = new JPanel();
        myStartButton = new JButton("Start");
        controlButtons.add(myStartButton);

        myStopButton = new JButton("Stop");
        myStopButton.setVisible(false);
        controlButtons.add(myStopButton);

        myRestartButton = new JButton("Restart");
        myRestartButton.setVisible(false);
        controlButtons.add(myRestartButton);

        inputSection.add(controlButtons);

        controlWebCrawler();
        myResultPanel = new JPanel();
        myResultPanel.add(setResultPanel());

        this.add(new JScrollPane(setResultPanel()));
        this.getRootPane().setBorder(new EmptyBorder(5,5,5,5));
    }


    private JPanel createInputSections(){
        JPanel mainPanel = new JPanel();
        JPanel thread = new JPanel();
        JPanel maxSite = new JPanel();
        JPanel startUrl = new JPanel();
        myRadioButtons = new ButtonGroup();

        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);

        JLabel l = new JLabel("Sites to Visit Input");
        maxSite.add(l);
        maxSiteTextField = new JFormattedTextField(format);
        maxSiteTextField.setColumns(5);
        maxSiteTextField.setValue(myMaxSites);
        maxSite.add(maxSiteTextField);

        JLabel l2 = new JLabel("URL Input");
        startUrl.add(l2);
        urlTextField = new JFormattedTextField(myUrl);
        urlTextField.setColumns(15);
        startUrl.add(urlTextField);

        JLabel l3 = new JLabel("Thread Input");
        thread.add(l3);
        threadTextField = new JFormattedTextField(format);
        threadTextField.setValue(myMaxThreads);
        threadTextField.setColumns(5);
        thread.add(threadTextField);


        mainPanel.add(maxSite, BorderLayout.NORTH);
        mainPanel.add(startUrl, BorderLayout.NORTH);
        mainPanel.add(thread, BorderLayout.NORTH);

        maxSite.setBackground(myBackground);
        startUrl.setBackground(myBackground);
        thread.setBackground(myBackground);

        JRadioButton a = new JRadioButton("Filter to Query");
        a.setActionCommand("Query");
        JRadioButton b = new JRadioButton("Filter Using Domain");
        b.setActionCommand("Domain");
        JRadioButton c = new JRadioButton("Complete URLs");
        c.setActionCommand("Complete");



        a.setBackground(myBackground);
        b.setBackground(myBackground);
        c.setBackground(myBackground);


        myRadioButtons.add(a);
        myRadioButtons.add(b);
        myRadioButtons.add(c);
        a.setSelected(true);



        mainPanel.add(a, BorderLayout.CENTER);
        mainPanel.add(b, BorderLayout.CENTER);
        mainPanel.add(c, BorderLayout.CENTER);
        Border border = BorderFactory.createLineBorder(Color.black, 1);
        mainPanel.setLayout(new FlowLayout());
        mainPanel.setBorder(border);
        return mainPanel;
    }

    private JList<String> setResultPanel(){
        myWebcrawlerListener = event -> {
            synchronized (myResultLock) {
                    resultDefaultList.addElement(event.getPropertyName());
            }
        };
        return new JList<>(resultDefaultList);
    }

//    private void clearResultPanel(){
//        resultDefaultList = new DefaultListModel<>();
//        resultDefaultList.clear();
//        myResultPanel.removeAll();
//    }

    private void setUpWebCrawler(){
        setStartUrl(urlTextField);
        setNumberOfThreads(threadTextField);
        setNumberOfSites(maxSiteTextField);
        myWc =  new WebCrawler(myMaxSites, myUrl, myMaxThreads);
        myWc.addPropertyChangeListener(myWebcrawlerListener);

        setMySanitizer(myRadioButtons.getSelection().getActionCommand());
        System.out.println("webcrawler.WebCrawler Created");
    }

    private void setStartUrl(JFormattedTextField urlTextField){
        myUrl = urlTextField.getText();
        System.out.println("Url Set");
    }

    private void setNumberOfSites(JFormattedTextField siteTextField){
        int numOfSites = Integer.parseInt(siteTextField.getText());
        if (numOfSites > 0){
            myMaxSites = numOfSites;
        }
        System.out.println("My max sites " + myMaxSites);
    }

    private void setNumberOfThreads(JFormattedTextField threadTextField) {

            int maxThreads = Integer.parseInt(threadTextField.getText());
            if (maxThreads > 0) {
                myMaxThreads = maxThreads;
            }
            System.out.println("Threads Set to " + myMaxThreads);

    }


    private void controlWebCrawler(){
        startingWebcrawler();
        stopRunningWebCrawler();
        restartingWebcrawler();

    }

    private void startingWebcrawler(){
        myStartButton.addActionListener((e) -> {
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
                System.out.println("webcrawler.WebCrawler Running");

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
//        if(!myWc.isRunning()&& !myWc.equals(null)){
//            myStopButton.setVisible(false);
//        }
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
            System.out.println("Restart event activated");
        });
    }


    private void stopRunningWebCrawler(){
        myStopButton.addActionListener((e) -> {
            try {
                myWc.stop();
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
}
