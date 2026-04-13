import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.TimeoutException;
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
import com.yodlee.dap.gatherer.gather.RequestItem;
import com.yodlee.dap.gatherer.gather.User;
import com.yodlee.dap.gatherer.gather.UserAddress;
import com.yodlee.dap.gatherer.gather.UserLocation;
import com.yodlee.dap.gatherer.gather.UserName;
import com.yodlee.dap.gatherer.gather.UserProfile;
import com.yodlee.dap.gatherer.gather.content.AddressType;
import com.yodlee.dap.gatherer.gather.content.Container;
import com.yodlee.dap.gatherer.gather.content.IndividualInformation;
import com.yodlee.dap.gatherer.gather.exceptions.GeneralException;
import com.yodlee.dap.gatherer.gather.exceptions.OAuthConsentExpiredException;
import com.yodlee.dap.gatherer.gather.exceptions.SiteApplicationErrorException;
import com.yodlee.dap.gatherer.gather.exceptions.SiteUnavailableException;
import com.yodlee.dap.gatherer.sanitizer.YProdDataLogger;
import com.yodlee.dap.gatherer.validationutils.ScriptConstants;
import com.yodlee.dap.gatherer.validationutils.ScriptUtil;
import com.yodlee.dap.gatherer.validationutils.XMLUtil;
import com.yodlee.dap.gatherer.validationutils.YDataLogger;
import com.yodlee.dap.gatherer.ylogger.YGathererLogSummary;
import com.yodlee.dap.gatherer.ylogger.YGathererLogger;
import com.yodlee.dap.gatherer.ylogger.YSystem;

import yodlee.gather.exceptions.OAuthAuthorizationRequiredException;

public abstract class SlashOAuthBase extends XProcessor implements IYodValidator,IYodOAuth {

    public static final String RCSId = "$Id: //gatherer/dap/agents/main/java/JPMCOAuthBase.java#1 $";

    protected static String SERVER_URL;

    protected final static String CLIENT_ID = "clientId";
    protected final static String ID_ATTR = "account_id";
    protected final static String TOKEN = "token";
    protected String sum_info=null;
    protected String domain=null;

    public static HashMap<String, String> uarException = new HashMap<String, String>();
    public static HashMap<String, String> siteException = new HashMap<String, String>();

    public static Document autoPopulationDoc = null;
    public static Document validationDoc = null;
    public static String autoPopulationRule;
    public static String validationRule;

    protected static Map<String, Map<String, String>> fieldDetailsMap = null;
    protected static Map<String, Map<String, String>> apiDetailsMap = null;
    protected static Map<String, Map<String, String>> associationMap = null;
    protected static Map<String, List<Map<String,String>>> manipulationMap = null;
    protected static Map<String, String> additionalDataMap = null;

    public String accountListResponseDocument = "";
    protected static User userDetail;
    protected String MEM_ID="";
    protected int retryAccountLevel =0;


    protected static USOAuthUtil usoauthUtil;

    //Slash cards are charge cards which are paid off daily and do not have most of the fields for cards. Therefore, suppressing validation.
    static {
        validationRule = "<validationRuleList>"
                + "<validationRule type='credits'>"
                +"<validation type='buisnessLogic' name='billDateAndDueDateValidation' on='0' errorLevel='2' isOverridable='0' level='statement' priority='0'>"
                +"<element name='billDate'></element>"
                +"<element name='dueDate'></element>"
                +"</validation>"
                +"<validation type='buisnessLogic' name='minAmountDueEndingBalanceAndAmountDueValidation' on='0' errorLevel='2' isOverridable='0' level='statement' priority='1'>"
                +"<element name='minAmount'></element>"
                +"<element name='endingBalance'></element>"
                +"</validation>"
                + "<validation name ='accountNumberValidation' errorLevel='2'>"
                + "</validation>"
                + "<validation name ='accountDuplicationValidation' errorLevel='2'>"
                + "</validation>"
                + "</validationRule>"
                + "</validationRuleList>";

        try{
            validationDoc=XMLUtil.createDocument(validationRule);
        }
        catch (ParserConfigurationException e) {
            throw new GeneralException("exception occured:"+e.getLocalizedMessage());}
        catch (SAXException e) {
            throw new GeneralException("exception occured:"+e.getLocalizedMessage());}
        catch (IOException e) {
            throw new GeneralException("exception occured:"+e.getLocalizedMessage());
        }catch (Exception e) {
            throw new GeneralException("General exception occured:"+e.getLocalizedMessage());
        }
    }


