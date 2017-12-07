package scouts.cne.pt.model;

import java.sql.Date;
import java.util.LinkedList;

public class Elemento {

	/**
	 *
	 */
	private static final long serialVersionUID = -6597748927158118909L;

	public static final String FIND_BY_NIN = "elemento.findByNin";
	public static final String FIND_BY_NIF = "elemento.findByNif";
	public static final String FIND_BY_NIF_OR_NIN = "elemento.findByNifOrNin";
	public static final String FIND = "elemento.find";

	private String nin;

	private String nif;
	private String nome;
	private String morada;
	private String localidade;
	private String codigoPostal;
	private Integer codigoPostal1;
	private Integer codigoPostal2;
	private String telefone;
	private String telemovel;
	private SEXO sexo;
	private String profissao;
	private String email;
	private String nomePai;
	private String telefonePai;
	private String emailPai;
	private String nomeMae;
	private String telefoneMae;
	private String emailMae;
	private String nomeEncEducacao;
	private String telefoneEncEducacao;
	private String emailEncEducacao;
	private String naturalidade;
	private String totem;
	private Boolean activo = false;
	private Boolean cartao = false;
	private CATEGORIA categoria;
	private Date dataNascimento = null;
	private Date dataAdmissao = null;
	private Date dataPromessa = null;
	private SECCAO seccao = SECCAO.NONE;
	private String observacoes;

	private Agrupamento agrupamento;

	public Elemento() {

	}

	/**
	 * @return the nin
	 */
	public String getNin() {
		return nin;
	}

	/**
	 * @param nin
	 *            the nin to set
	 */
	public void setNin(String nin) {
		this.nin = nin;
	}

	/**
	 * @return the nif
	 */
	public String getNif() {
		return nif;
	}

