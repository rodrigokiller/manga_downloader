package downloader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.RowSorter;
import javax.swing.SwingWorker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/* TODO
 * Lista de bugs a serem corrigidos: 
 * - Ao selecionar uma pasta, pode ser que a / não tenha sido colocada no final, ela deve ir para a variavel do path
 * - Verificar se o PATH é valido e mostrar mensagem 
 * - Ao apresentar erro no download, a janela deve voltar ao estado inicial (ou seja, os botões podem ser reclicados e os caminhos reinseridos)
 * - Ao clicar em OK e começar o download, pode-se inserir mais downloads na lista (o download é feito da lista)
 */

public class Downloader extends JFrame implements ActionListener{

	private JButton btnProcurar;
	private String path;
	private java.io.File myfile;
	private JTable tableFiles;
	private JPanel contentPane;
	private JButton filtrar;	
	private JTextField path_url;
	private JTextField lblCaminho;
	private JButton btnOk;
	private static List<downloader.File> fileList = new ArrayList<downloader.File>();		
	public static int pause = 0;
	private JButton bPause;
	private JLabel activeDown;
	private JLabel lblCaminho_1;
	private JButton btnAjuda;

	/**
	 * Launch the application.
	 */
	
	public static void createAndShowUI(){
		try {
			Downloader frame = new Downloader();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);					
		} catch (NullPointerException e){
			
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowUI();
			}
		});		
	}
	
	public void actionPerformed(ActionEvent e){
		Downloader.Task task = new Downloader.Task();
		if (e.getSource() == filtrar)
			try {
				if (path_url.getText().length() > 0)
					getUrlSource(path_url.getText());																				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if (e.getSource() == btnProcurar)
			file_open();
		if (e.getSource() == btnOk)
		{									
			task.execute();			
		}
		if (e.getSource() == btnAjuda){
			Help h = new Help();
			h.setVisible(true);
		}
		if (e.getSource() == bPause){
			String pauseStr = bPause.getText();
			if (pauseStr.equals("Pausar"))		
				bPause.setText("Despausar");
			if (pauseStr.equals("Despausar"))
				bPause.setText("Pausar");
		}
		if (bPause.getText().equals("Pausar")){
			if(!fileList.isEmpty() && lblCaminho.getText().length() > 0)
				btnOk.setEnabled(true);		
			if (btnOk.isEnabled())
				bPause.setEnabled(true);
			if (path_url.getText().length() > 0)
				filtrar.setEnabled(true);		
		}
	}
	
	public void getFiles(){		
		try {
			lblCaminho.setEnabled(false);
			//filtrar.setEnabled(false);
			//path_url.setEnabled(false);
			btnOk.setEnabled(false);
			btnProcurar.setEnabled(false);
			for (int i = 0; i < tableFiles.getRowCount(); i++)	{
			//int i = 0;
				if (((Integer)tableFiles.getValueAt(i, 3) < 100) && bPause.getText().equals("Pausar"))
					getUrlFiles(tableFiles.getValueAt(i, 4).toString(), tableFiles.getValueAt(i, 5).toString(), i, tableFiles.getValueAt(i, 1).toString(), tableFiles.getValueAt(i, 6).toString(), tableFiles.getValueAt(i, 0).toString());
			}
			lblCaminho.setEnabled(true);
			//filtrar.setEnabled(true);
			//path_url.setEnabled(true);
			btnOk.setEnabled(true);
			btnProcurar.setEnabled(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erro: " + e);
			btnOk.setEnabled(true);
		}
	}
	
	public void removeSelectedFromTable(){
		int[] selectedRows = tableFiles.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
            	((DefaultTableModel)tableFiles.getModel()).removeRow(selectedRows[i]);
            	//fileList.remove(i);
            }
        }
	}
	
	public void updateTable(int nFile, int tot, int i){
		float porcf = (nFile*100)/tot;
		int porc = (int) porcf;		
		fileList.set(i, new downloader.File(fileList.get(i).getTittle(),
				fileList.get(i).getSub(), 
				fileList.get(i).getCap(), 
				//getNFiles(fileList.get(i).getUrl(), fileList.get(i).getSite()),
				porc,
				fileList.get(i).getUrl(), 
				fileList.get(i).getSite()));			
		tableFiles.setValueAt(fileList.get(i).getPorc(), i, 3);
		tableFiles.setValueAt(fileList.get(i).getUrl(), i, 4);
		tableFiles.setValueAt(fileList.get(i).getSite(), i, 5);
		tableFiles.setValueAt(fileList.get(i).getSub(), i, 6);
		tableFiles.updateUI();			
	}
	
	public void addRow() {				
		
		javax.swing.table.DefaultTableModel dtm =
				(javax.swing.table.DefaultTableModel)
				tableFiles.getModel();
		for (int i = 0; i < fileList.size(); i++) {
		dtm.addRow(new Object[]{
				fileList.get(i).getTittle(),
				fileList.get(i).getCap(),
				null,
				fileList.get(i).getPorc(),
				fileList.get(i).getUrl(),
				fileList.get(i).getSite(),
				fileList.get(i).getSub()
				});		
		}
		getProgress();
	}
	
	//private JProgressBar getProgress(int x, int total) {
	public void getProgress(){
		final List<JProgressBar> bars = new ArrayList<JProgressBar>(); 
		final AbstractTableModel model = ((AbstractTableModel) tableFiles.getModel());
		for (int i = 0; i < tableFiles.getRowCount(); i++) {
			bars.add(new JProgressBar(0, 100));
		}
		TableColumnModel columnModel = tableFiles.getColumnModel();

		columnModel.getColumn(2).setCellRenderer(new TableCellRenderer() { //sets a progress bar as renderer
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JProgressBar bar = bars.get(row);
				bar.setValue((Integer) table.getValueAt(row, 3));
				bar.setStringPainted(true);
				return bar;
			}
		});

		model.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e) {
				int c = e.getColumn();
				int r = e.getFirstRow();
				if(c==1){
					model.fireTableChanged(new TableModelEvent(model,r));  //update the whole row
				}
			}
		});
    }	

	/**
	 * Create the frame.
	 */
	public Downloader() {
		setTitle("Downloader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 826, 613);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane spFiles = new JScrollPane();
		
		btnOk = new JButton("OK");
		btnOk.setToolTipText("Inicia download dos arquivos da tabela");
		btnOk.setEnabled(false);
		btnOk.addActionListener(this);				
		
		path_url = new JTextField();
		path_url.setColumns(10);
		path_url.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {								
				if (path_url.getText().length() > 0)
					filtrar.setEnabled(true);
				else
					filtrar.setEnabled(false);
			}
		});			
		
		filtrar = new JButton("Carregar");		
		filtrar.setToolTipText("Carrega os capitulos na tabela");
		filtrar.setEnabled(false);
		filtrar.addActionListener(this);
		
		lblCaminho = new JTextField();
		lblCaminho.setColumns(10);
		
		
		btnProcurar = new JButton("Procurar");
		btnProcurar.setToolTipText("Procura o caminho para salvar");
		btnProcurar.addActionListener(this);
		
		bPause = new JButton("Pausar");
		bPause.setEnabled(false);
		bPause.addActionListener(this);
		
		activeDown = new JLabel("");
		
		JLabel lblUrl = new JLabel("URL");
		
		lblCaminho_1 = new JLabel("Caminho");
		
		btnAjuda = new JButton("Ajuda");
		btnAjuda.addActionListener(this);
		
		JLabel lblRodrigoSanguanini = new JLabel("Rodrigo Sanguanini - Killer Skull - http://www.killerskull.com ");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(spFiles, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(activeDown, GroupLayout.PREFERRED_SIZE, 585, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
							.addComponent(bPause, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblCaminho, 671, 671, 671)
								.addComponent(path_url, GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btnProcurar, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
								.addComponent(filtrar, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)))
						.addComponent(lblCaminho_1)
						.addComponent(lblUrl, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblRodrigoSanguanini)
							.addPreferredGap(ComponentPlacement.RELATED, 673, Short.MAX_VALUE)
							.addComponent(btnAjuda)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblUrl)
					.addGap(2)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(path_url, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(filtrar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCaminho_1)
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCaminho, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnProcurar))
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnOk, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(bPause, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(activeDown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(18)
					.addComponent(spFiles, GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAjuda)
						.addComponent(lblRodrigoSanguanini))
					.addGap(5))
		);
		tableFiles = new JTable();
		//tableFiles.setAutoCreateRowSorter(true);		
		//tableFiles.setRowSorter(tableFiles.getRowSorter().toggleSortOrder(2));
		//tableFiles.setAutoCreateRowSorter(false);
		
		tableFiles.setModel (new DefaultTableModel(new Object[][] {},new String[] {"Título", "Capítulo", "%","","","",""}){
			Class[] types = new Class [] {
					java.lang.Object.class, 
					java.lang.Integer.class, 
					java.lang.Object.class, 
					java.lang.Integer.class,
					java.lang.String.class,
					java.lang.String.class,
					java.lang.String.class};			
			boolean[] canEdit = new boolean [] {
					true, true, false, false, false, false, false
			};

			public Class getColumnClass(int columnIndex) {
				return types [columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit [columnIndex];
			}
		});		
		tableFiles.getColumnModel().getColumn(3).setMinWidth(0);
		tableFiles.getColumnModel().getColumn(3).setMaxWidth(0);
		tableFiles.getColumnModel().getColumn(3).setWidth(0);	
		tableFiles.getColumnModel().getColumn(4).setMinWidth(0);
		tableFiles.getColumnModel().getColumn(4).setMaxWidth(0);
		tableFiles.getColumnModel().getColumn(4).setWidth(0);
		tableFiles.getColumnModel().getColumn(5).setMinWidth(0);
		tableFiles.getColumnModel().getColumn(5).setMaxWidth(0);
		tableFiles.getColumnModel().getColumn(5).setWidth(0);
		tableFiles.getColumnModel().getColumn(6).setMinWidth(0);
		tableFiles.getColumnModel().getColumn(6).setMaxWidth(0);
		tableFiles.getColumnModel().getColumn(6).setWidth(0);
		tableFiles.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					removeSelectedFromTable();
			}
		});		
		spFiles.setViewportView(tableFiles);		
		contentPane.setLayout(gl_contentPane);
		
		// Teste 
		getProgress();
	}
	
	public void file_open(){
        JFileChooser fc = new JFileChooser("");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = fc.showOpenDialog(this);        
        if(r == fc.CANCEL_OPTION)
            return;        
        myfile = fc.getSelectedFile();            
        if(myfile == null || myfile.getName().equals(""))
        {
            JOptionPane.showMessageDialog(this, "Selecione um arquivo!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {            
            path = myfile.getAbsolutePath();
            lblCaminho.setText(myfile.getAbsolutePath());
            myfile = new java.io.File(path);                
            //insere_tabela(myfile);
        }
        catch(Exception e){
        	lblCaminho.setText("Caminho");        	
        	JOptionPane.showMessageDialog(null, "Erro: " + e);
        }
    }
	
	//baixa a imagem da internet	
	private void getImg(String url_t, String nameFile, String cap, String sub, String title) throws IOException {
		title = title.replaceAll(":", "");
		File file = new File(lblCaminho.getText() + "" + title + "//" + sub + "//" + cap + "//" + nameFile);
		//activeDown.setText(url_t);
		activeDown.setText(lblCaminho.getText() + "" + title + "\\" + sub + "\\" + cap + "\\" + nameFile);
		//if (!file.exists() && bPause.getText().equals("Pausar"))
		if (!file.exists())
		{
			String url_e = "";
			try {
				url_e = URLEncoder.encode(url_t,"UTF-8")
						.replaceAll("\\+", "%20")
						.replaceAll("\\%21", "!")
						.replaceAll("\\%27", "'")
						.replaceAll("\\%28", "(")
						.replaceAll("\\%29", ")")
						.replaceAll("\\%7E", "~")
						.replaceAll("\\%3A", ":")
						.replaceAll("\\%2F", "/");			

				URL url = new URL(url_e);					
				InputStream in = new BufferedInputStream(url.openStream());
				File teste = new File (url_e);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1!=(n=in.read(buf)))
				{
					while(bPause.getText().equals("Despausar"))
						System.out.println();
					//System.out.println(buf.toString());
					//System.out.println(n);
					//System.out.println(teste.length());					
					out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response = out.toByteArray();

				// Criar pasta temporária 
				createDir(lblCaminho.getText() + "" + title + "\\" + sub + "\\" + cap);

				// Grava arquivos na pasta temporária					
				FileOutputStream fos = new FileOutputStream(lblCaminho.getText() + "" + title + "\\" + sub + "\\" + cap + "\\" + nameFile);			
				fos.write(response);
				fos.close();
			} catch (UnsupportedEncodingException e) {

			} catch (FileNotFoundException e) {
					/*JOptionPane.showMessageDialog(
							new JFrame("Capitalize Client"),
							"Arquivo não encontrado no servidor: " + url_t,
							"Erro",
							JOptionPane.ERROR_MESSAGE);*/				
			}
		}
	}
	
    // Criar diretorio
    static void createDir(String caminho) {
        try{
            boolean status;
            status = new File (caminho).mkdirs();
            
        }catch (Exception e){            
            System.out.println("asdf" + e.getMessage());
        }  
    }
    
    // Move e renomeia o diretorio
    static void moveDir(String caminho, String title, String number) {
        try{            
        	// inicia o FILE arq1 com o caminho temporario
            File arq1 = new File (caminho);            
            // inicia o FILE arq2 com o caminho que vai salvar
            File arq2 = new File ("D://" + title + "//" + number);            
            // Cria o diretório onde é pra salvar mesmo
            createDir("D://" + title + "//" + number); 
            // Copia os arquivos da pasta temporária para o diretório
            copyDirectory(arq1,arq2);  
            // Deleta a pasta temporária
            delete(arq1);
            
        }catch (Exception e){
            System.out.println(e.getMessage());
        }  
    }    

    // pega o código fonte da página URL solicitada
	private void getUrlSource(String url_s) throws IOException {		
		try {
			URL url = new URL(url_s); // converte a url STRING em uma URL objeto			
			URLConnection uc = url.openConnection(); // faz conexão
			
			//System.setProperty("http.agent", "");
			
			uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));			

			String inputLine;
			StringBuilder a = new StringBuilder();
			String site = "",tittle = "", sub = "";
			
			site = url.getHost();	
			System.out.println(site);
			
			// verifica qual o site.
			if (site.equals("centraldemangas.com.br")){
	        	JOptionPane.showMessageDialog(
	        			new JFrame("Oi"),
	                    "Esse site ainda está sendo desenvolvido.",
	                    "Erro",
	                    JOptionPane.ERROR_MESSAGE);
			}					
			
			if (!((site.equals("mangafox.me")) || (site.equals("www.mangasproject.net")))){
	        	JOptionPane.showMessageDialog(
	        			new JFrame("Capitalize Client"),
	                    "O endereço informado não pode ser resolvido.",
	                    "Erro",
	                    JOptionPane.ERROR_MESSAGE);
			}			
			
			// neste momento vai procurar qual o site que está pegando a informação.
			while ((inputLine = in.readLine()) != null)
			{									
				if (site.equals("mangafox.me")){					
					//http://mangafox.me/manga/seiken_densetsu_legend_of_mana/v01/c001/1.html					
					if (inputLine.contains("<link rel=\"alternate\" type=\"application/rss+xml\" title=\"RSS\" href=\""))
						getUrlSource(inputLine.substring(inputLine.indexOf("title=\"RSS\" href=\"")+18, inputLine.indexOf("\"/>")));

					if (url_s.contains("rss") && url_s.contains(".xml")){							
						String cap = "", link = "";
						if (inputLine.contains("<title>") && tittle.length() == 0){
							fileList = new ArrayList<downloader.File>();
							tittle = inputLine.substring(inputLine.indexOf("<title>")+7, inputLine.indexOf("</title>"));
						}
						while (inputLine.contains("<item>")){								
							inputLine = in.readLine();
							if (inputLine.contains("<title>")){
								sub = inputLine.substring(inputLine.indexOf(tittle)+tittle.length()+1, inputLine.indexOf("Ch")-1);
								cap = inputLine.substring(inputLine.indexOf("Ch") + 3, inputLine.indexOf("</title>"));
								inputLine = in.readLine();
								link = inputLine.substring(inputLine.indexOf("<link>") + 6, inputLine.indexOf("</link>"));								
								while (!inputLine.contains("</item>"))
									inputLine = in.readLine();
								inputLine = in.readLine();
								fileList.add(new downloader.File(tittle,sub,cap,0,link,site));								
							}							
						}
						addRow();
					}					
				}				
				else if (site.equals("www.mangasproject.net"))
				{
					// título 
					if (inputLine.contains("this.name = "))
					{   						
						inputLine = inputLine.substring(inputLine.indexOf("this.name = ")+13, inputLine.length());					
						tittle = inputLine.substring(0, inputLine.indexOf("\";"));														
					}
					// sub 
					if (inputLine.contains("this.group = "))
					{   						
						inputLine = inputLine.substring(inputLine.indexOf("this.group = ")+14, inputLine.length());					
						sub = inputLine.substring(0, inputLine.indexOf("\";"));									
					}
					// primeiro verifica todos os capitulos, armazenando em um vetor string url
					if (inputLine.contains("<div class=\"capitulo_listagem\">")){
						inputLine = in.readLine();
						inputLine = in.readLine();
						inputLine = in.readLine();
						if (inputLine.contains("<select name=\"capitulo_listagem\" onchange=\"window.location = this.value\">")){						
							inputLine = in.readLine();		
							fileList = new ArrayList<downloader.File>();	
							while (inputLine.contains("<option value=")){									
								inputLine = inputLine.substring(inputLine.indexOf("<option value=")+15, inputLine.length());
								String cap = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("<"));
								cap = cap.substring(cap.indexOf(" "), cap.length());
								cap = cap.trim();								// 
								inputLine = inputLine.substring(0, inputLine.indexOf("\""));							
								fileList.add(new downloader.File(tittle,sub,cap,0,inputLine,"mangas.xpg.uol.com.br"));							
								inputLine = in.readLine();
							}	
							addRow();
						}	

					}

				}
				a.append(inputLine + "\n");					
			}										
			in.close();
		} catch (MalformedURLException e) {
        	JOptionPane.showMessageDialog(
        			new JFrame("Capitalize Client"),
                    "O endereço informado é inválido.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
		}		
						
	}
	
	public static int getNFiles(String url_s, String site) throws IOException{
		URL url = new URL(url_s); // converte a url STRING em uma URL objeto
		URLConnection uc = url.openConnection(); // faz conexão
		uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
		int nFiles = 0;
		String inputLine;
		StringBuilder a = new StringBuilder();
		// neste momento vai procurar qual o site que está pegando a informação.
		while ((inputLine = in.readLine()) != null)
		{	
			if (site.equals("mangafox.me") && !url_s.equals(".xml")){
				if (inputLine.contains("<div id=\"top_center_bar\">")){
					inputLine = in.readLine();
					inputLine = in.readLine();
					inputLine = in.readLine();
					inputLine = in.readLine();
					inputLine = in.readLine();
					inputLine = in.readLine();					
					if (inputLine.contains("Page")){						
						inputLine = in.readLine();		
						inputLine = in.readLine();						
						while(inputLine.indexOf("<option value=") > 0){								
							inputLine = inputLine.substring(inputLine.indexOf("<option ")+7, inputLine.length());							
							nFiles++;						
						}
					}
				}
			}
			if (site.equals("mangas.xpg.uol.com.br"))
			{
				// imagens
				if (inputLine.contains("this.pages = new Array({ "))
				{   				
					while (inputLine.indexOf("path: \"") > 0){
						inputLine = inputLine.substring(inputLine.indexOf("path: \"")+7, inputLine.length());
						String img = inputLine.substring(0, inputLine.indexOf("\", "));
						inputLine = inputLine.substring(img.length(), inputLine.length());
						nFiles++;
					}
				}
			}					
			a.append(inputLine + "\n");	
		}

		in.close();
		
		return nFiles;
	}
	
	public void getUrlFiles(String url_s, String site, int j, String cap, String sub, String title) throws IOException{
		try {
			URL url = new URL(url_s); // converte a url STRING em uma URL objeto
			URLConnection uc = url.openConnection(); // faz conexão
			uc.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
			int nFile = 0;
			int tot = getNFiles(url_s, site);
			String inputLine;
			StringBuilder a = new StringBuilder();
			String tittle = "",number = "";
			String url_tmp = "";
			// neste momento vai procurar qual o site que está pegando a informação.
			while ((inputLine = in.readLine()) != null)
			{	
				if (site.equals("mangafox.me") && !url_s.equals(".xml")){					
					// imagens				
					/*if (inputLine.contains("<div id=\"series\">")){						
						while (!inputLine.contains("</div>")){
							inputLine = in.readLine();							
							if (inputLine.contains("<strong><a href="))
								url_tmp = inputLine.substring(inputLine.indexOf("href=\"")+6, inputLine.indexOf("\">"));									
						}										
					}*/
					
					if (inputLine.contains("id=\"comments\""))
						url_tmp = inputLine.substring(inputLine.indexOf("<a href=")+9, inputLine.indexOf("\" id=\""));					

					if (inputLine.contains("<div id=\"top_center_bar\">")){
						inputLine = in.readLine();
						inputLine = in.readLine();
						inputLine = in.readLine();
						inputLine = in.readLine();
						inputLine = in.readLine();
						inputLine = in.readLine();					
						if (inputLine.contains("Page")){						
							inputLine = in.readLine();		
							inputLine = in.readLine();
							String url_f = "";
							while(inputLine.indexOf("<option value=") > 0){								
								inputLine = inputLine.substring(inputLine.indexOf("<option ")+7, inputLine.length());
								url_f = url_tmp + inputLine.substring(inputLine.indexOf("value=\"")+7, inputLine.indexOf("\" "));														
								
								// ---------------------------------------------------------------------------------------------
								
								URL newUrl = new URL(url_f+".html");								
								URLConnection newUc = newUrl.openConnection();
								BufferedReader newIn = new BufferedReader(new InputStreamReader(newUc.getInputStream(), "UTF-8"));
								String newInputLine;
								StringBuilder b = new StringBuilder();
								
								String img = "";
								
								while ((newInputLine = newIn.readLine()) != null){									
									if (newInputLine.contains("<div id=\"viewer\">")){										
										newInputLine = newIn.readLine();
										newInputLine = newIn.readLine();
										if (newInputLine.contains("<img src=") && newInputLine.contains("onclick="))
											img = newInputLine.substring(newInputLine.indexOf("<img src=")+10, newInputLine.indexOf("\" onerror"));										
									}									
									b.append(newInputLine + "\n");
								}
								
								// ---------------------------------------------------------------------------------------------								
								getImg(img, getNameFile(img), cap, sub, title);
								nFile++;	
								updateTable(nFile, tot, j);
							}
						}
					}

				}
				
				if (site.equals("mangas.xpg.uol.com.br"))
				{
					// imagens
					if (inputLine.contains("this.pages = new Array({ "))
					{   				
						while (inputLine.indexOf("path: \"") > 0){					
							inputLine = inputLine.substring(inputLine.indexOf("path: \"")+7, inputLine.length());
							String img = inputLine;
							img = img.substring(0, img.indexOf("\", "));
							inputLine = inputLine.substring(img.length(), inputLine.length());						
							// quando temos a url completa da imagem, fazemos o download da mesma.
							getImg(img, getNameFile(img), cap, sub, title);
							nFile++;
							updateTable(nFile, tot, j);
						}	
					}
					// título 
					if (inputLine.contains("this.name = "))
					{   				
						inputLine = inputLine.substring(inputLine.indexOf("this.name = ")+13, inputLine.length());
						tittle = inputLine;
						tittle = tittle.substring(0, tittle.indexOf("\";"));
						inputLine = inputLine.substring(tittle.length(), inputLine.length());				
					}
					// número do capítulo
					if (inputLine.contains("this.number = "))
					{   				
						inputLine = inputLine.substring(inputLine.indexOf("this.number = ")+15, inputLine.length());
						number = inputLine;
						number = number.substring(0, number.indexOf("\";"));
						inputLine = inputLine.substring(number.length(), inputLine.length());				
					}
				}
				a.append(inputLine + "\n");	
			}
			in.close();
		} catch (FileNotFoundException e) {
			/*JOptionPane.showMessageDialog(
					new JFrame("Capitalize Client"),
					"Arquivo não encontrado no servidor: " + url_s,
					"Erro",
					JOptionPane.ERROR_MESSAGE);*/
		}
	}
	
	// pega o nome do arquivo (o final dele, ex: 123.png)
	private static String getNameFile(String url) {		
		while (url.contains("/")){
			url = url.substring(url.indexOf("/")+1, url.length());	
		}
		return url;
	}
	
	// Método para copiar o arquivo para outro lugar
	public static void copyFile(File source, File dest) throws IOException {
		
		if(!dest.exists()) {
			dest.createNewFile();
		}
        InputStream in = null;
        OutputStream out = null;
        try {
        	in = new FileInputStream(source);
        	out = new FileOutputStream(dest);
    
	        // Transfer bytes from in to out
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
        }
        finally {
        	in.close();
            out.close();
        }
        
	}
	
	// método para copiar o diretório
	public static void copyDirectory(File sourceDir, File destDir) throws IOException {
		
		if(!destDir.exists()) {
			destDir.mkdir();
		}
		
		File[] children = sourceDir.listFiles();
		
		for(File sourceChild : children) {
			String name = sourceChild.getName();
			File destChild = new File(destDir, name);
			if(sourceChild.isDirectory()) {
				copyDirectory(sourceChild, destChild);
			}
			else {
				copyFile(sourceChild, destChild);
			}
		}	
	}
	
	// deleta arquivo ou diretório
	public static boolean delete(File resource) throws IOException { 
		if(resource.isDirectory()) {
			File[] childFiles = resource.listFiles();
			for(File child : childFiles) {
				delete(child);
			}
						
		}
		return resource.delete();
				
	}
	
	class Task extends SwingWorker<Void, Void> {

	    @Override
	    public Void doInBackground() {	    	
	    	getFiles();
	    	return null;
	    }

	    @Override
	    public void done() {
	        //System.out.println("terminou");
	        //Toolkit.getDefaultToolkit().beep();
	    }
	    
	    public void pause(){
	    	while (pause == 1){
	    		
	    	}
	    }
	}
}
