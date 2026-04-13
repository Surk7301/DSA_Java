import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.yodlee.dap.gatherer.commonutils.StringConstants;
import com.yodlee.dap.gatherer.commonutils.XProcessor;
import com.yodlee.dap.gatherer.gather.ClassArgs;
import com.yodlee.dap.gatherer.gather.Constants;
import com.yodlee.dap.gatherer.gather.IYodOAuth;
import com.yodlee.dap.gatherer.gather.IYodRobot;
import com.yodlee.dap.gatherer.gather.IYodValidator;
import com.yodlee.dap.gatherer.gather.LoginInfo;
import com.yodlee.dap.gatherer.gather.OAuthSiteConnectionHelper;
import com.yodlee.dap.gatherer.gather.PropertyBagManager;
import com.yodlee.dap.gatherer.gather.RequestItem;
import com.yodlee.dap.gatherer.gather.User;
import com.yodlee.dap.gatherer.gather.UserAddress;
import com.yodlee.dap.gatherer.gather.UserLocation;
import com.yodlee.dap.gatherer.gather.UserProfile;
import com.yodlee.dap.gatherer.gather.YUtilities;
import com.yodlee.dap.gatherer.gather.content.AddressType;
import com.yodlee.dap.gatherer.gather.content.BankAccount;
import com.yodlee.dap.gatherer.gather.content.CardAccount;
import com.yodlee.dap.gatherer.gather.content.Container;
import com.yodlee.dap.gatherer.gather.content.FinancialContainer;
import com.yodlee.dap.gatherer.gather.content.IndividualInformation;
import com.yodlee.dap.gatherer.gather.content.Loan;
import com.yodlee.dap.gatherer.gather.content.OrganizationProfile;
import com.yodlee.dap.gatherer.gather.exceptions.GeneralException;
import com.yodlee.dap.gatherer.gather.exceptions.OAuthConsentExpiredException;
import com.yodlee.dap.gatherer.gather.exceptions.OAuthConsentRequiredException;
import com.yodlee.dap.gatherer.gather.exceptions.OAuthConsentRevokedException;
import com.yodlee.dap.gatherer.gather.exceptions.SiteApplicationErrorException;
import com.yodlee.dap.gatherer.gather.exceptions.SiteDownForMaintenanceException;
import com.yodlee.dap.gatherer.sanitizer.YProdDataLogger;
import com.yodlee.dap.gatherer.validationutils.FinancialContainerUtil;
import com.yodlee.dap.gatherer.validationutils.ScriptConstants;
import com.yodlee.dap.gatherer.validationutils.ScriptUtil;
import com.yodlee.dap.gatherer.validationutils.XMLUtil;
import com.yodlee.dap.gatherer.validationutils.YDataLogger;
import com.yodlee.dap.gatherer.validationutils.FinancialContainerUtil.AttributeType;
import com.yodlee.dap.gatherer.ylogger.YGathererLogSummary;
import com.yodlee.dap.gatherer.ylogger.YGathererLogger;

import yodlee.gather.exceptions.OAuthAuthorizationRequiredException;
import yodlee.gather.exceptions.OAuthSiteRequestTimedOutException;
import yodlee.gather.exceptions.OAuthSiteUnavailableException;



public abstract class AUOBCDRSandboxBase extends XProcessor implements IYodValidator,IYodOAuth {
    /*
     * RCSId to identify the version
     */
    public static String RCSId = "$Id: //gatherer/dap/agents/main/java/AUOBCDRSandboxBase.java#21 $";
    /*
     * Meta-field details
     */
    private static final String OAUTH_METAFIELD = "token";

    /*
     * URI - value fetched from the SUM_INFO_PARAM_KEY
     * (COM.YODLEE.SITE.OB_DATA_ACCESS_DETAILS)
     */
    protected static String API_END_POINT_URI = "";

    /*
     * ID allocated to each FI by Open Banking
     */
    protected static String X_FAPI_FINANCIAL_ID = "";

    /*
     * Version of the API Endpoint requested
     */
    protected static String X_V = "1";

    /*
     * Database identifiers
     */
    private static final String STAGE_FIREMEM_DB_ID = "fmemsdb";

    private static final String STAGE_DB_ID = "scstgx";

    private static final String UK_STAGE_DB_ID = "ukstg";


    /*
     * Request Type identifiers
     */
    protected static final String ACCOUNT_LEVEL_REQUEST = "ACCOUNT_SUMMARY_REQ";

    protected static final String ACCOUNT_TRANSACTION_REQUEST = "ACCT_TXN_REQUEST";

    protected static final String STATEMENT_REQUEST = "ACCT_STM_REQUEST";

    protected static final String CUSTOMER_DETAIL = "CUST_DETAIL";

    public static String ACCOUNT_SUMMARY_URL = "";

    protected static String sum_info = "";

    protected static String cobrand_id = "";

    protected boolean addRefresh = false;

    protected boolean editRefresh = false;
    /*
     * Data feed Response object identifiers
     */
    protected static final String DATA_OBJ_NODE = "data";

    protected static final String ACCOUNT_OBJ_NODE = "accounts";

    protected static final String LINKS_OBJ_NODE = "links";

    protected static final String TRANSACTION_OBJ_NODE = "transactions";

    /*
     * Account Level Response Nodes
     */
    protected static final String ACCOUNT_ID = "accountId";

    protected static final String ACCOUNT_NICK_NAME = "nickname";

    protected static final String ACCOUNT_TYPE = "productCategory";

    protected static final String ACCOUNT_HOLDER = "mailingName";

    protected static final String ACCOUNT_NAME = "displayName";

    protected static final String PRODUCT_NAME = "productName";

    protected static final String MASKED_NUMBER = "maskedNumber";

    protected static final String OPEN_STATUS = "openStatus";

    protected static final String IS_OWNED = "isOwned";

    protected static final String ACCOUNT_OWNERSHIP = "accountOwnership";

    /*
     * Account Detail Level Response fields
     */
    protected static final String ACCOUNT_NUMBER = "accountNumber";

    protected static final String ROUTING_NUMBER = "bsb";

    protected static final String TERM_DEPOSIT = "termDeposit";

    protected static final String CD_MATURITY_DATE = "maturityDate";

    protected static final String MATURITY_AMOUNT = "maturityAmount";

    protected static final String MATURITY_INSTRUCTION = "maturityInstructions";

    protected static final String CREDIT_CARD = "creditCard";

    protected static final String CARD_DUE_DATE = "paymentDueDate";

    protected static final String CARD_MIN_PAYMENT = "minPaymentAmount";

    protected static final String AMOUNT_DUE = "paymentDueAmount";

    protected static final String LOAN = "loan";

    protected static final String LOAN_DUE_DATE = "nextInstalmentDate";

    protected static final String LOAN_MATURITY_DATE = "loanEndDate";

    protected static final String ORIGINATION_DATE = "originalStartDate";

    protected static final String ORIGINAL_LOAN_AMOUNT = "originalLoanAmount";

    protected static final String LOAN_MIN_PAYMENT = "minInstalmentAmount";

    /*
     * Balance Level Response fields
     */
    protected static final String CURRENCY = "currency";

    protected static final String AVAILABLE_BALANCE = "currentBalance";

    protected static final String CURRENT_BALANCE = "availableBalance";

    protected static final String CREDIT_LINE = "creditLimit";

    /*
     * Transaction Level Response fields
     */
    protected static final String AMOUNT = "amount";

    protected static final String POST_DATE = "postingDateTime";

    protected static final String TRANS_DATE = "valueDateTime";

    protected static final String STATUS = "status";

    protected static final String DESCRIPTION = "description";

    protected static final String ADDRESSLINE = "AddressLine";

    protected static final String TRANSACTION_ID = "transactionId";

    protected static final String TRANSACTION_TYPE = "type";

    protected static final String MERCHANT_NAME = "merchantName";

    protected static final String MERCHANT_CODE = "merchantCategoryCode";

    protected static final String BILLER_CODE = "billerCode";

    protected static final String BILLER_NAME = "billerName";

    protected static final String APCA_NUMBER = "apcaNumber";


    /*
     * Statement Level Response fields
     */
    protected static final String Startdate = "StartDateTime";

    protected static final String Enddate = "EndDateTime";

    protected static final String CreationDate = "CreationDateTime";

    protected static final String StatementType = "STATE_TYPE";

    protected static final String StatementInterest_Type = "ST_INTEREST_TYPE";

