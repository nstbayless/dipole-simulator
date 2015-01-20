import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;


public class PhysComp extends JComponent{

	//coords of dipole:
	double dpx=400;
	double dpy=300;
	
	//velocity of dipole:
	float dvx = 0;
	float dvy = 0;
	
	//separation distance of dipole:
	float dp_s=36;
	
	//angle of the dipole, where dp=0 => negative on right, dp=pi/2 => negative below.
	float dp_ang=(float)(Math.PI/2);
	
	//angular velocity, in rad/s
	float dp_rv=0.0f;
	
	//coordinates of static charge:
	float chg_x = 200;
	float chg_y = 300;
	
	//radius of a charge (for graphics):
	float chg_r=16;
	
	//total charge product of the fixed charge and positive dipole:
	float q = 10000;
	//one over mass of dipole
	float im = 10000;
	//moment of inertia
	float ang_inertia = (float)(dp_s*dp_s/(im*12))*10000;
	
	//color of a positive charge:
	Color pos = Color.RED;
	
	//color of a negative charge:
	Color neg = Color.BLUE;
	
	//color of the dipole connector
	Color bar = Color.BLACK;
	
	Color bg = Color.WHITE;
	
	Color tex_col = Color.BLACK;
			
	String text = "Dipole Simulator. (c) Bayless 2015.";
	String instructions = "Left click to move charge, right click to move dipole. Have fun! *_*";
			
	float instruct_timer = 200;
	
	public PhysComp(){
		this.setPreferredSize(new Dimension(600,600));
	}
	
	public void init(){
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==1) {
					//set static charge location:
					chg_x=e.getX();
					chg_y=e.getY();
				}
				if (e.getButton()==3) {
					//set static charge location:
					dpx = e.getX();
					dpy = e.getY();
					dvx = 0;
					dvy = 0;
					dp_rv = 0;
					dp_ang = (float)(Math.random()*Math.PI*2);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(bg);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.setColor((q>0)?pos:neg);
		g.fillOval((int)(chg_x-chg_r/2), (int)(chg_y-chg_r/2),(int)(chg_r),(int)(chg_r));
		
		int dsx = (int)(Math.cos(dp_ang)*dp_s/2);
		int dsy = (int)(Math.sin(dp_ang)*dp_s/2);
		
		g.setColor(Color.BLACK);
		g.drawLine((int)(dpx-dsx), (int)(dpy-dsy), (int)(dpx+dsx), (int)(dpy+dsy));
		
		g.setColor(pos);
		g.fillOval((int)(dpx-dsx-chg_r/2), (int)(dpy-dsy-chg_r/2),(int)(chg_r),(int)(chg_r));
		
		g.setColor(neg);
		g.fillOval((int)(dpx+dsx-chg_r/2), (int)(dpy+dsy-chg_r/2),(int)(chg_r),(int)(chg_r));
		
		//draw text:
		
		g.setColor(tex_col);
		g.drawString(text, 0, this.getHeight()-4);
		if (instruct_timer>0) {
			g.setColor(new Color(0,0,0,Math.min(1,instruct_timer/100)));
			g.drawString(instructions, 0, 12);
			instruct_timer-=1;
		}
	}
	
	public void update(double dt) {
		//dipole s-vector
		double dsx = (Math.cos(dp_ang)*dp_s/2);
		double dsy = (Math.sin(dp_ang)*dp_s/2);
		
		//position of positive pole
		float dpos_x = (float)(dpx-dsx);
		float dpos_y = (float)(dpy-dsy);
		
		//position of negative pole
		float dneg_x = (float)(dpx+dsx);
		float dneg_y = (float)(dpy+dsy);
		
		//difference in coords of charge and positive pole
		float delp_x = dpos_x - chg_x;
		float delp_y = dpos_y - chg_y;
		
		//difference in coords of charge and negative pole
		float deln_x = dneg_x - chg_x;
		float deln_y = dneg_y - chg_y;
		
		//force felt by positive pole
		float dfp_x = (float) (q/(Math.pow(delp_x*delp_x+delp_y*delp_y,1.5))*delp_x);
		float dfp_y = (float) (q/(Math.pow(delp_x*delp_x+delp_y*delp_y,1.5))*delp_y);
		
		//force felt by negative pole
		float dfn_x = -(float) (q/(Math.pow(deln_x*deln_x+deln_y*deln_y,1.5))*deln_x);
		float dfn_y = -(float) (q/(Math.pow(deln_x*deln_x+deln_y*deln_y,1.5))*deln_y);
		
		//angle between charge and positive dipole.
		double ang_chg_to_dpp = Math.atan2(-delp_y, delp_x);
		
		//angle between charge and negative dipole.
		double ang_chg_to_dpn = Math.atan2(-deln_y, deln_x);
		
		double diffang_p = dp_ang + ang_chg_to_dpp;
		double diffang_n = - dp_ang - ang_chg_to_dpn;
		
		//torque of positive charge:
		float dpt_p = q*(float)(dp_s/2f*Math.sqrt(dfp_x*dfp_x+dfp_y*dfp_y)*Math.sin(diffang_p));
		float dpt_n = -q*(float)(dp_s/2f*Math.sqrt(dfn_x*dfn_x+dfn_y*dfn_y)*Math.sin(diffang_n));
	
		
		dp_rv+=dt*(dpt_p+dpt_n)/ang_inertia;
		
		dvx +=(dfp_x+dfn_x)*dt*im;
		dvy +=(dfp_y+dfn_y)*dt*im;
		
		//change in x coords this frame:
		double del_x = dvx*dt;
		double del_y = dvy*dt;
		
		dpx += del_x;
		dpy += del_y;
		
		dp_ang += dp_rv*dt;
	}
}
