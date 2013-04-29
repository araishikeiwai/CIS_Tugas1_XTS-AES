import java.awt.*;          
import java.awt.event.*;    
import javax.swing.*;       
import javax.swing.border.*;

public class Tugas1 extends JFrame {
	JPanel targetPanel, targetFilePanel, keyPanel, keyFilePanel, resultPanel, resultFilePanel, operationsPanel;
	JLabel targetSelectLabel, targetNameLabel, keySelectLabel, keyNameLabel, resultLabel, resultNameLabel;
	JFileChooser targetChooser, keyChooser, resultChooser;
	JButton targetButton, keyButton, resultButton, encryptButton, decryptButton;

	public Tugas1() {
		super("Assignment 1 CIS");

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setSize(new Dimension(500, 300));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		targetPanel = new JPanel();
		targetPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		targetSelectLabel = new JLabel("Select target file:");
		targetPanel.add(targetSelectLabel);
		this.add(targetPanel);

		targetFilePanel = new JPanel();
		targetFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		targetButton = new JButton("Browse");
		targetNameLabel = new JLabel();
		targetFilePanel.add(targetButton);
		targetFilePanel.add(targetNameLabel);
		this.add(targetFilePanel);

		keyPanel = new JPanel();
		keyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		keySelectLabel = new JLabel("Select key file:");
		keyPanel.add(keySelectLabel);
		this.add(keyPanel);

		keyFilePanel = new JPanel();
		keyFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		keyButton = new JButton("Browse");
		keyNameLabel = new JLabel();
		keyFilePanel.add(keyButton);
		keyFilePanel.add(keyNameLabel);
		this.add(keyFilePanel);

		resultPanel = new JPanel();
		resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		resultLabel = new JLabel("Save output to:");
		resultPanel.add(resultLabel);
		this.add(resultPanel);

		resultFilePanel = new JPanel();
		resultFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		resultButton = new JButton("Browse");
		resultNameLabel = new JLabel();
		resultFilePanel.add(resultButton);
		resultFilePanel.add(resultNameLabel);
		this.add(resultFilePanel);

		this.add(new JSeparator());

		operationsPanel = new JPanel();
		operationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		encryptButton = new JButton("Encrypt");
		decryptButton = new JButton("Decrypt");
		operationsPanel.add(encryptButton);
		operationsPanel.add(decryptButton);
		this.add(operationsPanel);
	}

	public static void main(String[] args) {
		try {   
        	 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
    	}  
      	catch (Exception e) {  
    	}

    	Tugas1 app = new Tugas1();
    	

    	app.setVisible(true);
	}
}