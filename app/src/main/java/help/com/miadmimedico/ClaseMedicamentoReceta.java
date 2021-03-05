package help.com.miadmimedico;

public class ClaseMedicamentoReceta {

    private String idMedicamento = "";
    private String tipoReceta = "";
    private String cedulaMedica = "";
    private String nombreMedicamento = "";
    private String cantidadPorcion = "";
    private String tipoPorcion = "";
    private String cantidadMedicamento = "";
    private String intervaloHora = "";
    private String dias = "";
    private String viaAdministracion = "";
    private String contacto1 = "";
    private String contacto2 = "";
    private String contacto3 = "";

    public ClaseMedicamentoReceta(String idMedicamento, String tipoReceta, String cedulaMedica, String nombreMedicamento,
                                  String cantidadPorcion, String tipoPorcion, String cantidadMedicamento,
                                  String intervaloHora, String dias, String viaAdministracion,
                                  String contacto1, String contacto2, String contacto3) {

        this.idMedicamento = idMedicamento;
        this.tipoReceta = tipoReceta;
        this.cedulaMedica = cedulaMedica;
        this.nombreMedicamento = nombreMedicamento;
        this.cantidadPorcion = cantidadPorcion;
        this.tipoPorcion = tipoPorcion;
        this.cantidadMedicamento = cantidadMedicamento;
        this.intervaloHora = intervaloHora;
        this.dias = dias;
        this.viaAdministracion = viaAdministracion;
        this.contacto1 = contacto1;
        this.contacto2 = contacto2;
        this.contacto3 = contacto3;

    }

    public ClaseMedicamentoReceta() {

    }


    public String getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(String idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getTipoReceta() {
        return tipoReceta;
    }

    public void setTipoReceta(String tipoReceta) {
        this.tipoReceta = tipoReceta;
    }

    public String getCedulaMedica() {
        return cedulaMedica;
    }

    public void setCedulaMedica(String cedulaMedica) {
        this.cedulaMedica = cedulaMedica;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }

    public String getCantidadPorcion() {
        return cantidadPorcion;
    }

    public void setCantidadPorcion(String cantidadPorcion) {
        this.cantidadPorcion = cantidadPorcion;
    }

    public String getTipoPorcion() {
        return tipoPorcion;
    }

    public void setTipoPorcion(String tipoPorcion) {
        this.tipoPorcion = tipoPorcion;
    }

    public String getCantidadMedicamento() {
        return cantidadMedicamento;
    }

    public void setCantidadMedicamento(String cantidadMedicamento) {
        this.cantidadMedicamento = cantidadMedicamento;
    }

    public String getIntervaloHora() {
        return intervaloHora;
    }

    public void setIntervaloHora(String intervaloHora) {
        this.intervaloHora = intervaloHora;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getViaAdministracion() {
        return viaAdministracion;
    }

    public void setViaAdministracion(String viaAdministracion) {
        this.viaAdministracion = viaAdministracion;
    }

    public String getContacto1() {
        return contacto1;
    }

    public void setContacto1(String contacto1) {
        this.contacto1 = contacto1;
    }

    public String getContacto2() {
        return contacto2;
    }

    public void setContacto2(String contacto2) {
        this.contacto2 = contacto2;
    }

    public String getContacto3() {
        return contacto3;
    }

    public void setContacto3(String contacto3) {
        this.contacto3 = contacto3;
    }
}
