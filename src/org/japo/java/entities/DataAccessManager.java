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
import org.japo.java.libraries.UtilesEntrada;

/**
 *
 * @author CicloM
 */
public class DataAccessManager {

    // Sentencias SQL
    public static final String DEF_SQL_MOD1 = "SELECT * FROM profesor";
    public static final String DEF_SQL_MOD2 = "DELETE FROM modulo WHERE acronimo='ED'";
    public static final String DEF_SQL_MOD3 = "INSERT INTO modulo(id,acronimo,nombre,codigo,horasCurso,curso) VALUES ('2','ED','Entorno de Desarrollo','MP4065','200','1')";
    public static final String DEF_SQL_MOD4 = "UPDATE modulo SET curso='2' WHERE horasCurso<'200'";
    public static final String DEF_SQL_MOD5 = "SELECT * FROM modulo WHERE id='%s'";
    public static final String DEF_SQL_ALU = "SELECT * FROM alumno";
    public static final String DEF_SQL_PRO = "SELECT * FROM profesor";

    public static final String CAB_LIST_MOD1 = "#    Id  Acrónimo Nombre                               Código   Grado     Familia";
    public static final String CAB_LIST_MOD2 = "=    ==  ======== ==================================   ======   ========  =========== ";

    public static final String CAB_LIST_ALU = "#     Exp         Nombre    Apellidos   Nif        Nac        Teléfono  Email               Domicilio         Localidad     Provincia     CP     User     Pass      Foto";
    public static final String CAB_LIST_ALU1 = "=    ==========  ========  =========== =========  ========== ========= ==================  ================  ============  ============  =====  =======  =======   =======";
    public static final String CAB_LIST_PROF1 = "#    Id            Nombre   Apellidos          Departamento      Especialidad      Tipo";
    public static final String CAB_LIST_PROF2 = "=    ==  =================  =============      ============      ============      ====";

    public static final String CAB_REG_MOD1 = "Proceso de Borrado - Resgistro %02d";
    public static final String CAB_REG_MOD2 = "===================================";

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

    public final void listarModulos() throws SQLException {
        System.out.println("---");
        System.out.println("Listado de módulos ...");
        System.out.println("---");
        // rs obtiene el resultado de la consulta, las sentencias que generan datos se tienen que atacar
        // con executeQuery (select). Y las que no generan datos sino que generan el número de filas afectadas por la secuencia SQL
        // se atacan con el método executeUpdate() (como insert, delete, update...)
        // Cuando el try termine se cierra el rs que hemos abierto con la sentencia sql_mod
        try (ResultSet rs = stmt.executeQuery(DEF_SQL_MOD1)) {
            if (rs.next()) {
                System.out.println(CAB_LIST_PROF1);
                System.out.println(CAB_LIST_PROF2);
                do {
                    // getRow devuelve el número de la fila donde está el puntero
                    int fila = rs.getRow();
                    // getInt cogerá el valor del campo id en este caso, ya que contiene datos de tipo entero,
                    // también se podría usar getInt(1) ya que es el número de columna de id
                    int id = rs.getInt("id");
                    // getString cogerá el valor del campo acronimo, y como es varchaar es el equivalente
                    String acronimo = rs.getString("acronimo");
                    String nombre = rs.getString("nombre");
                    String codigo = rs.getString("codigo");
                    String horasCurso = rs.getString("horasCurso");
                    String curso = rs.getString("curso");
                    System.out.printf("%-5d %-7d %3s %-33s %-2s %6d %7d%n", fila, id, acronimo, nombre, codigo, horasCurso, curso);
                    // Print para alumnos                    
                    //System.out.printf("%-4d %-15s %-5s %-11s %-10s %s %s %-19s %-23s %-7s %-13s %-7s %-8s %-9s %s %n", fila, exp, nombre, apellidos, nif, nac, telefono, email, domicilio, localidad, provincia, cp, user, pass, foto);
                    // Print para profesores
                    // System.out.printf("%-5d %-7d %-13s %-18s %-17s %-17s %3s %n", fila, id, nombre, apellido, departamento, especialidad, tipo);
                    
                } while (rs.next());
            } else {
                System.out.println("No hay datos que mostrar");
            }
        }
    }

