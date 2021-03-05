package help.com.miadmimedico;

public class ClaseMedicamento {

    private String idMedicamento = "";
    private String nombreMedicamento = "";
    private String cantidadPorcion = "";
    private String tipoPorcion = "";
    private String cantidadMedicamento = "";
    private String intervaloHora = "";
    private String dias = "";
    private String viaAdministracion = "";

    public ClaseMedicamento(String idMedicamento, String nombreMedicamento,
                            String cantidadPorcion, String tipoPorcion,
                            String cantidadMedicamento, String intervaloHora,
                            String dias, String viaAdministracion) {

        this.idMedicamento = idMedicamento;
        this.nombreMedicamento = nombreMedicamento;
        this.cantidadPorcion = cantidadPorcion;
        this.tipoPorcion = tipoPorcion;
        this.cantidadMedicamento = cantidadMedicamento;
        this.intervaloHora = intervaloHora;
        this.dias = dias;
        this.viaAdministracion = viaAdministracion;

    }

    public ClaseMedicamento() {

    }

    public String getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(String idMedicamento) {
        this.idMedicamento = idMedicamento;
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
}
