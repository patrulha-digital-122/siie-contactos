package scouts.cne.pt.model.siie;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 62000465 2019-02-28
 *
 */
public class Datum
{
	private String	nin;
	private String	agrupamento;
	private String	nome;
	private String	datanascimento;
	private String	cartaotxt;
	private Long	anoadmissao;
	private Long	rid;
	private String	morada;
	private String	localidade;
	private String	cp1;
	private String	telefone;
	private String	telemovel;
	private Long	ano;
	private String	cp2;
	private String	codigopostal;
	private String	regiao;
	private String	nucleo;
	private String	dataadmissao;
	private String	siglasituacao;
	private String	siglasexo;
	private String	profissao;
	private String	email;
	private String	notas;
	private String	pai;
	private String	paiprofissao;
	private String	paitelefone;
	private String	mae;
	private String	maeprofissao;
	private String	maetelefone;
	private String	naturalidade;
	private String	enceducacao;
	private String	enceducacaotelefone;
	private String	www;
	private Long	cid;
	private String	siglacategoria;
	private String	categoria;
	private String	observacoes;
	private String	elemnucleo;
	private String	elemregiao;
	private Boolean	elemNacional;
	private Boolean	ccartao;
	private String	datapromessa;
	private String	siglaseccao;
	private Boolean	florlis;
	private String	florlisnin;
	private String	deficiencia;
	private String	sigladeficiencia;
	private String	bcartao;
	private String	bflorlis;
	private Long	idade;
	private Long	ordem;
	private Boolean	formador;
	private Boolean	diretorformacao;
	private String	password;
	private String	paiemail;
	private String	maeemail;
	private String	enceducacaoemail;
	private String	situacao;
	private String	uploadgroup;
	private String	agrupdesc;
	private String	expr1;
	private String	tutor;
	private Boolean	registocriminal;
	private Boolean	expr2;
	private Object	registocriminaldata;
	private String	nif;
	private String	cc;
	private String	totem;
	private String	seccao;

	@JsonProperty( "nin" )
	public String getNin()
	{
		return nin;
	}

	@JsonProperty( "nin" )
	public void setNin( String value )
	{
		this.nin = value;
	}

	@JsonProperty( "agrupamento" )
	public String getAgrupamento()
	{
		return agrupamento;
	}

	@JsonProperty( "agrupamento" )
	public void setAgrupamento( String value )
	{
		this.agrupamento = value;
	}

	@JsonProperty( "nome" )
	public String getNome()
	{
		return nome;
	}

	@JsonProperty( "nome" )
	public void setNome( String value )
	{
		this.nome = value;
	}

	@JsonProperty( "datanascimento" )
	public String getDatanascimento()
	{
		return datanascimento;
	}

	@JsonProperty( "datanascimento" )
	public void setDatanascimento( String value )
	{
		this.datanascimento = value;
	}

	@JsonProperty( "cartaotxt" )
	public String getCartaotxt()
	{
		return cartaotxt;
	}

	@JsonProperty( "cartaotxt" )
	public void setCartaotxt( String value )
	{
		this.cartaotxt = value;
	}

	@JsonProperty( "anoadmissao" )
	public Long getAnoadmissao()
	{
		return anoadmissao;
	}

	@JsonProperty( "anoadmissao" )
	public void setAnoadmissao( Long value )
	{
		this.anoadmissao = value;
	}

	@JsonProperty( "rid" )
	public Long getRid()
	{
		return rid;
	}

	@JsonProperty( "rid" )
	public void setRid( Long value )
	{
		this.rid = value;
	}

	@JsonProperty( "morada" )
	public String getMorada()
	{
		return morada;
	}

	@JsonProperty( "morada" )
	public void setMorada( String value )
	{
		this.morada = value;
	}

	@JsonProperty( "localidade" )
	public String getLocalidade()
	{
		return localidade;
	}

	@JsonProperty( "localidade" )
	public void setLocalidade( String value )
	{
		this.localidade = value;
	}

	@JsonProperty( "cp1" )
	public String getCp1()
	{
		return cp1;
	}

	@JsonProperty( "cp1" )
	public void setCp1( String value )
	{
		this.cp1 = value;
	}

	@JsonProperty( "telefone" )
	public String getTelefone()
	{
		return telefone;
	}

	@JsonProperty( "telefone" )
	public void setTelefone( String value )
	{
		this.telefone = value;
	}

