package webcrawler.gui;

public class GUI {
    public  GUI() throws InterruptedException {
        MyFrame myFrame = new MyFrame();

        myFrame.start();
    }

    public static void main(String[] args) {
        try {
            GUI gui = new GUI();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
