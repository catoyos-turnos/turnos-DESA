package com.turnos.datos.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.core.Response.Status;

import com.turnos.datos.fabricas.ErrorBeanFabrica;
import com.turnos.datos.vo.ErrorBean;
import com.turnos.datos.vo.FestivoBean;
import com.turnos.datos.vo.FestivoBean.TipoFiesta;

public class FestivoHandler extends GenericHandler {
	
	private static final int LOC_H = 80;
	
	private static final String QUERY_ES_FESTIVO_EN_MUNI =
		"SELECT count(*) as festivo "
		+ "FROM dia_festivo fest, geo_municipio muni "
			+ "INNER JOIN geo_provincia prov ON muni.id_provincia=prov.id_provincia "
			+ "INNER JOIN geo_pais pais ON muni.cod_pais=pais.cod_pais "
		+ "WHERE muni.id_municipio=? AND fest.fecha=? AND ("
			+ "(fest.tipo='LOCAL' AND fest.id_municipio=muni.id_municipio) "
			+ "OR (fest.tipo='AUTONOMICA' AND fest.id_provincia=muni.id_provincia) "
			+ "OR (fest.tipo='NACIONAL' AND fest.cod_pais=muni.cod_pais) "
		+ ")";
	
	private static final String QUERY_GET_LISTA_FESTIVOS_MUNI = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "muni.id_municipio as municipioCod, muni.nombre as municipioNombre, "
			+ "prov.id_provincia as provinciaCod, prov.provincia as provinciaNombre, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest, geo_municipio muni "
			+ "INNER JOIN geo_provincia prov ON muni.id_provincia=prov.id_provincia "
			+ "INNER JOIN geo_pais pais ON muni.cod_pais=pais.cod_pais "
		+ "WHERE muni.id_municipio=? AND ("
				+ "(fest.tipo='LOCAL' AND fest.id_municipio=muni.id_municipio) "
			+ ") %s "
		+ "ORDER BY fest.fecha, fest.tipo";
	
	private static final String QUERY_GET_LISTA_FESTIVOS_MUNI_COMPL = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "muni.id_municipio as municipioCod, muni.nombre as municipioNombre, "
			+ "prov.id_provincia as provinciaCod, prov.provincia as provinciaNombre, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest, geo_municipio muni "
			+ "INNER JOIN geo_provincia prov ON muni.id_provincia=prov.id_provincia "
			+ "INNER JOIN geo_pais pais ON muni.cod_pais=pais.cod_pais "
		+ "WHERE muni.id_municipio=? AND ("
				+ "(fest.tipo='LOCAL' AND fest.id_municipio=muni.id_municipio) "
				+ "OR (fest.tipo='AUTONOMICA' AND fest.id_provincia=muni.id_provincia) "
				+ "OR (fest.tipo='NACIONAL' AND fest.cod_pais=muni.cod_pais) "
			+ ") %s "
		+ "ORDER BY fest.fecha, fest.tipo";
	
	private static final String QUERY_GET_LISTA_FESTIVOS_PROV = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "prov.id_provincia as provinciaCod, prov.provincia as provinciaNombre, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest, geo_provincia prov "
			+ "INNER JOIN geo_pais pais ON prov.cod_pais=pais.cod_pais "
		+ "WHERE prov.id_provincia=? AND ("
				+ "(fest.tipo='AUTONOMICA' AND fest.id_provincia=prov.id_provincia) "
			+ ") %s "
		+ "ORDER BY fest.fecha, fest.tipo";

	private static final String QUERY_GET_LISTA_FESTIVOS_PROV_COMPL = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "prov.id_provincia as provinciaCod, prov.provincia as provinciaNombre, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest, geo_provincia prov "
			+ "INNER JOIN geo_pais pais ON prov.cod_pais=pais.cod_pais "
		+ "WHERE prov.id_provincia=? AND ("
				+ "(fest.tipo='AUTONOMICA' AND fest.id_provincia=prov.id_provincia) "
				+ "OR (fest.tipo='NACIONAL' AND fest.cod_pais=prov.cod_pais) "
			+ ") %s "
		+ "ORDER BY fest.fecha, fest.tipo";

