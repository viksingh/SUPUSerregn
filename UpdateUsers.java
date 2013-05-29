package supuser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Properties;

import com.sybase.sup.admin.context.*;
import com.sybase.sup.admin.client.SUPApplication;
import com.sybase.sup.admin.client.SUPObjectFactory;
import com.sybase.sup.admin.enumeration.application.APPCONNECTION_REGISTRATION;
import com.sybase.sup.admin.enumeration.application.APPCONNECTION_SETTING_FIELD;
import com.sybase.sup.admin.exception.SUPAdminException;
import com.sybase.sup.admin.vo.application.AppConnectionRegistrationRequestVO;
import com.sybase.sup.admin.vo.application.AppConnectionSettingVO;
import com.sybase.sup.admin.vo.application.ApplicationConnectionVO;
import com.sybase.sup.admin.vo.PaginationResult;
import com.sybase.sup.admin.vo.application.filter.AppConnectionFilterSortVO;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UpdateUsers {

  private static String host;
	private static String serverPort;
	private static String admin;
	private static String password;
	private static String activationCode;
	private static String expirationTime;
	private static String templateName;
	private static int intserverPort;
	private static int intactivationCode;
	private static int intexpirationTime;

	static String DESTINATION_NAME1 = "ABAP_AS_WITHOUT_POOL";
	static String DESTINATION_NAME2 = "ABAP_AS_WITH_POOL";
	static {

		// SUP Properties
		host = Sup.getProperty("sup.host");
		serverPort = Sup.getProperty("sup.port");
		intserverPort = Integer.parseInt(serverPort);

		admin = Sup.getProperty("sup.admin");
		password = Sup.getProperty("sup.admin.pass");
		activationCode = Sup.getProperty("sup.app.activation.code");
		intactivationCode = Integer.parseInt(activationCode);

		expirationTime = Sup.getProperty("sup.app.activation.expiration");
		intexpirationTime = Integer.parseInt(expirationTime);

		templateName = Sup.getProperty("sup.app.tpl");

		// JCO Properties
		Properties connectProperties = new Properties();

		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
				Sup.getProperty("JCO_ASHOST"));
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
				Sup.getProperty("JCO_SYSNR"));
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
				Sup.getProperty("JCO_CLIENT"));
		connectProperties.setProperty(DestinationDataProvider.JCO_USER,
				Sup.getProperty("JCO_USER"));
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
				"nestle13");
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
				Sup.getProperty("JCO_PASSWD"));
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG,
				Sup.getProperty("JCO_LANG"));
		connectProperties.setProperty(
				DestinationDataProvider.JCO_POOL_CAPACITY,
				Sup.getProperty("JCO_POOL_CAPACITY"));
		connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,
				Sup.getProperty("JCO_PEAK_LIMIT"));

		createDestinationDataFile(DESTINATION_NAME1, connectProperties);
		createDestinationDataFile(DESTINATION_NAME2, connectProperties);

	}

	static void createDestinationDataFile(String destinationName,
			Properties connectProperties) {
		File destCfg = new File(destinationName + ".jcoDestination");
		try {
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectProperties.store(fos, "for tests only !");
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to create the destination files", e);
		}
	}

	public static Logger testConnection(Logger logger) throws JCoException {

		JCoDestination destination = JCoDestinationManager
				.getDestination(DESTINATION_NAME1);

		logger.log(Level.INFO, "Attributes:");
		logger.log(Level.INFO, "SID is"
				+ destination.getAttributes().getSystemID());
		logger.log(Level.INFO, "Host is"
				+ destination.getAttributes().getHost());
		logger.log(Level.INFO, "System number is"
				+ destination.getAttributes().getSystemNumber());
		return logger;

	}

	public static Logger step2CoconnectusingPoolnnectUsingPool(Logger logger)
			throws JCoException {
		JCoDestination destination = JCoDestinationManager
				.getDestination(DESTINATION_NAME2);

		logger.log(Level.INFO, "Attributes check : using connection pool");
		logger.log(Level.INFO, "SID is"
				+ destination.getAttributes().getSystemID());
		logger.log(Level.INFO, "Host is"
				+ destination.getAttributes().getHost());
		logger.log(Level.INFO, "System number is"
				+ destination.getAttributes().getSystemNumber());

		destination.ping();
		return logger;

	}

	public static void step3SimpleCall() throws JCoException {
		JCoDestination destination = JCoDestinationManager
				.getDestination(DESTINATION_NAME2);
		JCoFunction function = destination.getRepository().getFunction(
				"STFC_CONNECTION");
		if (function == null)
			throw new RuntimeException(
					"BAPI_COMPANYCODE_GETLIST not found in SAP.");
		function.getImportParameterList().setValue("REQUTEXT", "Hello SAP");

		try {
			function.execute(destination);
		} catch (AbapException e) {
			// System.out.println(e.toString());
			return;
		}


	public static void step3WorkWithStructure() throws JCoException {
		JCoDestination destination = JCoDestinationManager
				.getDestination(DESTINATION_NAME2);
		JCoFunction function = destination.getRepository().getFunction(
				"RFC_SYSTEM_INFO");
		if (function == null)
			throw new RuntimeException(
					"BAPI_COMPANYCODE_GETLIST not found in SAP.");
		try {
			function.execute(destination);
		} catch (AbapException e) {
			// System.out.println(e.toString());
			return;
		}

		JCoStructure exportStructure = function.getExportParameterList()
				.getStructure("RFCSI_EXPORT");
		System.out.println("System info for "
				+ destination.getAttributes().getSystemID() + ":\n");
		for (int i = 0; i < exportStructure.getMetaData().getFieldCount(); i++) {
			System.out.println(exportStructure.getMetaData().getName(i) + ":\t"
					+ exportStructure.getString(i));
		}
		System.out.println();

		// JCo still supports the JCoFields, but direct access via getXX is more
		// efficient as field iterator
		System.out.println("The same using field iterator: \nSystem info for "
				+ destination.getAttributes().getSystemID() + ":\n");
		for (JCoField field : exportStructure) {
			System.out.println(field.getName() + ":\t" + field.getString());
		}
		System.out.println();
	}

	public static Logger doProcessing(Logger logger) throws JCoException {

		List<String> SAPUserlist = new ArrayList<String>();
		List<String> SUPUserlist = new ArrayList<String>();
		List<String> UserAddSAPlist = new ArrayList<String>();

		JCoDestination destination = JCoDestinationManager
				.getDestination(DESTINATION_NAME2);
		JCoFunction function = destination.getRepository().getFunction(
				"ZSD_SUP_GET_USER_LIST");
		if (function == null)
			throw new RuntimeException(
					"ZSD_SUP_GET_USER_LIST not found in SAP.");
		try {
			function.execute(destination);
		} catch (AbapException e) {

			logger.log(Level.INFO, e.toString());

			return logger;
		}

		JCoStructure returnStructure = function.getExportParameterList()
				.getStructure("RETURN");
		if (!(returnStructure.getString("TYPE").equals("") || returnStructure
				.getString("TYPE").equals("S"))) {
			throw new RuntimeException(returnStructure.getString("MESSAGE"));
		}

		logger.log(Level.INFO, '\n' + "Users in SAP");

		JCoTable codes = function.getTableParameterList().getTable("USERS");
		for (int i = 0; i < codes.getNumRows(); i++) {
			codes.setRow(i);

			logger.log(
					Level.INFO,
					codes.getString("BNAME") + '\t'
							+ codes.getString("NAME_FIRST")
							+ codes.getString("NAME_LAST") + '\n');

			SAPUserlist.add(codes.getString("BNAME"));

		}

		/*
		 * codes.firstRow(); for (int i = 0; i < codes.getNumRows(); i++,
		 * codes.nextRow()) {}
		 */
		ServerContext serverContext = new ServerContext(host, intserverPort,
				admin, password, true);
		System.err.println("ServerContext: User is " + serverContext.getUser());

		String noll = "";
		ClusterContext clusterContext = new ClusterContext(serverContext, noll);

		System.err.println("CluserContext: TimeZone is "
				+ clusterContext.getTimeZone());
		SUPApplication app = SUPObjectFactory.getSUPApplication(clusterContext);

		PaginationResult<ApplicationConnectionVO> pag = new PaginationResult<ApplicationConnectionVO>();

		AppConnectionFilterSortVO filter = new AppConnectionFilterSortVO();

		int a = 0;
		Long a1 = new Long(a);

		int max = 200;
		Integer max1 = new Integer(max);

		try {
			pag = app.getApplicationConnections(filter, a1, max1);

			int i = pag.getTotalAvailableRecords();

			logger.log(Level.INFO, '\n' + "Users in SUP");

			List<ApplicationConnectionVO> SUPlist = new ArrayList<ApplicationConnectionVO>();
			SUPlist = pag.getItems();

			for (int j = 0; j < SUPlist.size(); j++) {
				SUPUserlist.add(SUPlist.get(j).getApplicationUser()
						.toUpperCase());

				SUPlist.get(j).getApplicationUser();
				logger.log(Level.INFO, SUPlist.get(j).getApplicationUser()
						.toUpperCase());

			}
		} catch (SUPAdminException e) {
			System.out.println(e.toString());
			return logger;
		}

		for (int j = 0; j < SAPUserlist.size(); j++) {

			if (SUPUserlist.contains(SAPUserlist.get(j)) == false) {

				UserAddSAPlist.add(SAPUserlist.get(j));

			}
		}

		System.out
				.println("********************* User getting added**********************");

		for (int j = 0; j < UserAddSAPlist.size(); j++) {
			AppConnectionRegistrationRequestVO acrrvo1 = new AppConnectionRegistrationRequestVO();
			Collection<AppConnectionRegistrationRequestVO> vos = new ArrayList<AppConnectionRegistrationRequestVO>();

			Map<APPCONNECTION_REGISTRATION, Object> req1 = new HashMap<APPCONNECTION_REGISTRATION, Object>();

			logger.log(Level.INFO,
					'\n' + "User added: " + UserAddSAPlist.get(j));

			req1.put(APPCONNECTION_REGISTRATION.USER_ID, UserAddSAPlist.get(j));
			req1.put(APPCONNECTION_REGISTRATION.ACTIVATION_CODE,
					intactivationCode);
			req1.put(APPCONNECTION_REGISTRATION.EXPIRATION_HOUR,
					intexpirationTime);
			acrrvo1.setRequest(req1);
			vos.add(acrrvo1);

			try {
				AppConnectionSettingVO settings = new AppConnectionSettingVO();
				Map<APPCONNECTION_SETTING_FIELD, Object> settingMap = new HashMap<APPCONNECTION_SETTING_FIELD, Object>();
				settingMap.put(APPCONNECTION_SETTING_FIELD.SERVER_NAME, host);
				settings.setSetting(settingMap);
				app.registerApplicationConnections(templateName, vos, settings);
				logger.log(Level.INFO, '\n' + "User added successfully");

			}

			catch (SUPAdminException e) {
				logger.log(Level.INFO, '\n' + "User registration failed");
			}

		}

		return logger;
	}

	public static void main(String[] args) throws JCoException {

		try {

			LogManager lm = LogManager.getLogManager();
			Logger logger;
			FileHandler fh = new FileHandler("userAddition.log");
			logger = Logger.getLogger("UpdateUsers");

			lm.addLogger(logger);
			logger.setLevel(Level.INFO);
			fh.setFormatter(new SimpleFormatter());

			logger.addHandler(fh);

			logger = testConnection(logger);

			logger = step2CoconnectusingPoolnnectUsingPool(logger);

			logger = doProcessing(logger);

			fh.close();
		} catch (Exception e) {
			System.out.println("Exception thrown: " + e);
			e.printStackTrace();
		}

	}
}
