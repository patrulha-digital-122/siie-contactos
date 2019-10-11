package scouts.cne.pt.model.siie.types;

/**
 * @author 62000465 2019-10-04
 *
 */
public enum SIIECategoria
{
	AL( "Aspirante a Lobito" ),
	L( "Lobito" ),
	AE( "Aspirante a Explorador / Moço" ),
	NE( "Noviço Explorador / Moço" ),
	E( "Explorador / Moço" ),
	AP( "Aspirante a Pioneiro / Marinheiro" ),
	NP( "Noviço Pioneiro / Marinheiro" ),
	P( "Pioneiro / Marinheiro" ),
	AC( "Aspirante a Caminheiro / Companheiro" ),
	NC( "Noviço Caminheiro / Companheiro" ),
	C( "Caminheiro / Companheiro" ),
	CIL( "Caminheiro / Companheiro com Insígnia de Ligação" ),
	ND( "Noviço a Dirigente" ),
	CD( "Candidato a Dirigente" ),
	AD( "Aspirante a Dirigente" ),
	D( "Dirigente" ),
	DH( "Dirigente Honorario" ),
	AUX( "Auxiliar" ),
	AUXFNA( "Auxiliar - FNA" ),
	Bene( "Benemérito" ),
	I( "Inactivo" );

	private final String name;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-04
	 * @param name
	 */
	private SIIECategoria( String name )
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
