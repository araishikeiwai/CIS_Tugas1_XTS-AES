import java.io.*;
import java.awt.*;          
import java.awt.event.*;    
import javax.swing.*;       
import javax.swing.border.*;

public class Tugas1 extends JFrame {
	JPanel targetPanel, targetFilePanel, keyPanel, keyFilePanel, resultPanel, resultFilePanel, operationsPanel;
	JLabel targetSelectLabel, targetNameLabel, keySelectLabel, keyNameLabel, resultLabel, resultNameLabel;
	JButton targetButton, keyButton, resultButton, encryptButton, decryptButton;

	File targetFile, keyFile, resultFile;

	public Tugas1() {
		super("XTS-AES Cipher");

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setSize(new Dimension(300, 300));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.addTargetSection();
		this.addKeySection();
		this.addResultSection();
		this.add(new JSeparator());
		this.addOperationsSection();
	}

	private void addTargetSection() {
		targetPanel = new JPanel();
		targetPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		targetSelectLabel = new JLabel("Select target file:");
		targetPanel.add(targetSelectLabel);
		this.add(targetPanel);

		targetFilePanel = new JPanel();
		targetFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		targetButton = new JButton("Browse");
		targetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(Tugas1.this) == JFileChooser.APPROVE_OPTION) {
					targetFile = fc.getSelectedFile();
					targetNameLabel.setText(targetFile.getName());
				}
			}
		});
		targetNameLabel = new JLabel();
		targetFilePanel.add(targetButton);
		targetFilePanel.add(targetNameLabel);
		this.add(targetFilePanel);
	}

	private void addKeySection() {
		keyPanel = new JPanel();
		keyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		keySelectLabel = new JLabel("Select key file:");
		keyPanel.add(keySelectLabel);
		this.add(keyPanel);

		keyFilePanel = new JPanel();
		keyFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		keyButton = new JButton("Browse");
		keyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(Tugas1.this) == JFileChooser.APPROVE_OPTION) {
					keyFile = fc.getSelectedFile();
					keyNameLabel.setText(keyFile.getName());
				}
			}
		});
		keyNameLabel = new JLabel();
		keyFilePanel.add(keyButton);
		keyFilePanel.add(keyNameLabel);
		this.add(keyFilePanel);
	}

	private void addResultSection() {
		resultPanel = new JPanel();
		resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		resultLabel = new JLabel("Save output to:");
		resultPanel.add(resultLabel);
		this.add(resultPanel);

		resultFilePanel = new JPanel();
		resultFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		resultButton = new JButton("Browse");
		resultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JFileChooser fc = new JFileChooser();
				if (fc.showSaveDialog(Tugas1.this) == JFileChooser.APPROVE_OPTION) {
					resultFile = fc.getSelectedFile();
					resultNameLabel.setText(resultFile.getName());
				}
			}
		});
		resultNameLabel = new JLabel();
		resultFilePanel.add(resultButton);
		resultFilePanel.add(resultNameLabel);
		this.add(resultFilePanel);
	}

	private void addOperationsSection() {
		operationsPanel = new JPanel();
		operationsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		encryptButton = new JButton("Encrypt");
		decryptButton = new JButton("Decrypt");
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (targetFile == null || keyFile == null || resultFile == null) {
					JOptionPane.showMessageDialog(null, "Please specify target, key, and result files.");
					return;
				}

				try {
					int[] targetBytes = CisUtils.toIntArray(targetFile);
					int[] keyBytes = CisUtils.toIntArray(keyFile);
					XTS cipher = new XTS(keyBytes, null);

					int[] resultBytes;
					String verb;
					if (ev.getSource() == encryptButton)
					{
						resultBytes = cipher.encrypt(targetBytes);
						verb = "encrypted";
					}
					else
					{
						resultBytes = cipher.decrypt(targetBytes);
						verb = "decrypted";
					}

					CisUtils.writeToFile(resultBytes, resultFile);
					JOptionPane.showMessageDialog(null, targetFile.getName() + " has been " + verb + " into " + resultFile.getName() + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Input/output error happened.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (CipherException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Something weird happened.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		encryptButton.addActionListener(listener);
		decryptButton.addActionListener(listener);
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
