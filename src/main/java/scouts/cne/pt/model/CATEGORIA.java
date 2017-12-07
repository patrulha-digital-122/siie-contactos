package scouts.cne.pt.model;

public enum CATEGORIA {
	AL ("Aspirante a Lobito", "dos 6 aos 10 anos"),
	L ("Lobito", "dos 6 aos 10 anos"),
	AE ("Aspirante a Explorador / Moço", "dos 6 aos 10 anos"),
	NE ("Noviço Explorador / Moço", "dos 6 aos 10 anos"),
	E ("Explorador / Moço", "dos 6 aos 10 anos"),
	AP ("Aspirante a Pioneiro / Marinheiro", "dos 6 aos 10 anos"),
	NP ("Noviço Pioneiro / Marinheiro", "dos 6 aos 10 anos"),
	P ("Pioneiro / Marinheiro", "dos 6 aos 10 anos"),
	AC ("Aspirante a Caminheiro / Companheiro", "dos 6 aos 10 anos"),
	NC ("Noviço Caminheiro / Companheiro", "dos 6 aos 10 anos"),
	C ("Caminheiro / Companheiro", "dos 6 aos 10 anos"),
	AD ("Aspirante a Dirigente", "dos 6 aos 10 anos"),
	ND ("Noviço a Dirigente", "dos 6 aos 10 anos"),
	D ("Dirigente", "dos 6 aos 10 anos"),
	DH ("Dirigente Honorario", "dos 6 aos 10 anos"),
	A ("Auxiliar", "dos 6 aos 10 anos"),
	AFNA ("Auxiliar - FNA", "dos 6 aos 10 anos"),
	B ("Benemérito", "dos 6 aos 10 anos"),
	NONE ("Nenhuma", "dos 6 aos 10 anos");

	private String nome;
	private String descricao;

	private CATEGORIA(String nome, String descricao) {
		this.nome = nome;
		this.descricao = descricao;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	public static CATEGORIA findCategoria(String seccao) {
		for (CATEGORIA categoria : values()) {
			if(categoria.getNome().equalsIgnoreCase(seccao)) {
				return categoria;
			}
		}
		return NONE;
	}
}
