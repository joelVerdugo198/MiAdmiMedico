package help.com.miadmimedico;

public class ClaseSeguimiento {
    private String idSeguimiento = "";
    private int generadorId = 0;
    private String idMedicamento = "";
    private String idAlarma = "";
    private String tipo = "";
    private String nombreMedicamento = "";
    private String cantidadPorcion = "";
    private String tipoPorcion = "";
    private String cantidadMedicamento = "";
    private String viaAdministracion = "";
    private String cantidadTotal = "";
    private String cantidadTomada = "";
    private String intervaloHora = "";
    private String alarmaConfirmda = "";
    private String envioSmsContacto = "";
    private String horaAlarma = "";


    public ClaseSeguimiento(String idSeguimiento, int generadorId,
                            String idMedicamento, String tipo,
                            String nombreMedicamento, String cantidadPorcion,
                            String tipoPorcion, String cantidadMedicamento,
                            String viaAdministracion, String cantidadTotal,
                            String cantidadTomada, String intervaloHora,
                            String alarmaConfirmda, String envioSmsContacto) {
        this.idSeguimiento = idSeguimiento;
        this.generadorId = generadorId;
        this.idMedicamento = idMedicamento;
        this.tipo = tipo;
        this.nombreMedicamento = nombreMedicamento;
        this.cantidadPorcion = cantidadPorcion;
        this.tipoPorcion = tipoPorcion;
        this.cantidadMedicamento = cantidadMedicamento;
        this.viaAdministracion = viaAdministracion;
        this.cantidadTotal = cantidadTotal;
        this.cantidadTomada = cantidadTomada;
        this.intervaloHora = intervaloHora;
        this.alarmaConfirmda = alarmaConfirmda;
        this.envioSmsContacto = envioSmsContacto;
    }

    public ClaseSeguimiento() {

    }

    public String getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(String idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public int getGeneradorId() {
        return generadorId;
    }

    public void setGeneradorId(int generadorId) {
        this.generadorId = generadorId;
    }

    public String getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(String idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getViaAdministracion() {
        return viaAdministracion;
    }

    public void setViaAdministracion(String viaAdministracion) {
        this.viaAdministracion = viaAdministracion;
    }

    public String getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(String cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String getCantidadTomada() {
        return cantidadTomada;
    }

    public void setCantidadTomada(String cantidadTomada) {
        this.cantidadTomada = cantidadTomada;
    }

    public String getIntervaloHora() {
        return intervaloHora;
    }

    public void setIntervaloHora(String intervaloHora) {
        this.intervaloHora = intervaloHora;
    }

    public String getAlarmaConfirmda() {
        return alarmaConfirmda;
    }

    public void setAlarmaConfirmda(String alarmaConfirmda) {
        this.alarmaConfirmda = alarmaConfirmda;
    }

    public String getEnvioSmsContacto() {
        return envioSmsContacto;
    }

    public void setEnvioSmsContacto(String envioSmsContacto) {
        this.envioSmsContacto = envioSmsContacto;
    }

    public String getHoraAlarma() {
        return horaAlarma;
    }

    public void setHoraAlarma(String horaAlarma) {
        this.horaAlarma = horaAlarma;
    }

    public String getIdAlarma() {
        return idAlarma;
    }

    public void setIdAlarma(String idAlarma) {
        this.idAlarma = idAlarma;
    }
}