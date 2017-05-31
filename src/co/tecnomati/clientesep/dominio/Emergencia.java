package co.tecnomati.clientesep.dominio;

import java.util.Date;

public class Emergencia {
	private int usid;
	private int sid;
	private String latitud;
	private String longitud;
    private int precision;
    private String fecha_loc;
    private String latitud_b;
	private String longitud_b;
    private int precision_b;
    private String fecha_loc_b;  
    private int estado;
	private String comentarioU;
	private String comentarioS;
	private String rutaImagen;
	private String direccion;
	private String ciudad;
	private String fecha;
	private String hora;


	

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * @return the fecha_loc
	 */
	public String getFecha_loc() {
		return fecha_loc;
	}

	/**
	 * @param fecha_loc the fecha_loc to set
	 */
	public void setFecha_loc(String fecha_loc) {
		this.fecha_loc = fecha_loc;
	}

	/**
	 * @return the latitud_b
	 */
	public String getLatitud_b() {
		return latitud_b;
	}

	/**
	 * @param latitud_b the latitud_b to set
	 */
	public void setLatitud_b(String latitud_b) {
		this.latitud_b = latitud_b;
	}

	/**
	 * @return the longitud_b
	 */
	public String getLongitud_b() {
		return longitud_b;
	}

	/**
	 * @param longitud_b the longitud_b to set
	 */
	public void setLongitud_b(String longitud_b) {
		this.longitud_b = longitud_b;
	}

	/**
	 * @return the precision_b
	 */
	public int getPrecision_b() {
		return precision_b;
	}

	/**
	 * @param precision_b the precision_b to set
	 */
	public void setPrecision_b(int precision_b) {
		this.precision_b = precision_b;
	}

	/**
	 * @return the fecha_loc_b
	 */
	public String getFecha_loc_b() {
		return fecha_loc_b;
	}

	/**
	 * @param fecha_loc_b the fecha_loc_b to set
	 */
	public void setFecha_loc_b(String fecha_loc_b) {
		this.fecha_loc_b = fecha_loc_b;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public int getUsid() {
		return usid;
	}

	public void setUsid(int usid) {
		this.usid = usid;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getComentarioU() {
		return comentarioU;
	}

	public void setComentarioU(String comentarioU) {
		this.comentarioU = comentarioU;
	}

	public String getComentarioS() {
		return comentarioS;
	}

	public void setComentarioS(String comentarioS) {
		this.comentarioS = comentarioS;
	}

	public String getRutaImagen() {
		return rutaImagen;
	}

	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}

	@Override
	public String toString() {
		return "Emergencia [usid=" + usid + ", sid=" + sid + ", latitud="
				+ latitud + ", longitud=" + longitud + ", estado=" + estado
				+ ", comentarioU=" + comentarioU + ", comentarioS="
				+ comentarioS + ", rutaImagen=" + rutaImagen + ", direccion="
				+ direccion + ", ciudad=" + ciudad + ", fecha=" + fecha
				+ ", hora=" + hora + "]";
	}

	

}
