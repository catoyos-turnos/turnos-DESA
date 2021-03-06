package com.turnos.datos.vo;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name = "turno_dia")
@JsonRootName(value = "turno_dia")
public class TurnoTrabajadorDiaBean extends ETLTBean {
	private static final long serialVersionUID = 74L;
	private TrabajadorBean trabajador;
	private ServicioBean servicio;
	private TurnoBean turno;
	private Date fecha;
	
	public TurnoTrabajadorDiaBean() {
		super(TurnoTrabajadorDiaBean.class);
	}

	public TrabajadorBean getTrabajador() {
		return trabajador;
	}

	public void setTrabajador(TrabajadorBean trabajador) {
		this.trabajador = trabajador;
	}

	public ServicioBean getServicio() {
		return servicio;
	}

	public void setServicio(ServicioBean servicio) {
		this.servicio = servicio;
	}

	public TurnoBean getTurno() {
		return turno;
	}

	public void setTurno(TurnoBean turno) {
		this.turno = turno;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
