package webcrawler;

public class Counter {
    private int myCounter;

    public Counter(int starting){
        myCounter = starting;
    }
    public Counter(){
        this(0);
    }

    public int getCount(){
        return myCounter;
    }

    public void increment(){
        myCounter++;
    }
}