    protected static final String StatementInterest_Amount = "STM_INTETEST_AMOUNT";

    protected static final String Interest_Indicator = "ST_INTEREST_INDICATOR";

    protected static final String StatementDueDate = "STM_DUE_DATE";

    protected static final String Previous_Balance = "STM_PRV_AMOUNT";

    protected static final String Minimum_Payment_Due = "STM_MINPAY_AMT";

    protected static final String Statement_Amount_type = "STM_AMOUNT_TYPE";

    protected static final String Statement_Amount_Indicator = "STM_AMOUNT_INDICATOR";

    protected static final String STATEMENT_ID = "StatementId";

    protected static final String PAYMENT_DUE_DATE = "Pay_Due";

    protected static final String NEXT_STM_DATE = "NEXT_STM_DATE";

    protected static final String STM_AMOUNT = "STATEMENT_AMOUNT";

    protected static final String STM_AMOUNT_INDICATOR = "STATEMENT_AMOUNT_INDICATOR";

    protected static final String STM_AMT_CURR = "STATEMENT_CURRENCY";

    protected static final String ESTM_STM_AMOUNT = "STATEMENT_AMOUNT_ESTM";

    protected static final String ESTM_STM_AMOUNT_INDICATOR = "STATEMENT_AMOUNT_ESTM_INDICATOR";

    protected static final String MIN_STM_AMOUNT = "STATEMENT_AMOUNT_MIN";

    protected static final String MIN_STM_AMOUNT_INDICATOR = "STATEMENT_AMOUNT_MIN_INDICATOR";

    protected static String x_fapi_id = "";

    protected static int i = 0;

    /*
     * Customer Profile
     */

    protected String accountHolder= null;

    protected HashMap<String,Object> piiData= null;

    //PII variables
    protected static final String FULL_NAME = "fullName";
    protected static final String EMAILID = "email";
    protected static final String MOBILE = "mobileNo";
    protected static final String PHONE = "phoneNo";
    protected static final String ADDRLINE1 = "line1";
    protected static final String ADDRLINE2 = "line2";
    protected static final String ADDRLINE3 = "line3";
    protected static final String CITY = "city";
    protected static final String STATE = "state";
    protected static final String COUNTRY = "country";
    protected static final String ZIP = "zip";
    protected static final String FULL_ADDRESS = "fullAddress";
    protected static final String HOME_ADDRESS_TYPE = "homeAddressType";
    protected static final String MAIL_ADDRESS_TYPE = "mailAddressType";
    protected static final String OFFICE_ADDRESS_TYPE="officeAddressType";
    protected static final String ABN="abn";
    protected static final String ACN="acn";
    protected static final String LEGAL_NAME="legalName";
    protected static final String BUSINESS_NAME="businessName";

    private PrivateKey privateKey;
    private X509Certificate x509Certificate;

    /*
     * Validation document and Auto Population document initialization
     */
    protected static Document validationDoc = null;

    protected static Document autoPopulationDoc = null;

    protected static String validationRule;

    protected static String MEM_ID = "";

    private static boolean CTSTransactionFlow=false;

    protected static Document validationDoc1 = null;

    protected static Document autoPopulationDoc1 = null;

    protected static String validationRule1;

    protected static Map<String,String> accountTypeMap = null;

    protected static final String DUMMY_CARD_ACCOUNT_CURRENCY = "AUD";

    static {
        accountTypeMap = new HashMap<>();
        accountTypeMap.put("TRANS_AND_SAVINGS_ACCOUNTS", "BANK");
        accountTypeMap.put("TERM_DEPOSITS", "BANK");
        accountTypeMap.put("CRED_AND_CHRG_CARDS", "CREDIT");
        accountTypeMap.put("BUSINESS_LOANS", "LOAN");
        accountTypeMap.put("MARGIN_LOANS", "LOAN");
        accountTypeMap.put("PERS_LOANS", "LOAN");
    }

