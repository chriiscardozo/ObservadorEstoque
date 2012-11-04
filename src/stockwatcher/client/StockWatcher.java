package stockwatcher.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StockWatcher implements EntryPoint {
	
	private static final String STYLE_SEM_MUDANCA = "semMudanca";
	private static final String STYLE_PAINEL_ADICIONAR = "painel_adicionar";
	private static final String STYLE_COLUNA_REMOVER = "tabela_coluna_remover";
	private static final String STYLE_CELULA_NUMERICA_TABELA = "celula_numerica_tabela";
	private static final String STYLE_TABELA_ESTOQUE = "tabela_estoque";
	private static final String STYLE_TITULO_TABELA_ESTOQUE = "titulo_tabela_estoque";
	
	private static final String DIV_ID_LISTA_ESTOQUE = "listaEstoque";
	
	private static final String TEXTO_ULTIMA_ATUALIZACAO = "Última atualização";
	private static final String TEXTO_CODIGO_DUPLICADO = "já existe.";
	private static final String TETXO_CODIGO_INVALIDO = "não é um código válido.";
	private static final String TEXTO_TABELA_REMOVER = "Remover";
	private static final String TEXTO_TABELA_VARIACAO = "Variação";
	private static final String TEXTO_TABELA_PRECO = "Preço";
	private static final String TEXTO_TABELA_CODIGO = "Código";
	private static final String TEXTO_BOTAO_ADICIONAR = "Adicionar";
	private static final String TEXTO_BOTAO_REMOVER = "x";
	
	private static final int INTERVALO_ATUALIZACAO = 5000; //ms
	private static final double PRECO_MAXIMO = 100.0;
	private static final double MAXIMA_VARIACAO = 0.02; // +/- 2%
	
	private VerticalPanel painelPrincipal = new VerticalPanel();
	private HorizontalPanel painelAdicionarItem = new HorizontalPanel();
	private FlexTable tabelaEstoque = new FlexTable();
	private TextBox textNovoItem = new TextBox();
	private Button botaoAdicionar = new Button(TEXTO_BOTAO_ADICIONAR);
	private Label labelUltimaAtualizacao = new Label();
	private ArrayList<String> itens = new ArrayList<String>();
	
	public void onModuleLoad() {
		tabelaEstoque.setText(0, 0, TEXTO_TABELA_CODIGO);
		tabelaEstoque.setText(0, 1, TEXTO_TABELA_PRECO);
		tabelaEstoque.setText(0, 2, TEXTO_TABELA_VARIACAO);
		tabelaEstoque.setText(0, 3, TEXTO_TABELA_REMOVER);
		
		painelAdicionarItem.add(textNovoItem);
		painelAdicionarItem.add(botaoAdicionar);
		
		painelPrincipal.add(tabelaEstoque);
		painelPrincipal.add(painelAdicionarItem);
		painelPrincipal.add(labelUltimaAtualizacao);
		
		RootPanel.get(DIV_ID_LISTA_ESTOQUE).add(painelPrincipal);
		textNovoItem.setFocus(true);
		
		aplicarEstilos();
		configurarEventos();
	}
	
	private void aplicarEstilos(){
		tabelaEstoque.getRowFormatter().setStyleName(0, STYLE_TITULO_TABELA_ESTOQUE);
		tabelaEstoque.addStyleName(STYLE_TABELA_ESTOQUE);
		painelAdicionarItem.addStyleName(STYLE_PAINEL_ADICIONAR);
		tabelaEstoque.setCellPadding(6);
	}
	
	private void configurarEventos(){
		botaoAdicionar.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				adicionarItem();
			}
		});
		
		textNovoItem.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_ENTER){
					adicionarItem();
				}
			}
		});
		
		Timer tempoAtualizacao = new Timer() {
			public void run() {
				atualizarLista();
			}
		};
		tempoAtualizacao.scheduleRepeating(INTERVALO_ATUALIZACAO);
	}
	
	private void atualizarLista(){
		Item[] itens = new Item[this.itens.size()];
		
		for(int i = 0; i < this.itens.size(); i++){
			double preco = Random.nextDouble() * PRECO_MAXIMO;
			double variacao = preco * MAXIMA_VARIACAO * (Random.nextDouble() * 2.0 - 1.0);
			
			itens[i] = new Item(this.itens.get(i), preco, variacao);
		}
		
		atualizaTabelaItens(itens);
	}
	
	private void atualizaTabelaItens(Item itens[]){
		for(Item item : itens){
			atualizarItemTabela(item);
		}
		atualizaLabel();
	}
	
	@SuppressWarnings("deprecation")
	private void atualizaLabel(){
		labelUltimaAtualizacao.setText(TEXTO_ULTIMA_ATUALIZACAO + ": " + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
	}
	
	private void atualizarItemTabela(Item item){
		if(!itens.contains(item.getCodigo())){
			return ;
		}
		
		int linha = itens.indexOf(item.getCodigo()) + 1;
		
		String textoPreco = NumberFormat.getFormat("#,##0.00").format(item.getPreco());
		NumberFormat formatoVariacao = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String textoVariacao = formatoVariacao.format(item.getVariacao());
		String textoPorcentagemVariacao = formatoVariacao.format(item.getPorcentagemVariacao());
		
		tabelaEstoque.setText(linha, 1, textoPreco);
		Label labelVariacao = (Label) tabelaEstoque.getWidget(linha, 2);
		labelVariacao.setText(textoVariacao + " (" + textoPorcentagemVariacao + "%)");
		
		String estiloVariacao = STYLE_SEM_MUDANCA;
		if(item.getPorcentagemVariacao() < -0.1f){
			estiloVariacao = "mudancaNegativa";
		}
		else if(item.getPorcentagemVariacao() > 0.1f){
			estiloVariacao = "mudancaPositiva";
		}
		
		labelVariacao.setStyleName(estiloVariacao);
	}
	
	private void adicionarItem(){
		final String codigo = textNovoItem.getText().toUpperCase().trim();
		textNovoItem.setFocus(true);
		
		if (!codigo.matches("^[0-9A-Z\\.]{1,10}$")) {
		      Window.alert("'" + codigo + "' " + TETXO_CODIGO_INVALIDO);
		      textNovoItem.selectAll();
		      return ;
		}
		
		textNovoItem.setText("");
		
		if(itens.contains(codigo)){
			Window.alert("'" + codigo + "' " + TEXTO_CODIGO_DUPLICADO);
			return ;
		}
		
		int linha = tabelaEstoque.getRowCount();
		itens.add(codigo);
		tabelaEstoque.setText(linha, 0, codigo);
		tabelaEstoque.setWidget(linha, 2, new Label());
		
		tabelaEstoque.getCellFormatter().addStyleName(linha, 1, STYLE_CELULA_NUMERICA_TABELA);
		tabelaEstoque.getCellFormatter().addStyleName(linha, 2, STYLE_CELULA_NUMERICA_TABELA);
		tabelaEstoque.getCellFormatter().addStyleName(linha, 3, STYLE_COLUNA_REMOVER);
		
		Button botaoRemover = new Button(TEXTO_BOTAO_REMOVER);
		botaoRemover.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int indiceRemovido = itens.indexOf(codigo);
				itens.remove(indiceRemovido);
				tabelaEstoque.removeRow(indiceRemovido + 1);
			}
		});
		botaoRemover.addStyleDependentName("remove");
		tabelaEstoque.setWidget(linha, 3, botaoRemover);
		
		atualizarLista();
	}
}
