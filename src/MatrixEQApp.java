import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.util.Timer;
import java.util.TimerTask;

import processing.core.PApplet;

import com.hookedup.led.LEDMatrix;
import com.hookedup.processing.EQLevels;
import com.hookedup.processing.ExtraWindow;
import com.hookedup.processing.ProcessingAppLauncherMinim;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;

public class MatrixEQApp extends BaseSwingFrameApp {
	Minim minim;
	AudioPlayer song;
	EQTask eqRunner = new EQTask();

	Timer timer;

	//ExtraWindow win;
	MyExtraWindow win;

	// --- music fun
	EQLevels eq;
	int lowEQColBuffer = 4;
	int highEQColBuffer = 4;

	float eqDrawFactor = (float) .06;
	float lowEQDrawFactor = (float) .02;
	float highEQDrawFactor = (float) .35;

	int eqFallDelayPos = 0;
	int eqFallDelayAt = 12;
	int musicTop = 400;

	float eqInputAdj = (float) .00;
	int eqLeftOffset = 1;
	int eqRightOffset = 3;

	float colorEQBrt = (float) 1;
	float colorEQBackBrt = (float) .1;

	int eqPos[];

	// --- added for matrix
	int MATRIX_COLS = 14;
	int MATRIX_ROWS = 7;
	LEDMatrix matrix;

	void loadDefaultMatrix() {
		String tmpResult = matrix.loadMatrixFile("c:/matrix/setup/default.xml");
		if (tmpResult.equals("")) {
			// System.out.println("File Loaded.");
			return;
		}
		System.out.println(tmpResult);
	}

	void matrixSetup() {
		matrix = new LEDMatrix(MATRIX_COLS, MATRIX_ROWS, 24, 24, 1);
		loadDefaultMatrix();

		// -- TO CONNECT --->>>
		 matrix.connectToController();

		matrix.refresh();
		matrix.emulatorDelay = 20;
		matrix.ui.setLocation(this.getLocation().x + this.getWidth(), this
				.getLocation().y);

		matrix.ui.setVisible(true);

		eqPos = new int[200];
		
		setupEQRunner();

	}