    static {
        validationRule = "";
        try {
            validationDoc = XMLUtil.createDocument(validationRule);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    static {
        validationRule1 = "";
        try {
            validationDoc1 = XMLUtil.createDocument(validationRule1);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param LoginInfo
     * @param User
     * @param ClassArgs
     * @param IYodRobot
     *
     *                  This method will get the OAuth Access Token from the request
     *                  XML and make the first API call that fetch the account level
     *                  details.
     */
    @Override
    public int login(LoginInfo loginInfo, User user, ClassArgs classArgs, IYodRobot pRobot) throws Exception {

        Long user_id = user.getMemId();
        if (user_id != null) {
            MEM_ID = user_id.toString();
        }

        RequestItem item = user.getItem(0);
        initializeParams(pRobot, item, YGathererLogger.getLogSummaryObj());

        DateFormat dateformat = new SimpleDateFormat(ScriptConstants.DATEFORMAT_DD_HYP_MM_HYP_YYYY);
        Date date = new Date();
        YProdDataLogger.initialize(MEM_ID, Constants.MEM_ID, dateformat.format(date),
                ScriptConstants.DATEFORMAT_DD_HYP_MM_HYP_YYYY);

        String endSiteResp = pRobot.getDataAccessAttribute(StringConstants.OAUTH_END_SITE_RESPONSE);
        YProdDataLogger.out("^^^^^The endSiteResp is = " + endSiteResp);



        if (!ScriptUtil.isNullValue(endSiteResp))
            OAuthFeedErrors.handleOCCErrors(endSiteResp, pRobot, item);

        JSONObject endSiteResponseToken=new JSONObject(endSiteResp);
        String endSiteAccess=(String) endSiteResponseToken.getJSONObject("body").get("access_token");
        YDataLogger.out("endSiteAccess Token Size > " + endSiteAccess.length());

        String accesstoken = user.get(OAUTH_METAFIELD);
        accesstoken = pRobot.doCrypt(accesstoken);
        YDataLogger.out("Access Token Size > " + accesstoken.length());

        //Changing to endSiteResponseToken
        containerHelper.put("accesstoken", endSiteAccess);

        sum_info = ScriptUtil.getSumInfoId(user).trim();
        cobrand_id = ScriptUtil.getCobrandID(logSummary);

        long numsucc = item.getNumSuccessfulRefresh();

        YDataLogger.out("2^^^^^The sum info id is=" + sum_info);
        YDataLogger.out("2^^^^^The cobrand id is=" + cobrand_id);
        YDataLogger.out("2^^^^^The number of successful refreshes is=" + numsucc);
        YProdDataLogger.out("2^^^^^The sum info id is=" + sum_info);
        YProdDataLogger.out("2^^^^^The number of successful refreshes is=" + numsucc);
        YProdDataLogger.out("2^^^^^The cobrand id is=" + cobrand_id);

        String refreshSource = YGathererLogger.getLogSummaryObj().getRefreshSource();
        YProdDataLogger.out("refreshSource::"+refreshSource);

        setURLAndVersion(pRobot);

        YDataLogger.out("sum_info id printed-------: " + sum_info);
        String serverURL = getServerURL(pRobot, ACCOUNT_LEVEL_REQUEST, null, null, null);

        String contents = postReqAndGetContents(pRobot, serverURL, ACCOUNT_LEVEL_REQUEST);
        YDataLogger.out("Santizied Account response > " + contents);
        containerHelper.put("accountsresponse", contents);

        piiData=getCustomerProfile(pRobot);

        return RequestItem.RETURN_CODE_SUCCESS;
    }

    /**
     * Method to read the SUM_INFO_PARAM_VALUE for URI and FAPI ID from the request
     * XML and construct the common ACCOUNT_SUMMARY_URL
     *
     * @param pRobot
     * @throws JSONException
     */
    private void setURLAndVersion(IYodRobot pRobot) throws JSONException {
        String openBankingDetails = pRobot.getPropertyValue(PropertyBagManager.SUM_INFO_PROPERTY_BAG,
                "COM.YODLEE.SITE.OB_DATA_ACCESS_DETAILS");

        if ((openBankingDetails != null) && (openBankingDetails.trim().length() > 1)) {
            openBankingDetails = this.replaceSpecialChars(openBankingDetails);
        }

        YDataLogger.out("2^^^^^^^^^The parameters for the request is=" + openBankingDetails);

        YDataLogger.out("OB Details Param Value > " + openBankingDetails);

        if (ScriptUtil.isNullValue(openBankingDetails)) {
            throw new GeneralException("OB_DATA_ACCESS_DETAILS SIPK value is Null >");
        }

        JSONObject OBDetailsObj = new JSONObject(openBankingDetails);

        API_END_POINT_URI = OBDetailsObj.getString("API_URI");
        X_V = OBDetailsObj.getString("X_V");

        YDataLogger.out("2^^^^^^The API URI is=" + API_END_POINT_URI);
        YDataLogger.out("2^^^^^^The x-v is=" + X_V);

        if (API_END_POINT_URI.endsWith("/")) {
            ACCOUNT_SUMMARY_URL = API_END_POINT_URI + "accounts";
        } else {
            ACCOUNT_SUMMARY_URL = API_END_POINT_URI + "/accounts";
        }
    }

    private String replaceSpecialChars(String input) {
        YDataLogger.out("The OB request input is:" + input);
        char c2 = input.charAt(1);
        char c4 = '"';
        return input.replace(c2, c4);
    }

    /**
     * This method is to make the HTTPS request and return the response contents
     *
     * @param pRobot
     * @param serverURL
     * @return
     * @throws Exception
     */
    protected String postReqAndGetContents(IYodRobot pRobot, String serverURL, String requestType) throws Exception {
        String accessToken = (String) containerHelper.get("accesstoken");
        //setting num_navigaion for cdr api count
        pRobot.incrementNumNavigations();

        String contents = "";

        HttpsURLConnection accountsthttp = setTwoWaySSLConnection(serverURL);
        YDataLogger.out("2^^^^^^The attributes send inm the connection request are::::" + accountsthttp);
        accountsthttp = setHttpHeaders(accountsthttp, accessToken, requestType);
        accountsthttp.setReadTimeout(120000);
        contents = sendRequest(accountsthttp);
        YDataLogger.out("Response > " + contents);

        return contents;
    }

    /**
     * Method to scrap account holder information
     * @param pRobot
     * @return String
     * @throws Exception
     */
    protected String getAccountHolder(IYodRobot pRobot) throws Exception
    {
        String fullName = (String) piiData.get(FULL_NAME);
        if(!ScriptUtil.isNullValue(fullName)) {
            accountHolder = fullName;
        }
        return accountHolder;
    }


    protected HashMap<String, Object> getCustomerProfile(IYodRobot pRobot) throws Exception {

        YDataLogger.out("Inside Customer Profile");

        String fullName = "";
        String line1 = "";
        String line2 = "";
        String line3 = "";
        String city="";
        String state="";
        String country = "";
        String zip = "";
        String emailIDUse = "";
        String mobileNumber = "";
        String addressType = "";
        String fullAddress = "";
        piiData = new HashMap<String,Object>();

        //PII API hit
        String SERVER_URL = getServerURL(pRobot,CUSTOMER_DETAIL,null,null,null);
        String customerDetails = postReqAndGetContents(pRobot, SERVER_URL, CUSTOMER_DETAIL);

        YProdDataLogger.out("Customer Response Start - ");
        YProdDataLogger.out(customerDetails);
        YProdDataLogger.out("Customer Response End - ");

        JSONObject data = new JSONObject(customerDetails).getJSONObject("data");
        JSONObject cusInfo = data.optJSONObject("person");
        JSONObject organisation = data.optJSONObject("organisation");

        if(cusInfo.optString("firstName")!=null && cusInfo.optString("lastName") != null){

            fullName = cusInfo.optString("firstName") + " " + cusInfo.optString("lastName");
            YDataLogger.out("^^^^The full name is^^^^^^"+fullName);
            if(!ScriptUtil.isNullValue(fullName)) {
                piiData.put(FULL_NAME, fullName);
            }
        }else {
            throw new GeneralException("FirstName or LastName is Null");
        }

        if(cusInfo.optJSONArray("physicalAddresses") != null)
        {
            JSONArray addressArray = cusInfo.getJSONArray("physicalAddresses");
            for(int i =0;i<addressArray.length();i++) {

                HashMap<String, String> address=new HashMap<String, String>();

                JSONObject addressObject = addressArray.optJSONObject(i).optJSONObject("simple");

                if(addressObject!=null) {
                    YDataLogger.out("----adressss-:" + addressObject);
                    line1 = addressObject.optString("addressLine1");
                    line2 = addressObject.optString("addressLine2");
                    line3 = addressObject.optString("addressLine3");
                    city = addressObject.optString("city");
                    state = addressObject.optString("state");
                    country = addressObject.optString("country");
                    zip = addressObject.optString("postCode");

                    address.put(ADDRLINE1, line1);
                    address.put(ADDRLINE2, line2);
                    address.put(ADDRLINE3, line3);
                    address.put(CITY, city);
                    address.put(STATE, state);
                    address.put(COUNTRY, country);
                    address.put(ZIP, zip);

                    fullAddress = line1 +" "+line2 +" "+line3 +" "+ city +" "+ state +" "+ country + " " +zip;
                    fullAddress.trim();

                    YDataLogger.out("---full address--:" + fullAddress);
                    address.put(FULL_ADDRESS, fullAddress);

                    addressType = addressArray.optJSONObject(i).optString("purpose");
                    YDataLogger.out("---addressType---"+addressType);
                    if(addressType.equals("PHYSICAL")) {
                        piiData.put(HOME_ADDRESS_TYPE, address);
                    }else  if(addressType.equals("MAIL")){
                        piiData.put(MAIL_ADDRESS_TYPE, address);
                    }else  if(addressType.equals("REGISTERED")){
                        piiData.put(OFFICE_ADDRESS_TYPE, address);
                    }

                }
            }
        }

        if(cusInfo.optJSONArray("emailAddresses") != null)
        {
            JSONArray emailArray = cusInfo.getJSONArray("emailAddresses");
            for(int i =0;i<emailArray.length();i++) {
                JSONObject emailObject = emailArray.optJSONObject(i);
                emailIDUse = emailObject.optString("address");
                piiData.put(EMAILID, emailIDUse);

            }

        }

        if(cusInfo.optJSONArray("phoneNumbers") != null)
        {
            JSONArray phoneArray = cusInfo.getJSONArray("phoneNumbers");
            for(int i =0;i<phoneArray.length();i++) {
                JSONObject phoneObject = phoneArray.optJSONObject(i);


                mobileNumber = phoneObject.optString("fullNumber");
                if(phoneObject.optString("purpose").equals("MOBILE")) {
                    piiData.put(MOBILE,mobileNumber);
                }else {
                    piiData.put(PHONE, mobileNumber);
                }

            }

        }

        if(organisation!=null) {
            piiData.put(ABN, organisation.optString(ABN));
            piiData.put(ABN, organisation.optString(ACN));
            piiData.put(BUSINESS_NAME, organisation.optString(BUSINESS_NAME));
            piiData.put(LEGAL_NAME, organisation.optString(LEGAL_NAME));
        }

        containerHelper.put("piiData", piiData);
        return piiData;
    }



    /**
     * To construct and return the server URL for all type of requests(Accounts,
     * balances and Transactions)
     *
     * @param requestType
     * @param accountId
     * @param startDate
     * @param endDate
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String getServerURL(IYodRobot pRobot, String requestType, String accountId, String startDate,
                                  String endDate) throws UnsupportedEncodingException {
        String serverURL = "";

        YDataLogger.out("Request is " + requestType);
        YProdDataLogger.out("^^^^^Global ACCOUNT_SUMMARY_URL in GetServerURL"+ACCOUNT_SUMMARY_URL);

        if (requestType.equals(ACCOUNT_LEVEL_REQUEST)) {
            serverURL = ACCOUNT_SUMMARY_URL+"?page-size=999";
        }
        else if (requestType.equals(ACCOUNT_TRANSACTION_REQUEST)) {
            if(!ScriptUtil.isNullValue(accountId)) {
                serverURL = ACCOUNT_SUMMARY_URL + "/" + accountId + "/transactions";

                if (!ScriptUtil.isNullValue(startDate) && !ScriptUtil.isNullValue(endDate)) {
                    if(startDate.contains("+")) { startDate =
                            URLEncoder.encode(startDate,"UTF-8"); } if(endDate.contains("+")) { endDate =
                            URLEncoder.encode(endDate,"UTF-8"); } serverURL = serverURL + "?oldest-time="+ startDate + "&newest-time=" + endDate;
                }
            }
            else {
                throw new GeneralException("No Account ID present!");
            }
        }else if(requestType.equals(CUSTOMER_DETAIL)) {
            YDataLogger.out("ACCOUNT_SUMMARY_URL"+ACCOUNT_SUMMARY_URL);
            if(ACCOUNT_SUMMARY_URL.contains("banking/accounts")) {
                serverURL=YUtilities.removeAndReplace(ACCOUNT_SUMMARY_URL, "banking/accounts", "common/customer");
            }else {
                throw new GeneralException("New variation for customer details");
            }
        }

        YDataLogger.out("Request type >" + requestType + " < Server URL > " + serverURL);
        YProdDataLogger.out("Request type >" + requestType + " < Server URL > " + serverURL);

        return serverURL;
    }

    public void getPreConnectionInfo(Map<String, String> preConnectionDetails) {
        preConnectionDetails.put(StringConstants.TWO_WAY_SSL_REQUIRED, "true");
        preConnectionDetails.put(StringConstants.TRANSPORT_CERTIFICATE_REQUIRED, "true");
        //preConnectionDetails.put(StringConstants.TLS_VERSION, "1.2");
        //preConnectionDetails.put(StringConstants.TLS_VERSION, "TLSv1.2");
    }

    /**
     * This method is used for setting up Two way SSL connection and this is for
     * Backward Compatibility
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public HttpsURLConnection setTwoWaySSLConnection(String urlStr) throws Exception {
        HttpsURLConnection conn = null;
        String certificateAlias = OAuthSiteConnectionHelper.getKeyStoreAlias();

        try {
            YDataLogger.out("2^^^^^Coming here in try block....");
            conn = OAuthSiteConnectionHelper.get2WaySSLConnection(urlStr, certificateAlias, "occ.keystore");

        } catch (Exception e1) {
            YDataLogger.out("Printitng exception---:" + e1.getMessage());
            YProdDataLogger.out("Printitng exception---:" + e1.getMessage());
        }

        return conn;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public X509Certificate getX509Certificate() {
        return this.x509Certificate;
    }

    public void setX509Certificate(X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
    }

    /**
     * Verify the environment from where the request is received
     *
     * @return
     */
    protected boolean isStageRequest() {
        if (YGathererLogger.getLogSummaryObj().getDbId().contains(STAGE_FIREMEM_DB_ID)
                || YGathererLogger.getLogSummaryObj().getDbId().contains(STAGE_DB_ID)
                || YGathererLogger.getLogSummaryObj().getDbId().contains(UK_STAGE_DB_ID)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to set the HTTP headers
     *
     * @param connection
     * @param accessToken
     * @return
     * @throws Exception
     */
    public HttpsURLConnection setHttpHeaders(HttpsURLConnection connection, String accessToken, String requestType) throws Exception {


        //accessToken = "";
        accessToken = "Bearer " + accessToken;
        YProdDataLogger.out("Access token length :: " + accessToken.length());
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        if(requestType.equalsIgnoreCase(ACCOUNT_LEVEL_REQUEST)) {
            connection.setRequestProperty("x-v", "2");
        } else {
            connection.setRequestProperty("x-v", "1");
        }
        connection.setRequestProperty("Authorization", accessToken);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
        YProdDataLogger.out("x-fapi-auth-date"+formatter.format(ZonedDateTime.now(ZoneOffset.UTC)));
        YDataLogger.out("x-fapi-auth-date"+""+formatter.format(ZonedDateTime.now(ZoneOffset.UTC)));
        connection.setRequestProperty("x-fapi-auth-date", ""+formatter.format(ZonedDateTime.now(ZoneOffset.UTC)));

		/*String x_fapi_id = "ab9d225e-a53a-4cd7-ac60-c02694a19c14";
		connection.setRequestProperty("x-fapi-interaction-id", x_fapi_id);

		YProdDataLogger.out("2^^^^The x-fapi-interaction-id is::::::" + x_fapi_id);
		YDataLogger.out("2^^^^The x-fapi-interaction-id is::::::" + x_fapi_id);*/

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);

        return connection;
    }

    /**
     * Method to make a request and return the response
     *
     * @param connection
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public String sendRequest(HttpsURLConnection connection) throws IOException, JSONException, Exception {
        String result = null;
        StringBuffer stringbuf = new StringBuffer();

        try {
            InputStream inputstream = connection.getInputStream();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));


            String str = null;
            while ((str = bufferedreader.readLine()) != null) {
                YDataLogger.out("Received line " + str);
                stringbuf.append(str);
            }

        } catch (MalformedURLException e) {
            throw new GeneralException("MalformedURLException during API request > " + e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new GeneralException("SocketTimeoutException occured after waiting for 120 sec  > " + e.getMessage());
        } catch (Exception e) {

            YProdDataLogger.out("Response code > " + connection.getResponseCode());
            YProdDataLogger.out("Response msg > " + connection.getResponseMessage());
            YProdDataLogger.out("err message > " + connection.getErrorStream());

            InputStream inputstream = connection.getErrorStream();
            if (inputstream != null) {
                YDataLogger.out("---Inside inputstram not null --:" + inputstream);
                InputStreamReader isr = new InputStreamReader(inputstream);
                BufferedReader bufferedreader = new BufferedReader(isr);

                StringBuilder response = new StringBuilder();
                String str = null;
                while ((str = bufferedreader.readLine()) != null) {
                    YDataLogger.out("Received line " + str);
                    response.append(str);
                    stringbuf.append(str);
                }

                str = response.toString().trim();

                /*
                 * The below conditions handles the case of Halifax - ASPSP returns Forbidden -
                 * Account closed or suspended in error stream for the balance call of accounts
                 * of the user which are closed or suspended so that TPP can skip fetching the
                 * balance of that particular account and proceed further.
                 */
                if (connection.getResponseCode() == 403 && str.toLowerCase().contains("account closed or suspended")) {
                    YDataLogger.out("Error Message in catch checking for Closed/Suspended Account >>" + str);
                    YProdDataLogger.out("Error Message in catch checking for Closed/Suspended Account >>" + str);
                    result = str;
                    return result;
                }

                YDataLogger.out("Error Message in catch checking >>" + str);
                YProdDataLogger.out("Error Message in catch checking >>" + str);

                int val = handle_FeedErrors(response, pRobot, str, connection.getResponseCode(),
                        connection.getResponseMessage(), item, logSummary);

                if (val == 1) {
                    throw new GeneralException("IOException during API request > " + e.getMessage());
                }
            } else {
                if (connection.getResponseCode() == 502
                        && connection.getResponseMessage().toLowerCase().equals("bad gateway")) {
                    throw new SiteApplicationErrorException("Bad Gateway");
                } else {
                    throw new GeneralException("IOException during API request > " + e.getMessage());
                }
            }
        }

        result = stringbuf.toString();
        return result;
    }

    /**
     * Method to get Account Summary level information
     *
     * @param item
     * @param pRobot
     * @param getAllAccounts
     * @param isValidationRequired
     * @return
     * @throws Exception
     */
    @Override
    public List<? extends Container> getAccounts(RequestItem item, IYodRobot pRobot, boolean getAllAccounts,
                                                 boolean isValidationRequired) throws Exception {

        List<Container> accountList = new ArrayList<Container>();

		/*
		if(CTSTransactionFlow) {
			YProdDataLogger.out("Insider the CTSTransaction FLow testing, now u can set hardcoded account");
		} */

        String accountResponse = (String) containerHelper.get("accountsresponse");
        YDataLogger.out("Account Response Start - ");
        YDataLogger.out(accountResponse);
        YDataLogger.out("Account Response End - ");
        YProdDataLogger.out("Account Response Start - ");
        YProdDataLogger.out(accountResponse);
        YProdDataLogger.out("Account Response End - ");


        if(accountResponse == null) {
            throw new GeneralException("Account Response is Null");
        }

        List<HashMap<String, String>> parsedAccountsList = parseAccountResponse(accountResponse);
        YDataLogger.out("checking account List---------------: " + parsedAccountsList.size());
        YProdDataLogger.out("checking account List---------------: " + parsedAccountsList.size());

        for (HashMap<String, String> account : parsedAccountsList) {

            String accountid = account.get(ACCOUNT_ID);
            String accountName = account.get(ACCOUNT_NAME);
            String accountType = account.get(ACCOUNT_TYPE);
            String accountNumber = account.get(MASKED_NUMBER);
            String openStatus = account.get(OPEN_STATUS);
            String accountNickName = account.get(ACCOUNT_NICK_NAME);
            String productName = account.get(PRODUCT_NAME);


            YDataLogger.out("^^^^^accountid"+accountid+"^^^accountName"+accountName+"^^^^^accountType"+accountType+"accountNickName"+accountNickName);
            YProdDataLogger.out("^^^^^accountid"+accountid+"^^^accountName"+accountName+"^^^^^accountType"+accountType+"accountNickName"+accountNickName);

            YProdDataLogger.out("^^^^^accountTypeMap.get(accountType)"+accountTypeMap.get(accountType)+
                    "containerHelper.isSupportedContainer(TAG_BANK, getAllAccounts)"+containerHelper.isSupportedContainer(TAG_BANK, getAllAccounts)
                    +"containerHelper.isSupportedContainer(TAG_LOAN, getAllAccounts)"+containerHelper.isSupportedContainer(TAG_LOAN, getAllAccounts));


            if(!accountTypeMap.containsKey(accountType)) {
                throw new GeneralException("New Account Type found, mapping needed"+accountType);
            }
            if (accountTypeMap.get(accountType).equalsIgnoreCase("BANK") && containerHelper.isSupportedContainer(TAG_BANK, getAllAccounts)) {
                accountList.add(setBankDetails(account));
            }
            if(accountTypeMap.get(accountType).equalsIgnoreCase("LOAN") && containerHelper.isSupportedContainer(TAG_LOAN, getAllAccounts)) {
                accountList.add(setLoanDetails(account));
            }
        }
        //Adding dummy card account for CDR sandbox
        if (containerHelper.isSupportedContainer(TAG_CREDITS, getAllAccounts)) {
            accountList.add(setDummyCardDetails());
        }

        return accountList;
    }

    private Container setDummyCardDetails() throws Exception {

        YDataLogger.out("setting dummy card account");

        CardAccount cardAccount = new CardAccount();
        cardAccount.setAccountNicknameAtSrcSite("Visa Card");
        cardAccount.setAccountNumber("1234");
        cardAccount.setUnmaskedAccountNumber("1234123412341234");
        cardAccount.setAccountName("Visa Card");
        cardAccount.setAccountStatus(CardAccount.ACCOUNT_STATUS_ACTIVE);
        cardAccount.setAccountClassification(CardAccount.ACCOUNT_CLASSIFICATION_PERSONAL);
        cardAccount.setAcctType(CardAccount.ACCT_TYPE_CREDIT);
        cardAccount.setAttribute(ACCOUNT_ID, "dummy-account");
        cardAccount.setAvailableCredit("100.00", DUMMY_CARD_ACCOUNT_CURRENCY);
        cardAccount.setRunningBalance("100.00", DUMMY_CARD_ACCOUNT_CURRENCY);
        cardAccount.setTotalCreditLine("1000.00", DUMMY_CARD_ACCOUNT_CURRENCY);
        cardAccount.setAccountOpenDate(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD, "2024-01-01");
        cardAccount.setMinPayment("100.00", DUMMY_CARD_ACCOUNT_CURRENCY);
        cardAccount.setAmountDue("100.00", DUMMY_CARD_ACCOUNT_CURRENCY);

        SimpleDateFormat sdf = new SimpleDateFormat(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 10);
        String dueDate = sdf.format(c.getTime());
        cardAccount.setDueDate(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD, dueDate);
        cardAccount.setCashApr("0.5");
        cardAccount.setCashAPRType("CASH_ADVANCE");
        cardAccount.setApr("0.5");
        cardAccount.setAPRType("PURCHASE");

        cardAccount.setSourceProductName("Mastercard");
        cardAccount.setSourceIsOwned(true);
        cardAccount.setSourceAccountOwnership("UNKNOWN");

        if(cardAccount.getOrganizationProfile()==null) {
            setOrgProfile(cardAccount);
        }

        return cardAccount;
    }


    private Container setLoanDetails(HashMap<String, String> account) throws Exception{

        YDataLogger.out("2^^^^^^^Coming in here to setCard account detials+++++++++++++++++++++++++++");

        String accountid = account.get(ACCOUNT_ID);
        String accountName = account.get(ACCOUNT_NAME);
        String accountType = account.get(ACCOUNT_TYPE);
        String accountNumber = account.get(MASKED_NUMBER);
        String status = account.get(OPEN_STATUS);
        String accountNickName = account.get(ACCOUNT_NICK_NAME);
        String productName = account.get(PRODUCT_NAME);

        Loan loanAccount = new Loan();


        if (!ScriptUtil.isNullValue(accountNickName)) {
            loanAccount.setAccountNicknameAtSrcSite(accountNickName);
        }

        if (!ScriptUtil.isNullValue(accountNumber)) {
            loanAccount.setAccountNumber(accountNumber);
        } else {
            throw new GeneralException("loan Account number is NULL");
        }

        if(!ScriptUtil.isNullValue(accountName)) {
            loanAccount.setAccountName(accountName);
        }

        if (!ScriptUtil.isNullValue(ACCOUNT_ID)) {
            loanAccount.setAttribute(ACCOUNT_ID, accountid);
        }

        if (!ScriptUtil.isNullValue(accountType)) {
            if (accountType.equalsIgnoreCase("PERS_LOANS")) {
                loanAccount.setLoanType(Loan.LOAN_TYPE_PERSONAL);
            }
        }

        if(!ScriptUtil.isNullValue(status)) {
            YDataLogger.out("Inside setting status");
            if(status.equalsIgnoreCase("OPEN"))
                loanAccount.setAccountStatus(BankAccount.ACCOUNT_STATUS_ACTIVE);
            else if(status.equalsIgnoreCase("CLOSED"))
                loanAccount.setAccountStatus(BankAccount.ACCOUNT_STATUS_CLOSED);
        }

        loanAccount.setPrincipalBalance("0.0");

        return loanAccount;
    }

    private Container setBankDetails(HashMap<String, String> account) throws Exception {

        BankAccount bankAccount = new BankAccount();

        String accountid = account.get(ACCOUNT_ID);
        String accountName = account.get(ACCOUNT_NAME);
        String accountType = account.get(ACCOUNT_TYPE);
        String accountNumber = account.get(MASKED_NUMBER);
        String status = account.get(OPEN_STATUS);
        String accountNickName = account.get(ACCOUNT_NICK_NAME);
        String productName = account.get(PRODUCT_NAME);
        String isOwned = account.get(IS_OWNED);
        String accountOwnership = account.get(ACCOUNT_OWNERSHIP);

        //Hardcoding the FAN for IAV testing
        String fullAccountNumber="100264752";
        String routingNumber="032-000";

        if (!ScriptUtil.isNullValue(accountName)) {
            bankAccount.setAccountName(accountName);
        } else {
            throw new GeneralException("Account name is coming Null");
        }


        if(!ScriptUtil.isNullValue(accountNumber)) {
            bankAccount.setAccountNumber(accountNumber);
        } else {
            throw new GeneralException("Account number is coming Null");
        }

        if(!ScriptUtil.isNullValue(status)) {
            YDataLogger.out("Inside setting status");
            if(status.equalsIgnoreCase("OPEN"))
                bankAccount.setAccountStatus(BankAccount.ACCOUNT_STATUS_ACTIVE);
            else if(status.equalsIgnoreCase("CLOSED"))
                bankAccount.setAccountStatus(BankAccount.ACCOUNT_STATUS_CLOSED);
        }

        if (!ScriptUtil.isNullValue(fullAccountNumber) && FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_FULL_ACCT_NUMBER_PAYMENT_ACCT_NUMBER, bankAccount)) {
            bankAccount.setPaymentAccountNumber(fullAccountNumber);
        }

        if (!ScriptUtil.isNullValue(routingNumber) && FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_BANK_TRANSFER_CODE,bankAccount)) {
            bankAccount.setRoutingNumber(routingNumber);
        }

        if (!ScriptUtil.isNullValue(accountNickName))
            bankAccount.setAccountNicknameAtSrcSite(accountNickName);

        if (!ScriptUtil.isNullValue(accountType)) {
            if (accountType.equalsIgnoreCase("TRANS_AND_SAVINGS_ACCOUNTS")) {
                bankAccount.setAcctType(BankAccount.ACCT_TYPE_SAVINGS);
            } else if(accountType.toUpperCase().contains("TERM_DEPOSIT")){
                bankAccount.setAcctType(BankAccount.ACCT_TYPE_CD);
            }else {
                throw new GeneralException("New Account Type > " + accountType);
            }
        }

        if (!ScriptUtil.isNullValue(ACCOUNT_ID)) {
            bankAccount.setAttribute(ACCOUNT_ID, accountid);
        }

        //Hardcoding the values for CDR Sandbox testing, since we do not have balance and details calls
        String currency = "AUD";
        String available = "0.0";
        String current = "0.0";

        if (ScriptUtil.isNullValue(available) && ScriptUtil.isNullValue(current)) {
            YDataLogger.out("2^^^^^^^^Both the mandatory balances are null....");
            throw new GeneralException("Both Available and Current balace are null");
        }

        if ((!ScriptUtil.isNullValue(available) || !ScriptUtil.isNullValue(current)) && !ScriptUtil.isNullValue(currency)) {
            if (!ScriptUtil.isNullValue(available)) {
                bankAccount.setAvailableBalance(available, currency);
            }
            if (!ScriptUtil.isNullValue(current)) {
                bankAccount.setCurrentBalance(current, currency);
            }
        } else {
            throw new GeneralException("Amount or Currency is Null");
        }

        if(!ScriptUtil.isNullValue(productName)) {
            YProdDataLogger.out("setting actual SourceProductName");
            bankAccount.setSourceProductName(productName);
        } else {
            YProdDataLogger.out("setting dummy SourceProductName");
            bankAccount.setSourceProductName("Everyday Savings");
        }

        if(!ScriptUtil.isNullValue(isOwned) && isOwned.equalsIgnoreCase("false")) {
            YProdDataLogger.out("setting actual SourceIsOwned");
            bankAccount.setSourceIsOwned(false);
        } else {
            YProdDataLogger.out("setting dummy SourceIsOwned");
            bankAccount.setSourceIsOwned(true);
        }

        if(!ScriptUtil.isNullValue(accountOwnership)) {
            YProdDataLogger.out("setting actual SourceAccountOwnership");
            bankAccount.setSourceAccountOwnership(accountOwnership);
        } else {
            YProdDataLogger.out("setting dummy SourceAccountOwnership");
            bankAccount.setSourceAccountOwnership("UNKNOWN");
        }

        if(bankAccount.getOrganizationProfile()==null) {
            setOrgProfile(bankAccount);
        }

        return bankAccount;

    }

