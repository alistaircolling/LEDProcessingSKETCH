
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import processing.core.*;
import com.hookedup.processing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class BaseSwingFrameApp extends JFrame implements IProcessingApp {

	private JPanel contentPane;
	PApplet proc;
	
	//--- This function is called by the launcher when it opens the window
	//  this provides a handle to the processing applet, needed for most libraries
	public void setProc(PApplet theproc){
		proc = theproc;
	}
	
	public void showApp(){
		this.setVisible(true);
	}

	void runDemo1(){
		if(proc==null){
			System.out.print("NULL");
		} else {
			System.out.print("ACTIVE");
		}
	
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProcessingAppLauncher  procLaunch =  new ProcessingAppLauncher();
					procLaunch.launch("BaseSwingFrameApp");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void systemExit(){
		System.exit(0);
	}
	
	/**
	 * Create the frame.
	 */
	public BaseSwingFrameApp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnDemo = new JButton("Demo1");
		btnDemo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runDemo1();
			}
		});
		btnDemo.setBounds(10, 11, 89, 23);
		contentPane.add(btnDemo);
	}

}