	private static final String QUERY_GET_LISTA_FESTIVOS_PAIS = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest, geo_pais pais "
		+ "WHERE pais.cod_pais=? AND ("
				+ "(fest.tipo='NACIONAL' AND fest.cod_pais=pais.cod_pais) "
			+ ") %s "
		+ "ORDER BY fest.fecha, fest.tipo";
	
	private static final String QUERY_GET_LISTA_FESTIVOS_RESIDENCIA = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "muni.id_municipio as municipioCod, muni.nombre as municipioNombre, "
			+ "prov.id_provincia as provinciaCod, prov.provincia as provinciaNombre, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest, residencia res "
			+ "INNER JOIN geo_municipio muni ON res.id_municipio=muni.id_municipio "
			+ "INNER JOIN geo_provincia prov ON muni.id_provincia=prov.id_provincia "
			+ "INNER JOIN geo_pais pais ON muni.cod_pais=pais.cod_pais "
		+ "WHERE res.codigo=? AND ("
			+ "(fest.tipo='LOCAL' AND fest.id_municipio=muni.id_municipio) "
			+ "OR (fest.tipo='AUTONOMICA' AND fest.id_provincia=muni.id_provincia) "
			+ "OR (fest.tipo='NACIONAL' AND fest.cod_pais=muni.cod_pais) "
			+ ") %s "
		+ "ORDER BY fest.fecha, fest.tipo";

	private static final String QUERY_GET_FESTIVO_COD = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo "
		+ "FROM dia_festivo fest WHERE fest.cod_festivo=?";

	private static final String QUERY_GET_FESTIVO_COD_GEO = 
		"SELECT fest.cod_festivo as codigo, fest.festivo as fiesta, fest.notas as notas, "
			+ "fest.fecha as fecha, fest.tipo as tipo, "
			+ "muni.id_municipio as municipioCod, muni.nombre as municipioNombre, "
			+ "prov.id_provincia as provinciaCod, prov.provincia as provinciaNombre, "
			+ "pais.cod_pais as paisCod, pais.pais as paisNombre "
		+ "FROM dia_festivo fest "
			+ "LEFT JOIN geo_municipio muni ON fest.id_municipio=muni.id_municipio "
			+ "LEFT JOIN geo_provincia prov ON fest.id_provincia=prov.id_provincia "
			+ "LEFT JOIN geo_pais pais ON fest.cod_pais=pais.cod_pais "
		+ "WHERE fest.cod_festivo=?";

	private static final String UPDATE_INSERT_NUEVO_FESTIVO =
			"INSERT INTO dia_festivo "
			+ "(festivo, notas, fecha, tipo, id_municipio, id_provincia, cod_pais) "
			+ "VALUES (?,?,?,?,?,?,?)";

	private static final String UPDATE_UPDATE_FESTIVO = "UPDATE dia_festivo SET %s WHERE cod_festivo=?";
	private static final String UPDATE_DELETE_FESTIVO = "DELETE FROM dia_festivo WHERE cod_festivo=?";
	
	private static String genQStrRangoFechas(Date fecha_ini, Date fecha_fin) {
		String rang = "";
		if(fecha_ini != null) rang += "AND fest.fecha>=? ";
		if(fecha_fin != null) rang += "AND fest.fecha<? ";
		return rang;
	}

	public static boolean esFestivo(Connection conexion, String codMunicipio, Date fecha, 
			 ErrorBean errorBean) {
		int LOC_M = 1;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);
		
		if(codMunicipio == null || "".equals(codMunicipio)) {
			return false;
		} else {
			try {
				PreparedStatement ps = nconexion.prepareStatement(QUERY_ES_FESTIVO_EN_MUNI);
				ps.setString(1, codMunicipio);
				ps.setDate(2, javaDateToSQLDate(fecha));
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getBoolean("existe");
				}
			} catch (SQLException e) {
				int[] loc = {LOC_H,LOC_M,1};
				ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
				e.printStackTrace();
			} finally {
				terminaOperacion(nconexion, cierraConexion);
			}
			
