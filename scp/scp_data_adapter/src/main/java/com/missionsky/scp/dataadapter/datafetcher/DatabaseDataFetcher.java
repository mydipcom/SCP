package com.missionsky.scp.dataadapter.datafetcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;



import com.missionsky.scp.dataadapter.entity.DataSource;

public class DatabaseDataFetcher implements DataFetcher{

	@Override
	public List<Map<String, String>> renderMergedFetchedData(
			DataSource dataSource, Integer offset) {
		// TODO Auto-generated method stub
		ResultSet rs=getDatabaseResult(dataSource,offset);
		return null;
	}

	private ResultSet getDatabaseResult(DataSource dataSource, Integer offset) {
		// TODO Auto-generated method stub
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn=DriverManager.getConnection(dataSource.getLink());
			Statement stm=conn.createStatement();
			ResultSet rsResult=null;
			rsResult=stm.executeQuery("select * from product limit ");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getTargetByPath(Object object, String pathStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getTotal(DataSource dataSource) {
		// TODO Auto-generated method stub
		return null;
	}

}
