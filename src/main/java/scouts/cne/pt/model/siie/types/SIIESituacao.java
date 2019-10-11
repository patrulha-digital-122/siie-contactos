package scouts.cne.pt.model.siie.types;

/**
 * @author 62000465 2019-10-04
 *
 */
public enum SIIESituacao
{
	A( "Activo" ),
	E( "Exonerado (disciplina)" ),
	F( "Falecido" ),
	I( "Inactivo" ),
	S( "Suspenso (administrativo)" ),
	T( "Transferido" ),
	V( "Ativo (Formação)" );

	private final String name;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-04
	 * @param name
	 */
	private SIIESituacao( String name )
	{
		this.name = name;
	}

	/**
	 * Getter for name
	 * 
	 * @author 62000465 2019-10-04
	 * @return the name {@link String}
	 */
	public String getName()
	{
		return name;
	}
}
