package estapar.mobilidade.mobilidadepassageiro;

import java.io.Serializable;
import java.util.Date;




public class LocalizacaoLinhaDTO extends InformationRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idLinha;
	
	private Long idVeiculo;

	private String numeroLinha;
	
	private String descricaoLinha;
	
	private String nomeEmpresa;

	private String numeroRegistro;

	private String latitude;

	private String longitude;

	private String dataHoraRegistro;
	
	private String linhaFavorita;

	private String distancia;
	
	public LocalizacaoLinhaDTO() {}
	
	public LocalizacaoLinhaDTO(Long idLinha, String numeroLinha, String descricaoLinha, String nomeEmpresa, String numeroRegistro,
                               String latitude, String longitude, Date dataHoraRegistro, String linhaFavorita) {
		this(idLinha, numeroLinha, descricaoLinha, nomeEmpresa, numeroRegistro, latitude, longitude, dataHoraRegistro, linhaFavorita, null);
	}
	
	public LocalizacaoLinhaDTO(Long idLinha, String numeroLinha, String descricaoLinha, String nomeEmpresa, String numeroRegistro,
                               String latitude, String longitude, Date dataHoraRegistro, String linhaFavorita, String distancia) {
		this.idLinha = idLinha;
		this.numeroLinha = numeroLinha;
		this.descricaoLinha = descricaoLinha;
		this.nomeEmpresa = nomeEmpresa;
		this.numeroRegistro = numeroRegistro;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dataHoraRegistro = Dates.format(dataHoraRegistro, Dates.FORMAT_PT_BR_DATE_HOUR);
		this.linhaFavorita = linhaFavorita;
		this.distancia = distancia;
	}

	public LocalizacaoLinhaDTO(String message) {
		setMessage(message);
	}

	public Long getIdLinha() {
		return idLinha;
	}

	public void setIdLinha(Long idLinha) {
		this.idLinha = idLinha;
	}

	public Long getIdVeiculo() {
		return idVeiculo;
	}

	public void setIdVeiculo(Long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

	public String getNumeroLinha() {
		return numeroLinha;
	}

	public void setNumeroLinha(String numeroLinha) {
		this.numeroLinha = numeroLinha;
	}

	public String getDescricaoLinha() {
		return descricaoLinha;
	}

	public void setDescricaoLinha(String descricaoLinha) {
		this.descricaoLinha = descricaoLinha;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}

	public String getNumeroRegistro() {
		return numeroRegistro;
	}

	public void setNumeroRegistro(String numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getDataHoraRegistro() {
		return dataHoraRegistro;
	}

	public void setDataHoraRegistro(String dataHoraRegistro) {
		this.dataHoraRegistro = dataHoraRegistro;
	}
	
	public String getLinhaFavorita() {
		return linhaFavorita;
	}

	public void setLinhaFavorita(String linhaFavorita) {
		this.linhaFavorita = linhaFavorita;
	}

	public String getDistancia() {
		return distancia;
	}

	public void setDistancia(String distancia) {
		this.distancia = distancia;
	}
}
