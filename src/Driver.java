import javax.swing.JFrame;

public class Driver {

	/**
	 * @param args
	 */
	
	//frames per second:
	static double FRAME_RATE=30;
	public static void main(String[] args) {
		//set up rendering:
		JFrame jf = new JFrame();
		PhysComp pc = new PhysComp();
		jf.add(pc);
		jf.pack();
		jf.setLocation(128,128);
		jf.setVisible(true);
		pc.init();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		while (true){
			//update loop:
			try {
				Thread.sleep((int)(1000.0/FRAME_RATE));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pc.update(1.0/FRAME_RATE);
			pc.repaint();
		}
	}
}
