package scouts.cne.pt.app;

/**
 *
 */
/**
 * HasLogger is a feature interface that provides Logging capability for anyone implementing it where logger needs to
 * operate in serializable environment without being static.
 * 
 * @author anco62000465 2018-04-19
 */
public interface HasInternalization
{
	// default String getI18NString( String key )
	// {
	// return ConfigurationRepository.getInstance().getI18NString( key );
	// }
	//
	// default String getI18NString( String key, Object... values )
	// {
	// return MessageFormat.format( ConfigurationRepository.getInstance().getI18NString( key ), values );
	// }
	//
	// default VaadinIcons getI18NIcon( String key )
	// {
	// try
	// {
	// String i18nIconKey = getI18NString( key );
	// if ( StringUtils.isNotBlank( i18nIconKey ) )
	// {
	// return VaadinIcons.valueOf( i18nIconKey.toUpperCase() );
	// }
	// }
	// catch ( Exception e )
	// {
	// LoggerFactory.getLogger( getClass() ).error( e.getMessage() );
	// }
	// return null;
	// }
}