	/**
	 * @param nif
	 *            the nif to set
	 */
	public void setNif(String nif) {
		this.nif = nif;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome
	 *            the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the morada
	 */
	public String getMorada() {
		return morada;
	}

	/**
	 * @param morada
	 *            the morada to set
	 */
	public void setMorada(String morada) {
		this.morada = morada;
	}

	/**
	 * @return the localidade
	 */
	public String getLocalidade() {
		return localidade;
	}

	/**
	 * @param localidade
	 *            the localidade to set
	 */
	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	/**
	 * @return the codigoPostal
	 */
	public String getCodigoPostal() {
		return codigoPostal;
	}

	/**
	 * @return the codigoPostal
	 */
	public String getCodigoPostalNumerico() {
		StringBuilder stringBuilder = new StringBuilder();
		if ((codigoPostal1 != null) && (codigoPostal1 > 0)) {
			stringBuilder.append(codigoPostal1);
		}
		if ((codigoPostal2 != null) && (codigoPostal2 > 0)) {
			stringBuilder.append("-");
			stringBuilder.append(codigoPostal2);
		}
		return stringBuilder.toString();
	}

	/**
	 * @return the codigoPostal
	 */
	public String getCodigoPostalTotal() {
		StringBuilder stringBuilder = new StringBuilder();
		if ((codigoPostal1 != null) && (codigoPostal1 > 0)) {
			stringBuilder.append(codigoPostal1);
		}
		if ((codigoPostal2 != null) && (codigoPostal2 > 0)) {
			stringBuilder.append("-");
			stringBuilder.append(codigoPostal2);
		}
		if ((codigoPostal != null) && !codigoPostal.isEmpty()) {
			stringBuilder.append(" ");
			stringBuilder.append(codigoPostal);
		}
		return stringBuilder.toString();
	}

	/**
	 * @param codigoPostal
	 *            the codigoPostal to set
	 */
	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	/**
	 * @return the codigoPostal1
	 */
	public Integer getCodigoPostal1() {
		return codigoPostal1;
	}

	/**
	 * @param codigoPostal1
	 *            the codigoPostal1 to set
	 */
	public void setCodigoPostal1(Integer codigoPostal1) {
		this.codigoPostal1 = codigoPostal1;
	}

	/**
	 * @return the codigoPostal2
	 */
	public Integer getCodigoPostal2() {
		return codigoPostal2;
	}

	/**
	 * @param codigoPostal2
	 *            the codigoPostal2 to set
	 */
	public void setCodigoPostal2(Integer codigoPostal2) {
		this.codigoPostal2 = codigoPostal2;
	}

	/**
	 * @return the telefone
	 */
	public String getTelefone() {
		return telefone;
	}

	/**
	 * @param telefone
	 *            the telefone to set
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/**
	 * @return the telemovel
	 */
	public String getTelemovel() {
		return telemovel;
	}

	/**
	 * @param telemovel
	 *            the telemovel to set
	 */
	public void setTelemovel(String telemovel) {
		this.telemovel = telemovel;
	}

	/**
	 * @return the sexo
	 */
	public SEXO getSexo() {
		return sexo;
	}

	/**
	 * @param sexo
	 *            the sexo to set
	 */
	public void setSexo(SEXO sexo) {
		this.sexo = sexo;
	}

	/**
	 * @return the profissao
	 */
	public String getProfissao() {
		return profissao;
	}

	/**
	 * @param profissao
	 *            the profissao to set
	 */
	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the nomePai
	 */
	public String getNomePai() {
		return nomePai;
	}

	/**
	 * @param nomePai
	 *            the nomePai to set
	 */
	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	/**
	 * @return the telefonePai
	 */
	public String getTelefonePai() {
		return telefonePai;
	}

	/**
	 * @param telefonePai
	 *            the telefonePai to set
	 */
	public void setTelefonePai(String telefonePai) {
		this.telefonePai = telefonePai;
	}

	/**
	 * @return the emailPai
	 */
	public String getEmailPai() {
		return emailPai;
	}

	/**
	 * @param emailPai
	 *            the emailPai to set
	 */
	public void setEmailPai(String emailPai) {
		this.emailPai = emailPai;
	}

	/**
	 * @return the nomeMae
	 */
	public String getNomeMae() {
		return nomeMae;
	}

	/**
	 * @param nomeMae
	 *            the nomeMae to set
	 */
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	/**
	 * @return the telefoneMae
	 */
	public String getTelefoneMae() {
		return telefoneMae;
	}

	/**
	 * @param telefoneMae
	 *            the telefoneMae to set
	 */
	public void setTelefoneMae(String telefoneMae) {
		this.telefoneMae = telefoneMae;
	}

	/**
	 * @return the emailMae
	 */
	public String getEmailMae() {
		return emailMae;
	}

	/**
	 * @param emailMae
	 *            the emailMae to set
	 */
	public void setEmailMae(String emailMae) {
		this.emailMae = emailMae;
	}

	/**
	 * @return the nomeEncEducacao
	 */
	public String getNomeEncEducacao() {
		return nomeEncEducacao;
	}

	/**
	 * @param nomeEncEducacao
	 *            the nomeEncEducacao to set
	 */
	public void setNomeEncEducacao(String nomeEncEducacao) {
		this.nomeEncEducacao = nomeEncEducacao;
	}

	/**
	 * @return the telefoneEncEducacao
	 */
	public String getTelefoneEncEducacao() {
		return telefoneEncEducacao;
	}

	/**
	 * @param telefoneEncEducacao
	 *            the telefoneEncEducacao to set
	 */
	public void setTelefoneEncEducacao(String telefoneEncEducacao) {
		this.telefoneEncEducacao = telefoneEncEducacao;
	}

	/**
	 * @return the emailEncEducacao
	 */
	public String getEmailEncEducacao() {
		return emailEncEducacao;
	}

	/**
	 * @param emailEncEducacao
	 *            the emailEncEducacao to set
	 */
	public void setEmailEncEducacao(String emailEncEducacao) {
		this.emailEncEducacao = emailEncEducacao;
	}

	/**
	 * @return the naturalidade
	 */
	public String getNaturalidade() {
		return naturalidade;
	}

	/**
	 * @param naturalidade
	 *            the naturalidade to set
	 */
	public void setNaturalidade(String naturalidade) {
		this.naturalidade = naturalidade;
	}

	/**
	 * @return the totem
	 */
	public String getTotem() {
		return totem;
	}

	/**
	 * @param totem
	 *            the totem to set
	 */
	public void setTotem(String totem) {
		this.totem = totem;
	}

	/**
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * @param activo
	 *            the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * @return the cartao
	 */
	public Boolean getCartao() {
		return cartao;
	}

	/**
	 * @param cartao
	 *            the cartao to set
	 */
	public void setCartao(Boolean cartao) {
		this.cartao = cartao;
	}

	/**
	 * @return the categoria
	 */
	public CATEGORIA getCategoria() {
		return categoria;
	}

	/**
	 * @param categoria
	 *            the categoria to set
	 */
	public void setCategoria(CATEGORIA categoria) {
		this.categoria = categoria;
	}

	/**
	 * @return the dataNascimento
	 */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	/**
	 * @param dataNascimento
	 *            the dataNascimento to set
	 */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * @return the dataAdmissao
	 */
	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	/**
	 * @param dataAdmissao
	 *            the dataAdmissao to set
	 */
	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	/**
	 * @return the dataPromessa
	 */
	public Date getDataPromessa() {
		return dataPromessa;
	}

	/**
	 * @param dataPromessa
	 *            the dataPromessa to set
	 */
	public void setDataPromessa(Date dataPromessa) {
		this.dataPromessa = dataPromessa;
	}

	/**
	 * @return the seccao
	 */
	public SECCAO getSeccao() {
		return seccao;
	}

	/**
	 * @param seccao
	 *            the seccao to set
	 */
	public void setSeccao(SECCAO seccao) {
		this.seccao = seccao;
	}

	/**
	 * @return the observacoes
	 */
	public String getObservacoes() {
		return observacoes;
	}

	/**
	 * @param observacoes
	 *            the observacoes to set
	 */
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	/**
	 * @return the agrupamento
	 */
	public Agrupamento getAgrupamento() {
		return agrupamento;
	}

	/**
	 * @param agrupamento
	 *            the agrupamento to set
	 */
	public void setAgrupamento(Agrupamento agrupamento) {
		this.agrupamento = agrupamento;
	}

	/**
	 * @return the nome
	 */
	public String getNomeProprio() {
		LinkedList<String> l = new LinkedList<>();
		for (String string : getNome().split(" ")) {
			l.add(string);
		}
		return l.getFirst();
	}

	/**
	 * @return the nome
	 */
	public String getNomeApelido() {
		LinkedList<String> l = new LinkedList<>();
		for (String string : getNome().split(" ")) {
			l.add(string);
		}
		return l.getLast();
	}

	/**
	 * @return the email
	 */
	public String getEmailComposto() {
		StringBuilder stringBuilder = new StringBuilder();
		if ((getEmail() != null) && !getEmail().isEmpty()) {
			// stringBuilder.append(getNome());
			// stringBuilder.append(" <");
			stringBuilder.append(getEmail());

		}
		if ((getEmailMae() != null) && !getEmailMae().isEmpty()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(">, ");
				stringBuilder.append(getNomeMae());
				stringBuilder.append(" <");
			}
			stringBuilder.append(getEmailMae());
		}
		if ((getEmailPai() != null) && !getEmailPai().isEmpty()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(">, ");
				stringBuilder.append(getNomePai());
				stringBuilder.append(" <");
			}
			stringBuilder.append(getEmailPai());
		}
		return stringBuilder.toString();
	}
}
