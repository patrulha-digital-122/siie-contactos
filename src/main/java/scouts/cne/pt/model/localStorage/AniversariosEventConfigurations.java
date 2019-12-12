package scouts.cne.pt.model.localStorage;

/**
 * @author 62000465 2019-12-11
 *
 */
public class AniversariosEventConfigurations
{
	private boolean	bReceberNotificacoes			= false;
	private boolean	bElementosActivos				= false;
	private boolean	bElementosProprioAgrupamento	= false;

	/**
	 * Getter for receberNotificacoes
	 * 
	 * @author 62000465 2019-12-11
	 * @return the receberNotificacoes {@link boolean}
	 */
	public boolean isReceberNotificacoes()
	{
		return bReceberNotificacoes;
	}

	/**
	 * Setter for receberNotificacoes
	 * 
	 * @author 62000465 2019-12-11
	 * @param receberNotificacoes the receberNotificacoes to set
	 */
	public void setReceberNotificacoes( boolean receberNotificacoes )
	{
		bReceberNotificacoes = receberNotificacoes;
	}

	/**
	 * Getter for elementosActivos
	 * 
	 * @author 62000465 2019-12-11
	 * @return the elementosActivos {@link boolean}
	 */
	public boolean isElementosActivos()
	{
		return bElementosActivos;
	}

	/**
	 * Setter for elementosActivos
	 * 
	 * @author 62000465 2019-12-11
	 * @param elementosActivos the elementosActivos to set
	 */
	public void setElementosActivos( boolean elementosActivos )
	{
		bElementosActivos = elementosActivos;
	}

	/**
	 * Getter for elementosProprioAgrupamento
	 * 
	 * @author 62000465 2019-12-11
	 * @return the elementosProprioAgrupamento {@link boolean}
	 */
	public boolean isElementosProprioAgrupamento()
	{
		return bElementosProprioAgrupamento;
	}

	/**
	 * Setter for elementosProprioAgrupamento
	 * 
	 * @author 62000465 2019-12-11
	 * @param elementosProprioAgrupamento the elementosProprioAgrupamento to set
	 */
	public void setElementosProprioAgrupamento( boolean elementosProprioAgrupamento )
	{
		bElementosProprioAgrupamento = elementosProprioAgrupamento;
	}
}
