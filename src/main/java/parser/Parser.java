package xlsParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



public class Parser {

	public ArrayList<Employee> parseXls(String filePath, ArrayList<Employee> fullEmployeesCollection) throws IOException{
		ArrayList<Employee> selectedEmployeesCollection = new ArrayList<>();
		
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(new File(filePath));
			wb =  WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheetAt(0);
			
			for(int i = 1; i <= sheet.getLastRowNum(); i++) {
				boolean condition = 
						 sheet.getRow(i).getCell(4) != null &&
						 sheet.getRow(i).getCell(4).getCellType() == CellType.STRING &&
						 sheet.getRow(i).getCell(0).getCellType() == CellType.STRING &&
						 sheet.getRow(i).getCell(1).getCellType() == CellType.STRING &&
						 sheet.getRow(i).getCell(4).getStringCellValue().trim().equals("*");
				
				Employee employee = null;
				if(condition) {
					for(Employee e : fullEmployeesCollection) {
						boolean condition1 = 
								e.getName().equals(sheet.getRow(i).getCell(0).getStringCellValue().trim()) &&
								e.getPosition().equals(sheet.getRow(i).getCell(1).getStringCellValue().trim());
						if(condition1) {
							employee = e;
						}
					}
					
					if(employee == null) {
						throw new IOException("List in xls-file and in DB are not similar ");
					}
					else {
						selectedEmployeesCollection.add(employee);
					}
				}
				
			}
		}
		finally {
			if(fis != null) {
				fis.close();
			}
			if(wb != null) {
				wb.close();
			}
		}
		
		return selectedEmployeesCollection;
	}
}