			return false;
		}
	}

	public static ArrayList<FestivoBean> getFestivosMunicipio(Connection conexion, String codMunicipio, TipoFiesta tipo,
			Date fecha_ini, Date fecha_fin, boolean completo, boolean includeGeo, 
			int limite, int offset, ErrorBean errorBean) {
		int LOC_M = 2;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);

		ArrayList<FestivoBean> listaFests = new ArrayList<FestivoBean>();
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			String rang = FestivoHandler.genQStrRangoFechas(fecha_ini, fecha_fin);
			int codpar = 0;
			if(fecha_ini != null) codpar = 1;
			if(fecha_fin != null) codpar += 2;
			String query = "";
			if (completo) {
				query = anadeLimiteOffset(QUERY_GET_LISTA_FESTIVOS_MUNI_COMPL, limite, offset);
			} else {
				query = anadeLimiteOffset(QUERY_GET_LISTA_FESTIVOS_MUNI, limite, offset);
			}
			ps = nconexion.prepareStatement(String.format(query, rang));
			ps.setString(1, codMunicipio);
			switch (codpar) {
			case 1:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				break;
			case 2:
				ps.setDate(2, javaDateToSQLDate(fecha_fin));
				break;
			case 3:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				ps.setDate(3, javaDateToSQLDate(fecha_fin));
				break;
			default:
			case 0:
				break;
			}

			rs = ps.executeQuery();
			FestivoBean fest;
			while (rs.next()) {
				fest = new FestivoBean();
				fest.setCodigo(rs.getInt("codigo"));
				fest.setFiesta(rs.getString("fiesta"));
				fest.setNotas(rs.getString("notas"));
				fest.setFecha(rs.getDate("fecha"));
				fest.setTipo(rs.getString("tipo"));
				if (includeGeo) {
					fest.setMunicipioCod(rs.getString("municipioCod"));
					fest.setMunicipioNombre(rs.getString("municipioNombre"));
					fest.setProvinciaCod(rs.getString("provinciaCod"));
					fest.setProvinciaNombre(rs.getString("provinciaNombre"));
					fest.setPaisCod(rs.getString("paisCod"));
					fest.setPaisNombre(rs.getString("paisNombre"));
				}
				listaFests.add(fest);
			}

		} catch (SQLException e) {
			int[] loc = {LOC_H,LOC_M,1};
			ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
			e.printStackTrace();
			return null;
		} finally {
			terminaOperacion(nconexion, cierraConexion);
		}
		return listaFests;
	}

	public static ArrayList<FestivoBean> getFestivosProvincia(Connection conexion, String codProvincia, TipoFiesta tipo,
			Date fecha_ini, Date fecha_fin, boolean completo, boolean includeGeo, 
			int limite, int offset,  ErrorBean errorBean) {
		int LOC_M = 3;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);

		ArrayList<FestivoBean> listaFests = new ArrayList<FestivoBean>();
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			String rang = FestivoHandler.genQStrRangoFechas(fecha_ini, fecha_fin);
			int codpar = 0;
			if(fecha_ini != null) codpar = 1;
			if(fecha_fin != null) codpar += 2;
			
			String query = "";
			if (completo) {
				query = anadeLimiteOffset(QUERY_GET_LISTA_FESTIVOS_PROV_COMPL, limite, offset);
			} else {
				query = anadeLimiteOffset(QUERY_GET_LISTA_FESTIVOS_PROV, limite, offset);
			}
			ps = nconexion.prepareStatement(String.format(query, rang));
			
			ps.setString(1, codProvincia);
			switch (codpar) {
			case 1:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				break;
			case 2:
				ps.setDate(2, javaDateToSQLDate(fecha_fin));
				break;
			case 3:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				ps.setDate(3, javaDateToSQLDate(fecha_fin));
				break;
			case 0:
			default:
				break;
			}

			rs = ps.executeQuery();
			FestivoBean fest;
			while (rs.next()) {
				fest = new FestivoBean();
				fest.setCodigo(rs.getInt("codigo"));
				fest.setFiesta(rs.getString("fiesta"));
				fest.setNotas(rs.getString("notas"));
				fest.setFecha(rs.getDate("fecha"));
				fest.setTipo(rs.getString("tipo"));
				if (includeGeo) {
					fest.setProvinciaCod(rs.getString("provinciaCod"));
					fest.setProvinciaNombre(rs.getString("provinciaNombre"));
					fest.setPaisCod(rs.getString("paisCod"));
					fest.setPaisNombre(rs.getString("paisNombre"));
				}
				listaFests.add(fest);
			}

		} catch (SQLException e) {
			int[] loc = {LOC_H,LOC_M,1};
			ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
			e.printStackTrace();
			return null;
		} finally {
			terminaOperacion(nconexion, cierraConexion);
		}
		return listaFests;
	}

	public static ArrayList<FestivoBean> getFestivosPais(Connection conexion, 
			String codPais, TipoFiesta tipo, Date fecha_ini, Date fecha_fin,
			boolean completo, boolean includeGeo, 
			int limite, int offset, ErrorBean errorBean) {
		int LOC_M = 4;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);

		ArrayList<FestivoBean> listaFests = new ArrayList<FestivoBean>();
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			String rang = FestivoHandler.genQStrRangoFechas(fecha_ini, fecha_fin);
			int codpar = 0;
			if(fecha_ini != null) codpar = 1;
			if(fecha_fin != null) codpar += 2;

			String query = anadeLimiteOffset(QUERY_GET_LISTA_FESTIVOS_PAIS, limite, offset);
			ps = nconexion.prepareStatement(String.format(query, rang));

			ps.setString(1, codPais);
			switch (codpar) {
			case 1:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				break;
			case 2:
				ps.setDate(2, javaDateToSQLDate(fecha_fin));
				break;
			case 3:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				ps.setDate(3, javaDateToSQLDate(fecha_fin));
				break;
			default:
			case 0:
				break;
			}

			rs = ps.executeQuery();
			FestivoBean fest;
			while (rs.next()) {
				fest = new FestivoBean();
				fest.setCodigo(rs.getInt("codigo"));
				fest.setFiesta(rs.getString("fiesta"));
				fest.setNotas(rs.getString("notas"));
				fest.setFecha(rs.getDate("fecha"));
				fest.setTipo(rs.getString("tipo"));
				if (includeGeo) {
					fest.setPaisCod(rs.getString("paisCod"));
					fest.setPaisNombre(rs.getString("paisNombre"));
				}
				listaFests.add(fest);
			}

		} catch (SQLException e) {
				int[] loc = {LOC_H,LOC_M,1};
				ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
				e.printStackTrace();
				return null;
		} finally {
			terminaOperacion(nconexion, cierraConexion);
		}
		return listaFests;
	}

	public static ArrayList<FestivoBean> getFestivosResidencia(Connection conexion,
			String codigo, Date fecha_ini, Date fecha_fin,
			int limite, int offset, ErrorBean errorBean) {
		int LOC_M = 5;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);

		ArrayList<FestivoBean> listaFests = new ArrayList<FestivoBean>();
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			String rang = FestivoHandler.genQStrRangoFechas(fecha_ini, fecha_fin);
			int codpar = 0;
			if(fecha_ini != null) codpar = 1;
			if(fecha_fin != null) codpar += 2;

			String query = anadeLimiteOffset(QUERY_GET_LISTA_FESTIVOS_RESIDENCIA, limite, offset);
			ps = nconexion.prepareStatement(String.format(query, rang));
			ps.setString(1, codigo);
			switch (codpar) {
			case 1:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				break;
			case 2:
				ps.setDate(2, javaDateToSQLDate(fecha_fin));
				break;
			case 3:
				ps.setDate(2, javaDateToSQLDate(fecha_ini));
				ps.setDate(3, javaDateToSQLDate(fecha_fin));
				break;
			default:
			case 0:
				break;
			}

			rs = ps.executeQuery();
			FestivoBean fest;
			while (rs.next()) {
				fest = new FestivoBean();
				fest.setCodigo(rs.getInt("codigo"));
				fest.setFiesta(rs.getString("fiesta"));
				fest.setNotas(rs.getString("notas"));
				fest.setFecha(rs.getDate("fecha"));
				fest.setTipo(rs.getString("tipo"));
				fest.setMunicipioCod(rs.getString("municipioCod"));
				fest.setProvinciaCod(rs.getString("provinciaCod"));
				fest.setPaisCod(rs.getString("paisCod"));
				
				listaFests.add(fest);
			}

		} catch (SQLException e) {
			int[] loc = {LOC_H,LOC_M,1};
			ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
			e.printStackTrace();
			return null;
		} finally {
			terminaOperacion(nconexion, cierraConexion);
		}
		return listaFests;
	}

	public static FestivoBean getFestivo(Connection conexion, long codFest, boolean includeGeo, 
			ErrorBean errorBean) {
		int LOC_M = 6;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);

		FestivoBean fest = null;
		if (codFest >= 0) {
			try {
				PreparedStatement ps;
				if(includeGeo) {
					ps = nconexion.prepareStatement(QUERY_GET_FESTIVO_COD_GEO);
				} else {
					ps = nconexion.prepareStatement(QUERY_GET_FESTIVO_COD);
				}
				ps.setLong(1, codFest);
				ResultSet rs;
				rs = ps.executeQuery();

				if (rs.next()) {
					fest = new FestivoBean();
					fest.setCodigo(rs.getInt("codigo"));
					fest.setFiesta(rs.getString("fiesta"));
					fest.setNotas(rs.getString("notas"));
					fest.setFecha(rs.getDate("fecha"));
					fest.setTipo(rs.getString("tipo"));
					if (includeGeo) {
						fest.setMunicipioCod(rs.getString("municipioCod"));
						fest.setMunicipioNombre(rs.getString("municipioNombre"));
						fest.setProvinciaCod(rs.getString("provinciaCod"));
						fest.setProvinciaNombre(rs.getString("provinciaNombre"));
						fest.setPaisCod(rs.getString("paisCod"));
						fest.setPaisNombre(rs.getString("paisNombre"));
					}
				} else {
					int[] loc = {LOC_H,LOC_M,3};
					ErrorBeanFabrica.generaErrorBean(errorBean, Status.NOT_FOUND, "h48", loc, "no encotrado festivo con codigo " + codFest);
				}
			} catch (SQLException e) {
				int[] loc = {LOC_H,LOC_M,2};
				ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
				e.printStackTrace();
				return null;
			} finally {
				terminaOperacion(nconexion, cierraConexion);
			}
		} else {
			int[] loc = {LOC_H,LOC_M,1};
			ErrorBeanFabrica.generaErrorBean(errorBean, Status.BAD_REQUEST, "h22", loc, "debe incluir codigo");
		}
		return fest;
	}

	public static FestivoBean insertFestivo(Connection conexion, FestivoBean festRaw, 
			 ErrorBean errorBean) {
		int LOC_M = 7;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);
		
		
		FestivoBean fest = null;
		if (festRaw != null) {
			try {
				PreparedStatement ps = nconexion.prepareStatement(UPDATE_INSERT_NUEVO_FESTIVO, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, festRaw.getFiesta());
				ps.setString(2, festRaw.getNotas());
				ps.setDate(3, javaDateToSQLDate(festRaw.getFecha()));
				ps.setString(4, festRaw.getTipo());
				ps.setString(5, festRaw.getMunicipioCod());
				ps.setString(6, festRaw.getProvinciaCod());
				ps.setString(7, festRaw.getPaisCod());
				
				int c = ps.executeUpdate();
				if (c > 0 && ps.getGeneratedKeys().next()) {
					int codigo = ps.getGeneratedKeys().getInt(1);
					fest = FestivoHandler.getFestivo(nconexion, codigo, false,  errorBean);
					if(fest == null) {
						errorBean.setHttpCode(Status.INTERNAL_SERVER_ERROR);
						errorBean.updateErrorCode("69800603");
						errorBean.updateMsg("no insertada (?)");
					}
				}
			} catch (SQLException e) {
				int[] loc = {LOC_H,LOC_M,1};
				ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
				e.printStackTrace();
			} finally {
				terminaOperacion(nconexion, cierraConexion);
			}
		} else {
			errorBean.setHttpCode(Status.BAD_REQUEST);
			errorBean.updateErrorCode("69800601");
			errorBean.updateMsg("debe incluir datos residencia");
		}
			
		return fest;
	}

	public static FestivoBean updateFestivo(Connection conexion, long codFest,
			FestivoBean festRaw,  ErrorBean errorBean) {
		int LOC_M = 8;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);
		FestivoBean fest = null;
		
		if (festRaw != null) {
			int params = 0;
			String[]strs = new String[5];
			Date auxFecha = null;
			String upd = "";
			
			if(festRaw.getFiesta() != null) {
				upd += "festivo=?";
				strs[params++] = festRaw.getFiesta();
			}
			if(festRaw.getNotas() != null) {
				if(params > 0) upd += ", ";
				upd += "notas=?";
				strs[params++] = festRaw.getNotas();
			}
			if(festRaw.getTipo() != null) {
				if(params > 0) upd += ", ";
				upd += "tipo=?";
				strs[params++] = festRaw.getTipo();
			}
			if(festRaw.getMunicipioCod() != null) {
				if(params > 0) upd += ", ";
				upd += "id_municipio=?";
				strs[params++] = festRaw.getMunicipioCod();
			}
			if(festRaw.getProvinciaCod() != null) {
				if(params > 0) upd += ", ";
				upd += "id_provincia=?";
				strs[params++] = festRaw.getProvinciaCod();
			}
			if(festRaw.getPaisCod() != null) {
				if(params > 0) upd += ", ";
				upd += "cod_pais=?";
				strs[params++] = festRaw.getPaisCod();
			}
			if(festRaw.getFecha() != null) {
				if(params > 0) upd += ", ";
				upd += "fecha=?";
				auxFecha = festRaw.getFecha();
			}
			
			
			if (params == 0 && auxFecha == null) {
				int[] loc = {LOC_H,LOC_M,3};
				ErrorBeanFabrica.generaErrorBean(errorBean, Status.NOT_FOUND, "h36", loc, "Sin parametros para cambiar");
			} else {
				try {
					PreparedStatement ps = nconexion.prepareStatement(String.format(UPDATE_UPDATE_FESTIVO, upd));
					for (int i = 0; i < params; i++) {
						if(strs[i].equals(""))
							ps.setString(i+1, null);
						else
							ps.setString(i+1, strs[i]);
					}
					if (auxFecha != null) {
						ps.setDate(params+1, javaDateToSQLDate(auxFecha));
					}
					ps.setLong(params+2, codFest);
					
					int c = ps.executeUpdate();
					if (c > 0) {
						fest = FestivoHandler.getFestivo(nconexion, codFest, false, errorBean);
						if(fest == null) {
							int[] loc = {LOC_H,LOC_M,4};
							ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h96", loc, "???");
						}
					} else {
						int[] loc = {LOC_H,LOC_M,3};
						ErrorBeanFabrica.generaErrorBean(errorBean, Status.NOT_FOUND, "h48", loc, "no encotrado festivo con codigo " + codFest);
					}
				} catch (SQLException e) {
					int[] loc = {LOC_H,LOC_M,2};
					ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
					e.printStackTrace();
					return null;
				} finally {
					terminaOperacion(nconexion, cierraConexion);
				}
			}
		} else {
			int[] loc = {LOC_H,LOC_M,1};
			ErrorBeanFabrica.generaErrorBean(errorBean, Status.BAD_REQUEST, "h22", loc, "debe incluir datos festivo");
		}
			
		return fest;
	}

	public static boolean deleteFestivo(Connection conexion, long codFest, ErrorBean errorBean) {
		int LOC_M = 9;
		Connection nconexion = aseguraConexion(conexion);
		boolean cierraConexion = (conexion == null) || (conexion != nconexion);
		
		try {
			PreparedStatement ps = nconexion.prepareStatement(UPDATE_DELETE_FESTIVO);
			ps.setLong(1, codFest);

			int c = ps.executeUpdate();
			if (c > 0) {
				return true;
			} else {
				int[] loc = {LOC_H,LOC_M,2};
				ErrorBeanFabrica.generaErrorBean(errorBean, Status.NOT_FOUND, "h48", loc, "no encotrado festivo con codigo " + codFest);
			}
		} catch (SQLException e) {
			int[] loc = {LOC_H,LOC_M,1};
			ErrorBeanFabrica.generaErrorBean(errorBean, Status.INTERNAL_SERVER_ERROR, "h69", loc, e.getMessage(), null);
			e.printStackTrace();
		} finally {
			terminaOperacion(nconexion, cierraConexion);
		}
		
		return false;
	}
	

}
