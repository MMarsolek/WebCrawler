
public class ThreadControl {
	Thread one = new Thread() {
		public void run() {			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
					System.out.println(e);
			}
		}	
		
	};
}