	void sweepWidth() {
		for (int j = 0; j < matrix.cols(); j++) {
			matrix.clear();
			matrix.drawLine(j, 0, j, matrix.rows() - 1, 255, 255, 255);
			matrix.refresh();
			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	void sweepHeight() {
		for (int j = 0; j < matrix.rows(); j++) {
			matrix.clear();
			matrix.drawLine(0, j, matrix.cols() - 1, j, 255, 255, 255);
			matrix.refresh();
			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	int constrain(int theValue, int theMin, int theMax) {
		int retVal = theValue;
		if (retVal < theMin) {
			retVal = theMin;
		} else if (retVal > theMax) {
			retVal = theMax;
		}
		return retVal;
	}

	void incrFallDelay() {
		eqFallDelayPos++;
		if (eqFallDelayPos > eqFallDelayAt) {
			eqFallDelayPos = 0;
		}
	}

	void loadFromCanvas() {
		for (int iH = 0; iH < matrix.rows(); iH++) {
			for (int iW = 0; iW < matrix.cols(); iW++) {
				int cp = win.get((iW), (iH));
				int rr = (int) proc.red(cp);
				int red = (int) proc.red(cp) ;
				int green = (int) proc.green(cp);
				int blue = (int) proc.blue(cp) ;
				
			//	win.logIt(iW+":"+iH+"red:"+rr+" green:"+green);
			//	matrix.setRGB(iW, iH, (int) (proc.red(cp) * 255), (int) (proc
				//		.green(cp) * 255), (int) (proc.blue(cp) * 255));
				matrix.setRGB(iW, iH, red, green, blue);
			}
		}
		matrix.refresh();
	}

	void setupExtraWindow() {
		//win = new ExtraWindow(proc, "Matrix Setup", 0, 0, 500, 300);
		win = new MyExtraWindow(proc, "Matrix Setup", 500, 300);
		win.setVisible(false);
		
		matrixSetup();
	}

	void matrixEQ() {
		MATRIX_ROWS = matrix.rows();
		MATRIX_COLS = matrix.cols();

		incrFallDelay();
		matrix.clear();
		eq.fft.forward(eq.song.mix);
		int w = (int) (this.getWidth() / eq.fft.avgSize());
		float tmpEach = (((float) 1) / matrix.cols());
		int tmpTop = matrix.cols() - 1;

		for (int i = 0; i < matrix.cols(); i++) {
			// draw a rectangle for each average, multiply the value by 5 so we
			// can see it better
			float tColor = i == 0 ? 0 : (tmpEach * (float) i);
			int tmpc = Color.HSBtoRGB(tColor, 1, 1);
			Color tmpColor = new Color(tmpc);
			int tmpBack = Color.HSBtoRGB(tColor, 1, colorEQBackBrt);
			Color tmpBackColor = new Color(tmpBack);

			float matrixVal = eq.fft.getAvg(i);
			float tmpDrawFactor = eqDrawFactor;

			if (i >= MATRIX_COLS - highEQColBuffer - 1) {
				tmpDrawFactor = highEQDrawFactor;
			} else if (i <= lowEQColBuffer) {
				tmpDrawFactor = lowEQDrawFactor;
			}

			int tmpPos = constrain(
					(int) ((matrixVal * MATRIX_ROWS) * tmpDrawFactor), 0,
					MATRIX_ROWS - 1);
			eqPos[i] = Math.max(tmpPos, eqPos[i]);

			int r = tmpColor.getRed();
			int g = tmpColor.getGreen();
			int b = tmpColor.getBlue();

			int rb = tmpBackColor.getRed();
			int gb = tmpBackColor.getGreen();
			int bb = tmpBackColor.getBlue();

			matrix.drawLine(i, 0, i, MATRIX_ROWS - 1, rb, gb, bb);
			matrix.drawLine(i, MATRIX_ROWS - 1 - tmpPos, i, MATRIX_ROWS - 1, r,
					g, b);
			matrix.setRGB(i, MATRIX_ROWS - 1 - tmpPos, 150, 150, 150);
			matrix.setRGB(i, MATRIX_ROWS - 1 - eqPos[i], 255, 255, 255);
			if (eqPos[i] > 300) {
				if (MATRIX_ROWS - 1 - eqPos[i] - 1 > 0) {
					tmpBack = java.awt.Color.HSBtoRGB(tColor, (float) .4,
							colorEQBackBrt * 2);
					tmpBackColor = new Color(tmpBack);
					rb = tmpBackColor.getRed(); // (int)red(tmpBackColor);
					gb = tmpBackColor.getGreen(); // (int)green(tmpBackColor);
					bb = tmpBackColor.getBlue(); // (int)blue(tmpBackColor);
					matrix
							.setRGB(i, MATRIX_ROWS - 1 - eqPos[i] - 1, rb, gb,
									bb);
				}
				if (MATRIX_ROWS - 1 - eqPos[i] - 2 > 0) {
					tmpBack = Color.HSBtoRGB(tColor, (float) .7,
							colorEQBackBrt * 2);
					tmpBackColor = new Color(tmpBack);
					rb = tmpBackColor.getRed(); // (int)red(tmpBackColor);
					gb = tmpBackColor.getGreen(); // (int)green(tmpBackColor);
					bb = tmpBackColor.getBlue(); // (int)blue(tmpBackColor);
					matrix
							.setRGB(i, MATRIX_ROWS - 1 - eqPos[i] - 2, rb, gb,
									bb);
				}
			}
			if (eqPos[i] > 0 && eqFallDelayPos == 0) {
				eqPos[i] = eqPos[i] - 1;
			}
		}
		matrix.refresh();
	}

	// --- overrides the set process event to include loading minim from the
	// base
	public void setProc(PApplet theproc) {
		super.setProc(theproc);
		minim = ((ProcessingAppLauncherMinim) proc).minim;
		eq = new EQLevels(minim, 20);
	}

	void playDemoSong() {
		if (minim == null) {
			System.out.println("NULL Minim");
			return;
		}
		eq.loadSong(txtSongName.getText());
		eq.song.play();
	}

	private JPanel contentPane;
	private JTextField txtSongName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// AppSoundDemoSwing frame = new AppSoundDemoSwing();
					// frame.setVisible(true);

					// ProcessingAppLauncher procLaunch = new
					// ProcessingAppLauncher();
					// NOTE: Using Minim version
					ProcessingAppLauncherMinim procLaunch = new ProcessingAppLauncherMinim();
					procLaunch.launch("MatrixEQApp");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	void runDemo1() {
		if (proc == null) {
			System.out.print("NULL");
		} else {
			System.out.print("ACTIVE");
		}
		sweepHeight();
		sweepWidth();
	}

	/**
	 * Create the frame.
	 */
	public MatrixEQApp() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				eqRunner = null;

				matrix.end();
				timer.cancel();
				timer.purge();

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

		JButton btnPlayDemoSong = new JButton("Play Demo Song");
		btnPlayDemoSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playDemoSong();
			}
		});
		btnPlayDemoSong.setBounds(133, 11, 195, 23);
		contentPane.add(btnPlayDemoSong);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (eq.song == null)
					return;

				if (eq.song.isPlaying())
					eq.song.close();
			}
		});
		btnStop.setBounds(10, 45, 89, 23);
		contentPane.add(btnStop);

		txtSongName = new JTextField();
		txtSongName.setText("/Users/acolling/statetrooper.mp3");
		txtSongName.setBounds(133, 33, 195, 20);
		contentPane.add(txtSongName);
		txtSongName.setColumns(10);

		setupExtraWindow();
		
		//moved -- matrixSetup();
		
		
	}

	void setupEQRunner() {
		timer = new Timer();
		timer.schedule(eqRunner, 0, // initial delay
				100);
	}

	class EQTask extends TimerTask {
		public void run() {
			while(true){
				loadFromCanvas();	
			}
			
			/*
			while (true) {

				if (eq == null || eq.song == null) {
					return;
				}
				if (eq.song.isPlaying()) {
					//matrixEQ();
					loadFromCanvas();
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}*/
			// System.out.println("Time's up!");
			// toolkit.beep();
			// timer.cancel(); //Not necessary because we call System.exit
			// System.exit(0); //Stops the AWT thread (and everything else)
		}
	}
}
