
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import processing.core.*;

import com.hookedup.led.*;
import com.hookedup.processing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;


public class ControllerTestApp extends BaseSwingFrameApp {
	
	// --- added for matrix
	int MATRIX_COLS = 5;
	int MATRIX_ROWS = 10;
	LEDMatrix matrix;
	private JPanel contentPane;
	MyExtraWindow win;

	
	void loadDefaultMatrix(){
		String tmpResult = matrix.loadMatrixFile("/Users/alistaircolling/default.xml");
		if (tmpResult.equals("")){
//			System.out.println("File Loaded.");
			return;
		}
		System.out.println(tmpResult);
	}
	
	void setupExtraWindow(){
		win = new MyExtraWindow(proc, "Matrix Setup", 500, 300);
		
		win.setVisible(false);
		
	}
	
	void matrixSetup() {
		matrix = new LEDMatrix(MATRIX_COLS, MATRIX_ROWS, 24, 24, 1);
		loadDefaultMatrix();
		// -- TO CONNECT --->>> 
		matrix.connectToController();
		
		matrix.refresh();
		matrix.emulatorDelay = 20;
		matrix.ui.setLocation(this.getLocation().x + this.getWidth(),
           this.getLocation().y);
		matrix.ui.setVisible(true);

	}

	void sweepWidth() {
		for (int j = 0; j < matrix.cols(); j++) {
			matrix.clear();
			matrix.drawLine(j, 0, j, matrix.rows() - 1, 255, 255, 255);
			matrix.refresh();
		}
	}

	void sweepHeight() {
		for (int j = 0; j < matrix.rows(); j++) {
			matrix.clear();
			matrix.drawLine(0, j, matrix.cols() - 1, j, 255, 255, 255);
			matrix.refresh();
		}
	}

	int constrain(int theValue, int theMin, int theMax){
		int retVal = theValue;
		if (retVal < theMin){
			retVal = theMin;
		} else if (retVal > theMax) {
			retVal = theMax;
		}
		return retVal;
	}
	

	// --- overrides the set process event to include loading minim from the
	// base
	public void setProc(PApplet theproc) {
		super.setProc(theproc);
	}


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// AppSoundDemoSwing frame = new AppSoundDemoSwing();
					// frame.setVisible(true);
					
//					ProcessingAppLauncher procLaunch = new ProcessingAppLauncher();
					//NOTE: Using Minim version
					ProcessingAppLauncherMinim procLaunch = new ProcessingAppLauncherMinim();
					procLaunch.launch("ControllerTestApp");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	void allOff(){
		matrix.allTo(0, 0,0);
		matrix.refresh();
	}
	void allWhite(){
		matrix.allTo(255, 255,255);
		matrix.refresh();
	}
	
