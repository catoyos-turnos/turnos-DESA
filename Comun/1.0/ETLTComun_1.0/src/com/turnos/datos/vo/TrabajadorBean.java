package com.turnos.datos.vo;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name = "trabajador")
@JsonRootName(value = "trabajador")
public class TrabajadorBean extends ETLTBean {
	private static final long serialVersionUID = 74L;
	private String codigo;
	private String codResidencia;
	private String nombre;
	private String apellidos;
	
	public TrabajadorBean() {
		super(TrabajadorBean.class);
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodResidencia() {
		return codResidencia;
	}

	public void setCodResidencia(String codResidencia) {
		this.codResidencia = codResidencia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

}
