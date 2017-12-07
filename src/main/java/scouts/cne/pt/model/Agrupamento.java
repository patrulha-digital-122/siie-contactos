package scouts.cne.pt.model;

import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.InternetAddress;

/**
 *
 * The User JPA entity.
 *
 */
public class Agrupamento  {

	/**
	 *
	 */
	private static final long serialVersionUID = -3766844852597424858L;

	public static final String FIND_BY_ID = "agrupamento.findById";

	private String idCNE;
	private String nome;
	private String patrono;
	private InternetAddress email;

	private Set<Elemento> elementos = new HashSet<Elemento>();

	public Agrupamento() {
		super();
	}

	public Agrupamento(String id) {
		super();
		this.idCNE = id;
	}

	/**
	 * @return the idCNE
	 */
	public String getIdCNE() {
		return idCNE;
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
	 * @return the patrono
	 */
	public String getPatrono() {
		return patrono;
	}

	/**
	 * @param patrono
	 *            the patrono to set
	 */
	public void setPatrono(String patrono) {
		this.patrono = patrono;
	}

	/**
	 * @return the email
	 */
	public InternetAddress getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(InternetAddress email) {
		this.email = email;
	}



	/**
	 * @return the elementos
	 */
	public Set<Elemento> getElementos() {
		return elementos;
	}

	/**
	 * @param elementos
	 *            the elementos to set
	 */
	public void setElementos(Set<Elemento> elementos) {
		this.elementos = elementos;
	}
}