	@JsonProperty( "telemovel" )
	public String getTelemovel()
	{
		return telemovel;
	}

	@JsonProperty( "telemovel" )
	public void setTelemovel( String value )
	{
		this.telemovel = value;
	}

	@JsonProperty( "ano" )
	public Long getAno()
	{
		return ano;
	}

	@JsonProperty( "ano" )
	public void setAno( Long value )
	{
		this.ano = value;
	}

	@JsonProperty( "cp2" )
	public String getCp2()
	{
		return cp2;
	}

	@JsonProperty( "cp2" )
	public void setCp2( String value )
	{
		this.cp2 = value;
	}

	@JsonProperty( "codigopostal" )
	public String getCodigopostal()
	{
		return codigopostal;
	}

	@JsonProperty( "codigopostal" )
	public void setCodigopostal( String value )
	{
		this.codigopostal = value;
	}

	@JsonProperty( "regiao" )
	public String getRegiao()
	{
		return regiao;
	}

	@JsonProperty( "regiao" )
	public void setRegiao( String value )
	{
		this.regiao = value;
	}

	@JsonProperty( "nucleo" )
	public String getNucleo()
	{
		return nucleo;
	}

	@JsonProperty( "nucleo" )
	public void setNucleo( String value )
	{
		this.nucleo = value;
	}

	@JsonProperty( "dataadmissao" )
	public String getDataadmissao()
	{
		return dataadmissao;
	}

	@JsonProperty( "dataadmissao" )
	public void setDataadmissao( String value )
	{
		this.dataadmissao = value;
	}

	@JsonProperty( "siglasituacao" )
	public String getSiglasituacao()
	{
		return siglasituacao;
	}

	@JsonProperty( "siglasituacao" )
	public void setSiglasituacao( String value )
	{
		this.siglasituacao = value;
	}

	@JsonProperty( "siglasexo" )
	public String getSiglasexo()
	{
		return siglasexo;
	}

	@JsonProperty( "siglasexo" )
	public void setSiglasexo( String value )
	{
		this.siglasexo = value;
	}

	@JsonProperty( "profissao" )
	public String getProfissao()
	{
		return profissao;
	}

	@JsonProperty( "profissao" )
	public void setProfissao( String value )
	{
		this.profissao = value;
	}

	@JsonProperty( "email" )
	public String getEmail()
	{
		return email;
	}

	@JsonProperty( "email" )
	public void setEmail( String value )
	{
		this.email = value;
	}

	@JsonProperty( "notas" )
	public String getNotas()
	{
		return notas;
	}

	@JsonProperty( "notas" )
	public void setNotas( String value )
	{
		this.notas = value;
	}

	@JsonProperty( "pai" )
	public String getPai()
	{
		return pai;
	}

	@JsonProperty( "pai" )
	public void setPai( String value )
	{
		this.pai = value;
	}

	@JsonProperty( "paiprofissao" )
	public String getPaiprofissao()
	{
		return paiprofissao;
	}

	@JsonProperty( "paiprofissao" )
	public void setPaiprofissao( String value )
	{
		this.paiprofissao = value;
	}

	@JsonProperty( "paitelefone" )
	public String getPaitelefone()
	{
		return paitelefone;
	}

	@JsonProperty( "paitelefone" )
	public void setPaitelefone( String value )
	{
		this.paitelefone = value;
	}

	@JsonProperty( "mae" )
	public String getMae()
	{
		return mae;
	}

	@JsonProperty( "mae" )
	public void setMae( String value )
	{
		this.mae = value;
	}

	@JsonProperty( "maeprofissao" )
	public String getMaeprofissao()
	{
		return maeprofissao;
	}

	@JsonProperty( "maeprofissao" )
	public void setMaeprofissao( String value )
	{
		this.maeprofissao = value;
	}

	@JsonProperty( "maetelefone" )
	public String getMaetelefone()
	{
		return maetelefone;
	}

	@JsonProperty( "maetelefone" )
	public void setMaetelefone( String value )
	{
		this.maetelefone = value;
	}

	@JsonProperty( "naturalidade" )
	public String getNaturalidade()
	{
		return naturalidade;
	}

	@JsonProperty( "naturalidade" )
	public void setNaturalidade( String value )
	{
		this.naturalidade = value;
	}

	@JsonProperty( "enceducacao" )
	public String getEnceducacao()
	{
		return enceducacao;
	}

	@JsonProperty( "enceducacao" )
	public void setEnceducacao( String value )
	{
		this.enceducacao = value;
	}

