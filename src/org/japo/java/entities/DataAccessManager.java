/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.japo.java.entities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author CicloM
 */
public class DataAccessManager {

    // Sentencias SQL
    public static final String DEF_SQL_MOD1 = "SELECT * FROM modulo";
    public static final String DEF_SQL_MOD2 = "DELETE FROM modulo WHERE acronimo='ED'";
    public static final String DEF_SQL_MOD3 = "INSERT INTO modulo(id,acronimo,nombre,codigo,horasCurso,curso) VALUES ('2','ED','Entorno de Desarrollo','MP4065','200','1')";
    public static final String DEF_SQL_MOD4 = "UPDATE modulo SET curso='2' WHERE horasCurso<'200'";
    public static final String DEF_SQL_ALU = "SELECT * FROM alumno";
    public static final String DEF_SQL_PRO = "SELECT * FROM profesor";

    public static final String CAB_LIST_MOD1 = "#    Id  Acrónimo Nombre                            Código   Horas  Curso";
    public static final String CAB_LIST_MOD2 = "=    ==  ======== ===============================   ======   =====  ===== ";

    public static final String CAB_LIST_ALU1 = "";
    public static final String CAB_LIST_ALU2 = "";
    public static final String CAB_LIST_PROF = "";
    public static final String CAB_LIST_PROF2 = "";
    
    private Connection con;
    private Statement stmt;
    

    public DataAccessManager(Connection con) {
        this.con = con;
    }

    public DataAccessManager(Connection con, Statement stmt) {
        this.con = con;
        this.stmt = stmt;
    }

    public final void mostrarMetadatos() throws SQLException {
        DatabaseMetaData dmd = con.getMetaData();
        System.out.println("Información BASE DE DATOS");
        System.out.println("==========================");
        System.out.printf("Usuario ...: %s%n", dmd.getUserName());
        System.out.printf("Base de datos ...: %s%n", dmd.getDatabaseProductName());
        System.out.printf("Version SGBD ...: %s%n", dmd.getDatabaseProductVersion());
        System.out.printf("Driver JDBC ...: %s%n", dmd.getDriverName());
        System.out.printf("Versión JDBC ...: %2d.%d%n", dmd.getJDBCMajorVersion(), dmd.getJDBCMinorVersion());
    }

    public final void listarModulos() throws SQLException{
        System.out.println("---");
        System.out.println("Listado de módulos ...");
        System.out.println("---");
        // rs obtiene el resultado de la consulta, las sentencias que generan datos se tienen que atacar
        // con executeQuery (select). Y las que no generan datos sino que generan el número de filas afectadas por la secuencia SQL
        // se atacan con el método executeUpdate() (como insert, delete, update...)
        // Cuando el try termine se cierra el rs que hemos abierto con la sentencia sql_mod
        try(ResultSet rs = stmt.executeQuery(DEF_SQL_MOD1)){
            if(rs.next()){
                System.out.println(CAB_LIST_MOD1);
                System.out.println(CAB_LIST_MOD2);
                do{
                    // getRow devuelve el número de la fila donde está el puntero
                    int fila = rs.getRow();
                    // getInt cogerá el valor del campo id en este caso, ya que contiene datos de tipo entero,
                    // también se podría usar getInt(1) ya que es el número de columna de id
                    int id = rs.getInt("id");
                    // getString cogerá el valor del campo acronimo, y como es varchaar es el equivalente
                    String acronimo = rs.getString("acronimo");
                    String nombre = rs.getString("nombre");
                    String codigo = rs.getString("codigo");
                    int horasCurso = rs.getInt("horasCurso");
                    int curso = rs.getInt("curso");
                    System.out.printf("%-5d %-7d %3s %-33s %-2s %6d %7d%n", fila, id, acronimo, nombre, codigo, horasCurso, curso);
                }while(rs.next());
            }else{
                System.out.println("No hay datos que mostrar");
            }
        }
    }

    public final void borrarModulos() throws SQLException {
        System.out.println("---");
        System.out.println("Borrado de módulos ...");
        System.out.println("---");
        int filas = stmt.executeUpdate(DEF_SQL_MOD2);
        System.out.printf("Se han borrado %d modulos%n", filas);
        System.out.println("---");
    }

    public final void insertarModulos() throws SQLException {
        System.out.println("---");
        System.out.println("Inserción de datos ...");
        System.out.println("---");
        stmt.executeUpdate(DEF_SQL_MOD3);
    }

    public final void insertarModulosPreparados() throws SQLException {
        
        // Creamos un string que contenga una secuencia SQL preparada, cuyos parámetros serán IN ("?").
        String sql = "INSERT INTO modulo VALUES ( ?, ? ,?, ?, ?, ? )";
        // Creamos un objeto PreparedStatement, cuyo parámetro será el String, mediante la Connection (con)
        PreparedStatement sentencia = con.prepareStatement(sql);

        // Suministramos un valor para cada parámetro IN, gracias a los metodos de PreparedStatement
        // donde el primer parámetro será el número de valor a sustituir, y el segundo parámetro será el valor.
        sentencia.setInt(1, 2);
        sentencia.setString(2, "ED");
        sentencia.setString(3, "Entornos de Desarrollo");
        sentencia.setString(4, "MP4065");
        sentencia.setInt(5, 200);
        sentencia.setInt(6, 1);
        
        // Ejecutamos el objeto de PreparedStatement, el cual contiene la secuencia SQL con los valores ya añadidos
        sentencia.executeUpdate();
        
        System.out.println("---");
        System.out.println("Inserción de datos con SQL Preparada");
        System.out.println("---");
    }
    
    public final void modificarModulos() throws SQLException{
        System.out.println("---");
        System.out.println("Modificación de datos ...");
        System.out.println("---");
        stmt.executeUpdate(DEF_SQL_MOD4);
    }
    
}
