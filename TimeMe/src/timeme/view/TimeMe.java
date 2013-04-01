//TimeME: A desktop application that counts brief work time.
//Author: Alon Minski 2013.
//Planned features: Printing output from saved file, updating ADCOM directly


package timeme.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;

import timeme.bean.BriefBean;

/**
 * @author Vangivang
 *
 */
public class TimeMe extends WindowAdapter {

	private final static int DELAY = 1000;
	private final static int PORT = 9999;

	private static ServerSocket socket;    

	private int id = -1;

	private long startTime;
	private long endTime;

	private BriefBean selectedBriefBean;

	private JFrame frame;
	private JTextField titleTextField;

	private JLabel logoLabel;
	private JLabel briefNameLabel;
	private JLabel briefEllapsedTimeLabel;

	private boolean lightTOF = false;

	private JComboBox comboBox;

	private List<BriefBean> briefList;

	private Timer displayTimeTimer;

	public TimeMe() {
		initialize();
		loadData();
	}

	private void initialize() {
		briefList = new ArrayList<BriefBean>();
		displayTimeTimer = new Timer(DELAY, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				endTime = System.currentTimeMillis() - startTime;
				startTime = System.currentTimeMillis();
				selectedBriefBean.setEllapsedTime(endTime);
				briefEllapsedTimeLabel.setText(selectedBriefBean.getEllapsedTime());
			}
		});

		frame = new JFrame();
		frame.setBounds(100, 100, 361, 300);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);
		frame.getContentPane().setLayout(null);
		frame.setTitle("TimeMe! V0.9 - Beta");
		
		logoLabel = new JLabel();
		logoLabel.setBounds(245, 123, 90, 89);
		//logoLabel.setIcon(new javax.swing.ImageIcon("res/red.png")); // NOI18N
		logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/red.png"))); 
		
		
		frame.getContentPane().add(logoLabel);

		JLabel lblEnterTitle = new JLabel("Enter brief title:");
		lblEnterTitle.setBounds(10, 11, 95, 14);
		frame.getContentPane().add(lblEnterTitle);
		
		titleTextField = new JTextField();
		titleTextField.setBounds(10, 30, 154, 22);
		frame.getContentPane().add(titleTextField);
		titleTextField.setColumns(10);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 72, 414, 7);
		frame.getContentPane().add(separator);
		
		comboBox = new JComboBox(briefList.toArray());
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!lightTOF){
					selectedBriefBean = (BriefBean) comboBox.getSelectedItem();
					if (selectedBriefBean != null){
						briefNameLabel.setText(selectedBriefBean.getName());
						briefEllapsedTimeLabel.setText(String.valueOf(selectedBriefBean.getEllapsedTime()));
					}else{
						briefNameLabel.setText("####");
						briefEllapsedTimeLabel.setText("00:00:00");
					}
				}else{
					JOptionPane.showMessageDialog(null, "You canot do this. Press STOP first!");
				}
			}
		});
		comboBox.setBounds(10, 90, 154, 20);
		frame.getContentPane().add(comboBox);
		
		JButton btnAdd = new JButton("ADD");
		btnAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (!titleTextField.getText().isEmpty()){
				BriefBean newBrief = new BriefBean(titleTextField.getText(), ++id);
				comboBox.addItem(newBrief);
				briefList.add(newBrief);
				titleTextField.setText("");
				}else{
					JOptionPane.showMessageDialog(null, "Add a brief name before you click!");
				}
			}
		});
		
		btnAdd.setBounds(174, 29, 71, 23);
		
		frame.getContentPane().add(btnAdd);
		JButton btnStartstop = new JButton("START/STOP");
		btnStartstop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (comboBox.getSelectedItem() == null){
					JOptionPane.showMessageDialog(null, "nothing is selected");
					return;
				}else{
					if (!lightTOF){
						startTime = System.currentTimeMillis();
						displayTimeTimer.start();
						logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/green.png"))); 
						comboBox.setEnabled(false);
						lightTOF = true;
					}else{
						endTime = System.currentTimeMillis() - startTime;
						displayTimeTimer.stop();
						logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/red.png")));
						briefEllapsedTimeLabel.setText(String.valueOf(selectedBriefBean.getEllapsedTime()));
						comboBox.setEnabled(true);
						lightTOF = false;
					}
				}
				
				
			}
		});
		btnStartstop.setBounds(175, 89, 160, 22);
		frame.getContentPane().add(btnStartstop);
		
		JLabel lblBriefName = new JLabel("Brief name:");
		lblBriefName.setBounds(10, 165, 95, 14);
		frame.getContentPane().add(lblBriefName);
		
		JLabel lblEllapsedTime = new JLabel("Ellapsed time:");
		lblEllapsedTime.setBounds(10, 190, 103, 14);
		frame.getContentPane().add(lblEllapsedTime);
		
		briefNameLabel = new JLabel("####");
		briefNameLabel.setBounds(137, 165, 197, 14);
		frame.getContentPane().add(briefNameLabel);
		
		briefEllapsedTimeLabel = new JLabel("00:00:00");
		briefEllapsedTimeLabel.setBounds(137, 190, 197, 14);
		frame.getContentPane().add(briefEllapsedTimeLabel);
		
		JButton btnSaveData = new JButton("Save data");
		btnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lightTOF){
					JOptionPane.showMessageDialog(null, "Press STOP first!");
				}else{
					try {
						ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("list.dat"));
						objOut.writeObject(briefList);
						objOut.close();
						JOptionPane.showMessageDialog(null, "List saved");
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								"Something went wrong...");
						e.printStackTrace();
					}
				}
			}
		});
		btnSaveData.setBounds(10, 227, 115, 23);
		frame.getContentPane().add(btnSaveData);
		
		JButton btnRemove = new JButton("REMOVE");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(lightTOF){
					JOptionPane.showMessageDialog(null, "Press STOP first!");
				}else{
					BriefBean removedBean = (BriefBean) comboBox.getSelectedItem();
					comboBox.removeItem(removedBean);
					briefList.remove(removedBean);
					id++;
				}
			}
		});
		btnRemove.setBounds(250, 29, 85, 23);
		frame.getContentPane().add(btnRemove);
		
		JButton btnExportToTxt = new JButton("Export to txt");
		btnExportToTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(lightTOF){
					JOptionPane.showMessageDialog(null, "Press STOP first!");
				}else{
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter("dataList.txt"));
						for (BriefBean brf : briefList){
							out.append(brf.toFileString());
							out.newLine();
						}
						out.close();
						JOptionPane.showMessageDialog(null, "List exported to text file!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		btnExportToTxt.setBounds(134, 227, 111, 23);
		frame.getContentPane().add(btnExportToTxt);
		
	}

	//
	@Override
	public void windowClosing(WindowEvent e) {
		if(lightTOF){
			JOptionPane.showMessageDialog(null, "Stop the clock first!!");
			return;
		}
		int reply = JOptionPane.showConfirmDialog(null,
				"Did you remember to save?", "Are you sure?",
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	
	private void loadData() {
		File file = new File("list.dat");
		if (file.exists()){
			int reply = JOptionPane.showConfirmDialog(null, "Would you like to load the last saved file?", "Load file?",
					JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				try {
					FileInputStream fis = new FileInputStream(file);
					ObjectInputStream in = new ObjectInputStream(fis);
					briefList = (List<BriefBean>) in.readObject();
					if (!briefList.isEmpty()){
						comboBox.removeAllItems();
						for(BriefBean brf : briefList) {
							comboBox.addItem(brf);
							id++;
						}
					}
					in.close();
					fis.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void checkIfRunning() {
		  try {
		    //Bind to localhost adapter with a zero connection queue 
		    socket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
		  }
		  catch (BindException e) {
		    JOptionPane.showMessageDialog(null, "TimeMe already running!!");
		    System.exit(1);
		  }
		  catch (IOException e) {
		    System.err.println("Unexpected error.");
		    e.printStackTrace();
		    System.exit(2);
		  }
		}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		checkIfRunning();
		
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TimeMe window = new TimeMe();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
