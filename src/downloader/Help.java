package downloader;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.SystemColor;
//only in jdk7
//import java.awt.Window.Type;

public class Help extends JFrame {

	static JPanel window = new JPanel();
	
    public Help() {
    	setAlwaysOnTop(true);
		setResizable(false);
		// only in jdk7
		//setType(Type.UTILITY);
		setBounds(100, 100, 450, 300);
		
		JLabel lblMangDownloader = new JLabel("MANG\u00C1 DOWNLOADER");
		lblMangDownloader.setHorizontalAlignment(SwingConstants.CENTER);
		lblMangDownloader.setFont(new Font("Calibri", Font.BOLD, 20));
		
		JTextPane txtpnMangDownloader = new JTextPane();
		txtpnMangDownloader.setBackground(SystemColor.menu);
		txtpnMangDownloader.setEditable(false);
		txtpnMangDownloader.setText("Mang\u00E1 Downloader \u00E9 uma aplica\u00E7\u00E3o para fazer o download de mang\u00E1s pela internet, apenas adicionando o link para o primeiro cap\u00EDtulo. \r\n\nPara fazer o download, colocar o link do primeiro cap\u00EDtulo (funciona com qualquer cap\u00EDtulo) no campo URL e clicar em Carregar. Ao terminar de carregar, ser\u00E1 listado todos os cap\u00EDtulos dispon\u00EDveis, com possibilidade de remov\u00EA-los. Ap\u00F3s, \u00E9 s\u00F3 selecionar o local de download (o Mang\u00E1 Downloader j\u00E1 verifica por arquivos existentes) e clicar em OK para come\u00E7ar os downloads. \n\nFeedback pode ser enviado para rodrigokiller@gmail.com, assim como bugs e melhorias.\n\nPor Rodrigo Sanguanini (Killer Skull)");
		
		JLabel lblV = new JLabel("v 0.2");
		lblV.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(txtpnMangDownloader, GroupLayout.PREFERRED_SIZE, 423, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addGap(31)
								.addComponent(lblMangDownloader, GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
								.addContainerGap())
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(lblV, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGap(11)))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblMangDownloader)
					.addGap(1)
					.addComponent(lblV)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtpnMangDownloader, GroupLayout.PREFERRED_SIZE, 206, GroupLayout.PREFERRED_SIZE)
					.addGap(7))
		);
		getContentPane().setLayout(groupLayout);
		window.setBorder(new EmptyBorder(5, 5, 5, 5));		
		window.setVisible(false);		
	}
}
