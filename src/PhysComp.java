import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

//dipole simulator. (c) Bayless 2015.

public class PhysComp extends JComponent{
	
	//coords of dipole:
	double dipole_x=400;
	double dipole_y=300;
	
	//velocity of dipole:
	double dipole_vx = 0;
	double dipole_vy = 0;
	
	//separation distance of dipole:
	double dipole_sep=48;
	
	//angle of the dipole, where dp=0 => negative on right, dp=pi/2 => negative below.
	double dipole_theta=(float)(Math.PI/2);
	
	//angular velocity, in rad/s
	double dipole_omega=0.0f;
	
	//coordinates of static charge:
	double fixed_charge_x = 200;
	double fixed_charge_y = 300;
	
	//radius of a charge (for graphics):
	double chg_r=16;
	
	//total charge product of the fixed charge and positive dipole:
	double charge = 10000;
	//one over mass of dipole
	double inverse_mass = 10000;
	//moment of inertia
	double dipole_mass_moment = (float)(dipole_sep*dipole_sep/(inverse_mass*12))*10000;
	
	//color of a positive charge:
	static final Color COL_POS = Color.RED;
	
	//color of a negative charge:
	static final Color COL_NEG = Color.BLUE;
	
	//color of the dipole connector
	static final Color bar = Color.BLACK;
	
	static final Color bg = Color.WHITE;
	
	static final Color COL_TEXT = Color.BLACK;
			
	static final String text = "Dipole Simulator. (c) 2015.";
	static final String[] instructions =
		{"Left click to move charge, right click to move dipole.",
		 "Use up and down to grow/shrink the separation distance.",
		 "Use left and right to decrease/increase the speed of the simulation.",
		 "Use A and S to decrease/increase the precision of estimation.",
		 "Have fun! *_*"};
			
	float instruct_timer_max = 350;
	float instruct_timer = instruct_timer_max;
	float time_per_instruction = instruct_timer_max/instructions.length;
	
	//time to display updates per frame or simulation speed:
	float attribute_timer_max=100;
	float attribute_timer=0;
	enum attribute_type{
		NONE,
		ITERATIONS,
		SIMULATION_SPEED
	} attribute_type attribute = attribute_type.NONE;
	
	
	public PhysComp(){
		this.setPreferredSize(new Dimension(600,600));
	}
	
	//modify this to change precision of calculation.
	double updates_per_frame=50000;
	
	//modify this to change the simulation speed
	double time_dilation = 0.1;
	
	public void update(double dt){
		for (int i=0;i<updates_per_frame;i++)
		update_m(dt*time_dilation/updates_per_frame);
	}
	