    /**
     * Method to parse the account level JSON response
     *
     * @param accountResponse
     * @return
     * @throws Exception
     */

    public List<HashMap<String, String>> parseAccountResponse(String accountResponse) throws Exception {

        JSONObject accountJSONObj = new JSONObject(accountResponse);
        List<HashMap<String, String>> accountList = new ArrayList<HashMap<String, String>>();

        JSONObject dataObj = accountJSONObj.getJSONObject(DATA_OBJ_NODE);
        JSONArray accountArray = dataObj.getJSONArray(ACCOUNT_OBJ_NODE);

        for (int i = 0; i < accountArray.length(); i++) {

            JSONObject accountObj = accountArray.getJSONObject(i);

            String accountId = accountObj.getString(ACCOUNT_ID);
            String accountType = accountObj.getString(ACCOUNT_TYPE);
            String accountName = accountObj.getString(ACCOUNT_NAME);
            String accountNumber = accountObj.getString(MASKED_NUMBER);
            String status = accountObj.optString(OPEN_STATUS);
            String accountNickName = accountObj.optString(ACCOUNT_NICK_NAME);
            String productName = accountObj.optString(PRODUCT_NAME);
            String isOwned = accountObj.optString(IS_OWNED);
            String accountOwnership = accountObj.optString(ACCOUNT_OWNERSHIP);

            YDataLogger.out("< Account ID > " + accountId + "< Account Type > " + accountType);
            YProdDataLogger.out("< Account ID > " + accountId + "< Account Type > " + accountType);

            HashMap<String, String> accountMap = new HashMap<String, String>();

            accountMap.put(ACCOUNT_ID, accountId);
            accountMap.put(ACCOUNT_TYPE, accountType);
            accountMap.put(ACCOUNT_NAME, accountName);
            accountMap.put(MASKED_NUMBER, accountNumber);
            accountMap.put(OPEN_STATUS, status);
            accountMap.put(ACCOUNT_NICK_NAME, accountNickName);
            accountMap.put(PRODUCT_NAME, productName);
            accountMap.put(IS_OWNED, isOwned);
            accountMap.put(ACCOUNT_OWNERSHIP, accountOwnership);

            accountList.add(accountMap);
        }

        return accountList;
    }