	@JsonProperty( "enceducacaotelefone" )
	public String getEnceducacaotelefone()
	{
		return enceducacaotelefone;
	}

	@JsonProperty( "enceducacaotelefone" )
	public void setEnceducacaotelefone( String value )
	{
		this.enceducacaotelefone = value;
	}

	@JsonProperty( "www" )
	public String getWWW()
	{
		return www;
	}

	@JsonProperty( "www" )
	public void setWWW( String value )
	{
		this.www = value;
	}

	@JsonProperty( "cid" )
	public Long getCid()
	{
		return cid;
	}

	@JsonProperty( "cid" )
	public void setCid( Long value )
	{
		this.cid = value;
	}

	@JsonProperty( "siglacategoria" )
	public String getSiglacategoria()
	{
		return siglacategoria;
	}

	@JsonProperty( "siglacategoria" )
	public void setSiglacategoria( String value )
	{
		this.siglacategoria = value;
	}

	@JsonProperty( "categoria" )
	public String getCategoria()
	{
		return categoria;
	}

	@JsonProperty( "categoria" )
	public void setCategoria( String value )
	{
		this.categoria = value;
	}

	@JsonProperty( "observacoes" )
	public String getObservacoes()
	{
		return observacoes;
	}

	@JsonProperty( "observacoes" )
	public void setObservacoes( String value )
	{
		this.observacoes = value;
	}

	@JsonProperty( "elemnucleo" )
	public String getElemnucleo()
	{
		return elemnucleo;
	}

	@JsonProperty( "elemnucleo" )
	public void setElemnucleo( String value )
	{
		this.elemnucleo = value;
	}

	@JsonProperty( "elemregiao" )
	public String getElemregiao()
	{
		return elemregiao;
	}

	@JsonProperty( "elemregiao" )
	public void setElemregiao( String value )
	{
		this.elemregiao = value;
	}

	@JsonProperty( "elemNacional" )
	public Boolean getElemNacional()
	{
		return elemNacional;
	}

	@JsonProperty( "elemNacional" )
	public void setElemNacional( Boolean value )
	{
		this.elemNacional = value;
	}

	@JsonProperty( "ccartao" )
	public Boolean getCcartao()
	{
		return ccartao;
	}

	@JsonProperty( "ccartao" )
	public void setCcartao( Boolean value )
	{
		this.ccartao = value;
	}

	@JsonProperty( "datapromessa" )
	public String getDatapromessa()
	{
		return datapromessa;
	}

	@JsonProperty( "datapromessa" )
	public void setDatapromessa( String value )
	{
		this.datapromessa = value;
	}

	@JsonProperty( "siglaseccao" )
	public String getSiglaseccao()
	{
		return siglaseccao;
	}

	@JsonProperty( "siglaseccao" )
	public void setSiglaseccao( String value )
	{
		this.siglaseccao = value;
	}

	@JsonProperty( "florlis" )
	public Boolean getFlorlis()
	{
		return florlis;
	}

	@JsonProperty( "florlis" )
	public void setFlorlis( Boolean value )
	{
		this.florlis = value;
	}

	@JsonProperty( "florlisnin" )
	public String getFlorlisnin()
	{
		return florlisnin;
	}

	@JsonProperty( "florlisnin" )
	public void setFlorlisnin( String value )
	{
		this.florlisnin = value;
	}

	@JsonProperty( "deficiencia" )
	public String getDeficiencia()
	{
		return deficiencia;
	}

	@JsonProperty( "deficiencia" )
	public void setDeficiencia( String value )
	{
		this.deficiencia = value;
	}

	@JsonProperty( "sigladeficiencia" )
	public String getSigladeficiencia()
	{
		return sigladeficiencia;
	}

	@JsonProperty( "sigladeficiencia" )
	public void setSigladeficiencia( String value )
	{
		this.sigladeficiencia = value;
	}

	@JsonProperty( "bcartao" )
	public String getBcartao()
	{
		return bcartao;
	}

	@JsonProperty( "bcartao" )
	public void setBcartao( String value )
	{
		this.bcartao = value;
	}

	@JsonProperty( "bflorlis" )
	public String getBflorlis()
	{
		return bflorlis;
	}

	@JsonProperty( "bflorlis" )
	public void setBflorlis( String value )
	{
		this.bflorlis = value;
	}

	@JsonProperty( "idade" )
	public Long getIdade()
	{
		return idade;
	}