    @Override
    public Document getValidationDoc() {
        YDataLogger.out("inside val doc "+validationDoc);
        YProdDataLogger.out("inside val doc "+validationDoc);
        return validationDoc;
    }

    @Override
    public Document getAutoPopulationDoc() {

        return autoPopulationDoc;
    }

    @Override
    public int login(LoginInfo loginInfo, User user, ClassArgs classArgs, IYodRobot pRobot) throws Exception {

        YDataLogger.out("login here");
        YProdDataLogger.initialize("47930,47949", Constants.SUM_INFO_ID,
                new SimpleDateFormat(ScriptConstants.DATEFORMAT_DD_SL_MM_SL_YYYY).format(new Date()),
                ScriptConstants.DATEFORMAT_DD_SL_MM_SL_YYYY);

        String access_token = pRobot.doCrypt(user.get(TOKEN));
        containerHelper.put(TOKEN, access_token);
        sum_info=ScriptUtil.getSumInfoId(user);
        YDataLogger.out("csid in login is "+sum_info);
        containerHelper.put("CSID", sum_info);

        domain = ScriptUtil.getDomain(pRobot);
        YDataLogger.out("Domain here is "+domain );
        containerHelper.put("domainURL", "domain");
        fieldDetailsMap = SlashOAuthMapping.initializeDetailsMap();
        apiDetailsMap = SlashOAuthMapping.initializeApiCallsMap();
        associationMap = SlashOAuthMapping.initializeAssociationMap();
        manipulationMap = SlashOAuthMapping.initializeManipulationMap();
        additionalDataMap = SlashOAuthMapping.initializeAdditionalData();
        containerHelper.put("dateFormat", additionalDataMap.get("apiDateFormat"));

        String endSiteResp = pRobot.getDataAccessAttribute(StringConstants.OAUTH_END_SITE_RESPONSE);
        YDataLogger.out("^^^^^The endSiteResp is = " + endSiteResp);
        YProdDataLogger.out("^^^^^The endSiteResp is = " + endSiteResp);
        YDataLogger.out("2^^^access_token length"+access_token.length());
        YProdDataLogger.out("2^^^access_token length"+access_token.length());


        if(!ScriptUtil.isNullValue(endSiteResp))
            OAuthFeedErrors.handleOCCErrors(endSiteResp, pRobot, item);

        usoauthUtil = new USOAuthUtil(autoPopulationDoc, user.getItem(0), YGathererLogger.getLogSummaryObj(), Boolean.valueOf(additionalDataMap.get("isUUIDRequired")), additionalDataMap.get("responseDateFormat"),true);

        return RequestItem.RETURN_CODE_SUCCESS;
    }

