package scouts.cne.pt.model;

import java.util.Arrays;
import java.util.List;

public enum SECCAO {
	LOBITOS ("L", "Lobitos", "dos 6 aos 10 anos"),
	EXPLORADORES ("E", "Exploradores", "dos 6 aos 10 anos"),
	PIONEIROS ("P", "Pioneiros", "dos 6 aos 10 anos"),
	CAMINHEIROS ("C", "Caminheiros", "dos 6 aos 10 anos"),
	NONE ("N", "N/A", "Sem secção atribuida"),
	DIRIGENTES ("D", "Dirigentes", "dos 6 aos 10 anos");

	private String id;
	private String nome;
	private String descricao;

	private SECCAO(String id, String nome, String descricao) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
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

	public static SECCAO findSeccao(String seccao) {
		if(seccao.contains("Lobito")) {
			return LOBITOS;
		}
		if(seccao.contains("Explorador")) {
			return EXPLORADORES;
		}
		if(seccao.contains("Pioneiro")) {
			return PIONEIROS;
		}
		if(seccao.contains("Caminheiro")) {
			return SECCAO.CAMINHEIROS;
		}
		if(seccao.contains("Dirigente")) {
			return SECCAO.DIRIGENTES;
		}

		return NONE;
	}
	
	public static List< SECCAO > getListaSeccoes()
	{
		return Arrays.asList( LOBITOS, EXPLORADORES, PIONEIROS, CAMINHEIROS, DIRIGENTES );
	}
}
