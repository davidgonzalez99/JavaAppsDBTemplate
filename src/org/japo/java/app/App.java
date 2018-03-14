/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.japo.java.app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.japo.java.entities.DataAccessManager;
import org.japo.java.libraries.UtilesDB;


/**
 *
 * @author David González Candelas - davidgonzalezcandelas@gmail.com
 */
public class App {
    
    public void launchApp() {

        System.out.println("Iniciando acceso a Base de Datos...");
        System.out.println("---");
        // Este Statement permite que los datos sean dinámicos, es decir, que sean susceptibles a cambios gracias a los SQL correspondientes
        try(Connection con = UtilesDB.obtenerConexion(); Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            System.out.println("Acceso a Base de Datos INICIADO");
            System.out.println("---");
            DataAccessManager dam = new DataAccessManager(con, stmt);
            
            // Lógica de negocio
            
            
            System.out.println("---");
            System.out.println("Acceso a Base de Datos FINALIZADO");
            
        }catch(SQLException e) {
            System.out.println("ERROR: Acceso a Base de datos CANCELADO");
            System.out.printf("Código de error .: %d%n", e.getErrorCode());
            System.out.printf("Estado SQL ......: %s%n", e.getSQLState());
            System.out.printf("Descripción .....: %s%n", e.getLocalizedMessage());
        }
        
    }
}
