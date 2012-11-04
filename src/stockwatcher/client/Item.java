package stockwatcher.client;

public class Item {
	private String codigo;
	private double preco;
	private double variacao;
	
	public Item(){
	}
	
	public Item(String codigo, double preco, double variacao){
		this.codigo = codigo;
		this.preco = preco;
		this.variacao = variacao;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public double getPreco() {
		return preco;
	}
	public void setPreco(double preco) {
		this.preco = preco;
	}
	public double getVariacao() {
		return variacao;
	}
	public void setVariacao(double variacao) {
		this.variacao = variacao;
	}
	
	public double getPorcentagemVariacao(){
		return 100.0 * this.variacao/this.preco;
	}
}