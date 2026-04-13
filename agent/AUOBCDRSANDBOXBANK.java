
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.yodlee.dap.gatherer.gather.ClassArgs;
import com.yodlee.dap.gatherer.gather.Constants;
import com.yodlee.dap.gatherer.gather.IYodAccountDetail;
import com.yodlee.dap.gatherer.gather.IYodRobot;
import com.yodlee.dap.gatherer.gather.RequestItem;
import com.yodlee.dap.gatherer.gather.content.Address;
import com.yodlee.dap.gatherer.gather.content.AddressType;
import com.yodlee.dap.gatherer.gather.content.BankAccount;
import com.yodlee.dap.gatherer.gather.content.BankTransaction;
import com.yodlee.dap.gatherer.gather.content.FinancialContainer;
import com.yodlee.dap.gatherer.gather.content.IndividualInformation;
import com.yodlee.dap.gatherer.gather.content.Location;
import com.yodlee.dap.gatherer.gather.content.Site;
import com.yodlee.dap.gatherer.gather.content.OrganizationProfile;
import com.yodlee.dap.gatherer.gather.exceptions.GeneralException;
import com.yodlee.dap.gatherer.gather.exceptions.NoAccountsFoundException;
import com.yodlee.dap.gatherer.sanitizer.YProdDataLogger;
import com.yodlee.dap.gatherer.validationutils.BankUtil;
import com.yodlee.dap.gatherer.validationutils.FinancialContainerUtil;
import com.yodlee.dap.gatherer.validationutils.FinancialContainerUtil.AttributeType;
import com.yodlee.dap.gatherer.validationutils.IYodBankScript;
import com.yodlee.dap.gatherer.validationutils.ScriptConstants;
import com.yodlee.dap.gatherer.validationutils.ScriptUtil;
import com.yodlee.dap.gatherer.validationutils.YDataLogger;
import com.yodlee.dap.gatherer.ylogger.YGathererLogSummary;
import com.yodlee.dap.gatherer.ylogger.YGathererLogger;

public class AUOBCDRSandboxBank extends AUOBCDRSandboxBase implements IYodAccountDetail,IYodBankScript {
    /*
     * RCSId to identify the version
     */
    public static String RCSId = "$Id: //gatherer/dap/agents/main/java/AUOBCDRSandboxBank.java#10 $";

    boolean fanFlag=false;
    boolean rtnFlag=false;