	public void init(){
		Container top = this.getParent();
		while (top.getParent()!=null)
			top=top.getParent();
		top.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_UP)
					dipole_sep*=1.2;
				if (e.getKeyCode()==KeyEvent.VK_DOWN)
					dipole_sep/=1.2;
				if (e.getKeyCode()==KeyEvent.VK_LEFT){
					time_dilation/=1.2;
					attribute_timer=attribute_timer_max;
					attribute=attribute_type.SIMULATION_SPEED;
				}
				if (e.getKeyCode()==KeyEvent.VK_RIGHT){
					time_dilation*=1.2;
					attribute_timer=attribute_timer_max;
					attribute=attribute_type.SIMULATION_SPEED;
				}
				if (e.getKeyCode()==KeyEvent.VK_S) {
					updates_per_frame*=2;
					attribute_timer=attribute_timer_max;
					attribute=attribute_type.ITERATIONS;
				}
				if (e.getKeyCode()==KeyEvent.VK_A&&updates_per_frame>2) {
					updates_per_frame/=2;
					attribute_timer=attribute_timer_max;
					attribute=attribute_type.ITERATIONS;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}});
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==1) {
					//set static charge location:
					fixed_charge_x=e.getX();
					fixed_charge_y=e.getY();
				}
				if (e.getButton()==3) {
					//set static charge location:
					dipole_x = e.getX();
					dipole_y = e.getY();
					dipole_vx = 0;
					dipole_vy = 0;
					dipole_omega = 0;
					dipole_theta = (float)(Math.random()*Math.PI*2);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
		});
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(bg);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//draw static charge:
		g.setColor((charge>0)?COL_POS:COL_NEG);
		g.fillOval((int)(fixed_charge_x-chg_r/2), (int)(fixed_charge_y-chg_r/2),(int)(chg_r),(int)(chg_r));
		
		//determine coordinates of dipole charges:
		int dsx = (int)(Math.cos(dipole_theta)*dipole_sep/2);
		int dsy = (int)(Math.sin(dipole_theta)*dipole_sep/2);
		
		//draw bar connecting dipole:
		g.setColor(Color.BLACK);
		g.drawLine((int)(dipole_x-dsx), (int)(dipole_y-dsy), (int)(dipole_x+dsx), (int)(dipole_y+dsy));
		
		//draw positive end of dipole:
		g.setColor(COL_POS);
		g.fillOval((int)(dipole_x-dsx-chg_r/2), (int)(dipole_y-dsy-chg_r/2),(int)(chg_r),(int)(chg_r));
		
		//draw negative end of dipole:
		g.setColor(COL_NEG);
		g.fillOval((int)(dipole_x+dsx-chg_r/2), (int)(dipole_y+dsy-chg_r/2),(int)(chg_r),(int)(chg_r));
		
		//draw text:
		
		g.setColor(COL_TEXT);
		g.drawString(text, 0, this.getHeight()-4);
		if (instruct_timer>0) {
			//fadeout:
			g.setColor(new Color(0,0,0,Math.min(1,2*(instruct_timer%time_per_instruction)/time_per_instruction)));
			int message_index = (int)(instructions.length-instruct_timer/time_per_instruction);
			g.drawString(instructions[message_index], 0,
					//bounce:
					(int)(12+16/(time_per_instruction-(instruct_timer%time_per_instruction))));
			instruct_timer-=1;
		}
		
		if (attribute_timer>0){
			g.setColor(new Color(0,0,0,Math.min(1,2*attribute_timer/attribute_timer_max)));
			String message = "~";
			if (attribute == attribute_type.ITERATIONS)
				message = "" + (int)updates_per_frame + " iterations";
			if (attribute == attribute_type.SIMULATION_SPEED)
				message = "" + String.format((time_dilation>1)?"%.0f":("%." + (int)(-1*Math.log10(time_dilation)+1)+ "f"),
						time_dilation*10) + "x speed";
			g.drawString(message, this.getWidth()-8*message.length()+((attribute == attribute_type.ITERATIONS)?16:2),
					//bounce:
					(int)(12+16/(attribute_timer_max-attribute_timer)));
			attribute_timer-=1;
		}
	}
	
	private void update_m(double dt) {
		//dipole s-vector
		double dipole_s_vec_x = (Math.cos(dipole_theta)*dipole_sep/2);
		double dipole_s_vec_y = (Math.sin(dipole_theta)*dipole_sep/2);
		
		//position of positive pole
		double pole_pos_x = (double)(dipole_x-dipole_s_vec_x);
		double pole_pos_y = (double)(dipole_y-dipole_s_vec_y);
		
		//position of negative pole
		double pole_neg_x = (double)(dipole_x+dipole_s_vec_x);
		double pole_neg_y = (double)(dipole_y+dipole_s_vec_y);
		
		//difference in coords of charge and positive pole
		double delta_x_pos = pole_pos_x - fixed_charge_x;
		double delta_y_pos = pole_pos_y - fixed_charge_y;
		
		//difference in coords of charge and negative pole
		double delta_x_neg = pole_neg_x - fixed_charge_x;
		double delta_y_neg = pole_neg_y - fixed_charge_y;
		
		//inverse r from fixed charge to positive pole.
		double inv_r_pos = 1f/(double)Math.sqrt(delta_x_pos*delta_x_pos+delta_y_pos*delta_y_pos);
		double inv_r_neg = 1f/(double)Math.sqrt(delta_x_neg*delta_x_neg+delta_y_neg*delta_y_neg);
		
		//force felt by positive pole
		double pole_pos_force_x = (double) (charge*inv_r_pos*inv_r_pos*inv_r_pos*delta_x_pos);
		double pole_pos_force_y = (double) (charge*inv_r_pos*inv_r_pos*inv_r_pos*delta_y_pos);
		
		//force felt by negative pole
		double pole_neg_force_x = -(double) (charge*inv_r_neg*inv_r_neg*inv_r_neg*delta_x_neg);
		double pole_neg_force_y = -(double) (charge*inv_r_neg*inv_r_neg*inv_r_neg*delta_y_neg);
		
		//angle between charge and positive dipole.
		double ang_chg_to_dpp = Math.atan2(-delta_y_pos, delta_x_pos);
		
		//angle between charge and negative dipole.
		double ang_chg_to_dpn = Math.atan2(-delta_y_neg, delta_x_neg);
		
		double diffang_p = dipole_theta + ang_chg_to_dpp;
		double diffang_n = - dipole_theta - ang_chg_to_dpn;
		
		//torque of positive charge:
		double dpt_p = charge*(float)(dipole_sep/2f*Math.sqrt(pole_pos_force_x*pole_pos_force_x+pole_pos_force_y*pole_pos_force_y)*Math.sin(diffang_p));
		double dpt_n = -charge*(float)(dipole_sep/2f*Math.sqrt(pole_neg_force_x*pole_neg_force_x+pole_neg_force_y*pole_neg_force_y)*Math.sin(diffang_n));

		dipole_omega+=dt*(dpt_p+dpt_n)/dipole_mass_moment;
		
		dipole_vx +=(pole_pos_force_x+pole_neg_force_x)*dt*inverse_mass;
		dipole_vy +=(pole_pos_force_y+pole_neg_force_y)*dt*inverse_mass;
		
		//change in x coords this frame:
		double delta_x = dipole_vx*dt;
		double delta_y = dipole_vy*dt;
		
		dipole_x += delta_x;
		dipole_y += delta_y;
		
		dipole_theta += dipole_omega*dt;
	}
}