    /**
     * Method to parse the account detail level JSON response
     *
     * @param accountDetailResponse
     * @return
     * @throws JSONException
     */
    public void parseAccountDetailResponse(String accountDetailResponse, HashMap<String, String> account) throws Exception {

        JSONObject accountDetailJSONObj = new JSONObject(accountDetailResponse);
        JSONObject accountDetailObj = accountDetailJSONObj.getJSONObject(DATA_OBJ_NODE);

        String accountId = accountDetailObj.getString(ACCOUNT_ID);
        String fullAccountNumber = accountDetailObj.optString(ACCOUNT_NUMBER);
        String routingNumber = accountDetailObj.optString(ROUTING_NUMBER);

        YDataLogger.out("< Account ID > " + accountId + "< Account Number > " + fullAccountNumber +
                "< Routing Number > " + routingNumber);

        account.put(ACCOUNT_NUMBER, fullAccountNumber);
        account.put(ROUTING_NUMBER, routingNumber);

        if(accountDetailObj.has(TERM_DEPOSIT)) {
            JSONObject termDepositObj = accountDetailObj.getJSONObject(TERM_DEPOSIT);

            String maturityDate = termDepositObj.getString(CD_MATURITY_DATE);
            String maturityInstruction = termDepositObj.getString(MATURITY_INSTRUCTION);
            String maturityAmount = termDepositObj.optString(MATURITY_AMOUNT);

            account.put(CD_MATURITY_DATE, maturityDate);
            account.put(MATURITY_INSTRUCTION, maturityInstruction);
            account.put(MATURITY_AMOUNT, maturityAmount);
        }

        else if(accountDetailObj.has(CREDIT_CARD)) {
            JSONObject creditCardObj = accountDetailObj.getJSONObject(CREDIT_CARD);

            String minPaymentAmount = creditCardObj.getString(CARD_MIN_PAYMENT);
            String paymentDueDate = creditCardObj.getString(CARD_DUE_DATE);
            String paymentDueAmount = creditCardObj.getString(AMOUNT_DUE);

            account.put(CARD_MIN_PAYMENT, minPaymentAmount);
            account.put(CARD_DUE_DATE, paymentDueDate);
            account.put(AMOUNT_DUE, paymentDueAmount);
        }

        else if(accountDetailObj.has(LOAN)) {
            JSONObject loanObj = accountDetailObj.getJSONObject(LOAN);

            String nextInstalmentDate = loanObj.getString(LOAN_DUE_DATE);
            String loanEndDate = loanObj.getString(LOAN_MATURITY_DATE);
            String originalStartDate = loanObj.optString(ORIGINATION_DATE);
            String originalLoanAmount = loanObj.optString(ORIGINAL_LOAN_AMOUNT);
            String minInstalmentAmount = loanObj.optString(LOAN_MIN_PAYMENT);


            account.put(LOAN_DUE_DATE, nextInstalmentDate);
            account.put(LOAN_MATURITY_DATE, loanEndDate);
            account.put(ORIGINATION_DATE, originalStartDate);
            account.put(ORIGINAL_LOAN_AMOUNT, originalLoanAmount);
            account.put(LOAN_MIN_PAYMENT, minInstalmentAmount);
        }
    }

