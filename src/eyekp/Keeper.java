package eyekp;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

@SuppressWarnings("serial")
public class Keeper extends JDialog {
	private static boolean debug = false;
	private static final Dimension scDim = Toolkit.getDefaultToolkit().getScreenSize();
    private JLabel label = null;
    private boolean breaking = false;
    private int rest_time = 0;
    private static final int fontsize = 80;
    
    public Keeper(int rest_time) {
    	
//      this.getContentPane().setBackground(Color.black);
    	this.rest_time = rest_time;
        this.setSize(scDim);
        JPanel pane = (JPanel)this.getContentPane();
        pane.setBackground(Color.black);
        pane.setLayout(null);
        JLabel slb = new JLabel("Publish or Perish!");
        Font slbFont = slb.getFont();
        slb.setFont(new Font(slbFont.getName(), Font.BOLD, fontsize));
        Dimension ssize = slb.getPreferredSize();
        slb.setForeground(Color.WHITE);
        pane.add(slb);
        slb.setBounds((scDim.width-ssize.width)/2, (scDim.height-ssize.height)/2 - fontsize, ssize.width, ssize.height);
        label = new JLabel();
//      label.setForeground(Color.white);
		label.setText(this.formatLabel(rest_time));
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), Font.BOLD, fontsize));
        Dimension size = label.getPreferredSize();
        pane.add(label);
        label.setBounds((scDim.width-size.width)/2, (scDim.height-size.height)/2, size.width, size.height);
        this.setUndecorated(true);
        this.setVisible(false);
    }

    public static void main(String args[]){
    	
    	int rest_time = 4000;
    	int work_time = 3000;
    	if(!debug) {
    		rest_time = Integer.parseInt(args[1]) * 60 * 1000;
    		work_time = Integer.parseInt(args[0]) * 60 * 1000;
    	}
    	Keeper kp = new Keeper(rest_time);
    	ActionListener timerListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		kp.label.setText(kp.formatLabel(kp.rest_time));
        		kp.rest_time-=1000;
        		if(kp.rest_time < 0) {
        			kp.breaking = false;
        		}
        	}
        };
        Timer labelTimer = new Timer(1000, timerListener);
    	try {
    		while(true) {
    			if(!kp.breaking) {
    				kp.rest_time = rest_time;
    				kp.label.setText(kp.formatLabel(rest_time + 1000));
    				labelTimer.stop();
    				kp.setVisible(false);
    				Thread.sleep(work_time); // work 45 minutes
//    				Thread.sleep(3000);
    				kp.breaking = true;
    				System.out.println("Break at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    			}else {
    				int x = MouseInfo.getPointerInfo().getLocation().x;
    				
	    			if(x>scDim.width/2) {
	    				kp.moveMouse(new Point(0, 0));
	    			}
	    			kp.setLocation(new Point(0, 0));
	    			
	    			labelTimer.start();
	    			kp.setVisible(true);
	    			kp.setBackground(Color.black);
//	    			kp.label.setForeground(Color.white);
	    			kp.setAlwaysOnTop(true);
	    		}
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public String formatLabel(int rest_time) {
    	String time_min = Integer.toString((rest_time-1) / 60000);
		String time_sec = Integer.toString(((rest_time-1) % 60000)/1000);
		if(time_min.length()<2) {
			time_min = "0"+time_min;
		}
		if(time_sec.length()<2) {
			time_sec = "0"+time_sec;
		}
		return time_min + ":" + time_sec;
    }
    
    public void moveMouse(Point p) {
        GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device: gs) { 
            GraphicsConfiguration[] configurations =
                device.getConfigurations();
            for (GraphicsConfiguration config: configurations) {
                Rectangle bounds = config.getBounds();
                System.out.println(bounds.toString());
                if(!bounds.contains(p)) {
                    // Set point to screen coordinates.
                    Point b = bounds.getLocation(); 
                    Point s = new Point(p.x - b.x, p.y - b.y);

                    try {
                        Robot r = new Robot(device);
                        
                        r.mouseMove(s.x, s.y);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
        }
        // Couldn't move to the point, it may be off screen.
        return;
    }
    }