    protected boolean isStageRequest() {
        if(YGathererLogger.getLogSummaryObj().getDbId().contains(ScriptConstants.STAGE_DB_IDENTIFIER_NEW) ||
                YGathererLogger.getLogSummaryObj().getDbId().contains(ScriptConstants.STAGE_FIREMEM_DB_IDENTIFIER)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean isOnAccountSummaryPage(IYodRobot pRobot) throws Exception {
        return false;
    }

    @Override
    public boolean isLoginComplete(IYodRobot pRobot) throws Exception {
        return false;
    }

    @Override
    public int logout(IYodRobot robot) throws Exception {
        return RequestItem.RETURN_CODE_SUCCESS;
    }

    @Override
    public IndividualInformation getIndividualInformation(RequestItem item, IYodRobot pRobot) throws Exception {
        // Personal informations are available only if we provide additional information
        return null;
    }

    public List<? extends Container> getAccounts(RequestItem item, IYodRobot pRobot, boolean getAllAccounts,
                                                 boolean isValidationRequired) throws Exception {

        YDataLogger.out("2^^^inside get Accounts"+(String)containerHelper.get(TOKEN).toString());

        String access_token = (String)containerHelper.get(TOKEN).toString();

        YDataLogger.out("access token in accounts list call is"+access_token+access_token.length());
        usoauthUtil.setContainerHelper(containerHelper);
        usoauthUtil.setAllAccountsRequired(getAllAccounts);

        accountListResponseDocument = usoauthUtil.getAdditionalResponse("ACCOUNT_LIST",apiDetailsMap.get("ACCOUNT_LISTS_HEADERS"),
                apiDetailsMap.get("ACCOUNT_LISTS_BODY"), apiDetailsMap.get("ENDPOINTS").get("ACCOUNT_LISTS_ENDPOINT"),
                apiDetailsMap.get("METHODS").get("ACCOUNT_LISTS_METHOD"), null, null, null, null, containerHelper.get("dateFormat").toString(), pRobot, containerHelper.get(TOKEN).toString(),0);


        containerHelper.put("accountList", accountListResponseDocument);
        YDataLogger.out("account List response11----"+accountListResponseDocument);

        //Getting response headers here
        String respheader1= (String) containerHelper.get("Responseheaders");
        YDataLogger.out("----respheader---"+respheader1);

        List<Container> accountList = new ArrayList<Container>();
        Map<String, String> ACCOUNT_TYPE_ID_MAP = null;
        if (fieldDetailsMap.containsKey("ACCOUNT_TYPE_ID_MAP")) {
            ACCOUNT_TYPE_ID_MAP = fieldDetailsMap.get("ACCOUNT_TYPE_ID_MAP");
        }
        YDataLogger.out("---account type map here is----:"+ACCOUNT_TYPE_ID_MAP.toString());

        // PARSE AND SET ACCOUNT LISTS
        accountList = usoauthUtil.parseAccountLevelResponse(fieldDetailsMap.get("ACCOUNT_LISTS_MAP"),
                fieldDetailsMap.get("PRIMARY_KEY_MAP"), accountListResponseDocument, ACCOUNT_TYPE_ID_MAP,
                associationMap,manipulationMap);

        YDataLogger.out("++++The size of account list is ="+accountList.size());
        YDataLogger.out("++++The contents of account list is ="+accountList.toString());


        // GET ACCOUNT DETAILS

        if (apiDetailsMap.get("ENDPOINTS").containsKey("ACCOUNT_DETAILS_ENDPOINT")) {
            String accountDetailsResponse;
            YDataLogger.out("--- inside accnt details ---");

            // LOOPING THROUGH ACCOUNTS
            for (Container container : accountList) {

                // OBTAIN ACCOUNT DETAILS
                accountDetailsResponse = usoauthUtil.getAdditionalResponse("ACCOUNT_DETAILS",apiDetailsMap.get("ACCOUNT_DETAILS_HEADERS"),
                        apiDetailsMap.get("ACCOUNT_DETAILS_BODY"),
                        apiDetailsMap.get("ENDPOINTS").get("ACCOUNT_DETAILS_ENDPOINT"),
                        apiDetailsMap.get("METHODS").get("ACCOUNT_DETAILS_METHOD"), container.getAttribute(ID_ATTR),
                        null, null, null, containerHelper.get("dateFormat").toString(), pRobot, containerHelper.get(TOKEN).toString(),0);

                //PUT IN CONTAINER HELPER
                //containerHelper.put(container.getAttribute(ID_ATTR) + "_accountDetails", accountDetailsResponse);

                Map<String, String> accountDetailsMap = null;

                YDataLogger.out("Container here is "+container.toString());
                // CHECK CONTAINER
                if (container.getType().equalsIgnoreCase("cardaccount")&& containerHelper.isSupportedContainer(TAG_CREDITS, getAllAccounts)) {
                    YDataLogger.out("container.getType().equalsIgnoreCase(\"cardaccount\")" + container.getType().equalsIgnoreCase("cardaccount")+container.toString());
                    YDataLogger.out("containerHelper.isSupportedContainer(TAG_CREDITS, getAllAccounts)" + containerHelper.isSupportedContainer(TAG_CREDITS, getAllAccounts));
                    if (fieldDetailsMap.containsKey("CREDITS_ACCOUNT_DETAILS_MAP")) {
                        accountDetailsMap = fieldDetailsMap.get("CREDITS_ACCOUNT_DETAILS_MAP");
                    }
                } else if (container.getType().equalsIgnoreCase("bankaccount")&& containerHelper.isSupportedContainer(TAG_BANK, getAllAccounts)) {
                    YDataLogger.out("container.getType().equalsIgnoreCase(\"bankaccount\")" + container.getType().equalsIgnoreCase("bankaccount")+container.toString());
                    YDataLogger.out("containerHelper.isSupportedContainer(TAG_BANK, getAllAccounts)" + containerHelper.isSupportedContainer(TAG_BANK, getAllAccounts));
                    if (fieldDetailsMap.containsKey("BANK_ACCOUNT_DETAILS_MAP")) {
                        accountDetailsMap = fieldDetailsMap.get("BANK_ACCOUNT_DETAILS_MAP");
                    }
                } else if (container.getType().equalsIgnoreCase("loan")&& containerHelper.isSupportedContainer(TAG_LOAN, getAllAccounts)) {
                    YDataLogger.out("container.getType().equalsIgnoreCase(\"loan\"" + container.getType().equalsIgnoreCase("loan"));
                    YDataLogger.out("containerHelper.isSupportedContainer(TAG_LOAN, getAllAccounts)" + containerHelper.isSupportedContainer(TAG_LOAN, getAllAccounts)+container.toString());
                    if (fieldDetailsMap.containsKey("LOANS_MORTGAGE_ACCOUNT_DETAILS_MAP")) {
                        accountDetailsMap = fieldDetailsMap.get("LOANS_MORTGAGE_ACCOUNT_DETAILS_MAP");
                    }
                }
                else if (container.getType().equalsIgnoreCase("investmentaccount")&& containerHelper.isSupportedContainer(TAG_INVESTMENT, getAllAccounts)) {
                    YDataLogger.out("container.getType().equalsIgnoreCase(\"investmentaccount\")" + container.getType().equalsIgnoreCase("investmentaccount")+container.toString());
                    YDataLogger.out("containerHelper.isSupportedContainer(TAG_INVESTMENT, getAllAccounts)" + containerHelper.isSupportedContainer(TAG_INVESTMENT, getAllAccounts));
                    if (fieldDetailsMap.containsKey("STOCKS_ACCOUNT_DETAILS_MAP")) {
                        accountDetailsMap = fieldDetailsMap.get("STOCKS_ACCOUNT_DETAILS_MAP");
                    }
                } else {
                    throw new GeneralException("Unsupported container");
                }

                // PARSE AND SET ACCOUNT DETAILS
                if (accountDetailsMap != null)
                    usoauthUtil.parseAccountDetailsResponse(accountDetailsMap, accountDetailsResponse, container,
                            associationMap,manipulationMap);
            }
        }

        YDataLogger.out("2^^^^^The containerlist is=" + accountList +accountList.size());
        YProdDataLogger.out("2^^^^^The size of the containerlist is=" + accountList.size());
        return accountList;
    }

    protected String postRequestLocal(String requestType, IYodRobot pRobot,
                                      YGathererLogSummary logSummaryObj, Object httpHeader) throws Exception {

        YDataLogger.out("--------Inside Post Request Local----");

        HttpsURLConnection urlConnection  = null;
        String contents  = null;

        YDataLogger.out("check1 connection ::: Server URL set " + System.currentTimeMillis());
        try {
            URL feedURL = new URL(SERVER_URL);
            urlConnection = (HttpsURLConnection)feedURL.openConnection();
            urlConnection.setConnectTimeout(120000);

        } catch (SocketTimeoutException ste) {
            throw new TimeoutException("Connection timed out after 120 seconds");
        } catch (IOException e) {
            YSystem.printStackTrace(e);
            throw new GeneralException("IOException while opening connection to feed server url");
        }
        try{
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            HashMap<String, String> headers = (HashMap<String, String>) httpHeader;

            for (Iterator<Map.Entry<String, String>> headerIter =
                 headers.entrySet().iterator(); headerIter.hasNext();) {
                Map.Entry<String, String> entry = headerIter.next();
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());

                YDataLogger.out("printing request headers::"+entry.getKey()+ entry.getValue());
            }


            int i =0;
            InputStreamReader reader = null;
            try {

                YDataLogger.out("2^^^^^^^^^printing connnrequest ::" + urlConnection.toString());
                YDataLogger.out("2^^^^^^^^^printing urlConnection.getResponseCode():::: "
                        + urlConnection.getResponseCode() + "2^^^^^^^^^printing urlConnection.getResponseMessage()::: "
                        + urlConnection.getResponseMessage());
                YDataLogger.out("2^^^^^^^^^^^Request Type is: " + requestType);
                YDataLogger.out("2^^^^^^^^^^Headers are : " + urlConnection.getHeaderFields());
                YDataLogger.out("2^^^^^ printing response contents::::" + contents);

                Map<String, List<String>> headers1 = (Map<String, List<String>>)urlConnection.getHeaderFields();
                YDataLogger.out("--- headers values---::"+headers1.toString());
                for(Iterator<Entry<String, List<String>>> headerIter = headers1.entrySet().iterator();headerIter.hasNext();)
                {
                    YDataLogger.out("--inside interator---");
                    Entry<String, List<String>> entry = headerIter.next();
                    YDataLogger.out(" header keys and entries--"+entry);
                    YDataLogger.out(" header keys --"+entry.getKey());
                    YDataLogger.out(" header value--"+entry.getValue());

                    if(!ScriptUtil.isNullValue(entry.getKey())
                            && entry.getKey().contains("X-CorrelationID")) {
                        i++;

                        YDataLogger.out("Request is " + requestType);
                        YDataLogger.out("The Valueof i is " + i);

                        String ids = pRobot.getInteractionIds();

                        YProdDataLogger.out("------X-CorrelationID------:"+entry.getValue());
                        if(!ids.contains(entry.getValue().toString()))
                        {
                            pRobot.setInteractionIds(requestType+Integer.toString(i), entry.getValue().toString());
                        }
                        else
                        {
                            throw new GeneralException("This ID has already been used"+entry.getValue());
                        }
                    }
                }

                if(urlConnection.getResponseCode() == 200){
                    reader = new InputStreamReader(urlConnection.getInputStream());
                }

                else if(urlConnection.getResponseCode() == 500){
                    throw new SiteApplicationErrorException("Internal Server Error::::"+ "urlConnection.getResponseCode()::: "+urlConnection.getResponseCode() +
                            "urlConnection.getResponseMessage()::: "+urlConnection.getResponseMessage());
                } else if(urlConnection.getResponseCode() == 401){
                    throw new SiteApplicationErrorException("Unauthorized Error::::"+ "urlConnection.getResponseCode()::: "+urlConnection.getResponseCode() +
                            "urlConnection.getResponseMessage()::: "+urlConnection.getResponseMessage());
                }else if(urlConnection.getResponseCode() == 502){
                    throw new SiteApplicationErrorException("Bad Gateway::::"+ "urlConnection.getResponseCode()::: "+urlConnection.getResponseCode());
                }
                else if(urlConnection.getResponseCode() == 503){
                    throw new SiteUnavailableException("urlConnection.getResponseCode()::: "+urlConnection.getResponseCode() +
                            "urlConnection.getResponseMessage()::: "+urlConnection.getResponseMessage());
                }
                else {
                    YProdDataLogger.out("Printing response code request ++++++++++"+urlConnection.getResponseCode());
                    YProdDataLogger.out("Printing response message request ++++++++++"+urlConnection.getResponseMessage());
                    reader = new InputStreamReader(urlConnection.getErrorStream());
                }
            }catch(ConnectException ce) {
                YSystem.printStackTrace(ce);
                throw new SiteApplicationErrorException("After retry, Could not connect due to ConnectException");
            }catch(SSLHandshakeException ex){
                YSystem.printStackTrace(ex);
                throw new SiteApplicationErrorException("Could not connect due to SSLHandshakeException");
            }catch(SSLException ssle){
                YSystem.printStackTrace(ssle);
                throw new SiteApplicationErrorException("Could not connect due to SSLException");
            }catch(SocketException se) {
                YSystem.printStackTrace(se);
                throw new SiteApplicationErrorException("After retry, Could not connect due to SocketException");
            }catch(SocketTimeoutException st) {
                YSystem.printStackTrace(st);
                throw new SiteApplicationErrorException("After retry, Could not connect due to Interrupted I/O - SocketTimeoutException");
            }catch(UnknownHostException uhe){
                YSystem.printStackTrace(uhe);
                throw new SiteApplicationErrorException("Could not connect due to UnknownHostException");
            }catch (Exception e) {
                YSystem.printStackTrace(e);
                if ( reader!= null && (e instanceof IOException)) {
                    reader.close();
                }
                throw e;
            }

            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[ 2048 ];
            int num;

            while (-1 != (num = reader.read(cbuf))) {
                buf.append( cbuf, 0, num );
            }
            contents = buf.toString();

            YDataLogger.out("check6 contents ::: " + System.currentTimeMillis());

            reader.close();
        } catch (GeneralException genEx) {
            throw new GeneralException(genEx.getMessage());
        } catch (Exception e) {
            throw e;
        } finally {
            urlConnection.disconnect();
        }

        YDataLogger.out("2^^^^^ printing response contents::::" + contents);

        if(urlConnection.getResponseCode()!=200){

            YProdDataLogger.out("urlConnection.getResponseCode()::::"+urlConnection.getResponseCode() +
                    "urlConnection.getResponseMessage()::: "+urlConnection.getResponseMessage()+
                    "Printing content response::::"+contents);
        }

        YDataLogger.out("--------Exiting Post Request Local----");

        return contents;
    }

    public Object generateHttpHeader(String requestType, IYodRobot pRobot){
        HashMap<String, String> headers = new HashMap<String, String>();
        YDataLogger.out("-----request type in GenerateHttpHeaders-----"+requestType);
        headers.put("Authorization", "Bearer "+ containerHelper.get(TOKEN).toString());
        if(requestType.equals("STATEMENTS")) {
            headers.put("Accept", "application/pdf");
        }
        YDataLogger.out("headers for "+requestType+"are "+headers.toString());
        return headers;
    }

    public void getPreConnectionInfo(Map<String, String> preConnectionDetails) {
        preConnectionDetails.put(StringConstants.TWO_WAY_SSL_REQUIRED, "false");
        preConnectionDetails.put(StringConstants.TRANSPORT_CERTIFICATE_REQUIRED, "false");
    }


}