    /**
     * Method to parse the balance level JSON response
     *
     * @param balanceResponse
     * @return
     * @throws JSONException
     */
    private void parseBalanceResponse(String balanceResponse, HashMap<String,String> account) throws JSONException {

        JSONObject balanceJSONObj = new JSONObject(balanceResponse);
        JSONObject balanceObj = balanceJSONObj.getJSONObject(DATA_OBJ_NODE);

        String accountId = balanceObj.getString(ACCOUNT_ID);
        String currency = balanceObj.getString(CURRENCY);
        String availableBalance = balanceObj.getString(AVAILABLE_BALANCE);
        String currentBalance = balanceObj.getString(CURRENT_BALANCE);

        YDataLogger.out("2^^^^^^^The values for balanceObject are currency=" + currency + " availableBalance="
                + availableBalance + " accountId=" + accountId);


        account.put(CURRENCY, currency);
        account.put(AVAILABLE_BALANCE, availableBalance);
        account.put(CURRENT_BALANCE, currentBalance);

    }

    @Override
    public boolean isOnLoginPage(IYodRobot pRobot, LoginInfo loginInfo) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getLoginPage(IYodRobot pRobot, LoginInfo loginInfo) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getSupportedVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Document getAutoPopulationDoc() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document getValidationDoc() {
        // TODO Auto-generated method stub

        if (sum_info.contains("34730")) {
            return validationDoc1;
        } else {
            return validationDoc;
        }
    }

