package com.newland.dao;

import com.newland.model.ServiceObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectDao {
    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/optimize?useUnicode=true&characterEncoding=utf-8";
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "123456";

    public List<ServiceObject> selectByCreateDate(ServiceObject serviceObject){
        List<ServiceObject> result = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            String sql;
            sql = "select " +
                    "id,parent_id parentId,object_name objectName,create_date createDate,`type`,is_coverage isCoverage " +
                    "from t_service_object " +
                    "where " +
                    "create_date = ?;";

            ps = conn.prepareStatement(sql);

            ps.setString(1,serviceObject.getCreateDateString());

            ResultSet rs = ps.executeQuery();

            // 展开结果集数据库
            while(rs.next()){
                ServiceObject temp = new ServiceObject();
                // 通过字段检索
                temp.setId(rs.getString("id"));
                temp.setType(rs.getInt("type"));
                temp.setObjectName(rs.getString("objectName"));
                temp.setCreateDate(rs.getDate("createDate"));
                temp.setParentId(rs.getString("parentId"));
                temp.setIsCoverage(rs.getInt("isCoverage"));

                result.add(temp);
            }
            // 完成后关闭
            rs.close();
            ps.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(ps!=null) ps.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }

        return result;
    }
}