    /**
     * Execute method to fetch the Account Details including transactions
     *
     * @param item
     * @param classArgs
     * @param pRobot
     * @return
     * @throws Exception
     */
    @Override
    public int execute(RequestItem item, ClassArgs classArgs, IYodRobot pRobot) throws Exception {

        DateFormat dateformat = new SimpleDateFormat(ScriptConstants.DATEFORMAT_DD_HYP_MM_HYP_YYYY);
        Date date = new Date();
        YProdDataLogger.initialize(MEM_ID, Constants.MEM_ID, dateformat.format(date), ScriptConstants.DATEFORMAT_DD_HYP_MM_HYP_YYYY);

        YProdDataLogger.out("In execute method::");


        Site site = containerHelper.getSiteObject(item);
        List<BankAccount> bankAccountList = site.getChildren().subList(0, site.getChildren().size());

        Date startDate = BankUtil.getStartDate(item, YGathererLogger.getLogSummaryObj());
        Date endDate = BankUtil.getEndDate(item, YGathererLogger.getLogSummaryObj());

        for (BankAccount bankAccount : bankAccountList) {

            YDataLogger.out("2^^^^ user profile " + FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_HOLDER_DETAILS, bankAccount));
            YDataLogger.out(String.valueOf(FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_HOLDER_NAME_SINGLE, bankAccount)));
            YDataLogger.out(String.valueOf(FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_HOLDER_NAME_MULTIPLE, bankAccount)));

            if(FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_HOLDER_NAME_SINGLE, bankAccount) ||
                    FinancialContainerUtil.isAttributeRequired(item, AttributeType.ACCT_PROFILE_HOLDER_NAME_MULTIPLE, bankAccount)) {
                YDataLogger.out("Getting accountHolder for bank");
                if(ScriptUtil.isNullValue(accountHolder)) {
                    YDataLogger.out("calling account holder method from bank");
                    getAccountHolder(pRobot);
                }
                if(!ScriptUtil.isNullValue(accountHolder)) {
                    YDataLogger.out("setting account holder method from bank");
                    IndividualInformation individualInformation=new IndividualInformation();
                    individualInformation.setfullName(accountHolder);
                    bankAccount.setIndividualInformation(individualInformation);
                }
            }

            YProdDataLogger.out("attribute check::"+FinancialContainerUtil.isAttributeRequired(item, AttributeType.BASIC_AGG_DATA_TRANSACTIONS,bankAccount));
            if (FinancialContainerUtil.isAttributeRequired(item, AttributeType.BASIC_AGG_DATA_TRANSACTIONS,bankAccount)) {
                getAccountTransactions(item, pRobot, bankAccount, startDate, endDate, isValidationRequired);
            }

            //bankAccount.removeAttribute(ACCOUNT_ID);
        }
        YDataLogger.out("Site XML > " + site.asXML(true));

        return RequestItem.RETURN_CODE_SUCCESS;
    }

    /**
     * Method to fetch all the accounts that might have missed during the Account
     * Summary In this case, we do not have any additional information through this
     * method
     *
     * @param pRobot
     * @param item
     * @throws Exception
     */
    @Override
    public void callSharedFunction(IYodRobot pRobot, RequestItem item) throws Exception {
        YDataLogger.out("Call shared Function >");
        getBankAccounts(item, pRobot, isValidationRequired);
    }


    /**
     * Method to get all the Bank accounts information
     *
     * @param item
     * @param pRobot
     * @param val
     * @return
     * @throws Exception
     */
    public List<BankAccount> getBankAccounts(RequestItem item, IYodRobot pRobot, boolean val) throws Exception {
        YDataLogger.out("Get Bank Accounts >");
        List<BankAccount> bankAccountList = (List<BankAccount>) containerHelper.getAccountsForContainer(item, this,pRobot, isValidationRequired);
        YDataLogger.out(" Bank Account List size > " + bankAccountList.size());

        if (bankAccountList == null || bankAccountList.isEmpty()) {
            throw new NoAccountsFoundException("No Bank Accounts found >");
        }

        return bankAccountList;
    }

    /**
     * Method to fetch the transaction for all the accounts
     *
     * @param item
     * @param pRobot
     * @param bankAccount
     * @param startDate
     * @param endDate
     * @param isValidationRequired
     * @throws Exception
     */
    public void getAccountTransactions(RequestItem item, IYodRobot pRobot, BankAccount bankAccount, Date startDate,
                                       Date endDate, boolean isValidationRequired) throws Exception {

        YProdDataLogger.out("Inside getAccountTransactions::");

        startDate = formatTime(startDate, true);
        endDate = formatTime(endDate, false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        String startDateTime = sdf.format(startDate);
        String endDateTime = sdf.format(endDate);

        String serverUrl = getServerURL(pRobot, ACCOUNT_TRANSACTION_REQUEST, bankAccount.get(ACCOUNT_ID), startDateTime, endDateTime);

        String transactionDetails = postReqAndGetContents(pRobot, serverUrl, ACCOUNT_TRANSACTION_REQUEST);

        YProdDataLogger.out("Transaction Response Start - ");
        YProdDataLogger.out(transactionDetails);
        YProdDataLogger.out("Transaction Response End - ");

        List<HashMap<String, String>> transactionList = parseTransactionlevelResponse(transactionDetails);

        setAccountTransactions(bankAccount, transactionList);
    }

    /**
     * Method to add time(Hours, Minutes, Seconds) as per the defined spec
     *
     * @param date
     * @param value
     * @return
     */
    public Date formatTime(Date date, boolean value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if (value) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        }

        return cal.getTime();
    }

    /**
     * Method to parse the transaction level JSON response
     *
     * @param transactionResponse
     * @return
     * @throws Exception
     */
    public List<HashMap<String, String>> parseTransactionlevelResponse(String transactionResponse) throws Exception {

        List<HashMap<String, String>> transactionList = new ArrayList<>();
        String nextURLFirst = "";
        do {

            JSONObject transactionObj = new JSONObject(transactionResponse);

            JSONObject dataObj = transactionObj.getJSONObject(DATA_OBJ_NODE);
            JSONArray transactionArray = dataObj.getJSONArray(TRANSACTION_OBJ_NODE);

            JSONObject totalPageObj = transactionObj.optJSONObject("meta");

            JSONObject linkObj=transactionObj.optJSONObject("links");

            YDataLogger.out("2^^^^^The link value is="+linkObj);
            YDataLogger.out("In here to get the next navigation URL");

            int transArrayLength = transactionArray.length();

            for (int i = 0; i < transArrayLength; i++) {
                HashMap<String, String> transactionMap = new HashMap<String, String>();

                JSONObject transDataObj = transactionArray.getJSONObject(i);

                String accountId = transDataObj.getString(ACCOUNT_ID);
                YDataLogger.out("AccountID is::" + accountId);

                String transactionId = transDataObj.optString(TRANSACTION_ID);
                YDataLogger.out("TransactionID is::" + transactionId);

                String transactionType = transDataObj.getString(TRANSACTION_TYPE);
                YDataLogger.out("Transaction Type is::" + transactionType);

                String transactionAmount = transDataObj.optString(AMOUNT);
                YDataLogger.out("Amount is::" + transactionAmount);

                String currency = transDataObj.optString(CURRENCY);
                YDataLogger.out("Currency is::" + currency);

                String transactionStatus = transDataObj.getString(STATUS);
                YDataLogger.out("Status is::" + transactionStatus);

                String bookingDateTime = transDataObj.optString(POST_DATE);
                YDataLogger.out("BookingDateTime is::" + bookingDateTime);

                String transactionDateTime = transDataObj.optString(TRANS_DATE);
                YDataLogger.out("BookingDateTime is::" + transactionDateTime);

                String merchantName = transDataObj.optString(MERCHANT_NAME);
                YDataLogger.out("merchantName is::" + merchantName);

                String merchantCategoryCode = transDataObj.optString(MERCHANT_CODE);
                YDataLogger.out("merchantCategoryCode is::" + merchantCategoryCode);

                String billerCode = transDataObj.optString(BILLER_CODE);
                YDataLogger.out("billerCode is::" + billerCode);

                String billerName = transDataObj.optString(BILLER_NAME);
                YDataLogger.out("billerName is::" + billerName);

                String apcaNumber = transDataObj.optString(APCA_NUMBER);
                YDataLogger.out("apcaNumber is::" + apcaNumber);

                String description;
                try {
                    description = transDataObj.getString(DESCRIPTION);
                    YDataLogger.out("description : " + description);
                }catch(JSONException e) {
                    description = "No description returned";
                }

                transactionMap.put(DESCRIPTION, description);
                transactionMap.put(TRANSACTION_TYPE, transactionType);
                transactionMap.put(ACCOUNT_ID, accountId);
                transactionMap.put(AMOUNT, transactionAmount);
                transactionMap.put(CURRENCY, currency);
                transactionMap.put(STATUS, transactionStatus);
                transactionMap.put(POST_DATE, bookingDateTime);
                transactionMap.put(TRANS_DATE, transactionDateTime);
                transactionMap.put(MERCHANT_NAME, merchantName);
                transactionMap.put(MERCHANT_CODE, merchantCategoryCode);
                transactionMap.put(BILLER_CODE, billerCode);
                transactionMap.put(BILLER_NAME, billerName);
                transactionMap.put(APCA_NUMBER, apcaNumber);

                transactionList.add(transactionMap);
            }

            String totalNumberOfPages = totalPageObj.optString("TotalPages");

            nextURLFirst=linkObj.optString("next");

            if(!ScriptUtil.isNullValue(nextURLFirst)) {
                transactionResponse = postReqAndGetContents(pRobot, nextURLFirst, ACCOUNT_TRANSACTION_REQUEST);
                YDataLogger.out("2^^^^^The next request for the transaction is="+transactionResponse);
            }
        } while(!ScriptUtil.isNullValue(nextURLFirst));

        return transactionList;
    }

    /**
     * Method to set the transaction information
     *
     * @param bankAccount
     * @param transactionList
     */
    private void setAccountTransactions(BankAccount bankAccount, List<HashMap<String, String>> transactionList) throws Exception{
        for (HashMap<String, String> txnMap : transactionList) {
            BankTransaction bankTransaction = new BankTransaction();

            String postDate = txnMap.get(POST_DATE);
            String transDate = txnMap.get(TRANS_DATE);
            String transactionDesc = txnMap.get(DESCRIPTION);
            String amount = txnMap.get(AMOUNT);
            String currency = txnMap.get(CURRENCY);
            String status = txnMap.get(STATUS);
            String transType = txnMap.get(TRANSACTION_TYPE);
            String merchantname= txnMap.get(MERCHANT_NAME);
            String merchantcode= txnMap.get(MERCHANT_CODE);
            String billerCode= txnMap.get(BILLER_CODE);
            String billerName= txnMap.get(BILLER_NAME);
            String apcaNumber= txnMap.get(APCA_NUMBER);

            YDataLogger.out("Post Date > " + postDate + "< Txn Desc > " + transactionDesc + "< Txn Amt >" + amount
                    + "< Currency >" + currency + "< Txn Status >" + status + "< Base Type >" );
            YProdDataLogger.out("Post Date > " + postDate + "< Txn Desc > " + transactionDesc + "< Txn Amt >" + amount
                    + "< Currency >" + currency + "< Txn Status >" + status + "< Base Type >" );
            SimpleDateFormat parsedsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate;

            if (!ScriptUtil.isNullValue(postDate)) {

                if (ScriptUtil.isDateValid(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD, postDate, true, "", "")) {
                    bankTransaction.setPostDate(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD, postDate, "UTC", false);
                    parsedDate= parsedsdf.parse(postDate);
                    YDataLogger.out("parsed post date into one format from any formats"+parsedDate);
                    YDataLogger.out("formatted post date into one format from any formats"+sdFormat.format(parsedDate));
                    bankTransaction.setPostDateTime("yyyy-MM-dd HH:mm:ss", sdFormat.format(parsedDate));
                }
            } else {
                throw new GeneralException("Post date is Null >");
            }

            if (!ScriptUtil.isNullValue(transDate)) {
                if (ScriptUtil.isDateValid(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD, transDate, true, "", "")) {
                    bankTransaction.setTransDate(ScriptConstants.DATEFORMAT_YYYY_HYP_MM_DD, transDate, "UTC", false);
                    YDataLogger.out("transDate from txn response"+transDate);
                    parsedDate= parsedsdf.parse(transDate);
                    YDataLogger.out("parsed trans date into one format from any formats"+parsedDate);
                    YDataLogger.out("formatted trans date into one format from any formats"+sdFormat.format(parsedDate));
                    bankTransaction.setTransDateTime("yyyy-MM-dd HH:mm:ss", sdFormat.format(parsedDate));
                }
            }


            if(ScriptUtil.isNullValue(transDate) && ScriptUtil.isNullValue(postDate)){
                throw new GeneralException("Trans date and Post Date both are null");
            }

            YProdDataLogger.out("transaction description------" + transactionDesc);
            if (!ScriptUtil.isNullValue(transactionDesc)) {
                bankTransaction.setDescription(transactionDesc);
            } else {
                throw new GeneralException("Transaction Description is Null >");
            }

            if (!ScriptUtil.isNullValue(amount) && !ScriptUtil.isNullValue(currency)) {
                if (amount.contains("-")) {
                    YDataLogger.out("-----6------");
                    amount = amount.replace("-", "");
                    bankTransaction.setAmount(amount,currency);
                    bankTransaction.setTransactionBaseType(BankTransaction.TRANSACTION_BASE_TYPE_DEBIT);
                } else {
                    YDataLogger.out("-----7------");
                    bankTransaction.setAmount(amount,currency);
                    bankTransaction.setTransactionBaseType(BankTransaction.TRANSACTION_BASE_TYPE_CREDIT);
                }
            } else {
                throw new GeneralException("Amount or Currency is NULL > ");
            }

            if (!ScriptUtil.isNullValue(status)) {
                if (status.equalsIgnoreCase("POSTED")) {
                    bankTransaction.setTransactionStatus(BankTransaction.TRANSACTION_STATUS_TYPE_POSTED);
                } else if (status.equalsIgnoreCase("PENDING")) {
                    bankTransaction.setTransactionStatus(BankTransaction.TRANSACTION_STATUS_TYPE_PENDING);
                } else {
                    throw new GeneralException("New Status available >" + status);
                }
            } else {
                throw new GeneralException("Status is Null >" + status);
            }

            if(!ScriptUtil.isNullValue(merchantcode)) {
                YProdDataLogger.out("setting actual MCCode");
                bankTransaction.setMCCode(merchantcode);
            } else {
                YProdDataLogger.out("setting dummy MCCode");
                bankTransaction.setMCCode("123456");
            }

            if(!ScriptUtil.isNullValue(merchantname)) {
                YProdDataLogger.out("setting actual SourceMerchantName");
                bankTransaction.setSourceMerchantName(merchantname);
            } else {
                YProdDataLogger.out("setting dummy SourceMerchantName");
                bankTransaction.setSourceMerchantName("Paypal");
            }

            if(!ScriptUtil.isNullValue(transType)) {
                YProdDataLogger.out("setting actual SourceTransactionType");
                bankTransaction.setSourceTransactionType(transType);
            } else {
                YProdDataLogger.out("setting dummy SourceTransactionType");
                bankTransaction.setSourceTransactionType("TRANSFER");
            }

            if(!ScriptUtil.isNullValue(billerCode)) {
                YProdDataLogger.out("setting actual SourceBillerCode");
                bankTransaction.setSourceBillerCode(billerCode);
            } else {
                YProdDataLogger.out("setting dummy SourceBillerCode");
                bankTransaction.setSourceBillerCode("123456");
            }

            if(!ScriptUtil.isNullValue(billerName)) {
                YProdDataLogger.out("setting actual SourceBillerName");
                bankTransaction.setSourceBillerName(billerName);
            } else {
                YProdDataLogger.out("setting dummy SourceBillerName");
                bankTransaction.setSourceBillerName("Paypal");
            }

            if(!ScriptUtil.isNullValue(apcaNumber)) {
                YProdDataLogger.out("setting actual SourceAPCANumber");
                bankTransaction.setSourceAPCANumber(apcaNumber);
            } else {
                YProdDataLogger.out("setting dummy SourceAPCANumber");
                bankTransaction.setSourceAPCANumber("123456");
            }

            YProdDataLogger.out("setting dummy SourcePayeeName");
            bankTransaction.setSourcePayeeName("Paypal");

            bankAccount.addTransaction(bankTransaction);
        }
    }

    @Override
    public void getFullAccountNumber(FinancialContainer financialContainer, RequestItem item, IYodRobot pRobot)
            throws Exception {
    }

    @Override
    public void navigateToFullAcctNumberPage(FinancialContainer financialContainer, RequestItem item, IYodRobot pRobot)
            throws Exception {
    }

    @Override
    public boolean isOnFullAcctNumberPage(FinancialContainer financialContainer, RequestItem item, IYodRobot pRobot)
            throws Exception {
        return false;
    }

    @Override
    public int doBasicVerification(RequestItem item, ClassArgs classArgs, IYodRobot robot) throws Exception {
        return 0;
    }

    @Override
    public int doExtendedVerification(RequestItem item, ClassArgs classArgs, IYodRobot robot) throws Exception {
        return 0;
    }

    @Override
    public double getSupportedVersion() {
        return 0;
    }

    @Override
    public Object generateHttpHeader(String arg0, IYodRobot arg1, Object arg2) {
        return null;
    }

    @Override
    protected void handlePreLoadDocumentErrors(String arg0, Exception arg1, String arg2) throws Exception {
    }

    @Override
    protected int isRetriableError(String arg0, Exception arg1) {
        return 0;
    }

    @Override
    public Object generateRequest(String arg0, IYodRobot arg1, YGathererLogSummary arg2) {
        return null;
    }

    @Override
    public int handleFeedErrors(Document arg0, Object arg1, IYodRobot arg2, String arg3, RequestItem arg4,
                                YGathererLogSummary arg5) throws Exception {
        return 0;
    }

    @Override
    public void parseResponse(IYodRobot arg0, String arg1, Document arg2, Object arg3, Object arg4) throws Exception {
    }

    @Override
    public void getAccountDetails(RequestItem item, IYodRobot pRobot, BankAccount bankAccount,
                                  boolean isValidationRequired) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void verifyProperTransactionPage(IYodRobot pRobot, String[] pageIdentifier) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void navigateToAccountTxn(IYodRobot pRobot, BankAccount bankAccount) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public IndividualInformation getIndividualInformation(RequestItem arg0, IYodRobot arg1) throws Exception {
        return null;
    }
    @Override
    public boolean isOnAccountTransactionsPage(IYodRobot pRobot) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void matchTransactions(IYodRobot pRobot) throws Exception {
        // TODO Auto-generated method stub

    }

}