	@JsonProperty( "idade" )
	public void setIdade( Long value )
	{
		this.idade = value;
	}

	@JsonProperty( "ordem" )
	public Long getOrdem()
	{
		return ordem;
	}

	@JsonProperty( "ordem" )
	public void setOrdem( Long value )
	{
		this.ordem = value;
	}

	@JsonProperty( "formador" )
	public Boolean getFormador()
	{
		return formador;
	}

	@JsonProperty( "formador" )
	public void setFormador( Boolean value )
	{
		this.formador = value;
	}

	@JsonProperty( "diretorformacao" )
	public Boolean getDiretorformacao()
	{
		return diretorformacao;
	}

	@JsonProperty( "diretorformacao" )
	public void setDiretorformacao( Boolean value )
	{
		this.diretorformacao = value;
	}

	@JsonProperty( "password" )
	public String getPassword()
	{
		return password;
	}

	@JsonProperty( "password" )
	public void setPassword( String value )
	{
		this.password = value;
	}

	@JsonProperty( "paiemail" )
	public String getPaiemail()
	{
		return paiemail;
	}

	@JsonProperty( "paiemail" )
	public void setPaiemail( String value )
	{
		this.paiemail = value;
	}

	@JsonProperty( "maeemail" )
	public String getMaeemail()
	{
		return maeemail;
	}

	@JsonProperty( "maeemail" )
	public void setMaeemail( String value )
	{
		this.maeemail = value;
	}

	@JsonProperty( "enceducacaoemail" )
	public String getEnceducacaoemail()
	{
		return enceducacaoemail;
	}

	@JsonProperty( "enceducacaoemail" )
	public void setEnceducacaoemail( String value )
	{
		this.enceducacaoemail = value;
	}

	@JsonProperty( "situacao" )
	public String getSituacao()
	{
		return situacao;
	}

	@JsonProperty( "situacao" )
	public void setSituacao( String value )
	{
		this.situacao = value;
	}

	@JsonProperty( "uploadgroup" )
	public String getUploadgroup()
	{
		return uploadgroup;
	}

	@JsonProperty( "uploadgroup" )
	public void setUploadgroup( String value )
	{
		this.uploadgroup = value;
	}

	@JsonProperty( "agrupdesc" )
	public String getAgrupdesc()
	{
		return agrupdesc;
	}

	@JsonProperty( "agrupdesc" )
	public void setAgrupdesc( String value )
	{
		this.agrupdesc = value;
	}

	@JsonProperty( "expr1" )
	public String getExpr1()
	{
		return expr1;
	}

	@JsonProperty( "expr1" )
	public void setExpr1( String value )
	{
		this.expr1 = value;
	}

	@JsonProperty( "tutor" )
	public String getTutor()
	{
		return tutor;
	}

	@JsonProperty( "tutor" )
	public void setTutor( String value )
	{
		this.tutor = value;
	}

	@JsonProperty( "registocriminal" )
	public Boolean getRegistocriminal()
	{
		return registocriminal;
	}

	@JsonProperty( "registocriminal" )
	public void setRegistocriminal( Boolean value )
	{
		this.registocriminal = value;
	}

	@JsonProperty( "expr2" )
	public Boolean getExpr2()
	{
		return expr2;
	}

	@JsonProperty( "expr2" )
	public void setExpr2( Boolean value )
	{
		this.expr2 = value;
	}

	@JsonProperty( "registocriminaldata" )
	public Object getRegistocriminaldata()
	{
		return registocriminaldata;
	}

	@JsonProperty( "registocriminaldata" )
	public void setRegistocriminaldata( Object value )
	{
		this.registocriminaldata = value;
	}

	@JsonProperty( "nif" )
	public String getNif()
	{
		return nif;
	}

	@JsonProperty( "nif" )
	public void setNif( String value )
	{
		this.nif = value;
	}

	@JsonProperty( "cc" )
	public String getCc()
	{
		return cc;
	}

	@JsonProperty( "cc" )
	public void setCc( String value )
	{
		this.cc = value;
	}

	@JsonProperty( "totem" )
	public String getTotem()
	{
		return totem;
	}

	@JsonProperty( "totem" )
	public void setTotem( String value )
	{
		this.totem = value;
	}

	@JsonProperty( "seccao" )
	public String getSeccao()
	{
		return seccao;
	}

	@JsonProperty( "seccao" )
	public void setSeccao( String value )
	{
		this.seccao = value;
	}
}