	void testRGB(){
		
		try {
			matrix.allTo(255, 0,0);
			matrix.refresh();
			Thread.sleep(1300);
			matrix.allTo(0, 255,0);
			matrix.refresh();
			Thread.sleep(1300);
			matrix.allTo(0, 0,255);
			matrix.refresh();
			Thread.sleep(1300);
			matrix.allTo(255, 255,255);
			matrix.refresh();
			Thread.sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	int iHue = 0;
	
	int incrToCount( int val, int amt, int maxAmt){
		  val += amt;

		  if( val > maxAmt - 1 )
		    val -= maxAmt;

		  if( val < 0 )
		    val += maxAmt;

		  return val;  
		}

		int increment(int val, int amt){
		  return incrToCount(val,amt,256);
		}

		int incrToMax(int val, int amt, int maxAmt){
		  val += amt;

		  if( val > maxAmt )
		    val = maxAmt;

		  if( val < 0 )
		    val = 0;

		  return val;  
		}


    /// <summary>
    /// Convert HSV to RGB
    /// h is from 0-360
    /// s,v values are 0-1
    /// r,g,b values are 0-255
    /// Based upon http://ilab.usc.edu/wiki/index.php/HSV_And_H2SV_Color_Space#HSV_Transformation_C_.2F_C.2B.2B_Code_2
    /// </summary>
    public void HsvToRgb(double h, double S, double V, SmartRGB rgb)
    {
        // ######################################################################
        // T. Nathan Mundhenk
        // mundhenk@usc.edu
        // C/C++ Macro HSV to RGB

        double H = h;
        while (H < 0) { H += 360; };
        while (H >= 360) { H -= 360; };
        double R, G, B;
        if (V <= 0)
        { R = G = B = 0; }
        else if (S <= 0)
        {
            R = G = B = V;
        }
        else
        {
            double hf = H / 60.0;
            int i = (int)Math.floor(hf);
            double f = hf - i;
            double pv = V * (1 - S);
            double qv = V * (1 - S * f);
            double tv = V * (1 - S * (1 - f));
            switch (i)
            {

                // Red is the dominant color

                case 0:
                    R = V;
                    G = tv;
                    B = pv;
                    break;

                // Green is the dominant color

                case 1:
                    R = qv;
                    G = V;
                    B = pv;
                    break;
                case 2:
                    R = pv;
                    G = V;
                    B = tv;
                    break;

                // Blue is the dominant color

                case 3:
                    R = pv;
                    G = qv;
                    B = V;
                    break;
                case 4:
                    R = tv;
                    G = pv;
                    B = V;
                    break;

                // Red is the dominant color

                case 5:
                    R = V;
                    G = pv;
                    B = qv;
                    break;

                // Just in case we overshoot on our math by a little, we put these here. Since its a switch it won't slow us down at all to put these here.

                case 6:
                    R = V;
                    G = tv;
                    B = pv;
                    break;
                case -1:
                    R = V;
                    G = pv;
                    B = qv;
                    break;

                // The color is not defined, we should throw an error.

                default:
                    //LFATAL("i Value error in Pixel conversion, Value is %d", i);
                    R = G = B = V; // Just pretend its black/white
                    break;
            }
        }
        rgb.r = Clamp((int)(R * 255.0));
        rgb.g = Clamp((int)(G * 255.0));
        rgb.b = Clamp((int)(B * 255.0));
    }

    /// <summary>
    /// Clamp a value to 0-255
    /// </summary>
    int Clamp(int i)
    {
        if (i < 0) return 0;
        if (i > 255) return 255;
        return i;
    }

    
	int incrAmt = 75;
	private JTextField txtImageFilename;

	void runDemo1() {
	
	}

	void scrollGraphicDemo() {
		  PImage myImage;
	      myImage = proc.loadImage(txtImageFilename.getText());
		  proc.colorMode(proc.RGB,1);
		  proc.colorMode(proc.HSB,1);
		  for ( int i = 0 ; i > 0-myImage.width  ; i--) {
		    win.image(myImage, i, 0);
		    loadFromCanvas();
		    proc.delay(50);
		  }
		}

	void loadFromCanvas() {
		  for (int iH = 0 ; iH < matrix.rows() ; iH++) {
		    for (int iW = 0 ; iW < matrix.cols() ; iW++) {
		      int cp = win.get( (iW), (iH));    
		      matrix.setRGB(iW, iH, (int)(proc.red(cp)*255), (int)(proc.green(cp)*255), (int)(proc.blue(cp)*255));
		    }
		  }
		  matrix.refresh();
	}

	/**
	 * Create the frame.
	 */
	public ControllerTestApp() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				matrix.end();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
				System.exit(0);
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnDemo = new JButton("Demo 1");
		btnDemo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runDemo1();
			}
		});
		btnDemo.setBounds(10, 11, 89, 23);
		contentPane.add(btnDemo);
		
		JButton btnAllwhite = new JButton("All White");
		btnAllwhite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allWhite();
			}
		});
		btnAllwhite.setBounds(109, 11, 96, 23);
		contentPane.add(btnAllwhite);
		
		JButton btnAllOff = new JButton("All Off");
		btnAllOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allOff();
			}
		});
		btnAllOff.setBounds(214, 11, 89, 23);
		contentPane.add(btnAllOff);
		
		JButton btnTestRgb = new JButton("Test RGB");
		btnTestRgb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testRGB();
			}
		});
		btnTestRgb.setBounds(109, 45, 89, 23);
		contentPane.add(btnTestRgb);
		
		JButton btnScrollImage = new JButton("Scroll Image");
		btnScrollImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scrollGraphicDemo();
			}
		});
		btnScrollImage.setBounds(10, 107, 149, 23);
		contentPane.add(btnScrollImage);
		
		txtImageFilename = new JTextField();
		txtImageFilename.setText("c:\\data\\test002.jpg");
		txtImageFilename.setBounds(10, 137, 236, 20);
		contentPane.add(txtImageFilename);
		txtImageFilename.setColumns(10);
		
		matrixSetup();
		//setupExtraWindow();
	

}


class SmartRGB
{
	public int r;
	public int g;
	public int b;
}
}