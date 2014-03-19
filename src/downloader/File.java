package downloader;

public class File {
	private String tittle;
	private String sub;
	private String cap;
	private int porc;
	private String url;
	private String site;
	public File(String tittle, String sub, String cap, int porc, String url, String site) {
		super();
		this.tittle = tittle;
		this.sub = sub;
		this.cap = cap;
		this.porc = porc;
		this.url = url;
		this.site = site;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	@Override
	public String toString() {
		/*return "Título = " + tittle + ", " + 
	           "Sub = " + sub + ", " + 
			   "Capítulo = " + cap	+ ", " + 
	           "Porcentagem = " + porc + ", " + 
			   "URL = " + url;*/
		return tittle;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPorc() {
		return porc;
	}
	public void setPorc(int porc) {
		this.porc = porc;
	}
	public String getTittle() {
		return tittle;
	}
	public void setTittle(String tittle) {
		this.tittle = tittle;
	}
	public String getSub() {
		return sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}	
}
