package scouts.cne.pt.model.siie.types;

public enum SIIESeccao
{
	D( "Dirigentes" ),
	L( "Lobitos" ),
	E( "Exploradores / Moços" ),
	P( "Pioneiros / Marinheiros" ),
	C( "Caminheiros / Companheiros" ),
	A( "Sem secção atribuida" );
	private String nome;

	private SIIESeccao( String nome )
	{
		this.nome = nome;
	}

	/**
	 * @return the nome
	 */
	public String getNome()
	{
		return nome;
	}
}
