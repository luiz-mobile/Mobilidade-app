package estapar.mobilidade.mobilidadepassageiro;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Geison on 25/09/2015.
 */
public class LocalizacaoLinhaParcelable implements Parcelable {

    public Long idLinha;

    public Long idVeiculo;

    public String numeroLinha;

    public String descricaoLinha;

    public String nomeEmpresa;

    public String numeroRegistro;

    public String latitude;

    public String longitude;

    public String dataHoraRegistro;

    public String distancia;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.idLinha);
        dest.writeValue(this.idVeiculo);
        dest.writeString(this.numeroLinha);
        dest.writeString(this.descricaoLinha);
        dest.writeString(this.nomeEmpresa);
        dest.writeString(this.numeroRegistro);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.dataHoraRegistro);
        dest.writeString(this.distancia);
    }

    public LocalizacaoLinhaParcelable(LocalizacaoLinhaDTO localizacaoLinhaDTO) {
        this.idLinha = localizacaoLinhaDTO.getIdLinha();
        this.idVeiculo = localizacaoLinhaDTO.getIdVeiculo();
        this.numeroLinha = localizacaoLinhaDTO.getNumeroLinha();
        this.descricaoLinha = localizacaoLinhaDTO.getDescricaoLinha();
        this.nomeEmpresa = localizacaoLinhaDTO.getNomeEmpresa();
        this.numeroRegistro = localizacaoLinhaDTO.getNumeroRegistro();
        this.latitude = localizacaoLinhaDTO.getLatitude();
        this.longitude = localizacaoLinhaDTO.getLongitude();
        this.dataHoraRegistro = localizacaoLinhaDTO.getDataHoraRegistro();
        this.distancia = localizacaoLinhaDTO.getDistancia();
    }

    protected LocalizacaoLinhaParcelable(Parcel in) {
        this.idLinha = (Long) in.readValue(Long.class.getClassLoader());
        this.idVeiculo = (Long) in.readValue(Long.class.getClassLoader());
        this.numeroLinha = in.readString();
        this.descricaoLinha = in.readString();
        this.nomeEmpresa = in.readString();
        this.numeroRegistro = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.dataHoraRegistro = in.readString();
        this.distancia = in.readString();
    }

    public static final Parcelable.Creator<LocalizacaoLinhaParcelable> CREATOR = new Parcelable.Creator<LocalizacaoLinhaParcelable>() {
        public LocalizacaoLinhaParcelable createFromParcel(Parcel source) {
            return new LocalizacaoLinhaParcelable(source);
        }

        public LocalizacaoLinhaParcelable[] newArray(int size) {
            return new LocalizacaoLinhaParcelable[size];
        }
    };
}
