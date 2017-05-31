package co.tecnomati.clientesep.dominio;

public class Usuario {

String 	idUser;
String	user;
String	telefono;
String	contacto1;
String	contacto2;
String	idGCM;
boolean vibrar;
String domicilio;
String comentario;
boolean monitoreo;
/**
 * @return the monitoreo
 */
public boolean isMonitoreo() {
	return monitoreo;
}
/**
 * @param monitoreo the monitoreo to set
 */
public void setMonitoreo(boolean monitoreo) {
	this.monitoreo = monitoreo;
}
public String getIdUser() {
	return idUser;
}
public void setIdUser(String idUser) {
	this.idUser = idUser;
}
public String getUser() {
	return user;
}
public void setUser(String user) {
	this.user = user;
}
public String getTelefono() {
	return telefono;
}
public void setTelefono(String telefono) {
	this.telefono = telefono;
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
public String getIdGCM() {
	return idGCM;
}
public void setIdGCM(String idGCM) {
	this.idGCM = idGCM;
}
public boolean isVibrar() {
	return vibrar;
}
public void setVibrar(boolean vibrar) {
	this.vibrar = vibrar;
}

public String getDomicilio() {
	return domicilio;
}
public void setDomicilio(String domicilio) {
	this.domicilio = domicilio;
}
public String getComentario() {
	return comentario;
}
public void setComentario(String comentario) {
	this.comentario = comentario;
}
@Override
public String toString() {
	return "Usuario [idUser=" + idUser + ", user=" + user + ", telefono="
			+ telefono + ", contacto1=" + contacto1 + ", contacto2="
			+ contacto2 + ", idGCM=" + idGCM + ", vibrar=" + vibrar
			+ ", domicilio=" + domicilio + ", comentario=" + comentario + "]";
}




}	