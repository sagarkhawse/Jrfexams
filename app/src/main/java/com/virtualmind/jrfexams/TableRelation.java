package com.virtualmind.jrfexams;

import java.util.ArrayList;
import java.util.Scanner;

public class TableRelation {

    public static void main(String[] args) {

        ArrayList<Table> tablesList = new ArrayList<Table>();

//
//		tableColumnDetails
//

        Table employeTable = new Table("Employee");
        employeTable.addColumn(new Column("EmployeeName", false, null));
        employeTable.addColumn(new Column("EmployeeAge", false, null));
        employeTable.addColumn(new Column("EmployeeLocation", true, new ForeignKeyRelation("Location", "LocationName")));
        employeTable.addColumn(new Column("AssiagnedVehicle", true, new ForeignKeyRelation("Vechicles", "VehicleName")));
        employeTable.addColumn(new Column("EmployeeDepartment", true, new ForeignKeyRelation("Department", "DepartmentName")));

        Table locationTable = new Table("Location");
        locationTable.addColumn(new Column("LocationName", false, null));
        locationTable.addColumn(new Column("LocationPincode", false, null));
        locationTable.addColumn(new Column("LocationAdmin", true, new ForeignKeyRelation("Employee", "EmployeeName")));


        Table departmentTable = new Table("Department");
        departmentTable.addColumn(new Column("DepartmentName", false, null));
        departmentTable.addColumn(new Column("DepartmentRevenue", false, null));
        departmentTable.addColumn(new Column("DepartmentHead", true, new ForeignKeyRelation("Employee", "EmployeeName")));

        Table productTable = new Table("Product");
        productTable.addColumn(new Column("ProductName", false, null));
        productTable.addColumn(new Column("ProductUnitPrice", false, null));
        productTable.addColumn(new Column("ProductStock", false, null));

        Table ordersTable = new Table("Orders");
        ordersTable.addColumn(new Column("OrderProduct", true, new ForeignKeyRelation("Product", "ProductName")));
        ordersTable.addColumn(new Column("OrderQuantity", false, null));
        ordersTable.addColumn(new Column("OrderBy", true, new ForeignKeyRelation("Employee", "EmployeeName")));

        Table vechiclesTable = new Table("Vechicles");
        vechiclesTable.addColumn(new Column("VehicleName", false, null));
        vechiclesTable.addColumn(new Column("VehicleTopSpeed", false, null));

//
//		below is the relations
//

        tablesList.add(employeTable);
        tablesList.add(locationTable);
        tablesList.add(departmentTable);
        tablesList.add(productTable);
        tablesList.add(ordersTable);
        tablesList.add(vechiclesTable);

//
//		Input for the program
//
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Table1:");
        String table1 = scanner.next();

        System.out.println("Enter Table2:");
        String table2 = scanner.next();

        System.out.println("input : " + table1 + " to " + table2);


        Table tableA, tableB;
        if (table1.equals("Vechicles")) {
            tableA = vechiclesTable;
        }
        if (table2.equals("Employee")) {
            tableB = employeTable;
        }


        String TableNAme = "";
        String prevTableColumn = "";
                String currentColun = "";
        for (int i = 0; i < employeTable.tableColumns.size(); i++) {
            if (employeTable.tableColumns.get(i).isForeignKey) {
                TableNAme = employeTable.tableName;

                if (employeTable.tableColumns.get(i).ForeignKeyRelation.foreignTable.equals(table1)) {
                    currentColun = employeTable.tableColumns.get(i).columnName;
                    prevTableColumn = employeTable.tableColumns.get(i).ForeignKeyRelation.foreignTableColumn;
                }
            }
        }
        System.out.println("output --> " + table1 + "-->" + TableNAme+ "("+prevTableColumn + "," + currentColun+")");

//
//		develop logic to detect relation between tables
//		Refer the image for better understanding of the relationship
//
//		Output Format:
//		Table --> Table(PrevTableCol,CurrentTableCol) --> Table(PrevTableCol,CurrentTableCol) --> .....
//		PrevTableCol,CurrentTableCol are directly related or indirectly if both tables has same foreign key
// --------------------------------------------------------------------------------------------------------------
//		example:
// --------------------------------------------------------------------------------------------------------------
//		Input: Vechicles to Employee
//      Ouput:
//		Shortest path:
//		Output --> Vechicles --> Employee(VehicleName,AssiagnedVehicle)
//
//		All possible path
//		... <Print all possible paths, refer Usecase1.JPG> .............
//
// --------------------------------------------------------------------------------------------------------------
//		Input: Department to Orders
//      Ouput:
//		Shortest path:
//		Output --> Department --> Orders(DepartmentHead,OrderBy)
//
//		All possible path
//		... <Print all possible paths, refer Usecase2.JPG> .............
//
// --------------------------------------------------------------------------------------------------------------
//		Input: Product to Vehicles
//      Ouput:
//		Shortest path:
//		Output --> Product --> Orders(ProductName,OrderProduct) --> Employee(OrderBy,EmployeeName) --> Vehicles(AssiagnedVehicle,VehicleName)
//
//		All possible path
//		... <Print all possible paths, refer Usecase3.JPG> .............
//
// --------------------------------------------------------------------------------------------------------------
//		Note:
//		1. We need to execute this logic for N(say 100) tables and finding the relationship path should be in shortest time
//		2. We need both outputs
//			A. Shortest path
//			B. All possible paths
//		3. Two table are inter-related if they have same foreign key (for example: LocationAdmin, DeptHead and OrderBy are related)
//		4. Please refer the image for better understanding
//

    }


    static class ForeignKeyRelation {
        String foreignTable;
        String foreignTableColumn;

        public ForeignKeyRelation() {
            // TODO Auto-generated constructor stub
        }

        public ForeignKeyRelation(String foreignTable, String foreignTableColumn) {
            this.foreignTable = foreignTable;
            this.foreignTableColumn = foreignTableColumn;
        }
    }

    static class Column {
        String columnName;
        boolean isForeignKey;
        ForeignKeyRelation ForeignKeyRelation;

        public Column() {
            // TODO Auto-generated constructor stub
        }

        public Column(String columnName, boolean isForeignKey, ForeignKeyRelation ForeignKeyRelation) {
            this.columnName = columnName;
            this.isForeignKey = isForeignKey;
            this.ForeignKeyRelation = ForeignKeyRelation;
        }
    }

    static class Table {
        String tableName;
        ArrayList<Column> tableColumns = new ArrayList<Column>();

        public Table() {

        }

        public Table(String tableName) {
            this.tableName = tableName;
        }

        public Table addColumn(Column column) {
            tableColumns.add(column);
            return this;
        }
    }


}
