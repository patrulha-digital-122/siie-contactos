package scouts.cne.pt.model.siie;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 62000465 2019-02-08
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class SIIEElementos
{
	private Long			count;
	private List< SIIEElemento >	data	= new ArrayList<>();
	private Object			dataKeys;
	private Object			result;

	@JsonProperty( "Count" )
	public Long getCount()
	{
		return count;
	}

	@JsonProperty( "Count" )
	public void setCount( Long value )
	{
		this.count = value;
	}

	@JsonProperty( "Data" )
	public List< SIIEElemento > getData()
	{
		return data;
	}

	@JsonProperty( "Data" )
	public void setData( List< SIIEElemento > value )
	{
		this.data = value;
	}

	@JsonProperty( "DataKeys" )
	public Object getDataKeys()
	{
		return dataKeys;
	}

	@JsonProperty( "DataKeys" )
	public void setDataKeys( Object value )
	{
		this.dataKeys = value;
	}

	@JsonProperty( "Result" )
	public Object getResult()
	{
		return result;
	}

	@JsonProperty( "Result" )
	public void setResult( Object value )
	{
		this.result = value;
	}

}