    @Override
    protected void handlePreLoadDocumentErrors(String responseData, Exception e, String requestType) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected int isRetriableError(String message, Exception exception) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object generateHttpHeader(String requestType, IYodRobot pRobot, Object request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object generateRequest(String requestType, IYodRobot pRobot, YGathererLogSummary logSummaryObj) {
        // TODO Auto-generated method stub

        return null;
    }

    // @Override
    public int handle_FeedErrors(Object obj, IYodRobot pRobot, String str, int code, String errMessage,
                                 RequestItem item, YGathererLogSummary logSummary) throws Exception {
        YDataLogger.out("--- handle_FeedErrors---" + str);
        YProdDataLogger.out("--- handle_FeedErrors---" + str);

        if (code == 503) {
            throw new OAuthSiteUnavailableException("Service Unavailable");
        } else if (code == 405) {
            throw new GeneralException("Method Not Allowed");
        } else if (code == 415) {
            throw new GeneralException("Unsupported Media Type");
        } else if (code == 406) {
            throw new GeneralException("The accept header Not Acceptable");
        } else if (code == 429) {
            throw new GeneralException(" Too Many Requests");
        } else if ((code == 502 && errMessage.equalsIgnoreCase("Bad Gateway"))
                || (code == 502 && errMessage.equalsIgnoreCase("Proxy Error"))) {
            throw new OAuthSiteUnavailableException("The request not processed by end site due to some error");
        }else if(code==401 && errMessage.contains("Unauthorized")) {
            throw new OAuthAuthorizationRequiredException();

        }

        if (obj == null) {
            throw new CertificateException("connection is null");
        } else if (str.toLowerCase().contains("access denied")
                && str.toLowerCase().contains("you don't have permission to access")) {
            throw new OAuthConsentRevokedException("Access Denied, You don't have permission to access");
        }

        int val = 0;
        JSONObject object = new JSONObject(str);
        if (object.optJSONObject("error") != null) {
            YProdDataLogger.out("--- in if condition one--");
            YDataLogger.out("---object error is not null--" + object.optJSONObject("error"));
            String codeVal = object.getJSONObject("error").optString("statusCode").trim();
            String errorVal = object.getJSONObject("error").optString("message").trim();
            YDataLogger.out("code----:" + codeVal + "--errorVal---:" + errorVal);
            YProdDataLogger.out("code----:" + codeVal + "--errorVal---:" + errorVal);

            if (codeVal.equals("403") && errorVal.toLowerCase().contains("forbidden")) {
                val = 1;
            } else if (codeVal.equals("403") && errorVal.contains("UK.OBIE.Reauthenticate")) {
                throw new OAuthConsentRequiredException("User need to Reauthenticate");
            } else {
                throw new GeneralException("Need to handle new error");
            }

        } else if(object.optString("status").equals("400")
                && object.optString("details").contains("No Active Scenario found")) {
            throw new GeneralException("No Active Scenario found");
        }else if (!ScriptUtil.isNullValue(object.optString("code")) && !object.optString("code").isEmpty()) {
            YProdDataLogger.out("--- in else condition one--");
            String codeVal = object.optString("code");
            YDataLogger.out("--codeval---:" + codeVal);
            YProdDataLogger.out("--codeval---:" + codeVal);

            String errorVal = object.optString("error");
            YDataLogger.out("--error val--:" + errorVal);
            YProdDataLogger.out("--error val--:" + errorVal);

            if (!ScriptUtil.isNullValue(errorVal)) {
                YDataLogger.out("-- error in code element--");
                if (codeVal.equals("401") && errorVal.toLowerCase().equals("token expired")) {
                    val = 0;
                    throw new OAuthConsentExpiredException("Token code expired");
                } else if (codeVal.equals("500")) {
                    val = 0;
                    throw new OAuthSiteUnavailableException("Internal Server Error");
                } else
                    val = 1;
            } else {
                String message = object.optString("Message");
                YDataLogger.out("--message val--:" + message);
                YProdDataLogger.out("--message val--:" + message);
                if (codeVal.equals("500 Internal Server Error")
                        && errorVal.toLowerCase().equals("internal server error")) {
                    val = 0;
                    throw new OAuthSiteUnavailableException("Internal Server Error");
                } else
                    val = 1;
            }
        } else if (!ScriptUtil.isNullValue(object.optString("status")) && !object.optString("status").isEmpty()) {
            YProdDataLogger.out("--- in else condition two--");
            String errorVal = "";
            String codeVal = object.optString("status");
            YDataLogger.out("--status---:" + codeVal);
            YProdDataLogger.out("--status---:" + codeVal);
            if (codeVal.equals("503")) {
                errorVal = object.optString("title").trim();
                YDataLogger.out("--errorVal---:" + errorVal);
                YProdDataLogger.out("--errorVal---:" + errorVal);

                if (errorVal.toLowerCase().equals("down for maintenance")) {
                    val = 0;
                    throw new SiteDownForMaintenanceException("Down for maintenance");
                } else {
                    val = 1;
                }
            }
            else if(codeVal.equals("404"))
            {
                YDataLogger.out("--Throwing SiteApplicationErrorException 404 Not Found---:");
                YProdDataLogger.out("--Throwing SiteApplicationErrorException 404 Not Found---:");
                //Handling based on getTransaction scenario flow
                YProdDataLogger.out("Printing if values"+object.optString("title").contains("The specified resource was not found")
                        + "second" +object.optString("status").equals("404")
                        + "third" + object.optString("details").contains("Cts.ConformanceSuite.Application.Common.Exceptions.NotFoundException"));
                val=2;

            }else {
                errorVal = object.optJSONObject("error").optString("message").trim();
                YDataLogger.out("--errorVal---:" + errorVal);
                YProdDataLogger.out("--errorVal---:" + errorVal);

                if (codeVal.equals("401") && ScriptUtil.isNullValue(errorVal)) {
                    val = 0;
                    throw new OAuthSiteUnavailableException("Some Unexpected Error Occured During the Request");
                } else {
                    val = 1;
                }
            }
        } else if (!ScriptUtil.isNullValue(object.optString("message"))) {
            String errorVal = "";
            errorVal = object.optString("message").trim();
            if (errorVal.contains("Endpoint request timed out")) {
                throw new OAuthSiteRequestTimedOutException("Request timeout");
            }
        } else {
            YProdDataLogger.out("--- in else condition three--");
            String codeVal = object.optString("Code");
            YDataLogger.out("--codeval in else condition---:" + codeVal);
            YProdDataLogger.out("--codeval in else condition---:" + codeVal);

            if (codeVal.contains("500")) {
                String errorVal = object.optString("Message");
                YDataLogger.out("--error val--:" + errorVal);
                YProdDataLogger.out("--error val--:" + errorVal);
                if (errorVal.toLowerCase().equals("internal server error")
                        || errorVal.toLowerCase().contains("unexpected error")) {
                    throw new OAuthSiteUnavailableException("500 Internal Server Error");
                }
                val = 0;
            } else if (codeVal.contains("400")) {
                String errorVal = object.optString("Message");
                YDataLogger.out("--error val--:" + errorVal);
                YProdDataLogger.out("--error val--:" + errorVal);
                if (errorVal.toLowerCase().contains("consent validation failed")) {
                    throw new OAuthConsentRevokedException("consent validation failed");
                } else if (errorVal.toLowerCase().contains("invalid account status")) {
                    throw new OAuthSiteUnavailableException("Invalid Account Status");
                }
                val = 0;
            } else if (codeVal.contains("401 Require Re-authentication for SCA")) // For YBS getting this specific error
            // code
            {
                throw new OAuthConsentRequiredException("Re-authentication required");
            } else if (codeVal.contains("403 Forbidden")) {
                String errorVal = object.optString("Message");
                YDataLogger.out("--error val--:" + errorVal);
                YProdDataLogger.out("--error val--:" + errorVal);
                if (errorVal.contains("Customer needs to Reauthenticate")) {
                    throw new OAuthConsentExpiredException("Customer needs to Reauthenticate");
                }

            } else
                val = 1;
        }
        YProdDataLogger.out("Printing return value"+val);
        return val;
    }

    private void setOrgProfile(FinancialContainer fc) {

        YProdDataLogger.out("In setOrgProfile::");

        OrganizationProfile orgProfile = new OrganizationProfile();

        if(piiData.get(BUSINESS_NAME)!=null && !ScriptUtil.isNullValue((String)piiData.get(BUSINESS_NAME))) {
            YProdDataLogger.out("setting actual BusinessName");
            orgProfile.setBusinessName((String)piiData.get(BUSINESS_NAME));
        } else {
            YProdDataLogger.out("setting dummy BusinessName");
            orgProfile.setBusinessName("ABC Company");
        }

        if(piiData.get(LEGAL_NAME)!=null && !ScriptUtil.isNullValue((String)piiData.get(LEGAL_NAME))) {
            YProdDataLogger.out("setting actual LegalName");
            orgProfile.setLegalName((String)piiData.get(LEGAL_NAME));
        } else {
            YProdDataLogger.out("setting dummy LegalName");
            orgProfile.setLegalName("ABC LLC");
        }

        if(piiData.get(ACN)!=null && !ScriptUtil.isNullValue((String)piiData.get(ACN))) {
            YProdDataLogger.out("setting actual ACN");
            orgProfile.setACN((String)piiData.get(ACN));
        } else {
            YProdDataLogger.out("setting dummy ACN");
            orgProfile.setACN("123456");
        }

        if(piiData.get(ABN)!=null && !ScriptUtil.isNullValue((String)piiData.get(ABN))) {
            YProdDataLogger.out("setting actual ABN");
            orgProfile.setABN((String)piiData.get(ABN));
        } else {
            YProdDataLogger.out("setting dummy ABN");
            orgProfile.setABN("123456");
        }

        fc.setOrganizationProfile(orgProfile);
    }

    @Override
    public void parseResponse(IYodRobot pRobot, String requestType, Document doc, Object obj1, Object obj2)
            throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleIntermediatePage(IYodRobot pRobot) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public IndividualInformation getIndividualInformation(RequestItem item, IYodRobot pRobot) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isOnAccountSummaryPage(IYodRobot pRobot) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLoginComplete(IYodRobot pRobot) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int handleFeedErrors(Document arg0, Object arg1, IYodRobot arg2, String arg3, RequestItem arg4,
                                YGathererLogSummary arg5) throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUserProfile(IYodRobot pRobot, User u) throws Exception
    {
        YDataLogger.out("Inside getUserProfile:::::: "+piiData);
        YProdDataLogger.out("Inside getUserProfile:::::: "+piiData);
        UserProfile profile = getUserProfile();

        String fullName=(String) piiData.get(FULL_NAME);
        String phone=(String) piiData.get(PHONE);
        String mobileNo=(String) piiData.get(MOBILE);
        String email=(String) piiData.get(EMAILID);
        HashMap<String, String> homeAddressType=(HashMap<String, String>) piiData.get(HOME_ADDRESS_TYPE);
        HashMap<String, String> mailAddressType=(HashMap<String, String>) piiData.get(MAIL_ADDRESS_TYPE);
        HashMap<String, String> officeAddressType=(HashMap<String, String>) piiData.get(OFFICE_ADDRESS_TYPE);

        YDataLogger.out("Printing Details"+"fullName"+fullName+"phone"+phone+"mobileNo"+mobileNo+"email"+email);

        if(!ScriptUtil.isNullValue(fullName)) {
            profile.setFullName(fullName);
        }

        if(!ScriptUtil.isNullValue(mobileNo)) {
            profile.setMobilePhone(mobileNo);
        }else {
            profile.setPhone(phone);
        }

        if(!ScriptUtil.isNullValue(email)) {
            profile.setEmail(email);
        }

        if(homeAddressType!=null){
            UserLocation userLocation=new UserLocation();
            UserAddress userAddress = new UserAddress();
            if(!ScriptUtil.isNullValue(homeAddressType.get(ADDRLINE1))) {
                userAddress.setAddress1(homeAddressType.get(ADDRLINE1));
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(ADDRLINE2))) {
                userAddress.setAddress2(homeAddressType.get(ADDRLINE2));
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(ADDRLINE3))) {
                userAddress.setAddress3(homeAddressType.get(ADDRLINE3));
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(CITY))) {
                userAddress.setCity(homeAddressType.get(CITY));
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(STATE))) {
                userAddress.setState(homeAddressType.get(STATE));;
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(COUNTRY))) {
                userAddress.setCountry(homeAddressType.get(COUNTRY));
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(ZIP))) {
                userAddress.setZip(homeAddressType.get(ZIP));
            }
            if(!ScriptUtil.isNullValue(homeAddressType.get(FULL_ADDRESS))) {
                userLocation.setFullAddress(homeAddressType.get(FULL_ADDRESS));
            }

            userLocation.setAddressType(AddressType.HOME);
            YDataLogger.out("Setting home address"+userLocation.getAddressType()+homeAddressType.get(FULL_ADDRESS));
            userLocation.setAddress(userAddress);
            profile.setAddress(userLocation);
        }
        if(mailAddressType!=null){
            UserLocation userLocation=new UserLocation();
            UserAddress userAddress = new UserAddress();

            if(!ScriptUtil.isNullValue(mailAddressType.get(ADDRLINE1))) {
                userAddress.setAddress1(mailAddressType.get(ADDRLINE1));
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(ADDRLINE2))) {
                userAddress.setAddress2(mailAddressType.get(ADDRLINE2));
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(ADDRLINE3))) {
                userAddress.setAddress3(mailAddressType.get(ADDRLINE3));
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(CITY))) {
                userAddress.setCity(mailAddressType.get(CITY));
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(STATE))) {
                userAddress.setState(mailAddressType.get(STATE));;
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(COUNTRY))) {
                userAddress.setCountry(mailAddressType.get(COUNTRY));
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(ZIP))) {
                userAddress.setZip(mailAddressType.get(ZIP));
            }
            if(!ScriptUtil.isNullValue(mailAddressType.get(FULL_ADDRESS))) {
                userLocation.setFullAddress(mailAddressType.get(FULL_ADDRESS));
            }
            userLocation.setAddressType(AddressType.COMMUNICATION);
            YDataLogger.out("Setting mail address"+userLocation.getAddressType()+mailAddressType.get(FULL_ADDRESS));
            userLocation.setAddress(userAddress);
            profile.setAddress(userLocation);
        }

        if(officeAddressType!=null){
            UserLocation userLocation=new UserLocation();
            UserAddress userAddress = new UserAddress();

            if(!ScriptUtil.isNullValue(officeAddressType.get(ADDRLINE1))) {
                userAddress.setAddress1(officeAddressType.get(ADDRLINE1));
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(ADDRLINE2))) {
                userAddress.setAddress2(officeAddressType.get(ADDRLINE2));
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(ADDRLINE3))) {
                userAddress.setAddress3(officeAddressType.get(ADDRLINE3));
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(CITY))) {
                userAddress.setCity(officeAddressType.get(CITY));
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(STATE))) {
                userAddress.setState(officeAddressType.get(STATE));;
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(COUNTRY))) {
                userAddress.setCountry(officeAddressType.get(COUNTRY));
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(ZIP))) {
                userAddress.setZip(officeAddressType.get(ZIP));
            }
            if(!ScriptUtil.isNullValue(officeAddressType.get(FULL_ADDRESS))) {
                userLocation.setFullAddress(officeAddressType.get(FULL_ADDRESS));
            }
            userLocation.setAddressType(AddressType.OFFICE);
            YDataLogger.out("Setting office address"+userLocation.getAddressType()+mailAddressType.get(FULL_ADDRESS));
            userLocation.setAddress(userAddress);
            profile.setAddress(userLocation);

        }
        FullAddressUtil.setFullAddress(getUserProfile());
        return RequestItem.RETURN_CODE_SUCCESS;
    }

    @Override
    protected String postRequestForTextResponse(String requestType, IYodRobot pRobot, YGathererLogSummary logSummaryObj,
                                                String summaryRequest, Object httpHeader) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}




