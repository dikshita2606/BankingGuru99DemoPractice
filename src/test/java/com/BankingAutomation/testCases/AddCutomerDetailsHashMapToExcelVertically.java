package com.BankingAutomation.testCases;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.BankingAutomation.pageObjects.AddCustomerPage;
import com.BankingAutomation.pageObjects.logInPage;

import net.bytebuddy.utility.RandomString;

@Test
public class AddCutomerDetailsHashMapToExcelVertically extends BaseClass{
	
	public static String filePath = System.getProperty("user.dir")+"/src/test/java/com/BankingAutomation/testData/CustomerDetails.xlsx";
	public static FileInputStream fin;
	public static FileOutputStream fout;
	public static XSSFWorkbook xlsWorkbook;
	public static XSSFSheet xlsSheet;
	public static XSSFCell xlsCell;
	public static XSSFRow xlsRow;

	public void addCustomer() throws InterruptedException, IOException {
		
		System.out.println("================Add Customer Test Case Storing Values Vertically===============");
		
		AddCustomerPage addcust = new AddCustomerPage(driver);
		driver.manage().window().maximize();
		logInPage lp =new logInPage(driver);
		lp.setUserName(UserName);
		logger.info("User Name set successfully");
		lp.setPassword(Password);
		logger.info("Password set successfully");
		lp.clickSubmit();
		logger.info("Submit Button Clicked");
		System.out.println("_________________________________________________________________");
		
		Thread.sleep(3000);
		
		addcust.newCustomerClick();
		logger.info("Add Customer Link is clicked");
		System.out.println("_________________________________________________________________");
		Thread.sleep(5000);
		
		System.out.println("---------------Calling handleGoogleAds function---------------");
		handleGoogleAds(driver);
		
		driver.switchTo().defaultContent();
		Thread.sleep(2000);
		System.out.println("---------------Calling addCustomerDetails function---------------");
		addCustomerDetails();
		
		try {
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
		}
		catch(NoAlertPresentException ex) {
			System.out.println(ex.getMessage());
		}
		
		System.out.println("---------------Calling tableToHashMapVertical function---------------");
		Map<String, String> datafound = tableToHashMapVertical();
		
		System.out.println("---------------Calling addHashMapToSheetHorizontally function---------------");
		addHashMapToSheetVertically(filePath,"CustomerDetails1",datafound);
		
		System.out.println("---------------Closing Excel and saving data---------------");
		try (FileOutputStream outputStream = new FileOutputStream(filePath)){
			xlsWorkbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

		Thread.sleep(3000);
		
		//click home Link Right bottom
		driver.findElement(By.xpath("//p/a")).click();
		
		lp.clicklogOut();
		logger.info("logOut clicked");
	     
	}

	//Function to enter Customer Details
	public void addCustomerDetails() throws InterruptedException {
		AddCustomerPage addcust = new AddCustomerPage(driver);
		addcust.custNameInput("Diks");
		addcust.custGender("female");
		addcust.custdob("10","15","2007");
		Thread.sleep(5000);
		addcust.custaddress("INDIA");
		addcust.custcity("Dewas");
		addcust.custstate("Dewas");
		addcust.custpinno("5000074");
		addcust.custtelephoneno("987890091");
		addcust.custPassword("Test@123");
		
		String email=randomestring()+"@gmail.com";
		addcust.custemailid(email);
		addcust.custsubmit();
		logger.info("Submit btn clicked");
		System.out.println("_________________________________________________________________");
		
		//for handling if ny type of alert 
		try {
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
		}
		catch(NoAlertPresentException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	//Function to Generate random String
	public String randomestring() {
		String randomString = RandomString.make(8);
		return(randomString);
	}

	//Function to convert WebTable Data to HashMap
	public Map<String, String> tableToHashMapVertical() {
		WebElement table = driver.findElement(By.xpath("//table[@id='customer']"));
		 
		//create hashmap to store header name and values adn stored the values in excel vertically
		Map<String, String> customerData = new HashMap<>();
		
		List<WebElement> rows = table.findElements(By.xpath(".//tr"));
		System.out.println("Row Size : "+rows.size());
		for(int i=3;i<rows.size()-1;i++) {
			WebElement row = rows.get(i);
			List<WebElement> cells = row.findElements(By.xpath(".//td"));
			String header = cells.get(0).getText();
			System.out.println("Header : "+header);
			String Value = cells.get(1).getText();
			System.out.println("Value : "+Value);
			customerData.put(header, Value);			
		}
		return customerData;
	}

	public void addHashMapToSheetVertically(String xlFile, String xlSheet, Map<String, String> hashMap) throws IOException {
		
		fin = new FileInputStream(filePath);
		xlsWorkbook = new XSSFWorkbook(fin);
		xlsSheet =  xlsWorkbook.getSheet(xlSheet);
		int rowCount = xlsSheet.getLastRowNum();
		System.out.println("Row Count in eexcel : "+rowCount);
				
		int rowNum = rowCount+2;
    	System.out.println("RowNum : "+rowNum);
	    System.out.println(rowNum);
	    for (Map.Entry<String, String> entry : hashMap.entrySet()) {
	    	Row row = xlsSheet.createRow(rowNum++);
	        row.createCell(0).setCellValue(entry.getKey());
	        row.createCell(1).setCellValue(entry.getValue());
	    }
	}
}
