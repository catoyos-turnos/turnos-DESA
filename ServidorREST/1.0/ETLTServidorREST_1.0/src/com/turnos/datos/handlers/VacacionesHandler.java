package com.turnos.datos.handlers;

import java.util.ArrayList;
import java.util.Date;

import com.turnos.datos.vo.ErrorBean;
import com.turnos.datos.vo.VacacionesBean;

//76xxxx
public class VacacionesHandler extends GenericHandler {


	//00xx
	public static ArrayList<VacacionesBean> listVacaciones(Object object,
			String codRes, String codTrab, Date fecha_ini, Date fecha_fin,
			ErrorBean errorBean) {
		// TODO Auto-generated method stub
		return null;
	}

	//01xx
	public static VacacionesBean getVacaciones(Object object, String codVacs,
			ErrorBean errorBean) {
		// TODO Auto-generated method stub
		return null;
	}

	//02xx
	public static ArrayList<VacacionesBean> getVacacionesResDia(Object object,
			String codRes, Date fecha, ErrorBean eb) {
		// TODO Auto-generated method stub
		return null;
	}

	//03xx
	public static VacacionesBean insertVacaciones(Object object, String codRes,
			String codTrab, VacacionesBean vacsRaw, ErrorBean errorBean) {
		// TODO Auto-generated method stub
		return null;
	}

	//04xx
	public static VacacionesBean updateVacaciones(Object object,
			String codVacs, VacacionesBean vacsRaw, ErrorBean errorBean) {
		// TODO Auto-generated method stub
		return null;
	}

	//05xx
	public static boolean deleteVacaciones(Object object, String codVacs,
			ErrorBean errorBean) {
		// TODO Auto-generated method stub
		return false;
	}

}
