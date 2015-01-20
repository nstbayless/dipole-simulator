import javax.swing.JFrame;


public class Driver {

	/**
	 * @param args
	 */
	
	static double FRAME_RATE=30;
	static double UPDATES_PER_FRAME=5000;
	static double TIME_DILATION = 0.1;
	public static void main(String[] args) {
		//set up rendering:
		JFrame jf = new JFrame();
		PhysComp pc = new PhysComp();
		jf.add(pc);
		jf.pack();
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
			for (int i=0;i<UPDATES_PER_FRAME;i++)
				pc.update(TIME_DILATION/FRAME_RATE/UPDATES_PER_FRAME);
			pc.repaint();
		}
	}
}