    public final void listarModulos(int lineasPagina) throws SQLException {

        // Si el número de lineas es menor o igual a 0 muestra toda la informacion en una linea
        if (lineasPagina <= 0) {
            listarModulos();

            // Si el número de lineas es mayor que 0 hace el proceso
        } else {
            System.out.println("Listado de módulos ...");
            System.out.println("---");
            // Alamacena el resultado de la SQL en el rs
            try (ResultSet rs = stmt.executeQuery(DEF_SQL_MOD1)) {
                // Si hay mas en el rs sigue el proceso
                if (rs.next()) {
                    // Semaforo
                    boolean nuevaLineaOK;
                    // Inicializamos la linea y el número de la página a 1
                    int lineaAct = 1;
                    int paginaAct = 1;
                    // Bucle Externo para mostrar el número de página y el encabezado de tabla con cada página
                    // si la información es mayor que la que cabe en la pagina nos pregunta si queremos mostrar 
                    // el resto de información en otra pagina, lo hará dependiendo de la respuesta (sSnN), si la 
                    // respuesta es (sS) mostrará la siguiente pagina, sino el programa finaliza
                    do {
                        System.out.printf("Página ...: %02d%n", paginaAct);
                        System.out.println("==============");
                        System.out.println(CAB_LIST_MOD1);
                        System.out.println(CAB_LIST_MOD2);
                        // Bucle Interno que almacena en variables el contenido de la tabla módulo
                        // y lo muestra por filas, usa un actualizador para llegar al limite de
                        // lineas por pagina (que establecemos nosotros en 3) y usa el semaforo
                        // dependiendo de si hay o no más datos en el rs para salir del blucle
                        do {
                            int fila = rs.getRow();
                            int id = rs.getInt("id");
                            String acronimo = rs.getString("acronimo");
                            String nombre = rs.getString("nombre");
                            String codigo = rs.getString("codigo");
                            int horasCurso = rs.getInt("horasCurso");
                            int curso = rs.getInt("curso");
                            System.out.printf("%-5d %-7d %3s %-33s %-2s %6d %7d%n", fila, id, acronimo, nombre, codigo, horasCurso, curso);
                            lineaAct++;
                            nuevaLineaOK = rs.next();
                        } while (lineaAct <= lineasPagina && nuevaLineaOK);
                        if (nuevaLineaOK) {
                            System.out.println("---");
                            char respuesta = UtilesEntrada.leerOpcion("sSnN", "Siguiente página (S/N) ...: ", "ERROR: Entrada incorrecta");
                            if (respuesta == 's' || respuesta == 'S') {
                                paginaAct++;
                                lineaAct = 1;
                                System.out.println("---");
                            } else {
                                nuevaLineaOK = false;
                            }
                        }
                    } while (nuevaLineaOK);
                } else {
                    System.out.println("No hay modulos que mostrar");
                }
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

    public final void borrarModulosInteractivo() throws SQLException {
        System.out.println("Borrado de módulos ...");
        System.out.println("---");
        // Almacena los registros resultantes de la SQL en el rs
        try (ResultSet rs = stmt.executeQuery(DEF_SQL_MOD1)) {
            // Variable para saber el número de modulos borrados
            int regBorrados = 0;
            // Bucle que siga mostrando los registros mientras haya elementos en el rs
            while (rs.next()) {
                // Cogemos cada uno de los campos(columnas 1, 2, 3...) de la tabla modulos 
                // para hacer un registro con esos datos correspondiente a un modulo completo
                System.out.printf(CAB_REG_MOD1 + "%n", rs.getRow());
                System.out.println(CAB_REG_MOD2);
                System.out.printf("Id ................: %d%n", rs.getInt(1));
                System.out.printf("Acronimo ..........: %s%n", rs.getString(2));
                System.out.printf("Nombre ............: %s%n", rs.getString(3));
                System.out.printf("Codigo ............: %s%n", rs.getString(4));
                System.out.printf("Horas del Curso ...: %d%n", rs.getInt(5));
                System.out.printf("Curos .............: %d%n", rs.getInt(6));

                // Creamos una variable char que almacena la respuesta de si queremos borrar o no el registro, gracias al metodo de leerOpcion
                char respuesta = UtilesEntrada.leerOpcion("SsNn", "Borrar Modulo (S/N) ...: ", "ERROR: Entrada Incorrecta");

                // Si la respuesta es si, borrará el registro(fila) entero y sumará 1 a regBorrados
                if (respuesta == 'S' || respuesta == 's') {
                    rs.deleteRow();
                    regBorrados++;
                    System.out.println("---");
                    System.out.println("Modulo actual borrado");
                }
                System.out.println("---");
            }
            System.out.printf("Se han borrado %d modulos %n", regBorrados);
        }
    }

    public final void insertarModulos() throws SQLException {
        System.out.println("---");
        System.out.println("Inserción de datos ...");
        System.out.println("---");
        stmt.executeUpdate(DEF_SQL_MOD3);
    }

    public final void insertarModulosInterativo() throws SQLException{
        System.out.println("Inserción de profesores...");
        System.out.println("---");
        
        // Almacenamos en el rs todos los datos del módulo
        try(ResultSet rs = stmt.executeQuery(DEF_SQL_MOD1)){
            
            // Nos posicionamos en la InsertRow
            rs.moveToInsertRow();
            // Insertamos los datos, los cuales irán a los campos en la fila InsertRow
            rs.updateInt(1, UtilesEntrada.leerEntero("Id .........: ", "ERROR: Entrada Incorrecta"));
            rs.updateString(2, UtilesEntrada.leerTexto("Nombre ...: "));
            rs.updateString(3, UtilesEntrada.leerTexto("Apellidos .....: "));
            rs.updateString(4, UtilesEntrada.leerTexto("Departamento .....: "));
            rs.updateString(5, UtilesEntrada.leerTexto("Especialidad ......: "));
            rs.updateString(6, UtilesEntrada.leerTexto("Tipo ....: "));
            System.out.println("---");
            
            // Si la respues es Si...
            char respuesta = UtilesEntrada.leerOpcion("SsNn", "Insertar modulo (S/N) ...: ", "ERROR: Entrada Incorrecta");
            if(respuesta == 's' || respuesta == 'S'){
                rs.insertRow();
                System.out.println("---");
                System.out.println("Inserción de datos COMPLETADA");
            }else{
                System.out.println("---");
                System.out.println("Inserción de datos CANCELADA");
            }
            // Nos devuelve a la fila en la que estabamos antes de movernos a la fila especial InsertRow
            rs.moveToCurrentRow();
        }
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

    public final void modificarModulos() throws SQLException {
        System.out.println("---");
        System.out.println("Modificación de datos ...");
        System.out.println("---");
        stmt.executeUpdate(DEF_SQL_MOD4);
    }

    // Este metodo busca registros para cambiarlos, excepto el campo que han servido para realizar la búsqueda (id) por ejemplo
    public final void modiciarModulosInteractivo() throws SQLException {
        
        System.out.println("Actualización de módulos");
        System.out.println("---");
        
        // Utilizamos la ID de la tabla como "motor de búsqueda" para los registros
        int id = UtilesEntrada.leerEntero("Id búsqueda ...: ", "ERROR: Entrada incorrecta");
        
        // Almacenamos en el ResultSet el resultado de la SQL, haciendo que coincida con la id para dependiendo
        // de cual elijamos nos muestre un registro u otro, además convertimos la id a String para poder realizar
        // búsquedas en otras tablas independientemente del tipo que se la clave primaria
        try(ResultSet rs = stmt.executeQuery(String.format(DEF_SQL_MOD5, id + ""))){
            
            // La condición se cumple si hay más registros en el rs, así que si hay pasamos a la primera columna desde el BeforeFirst
            if(rs.next()){
                
                // Mostramos el registro correspondiente a la id especificada arriba
                System.out.println("Registro actual - Estado INICIAL");
                System.out.println("================================");
                
                // Cogemos los datos de los campos correspondientes a la id especificada arriba y los mostramos
                System.out.printf("Acrónimo ...: %s%n", rs.getString("acronimo"));
                System.out.printf("Nombre .....: %s%n", rs.getString("nombre"));
                System.out.printf("Código .....: %s%n", rs.getString("codigo"));
                System.out.printf("Horas ......: %d%n", rs.getInt("horasCurso"));
                System.out.printf("Curso ......: %d%n", rs.getInt("curso"));
                
                // Actualizamos cada uno de los campos del registro y se almacenan en BeforeFirst
                System.out.println("---");
                System.out.println("Registro actual - Estado FINAL");
                System.out.println("---");
                
                rs.updateString(2, UtilesEntrada.leerTexto("Acrónimo ...: "));
                rs.updateString(3, UtilesEntrada.leerTexto("Nombre .....: "));
                rs.updateString(4, UtilesEntrada.leerTexto("Códgio .....: "));
                rs.updateInt(5, UtilesEntrada.leerEntero("Horas ......: ", "ERROR: Entrada Incorrecta"));
                rs.updateInt(6, UtilesEntrada.leerEntero("Curso ......: ", "ERROR: Entrada Incorrecta"));
                
                System.out.println("---");
                
                // Creamos una variable para almacenar la decisión del usuario de actualizar o no el registro
                char respuesta = UtilesEntrada.leerOpcion("SsNn", "Actualizar MÓDULO (S/N) ...: ", "ERROR: Lectura Incorrecta");
                
                // Si la respuesta contiene S o s la actualización se realiza gracias a rs.updateRow() que manda el registro
                // desde el BeforeFirst hasta la tabla de la base de datos
                if(respuesta == 'S' || respuesta == 's'){
                    rs.updateRow();
                    System.out.println("Actualización COMPLETADA");
                }else{
                    System.out.println("Actualización CANCELADA");
                }
            }else{
                System.out.println("ERROR: No hay datos asociados");
            }
        }
    }
    
}
