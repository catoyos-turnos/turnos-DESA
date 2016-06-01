package com.turnos.cliente.modelo;

import java.io.Serializable;
import java.util.List;

import com.turnos.datos.vo.ServicioBean;
import com.turnos.datos.vo.TurnoBean;
import com.turnos.datos.vo.TurnoBean.TipoTurno;

public class Turno implements Serializable {
	private static final long serialVersionUID = 47L;
	
	private TurnoBean beanOriginal;
	private TurnoBean beanAux;
	private boolean flagNueva;
	private boolean flagModificada;
	private boolean flagBorrada;
	
	private Turno(TurnoBean residencia, boolean nueva) {
		this.beanOriginal = residencia;
		this.flagNueva = nueva;
		this.flagModificada = false;
		this.flagBorrada = false;
		this.beanAux = new TurnoBean();
	}

	public static List<Turno> listaTurnos (String codRes) {
		//TODO
		return null;
	}

	public static Turno getTurno (String codRes, String codTurno) {
		//TODO
		return null;
	}

	public static Turno nuevoTurno(TurnoBean turnoRaw, String codRes) {
		// TODO
		return null;
	}

	public static Turno modTurno(TurnoBean turnoRaw, String codRes, String codTurno) {
		// TODO
		return null;
	}

	public static Turno borraTurno(String codRes, String codTurno) {
		//TODO
		return null;
	}

	public List<Servicio> getServicios (String codRes, String codTurno) {
		//TODO
		return null;
	}

	public boolean addServicio (String codRes, ServicioBean servRaw, String codTurno) {
		//TODO
		return false;
	}

	public boolean deleteServicio (String codRes, String codTurno) {
		//TODO
		return false;
	}
	
	
	public String getCodTurno() {
		if(flagBorrada)
			return null;
		else if(flagNueva)
			return beanOriginal.getCodTurno();
		else if(flagModificada && beanAux.getCodTurno()!=null)
			return beanAux.getCodTurno();
		else return beanOriginal.getCodTurno();
	}

	public String getCodResidencia() {
		if(flagBorrada)
			return null;
		else if(flagNueva)
			return beanOriginal.getCodResidencia();
		else if(flagModificada && beanAux.getCodResidencia()!=null)
			return beanAux.getCodResidencia();
		else return beanOriginal.getCodResidencia();
	}

	public TipoTurno getTipo() {
		if(flagBorrada)
			return null;
		else if(flagNueva)
			return TipoTurno.safeValueOf(beanOriginal.getTipo());
		else if(flagModificada && beanAux.getTipo()!=null)
			return TipoTurno.safeValueOf(beanAux.getTipo());
		else return TipoTurno.safeValueOf(beanOriginal.getTipo());
	}

	public void setCodTurno(String codTurno) {
		if(flagBorrada)
			;
		else if(flagNueva)
			beanOriginal.setCodTurno(codTurno);
		else {
			flagModificada = true;
			beanAux.setCodTurno(codTurno);
		}
	}

	public void setCodResidencia(String codResidencia) {
		if(flagBorrada)
			;
		else if(flagNueva)
			beanOriginal.setCodResidencia(codResidencia);
		else {
			flagModificada = true;
			beanAux.setCodResidencia(codResidencia);
		}
	}

	public void setTipo(TipoTurno tipo) {
		if(flagBorrada)
			;
		else if(flagNueva)
			beanOriginal.setTipo(tipo.name());
		else {
			flagModificada = true;
			beanAux.setTipo(tipo.name());
		}
	}


	public static Turno genera(TurnoBean bean) {
		if (bean == null) {
			return null;
		} else return new Turno(bean, false);
	}

	public static Turno nuevo() {
		return new Turno(new TurnoBean(), true);
	}

}
